import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.json.*;


public class DatabaseManager {

    private List<BeatmapArchive> beatmapArchiveList = new ArrayList<>();
    private long songsFolderLastModified;
    private Path databaseFilePath;
    private Path songsFolderPath;


    public DatabaseManager(Path songsFolderPath, Path databaseFilePath) {
        this.songsFolderPath = songsFolderPath;
        this.databaseFilePath = databaseFilePath;
    }

    public void createDatabase() {
        try {
            Files.deleteIfExists(databaseFilePath);
            Files.createFile(databaseFilePath);

            // Parse beatmaps from beatmap files

            JSONObject rootObjJSON = new JSONObject();
            JSONArray beatmapArchiveListJSON = new JSONArray();

            File songsFolder = songsFolderPath.toFile();

            for (File archive : songsFolder.listFiles()) {
                beatmapArchiveListJSON.put(BeatmapArchive.parseBeatmapArchive(archive).serializeBeatmapArchive());
            }

            // Serialize beatmaps into JSON

            rootObjJSON.put("beatmapArchiveList", beatmapArchiveListJSON);
            rootObjJSON.put("songsFolderLastModified", songsFolder.lastModified());

            Files.write(databaseFilePath, rootObjJSON.toString(4).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readDatabase() {
        try {
            JSONObject rootObjJSON = new JSONObject(new String(Files.readAllBytes(databaseFilePath)));

            songsFolderLastModified = rootObjJSON.getLong("songsFolderLastModified");

            JSONArray beatmapArchiveListJSON = rootObjJSON.getJSONArray("beatmapArchiveList");
            for (int i = 0; i < beatmapArchiveListJSON.length(); i++) {
                beatmapArchiveList.add(BeatmapArchive.deserializeArchive(beatmapArchiveListJSON.getJSONObject(i)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BeatmapArchive> getBeatmapArchiveList() {
        return beatmapArchiveList;
    }

    public void syncDatabase() {

        // Remove database BeatmapArchive entries if they do not exist in the filesystem.

        File[] beatmapArchiveListFS = songsFolderPath.toFile().listFiles();

        // TODO - WIP
        for (BeatmapArchive beatmapArchive : beatmapArchiveList) {

        }
    }

}
