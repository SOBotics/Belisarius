package bugs.stackoverflow.belisarius.services;

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
        try (FileInputStream propertiesFis = new FileInputStream(FileUtils.LOGIN_PROPERTIES_FILE)) {
            prop = new Properties();
            prop.load(propertiesFis);
        } catch (IOException exception) {
            LOGGER.info("IOException occurred while loading properties from " + FileUtils.LOGIN_PROPERTIES_FILE, exception);
        }
    }

    public String getProperty(String name) {
        String property = prop.getProperty(name);
        if (property == null) {
            property = System.getenv(name);
        }

        return property;
    }
}
