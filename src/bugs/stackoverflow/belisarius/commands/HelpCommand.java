package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.utils.CommandUtils;
import fr.tunaki.stackoverflow.chat.*;

public class HelpCommand implements Command {

	private Message message;
	
	public HelpCommand(Message message) {
		this.message = message;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		room.replyTo(message.getId(), "I'm a bot that monitors for bad edits and possible vandalism on posts.");
		
	}

	@Override
	public String getDescription() {
		return "Returns the description of the bot.";
	}

	@Override
	public String getName() {
		return "help";
	}

	
	
}
