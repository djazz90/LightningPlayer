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

    private static final Double DEFAULT_VOLUME_LEVEL = 0.7;
    private static final String DEFAULT_API_KEY = "none";

    private static final String VOLUME_LEVEL_PROPERTY_NAME = "volume.level";
    private static final String NAVIGATION_STATE_PROPERTY_NAME = "navigation.state";
    private static final String API_KEY_PROPERTY_NAME = "api.key";

    @Getter
    @Setter
    private static Double volumeLevel = DEFAULT_VOLUME_LEVEL;

    @Getter
    @Setter
    private static NavigationState navigationState = NavigationState.NEXT_SONG;

    @Getter
    @Setter
    private static String apiKey = DEFAULT_API_KEY;

    public enum NavigationState {
        NEXT_SONG, REPEAT_SONG, REPEAT_PLAYLIST, SHUFFLE
    }

    public static void initialize() {
        File file = FileUtils.getFile(FileUtils.getUserDirectory(), "LightningPlayer");
        //if it is creatable or already exists, it should create the settings file there.
        //the folder needs to be writable too.
        if ((file.mkdir() || file.exists()) && file.canWrite()) {
            propertiesFile = FileUtils.getFile(file.getAbsolutePath(), "settings.properties");
        } else {
            //as default setting: create it to the main program folder (that should be always writable as a safety mesure)
            propertiesFile = new File("settings.properties");
        }
        log.info("propertiesFile full path: {}", propertiesFile.getAbsolutePath());
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
        }

    }

    public static void save() throws IOException {
        log.info("Saving settings...");
        properties.setProperty(VOLUME_LEVEL_PROPERTY_NAME, volumeLevel.toString());
        properties.setProperty(NAVIGATION_STATE_PROPERTY_NAME, navigationState.toString());
        properties.setProperty(API_KEY_PROPERTY_NAME, apiKey);

        try (FileOutputStream outputStream = new FileOutputStream(propertiesFile)) {
            properties.store(outputStream, null);

        }
    }

    private PlayerSettings() {
        throw new AssertionError("Instance creation is not allowed for PlayerSettings class");
    }

}
