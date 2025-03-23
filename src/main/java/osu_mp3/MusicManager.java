package osu_mp3;

import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.time.Duration;

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
                togglePause();
            }
            return null;
        });

        musicPlayer.setTimeChangedCallback((duration) -> {
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

            currentSong = null;

            // Clear color from grid of previous collection
            if (App.songCollectionDict.containsKey(currentCollectionID)) {
                for (SongData songData : App.songCollectionDict.get(currentCollectionID).getSongList()) {
                    App.songPaneLookupDict.get(songData).resetState();
                }
            }

            currentCollectionID = pane.collectionID;

            // find collection
            musicQueue = new MusicQueue(App.songCollectionDict.get(pane.collectionID));
            musicQueue.shuffle();
            musicQueue.moveToFront(pane.songData);
        } else {
            musicQueue.moveToNextIndex(pane.songData);
        }

        playMedia(pane.songData);
    }

    public void playMedia(SongData songData) {

        if (currentSong != null) {
            App.songPaneLookupDict.get(currentSong).setStatePlayed();
        }

        App.songPaneLookupDict.get(songData).setStatePlaying();

        musicPlayer.playMedia(songData.filePath);
        currentSong = songData;
    }

    private void onStartOfMedia() {
        Platform.runLater(() -> {
            App.controller.prepareSeekBar(musicPlayer.getDuration());
            App.controller.audioControlsPause();
            App.controller.audioControlsSetSongTitle(currentSong.artistName + " - " + currentSong.songName);
        });
    }

    public void togglePause() {
        boolean result = musicPlayer.togglePause();
        Platform.runLater(() -> {
            if (result) {
                App.controller.audioControlsPause();
            } else {
                App.controller.audioControlsPlay();
            }
        });
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

    public void stop() {
        musicQueue = null;
        currentSong = null;
        musicPlayer.dispose();
        Platform.runLater(() -> {
            App.controller.audioControlsReset();
        });
    }

    public static MusicManager getInstance() {
        return instance;
    }
}
