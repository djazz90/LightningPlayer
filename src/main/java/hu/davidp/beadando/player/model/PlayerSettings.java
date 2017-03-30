package hu.davidp.beadando.player.model;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class PlayerSettings {
    private static Properties properties = new Properties();
    private static final File PROPERTIES_FILE = new File("settings.properties");

    private static final String VOLUME_LEVEL_STRING = "volume.level";
    private static final String REPEAT_STRING = "repeat";
    private static final String SHUFFLE_STRING = "shuffle";

    private static final Double VOLUME_LEVEL = 0.7;
    private static final Boolean REPEAT = false;
    private static final Boolean SHUFFLE = false;

    public static void initialize() {
        if (!PROPERTIES_FILE.exists()) {
            properties.setProperty(VOLUME_LEVEL_STRING, VOLUME_LEVEL.toString());
            properties.setProperty(REPEAT_STRING, REPEAT.toString());
            properties.setProperty(SHUFFLE_STRING, SHUFFLE.toString());

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
