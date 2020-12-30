package sample;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.swing.*;

public class MusicPlayer extends Application{

    public static MediaPlayer player;
    static boolean isPlaying = false;
    static SwingWorker worker;
    static double volume = 0.5;


    public static void playMedia(Media media) throws InterruptedException {

        if (!isPlaying) {
            isPlaying = true;
            player = new MediaPlayer(media);
            player.setVolume(volume);
            player.play();
        }else {
            isPlaying = false;
            player.stop();
            playMedia(media);
        }

    }


    public static void setVolume(double percent){
        volume = percent;

        if (player != null)
        player.setVolume(percent);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}


