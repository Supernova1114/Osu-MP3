package osu_mp3;

public class SongData {

    public String songName = "";
    public String artistName = "";
    public String filePath = "";

    public SongData() {}

    public SongData(String songName, String artistName, String filePath) {
        this.songName = songName;
        this.artistName = artistName;
        this.filePath = filePath;
    }

    public String toString() {
        return "SongData:\n" +
                "title = " + songName + "\n" +
                "artist = " + artistName + "\n" +
                "filePath = " + filePath + "\n";
    }

}
