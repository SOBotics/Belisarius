package bugs.stackoverflow.belisarius.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import bugs.stackoverflow.belisarius.utils.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyService.class);

    private Properties prop;

    public PropertyService() {
        File propertiesFile = new File(FileUtils.LOGIN_PROPERTIES_FILE);

        // for testing
        if (propertiesFile.isFile()) {
            loadProperties(FileUtils.LOGIN_PROPERTIES_FILE);
        } else {
            loadProperties(FileUtils.LOGIN_PROPERTIES_EXAMPLE_FILE);
        }
    }

    // added for testing
    public PropertyService(String filename) {
        loadProperties(filename);
    }

    public String getProperty(String name) {
        String property = prop.getProperty(name);

        if (property == null) {
            property = System.getenv(name);
        }

        return property;
    }

    private void loadProperties(String filename) {
        try (FileInputStream propertiesFis = new FileInputStream(filename)) {
            prop = new Properties();
            prop.load(propertiesFis);
        } catch (IOException exception) {
            LOGGER.info(
                "IOException occurred while loading properties from " + filename,
                exception
            );
        }
    }
}
