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

public class Main extends Application{

    private  String version = "0.6.1";
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
            //System.out.println("Key pressed");

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
            System.out.println("Settings File Exists");
            applicationProps = new Properties();

            FileInputStream in = new FileInputStream(settingsPath);
            applicationProps.load(in);
            in.close();
        }else {
            System.out.println("Settings File Does Not Exist");
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

                        if (collectionDBLastModified != collectionDBPrevLastMod){
                            System.out.println("collection.db was modified!");

                            if (songsFolderLastModified != songsFolderPrevLastMod){
                                System.out.println("Songs folder was modified!");

                                // FIXME: 8/17/2021 Look for which beatmap folder was modified and then replace the info, look for new or missing song folders
                                while ((line = bufferedReader.readLine()) != null){
                                    lineList.add(line);
                                }

                                bufferedReader.close();
                                getSongsFromDatabase(lineList, songFolder, true);
                            }else {
                                //if Songs folder was not modified
                                while ((line = bufferedReader.readLine()) != null){
                                    lineList.add(line);
                                }

                                bufferedReader.close();
                                getSongsFromDatabase(lineList, songFolder, false);

                            }

                        }else {
                            //if collection.db was not modified
                            while ((line = bufferedReader.readLine()) != null){
                                lineList.add(line);
                            }
                            bufferedReader.close();

                            getSongsFromDatabase(lineList, songFolder, false);
                        }





                    }else {
                        getSongsFromSongFolder(songFolder, collectionsDB);
                    }

                    System.out.println();

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
                            //System.out.println(str);
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


                                //////////////////////////////////////////////////////////////DEBUG
                                //System.out.println(file);

                                if (file != null) {

                                    BufferedReader beatmapFileReader = new BufferedReader(new FileReader(file));



                                    String line;
                                    while ((line = beatmapFileReader.readLine()) != null) {

                                        if (line.contains("AudioFilename")) {
                                            //System.out.println(line);
                                            musicFile = new File(file.getParentFile().getPath() + File.separator + line.substring(15));//15 is length of [AudioFilename: ]
                                        }
                                        if (line.contains("[Events]")){
                                            beatmapFileReader.readLine();
                                            String temp = beatmapFileReader.readLine();

                                            String [] tempSplit = temp.split(",");

                                            imageFile = new File(file.getParentFile().getPath() + File.separator + tempSplit[2].replace("\"", ""));

                                            //System.out.println(imageFile.getName());




                                        }
                                    }

                                    beatmapFileReader.close();

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

                                    //row++

                                }else {

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
                                row++;
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
    public static FilenameFilter getFilenameFilter(String endsWith){
        //will only return specified files
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(endsWith);
            }
        };
        return filenameFilter;
    }


    public static void setOsuFolder(File osuFolderFile) throws IOException {
        osuFolder = osuFolderFile;
        applicationProps.setProperty("osuFolderLocation", osuFolder.getPath());

        FileOutputStream out = new FileOutputStream(settingsPath);
        applicationProps.store(out, "---No Comment---");
        out.close();
    }


    //read Songs from Database file
    public static void getSongsFromDatabase(ArrayList<String> lineList, File songFolder, boolean isSongsFolderModified) throws IOException, NoSuchAlgorithmException {

        if (isSongsFolderModified == false) {
            for (int i = 5; i < lineList.size(); i++) {

                String temp = lineList.get(i);
                temp = temp.replaceAll("\\{ ", "").replaceAll("}", "");

                //Split main line into beatmap folder location, beatmap folder lastModified, and String Array of beatmaps with MD5;
                String[] firstSplit = temp.split(" \\| ");

                //Split String Array of beatmaps with MD5 into an actual array;
                String[] secondSplit = firstSplit[2].split(" ; ");

                //Split each into MD5, and beatmap difficulty location
                for (String str : secondSplit) {
                    String[] tempArray = str.split(" = ");

                    hashMap.put(tempArray[0], new File(tempArray[1]));//key, difficulty
                }

            }//for
        }else {

            Files.deleteIfExists(songsDatabaseFilePath);
            Files.createFile(songsDatabaseFilePath);

            BufferedWriter writer = new BufferedWriter(new FileWriter(songsDatabaseFilePath.toFile()));


            ArrayList<String> tempLineList = new ArrayList<>();
            //Make temp list with only beatmap database entries
            for (int i=5; i<lineList.size(); i++){
                tempLineList.add(lineList.get(i));
            }

            File[] beatmapList = songFolder.listFiles();
            ArrayList<String> bmFolderPaths = new ArrayList<>();

            for (int i=0; i< beatmapList.length; i++){
                bmFolderPaths.add(beatmapList[i].toPath().toString());
            }


            if (lineList.size() == bmFolderPaths.size()){

                for (int i=0; i< bmFolderPaths.size(); i++){
                    //check each folder to see whether it was modified or not

                    //Split main into beatmap folder location, beatmap folder lastModified, and String Array of beatmaps with MD5;
                    String[] firstSplit = tempLineList.get(i).split(" \\| ");


                    if ( bmFolderPaths.contains(firstSplit[0]) ){
                        //System.out.println("Success");
                        //System.out.println(bmFolderPaths.get(bmFolderPaths.indexOf(firstSplit[0])));

                        int index = bmFolderPaths.indexOf(firstSplit[0]);

                        long beatmapFolderLastMod = new File(bmFolderPaths.get(index)).lastModified();//from beatmap folder itself
                        long dbBeatmapFolderLastMod = Long.parseLong(firstSplit[1]);//from database


                        if ( beatmapFolderLastMod != dbBeatmapFolderLastMod ){
                            System.out.println("This beatmap folder was modified");

                            String entry = "";

                            entry += beatmapList[i].getPath().toString() + " | " + beatmapList[i].lastModified() + " | { ";

                            File[] difficultyList = beatmapList[i].listFiles(getFilenameFilter(".osu"));

                            for (File difficulty: difficultyList){
                                String key = MD5Calculator.GetMD5Hash(difficulty);

                                entry += key + " = " + difficulty.getPath().toString() + " ; ";
                            }

                            entry += "}";

                            tempLineList.set(i, entry);
                        }

                    }else {
                        tempLineList.remove(i);
                    }


                }
            }//lineList.size() == bmFolderPaths.size()

            // FIXME: 8/17/2021 make < and > now

        }







    }

    //get songs by scanning through song folder and create Database file
    public static void getSongsFromSongFolder(File songFolder, File collectiondb) throws Exception {

        File[] beatmapList = songFolder.listFiles();
        System.out.println("BeatmapFoldersTotal: " + beatmapList.length);

        //A list of lists of beatmap difficulties (.osu)
        ArrayList<File[]> difficultyListList = new ArrayList<>();

        ArrayList<String> databaseEntries = new ArrayList<>();



        for (int i=0; i<beatmapList.length; i++){
            String entry = "";

            entry += beatmapList[i].getPath().toString() + " | " + beatmapList[i].lastModified() + " | { ";

            File[] difficultyList = beatmapList[i].listFiles(getFilenameFilter(".osu"));
            difficultyListList.add(difficultyList);

            for (File difficulty: difficultyList){
                String key = MD5Calculator.GetMD5Hash(difficulty);

                entry += key + " = " + difficulty.getPath().toString() + " ; ";

                hashMap.put(key, difficulty);
            }

            entry += "}";

            databaseEntries.add(entry);
            System.out.println(i + " " + entry);

        }

        Files.createFile(songsDatabaseFilePath);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(songsDatabaseFilePath.toString()));

        bufferedWriter.write("#DATABASE");
        bufferedWriter.newLine();
        bufferedWriter.write(collectionDBLastModified + "");
        bufferedWriter.newLine();
        bufferedWriter.write(songsFolderLastModified + "");
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        for (int i=0; i<beatmapList.length; i++){

            bufferedWriter.newLine();
            bufferedWriter.write(databaseEntries.get(i));

        }//for

        bufferedWriter.close();

    }//getSongsNormally()


    //moved out of GlobalKeyListener
    public static void togglePause(){
        root.requestFocus();
        controller.TogglePause();
    }


    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }
}
