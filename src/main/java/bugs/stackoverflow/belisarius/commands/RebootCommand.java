package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class RebootCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(RebootCommand.class);

    public RebootCommand(Message message) {
        super(message);
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.getNameAndId() + " is attempting to reboot me.");

        if (isModOrRO()) {
            service.replyToMessage(this.message.getId(), "Rebooting, please wait...");
            service.reboot();
        } else {
            this.sendFailMessage(service);
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
