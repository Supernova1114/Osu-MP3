import java.io.*;
import java.nio.file.Files;

public class BeatmapFile {

    private String filePath;

    private String MD5Hash;

    private SongData songData;

    public BeatmapFile(String filePath, String MD5Hash, SongData songData) {
        this.filePath = filePath;
        this.MD5Hash = MD5Hash;
        this.songData = songData;
    }

    public static BeatmapFile parseBeatmapFile(File beatmapFile) {

        try (BufferedReader beatmapFileReader = new BufferedReader(new FileReader(beatmapFile))) {

            // TODO - 1/7/2025 - rewrite / fix-up beatmap parser below

            String line;
            while ((line = beatmapFileReader.readLine()) != null) {

                if (line.contains("AudioFilename")) {
                    //System.out.println(line);
                    musicFile = new File(file.getParentFile().getPath() + File.separator + line.substring(15));//15 is length of [AudioFilename: ]
                }
                if (line.contains("[Events]")){
                    beatmapFileReader.readLine();
                    String temp = beatmapFileReader.readLine();

                    String [] tempSplit = temp.split(",");

                    imageFile = new File(file.getParentFile().getPath() + File.separator + tempSplit[2].replace("\"", ""));

                    //System.out.println(imageFile.getName());




                }
            }

            beatmapFileReader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMD5Hash() {
        return MD5Hash;
    }


}
