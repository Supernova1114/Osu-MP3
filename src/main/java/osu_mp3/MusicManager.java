package osu_mp3;

import javafx.application.Platform;

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
            nextSong();
            return null;
        });

        musicPlayer.setTimeChangedCallback((duration)->{
            Platform.runLater(() -> App.controller.setCurrentSeekTime(duration));
            return null;
        });

        musicPlayer.setStartOfMediaCallback((n)->{
            onStartOfMedia();
            return null;
        });
    }

    public void playMedia(SongPane pane) {

        // If this is a song from a different collection, create a new playlist
        if (currentCollectionName != pane.collectionName) {
            currentCollectionName = pane.collectionName;

            // find collection
            musicQueue = new MusicQueue(App.songCollectionDict.get(pane.collectionName));
            musicQueue.shuffle();
            musicQueue.moveToFront(pane.songData);
        } else {
            musicQueue.moveToNextIndex(pane.songData);
        }

        //musicQueue.moveToCurrentIndex(pane.songData);

        currentSong = pane.songData;
//        pane.setLabelColor(Color.RED);
        musicPlayer.playMedia(pane.songData.filePath);
    }

    public void playMedia(SongData songData) {
        currentSong = songData;
        musicPlayer.playMedia(songData.filePath);
    }

    public void onStartOfMedia() {
        Platform.runLater(() -> {
            App.controller.refreshSeekBar(musicPlayer.getDuration());
            App.controller.pauseButton.setText("| |");
            App.controller.songTitleLabel.setText("Playing: " + currentSong.artistName + " - " + currentSong.songName);
        });
    }

    public void togglePause() {
        boolean wasSetToPlay = musicPlayer.togglePause();
        Platform.runLater(() -> App.controller.pauseButton.setText(wasSetToPlay ? "| |" : ">"));
    }

    public void nextSong() {
        if (musicQueue != null) {
            playMedia(musicQueue.nextSong());
        }
    }

    public void prevSong() {
        if (musicQueue != null) {
            playMedia(musicQueue.prevSong());
        }
    }

    public void setVolume(double percent) {
        musicPlayer.setVolume(percent);
    }

    public void seek(double seconds) {
        musicPlayer.seek(seconds);
    }

    public void dispose() {
        musicPlayer.dispose();
    }

    public static MusicManager getInstance() {
        return instance;
    }
}
