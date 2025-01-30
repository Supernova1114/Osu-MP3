package osu_mp3;

import java.util.Collections;
import java.util.List;

public class SongCollection {

    private String name;
    private List<SongData> songList;

    public SongCollection(String name, List<SongData> songList) {
        this.name = name;
        this.songList = songList;
    }

    public int size() {
        return songList.size();
    }

    public SongData get(int index) {
        return songList.get(index);
    }

    public String getName() {
        return name;
    }

    // TODO - implement add, remove, funcs for list, implement songCollection into MusicQueue???
    public List<SongData> getSongList() {
        return songList;
    }

    public int indexOf(SongData songData) {
        return songList.indexOf(songData);
    }

    public void remove(int index) {
        songList.remove(index);
    }
    public void remove(SongData songData) {
        songList.remove(songData);
    }

    public void add(int index, SongData songData) {
        songList.add(index, songData);
    }

    public void shuffle() {
        Collections.shuffle(songList);
    }
}
