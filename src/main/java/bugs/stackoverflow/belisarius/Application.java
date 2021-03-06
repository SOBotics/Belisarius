package bugs.stackoverflow.belisarius;

import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import org.sobotics.redunda.PingService;

import io.swagger.client.ApiException;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws ApiException {

        PropertyService propertyService = new PropertyService();
        StackExchangeClient client = new StackExchangeClient(propertyService.getProperty("email"), propertyService.getProperty("password"));
        LOGGER.info("Created a chatexchange client.");

        // Initialise database
        DatabaseUtils.createVandalisedPostTable();
        DatabaseUtils.createBlacklistedWordCaughtTable();
        DatabaseUtils.createOffensiveWordCaughtTable();
        DatabaseUtils.createReasonCaughtTable();
        DatabaseUtils.createFeedbackTable();
        LOGGER.info("Initialised database!");

        try {
            // Initialise Higgs
            int higgsDashboardId = Integer.parseInt(propertyService.getProperty("higgsBotId"));
            int roomId = Integer.parseInt(propertyService.getProperty("roomid"));

            if (propertyService.getProperty("useHiggs").equals("true") && higgsDashboardId != 0) {
                HiggsService.initInstance(propertyService.getProperty("higgsUrl"), propertyService.getProperty("higgsSecret"));
            }
            LOGGER.info("Initialised Higgs!");

            // Initialise Redunda
            PingService redunda = new PingService(propertyService.getProperty("redundaKey"), propertyService.getProperty("version"));
            if (propertyService.getProperty("useRedunda").equals("false")) {
                redunda.setDebugging(true);
            }
            LOGGER.info("Initialised Redunda!");

            MonitorService monitorService = new MonitorService(client, roomId, propertyService.getProperty("site"), redunda);
            monitorService.runMonitor();
            LOGGER.info("Monitor started.");
        } catch (NumberFormatException exception) {
            LOGGER.error("Failed to format values from login.properties file!", exception);
        }

    }

}
