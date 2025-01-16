import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SettingsManager {

    private Properties properties = new Properties();;
    private Path settingsFilePath;

    public SettingsManager(Path settingsFilePath) {
        this.settingsFilePath = settingsFilePath;
    }

    public void loadSettings() {
        try (FileInputStream in = new FileInputStream(settingsFilePath.toFile())) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        try (FileOutputStream out = new FileOutputStream(settingsFilePath.toFile())) {
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean settingsFileExists() {
        return Files.exists(settingsFilePath);
    }

    public void setDefaultProperties() {
        properties.setProperty("osuFolderLocation", "");
        properties.setProperty("showArtists", "true");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

}
