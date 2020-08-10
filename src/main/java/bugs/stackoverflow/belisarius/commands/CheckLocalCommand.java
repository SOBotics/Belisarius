package bugs.stackoverflow.belisarius.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class CheckLocalCommand implements Command {

	private Message message;
	private MonitorService service;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckLocalCommand.class);
	
	public CheckLocalCommand(Message message, MonitorService service)
	{
		this.message = message;
		this.service = service;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
	}

	@Override
	public void execute(Room room) {
		LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") is checking a post for vandalism.");
		if (this.message.getUser().isModerator() || this.message.getUser().isRoomOwner()) {
			String args[] = CommandUtils.extractData(message.getPlainContent()).trim().split(" ");
			
	        if(args.length!=2){
	            room.send("Error in arguments passed.");
	            return;
	        }
			
			String postId = args[0];
			String revisionId = args[1];
			service.executeOnceLocal(postId, revisionId, room);
		} else {
			room.replyTo(this.message.getId(), "You must be either a moderator or a room owner to execute the check command.");
			return;
		}
		
	}

	@Override
	public String getDescription() {
		return "Checks a post, on the local db, for potential vandalism (must be either a moderator or a room owner).";
	}

	@Override
	public String getName() {
		return "checklocal";
	}

}
