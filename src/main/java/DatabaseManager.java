import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.json.*;


public class DatabaseManager {


    private static BeatmapDatabase database;

    public static void createDatabase(File songsFolder) {

        File[] beatmapArchiveList = songsFolder.listFiles();

        JSONObject mainObject = new JSONObject();
        JSONArray beatmapFolderList = new JSONArray();

        for (File beatmapArchive : beatmapArchiveList)
        {
            JSONObject beatmapFolder = new JSONObject();

            beatmapFolder.put("beatmapFolderPath", beatmapArchive.getPath());
            beatmapFolder.put("beatmapFolderLastModified", beatmapArchive.lastModified());

            File[] beatmapList = beatmapArchive.listFiles(getFilenameFilter(".osu"));

            JSONArray osuFileList = new JSONArray();

            for (File beatmap: beatmapList){

                JSONObject osuFile = new JSONObject();

                String hash = MD5Calculator.GetMD5Hash(beatmap);

                osuFile.put("MD5Hash", hash);
                osuFile.put("osuFilePath", beatmap.getPath());

                osuFileList.put(osuFile);
            }

            beatmapFolder.put("osuFileList", osuFileList);
            beatmapFolderList.put(beatmapFolder);
        }

        mainObject.put("collectionDBLastModified", collectionDBLastModified);
        mainObject.put("songsFolderLastModified", songsFolderLastModified);

        mainObject.put("beatmapFolderList", beatmapFolderList);

        Files.createFile(songsDatabaseFilePath);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(songsDatabaseFilePath.toString()));

        bufferedWriter.write(mainObject.toString(4));

        bufferedWriter.close();
    }


    public static void syncDatabase(File songsDatabaseFilePath)
    {
        System.out.println("Reading Database...");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(songsDatabaseFilePath)))
        {
            JSONObject mainObject =  new JSONObject(bufferedReader);

            database.collectionDBLastModified = mainObject.getLong("collectionDBLastModified");
            database.songsFolderLastModified = mainObject.getLong("songsFolderLastModified");

            // WIP

            hashMap.put(tempArray[0], new File(tempArray[1]));//key, difficulty
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }





    public static void modifyDatabase(File songFolder)
    {

        System.out.println("Modifying Database...");

        File[] beatmapFolderList = songFolder.listFiles();

        ArrayList<String> beatmapFolderPathList = new ArrayList<>();

        for (int i=0; i<beatmapFolderList.length; i++){
            beatmapFolderPathList.add(beatmapFolderList[i].getPath());
        }


        ArrayList<String> tempLineList = new ArrayList<>(beatmapFolderList.length);
        ArrayList<String> tempPathList = new ArrayList<>(beatmapFolderList.length);
        ArrayList<String> tempModList = new ArrayList<>(beatmapFolderList.length);
        //Make temp list with only beatmap database entries
        //Make temp list with only paths from database
        //make temp list with only modification times from database
        for (int i=5; i<lineList.size(); i++){
            tempLineList.add(lineList.get(i));

            //Split main line into beatmap folder location, beatmap folder lastModified, and String Array of beatmaps with MD5;
            String[] temp = lineList.get(i).split(" \\| ");

            tempPathList.add(temp[0]);
            tempModList.add(temp[1]);
        }

        for (int i=0; i<beatmapFolderList.length; i++){

            //if (database contains beatmapFolder[i] path (as String))
            if (tempPathList.contains(beatmapFolderList[i].getPath())){
                //check for modifications
                long currentModTime = beatmapFolderList[i].lastModified();
                long prevModTime = Long.parseLong(tempModList.get(tempPathList.indexOf(beatmapFolderList[i].getPath())));

                if (currentModTime != prevModTime){
                    System.out.println("Found Modified Folder: " + beatmapFolderList[i].getPath());

                    tempPathList.set(i, beatmapFolderList[i].getPath());
                    tempLineList.set(i, createEntry(beatmapFolderList[i]));

                }

            }else {
                //add entry to tempLineList
                tempPathList.add(beatmapFolderList[i].getPath());
                tempLineList.add(createEntry(beatmapFolderList[i]));
            }



        }//for

        //System.out.println("tempLineList: " + tempLineList.size());
        //System.out.println("tempPathList: " + tempPathList.size());
        //check for beatmap folders that are listed in database but do not exist in song folder
        int length = tempLineList.size();
        for (int i=0; i<length; i++){
            if (!beatmapFolderPathList.contains(tempPathList.get(i))){
                tempLineList.remove(i);
            }
        }

        //System.out.println("New Beatmap Count: " + tempLineList.size());

        //use BufferedWriter to write a new database file with the modifications

        Files.deleteIfExists(songsDatabaseFilePath);
        Files.createFile(songsDatabaseFilePath);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(songsDatabaseFilePath.toString()));

        bufferedWriter.write("#DATABASE");
        bufferedWriter.newLine();
        bufferedWriter.write(collectionDBLastModified + "");
        bufferedWriter.newLine();
        bufferedWriter.write(songsFolderLastModified + "");
        bufferedWriter.newLine();
        bufferedWriter.newLine();

        for (int i=0; i<tempLineList.size(); i++){

            bufferedWriter.newLine();
            bufferedWriter.write(tempLineList.get(i));

        }//for

        bufferedWriter.close();

        //read new File
        BufferedReader bufferedReader = new BufferedReader(new FileReader(songsDatabaseFilePath.toString()));
        ArrayList<String> newLineList = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null){
            newLineList.add(line);
        }

        bufferedReader.close();

        getSongsFromDatabase(newLineList, songFolder, false);
    }



}
