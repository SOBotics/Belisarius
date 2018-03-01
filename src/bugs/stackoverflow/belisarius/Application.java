package bugs.stackoverflow.belisarius;

import java.util.*;

import bugs.stackoverflow.belisarius.rooms.*;
import bugs.stackoverflow.belisarius.services.*;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.LoginUtils;
import fr.tunaki.stackoverflow.chat.*;

public class Application {
	
	public static void main(String[] args) {
		
		StackExchangeClient client = LoginUtils.getClient();
		
		DatabaseUtils.createVandalisedPostTable();
		DatabaseUtils.createReasonTable();
		DatabaseUtils.createBlacklistedWordTable();
		DatabaseUtils.createBlacklistedWordCaughtTable();
		DatabaseUtils.createOffensiveWordTable();
		DatabaseUtils.createOffensiveWordCaughtTable();
		DatabaseUtils.createReasonCaughtTable();
		DatabaseUtils.createFeedbackTable();
		
		List<Chatroom> rooms = new ArrayList<>();
		rooms.add(new SOBotics());
		rooms.add(new BelisariusHQ());
		
		MonitorService monitorService = new MonitorService(client, rooms);
		monitorService.startMonitor();
		monitorService.runMonitor();
	}

}