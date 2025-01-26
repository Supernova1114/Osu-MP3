package osu_mp3;

import java.util.Collections;
import java.util.List;


public class MusicQueue {

    private List<SongData> songQueue;
    private int currentIndex = 0;

    public MusicQueue(List<SongData> songQueue) {
        this.songQueue = songQueue;
    }

    public void moveToCurrentIndex(SongData songData) {

        int songIndex = songQueue.indexOf(songData);

        if (songIndex != currentIndex) {
            songQueue.remove(songIndex);
            songQueue.add(songIndex > currentIndex ? ++currentIndex : currentIndex, songData);
        }
    }

    public void shuffle() {
        Collections.shuffle(songQueue);
    }

    public SongData nextSong() {
        return currentIndex + 1 < queueSize() ? getSong(++currentIndex) : getSong(currentIndex);
    }

    public SongData prevSong() {
        return currentIndex > 0 ? getSong(--currentIndex) : getSong(currentIndex);
    }

    private SongData getSong(int index) {
        return songQueue.get(index);
    }

    public SongData getSong() {
        return getSong(currentIndex);
    }

    public int queueSize() {
        return songQueue.size();
    }

}
