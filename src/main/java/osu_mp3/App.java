package osu_mp3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lazer_database.RealmDatabaseReader;
import stable_database.DatabaseManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class App extends Application {

    private final String APP_VERSION = "1.0.0";
    private final String APP_TITLE = "Osu! MP3 v" + APP_VERSION;
    private final int SCENE_WIDTH = 600;
    private final int SCENE_HEIGHT = 600;
    private final int WINDOW_WIDTH = 630;
    private final int MIN_WINDOW_HEIGHT = 187;
    private static String programDirectory;
    private static final String SETTINGS_FILE_NAME = "settings.conf";
    private static final String DATABASE_FOLDER_NAME = "database";
    private static final String FXML_MAIN_SCENE = "primary_stage.fxml";


    // Osu! Stable - Files
    private static final String SONGS_FOLDER_NAME = "Songs";
    private static final String COLLECTIONS_FILE_NAME = "collection.db";
    private static final String STABLE_DATABASE_FILE_NAME = "beatmaps.db";

    // Osu! Lazer - Files
    private static final String LAZER_DATABASE_FILE_NAME = "client.realm";
    private static final String LAZER_DATABASE_COPY_FILE_NAME = "client.realm";
    private static final String LAZER_FILES_FOLDER_NAME = "files";

    public static Stage primaryStage;
    public static Controller controller;
    public static Parent rootNode;
    private static GlobalKeyListener globalKeyListener;
    private static SettingsManager settingsManager;
    private static MusicManager musicManager;

    public static Path osuStableFolderPath = null;
    public static Path osuLazerFolderPath = null;
    public static String osuDatabaseMode = null;
    public static LinkedHashMap<Integer, SongCollection> songCollectionDict = new LinkedHashMap<>();
    public static HashMap<SongData, SongPane> songPaneLookupDict = new HashMap<>();

    private static volatile boolean isDoneLoadingSongs = true;


    @Override
    public void start(Stage stage) throws Exception {

        programDirectory = Path.of(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();

        musicManager = new MusicManager();
        globalKeyListener = new GlobalKeyListener();

        initializeSettings();
        initializeFileStructure();
        initializeGUI(stage);
        loadSongsAsync();
    }

    private void initializeFileStructure() throws IOException {
        Path databaseFolder = Path.of(programDirectory, DATABASE_FOLDER_NAME);
        if (Files.notExists(databaseFolder)) {
            Files.createDirectory(databaseFolder);
        }
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

        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {

            if (controller.comboBox.isShowing()) { return; }

            if (event.getCode() == KeyCode.UP) {
                controller.volumeSlider.increment();
            }
            if (event.getCode() == KeyCode.DOWN) {
                controller.volumeSlider.decrement();
            }
        });

        mainScene.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if ( event.getCode() == KeyCode.SPACE && controller.comboBox.isShowing() == false) {
                rootNode.requestFocus();
                MusicManager.getInstance().togglePause();
            }
        });

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void initializeSettings() {
        Path settingsFilePath = Path.of(programDirectory, SETTINGS_FILE_NAME);
        settingsManager = new SettingsManager(settingsFilePath);

        if (settingsManager.settingsFileExists()) {
            System.out.println("Settings File Exists.");
            settingsManager.loadSettings();
        } else {
            System.out.println("Settings File Does Not Exist. Creating File.");
            settingsManager.setDefaultProperties();
            settingsManager.saveSettings();
        }

        retrieveSettingVariables();
    }

    // Initialize application setting variables via properties stored in SettingsManager
    private static void retrieveSettingVariables() {
        // Initialize settings variables.
        osuLazerFolderPath = Path.of(settingsManager.getProperty(SettingsManager.Settings.OSU_LAZER_FOLDER_PATH));
        osuStableFolderPath = Path.of(settingsManager.getProperty(SettingsManager.Settings.OSU_STABLE_FOLDER_PATH));
        osuDatabaseMode = settingsManager.getProperty(SettingsManager.Settings.OSU_DATABASE_MODE);
    }

    // Update SettingsManager with the most up-to-date application setting variables.
    private static void storeSettingVariables() {
        settingsManager.setProperty(SettingsManager.Settings.OSU_LAZER_FOLDER_PATH, osuLazerFolderPath.toString());
        settingsManager.setProperty(SettingsManager.Settings.OSU_STABLE_FOLDER_PATH, osuStableFolderPath.toString());
        settingsManager.setProperty(SettingsManager.Settings.OSU_DATABASE_MODE, osuDatabaseMode);
    }

    public static void exitApplication() {
        storeSettingVariables();
        settingsManager.saveSettings();
        globalKeyListener.cleanUp();
        musicManager.dispose();
        primaryStage.close();
        System.exit(0);
    }

    private static void loadSongsAsync() {

        controller.disableMenuControls(true);

        Thread thread = new Thread(() -> {
            List<SongCollection> songCollectionList = loadSongCollections(osuDatabaseMode);
            for (SongCollection collection : songCollectionList) {
                songCollectionDict.put(collection.getID(), collection);
            }

            controller.setItemsComboBox(songCollectionDict.values());
            controller.disableMenuControls(false);
        });

        thread.start();
    }

    private static List<SongCollection> loadSongCollections(String dbMode) {

        if (dbMode.equals("lazer")) {

            Path osuFilesFolderPath = Path.of(osuLazerFolderPath.toString(), LAZER_FILES_FOLDER_NAME);
            Path realmFilePath = Path.of(osuLazerFolderPath.toString(), LAZER_DATABASE_FILE_NAME);
            Path realmCopyFilePath = Path.of(programDirectory, DATABASE_FOLDER_NAME, LAZER_DATABASE_COPY_FILE_NAME);

            if (Files.notExists(osuFilesFolderPath) || Files.notExists(realmFilePath)) {
                showAlert(Alert.AlertType.INFORMATION, "Please set Osu! Lazer data folder.", "(File > Set Osu! Lazer Folder)");
                return Collections.emptyList();
            }

            System.out.println("Reading Osu! Lazer Database");

            // Copy realm file as we do not want to touch original file (Cannot open realm as readonly).
            try {
                System.out.println("Copying Osu! Lazer Database");
                controller.setStatusLabel("Copying Osu! Lazer Database");

                Files.copy(realmFilePath, realmCopyFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            controller.setStatusLabel("Reading Osu! Lazer Database");

            RealmDatabaseReader realmDatabaseReader = new RealmDatabaseReader(realmCopyFilePath, osuFilesFolderPath);
            List<SongCollection> songCollectionList = realmDatabaseReader.getSongCollections();
            realmDatabaseReader.closeDatabase();

            System.out.println("Done");
            controller.setStatusLabel("");

            return songCollectionList;
        } // if
        else if (dbMode.equals("stable")) {

            Path songsFolderPath = Path.of(osuStableFolderPath.toString(), SONGS_FOLDER_NAME);
            Path collectionsFilePath = Path.of(osuStableFolderPath.toString(), COLLECTIONS_FILE_NAME);
            Path databaseFilePath = Path.of(programDirectory, DATABASE_FOLDER_NAME, STABLE_DATABASE_FILE_NAME);

            if (Files.notExists(songsFolderPath) || Files.notExists(collectionsFilePath)) {
                showAlert(Alert.AlertType.INFORMATION, "Please set Osu! Stable installation folder.", "(File > Set Osu! Stable Folder)");
                return Collections.emptyList();
            }

            System.out.println("Reading Osu! Stable Database.");

            DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, collectionsFilePath, databaseFilePath);

            if (Files.exists(databaseFilePath)) {
                controller.setStatusLabel("Reading Osu! Stable Database");
                databaseManager.readDatabase();
                controller.setStatusLabel("Syncing Osu! Stable Database");
                databaseManager.syncDatabase();
            } else {
                controller.setStatusLabel("Creating Osu! Stable Database (May take some time)");
                databaseManager.createDatabase();
                controller.setStatusLabel("Reading Osu! Stable Database");
                databaseManager.readDatabase();
            }

            List<SongCollection> songCollectionList = databaseManager.getSongCollections();

            controller.setStatusLabel("");
            System.out.println("Done.");

            return songCollectionList;
        }

        System.out.println("WARNING: Invalid Database Mode \"" + dbMode + "\".");
        controller.setStatusLabel("Please Select Osu! Database Mode");

        return Collections.emptyList();
    }

    public static void displayNewSongCollection(SongCollection collection) {

        List<Node> nodes = new ArrayList<>();

        // Songs
        for (SongData songData : collection.getSongList()) {

            // Create new SongPane if it's not already in dict
            if (songPaneLookupDict.containsKey(songData)) {
                nodes.add(songPaneLookupDict.get(songData));
            }
            else
            {
                SongPane pane = new SongPane(collection.getID(), songData);
                songPaneLookupDict.put(songData, pane);
                nodes.add(pane);
            }
        }

        Platform.runLater(()->{
            controller.clearGrid();
            controller.addColumnToGrid(0, nodes);
        });
    }

    public static void switchOsuDBModes(String dbMode) {

        MusicManager.getInstance().stop();
        songPaneLookupDict.clear();
        songCollectionDict.clear();
        controller.clearGrid();
        osuDatabaseMode = dbMode;
        // FIXME - could be issues with async func being here if called too quickly
        loadSongsAsync();
    }

    public static void showAlert(Alert.AlertType alertType, String header, String content) {
        Platform.runLater(()->{
            Alert alert = new Alert(alertType, content, ButtonType.OK);
            alert.setHeaderText(header);
            alert.showAndWait();
        });
    }

}
