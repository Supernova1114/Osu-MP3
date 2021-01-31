import com.melloware.jintellitype.JIntellitype;
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
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Main extends Application{

    public static Stage primaryStage;
    public static Controller controller;
    public static File osuFolder = null;//new File("D:\\Program Files\\osu!");
    public static ArrayList<ArrayList<SongPane>> songPaneCollectionList = new ArrayList<>();
    public static Parent root;

    public static String currentDirectory = System.getProperty("user.dir");
    public static Path songHashMapPath = Paths.get(currentDirectory + File.separator + "SongMapHash.db");
    public static String settingsPath = currentDirectory + File.separator + "settings.conf";

    public static Properties applicationProps;

    static long songFolderLastModified = -1;

    private static String hashMapSplitChar = " [] ";

    public static Map<String, File> hashMap = new HashMap<>();

    public static WindowsKeyListener keyListener;



    @Override
    public void start(Stage stage) throws Exception{
        primaryStage = stage;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                JIntellitype.getInstance().cleanUp();
                System.exit(0);
            }
        });

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sample.fxml"));

        root = fxmlLoader.load();
        controller = fxmlLoader.getController();

        /*root.addEventFilter(KeyEvent.KEY_RELEASED, k -> {
            if ( k.getCode() == KeyCode.SPACE || k.getCode() == KeyCode.F7 || k.getCode().isMediaKey() ){
                root.requestFocus();
                controller.TogglePause();
                k.consume();
            }
        });*/


       /* root.setOnKeyReleased(k -> {

        });*/

        /*root.setOnKeyPressed(k -> {
            //System.out.println(k.getCode());

        });*/



        /*root.setOnKeyPressed(k -> {

        });*/

        /*WindowsProvider provider = new WindowsProvider();

        provider.register(MediaKey.MEDIA_PLAY_PAUSE, new HotKeyListener() {
            @Override
            public void onHotKey(HotKey hotKey) {
                System.out.println(hotKey.toString());
            }
        });*/


        Scene scene = new Scene(root, 600, 600);
        /*scene.setOnKeyPressed(event -> {
            System.out.println(event.getCode());
        });*/


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



        primaryStage.setTitle("Osu! MP3");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Start up JIntellitype
        keyListener = new WindowsKeyListener();



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

            applicationProps.setProperty("osuFolderLocation", "null");

            FileOutputStream out = new FileOutputStream(settingsPath);
            applicationProps.store(out, "---No Comment---");
            out.close();
        }

        String osuFolderLocProp = applicationProps.getProperty("osuFolderLocation");

        if (osuFolderLocProp != "null"){
            osuFolder = new File(osuFolderLocProp);
            Begin();
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





    }


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
                    File songFolder = osuFolder.listFiles(SONGSFOLDER)[0];
                    File collectionsDB = osuFolder.listFiles(COLLECTIONDB)[0];
                    //collectionsDB = new File("C:\\Users\\Fart\\Downloads\\Osu MP4 test coll\\seal\\collection.db");

                    //songFolderLastModified = songFolder.lastModified();
                    songFolderLastModified = collectionsDB.lastModified();

                    if (Files.exists(songHashMapPath)) {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(songHashMapPath.toFile()));
                        String line;
                        ArrayList<String> lineList = new ArrayList();
                        while ((line = bufferedReader.readLine()) != null) {
                            //System.out.println(line);
                            lineList.add(line);
                            //System.out.println(line);
                        }
                        bufferedReader.close();
                        System.out.println("lines: " + lineList.size());

                        long lastModified = Long.parseLong(lineList.get(1));

                        System.out.println("Last Modified:\n[" + songFolderLastModified + "\n" + lastModified + "]");

                        if (songFolderLastModified != lastModified){
                            System.out.println("Song Folder Was Modified!");

                            Files.deleteIfExists(songHashMapPath);
                            getSongsNormally(songFolder, collectionsDB);
                        }else {

                            //add to hashmap
                            for (int i = 3; i < lineList.size(); i++) {//i=3 is 4th line to begin at
                                //System.out.println(lineList.get(i));
                                String hash = lineList.get(i).substring(0, 32);//32 = hash size
                                File file = new File(lineList.get(i).substring(32 + hashMapSplitChar.length()));
                                //System.out.println(hash);
                                hashMap.put(hash, file);

                            }
                        }

                    }else {
                        getSongsNormally(songFolder, collectionsDB);
                    }
                    // FIXME: 1/7/2021


                    // Files.deleteIfExists(songHashMapPath);//temppppppppp



                    //File collectionsDB = new File("D:\\Program Files\\osu!\\collection.db");

                    //String data = "";
                    String data = new String(Files.readAllBytes(Paths.get(collectionsDB.getPath())));

                    /*BufferedReader collectionFileReader = new BufferedReader(new FileReader(collectionsDB.getPath()));

                    String data = null;
                    int j;
                    while ((j = collectionFileReader.read()) != -1) {
                        data += (char)j;
                    }*/


                    String[] clean = data.split("\\P{Print}");



                    ArrayList<String> cleanArray = new ArrayList<>();

                    for (String a : clean) {
                        //System.out.print(a + ",");
                        if (!a.equals("")) {
                            cleanArray.add(a);
                        }
                    }

                    System.out.println(cleanArray);

                    cleanArray.remove(0);

                    ArrayList<ArrayList<String>> CollectionList = new ArrayList<>();


                    for (int i = 0; i < cleanArray.size(); i++) {

                        if (cleanArray.get(i).length() < 32) {
                            int finalI = i;
                            CollectionList.add(new ArrayList<String>() {{
                                add(cleanArray.get(finalI));
                            }});
                        } else {
                            CollectionList.get(CollectionList.size() - 1).add(cleanArray.get(i).replace(" ", ""));
                        }
                    }

                    //DEBUG////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                    /////////////////////////////////////

                    int row = 0;
                    int col = 0;

                    for (ArrayList<String> collection : CollectionList) {//Add Songs and Title Panes
                        if (collection.size() > 1) {

                            //This for the songPaneCollectionList//////////////////////////////////
                            ArrayList<SongPane> songPaneCollection = new ArrayList<>();


                            String collectionName = collection.get(0);



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
                                            break;
                                        }
                                    }

                                    beatmapFileReader.close();

                                    SongPane songPane = new SongPane(file.getName(), collection.get(i), file, musicFile, collectionName);
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

    //get songs by scanning through song folder
    public static void getSongsNormally(File songFolder, File collectiondb) throws IOException, NoSuchAlgorithmException {

        ArrayList<File[]> diffListList = new ArrayList<>();
        File[] beatmapList = songFolder.listFiles();


        //Adds lists of .osu files for each beatmap into an ArrayList;
        for (File beatmap : beatmapList) {
            File[] difficultyList = beatmap.listFiles(getFilenameFilter(".osu"));
            diffListList.add(difficultyList);
        }

        try {
            for (File[] diffList : diffListList) {
                for (File difficulty : diffList) {
                    System.out.println(difficulty.getPath());
                    String key = MD5Calculator.GetMD5Hash(difficulty);
                    hashMap.put(key, difficulty);
                    //System.out.println(hashMap.size() + " " + key);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.label.setText(hashMap.size() + "");
                        }
                    });

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        System.out.println(hashMap.size() + " Beatmaps Available");


        System.out.println("MISSING SONGHASHMAPFILE");
        Files.createFile(songHashMapPath);

        String [] hashSet = hashMap.keySet().toArray(new String[0]);
        File [] fileSet = hashMap.values().toArray(new File[0]);


        assert hashSet.length == fileSet.length;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(songHashMapPath.toString())));
        bufferedWriter.write("#SONG AND HASH LIBRARY");
        bufferedWriter.newLine();
        bufferedWriter.write(collectiondb.lastModified() + "");//
        bufferedWriter.newLine();
        bufferedWriter.newLine();
        System.out.println("hashset length: " + hashSet.length);
        for (int i=0; i<hashSet.length; i++){
            bufferedWriter.write(hashSet[i]);
            bufferedWriter.write(hashMapSplitChar);
            bufferedWriter.write(fileSet[i].toPath().toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }


    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }
}
