package bugs.stackoverflow.belisarius.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyService {

	public static String loginPropertiesFile = "./properties/login.properties";
	
	private Properties prop;

	public PropertyService() {
		prop = new Properties();
		try{
			prop.load(new FileInputStream(loginPropertiesFile));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public String getApiKey() {
		String apiKey = prop.getProperty("apikey");
		if (apiKey == null) {
			apiKey = System.getenv("apikey");
		}
			
		return apiKey;
	}	
	
	public String getEmail() {
		String email = prop.getProperty("email");
		if (email == null) {
			email = System.getenv("email");
		}
		return email;
	}
	
	public String getPassword() {
		String password = prop.getProperty("password");
		if (password == null) {
			password = System.getenv("password");
		}
		return password;
	}
	
	public int getRoomId() {
		String roomId = prop.getProperty("roomid");
		if (roomId == null) {
			roomId = System.getenv("roomId");
		}
		
		int returnRoomId = 0;
		try{
			returnRoomId = Integer.parseInt(roomId);
		} catch(NumberFormatException e){
			System.out.println("Error parsing roomId: " + e.getMessage());
		}

		return returnRoomId;
	}
	
	public String getSite() {
		String site = prop.getProperty("site");
		if (site == null) {
			site = System.getenv("site");
		}
		return site;
	}
}