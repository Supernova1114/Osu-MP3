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


    public static void playMedia(Media media) throws InterruptedException {

        if (!isPlaying) {
            isPlaying = true;
            player = new MediaPlayer(media);
            player.play();
        }else {
            isPlaying = false;
            player.stop();
            playMedia(media);
        }

    }


    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}


