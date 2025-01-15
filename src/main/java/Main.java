import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(final String[] args) throws InterruptedException {
        //App.launch(App.class, args);


        test();
    }

    public static void test() {

        final String DATABASE_FILE_NAME = "Beatmaps.db";
        final Path DATABASE_FILE_PATH = Path.of(System.getProperty("user.dir"), DATABASE_FILE_NAME);
        final Path SONGS_FOLDER_PATH = Path.of("D:\\Program Files\\osu!\\Songs");
        final Path COLLECTIONS_FILE_PATH = Path.of("D:\\Program Files\\osu!\\collection.db");

//        DatabaseManager databaseManager = new DatabaseManager(SONGS_FOLDER_PATH, DATABASE_FILE_PATH);
//
//        //databaseManager.createDatabase();
//        databaseManager.readDatabase();
//        databaseManager.syncDatabase();
//
//        System.out.println("Database Size: " + databaseManager.getBeatmapArchiveCount());

        BeatmapCollectionDecoder beatmapCollectionDecoder = new BeatmapCollectionDecoder(COLLECTIONS_FILE_PATH);
        beatmapCollectionDecoder.readCollections();

        for (BeatmapCollection collection : beatmapCollectionDecoder.getBeatmapCollectionList()) {
            System.out.println("Collection Name: " + collection.getName());
            System.out.println("Collection Size: " + collection.size());
            System.out.println();
        }

//        BeatmapArchive archive = databaseManager.getBeatmapArchiveList().get(0);
//        System.out.println(archive);
//        System.out.println();
//        System.out.println(archive.getBeatmapList().get(0));
    }

}
