package osu_mp3;

import realm_database.RealmReadTester;

import java.nio.file.Path;

public class Main {

    public static void main(final String[] args) throws InterruptedException {
//        App.launch(App.class, args);



        Path realmFilePath = Path.of("D:\\Program Files\\osu!-lazer\\client.realm");
        Path osuFilesPath = Path.of("D:\\Program Files\\osu!-lazer\\files");

        RealmReadTester realmReadTester = new RealmReadTester(realmFilePath, osuFilesPath);
        realmReadTester.openDatabase();
        realmReadTester.getBeatmapCollections();
        realmReadTester.closeDatabase();


    }

    public static void test() {

    }

}
