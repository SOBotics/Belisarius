package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.services.PropertyService;
import fr.tunaki.stackoverflow.chat.StackExchangeClient;

public class LoginUtils {

	public static StackExchangeClient getClient() {
		StackExchangeClient client;
		
		PropertyService ps = new PropertyService();
        client = new StackExchangeClient(ps.getEmail(), ps.getPassword());
		
		return client;
	}

}