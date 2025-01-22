import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class BeatmapArchive {

    private String folderPath;
    private long folderLastModified;
    private List<Beatmap2> beatmapList;


    public BeatmapArchive(String folderPath, long folderLastModified, List<Beatmap2> beatmapList) {
        this.folderPath = folderPath;
        this.folderLastModified = folderLastModified;
        this.beatmapList = beatmapList;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public long getFolderLastModified() {
        return folderLastModified;
    }

    public List<Beatmap2> getBeatmapList() {
        return beatmapList;
    }

    public JSONObject serializeBeatmapArchive() {

        JSONArray beatmapListJSON = new JSONArray();
        JSONObject beatmapArchiveJSON = new JSONObject();

        for (Beatmap2 beatmap : beatmapList) {
            beatmapListJSON.put(beatmap.serializeBeatmap());
        }

        beatmapArchiveJSON.put("beatmapList", beatmapListJSON);
        beatmapArchiveJSON.put("folderPath", folderPath);
        beatmapArchiveJSON.put("folderLastModified", folderLastModified);

        return beatmapArchiveJSON;
    }

    public static BeatmapArchive deserializeArchive(JSONObject archiveObj) {

        List<Beatmap2> beatmapList = new ArrayList<>();

        JSONArray beatmapListJSON = archiveObj.getJSONArray("beatmapList");

        for (int i = 0; i < beatmapListJSON.length(); i++) {
            beatmapList.add(Beatmap2.deserializeBeatmap(beatmapListJSON.getJSONObject(i)));
        }

        return new BeatmapArchive(
                archiveObj.getString("folderPath"),
                archiveObj.getLong("folderLastModified"),
                beatmapList
        );
    }

    public static BeatmapArchive parseBeatmapArchive(File archiveFile) {

        List<Beatmap2> beatmapList = new ArrayList<>();

        File[] beatmapFileList = archiveFile.listFiles(getFilenameFilter(".osu"));
        for (File beatmapFile : beatmapFileList) {
            Beatmap2 beatmap = Beatmap2.parseBeatmapFile(beatmapFile);
            beatmapList.add(beatmap);
        }

        return new BeatmapArchive(
                archiveFile.getPath(),
                archiveFile.lastModified(),
                beatmapList
        );
    }

    private static FilenameFilter getFilenameFilter(String endsWith){
        return (dir, name) -> name.endsWith(endsWith);
    }

    public String toString() {
        return ("BeatmapArchive:\n" +
               "folderPath = " + folderPath + "\n" +
               "folderLastModified = " + folderLastModified + "\n");
    }
}
