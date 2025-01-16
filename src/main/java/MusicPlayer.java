import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {

    public static MediaPlayer player;
    public static boolean isActive = false;
    public static boolean isPaused = false;
    //static SwingWorker worker;
    static double volume = 0.5;

    private static List<SongPane> currentCollection;

    public static List<SongPane> tempCollection = new ArrayList<>();//a copy of currentCollection so it can be edited
    //public static ArrayList<SongPane> timeline = new ArrayList<>();

    private static int tempCollectionSize = 0;

    //private static boolean createPlaylist = false;
    private static boolean flag = false;

    private static SongPane previousSong;
    private static int songIndex = 0;

    public static Duration totalDuration;
    public static Duration startTime;


    public static void playMedia(SongPane pane) throws InterruptedException {

        //gets media file
        Media media = new Media(pane.musicFile.toURI().toString());

        //Makes shuffled playlist when new playlist is clicked.
        if (currentCollection == null || !currentCollection.get(0).collectionName.equals(pane.collectionName)) {
            //search for collection
            for (int i = 0; i < App.songPaneCollectionList.size(); i++) {
                if (App.songPaneCollectionList.get(i).get(0).collectionName.equals(pane.collectionName)) {
                    currentCollection = App.songPaneCollectionList.get(i);

                    //System.out.println(pane.name);

                    //copy currentCollection to tempCollection
                    tempCollection.clear();

                    tempCollection.addAll(currentCollection);

                    tempCollectionSize = tempCollection.size();//get size

                    Collections.shuffle(tempCollection);
                    songIndex = 0;
                    tempCollection.remove(pane);
                    tempCollection.add(0, pane);


                    System.out.println("Chosen New Collection!");
                    break;
                }
            }

            flag = true;

        }


        if (!isActive) {
            isActive = true;

            //creates new media player and inserts media
            player = new MediaPlayer(media);

            App.controller.seekBar.setDisable(true);


            //Changes seekbar currentTime as music progresses.
            player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    App.controller.setCurrentSeekTime(newValue.toSeconds());
                }
            });



            //System.out.println("current index: " + songIndex + " Duration: " + player.getTotalDuration().toSeconds());

            previousSong = pane;//current song


            //On end of media
            //run()
            player.setOnEndOfMedia(() -> {
                //System.out.println();
                //System.out.println("Finished Song!");

                if (songIndex < tempCollectionSize-1) {/////<<< Should i keep? was for the remove //Fix < dont mind this rn
                    //increase index
                    songIndex++;

                    //System.out.println(songIndex);

                    //get next song in list
                    SongPane nextSong = tempCollection.get(songIndex);  //(int)(Math.random() * tempCollectionSize-1));
                    //System.out.println("Now Playing: " + nextSong.name + "\n In Collection: " + tempCollection.get(0).collectionName);

                    try {
                        //play next song
                        playMedia(nextSong);

                        nextSong.label.setTextFill(Color.RED);
                        pane.label.setTextFill(Color.DARKBLUE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            });


            //get start and stop times (ready == when it gets the data cause it is async)
            player.setOnReady(() -> {
                totalDuration = media.getDuration();

                App.controller.refreshSeekBar(totalDuration);

                player.setVolume(volume);
                player.play();

                isPaused = false;
                App.controller.pauseButton.setText("| |");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        App.controller.songTitleLabel.setText("Playing: " + pane.label.getText());
                    }
                });

                //System.out.println("current index: " + songIndex + " Duration: " + player.getTotalDuration().toSeconds());


            });



        //if !Active
        }else {
            //else if Active (will run when setOnEndOfMedia runs because media player is active at that time)

            player.stop();
            isActive = false;
            isPaused = true;

            App.controller.pauseButton.setText(">");

            previousSong.label.setTextFill(Color.DARKBLUE);//previous song
            pane.label.setTextFill(Color.RED);//current song

            //HELP
            player.dispose();

            //System.out.println(player.getStatus());

            player = null;

            //play new song
            playMedia(pane);
        }

    }//playMedia()


    //inserts a songs at current list index when clicked and removes it from its previous location
    public static void insertSong(SongPane pane){
        if (flag == false) {
            if (tempCollection.indexOf(pane) != songIndex) {
                tempCollection.remove(pane);
                songIndex++;

                if (songIndex >= tempCollectionSize){//clamp index
                    songIndex = tempCollectionSize-1;
                }

                tempCollection.add(songIndex, pane);
                //System.out.println("inserted at index: " + songIndex);
            }
        }else
            flag = false;
    }


    //sets volume of media player
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
            App.controller.pauseButton.setText(">");
        }
    }

    //Play Music
    public static void play(){
        if (player != null && isActive){
            player.play();
            isPaused = false;
            App.controller.pauseButton.setText("| |");
        }
    }

    //plays next song
    public static void nextSong() throws InterruptedException {
        if (isActive) {
            if (songIndex + 1 < tempCollectionSize) {
                songIndex++;
                playMedia(tempCollection.get(songIndex));


            }
        }
    }

    //plays prev song
    public static void prevSong() throws InterruptedException {
        if (isActive) {
            if (songIndex > 0) {
                songIndex--;
                playMedia(tempCollection.get(songIndex));
                //playMedia(timeline.get(songIndex));

            }
        }
    }

    //set seekbar media position
    public static void setSeek(double seconds){
        player.seek(Duration.seconds(seconds));

        //System.out.println(player.getStatus());

        //System.out.println("-seekPos = " + (int)player.currentTimeProperty().get().toSeconds() + " -seconds: " + (int)seconds);
        //System.out.println("Curr Time = " + player.getCurrentTime().toSeconds());

    }


}


