package osu_mp3;

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
import lazer_database.RealmDatabaseReader;


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

        fxmlLoader.setLocation(getClass().getResource("/primary_stage.fxml"));
        System.out.println(fxmlLoader.getLocation());

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

//        DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, collectionsFilePath, databaseFilePath);
//
//        if (Files.exists(databaseFilePath)) {
//            databaseManager.readDatabase();
//            databaseManager.syncDatabase();
//        } else {
//            databaseManager.createDatabase();
//            databaseManager.readDatabase();
//        }

        List<SongCollection> songCollectionList = loadSongCollections("lazer");

        // TODO - stuffs here
        addSongPanes(songCollectionList);

        Platform.runLater(()-> {
            controller.gridPane.setDisable(false);
            controller.exportSongListMenuItem.setDisable(false);
        });

    }

    public static List<SongCollection> loadSongCollections(String version) {

        if (version.equals("lazer")) {

            System.out.println("Reading Osu! Lazer Database.");

            Path realmFilePath = Path.of("D:\\Program Files\\osu!-lazer\\client.realm");
            Path osuFilesPath = Path.of("D:\\Program Files\\osu!-lazer\\files");

            RealmDatabaseReader realmDatabaseReader = new RealmDatabaseReader(realmFilePath, osuFilesPath);
            List<SongCollection> songCollectionList = realmDatabaseReader.getSongCollections();
            realmDatabaseReader.closeDatabase();

            return songCollectionList;
        } // if
        else if (version.equals("stable")) {

            System.out.println("Reading Osu! Stable Database.");

            Path songsFolderPath = Path.of("D:\\Program Files\\osu!\\Songs");
            Path collectionsFilePath = Path.of("D:\\Program Files\\osu!\\collection.db");
            Path databaseFilePath = Path.of(System.getProperty("user.dir"), "Beatmaps.db");

            DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, collectionsFilePath, databaseFilePath);
            databaseManager.readDatabase();
            databaseManager.syncDatabase();

            return databaseManager.getSongCollections();
        }

        return Collections.emptyList();
    }

    public static void setOsuFolder(File osuFolderFile) throws IOException {
        osuFolder = osuFolderFile;
        settingsManager.setProperty("osuFolderLocation", osuFolder.getPath());
        settingsManager.saveSettings();
    }


    //moved out of GlobalKeyListener
    public static void togglePause() {
        root.requestFocus();
        controller.TogglePause();
    }

    private static void addSongPanes(List<SongCollection> songCollectionList)
    {
        for (int i = 0; i < songCollectionList.size(); i++) {

            final int currentCol = i; // A final var is necessary for Platform.runLater threads.

            SongCollection songCollection = songCollectionList.get(i);

            int collectionSize = songCollection.size();

            if (collectionSize == 0) {
                continue;
            }

            // Add column title.
            Platform.runLater(
                ()-> controller.addToGrid(
                    new Label(songCollection.getName() + " (" + collectionSize + ")"),
                    currentCol,
                    0
                )
            );

            List<SongPane> songPanes = new ArrayList<>();

            // Add rows
            for (int j = 0; j < collectionSize; j++) {

                final int currentRow = j + 1; // A final var is necessary for Platform.runLater threads.

                SongPane songPane = new SongPane(
                    songCollection.get(j),
                    songCollection.getName()
                );

                songPanes.add(songPane);

                // Add song pane to row.
                Platform.runLater(() -> controller.addToGrid(songPane, currentCol, currentRow));
            }

            songPaneCollectionList.add(songPanes);
        }
    }

}
