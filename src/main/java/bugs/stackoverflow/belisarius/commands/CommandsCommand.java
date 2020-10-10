package bugs.stackoverflow.belisarius.commands;

import java.util.List;

import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.sobotics.chatexchange.chat.Message;

public class CommandsCommand implements Command {

    private Message message;
    private List<Command> commands;

    public CommandsCommand(Message message, List<Command> commands) {
        this.message = message;
        this.commands = commands;
    }

    @Override
    public boolean validate() {
        return CommandUtils.checkForCommand(this.message.getPlainContent(), this.getName());
    }

    @Override
    public void execute(MonitorService service) {
        StringBuilder commandString = new StringBuilder();
        for (Command c : this.commands) {
            commandString.append("    ").append(padRight(c.getName(), 15)).append(" - ").append(c.getDescription()).append("\n");
        }
        service.replyToMessage(this.message.getId(), "The list of commands are as follows:");
        service.sendMessageToChat(commandString.toString());
    }

    public static String padRight(String text, int padding) {
        return String.format("%1$-" + padding + "s", text);
    }

    @Override
    public String getDescription() {
        return "Returns the list of commands associated with this bot.";
    }

    @Override
    public String getName() {
        return "commands";
    }

}
