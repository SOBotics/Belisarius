package bugs.stackoverflow.belisarius.commands;

import java.util.List;

import bugs.stackoverflow.belisarius.utils.CommandUtils;
import fr.tunaki.stackoverflow.chat.*;

public class CommandsCommand implements Command {

	private Message message;
	private List<Command> commands;
	
	public CommandsCommand(Message message, List<Command> commands) {
		this.message = message;
		this.commands = commands;
	}
	
	@Override
	public boolean validate() {
		return CommandUtils.checkForCommand(this.message.getPlainContent(),this.getName());
	}

	@Override
	public void execute(Room room) {
		String commandString = "";
		for (Command c : this.commands) {
			commandString += "    " + padRight(c.getName(), 15) + " - " + c.getDescription() + "\n";
		}
		room.replyTo(this.message.getId(), "The list of commands are as follows:");
		room.send(commandString);
	}
	
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
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
