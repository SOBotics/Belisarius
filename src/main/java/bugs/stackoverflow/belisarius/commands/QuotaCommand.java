package bugs.stackoverflow.belisarius.commands;

import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.services.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;

public class QuotaCommand extends Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotaCommand.class);

    public QuotaCommand(Message message) {
        super(message);
    }

    @Override
    public void execute(MonitorService service) {
        int quota = ApiService.getQuota();

        LOGGER.info(this.getNameAndId() + " is checking my remaining quota, which is " + quota);

        service.replyToMessage(
            message.getId(),
            "The remaining quota is: " + quota
        );
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
