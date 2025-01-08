public class SongData {

    private String songName;
    private String artistName;
    private String filePath;


    public SongData(String songName, String artistName, String filePath) {
        this.songName = songName;
        this.artistName = artistName;
        this.filePath = filePath;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getFilePath() {
        return filePath;
    }
}
