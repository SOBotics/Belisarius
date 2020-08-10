package bugs.stackoverflow.belisarius.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugs.stackoverflow.belisarius.utils.*;
import org.sobotics.chatexchange.chat.*;

public class QuotaCommand implements Command {

	private Message message;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuotaCommand.class);
	
	public QuotaCommand(Message message) {
		this.message = message;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") is checking my remaining quota which is " + StatusUtils.remainingQuota);
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
