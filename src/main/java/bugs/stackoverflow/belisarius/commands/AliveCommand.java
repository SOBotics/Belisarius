package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class AliveCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliveCommand.class);

    private final Message message;

    public AliveCommand(Message message) {
        this.message = message;
    }

    @Override
    public boolean validate() {
        return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") wants to know if I'm alive.");
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
