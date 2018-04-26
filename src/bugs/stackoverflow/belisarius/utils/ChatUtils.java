package bugs.stackoverflow.belisarius.utils;

import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.*;

public class ChatUtils {

    public static void reply(Room room, PingMessageEvent event){
		String message = event.getMessage().getPlainContent().trim();
		
		if (message.split(" ")[1].toLowerCase().equals("t") || message.split(" ")[1].toLowerCase().equals("tp")) {
			PostUtils.storeFeedback(room, event, "tp");
		} else if (message.split(" ")[1].toLowerCase().equals("f") || message.split(" ")[1].toLowerCase().equals("fp")) {
			PostUtils.storeFeedback(room, event, "fp");
		}
    }
}