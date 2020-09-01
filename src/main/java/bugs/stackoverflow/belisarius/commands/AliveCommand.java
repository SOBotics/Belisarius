package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class AliveCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliveCommand.class);

    private Message message;

    public AliveCommand(Message message) {
        this.message = message;
    }

    @Override
    public boolean validate() {
        return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
    }

    @Override
    public void execute(Room room) {
        LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") wants to know if I'm alive.");
        room.replyTo(this.message.getId(), "Yeah, I'm alive.");

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
