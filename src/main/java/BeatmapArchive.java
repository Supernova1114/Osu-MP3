import java.util.List;

public class BeatmapArchive {

    private String folderPath;
    private long folderLastModified;
    private List<Beatmap> beatmapFileList;


    public BeatmapArchive(String folderPath, long folderLastModified, List<Beatmap> beatmapFileList) {
        this.folderPath = folderPath;
        this.folderLastModified = folderLastModified;
        this.beatmapFileList = beatmapFileList;
    }

    public String getBeatmapFolderPath() {
        return folderPath;
    }

    public long getBeatmapFolderLastModified() {
        return folderLastModified;
    }

    public List<Beatmap> getBeatmapFileList() {
        return beatmapFileList;
    }
}
