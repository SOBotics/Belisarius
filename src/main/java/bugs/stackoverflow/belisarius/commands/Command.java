package bugs.stackoverflow.belisarius.commands;

import org.sobotics.chatexchange.chat.Room;

public interface Command {

	public boolean validate();
	public void execute(Room room);
	public String getDescription();
	public String getName();
	
}