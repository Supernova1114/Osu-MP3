import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class MusicPlayer extends Application{

    public static MediaPlayer player;
    public static boolean isActive = false;
    public static boolean isPaused = false;
    static SwingWorker worker;
    static double volume = 0.5;

    private static ArrayList<SongPane> currentCollection;

    public static ArrayList<SongPane> tempCollection = new ArrayList<>();//a copy of currentCollection so it can be edited
    public static ArrayList<SongPane> timeline = new ArrayList<>();

    private static int tempCollectionSize = 0;

//    private static boolean createPlaylist = false;
    private static boolean flag = false;

    private static SongPane previousSong;
    private static int songIndex = 0;

    public static void insertSong(SongPane pane){
        if (flag == false) {
            if (tempCollection.indexOf(pane) != songIndex) {
                tempCollection.remove(pane);
                songIndex++;

                if (songIndex >= tempCollectionSize){//clamp index
                    songIndex = tempCollectionSize-1;
                }

                tempCollection.add(songIndex, pane);
                System.out.println("inserted at index: " + songIndex);
                System.out.println();
            }
        }else
            flag = false;
    }

    public static void playMedia(SongPane pane) throws InterruptedException {
        Media media = new Media(pane.musicFile.toURI().toString());

        if (currentCollection == null || currentCollection.get(0).collectionName != pane.collectionName) {
            //search for collection
            for (int i = 0; i < Main.songPaneCollectionList.size(); i++) {
                if (Main.songPaneCollectionList.get(i).get(0).collectionName.equals(pane.collectionName)) {
                    currentCollection = Main.songPaneCollectionList.get(i);
                    //
                    System.out.println(currentCollection.get(0).name);

                    //copy currentCollection to tempCollection
                    tempCollection.clear();
                    for (SongPane song: currentCollection){
                        tempCollection.add(song);
                    }
                    tempCollectionSize = tempCollection.size();//get size

                    Collections.shuffle(tempCollection);
                    songIndex = 0;
                    tempCollection.remove(pane);
                    tempCollection.add(0, pane);


                    System.out.println("Chosen New Collection!");
                    break;
                }
            }

            //createPlaylist = true;
            flag = true;

        }

        /*if (createPlaylist){
            createPlaylist=false;
        }else{

        }*/


        if (!isActive) {
            isActive = true;
            isPaused = false;

            Main.controller.pauseButton.setText("| |");

            if (player !=null)
                player.dispose();

            player = new MediaPlayer(media);

            System.out.println("current index: " + songIndex);

            previousSong = pane;//current song


           /* if (timeline.contains(pane))//remove if already exists
                timeline.remove(pane);

            timeline.add(pane);//add song to dynamic timeline*/

            //System.out.println("wowowoowo");

            //testing
            //player.setStopTime(Duration.seconds(0.05));

            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    System.out.println();
                    System.out.println("Finished Song!");
                    if (songIndex < tempCollectionSize-1) {/////<<< Should i keep? was for the remove
                        songIndex++;

                        System.out.println(songIndex);
                        SongPane nextSong = tempCollection.get(songIndex);  //(int)(Math.random() * tempCollectionSize-1));
                        System.out.println("Now Playing: " + nextSong.name + "\n In Collection: " + tempCollection.get(0).collectionName);
                        try {
                            playMedia(nextSong);
                        /*int index = Main.controller.gridPane.getChildren().indexOf(nextSong);
                        ((SongPane)Main.controller.gridPane.getChildren().get(index)).label.setTextFill(Color.RED);
                        int index2 = Main.controller.gridPane.getChildren().indexOf(pane);
                        ((SongPane)Main.controller.gridPane.getChildren().get(index2)).label.setTextFill(Color.DARKBLUE);*/
                            nextSong.label.setTextFill(Color.RED);
                            pane.label.setTextFill(Color.DARKBLUE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            player.setVolume(volume);
            player.play();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.controller.songTitleLabel.setText("Playing: " + pane.label.getText());
                }
            });

        }else {
            player.stop();
            isActive = false;
            isPaused = true;

            Main.controller.pauseButton.setText(">");

            previousSong.label.setTextFill(Color.DARKBLUE);//previous song
            pane.label.setTextFill(Color.RED);//current song

            playMedia(pane);
        }

    }


    public static void setVolume(double percent){
        volume = percent;

        if (player != null)
        player.setVolume(percent);
    }

    //Pause Music
    public static void pause(){
        if (player != null && isActive){
            player.pause();
            isPaused = true;
            Main.controller.pauseButton.setText(">");
        }
    }

    //Play Music
    public static void play(){
        if (player != null && isActive){
            player.play();
            isPaused = false;
            Main.controller.pauseButton.setText("| |");
        }
    }

    public static void nextSong() throws InterruptedException {
        if (isActive) {
            if (songIndex + 1 < tempCollectionSize) {
                songIndex++;
                playMedia(tempCollection.get(songIndex));


            }
        }
    }

    public static void prevSong() throws InterruptedException {
        if (isActive) {
            if (songIndex > 0) {
                songIndex--;
                playMedia(tempCollection.get(songIndex));
                //playMedia(timeline.get(songIndex));

            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

}


