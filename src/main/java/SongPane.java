import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FilenameFilter;

public class SongPane extends Pane {

    //Data
    public String name;
    public String md5Hash;
    public String collectionName;
    public File file;
    public File musicFile;
    public File imageFile;
    public Label label;
    public Image image;
    boolean isDragged = false;


    //temp
    //private boolean hasPlayed = false;

    //a Filter to find imageFile;
    FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
        }
    };



    public SongPane(String name, String md5Hash, File file, File musicFile, File imageFile, String collectionName) {
        this.name = name;
        this.md5Hash = md5Hash;
        this.file = file;
        this.musicFile = musicFile;
        this.imageFile = imageFile;
        //LiSA - Rising Hope (TV Size) (xChippy) [Hope]
        //int sub = name.indexOf(" - ");

        try {
            //label = new Label(name.substring(sub + 3, name.substring(sub + 3).indexOf("(") + sub + 3));//name.indexOf(".osu")
            label = new Label(name.substring(0, name.lastIndexOf("(")));

            /*label.setTextFill(Color.WHITE);
            label.setWrapText(true);*/

            //label = new Label(name);
        }catch (Exception e){
            System.out.println("err: Label Failed");
            e.printStackTrace();
        }
        this.collectionName = collectionName;

        /*System.out.println(name);
        System.out.println(file);*/


        getChildren().add(label);


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
            if (!isDragged){
                ((Label) getChildren().get(0)).setTextFill(Color.RED);
                try {
                    MusicPlayer.playMedia(this);
                    MusicPlayer.insertSong(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        setOnMouseDragged(event -> {
            isDragged = true;
        });

        setOnMousePressed(event -> {
            isDragged = false;
        });


    }


    public String toString(){
        return "SongPane: \n" + name + "\n" + md5Hash + "\n" + file + "\n" + musicFile;
    }


}
