package bugs.stackoverflow.belisarius.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;
import org.sobotics.chatexchange.chat.*;

public class StopCommand implements Command {

	private Message message;
	private MonitorService service;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);
	
	public StopCommand(Message message, MonitorService service) {
		this.message = message;
		this.service = service;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") is attempting to stop me.");
		if (this.message.getUser().isModerator() || this.message.getUser().isRoomOwner()) {
			service.stop();
		} else {
			room.replyTo(this.message.getId(), "You must be either a moderator or a room owner to execute the stop command.");
			return;
		}
	}

	@Override
	public String getDescription() {
		return "Stops the bot (must be a either a moderator or a room owner).";
	}

	@Override
	public String getName() {
		return "stop";
	}

}
