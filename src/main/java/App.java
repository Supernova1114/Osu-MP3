import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class App extends Application{

    private String version = "0.7.2";
    private String stageTitle = "Osu! MP3 v" + version;

    public static Stage primaryStage;
    public static Controller controller;
    public static File osuFolder = null;//new File("D:\\Program Files\\osu!");
    public static ArrayList<ArrayList<SongPane>> songPaneCollectionList = new ArrayList<>();
    public static Parent root;

    public static String currentDirectory = System.getProperty("user.dir");
    public static Path songsDatabaseFilePath = Paths.get(currentDirectory + File.separator + "SongMapHash.db");
    public static String settingsPath = currentDirectory + File.separator + "settings.conf";

    public static Properties applicationProps;

    static long collectionDBLastModified = -1;
    static long songsFolderLastModified = -1;

    private static final String hashMapSplitChar = " [] ";

    public static Map<String, File> hashMap = new HashMap<>();

    GlobalKeyListener globalKeyListener;

// FIXME: 8/18/2021 make a version line in database so that it will delete old database and create new one

    @Override
    public void start(Stage stage) throws Exception{
        primaryStage = stage;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                globalKeyListener.cleaUp();
                primaryStage.close();
                System.exit(0);
            }
        });

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sample.fxml"));

        root = fxmlLoader.load();
        controller = fxmlLoader.getController();


        Scene scene = new Scene(root, 600, 600);


        scene.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {


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

        scene.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if ( event.getCode() == KeyCode.SPACE ){
                root.requestFocus();
                controller.TogglePause();
                event.consume();
            }
            event.consume();
        });



        primaryStage.setTitle(stageTitle);
        primaryStage.setScene(scene);
        primaryStage.show();



        //
        if (Files.exists(Paths.get(settingsPath))) {
            System.out.println("Settings File Exists\n");
            applicationProps = new Properties();

            FileInputStream in = new FileInputStream(settingsPath);
            applicationProps.load(in);
            in.close();
        }else {
            System.out.println("Settings File Does Not Exist\n");
            applicationProps = new Properties();

            //Default Properties
            applicationProps.setProperty("osuFolderLocation", "null");
            applicationProps.setProperty("showArtists", "true");

            FileOutputStream out = new FileOutputStream(settingsPath);
            applicationProps.store(out, "---No Comment---");
            out.close();
        }

        String osuFolderLocProp = applicationProps.getProperty("osuFolderLocation");
        //String showArtistsProp = applicationProps.getProperty("showArtists");

        //controller.showArtists(Boolean.parseBoolean(showArtistsProp));


        if (!osuFolderLocProp.equals("null")){
            osuFolder = new File(osuFolderLocProp);
            try{
                Begin();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        globalKeyListener = new GlobalKeyListener();

    }//start()


    //Main Processes after choosing files and pressing start
    public static void Begin() throws Exception {

        if (osuFolder != null) {

            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    FilenameFilter SONGSFOLDER = new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.equals("Songs");
                        }
                    };
                    FilenameFilter COLLECTIONDB = new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.equals("collection.db");
                        }
                    };

                    //GetSongsFolder
                    File songFolder = Objects.requireNonNull(osuFolder.listFiles(SONGSFOLDER))[0];
                    File collectionsDB = Objects.requireNonNull(osuFolder.listFiles(COLLECTIONDB))[0];

                    songsFolderLastModified = songFolder.lastModified();
                    collectionDBLastModified = collectionsDB.lastModified();


                    // Temp stuffs ------------------------------------------------------------------------------------

//                    //Put title pane
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            controller.addToGrid(new Pane() {{
//                                getChildren().add(new Label(collection.get(0) + " " + (collection.size() - 1)));
//                            }}, finalCol, finalRow);
//                        }
//                    });
//
//                    //Add SongPane
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                controller.addToGrid(songPane, finalCol1, finalRow1);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            controller.addToGrid(new Pane() {{
//                                getChildren().add(new Label(collection.get(0)));
//                            }}, finalCol2, finalRow2);
//                        }
//                    });

                    // --------------------------------------------------------------------------

                    controller.gridPane.setDisable(false);
                    controller.exportSongListMenuItem.setDisable(false);

                    return null;
                }
            };
            worker.execute();

        }

    }

    //Create filenameFilter



    public static void setOsuFolder(File osuFolderFile) throws IOException {
        osuFolder = osuFolderFile;
        applicationProps.setProperty("osuFolderLocation", osuFolder.getPath());

        FileOutputStream out = new FileOutputStream(settingsPath);
        applicationProps.store(out, "---No Comment---");
        out.close();
    }


    //moved out of GlobalKeyListener
    public static void togglePause(){
        root.requestFocus();
        controller.TogglePause();
    }
}
