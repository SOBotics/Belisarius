package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class AliveCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliveCommand.class);

    public AliveCommand(Message message) {
        super(message);
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.getNameAndId() + " wants to know if I'm alive.");

        service.replyToMessage(this.message.getId(), "Yeah, I'm alive.");
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
