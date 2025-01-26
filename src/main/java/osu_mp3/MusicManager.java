package osu_mp3;

import javafx.application.Platform;

import java.util.List;

public class MusicManager {

    private static MusicManager instance;
    private String currentCollectionName = "";
    private MusicPlayer musicPlayer;
    private MusicQueue musicQueue;
    private SongData currentSong;


    public MusicManager() {

        instance = this;

        musicPlayer = new MusicPlayer();

        musicPlayer.setEndOfMediaCallback((n)->{
            musicPlayer.playMedia(musicQueue.nextSong().filePath);
            return null;
        });

        musicPlayer.setTimeChangedCallback((seconds)->{
            App.controller.setCurrentSeekTime(seconds);
            return null;
        });

        musicPlayer.setStartOfMediaCallback((n)->{
            onStartOfMedia();
            return null;
        });
    }

    public void playMedia(SongPane pane) {

        if (currentCollectionName != pane.collectionName) {
            currentCollectionName = pane.collectionName;

            // find collection
            musicQueue = new MusicQueue(App.songCollectionDict.get(pane.collectionName));
            musicQueue.shuffle();
        }

        musicQueue.moveToCurrentIndex(pane.songData);

        currentSong = pane.songData;
        musicPlayer.playMedia(pane.songData.filePath);
    }

    public void playMedia(SongData songData) {
        currentSong = songData;
        musicPlayer.playMedia(songData.filePath);
    }

    public void onStartOfMedia() {

        App.controller.refreshSeekBar(musicPlayer.getDuration());

        App.controller.pauseButton.setText("| |");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                App.controller.songTitleLabel.setText("Playing: " + currentSong.artistName + " - " + currentSong.songName);
            }
        });
    }

    public void play() {
        musicPlayer.play();
        App.controller.pauseButton.setText("| |");
    }

    public void pause() {
        musicPlayer.pause();
        App.controller.pauseButton.setText(">");
    }

    public void togglePause() {
        musicPlayer.togglePause();
    }

    public void nextSong() {
        playMedia(musicQueue.nextSong());
    }

    public void prevSong() {
        playMedia(musicQueue.prevSong());
    }

    public void setVolume(double percent) {
        musicPlayer.setVolume(percent);
    }

    public void seek(double seconds) {
        musicPlayer.seek(seconds);
    }
    public static MusicManager getInstance() {
        return instance;
    }
}
