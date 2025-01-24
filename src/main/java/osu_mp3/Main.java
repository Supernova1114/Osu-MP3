package osu_mp3;

import realm_database.RealmDatabaseReader;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws InterruptedException {
//        App.launch(App.class, args);

        System.out.println("Osu! Lazer Test:");

        Path realmFilePath = Path.of("D:\\Program Files\\osu!-lazer\\client.realm");
        Path osuFilesPath = Path.of("D:\\Program Files\\osu!-lazer\\files");

        RealmDatabaseReader realmDatabaseReader = new RealmDatabaseReader(realmFilePath, osuFilesPath);
        List<SongCollection> songCollectionList1 = realmDatabaseReader.getSongCollections();
        realmDatabaseReader.closeDatabase();

        System.out.println("Collection: " + songCollectionList1.get(0).getName());
        System.out.println(songCollectionList1.get(0).get(0).toString());

        System.out.println();
        System.out.println("Osu! Stable Test:");

        Path songsFolderPath = Path.of("D:\\Program Files\\osu!\\Songs");
        Path collectionsFilePath = Path.of("D:\\Program Files\\osu!\\collection.db");
        Path databaseFilePath = Path.of(System.getProperty("user.dir"), "Beatmaps.db");

        DatabaseManager databaseManager = new DatabaseManager(songsFolderPath, collectionsFilePath, databaseFilePath);
        databaseManager.readDatabase();
        databaseManager.syncDatabase();

        List<SongCollection> songCollectionList2 = databaseManager.getSongCollections();

        System.out.println("Collection: " + songCollectionList2.get(0).getName());
        System.out.println(songCollectionList2.get(0).get(0).toString());

    }

}
