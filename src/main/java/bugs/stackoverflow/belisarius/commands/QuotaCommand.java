package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class QuotaCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotaCommand.class);

    private final Message message;

    public QuotaCommand(Message message) {
        this.message = message;
    }

    @Override
    public boolean validate() {
        return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
    }

    @Override
    public void execute(MonitorService service) {
        LOGGER.info(this.message.getUser().getName() + " (" + this.message.getUser().getId() + ") "
                  + "is checking my remaining quota, which is " + ApiService.getQuota());
        service.replyToMessage(message.getId(), "The remaining quota is: " + ApiService.getQuota());
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
