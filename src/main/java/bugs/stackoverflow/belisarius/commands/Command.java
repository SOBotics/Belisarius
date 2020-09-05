package bugs.stackoverflow.belisarius.commands;

import org.sobotics.chatexchange.chat.Room;

public interface Command {

    boolean validate();

    void execute(Room room);

    String getDescription();

    String getName();

}
