package bugs.stackoverflow.belisarius.commandlists;

import java.util.*;

import bugs.stackoverflow.belisarius.commands.*;
import bugs.stackoverflow.belisarius.commands.Command;
import bugs.stackoverflow.belisarius.services.MonitorService;
import fr.tunaki.stackoverflow.chat.*;
import fr.tunaki.stackoverflow.chat.event.*;

public class BelisariusCommandList {
	public void mention(Room room, PingMessageEvent event, MonitorService service, String siteName, String siteUrl) {
		
		Message message = event.getMessage();
		
		List<Command> commands = new ArrayList<>(Arrays.asList(
				new AliveCommand(message),
				new HelpCommand(message),
				new QuotaCommand(message),
				new RebootCommand(message, service),
				new StopCommand(message, service)
				));
		
		commands.add(new CommandsCommand(message, commands));
		
		for (Command c : commands) {
			if (c.validate()) {
				c.execute(room);
			}
		}

	}
	
	public void posted(Room room, MessagePostedEvent event, MonitorService service) {
		String message = event.getMessage().getPlainContent().trim();
		
		int cp = Character.codePointAt(message, 0);
		if (message.toLowerCase().startsWith("@bots alive")) {
			room.send("Yeah, I'm alive.");
		} else {
			if (cp == 128642 || (cp>=128644 && cp<=128650)) {
				room.send("[\uD83D\uDE83](https://en.wikipedia.org/wiki/Belisarius)");
			}
		}
	}
	
}