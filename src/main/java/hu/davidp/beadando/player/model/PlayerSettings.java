package hu.davidp.beadando.player.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class PlayerSettings {
    private static Properties properties = new Properties();
    private static File propertiesFile;

    @Getter
    private static File autoSavedPlaylistFile;

    private static final Double DEFAULT_VOLUME_LEVEL = 0.7;
    private static final String DEFAULT_API_KEY = "none";

    private static final String VOLUME_LEVEL_PROPERTY_NAME = "volume.level";
    private static final String NAVIGATION_STATE_PROPERTY_NAME = "navigation.state";
    private static final String API_KEY_PROPERTY_NAME = "api.key";
    private static final String PLAYLIST_AUTO_SAVE_PROPERTY_NAME = "playlist.backup";

    @Getter
    @Setter
    private static Double volumeLevel = DEFAULT_VOLUME_LEVEL;

    @Getter
    @Setter
    private static NavigationState navigationState = NavigationState.NEXT_SONG;

    @Getter
    @Setter
    private static String apiKey = DEFAULT_API_KEY;

    @Getter
    @Setter
    private static PlaylistAutoSave playlistAutoSave = PlaylistAutoSave.PLAYLIST_AUTO_SAVE_AND_LOAD;

    public enum NavigationState {
        NEXT_SONG, REPEAT_SONG, REPEAT_PLAYLIST, SHUFFLE
    }

    public enum PlaylistAutoSave {
        PLAYLIST_AUTO_SAVE_AND_LOAD, PLAYLIST_AUTO_SAVE_AND_LOAD_OFF
    }

    public static void initialize() {
        propertiesFile = FileUtils.getFile(createPlayerFolderInUserHome().getAbsolutePath(), "settings.properties");
        autoSavedPlaylistFile = FileUtils.getFile(createPlayerFolderInUserHome().getAbsolutePath(), "backup-playlist.xspf");
        log.info("propertiesFile full path: {}", propertiesFile.getAbsolutePath());
        log.info("autoSavedPlaylistFile full path: {}", autoSavedPlaylistFile.getAbsolutePath());

        if (!propertiesFile.exists()) {
            try {
                // az összes beállítás mentése, majd logolás annak kimenetele szerint
                save();
                log.info("The initial property file is created!");
            } catch (IOException e) {
                log.info("The initial property file cannot be created!", e);
            }
        } else {
            try {
                load();
                log.info("The property file is found and successfully loaded!");
            } catch (IOException e) {
                log.error("The property file cannot be loaded!", e);
            }
        }
    }

    public static void load() throws IOException {
        log.info("Loading settings...");
        try (FileInputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
            // az összes beállítás betöltése és logolása
            volumeLevel = Double.valueOf(properties.getProperty(VOLUME_LEVEL_PROPERTY_NAME));
            log.info("volume level: {}", volumeLevel);

            navigationState = NavigationState.valueOf(properties.getProperty(NAVIGATION_STATE_PROPERTY_NAME));
            log.info("navigation state: {}", navigationState.name());

            apiKey = properties.getProperty(API_KEY_PROPERTY_NAME);
            log.info("api key: {}", apiKey);

            playlistAutoSave = PlaylistAutoSave.valueOf(properties.getProperty(PLAYLIST_AUTO_SAVE_PROPERTY_NAME));
            log.info("playlist auto save: {}", playlistAutoSave.name());
        }


    }

    public static void save() throws IOException {
        log.info("Saving settings...");
        properties.setProperty(VOLUME_LEVEL_PROPERTY_NAME, volumeLevel.toString());
        properties.setProperty(NAVIGATION_STATE_PROPERTY_NAME, navigationState.toString());
        properties.setProperty(API_KEY_PROPERTY_NAME, apiKey);
        properties.setProperty(PLAYLIST_AUTO_SAVE_PROPERTY_NAME, playlistAutoSave.toString());

        try (FileOutputStream outputStream = new FileOutputStream(propertiesFile)) {
            properties.store(outputStream, null);

        }
    }

    private static File createPlayerFolderInUserHome() {
        File file = FileUtils.getFile(FileUtils.getUserDirectory(), "LightningPlayer");
        //if it is creatable or already exists, it should create the settings file there.
        //the folder needs to be writable too.
        if ((file.mkdir() || file.exists()) && file.canWrite()) {
            return FileUtils.getFile(file.getAbsolutePath());
        } else {
            //as default setting: create it to the main program folder (that should be always writable as a safety mesure)
            return new File("");
        }
    }

    private PlayerSettings() {
        throw new AssertionError("Instance creation is not allowed for PlayerSettings class");
    }

}
