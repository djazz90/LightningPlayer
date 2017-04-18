package hu.davidp.beadando.player.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class PlayerSettings {
    private static Properties properties = new Properties();
    private static final File PROPERTIES_FILE = new File("settings.properties");

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
        if (!PROPERTIES_FILE.exists()) {
            try {
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
        try (FileInputStream inputStream = new FileInputStream(PROPERTIES_FILE)) {
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

        // az összes beállítás mentése, majd logolás annak kimenetele szerint
        try (FileOutputStream outputStream = new FileOutputStream(PROPERTIES_FILE)) {
            properties.store(outputStream, null);

        }
    }

    private PlayerSettings() {
        //hidden utility class constructor
    }

}
