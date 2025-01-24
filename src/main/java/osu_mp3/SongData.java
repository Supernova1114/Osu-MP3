package osu_mp3;

public class SongData {

    public String songName = "";
    public String artistName = "";
    public String filePath = "";

    public SongData() {}

    public String toString() {
        return "SongData:\n" +
                "title = " + songName + "\n" +
                "artist = " + artistName + "\n" +
                "filePath = " + filePath + "\n";
    }

}
