package bugs.stackoverflow.belisarius.utils;

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
import bugs.stackoverflow.belisarius.models.VandalisedPost.Feedback;
import bugs.stackoverflow.belisarius.services.MonitorService;

import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.MessagePostedEvent;
import org.sobotics.chatexchange.chat.event.PingMessageEvent;

public class ChatUtils {

    private static final String SITE_STACK_OVERFLOW = "stackoverflow";
    private static final String SITE_STACK_EXCHANGE = "stackexchange";

    public static ChatHost getChatHost(String siteName) {
        switch (siteName) {
            case SITE_STACK_OVERFLOW:
                return ChatHost.STACK_OVERFLOW;
            case SITE_STACK_EXCHANGE:
                return ChatHost.STACK_EXCHANGE;
            default:
                return ChatHost.STACK_OVERFLOW;
        }
    }

    public static void reply(Room room, PingMessageEvent event) {
        String message = event.getMessage().getPlainContent().trim();
        String feedbackArg = message.split(" ")[1].toLowerCase();
        if (feedbackArg.equals(Feedback.T.toString()) || feedbackArg.equals(Feedback.TP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.TP);
        } else if (feedbackArg.equals(Feedback.F.toString()) || feedbackArg.equals(Feedback.FP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.FP);
        }
    }

    public static void mention(Room room, PingMessageEvent event, MonitorService service) {

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

        for (Command command : commands) {
            if (command.validate()) {
                command.execute(room);
            }
        }

    }

    public static void posted(Room room, MessagePostedEvent event) {
        String message = event.getMessage().getPlainContent().trim();

        int codePoint = Character.codePointAt(message, 0);
        if (message.toLowerCase().startsWith("@bots alive")) {
            room.send("Yeah, I'm alive.");
        } else if (codePoint == 128642 || (codePoint >= 128644 && codePoint <= 128650)) {
            room.send("[\uD83D\uDE83](https://www.youtube.com/watch?v=oKk-2Pu2N8g)");
        }
    }
}
