package bugs.stackoverflow.belisarius.commands;

import java.util.List;

import bugs.stackoverflow.belisarius.services.MonitorService;

import org.sobotics.chatexchange.chat.Message;

public class CommandsCommand extends Command {
    private final List<Command> commands;

    public CommandsCommand(Message message, List<Command> commands) {
        super(message);

        this.commands = commands;
    }

    @Override
    public void execute(MonitorService service) {
        StringBuilder commandString = new StringBuilder();

        for (Command c : this.commands) {
            commandString
                .append("    ")
                .append(padRight(c.getName(), 15))
                .append(" - ")
                .append(c.getDescription())
                .append("\n");
        }

        service.replyToMessage(this.message.getId(), "The list of commands is as follows:");
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
