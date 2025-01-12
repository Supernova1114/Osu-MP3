import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.json.*;


public class DatabaseManager {

    private static final String DATABASE_FILE_NAME = "Beatmaps.db";
    private static Path DATABASE_FILE_PATH = Path.of(System.getProperty("user.dir"), DATABASE_FILE_NAME);
    private static LinkedHashMap<String, Beatmap> beatmapHashMap = new LinkedHashMap<>(); // LinkedHashMap preserves order of insertion.

    private static List<BeatmapArchive> beatmapArchiveList = new ArrayList<>();

    public static void createDatabase(File songsFolder) {

        try {
            Files.deleteIfExists(DATABASE_FILE_PATH);
            Files.createFile(DATABASE_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse beatmaps from beatmap files

        JSONObject rootObjJSON = new JSONObject();
        JSONArray beatmapArchiveListJSON = new JSONArray();

        for (File archive : songsFolder.listFiles()) {
            BeatmapArchive.deserializeArchive(archive); // TODO - finish up
        }

        // Serialize beatmaps into JSON



        try {
            Files.write(DATABASE_FILE_PATH, rootObjJSON.toString(4).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readDatabase() {
        try {
            JSONObject rootObjJSON = new JSONObject(new String(Files.readAllBytes(DATABASE_FILE_PATH)));
            JSONArray beatmapListJSON = rootObjJSON.getJSONArray("beatmapList");

            for (int i = 0; i < beatmapListJSON.length(); i++) {
                JSONObject beatmapJSON = beatmapListJSON.getJSONObject(i);
                Beatmap beatmap = Beatmap.deserializeBeatmap(beatmapJSON);
                beatmapHashMap.put(beatmap.getMD5Hash(), beatmap);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testPrintDatabase() {
        for (Beatmap beatmap : beatmapHashMap.values()) {
            System.out.println(beatmap);
        }
    }

    public static void syncDatabase() {

        // Iterate through current database and check saved lastModified against file system lastModified.

        for (Beatmap beatmap : beatmapHashMap.values()) {

        }
    }

//
//
//
//
//
//    public static void modifyDatabase(File songFolder)
//    {
//
//        System.out.println("Modifying Database...");
//
//        File[] beatmapFolderList = songFolder.listFiles();
//
//        ArrayList<String> beatmapFolderPathList = new ArrayList<>();
//
//        for (int i=0; i<beatmapFolderList.length; i++){
//            beatmapFolderPathList.add(beatmapFolderList[i].getPath());
//        }
//
//
//        ArrayList<String> tempLineList = new ArrayList<>(beatmapFolderList.length);
//        ArrayList<String> tempPathList = new ArrayList<>(beatmapFolderList.length);
//        ArrayList<String> tempModList = new ArrayList<>(beatmapFolderList.length);
//        //Make temp list with only beatmap database entries
//        //Make temp list with only paths from database
//        //make temp list with only modification times from database
//        for (int i=5; i<lineList.size(); i++){
//            tempLineList.add(lineList.get(i));
//
//            //Split main line into beatmap folder location, beatmap folder lastModified, and String Array of beatmaps with MD5;
//            String[] temp = lineList.get(i).split(" \\| ");
//
//            tempPathList.add(temp[0]);
//            tempModList.add(temp[1]);
//        }
//
//        for (int i=0; i<beatmapFolderList.length; i++){
//
//            //if (database contains beatmapFolder[i] path (as String))
//            if (tempPathList.contains(beatmapFolderList[i].getPath())){
//                //check for modifications
//                long currentModTime = beatmapFolderList[i].lastModified();
//                long prevModTime = Long.parseLong(tempModList.get(tempPathList.indexOf(beatmapFolderList[i].getPath())));
//
//                if (currentModTime != prevModTime){
//                    System.out.println("Found Modified Folder: " + beatmapFolderList[i].getPath());
//
//                    tempPathList.set(i, beatmapFolderList[i].getPath());
//                    tempLineList.set(i, createEntry(beatmapFolderList[i]));
//
//                }
//
//            }else {
//                //add entry to tempLineList
//                tempPathList.add(beatmapFolderList[i].getPath());
//                tempLineList.add(createEntry(beatmapFolderList[i]));
//            }
//
//
//
//        }//for
//
//        //System.out.println("tempLineList: " + tempLineList.size());
//        //System.out.println("tempPathList: " + tempPathList.size());
//        //check for beatmap folders that are listed in database but do not exist in song folder
//        int length = tempLineList.size();
//        for (int i=0; i<length; i++){
//            if (!beatmapFolderPathList.contains(tempPathList.get(i))){
//                tempLineList.remove(i);
//            }
//        }
//
//        //System.out.println("New Beatmap Count: " + tempLineList.size());
//
//        //use BufferedWriter to write a new database file with the modifications
//
//        Files.deleteIfExists(songsDatabaseFilePath);
//        Files.createFile(songsDatabaseFilePath);
//
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(songsDatabaseFilePath.toString()));
//
//        bufferedWriter.write("#DATABASE");
//        bufferedWriter.newLine();
//        bufferedWriter.write(collectionDBLastModified + "");
//        bufferedWriter.newLine();
//        bufferedWriter.write(songsFolderLastModified + "");
//        bufferedWriter.newLine();
//        bufferedWriter.newLine();
//
//        for (int i=0; i<tempLineList.size(); i++){
//
//            bufferedWriter.newLine();
//            bufferedWriter.write(tempLineList.get(i));
//
//        }//for
//
//        bufferedWriter.close();
//
//        //read new File
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(songsDatabaseFilePath.toString()));
//        ArrayList<String> newLineList = new ArrayList<>();
//        String line;
//
//        while ((line = bufferedReader.readLine()) != null){
//            newLineList.add(line);
//        }
//
//        bufferedReader.close();
//
//        getSongsFromDatabase(newLineList, songFolder, false);
//    }





}
