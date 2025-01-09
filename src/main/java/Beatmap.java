import java.io.*;
import java.security.NoSuchAlgorithmException;

public class Beatmap {

    private String filePath;

    private String MD5Hash;

    private SongData songData;

    public Beatmap(String filePath, String MD5Hash, SongData songData) {
        this.filePath = filePath;
        this.MD5Hash = MD5Hash;
        this.songData = songData;
    }

    public static Beatmap parseBeatmapFile(File beatmapFile) {

        SongData songData = new SongData();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(beatmapFile))) {

            String line;
            while ( (line = bufferedReader.readLine()) != null ) {

                String[] keyValuePair = parseKeyValue(line);

                switch (keyValuePair[0]) {
                    case "AudioFilename":
                        songData.filePath = keyValuePair[1].isEmpty() ? "" : beatmapFile.getParentFile().getPath() + File.separator + keyValuePair[1];
                        break;
                    case "Title":
                        songData.songName = keyValuePair[1];
                        break;
                    case "Artist":
                        songData.artistName = keyValuePair[1];
                        break;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String hash = null;
        try {
            hash = MD5Calculator.GetMD5Hash(beatmapFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // TODO - properly handle failed parsing / incomplete song data.
        return new Beatmap(beatmapFile.getPath(), hash, songData);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMD5Hash() {
        return MD5Hash;
    }

    public SongData getSongData() {
        return songData;
    }

    private static String[] parseKeyValue(String line) {

        String delimiter = ":";
        String[] split = line.split(delimiter);

        String[] result = new String[2];
        result[0] = split[0].trim(); // Key
        result[1] = split.length > 1 ? split[1].trim() : ""; // Value

        return result;
    }

}
