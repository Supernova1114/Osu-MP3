package osu_mp3;

import java.util.ArrayList;
import java.util.List;

public class BeatmapCollection {

    private String name;

    // List of MD5 hashes of beatmap files.
    private List<String> hashList = new ArrayList<>();
    // Note: Osu! leaves deleted beatmaps within the collections file.
    // Therefore, the beatmap collection hashes must be validated against currently available beatmaps.

    public BeatmapCollection(String name) {
        this.name = name;
    }

    public void addBeatmapHash(String MD5Hash) {
        hashList.add(MD5Hash);
    }

    public String getName() {
        return name;
    }

    public int size() {
        return hashList.size();
    }

    public List<String> getHashList() {
        return hashList;
    }
}
