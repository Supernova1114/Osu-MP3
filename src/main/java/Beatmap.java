import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class Beatmap {

    private String filePath;

    private long fileLastModified;

    private String MD5Hash;

    private SongData songData;


    public Beatmap(String filePath, long fileLastModified, String MD5Hash, SongData songData) {
        this.filePath = filePath;
        this.fileLastModified = fileLastModified;
        this.MD5Hash = MD5Hash;
        this.songData = songData;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileLastModified() {
        return fileLastModified;
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

    public JSONObject serializeBeatmap() {
        JSONObject beatmapObj = new JSONObject();
        JSONObject songDataObj = new JSONObject();

        songDataObj.put("songName", songData.songName);
        songDataObj.put("artistName", songData.artistName);
        songDataObj.put("filePath", songData.filePath);

        beatmapObj.put("filePath", filePath);
        beatmapObj.put("fileLastModified", fileLastModified);
        beatmapObj.put("hash", MD5Hash);
        beatmapObj.put("songData", songDataObj);

        return beatmapObj;
    }

    public static Beatmap deserializeBeatmap(JSONObject beatmapObj) {

        JSONObject songDataObj = beatmapObj.getJSONObject("songData");

        SongData songData = new SongData();
        songData.filePath = songDataObj.getString("filePath");
        songData.songName = songDataObj.getString("songName");
        songData.artistName = songDataObj.getString("artistName");

        Beatmap beatmap = new Beatmap(
                beatmapObj.getString("filePath"),
                beatmapObj.getLong("fileLastModified"),
                beatmapObj.getString("hash"),
                songData
        );

        return beatmap;
    }

    public static Beatmap deserializeBeatmap(File beatmapFile) {

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

        // TODO - properly handle failed parsing / incomplete song data???
        return new Beatmap(beatmapFile.getPath(), beatmapFile.lastModified(), hash, songData);
    }

    @Override
    public String toString() {
        return "Beatmap:\n" +
                "filePath = " + filePath + "\n" +
                "fileLastModified = " + fileLastModified + "\n" +
                "MD5Hash = " + MD5Hash + "\n" +
                "songName = " + songData.songName + "\n" +
                "artistName = " + songData.artistName + "\n" +
                "songFilePath = " + songData.filePath + "\n";

    }
}
