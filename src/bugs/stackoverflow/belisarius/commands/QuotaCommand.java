package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.utils.*;
import fr.tunaki.stackoverflow.chat.*;

public class QuotaCommand implements Command {

	private Message message;
	
	public QuotaCommand(Message message) {
		this.message = message;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		room.replyTo(message.getId(), "The remaining quota is: " + StatusUtils.remainingQuota);
		
	}

	@Override
	public String getDescription() {
		return "Returns the current quota";
	}

	@Override
	public String getName() {
		return "quota";
	}

}
