package bugs.stackoverflow.belisarius;

import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

import org.sobotics.chatexchange.chat.StackExchangeClient;

import io.swagger.client.ApiException;

public class Application {

    public static void main(String[] args) throws ApiException {

        PropertyService propertyService = new PropertyService();
        StackExchangeClient client = new StackExchangeClient(propertyService.getEmail(), propertyService.getPassword());

        DatabaseUtils.createVandalisedPostTable();
        DatabaseUtils.createReasonTable();
        DatabaseUtils.createBlacklistedWordTable();
        DatabaseUtils.createBlacklistedWordCaughtTable();
        DatabaseUtils.createOffensiveWordTable();
        DatabaseUtils.createOffensiveWordCaughtTable();
        DatabaseUtils.createReasonCaughtTable();
        DatabaseUtils.createFeedbackTable();

        int higgsDashboardId = propertyService.getHiggsBotId();
        if (propertyService.getUseHiggs() && higgsDashboardId != 0) {
            HiggsService.initInstance(propertyService.getHiggsUrl(), propertyService.getHiggsSecret());
        }

        MonitorService monitorService = new MonitorService(client, propertyService.getRoomId(), propertyService.getSite());
        monitorService.runMonitor();
    }

}
