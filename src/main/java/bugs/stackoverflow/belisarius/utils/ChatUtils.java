package bugs.stackoverflow.belisarius.utils;

import java.util.ArrayList;
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

    public static ChatHost getChatHost(String siteName) {
        return "stackoverflow".equals(siteName) ? ChatHost.STACK_OVERFLOW : ChatHost.STACK_EXCHANGE;
    }

    public static void handleReplies(Room room, PingMessageEvent event) {
        String message = event.getMessage().getPlainContent().trim();
        String feedbackArg = message.split(" ")[1].toLowerCase();
        if (feedbackArg.equals(Feedback.T.toString()) || feedbackArg.equals(Feedback.TP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.TP);
        } else if (feedbackArg.equals(Feedback.F.toString()) || feedbackArg.equals(Feedback.FP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.FP);
        }
    }

    public static void handleMentionedEvent(PingMessageEvent event, MonitorService service) {
        Message message = event.getMessage();

        List<Command> commands = new ArrayList<>();
        commands.add(new AliveCommand(message));
        commands.add(new CheckCommand(message));
        commands.add(new HelpCommand(message));
        commands.add(new QuotaCommand(message));
        commands.add(new RebootCommand(message));
        commands.add(new StopCommand(message));
        commands.add(new CommandsCommand(message, commands));

        for (Command command : commands) {
            if (command.validate()) {
                command.execute(service);
            }
        }

    }

    public static void handleMessagePostedEvent(MessagePostedEvent event, MonitorService service) {
        String message = event.getMessage().getPlainContent().trim();

        int codePoint = Character.codePointAt(message, 0);
        if (message.toLowerCase().startsWith("@bots alive")) {
            service.sendMessageToChat("Yeah, I'm alive.");
        } else if (codePoint == 128642 || (codePoint >= 128644 && codePoint <= 128650)) {
            service.sendMessageToChat("[\uD83D\uDE83](https://www.youtube.com/watch?v=oKk-2Pu2N8g)");
        }
    }
}
