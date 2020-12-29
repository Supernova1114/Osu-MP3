package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jdk.nashorn.internal.parser.JSONParser;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sample.fxml"));

        Parent root = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();


        //Path to songs folder
        String path = "D:\\Program Files\\osu!\\Songs\\";

        File songFolder = new File(path);


        //Filter to return only .osu files
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".osu");
            }
        };



        Map <String, File> hashMap = new HashMap<>();
        ArrayList<File[]> diffListList = new ArrayList<>();
        File [] beatmapList = songFolder.listFiles();


        //Adds lists of .osu files for each beatmap into an ArrayList;
        for ( File beatmap: beatmapList ){
            File [] difficultyList = beatmap.listFiles(filenameFilter);
            diffListList.add(difficultyList);
        }

        for (File [] diffList: diffListList){
            for (File difficulty: diffList){
                String key = MD5Calculator.GetMD5Hash(difficulty);
                hashMap.put(key, difficulty);
                System.out.println(hashMap.size() + " " + key);
            }
        }


        System.out.println("\n" + hashMap.size() + " Beatmaps Available");

       /* System.out.println();
        System.out.println(hashMap);
        System.out.println();*/

        File collectionsDB = new File("D:\\Program Files\\osu!\\collection.db");

        String data = "";
        data = new String(Files.readAllBytes(Paths.get("D:\\Program Files\\osu!\\collection.db")));
        //System.out.println(data);

        //System.out.println(data);

        //String clean = data.replaceAll("\\P{Print}", " ");
        //String [] cleanSplit = clean.split(" ");
        String [] clean = data.split("\\P{Print}");

        ArrayList<String> cleanArray = new ArrayList<>();

        for (String a: clean){
            //System.out.print(a + ",");
            if (!a.equals("")){
                cleanArray.add(a);
            }
        }

        System.out.println("\n\n\n");

        //System.out.println(cleanArray);

        cleanArray.remove(0);

        /*for (int i=0; i<cleanArray.size(); i++){
            if ( String.valueOf(cleanArray.get(i).charAt(0)).equals(" ") )
            cleanArray.set(i, cleanArray.get(i).substring(1) );
        }*/

        //System.out.println("\n\n\n");

        //System.out.println(cleanArray);


        ArrayList<ArrayList<String>> CollectionList = new ArrayList<>();


        for (int i=0; i<cleanArray.size(); i++) {

            if (cleanArray.get(i).length() < 32) {
                int finalI = i;
                CollectionList.add(new ArrayList<String>() {{
                    add(cleanArray.get(finalI));
                }});
            }else{
                CollectionList.get(CollectionList.size()-1).add(cleanArray.get(i).replace(" ", ""));
            }
        }




        System.out.println(CollectionList);

        System.out.println("\n\n\n");
        int row = 0;
        int col = 0;
        for (ArrayList<String> collection: CollectionList){
            if (collection.size() > 1){
                System.out.println(collection.get(0));
                controller.addToGrid(new Pane(){{getChildren().add(new Label(collection.get(0)));}}, col, row);
                row++;
                for (int i=1; i<collection.size(); i++){
                    int finalI = i;

                    controller.addToGrid(new Pane() {{
                        String name = hashMap.get(collection.get(finalI)).getName();
                        getChildren().add(new Label(name.substring(name.indexOf("["), name.indexOf("]")+1)));
                    }}, col, row);

                    row++;
                }
            }else {
                if (collection.size() == 1) {
                    System.out.println(collection.get(0));
                    controller.addToGrid(new Pane() {{
                        getChildren().add(new Label(collection.get(0)));
                    }}, col, row);
                }
            }
            System.out.println();
            row=0;
            col++;
        }


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


    public static void main(String[] args) {
        launch(args);
    }
}
