package bugs.stackoverflow.belisarius;

import java.util.List;

import bugs.stackoverflow.belisarius.models.Chatroom;
import bugs.stackoverflow.belisarius.models.Higgs;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.LoginUtils;

import org.sobotics.chatexchange.chat.StackExchangeClient;

import io.swagger.client.ApiException;

public class Application {

    public static void main(String[] args) throws ApiException {

        StackExchangeClient client = LoginUtils.getClient();

        DatabaseUtils.createRoomTable();
        DatabaseUtils.createVandalisedPostTable();
        DatabaseUtils.createReasonTable();
        DatabaseUtils.createBlacklistedWordTable();
        DatabaseUtils.createBlacklistedWordCaughtTable();
        DatabaseUtils.createOffensiveWordTable();
        DatabaseUtils.createOffensiveWordCaughtTable();
        DatabaseUtils.createReasonCaughtTable();
        DatabaseUtils.createFeedbackTable();
        DatabaseUtils.createHiggsTable();

        PropertyService propertyService = new PropertyService();
        // Get Higgs information for Higgs (dashboard id 3)
        Higgs higgs = DatabaseUtils.getHiggs(3);
        if (higgs != null && propertyService.getUseHiggs()) {
            HiggsService.initInstance(higgs.getUrl(), higgs.getKey());
        }

        List<Chatroom> rooms = DatabaseUtils.getRooms();
        MonitorService monitorService = new MonitorService(client, rooms);
        monitorService.startMonitor();
        monitorService.runMonitor();
    }

}
