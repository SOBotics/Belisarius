package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;
import fr.tunaki.stackoverflow.chat.*;

public class RebootCommand implements Command {

	private Message message;
	private MonitorService service;
	
	public RebootCommand(Message message, MonitorService service) {
		this.message = message;
		this.service = service;
	}

	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		if (this.message.getUser().isModerator() || this.message.getUser().isRoomOwner()) {
			room.replyTo(this.message.getId(), "Rebooting, please wait...");
			service.reboot();
		} else {
			room.replyTo(this.message.getId(), "You must be either a moderator or a room owner to execute the reboot command.");
			return;
		}
	}

	@Override
	public String getDescription() {
		return "Stops and starts the bot (must be either a moderator or a room owner).";
	}

	@Override
	public String getName() {
		return "reboot";
	}
	
}
