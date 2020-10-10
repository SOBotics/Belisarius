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

    public int getRoomId() {
        String roomId = prop.getProperty("roomid");
        if (roomId == null) {
            roomId = System.getenv("roomid");
        }

        int returnRoomId = 0;
        try {
            returnRoomId = Integer.parseInt(roomId);
        } catch (NumberFormatException ex) {
            LOGGER.error("Error parsing room id.", ex);
        }

        return returnRoomId;
    }

    public String getSite() {
        String site = prop.getProperty("site");
        if (site == null) {
            site = System.getenv("site");
        }
        return site;
    }

    public boolean getUseHiggs() {
        String useHiggs = prop.getProperty("useHiggs");
        if (useHiggs == null) {
            useHiggs = System.getenv("useHiggs");
        }
        return "true".equals(useHiggs);
    }

    public String getHiggsUrl() {
        String url = prop.getProperty("higgsurl");
        if (url == null) {
            url = System.getenv("higgsurl");
        }
        return url;
    }

    public int getHiggsBotId() {
        String botId = prop.getProperty("higgsBotId");
        if (botId == null) {
            botId = System.getenv("higgsBotId");
        }

        int returnBotId = 0;
        try {
            returnBotId = Integer.parseInt(botId);
        } catch (NumberFormatException ex) {
            LOGGER.error("Error parsing Higgs bot id.", ex);
        }

        return returnBotId;
    }

    public String getHiggsSecret() {
        String secretKey = prop.getProperty("higgskey");
        if (secretKey == null) {
            secretKey = System.getenv("higgskey");
        }
        return secretKey;
    }

    public boolean getShouldOutputMessage() {
        String outputMessage = prop.getProperty("outputMessage");
        if (outputMessage == null) {
            outputMessage = System.getenv("outputMessage");
        }
        return "true".equals(outputMessage);
    }
}
