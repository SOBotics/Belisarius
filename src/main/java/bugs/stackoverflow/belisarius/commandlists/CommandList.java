package bugs.stackoverflow.belisarius.commandlists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bugs.stackoverflow.belisarius.commands.AliveCommand;
import bugs.stackoverflow.belisarius.commands.CheckCommand;
import bugs.stackoverflow.belisarius.commands.Command;
import bugs.stackoverflow.belisarius.commands.CommandsCommand;
import bugs.stackoverflow.belisarius.commands.HelpCommand;
import bugs.stackoverflow.belisarius.commands.QuotaCommand;
import bugs.stackoverflow.belisarius.commands.RebootCommand;
import bugs.stackoverflow.belisarius.commands.StopCommand;
import bugs.stackoverflow.belisarius.services.MonitorService;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.MessagePostedEvent;
import org.sobotics.chatexchange.chat.event.PingMessageEvent;

public class CommandList {
    public void mention(Room room, PingMessageEvent event, MonitorService service) {

        Message message = event.getMessage();

        List<Command> commands = new ArrayList<>(Arrays.asList(
                new AliveCommand(message),
                new CheckCommand(message, service),
                new HelpCommand(message),
                new QuotaCommand(message),
                new RebootCommand(message, service),
                new StopCommand(message, service)
                ));

        commands.add(new CommandsCommand(message, commands));

        for (Command c : commands) {
            if (c.validate()) {
                c.execute(room);
            }
        }

    }

    public void posted(Room room, MessagePostedEvent event) {
        String message = event.getMessage().getPlainContent().trim();

        int cp = Character.codePointAt(message, 0);
        if (message.toLowerCase().startsWith("@bots alive")) {
            room.send("Yeah, I'm alive.");
        } else {
            if (cp == 128642 || (cp >= 128644 && cp <= 128650)) {
                room.send("[\uD83D\uDE83](https://www.youtube.com/watch?v=oKk-2Pu2N8g)");
            }
        }
    }

}
