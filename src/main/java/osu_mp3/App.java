package osu_mp3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import lazer_database.RealmDatabaseReader;
import stable_database.DatabaseManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class App extends Application {

    private final String APP_VERSION = "1.x - WIP";
    private final String APP_TITLE = "Osu! MP3 v" + APP_VERSION;
    private final int SCENE_WIDTH = 600;
    private final int SCENE_HEIGHT = 600;
    private final int WINDOW_WIDTH = 630;
    private final int MIN_WINDOW_HEIGHT = 187;
    private static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
    private static final String SETTINGS_FILE_NAME = "settings.conf";
    private static final String FXML_MAIN_SCENE = "primary_stage.fxml";


    // Osu! Stable - Files
    private static final String SONGS_FOLDER_NAME = "Songs";
    private static final String COLLECTIONS_FILE_NAME = "collection.db";
    private static final String STABLE_DATABASE_FILE_NAME = "Beatmaps.db";

    // Osu! Lazer - Files
    private static final String LAZER_DATABASE_FILE_NAME = "client.realm";
    private static final String LAZER_FILES_FOLDER_NAME = "files";

    public static Stage primaryStage;
    public static Controller controller;
    public static Parent rootNode;
    private static GlobalKeyListener globalKeyListener;
    private static SettingsManager settingsManager;
    private static MusicManager musicManager;

    public static Path osuStableFolderPath = null;
    public static Path osuLazerFolderPath = null;
    public static String osuDatabaseVersion = null;
    public static String lastCollectionShown = null;
    public static HashMap<String, SongCollection> songCollectionDict;
    private static String prevValue = "";


    @Override
    public void start(Stage stage) throws Exception {

        musicManager = new MusicManager();
        globalKeyListener = new GlobalKeyListener();

        initializeSettings();
        initializeGUI(stage);
        loadSongsAsync();
    }

    private void initializeGUI(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setOnCloseRequest(event -> exitApplication());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/" + FXML_MAIN_SCENE));

        rootNode = fxmlLoader.load();
        controller = fxmlLoader.getController();

        Scene mainScene = new Scene(rootNode, SCENE_WIDTH, SCENE_HEIGHT);

        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMaxWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

//        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
//
//            if (event.getCode() == KeyCode.UP){
//                controller.volumeSlider.increment();
//                event.consume();
//            }
//            if (event.getCode() == KeyCode.DOWN){
//                controller.volumeSlider.decrement();
//                event.consume();
//            }
//
//            event.consume();
//        });

//        mainScene.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
//            if ( event.getCode() == KeyCode.SPACE ){
//                rootNode.requestFocus();
//                controller.TogglePause();
//                event.consume();
//            }
//            event.consume();
//        });

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void initializeSettings() {
        Path settingsFilePath = Path.of(CURRENT_DIRECTORY, SETTINGS_FILE_NAME);
        settingsManager = new SettingsManager(settingsFilePath);

        if (settingsManager.settingsFileExists()) {
            System.out.println("Settings File Exists.");
            settingsManager.loadSettings();
        } else {
            System.out.println("Settings File Does Not Exist. Creating File.");
            settingsManager.setDefaultProperties();
            settingsManager.saveSettings();
        }

        // Initialize settings variables.
        osuLazerFolderPath = Path.of(settingsManager.getProperty(SettingsManager.Settings.OSU_LAZER_FOLDER_PATH));
        osuStableFolderPath = Path.of(settingsManager.getProperty(SettingsManager.Settings.OSU_STABLE_FOLDER_PATH));
        osuDatabaseVersion = settingsManager.getProperty(SettingsManager.Settings.OSU_VERSION);
        lastCollectionShown = settingsManager.getProperty(SettingsManager.Settings.LAST_COLLECTION_SHOWN);
    }

    public static void exitApplication() {
        globalKeyListener.cleaUp();
        musicManager.dispose();
        primaryStage.close();
        System.exit(0);
    }

    private static void loadSongsAsync() {

        boolean validOsuLazerDatabase = osuDatabaseVersion.equals("lazer") && Files.exists(osuLazerFolderPath);
        boolean validOsuStableDatabase = osuDatabaseVersion.equals("stable") && Files.exists(osuStableFolderPath);

        if (!validOsuStableDatabase && !validOsuLazerDatabase) {
            System.out.println("ERROR: Invalid database folder path or database parser version!");
            return;
        }

        Thread thread = new Thread(() -> {
            List<SongCollection> songCollectionList = loadSongCollections(osuDatabaseVersion);

            songCollectionDict = new HashMap();
            for (SongCollection collection : songCollectionList) {
                songCollectionDict.put(collection.getName(), collection);
            }

            controller.comboBox.getItems().addAll(songCollectionDict.keySet());

            controller.comboBox.setOnAction((event) -> {
                String value = controller.comboBox.getValue();
                if (value == null) {
                    return;
                }

                if (value.equals(prevValue)) {
                    return;
                }

                prevValue = value;

                displaySongCollection(value);
            });

            controller.comboBox.setOnHidden((event)->{
                if (controller.comboBox.getValue() == null) {
                    controller.comboBox.setValue(prevValue);
                }
            });

            Platform.runLater(()-> {
                controller.comboBox.setValue(lastCollectionShown);
                controller.gridPane.setDisable(false);
                controller.exportSongListMenuItem.setDisable(false);
            });
        });

        thread.start();
    }

    public static List<SongCollection> loadSongCollections(String version) {

        if (version.equals("lazer")) {
            System.out.println("Reading Osu! Lazer Database.");

            Path osuFilesFolderPath = Path.of(osuLazerFolderPath.toString(), LAZER_FILES_FOLDER_NAME);
            Path realmFilePath = Path.of(osuLazerFolderPath.toString(), LAZER_DATABASE_FILE_NAME);

            RealmDatabaseReader realmDatabaseReader = new RealmDatabaseReader(realmFilePath, osuFilesFolderPath);
            List<SongCollection> songCollectionList = realmDatabaseReader.getSongCollections();
            realmDatabaseReader.closeDatabase();

            return songCollectionList;
        } // if
        else if (version.equals("stable")) {
            System.out.println("Reading Osu! Stable Database.");

            Path songsFolderPath = Path.of(osuStableFolderPath.toString(), SONGS_FOLDER_NAME);
            Path collectionsFilePath = Path.of(osuStableFolderPath.toString(), COLLECTIONS_FILE_NAME);
            Path databaseFilePath = Path.of(CURRENT_DIRECTORY, STABLE_DATABASE_FILE_NAME);

            DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, collectionsFilePath, databaseFilePath);

            if (Files.exists(databaseFilePath)) {
                databaseManager.readDatabase();
                databaseManager.syncDatabase();
            } else {
                databaseManager.createDatabase();
                databaseManager.readDatabase();
            }

            return databaseManager.getSongCollections();
        }

        return Collections.emptyList();
    }

    public static void setOsuStableFolderPath(File osuFolderFile) throws IOException {
//        osuStableFolderPath = osuFolderFile;
//        settingsManager.setProperty("osuFolderLocation", osuStableFolderPath.getPath());
//        settingsManager.saveSettings();
    }

    private static void displaySongCollection(String name) {
        SongCollection collection = songCollectionDict.get(name);

        int collectionSize = collection.size();

        List<Node> nodes = new ArrayList<>();

        // Title header
        Label titleLabel = new Label(collection.getName() + " (" + collectionSize + ")");
        titleLabel.setPadding(new Insets(0,0,0,5));
        nodes.add(titleLabel);

        // Songs
        for (SongData songData : collection.getSongList()) {
            nodes.add(new SongPane(songData, collection.getName()));
        }

        //nodes.add(new SongPane(new SongData("abcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefgabcdefg", "abcdefgabcdefg", ""), "test"));

        Platform.runLater(()->{
            controller.clearGrid();
            controller.addColumnToGrid(0, nodes);
        });
    }

}
