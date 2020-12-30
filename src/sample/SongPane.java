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
    public File file;
    public File musicFile;
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



    public SongPane(String name, String md5Hash, File file) throws FileNotFoundException {
        this.name = name;
        this.md5Hash = md5Hash;
        this.file = file;
        label = new Label(name);

        getChildren().add(label);

        //Find music file
        Scanner scanner = new Scanner(file);//Scans beatmap diff file
        while (scanner.hasNextLine()){
            String scan = scanner.nextLine();

            if (scan.contains("AudioFilename")){
                musicFile = new File(file.getParentFile().getPath() + File.separator + scan.substring(15));//15 is length of [AudioFilename: ]
                break;
            }
            //System.out.println(scanner.nextLine());
        }



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
                MusicPlayer.playMedia(new Media(musicFile.toURI().toString()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }




}
