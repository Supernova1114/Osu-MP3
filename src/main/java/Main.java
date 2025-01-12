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
        Path DATABASE_FILE_PATH = Path.of(System.getProperty("user.dir"), DATABASE_FILE_NAME);
        Path songsFolderPath = Path.of("D:\\Program Files\\osu!\\Songs");

        DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, DATABASE_FILE_PATH);

        String songsFolder = "D:\\Program Files\\osu!\\Songs";
        databaseManager.createDatabase(new File(songsFolder));
        databaseManager.readDatabase();

        BeatmapArchive archive = databaseManager.getBeatmapArchiveList().get(0);
        System.out.println(archive);
        System.out.println();
        System.out.println(archive.getBeatmapList().get(0));
    }

}
