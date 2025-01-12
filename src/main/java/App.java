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


        //temp
        //Begin();

        //Path to songs folder
        //String path = "D:\\Program Files\\osu!\\Songs\\";

        //File songFolder = new File(path);



        /*System.out.println();
        String realclean = data.replaceAll("\\P{Print}", " ");
        System.out.println(realclean);*/

       /* ArrayList<String> cleanerArray = new ArrayList<>();
        for (String a: cleanSplit){
            if (!a.equals(" ")){
                cleanerArray.add(a);
            }

        }*/


        /*for(String e: cleanSplit){
            System.out.print(e + "");
        }
        System.out.println();
        System.out.println();

        System.out.println(cleanerArray);*/

       /* data.split("\u0001");
        data.split("\u000B");


        System.out.println(" \u0001 \u0003 \u000B \u0006 \u0001 \u000B \u000B \u000B \u000B \u000B \u000B \u000B \u000B \u000B \u000B \u0007 \u000B \u000B \u000B " +
                "\u000B \u000B \u000B \u000B  ");*/





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

                    if (Files.exists(songsDatabaseFilePath)){

                        BufferedReader bufferedReader = new BufferedReader(new FileReader(songsDatabaseFilePath.toFile()));
                        ArrayList<String> lineList = new ArrayList<>();
                        String line;

                        //Read first 3 lines of Database file
                        for (int i=0; i<3; i++){
                            if ((line = bufferedReader.readLine()) != null)
                                lineList.add(line);
                        }

                        long collectionDBPrevLastMod = Long.parseLong(lineList.get(1));
                        long songsFolderPrevLastMod = Long.parseLong(lineList.get(2));

                        //System.out.println("collection.db\ncurr: " + collectionDBLastModified + "\nprev: " + collectionDBPrevLastMod);
                        //System.out.println("Songs folder\ncurr: " + songsFolderLastModified + "\nprev: " + songsFolderPrevLastMod + "\n");


                        if (collectionDBLastModified != collectionDBPrevLastMod){
                            System.out.println("collection.db was modified!");

                            if (songsFolderLastModified != songsFolderPrevLastMod){
                                System.out.println("Songs folder was modified!");

                                while ((line = bufferedReader.readLine()) != null){
                                    lineList.add(line);
                                }

                                bufferedReader.close();
                                //getSongsFromDatabase(lineList, songFolder, true);
                            }else {
                                //if Songs folder was not modified
                                while ((line = bufferedReader.readLine()) != null){
                                    lineList.add(line);
                                }

                                bufferedReader.close();
                                //getSongsFromDatabase(lineList, songFolder, false);

                            }

                        }else {
                            //if collection.db was not modified
                            while ((line = bufferedReader.readLine()) != null){
                                lineList.add(line);
                            }
                            bufferedReader.close();

                            //getSongsFromDatabase(lineList, songFolder, false);
                        }





                    }else {
                        System.out.println("Database does not exist! ... Creating Database");
                        //getSongsFromSongFolder(songFolder, collectionsDB);
                    }



                    //Get collectionDB info
                    String data = new String(Files.readAllBytes(Paths.get(collectionsDB.getPath())));


                    String[] clean = data.split("\\P{Print}");



                    ArrayList<String> cleanArray = new ArrayList<>();

                    for (String a : clean) {
                        //System.out.print(a + ",");
                        if (!a.equals("")) {
                            cleanArray.add(a);
                        }
                    }

                    //System.out.println(cleanArray);

                    cleanArray.remove(0);

                    ArrayList<ArrayList<String>> CollectionList = new ArrayList<>();
                    //String [][] CollectionList = new String[][];


                    for (int i = 0; i < cleanArray.size(); i++) {

                        if (cleanArray.get(i).length() < 32) {
                            int finalI = i;
                            ArrayList<String> finalCleanArray = cleanArray;//fuck

                            CollectionList.add(new ArrayList<String>() {{
                                add(finalCleanArray.get(finalI));
                            }});
                        } else {
                            CollectionList.get(CollectionList.size() - 1).add(cleanArray.get(i).replace(" ", ""));
                        }
                    }


                    /*//DEBUG////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    int count = 0;
                    for (ArrayList<String> collection: CollectionList){
                        System.out.println();
                        count=0;
                        for (String str: collection){
                            count++;
                            System.out.println(str);
                        }
                        System.out.println(count-1);//-1 because -the title
                    }
                    /////////////////////////////////////*/

                    int row = 0;
                    int col = 0;

                    //System.out.println("CollSize = " + CollectionList.size());


                    for (ArrayList<String> collection : CollectionList) {//Add Songs and Title Panes

                        if (collection.size() > 1) {

                            String collectionName = collection.get(0);

                            //This for the songPaneCollectionList//////////////////////////////////
                            ArrayList<SongPane> songPaneCollection = new ArrayList<>();

                            int finalCol = col;
                            int finalRow = row;

                            //Put title pane
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    controller.addToGrid(new Pane() {{
                                        getChildren().add(new Label(collection.get(0) + " " + (collection.size() - 1)));
                                    }}, finalCol, finalRow);
                                }
                            });

                            row++;

                            for (int i = 1; i < collection.size(); i++) {

                                File musicFile = null;
                                File imageFile = null;

                                File file = hashMap.get(collection.get(i));
                                //System.out.println(hashMap.get(collection.get(i)));


                                //////////////////////////////////////////////////////////////DEBUG
                                //System.out.println(file);

                                if (file != null) {



                                    SongPane songPane = new SongPane(file.getName(), collection.get(i), file, musicFile, imageFile, collectionName);
                                    songPaneCollection.add(songPane);

                                    //DEBUG
                                    //System.out.println(songPane + "\n");


                                    int finalCol1 = col;
                                    int finalRow1 = row;

                                    //Add SongPane
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                controller.addToGrid(songPane, finalCol1, finalRow1);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    // FIXME: 8/18/2021 and then comment row++ after the fixme below
                                    row++;

                                }
                                /*else {

                                    // FIXME: 8/18/2021 only put this back once osu stops being stupid and keeping md5 hashes from deleted songs
                                    int finalCol3 = col;
                                    int finalRow3 = row;

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            controller.addToGrid(new Pane() {{
                                                getChildren().add(new Label("<>"));
                                            }}, finalCol3, finalRow3);
                                        }
                                    });
                                }
                                row++;*/
                            }//for

                            //add to map
                            try {
                                songPaneCollectionList.add(songPaneCollection);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            //System.out.println("size: " + songPaneCollection.size());

                        } else {
                            if (collection.size() == 1) {

                                int finalCol2 = col;
                                int finalRow2 = row;

                                //Only add title
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        controller.addToGrid(new Pane() {{
                                            getChildren().add(new Label(collection.get(0)));
                                        }}, finalCol2, finalRow2);
                                    }
                                });
                            }
                        }
                        //System.out.println();

                        row = 0;
                        col++;
                    }

                    System.out.println("Hashset length: " + hashMap.size());

                    /*for (int i=0; i<hashMap.size(); i++){
                        System.out.println(hashMap.toString());
                    }*/

                    System.out.println("Begin Worker Completed!");

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
