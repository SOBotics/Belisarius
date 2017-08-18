package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.utils.CommandUtils;
import fr.tunaki.stackoverflow.chat.*;

public class AliveCommand implements Command {

	private Message message;

	public AliveCommand(Message message) {
		this.message = message;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		room.replyTo(this.message.getId(), "Yeah, I'm alive.");
		
	}

	@Override
	public String getDescription() {
		return "Test to check if the bot is alive or not.";
	}

	@Override
	public String getName() {
		return "alive";
	}

}
