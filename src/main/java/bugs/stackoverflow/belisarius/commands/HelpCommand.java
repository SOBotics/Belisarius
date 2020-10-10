package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class HelpCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

    private Message message;

    public HelpCommand(Message message) {
        this.message = message;
    }

    @Override
    public boolean validate() {
        return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") wants to know more about me.");
        service.replyToMessage(message.getId(), "I'm a bot that monitors for bad edits and possible vandalism on posts.");

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
