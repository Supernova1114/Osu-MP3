package sample;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.swing.*;

public class MusicPlayer extends Application{

    public static MediaPlayer player;
    public static boolean isActive = false;
    public static boolean isPaused = false;
    static SwingWorker worker;
    static double volume = 0.5;


    public static void playMedia(Media media) throws InterruptedException {

        if (!isActive) {
            isActive = true;
            isPaused = false;

            Main.controller.pauseButton.setText("| |");

            player = new MediaPlayer(media);
            player.setVolume(volume);
            player.play();
        }else {
            player.stop();
            isActive = false;
            isPaused = true;

            Main.controller.pauseButton.setText(">");

            playMedia(media);
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


