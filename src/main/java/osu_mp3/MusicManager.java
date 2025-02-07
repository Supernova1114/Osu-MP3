package osu_mp3;

import javafx.application.Platform;

public class MusicManager {

    private static MusicManager instance;
    private int currentCollectionID = -1;
    private MusicPlayer musicPlayer;
    private MusicQueue musicQueue;
    private SongData currentSong;


    public MusicManager() {
        instance = this;
        initializeMusicPlayer();
    }

    private void initializeMusicPlayer() {
        musicPlayer = new MusicPlayer();

        musicPlayer.setEndOfMediaCallback((n)->{
            nextSong();
            if (musicQueue.isAtLastSong()) {
                // TODO - this should be pause only
                togglePause();
            }
            return null;
        });

        musicPlayer.setTimeChangedCallback((duration)->{
            if (App.controller.isSeekBarPressed == false) {
                Platform.runLater(() -> App.controller.setSeekBarPosition(duration));
            }
            return null;
        });

        musicPlayer.setStartOfMediaCallback((n)->{
            onStartOfMedia();
            return null;
        });
    }

    public void playMedia(SongPane pane) {

        // If this is a song from a different collection, create a new playlist
        if (currentCollectionID != pane.collectionID) {
            currentCollectionID = pane.collectionID;

            // find collection
            musicQueue = new MusicQueue(App.songCollectionDict.get(pane.collectionID));
            musicQueue.shuffle();
            musicQueue.moveToFront(pane.songData);
        } else {
            musicQueue.moveToNextIndex(pane.songData);
        }

        currentSong = pane.songData;
        musicPlayer.playMedia(pane.songData.filePath);
    }

    public void playMedia(SongData songData) {
        currentSong = songData;
        musicPlayer.playMedia(songData.filePath);
    }

    private void onStartOfMedia() {
        Platform.runLater(() -> {
            App.controller.prepareSeekBar(musicPlayer.getDuration());
            App.controller.pauseButton.setText("| |");
            App.controller.songTitleLabel.setText("Playing: " + currentSong.artistName + " - " + currentSong.songName);
        });
    }

    public void togglePause() {
        Platform.runLater(() -> App.controller.pauseButton.setText(musicPlayer.togglePause() ? "| |" : ">"));
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

    public void seek(long milliseconds) {
        musicPlayer.seek(milliseconds);
    }

    public void dispose() {
        musicPlayer.dispose();
    }

    public static MusicManager getInstance() {
        return instance;
    }
}
