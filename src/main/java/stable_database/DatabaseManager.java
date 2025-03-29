package stable_database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.json.*;
import osu_mp3.SongCollection;
import osu_mp3.SongData;


public class DatabaseManager {

    private List<BeatmapArchive> beatmapArchiveList = new ArrayList<>();
    private long songsFolderLastModified;
    private Path databaseFilePath;
    private Path songsFolderPath;
    private Path collectionsFilePath;


    public DatabaseManager(Path songsFolderPath, Path collectionsFilePath, Path databaseFilePath) {
        this.songsFolderPath = songsFolderPath;
        this.collectionsFilePath = collectionsFilePath;
        this.databaseFilePath = databaseFilePath;
    }

    public void createDatabase() {
        System.out.println("Creating Database.");

        List<BeatmapArchive> archiveList = new ArrayList<>();

        for (File archive : songsFolderPath.toFile().listFiles()) {
            archiveList.add(BeatmapArchive.parseBeatmapArchive(archive));
        }

        writeToDatabase(archiveList);
    }

    private void writeToDatabase(List<BeatmapArchive> archiveList) {
        try {
            System.out.println("Writing to Database.");

            Files.deleteIfExists(databaseFilePath);
            Files.createFile(databaseFilePath);

            JSONObject rootObjJSON = new JSONObject();
            JSONArray beatmapArchiveListJSON = new JSONArray();

            // Serialize beatmaps into JSON

            for (BeatmapArchive archive : archiveList) {
                beatmapArchiveListJSON.put(archive.serializeBeatmapArchive());
            }

            rootObjJSON.put("beatmapArchiveList", beatmapArchiveListJSON);
            rootObjJSON.put("songsFolderLastModified", songsFolderPath.toFile().lastModified());

            Files.write(databaseFilePath, rootObjJSON.toString(4).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readDatabase() {
        System.out.println("Reading Database.");

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

    public void syncDatabase() {
        System.out.println("Syncing Database.");

        // Used to figure out which archives need to be added to the database.
        List<String> archivesToAdd = new ArrayList<>();

        // Initialize archivesToAdd to contain all paths from filesystem
        File[] archiveListFS = songsFolderPath.toFile().listFiles();

        for (File file : archiveListFS) {
            archivesToAdd.add(file.getPath());
        }

        boolean shouldModifyDatabase = false;

        for (int i = 0; i < beatmapArchiveList.size(); i++) {

            BeatmapArchive dbArchive = beatmapArchiveList.get(i);

            // Remove database entry if archive does not exist in the filesystem.
            if (Files.exists(Path.of(dbArchive.getFolderPath())) == false) {

                shouldModifyDatabase = true;
                beatmapArchiveList.remove(i);
                i--; // Update position as we have removed item.

                System.out.println("Database Remove: " + dbArchive.getFolderPath());
                continue;
            }

            File fsArchive = new File(dbArchive.getFolderPath());

            // TODO - Check for modifications of beatmap files as well??
            // Update database if archive has been modified in filesystem.
            if (dbArchive.getFolderLastModified() != fsArchive.lastModified()) {

                shouldModifyDatabase = true;
                BeatmapArchive newArchive = BeatmapArchive.parseBeatmapArchive(fsArchive);
                beatmapArchiveList.set(i, newArchive);

                System.out.println("Database Update: " + newArchive.getFolderPath());
            }

            // Remove entry from archivesToAdd as we know entry already exists within the database.
            archivesToAdd.remove(beatmapArchiveList.get(i).getFolderPath());
        }

        // Add new beatmap archives to database.
        for (String newArchivePath : archivesToAdd) {

            shouldModifyDatabase = true;
            beatmapArchiveList.add(BeatmapArchive.parseBeatmapArchive(new File(newArchivePath)));

            System.out.println("Database Add: " + newArchivePath);
        }

        // Only write to database file if a modification has been done.
        if (shouldModifyDatabase) {
            writeToDatabase(beatmapArchiveList);
        }
    }

    public List<BeatmapArchive> getBeatmapArchiveList() {
        return beatmapArchiveList;
    }

    public int getBeatmapArchiveCount() {
        return beatmapArchiveList.size();
    }

    /**
     * Creates hashmap based on current list of beatmaps in deserialized database.
     * Key = MD5 Hash, Value = Beatmap object.
     * HashMap is outdated when modifications are made to the database. HashMap will need to be
     * rebuilt in that case.
     * @return
     */
    private HashMap<String, Beatmap> getBeatmapHashDict() {

        // Key = MD5 hash, Value = Beatmap object.
        HashMap<String, Beatmap> hashMap = new HashMap<>();

        for (BeatmapArchive archive : beatmapArchiveList) {
            for (Beatmap beatmap : archive.getBeatmapList()) {
                hashMap.put(beatmap.getMD5Hash(), beatmap);
            }
        }

        return hashMap;
    }

    public List<SongCollection> getSongCollections() {

        HashMap<String, Beatmap> beatmapHashDict = getBeatmapHashDict();

        BeatmapCollectionDecoder beatmapCollectionDecoder = new BeatmapCollectionDecoder(collectionsFilePath);
        List<BeatmapCollection> beatmapCollectionList = beatmapCollectionDecoder.readCollections();

        List<SongCollection> songCollectionList = new ArrayList<>();

        for (BeatmapCollection beatmapCollection : beatmapCollectionList) {

            List<SongData> songDataList = new ArrayList<>();

            for (String hash : beatmapCollection.getHashList()) {
                // Check to see if hash actually exists within list of available beatmaps.
                // This is done as it is possible for Osu! to leave hash references of deleted beatmaps within
                // the collections.db file.
                if (beatmapHashDict.containsKey(hash)) {
                    songDataList.add(beatmapHashDict.get(hash).getSongData());
                }
            }

            songCollectionList.add(new SongCollection(beatmapCollection.getName(), songDataList));
        }

        return songCollectionList;
    }



}
