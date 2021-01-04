package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    public static Stage primaryStage;
    public static Controller controller;
    public static File osuFolder = null;//new File("D:\\Program Files\\osu!");
    public static ArrayList<ArrayList<SongPane>> songPaneCollectionList = new ArrayList<>();


    @Override
    public void start(Stage stage) throws Exception{
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sample.fxml"));

        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();


        primaryStage.setTitle("Osu! MP3");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

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


                    Map<String, File> hashMap = new HashMap<>();
                    ArrayList<File[]> diffListList = new ArrayList<>();
                    File[] beatmapList = songFolder.listFiles();


                    //Adds lists of .osu files for each beatmap into an ArrayList;
                    for (File beatmap : beatmapList) {
                        File[] difficultyList = beatmap.listFiles(getFilenameFilter(".osu"));
                        diffListList.add(difficultyList);
                    }

                    for (File[] diffList : diffListList) {
                        for (File difficulty : diffList) {
                            String key = MD5Calculator.GetMD5Hash(difficulty);
                            hashMap.put(key, difficulty);
                            //System.out.println(hashMap.size() + " " + key);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    controller.label.setText(hashMap.size() + " " + key);
                                }
                            });

                        }

                    }


                    System.out.println(hashMap.size() + " Beatmaps Available");


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
                            System.out.println(str);
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
                                        getChildren().add(new Label(collection.get(0)));
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


    public static void main(String[] args) {
        launch(args);
    }
}
