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

    private static final String VOLUME_LEVEL_PROPERTY_NAME = "volume.level";
    private static final String REPEAT_STRING_PROPERTY_NAME = "repeat";
    private static final String SHUFFLE_STRING_PROPERTY_NAME = "shuffle";

    @Getter
    @Setter
    private static Double volumeLevel = 0.7;

    @Getter
    @Setter
    private static Boolean repeat = false;

    @Getter
    @Setter
    private static Boolean shuffle = false;

    public static void initialize() {
        if (!PROPERTIES_FILE.exists()) {
            properties.setProperty(VOLUME_LEVEL_PROPERTY_NAME, volumeLevel.toString());
            properties.setProperty(REPEAT_STRING_PROPERTY_NAME, repeat.toString());
            properties.setProperty(SHUFFLE_STRING_PROPERTY_NAME, shuffle.toString());

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
