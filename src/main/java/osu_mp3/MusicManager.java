package osu_mp3;

import java.util.List;

public class MusicManager {

    private static MusicManager instance;
    private String currentCollectionName = "";
    private MusicPlayer musicPlayer;
    private MusicQueue musicQueue;


    public MusicManager() {

        instance = this;

        musicPlayer = new MusicPlayer();

        musicPlayer.setEndOfMediaCallback((p)->{
            musicPlayer.playMedia(musicQueue.nextSong().filePath);
            return null;
        });

        musicPlayer.setTimeChangedCallback((seconds)->{
            return null;
        });
    }

    public void playMedia(SongPane pane) {

        if (currentCollectionName != pane.collectionName) {
            currentCollectionName = pane.collectionName;

            // find collection
            List<SongData> songDataList = App.songCollectionDict.get(pane.collectionName).getSongList();
            musicQueue = new MusicQueue(songDataList);
            musicQueue.shuffle();
        }

        musicQueue.moveToCurrentIndex(pane.songData);
        musicPlayer.playMedia(pane.songData.filePath);
    }

    public void play() {
        musicPlayer.play();
    }

    public void pause() {
        musicPlayer.pause();
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

    public void setVolume(float percent) {
        musicPlayer.setVolume(percent);
    }

    public static MusicManager getInstance() {
        return instance;
    }
}
