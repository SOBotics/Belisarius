package bugs.stackoverflow.belisarius;

import java.util.*;

import bugs.stackoverflow.belisarius.rooms.*;
import bugs.stackoverflow.belisarius.services.*;
import bugs.stackoverflow.belisarius.utils.LoginUtils;
import fr.tunaki.stackoverflow.chat.*;

public class Application {
	
	public static void main(String[] args) {
		
		StackExchangeClient client = LoginUtils.getClient();
			
		List<Chatroom> rooms = new ArrayList<>();
		rooms.add(new SOBotics());
		rooms.add(new BelisariusHQ());
		
		MonitorService monitorService = new MonitorService(client, rooms);
		monitorService.startMonitor();
		monitorService.runMonitor();
	}

}