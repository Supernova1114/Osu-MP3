package osu_mp3;


public class MusicQueue {

    private SongCollection collection;
    private int currentIndex = 0;

    public MusicQueue(SongCollection collection) {
        this.collection = collection;
    }

    public void moveToCurrentIndex(SongData songData) {

        int songIndex = collection.indexOf(songData);

        if (songIndex != currentIndex) {
            collection.remove(songIndex);
            collection.add(songIndex > currentIndex ? ++currentIndex : currentIndex, songData);
        }
    }

    public void shuffle() {
        collection.shuffle();
    }

    public SongData nextSong() {
        return currentIndex + 1 < queueSize() ? getSong(++currentIndex) : getSong(currentIndex);
    }

    public SongData prevSong() {
        return currentIndex > 0 ? getSong(--currentIndex) : getSong(currentIndex);
    }

    private SongData getSong(int index) {
        return collection.get(index);
    }

    public SongData getSong() {
        return getSong(currentIndex);
    }

    public int queueSize() {
        return collection.size();
    }

}
