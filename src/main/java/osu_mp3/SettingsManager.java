package osu_mp3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SettingsManager {

    public enum Settings {
        OSU_STABLE_FOLDER_PATH("osuStableFolderPath", ""),
        OSU_LAZER_FOLDER_PATH("osuLazerFolderPath", ""),
        OSU_DATABASE_MODE("osuDatabaseMode", ""),
        SHOW_ARTISTS("showArtists", "true"),
        LAST_COLLECTION_SHOWN("lastCollectionShown", "");

        private final String key;
        private final String defaultValue;

        Settings(final String key, final String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String toString() {
            return key;
        }
    }

    private Path settingsFilePath;
    private Properties properties = new Properties();

    public SettingsManager(Path settingsFilePath) {
        this.settingsFilePath = settingsFilePath;
    }

    public void loadSettings() {
        try (FileInputStream in = new FileInputStream(settingsFilePath.toFile())) {
            properties.load(in);
        } catch (IOException e) {
            System.out.println("Unable to load settings!");
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        try (FileOutputStream out = new FileOutputStream(settingsFilePath.toFile())) {
            properties.store(out, null);
        } catch (IOException e) {
            System.out.println("Unable to save settings!");
            e.printStackTrace();
        }
    }

    public boolean settingsFileExists() {
        return Files.exists(settingsFilePath);
    }

    public void setDefaultProperties() {
        for (Settings setting : Settings.values()) {
            properties.setProperty(setting.key, setting.defaultValue);
        }
    }

    public String getProperty(Settings setting) {
        return properties.getProperty(setting.key);
    }

    public void setProperty(Settings setting, String value) {
        properties.setProperty(setting.key, value);
    }

}
