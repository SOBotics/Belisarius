package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class StopCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);

    public StopCommand(Message message) {
        super(message);
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.getNameAndId() + " is attempting to stop me.");

        if (this.isModOrRO()) {
            service.stop();
        } else {
            this.sendFailMessage(service);
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
