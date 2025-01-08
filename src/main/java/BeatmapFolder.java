import java.util.List;

public class BeatmapFolder {

    private String folderPath;
    private long folderLastModified;
    private List<BeatmapFile> beatmapFileList;


    public BeatmapFolder(String folderPath, long folderLastModified, List<BeatmapFile> beatmapFileList) {
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

    public List<BeatmapFile> getBeatmapFileList() {
        return beatmapFileList;
    }
}
