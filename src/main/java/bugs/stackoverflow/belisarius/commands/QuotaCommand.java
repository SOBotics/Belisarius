package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.utils.CommandUtils;
import bugs.stackoverflow.belisarius.utils.StatusUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

public class QuotaCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotaCommand.class);

    private Message message;

    public QuotaCommand(Message message) {
        this.message = message;
    }

    @Override
    public boolean validate() {
        return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
    }

    @Override
    public void execute(Room room) {
        LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") is checking my remaining quota which is " + StatusUtils.remainingQuota);
        room.replyTo(message.getId(), "The remaining quota is: " + StatusUtils.remainingQuota);
    }

    @Override
    public String getDescription() {
        return "Returns the current quota";
    }

    @Override
    public String getName() {
        return "quota";
    }

}
