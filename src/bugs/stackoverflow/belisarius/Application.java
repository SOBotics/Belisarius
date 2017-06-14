package bugs.stackoverflow.belisarius;

import bugs.stackoverflow.belisarius.service.Monitor;
import bugs.stackoverflow.belisarius.service.PropertyService;
import bugs.stackoverflow.belisarius.util.Login;
import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;

public class Application {
	
	public static void main(String[] args) {
		
		StackExchangeClient client = Login.getClient();
		
		PropertyService ps = new PropertyService();
		Room room = client.joinRoom(ChatHost.STACK_OVERFLOW, ps.getRoomId());
		
		Monitor monitor = new Monitor(room);
		monitor.startMonitor();
	}

}
