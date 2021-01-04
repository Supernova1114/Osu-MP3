package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    private static SongPane currentSong;


    public static void playMedia(SongPane pane) throws InterruptedException {
        Media media = new Media(pane.musicFile.toURI().toString());

        if (currentCollection == null || currentCollection.get(0).collectionName != pane.collectionName) {
            //search for collection
            for (int i = 0; i < Main.songPaneCollectionList.size(); i++) {
                if (Main.songPaneCollectionList.get(i).get(0).collectionName.equals(pane.collectionName)) {
                    currentCollection = Main.songPaneCollectionList.get(i);

                    //copy currentCollection to tempCollection
                    tempCollection.clear();
                    for (SongPane song: currentCollection){
                        tempCollection.add(song);
                    }
                    Collections.shuffle(tempCollection);

                    System.out.println("Chosen New Collection!");
                    break;
                }
            }
        }




        if (!isActive) {
            isActive = true;
            isPaused = false;

            Main.controller.pauseButton.setText("| |");

            player = new MediaPlayer(media);

            currentSong = pane;

            //player.setStopTime(Duration.seconds(0.1));

            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    System.out.println();
                    System.out.println("Finished Song!");
                    if (tempCollection.size() > 0) {
                        SongPane randomSong = tempCollection.get(0);  //(int)(Math.random() * tempCollection.size()-1));
                        System.out.println("Now Playing: " + randomSong.name + "\n In Collection: " + tempCollection.get(0).collectionName);
                        tempCollection.remove(randomSong);
                        try {
                            playMedia(randomSong);
                        /*int index = Main.controller.gridPane.getChildren().indexOf(randomSong);
                        ((SongPane)Main.controller.gridPane.getChildren().get(index)).label.setTextFill(Color.RED);
                        int index2 = Main.controller.gridPane.getChildren().indexOf(pane);
                        ((SongPane)Main.controller.gridPane.getChildren().get(index2)).label.setTextFill(Color.DARKBLUE);*/
                            randomSong.label.setTextFill(Color.RED);
                            pane.label.setTextFill(Color.DARKBLUE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            player.setVolume(volume);
            player.play();
        }else {
            player.stop();
            isActive = false;
            isPaused = true;

            Main.controller.pauseButton.setText(">");

            currentSong.label.setTextFill(Color.DARKBLUE);

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


    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}


