package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class HelpCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(Message message) {
        super(message);
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.getNameAndId() + " wants to know more about me.");

        service.replyToMessage(
            message.getId(),
            "I'm a bot that monitors for bad edits and possible vandalism on posts."
        );
    }

    @Override
    public String getDescription() {
        return "Returns the description of the bot.";
    }

    @Override
    public String getName() {
        return "help";
    }

}
