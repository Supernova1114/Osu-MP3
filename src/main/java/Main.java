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

        String songsFolder = "D:\\Program Files\\osu!\\Songs";
        DatabaseManager.createDatabase(new File(songsFolder));
        DatabaseManager.readDatabase();
        DatabaseManager.testPrintDatabase();
    }

}
