package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.models.VandalisedPost.Feedback;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.*;

public class ChatUtils {

    public static void reply(Room room, PingMessageEvent event) {
        String message = event.getMessage().getPlainContent().trim();

        if (message.split(" ")[1].toLowerCase().equals(Feedback.T.toString()) || message.split(" ")[1].toLowerCase().equals(Feedback.TP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.TP);
        } else if (message.split(" ")[1].toLowerCase().equals(Feedback.F.toString()) || message.split(" ")[1].toLowerCase().equals(Feedback.FP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.FP);
        }
    }
}