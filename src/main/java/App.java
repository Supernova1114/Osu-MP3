import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class App extends Application {

    private final String APP_VERSION = "0.7.2";
    private final String APP_TITLE = "Osu! MP3 v" + APP_VERSION;
    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 600;
    private static final String DATABASE_FILE_NAME = "Beatmaps.db";
    private static final String SETTINGS_FILE_NAME = "settings.conf";
    private static final String SONGS_FOLDER_NAME = "Songs";
    private static final String COLLECTIONS_FILE_NAME = "collection.db";

    private static final String currentDirectory = System.getProperty("user.dir");
    private static final Path databaseFilePath = Path.of(currentDirectory, DATABASE_FILE_NAME);
    private static final Path settingsPath = Path.of(currentDirectory, SETTINGS_FILE_NAME);

    public static Stage primaryStage;
    public static Controller controller;
    public static File osuFolder = null;
    public static List<List<SongPane>> songPaneCollectionList = new ArrayList<>();
    public static Parent root;
    private GlobalKeyListener globalKeyListener;
    private static SettingsManager settingsManager = new SettingsManager(settingsPath);

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;

        primaryStage.setOnCloseRequest(event -> {
            globalKeyListener.cleaUp();
            primaryStage.close();
            System.exit(0);
        });

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("primary_stage.fxml"));

        root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        Scene mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {

            if (event.getCode() == KeyCode.UP){
                controller.volumeSlider.increment();
                event.consume();
            }
            if (event.getCode() == KeyCode.DOWN){
                controller.volumeSlider.decrement();
                event.consume();
            }

            event.consume();
        });

        mainScene.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if ( event.getCode() == KeyCode.SPACE ){
                root.requestFocus();
                controller.TogglePause();
                event.consume();
            }
            event.consume();
        });

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        if (settingsManager.settingsFileExists()) {
            System.out.println("Settings File Exists");
            settingsManager.loadSettings();
        } else {
            System.out.println("Settings File Does Not Exist");
            settingsManager.setDefaultProperties();
            settingsManager.saveSettings();
        }

        String osuFolderLocProp = settingsManager.getProperty("osuFolderLocation");

        if (!osuFolderLocProp.isEmpty()) {
            osuFolder = new File(osuFolderLocProp);
            try {
                Begin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        globalKeyListener = new GlobalKeyListener();

    }//start()


    //Main Processes after choosing files and pressing start
    public static void Begin() {

        if (osuFolder != null) {

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    loadSongsAsync();
                    return null;
                }
            };

            new Thread(task).start();
        }

    }

    private static void loadSongsAsync() {

        Path songsFolderPath = Path.of(osuFolder.getPath(), SONGS_FOLDER_NAME);
        Path collectionsFilePath = Path.of(osuFolder.getPath(), COLLECTIONS_FILE_NAME);

        DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, databaseFilePath);

        if (Files.exists(databaseFilePath)) {
            databaseManager.readDatabase();
            databaseManager.syncDatabase();
        } else {
            databaseManager.createDatabase();
            databaseManager.readDatabase();
        }

        BeatmapCollectionDecoder beatmapCollectionDecoder = new BeatmapCollectionDecoder(collectionsFilePath);
        beatmapCollectionDecoder.readCollections();

        List<BeatmapCollection> beatmapCollectionList = beatmapCollectionDecoder.getBeatmapCollectionList();
        HashMap<String, Beatmap> beatmapHashDict = databaseManager.buildBeatmapHashDict();

        addSongPanes(beatmapCollectionList, beatmapHashDict);

        Platform.runLater(()-> {
            controller.gridPane.setDisable(false);
            controller.exportSongListMenuItem.setDisable(false);
        });

    }

    public static void setOsuFolder(File osuFolderFile) throws IOException {
        osuFolder = osuFolderFile;
        settingsManager.setProperty("osuFolderLocation", osuFolder.getPath());
        settingsManager.saveSettings();
    }


    //moved out of GlobalKeyListener
    public static void togglePause(){
        root.requestFocus();
        controller.TogglePause();
    }

    private static void addSongPanes(List<BeatmapCollection> beatmapCollectionList, HashMap<String, Beatmap> beatmapHashDict)
    {
        for (int i = 0; i < beatmapCollectionList.size(); i++) {

            final int currentCol = i; // A final var is necessary for Platform.runLater threads.

            BeatmapCollection beatmapCollection = beatmapCollectionList.get(i);

            int collectionSize = beatmapCollection.size();

            if (collectionSize == 0) {
                continue;
            }

            // Add column title.
            Platform.runLater(
                ()-> controller.addToGrid(
                    new Label(beatmapCollection.getName() + " (" + collectionSize + ")"),
                    currentCol,
                    0
                )
            );

            List<SongPane> songPanes = new ArrayList<>();

            // Add rows
            int rowOffset = 0;
            for (int j = 0; j < collectionSize; j++) {

                final int currentRow = j + rowOffset + 1; // A final var is necessary for Platform.runLater threads.

                String hash = beatmapCollection.getHash(j);
                boolean isValidHash = beatmapHashDict.containsKey(hash);

                if (isValidHash) {
                    Beatmap beatmap = beatmapHashDict.get(hash);
                    SongData songData = beatmap.getSongData();

                    SongPane songPane = new SongPane(
                        songData.artistName + " - " + songData.songName,
                        hash,
                        new File(beatmap.getFilePath()),
                        new File(songData.filePath),
                        null,
                        beatmapCollection.getName()
                    );

                    songPanes.add(songPane);

                    // Add song pane to row.
                    Platform.runLater(() -> controller.addToGrid(songPane, currentCol, currentRow));

                } else {
                    rowOffset--; // Stay on same row until a valid hash is found.
                }
            }

            songPaneCollectionList.add(songPanes);
        }
    }

}
