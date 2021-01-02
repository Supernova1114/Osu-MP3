package sample;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Scanner;

public class SongPane extends Pane {

    //Data
    public String name;
    public String md5Hash;
    private File file;
    private File musicFile;
    public File imageFile;
    public Label label;

    //temp
    //private boolean hasPlayed = false;

    //a Filter to find imageFile;
    FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
        }
    };



    public SongPane(String name, String md5Hash, File file, File musicFile) throws Exception {
        this.name = name;
        this.md5Hash = md5Hash;
        this.file = file;
        this.musicFile = musicFile;
        label = new Label(name);

        /*System.out.println(name);
        System.out.println(file);*/

        getChildren().add(label);

        //Find music file
        /*if (name.equals("Camellia feat. Nanahira - Amor De Verao (Tofu1222) [Regou's Another].osu") || name.equals("That Poppy - Altar (-NeBu-) [ChuuritsuTv's Old-Style Insane].osu"))
        try {
            System.out.println(file);
            Scanner scanner = new Scanner(file);//Scans beatmap diff file
            System.out.println(scanner.hasNext());
            scanner.close();
        }catch (Exception e){
            e.printStackTrace();
        }*/




        //System.out.println();


        /*//Find image file
        File[] imagefileList = file.getParentFile().listFiles(filenameFilter);//.png .jpg .jpeg for image files
        System.out.println();
        System.out.println();
        for (File imageFile: imagefileList){
            System.out.println(imageFile.getName());
        }*/


        //try to play music :)))
        setOnMouseClicked(event -> {
            ((Label) getChildren().get(0)).setTextFill(Color.DARKBLUE);
            try {
                MusicPlayer.playMedia(new Media(this.musicFile.toURI().toString()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


    }
}
