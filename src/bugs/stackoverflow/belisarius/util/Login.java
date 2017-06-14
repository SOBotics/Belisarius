package bugs.stackoverflow.belisarius.util;

import fr.tunaki.stackoverflow.chat.StackExchangeClient;
import bugs.stackoverflow.belisarius.service.PropertyService;

public class Login {
	
	public static StackExchangeClient getClient() {
		StackExchangeClient client;
		
		PropertyService ps = new PropertyService();
        client = new StackExchangeClient(ps.getEmail(), ps.getPassword());
		
		return client;
	}

}