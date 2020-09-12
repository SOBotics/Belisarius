package bugs.stackoverflow.belisarius.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import bugs.stackoverflow.belisarius.utils.PathUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyService.class);

    private Properties prop;

    public PropertyService() {
        try (FileInputStream propertiesFis = new FileInputStream(PathUtils.LOGIN_PROPERTIES_FILE)) {
            prop = new Properties();
            prop.load(propertiesFis);
        } catch (IOException exception) {
            LOGGER.info("IOException occurred while loading properties from " + PathUtils.LOGIN_PROPERTIES_FILE, exception);
        }
    }

    public String getApiKey() {
        String apiKey = prop.getProperty("apikey");
        if (apiKey == null) {
            apiKey = System.getenv("apikey");
        }

        return apiKey;
    }

    public String getEmail() {
        String email = prop.getProperty("email");
        if (email == null) {
            email = System.getenv("email");
        }
        return email;
    }

    public String getPassword() {
        String password = prop.getProperty("password");
        if (password == null) {
            password = System.getenv("password");
        }
        return password;
    }

    /* public int getRoomId() {
        String roomId = prop.getProperty("roomid");
        if (roomId == null) {
            roomId = System.getenv("roomId");
        }

        int returnRoomId = 0;
        try {
            returnRoomId = Integer.parseInt(roomId);
        } catch (NumberFormatException ex) {
            System.out.println("Error parsing roomId: " + ex.getMessage());
        }

        return returnRoomId;
    }

    public String getSite() {
        String site = prop.getProperty("site");
        if (site == null) {
            site = System.getenv("site");
        }
        return site;
    } */

    public boolean getUseHiggs() {
        String useHiggs = prop.getProperty("useHiggs");
        if (useHiggs == null) {
            useHiggs = System.getenv("useHiggs");
        }
        return "true".equals(useHiggs);
    }
}
