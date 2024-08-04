package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class CheckCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckCommand.class);

    public CheckCommand(Message message) {
        super(message);
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.getNameAndId() + " is checking a post for vandalism.");

        if (isModOrRO()) {
            String[] args = CommandUtils.extractData(message.getPlainContent()).trim().split(" ");

            if (args.length != 1) {
                service.sendMessageToChat("Error in arguments passed.");

                return;
            }

            String postId = args[0];
            service.executeOnce(postId);
        } else {
            this.sendFailMessage(service);
        }
    }

    @Override
    public String getDescription() {
        return "Checks a post, through the API, for potential vandalism (must be either a moderator or a room owner).";
    }

    @Override
    public String getName() {
        return "check";
    }
}
