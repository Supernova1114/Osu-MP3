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
        File testFile = new File("D:\\Program Files\\osu!\\Songs\\357777 NOMA - Brain Power\\NOMA - Brain Power (Jacob) [Exote's EXHAUST].osu");

        Beatmap beatmap = Beatmap.deserializeBeatmap(testFile);
        System.out.println(beatmap);

        Path path = Path.of("test.db");

        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("test.db"))) {
            bufferedWriter.write(beatmap.serializeBeatmap().toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dbPath = "D:\\Projects\\Osu-MP3\\test.db";
        try {

            JSONObject beatmapObj = new JSONObject(new String(Files.readAllBytes(Path.of(dbPath))));
            Beatmap beatmap2 = Beatmap.deserializeBeatmap(beatmapObj);

            System.out.println(beatmap2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
