package bugs.stackoverflow.belisarius.utils;

import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.*;

public class ChatUtils {

    public static void reply(Room room, PingMessageEvent event, String sitename, String siteurl){
		String message = event.getMessage().getPlainContent().trim();
		
		if (message.split(" ")[1].toLowerCase().equals("t") || message.split(" ")[1].toLowerCase().equals("tp")) {
			PostUtils.storeFeedback(room, event, "tp");
		} else if (message.split(" ")[1].toLowerCase().equals("f") || message.split(" ")[1].toLowerCase().equals("fp")) {
			PostUtils.storeFeedback(room, event, "fp");
		}
    }
	
	private void messagePosted(Room room, MessagePostedEvent event) {
		String message = event.getMessage().getPlainContent().trim();
		
		int cp = Character.codePointAt(message, 0);
		if (message.toLowerCase().startsWith("@bots alive")) {
			room.send("Yeah, I'm alive.");
		} else if (message.toLowerCase().startsWith("@petterfriberg great prayer, let's begin. can you pass the fork please?")) {
			room.send("@Housekeeping romance is in the air, please play a song for this magical moment.");
		} else {
			if (cp == 128642 || (cp>=128644 && cp<=128650)) {
				room.send("[\uD83D\uDE83](https://en.wikipedia.org/wiki/Belisarius)");
			}
		}
		
	}
	

	
}
