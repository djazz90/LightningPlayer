package hu.davidp.beadando.player.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class PlayerSettings {
    private static Properties properties = new Properties();
    private static final File PROPERTIES_FILE = new File("settings.properties");

    private static final Double DEFAULT_VOLUME_LEVEL = 0.7;

    private static final String VOLUME_LEVEL_PROPERTY_NAME = "volume.level";
    private static final String NAVIGATION_STATE_PROPERTY_NAME = "navigation.state";

    @Getter
    @Setter
    private static Double volumeLevel = DEFAULT_VOLUME_LEVEL;

    @Getter
    @Setter
    private static NavigationState navigationState = NavigationState.NEXT_SONG;

    public enum NavigationState {
        NEXT_SONG, REPEAT_SONG, REPEAT_PLAYLIST, SHUFFLE
    }

    public static void initialize() {
        if (!PROPERTIES_FILE.exists()) {
            properties.setProperty(VOLUME_LEVEL_PROPERTY_NAME, volumeLevel.toString());
            properties.setProperty(NAVIGATION_STATE_PROPERTY_NAME, navigationState.toString());

            try (FileOutputStream outputStream = new FileOutputStream(PROPERTIES_FILE)) {
                properties.store(outputStream, null);
                log.info("The initial property file is created!");
            } catch (IOException e) {
                log.error("The initial property file cannot be created!", e);
            }
        }
    }

    private PlayerSettings() {
        //hidden utility class constructor
    }

}
