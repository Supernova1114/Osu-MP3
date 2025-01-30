package stable_database;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Reference: https://github.com/ppy/osu/blob/master/osu.Game/Database/LegacyCollectionImporter.cs

public class BeatmapCollectionDecoder {

    private Path collectionFilePath;

    public BeatmapCollectionDecoder(Path collectionFilePath) {
        this.collectionFilePath = collectionFilePath;
    }

    public List<BeatmapCollection> readCollections() {

        List<BeatmapCollection> beatmapCollectionList = new ArrayList<>();

        try (BinaryReader binaryReader = new BinaryReader(new FileInputStream(collectionFilePath.toFile()))) {

            binaryReader.readInt32(); // Version

            int collectionCount = binaryReader.readInt32();

            for (int i = 0; i < collectionCount; i++) {

                String collectionName = binaryReader.readString();

                BeatmapCollection beatmapCollection = new BeatmapCollection(collectionName);

                int beatmapCount = binaryReader.readInt32();

                for (int j = 0; j < beatmapCount; j++) {
                    String MD5Hash = binaryReader.readString();
                    beatmapCollection.addBeatmapHash(MD5Hash);
                }

                beatmapCollectionList.add(beatmapCollection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return beatmapCollectionList;
    }

}
