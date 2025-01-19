import java.util.ArrayList;
import java.util.List;


public class MusicQueueManager {

    private List<SongData> songQueue;
    private int currentIndex = 0;

    public MusicQueueManager(List<SongData> songQueue) {
        this.songQueue = songQueue;
    }

    public void moveToCurrentIndex(SongData songData) {
//        songQueue.remove(songData);
//        currentIndex++; // So that the song is placed after the current song.
//        songQueue.add(currentIndex, songData);
    }

    private void insert(int index, SongData songData) {
        songQueue.set(index, songData);
    }

    public void nextSong() {
        if (currentIndex + 1 < songQueue.size()) {
            currentIndex++;
            // TODO - add play next song
        }
    }

    public void prevSong() {
        if (currentIndex > 0) {
            currentIndex--;
            // TODO - add play prev song
        }
    }

}
