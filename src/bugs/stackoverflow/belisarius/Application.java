package bugs.stackoverflow.belisarius;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;
import bugs.stackoverflow.belisarius.utils.LoginUtils;
import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;

public class Application {
	
	public static void main(String[] args) {
		
		StackExchangeClient client = LoginUtils.getClient();
		
		PropertyService ps = new PropertyService();
		Room room = client.joinRoom(ChatHost.STACK_OVERFLOW, ps.getRoomId());
		
		MonitorService monitorService = new MonitorService(room);
		monitorService.startMonitor();
	}

}
