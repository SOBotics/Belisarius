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
        // Initialise database
        DatabaseUtils.createVandalisedPostTable();
        DatabaseUtils.createBlacklistedWordCaughtTable();
        DatabaseUtils.createOffensiveWordCaughtTable();
        DatabaseUtils.createReasonCaughtTable();
        DatabaseUtils.createFeedbackTable();
        LOGGER.info("Initialised database!");

        init();
    }

    public static void init() throws ApiException {
        PropertyService propertyService = new PropertyService();

        // Login
        String email = propertyService.getProperty("email");
        String password = propertyService.getProperty("password");

        StackExchangeClient client = new StackExchangeClient(email, password);
        LOGGER.info("Created a chatexchange client.");

        // Initialise Higgs
        int higgsDashboardId = Integer.parseInt(propertyService.getProperty("higgsBotId"));
        int roomId = Integer.parseInt(propertyService.getProperty("roomid"));

        String higgsUrl = propertyService.getProperty("higgsUrl");
        String useHiggs = propertyService.getProperty("useHiggs");
        String secret = propertyService.getProperty("higgsSecret");

        if ("true".equals(useHiggs) && higgsDashboardId != 0) {
            HiggsService.initInstance(higgsUrl, secret);
        }
        LOGGER.info("Initialised Higgs!");

        // Initialise Redunda
        String useRedunda = propertyService.getProperty("useRedunda");
        String redundaKey = propertyService.getProperty("redundaKey");
        String version = propertyService.getProperty("version");

        PingService redunda = new PingService(redundaKey, version);
        if ("false".equals(useRedunda)) {
            redunda.setDebugging(true);
        }
        LOGGER.info("Initialised Redunda!");

        String site = propertyService.getProperty("site");

        MonitorService monitorService = new MonitorService(client, roomId, site, redunda);
        monitorService.runMonitor();
        LOGGER.info("Monitor started.");
    }
}
