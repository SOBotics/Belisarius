package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class RebootCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(RebootCommand.class);

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
        LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") is attempting to reboot me.");
        if (this.message.getUser().isModerator() || this.message.getUser().isRoomOwner()) {
            room.replyTo(this.message.getId(), "Rebooting, please wait...");
            service.reboot();
        } else {
            room.replyTo(this.message.getId(), "You must be either a moderator or a room owner to execute the reboot command.");
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
