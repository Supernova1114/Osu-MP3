package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
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
                String md5Hash = MD5Calculator.GetMD5Hash(difficulty);
                hashMap.put(md5Hash, difficulty);
                System.out.println(md5Hash);
            }
        }
        System.out.println(hashMap.size());

        String chosen = "ff1c1e17bd0b0c90ceb76747f66891be";
        File chosenMap = hashMap.get(chosen);
        System.out.println();
        System.out.println(chosenMap.getName());
        


       /* System.out.println();
        System.out.println(hashMap);
        System.out.println();
*/




    }


    public static void main(String[] args) {
        launch(args);
    }
}
