package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.models.VandalisedPost.Feedback;

import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.PingMessageEvent;

public class ChatUtils {

    public static void reply(Room room, PingMessageEvent event) {
        String message = event.getMessage().getPlainContent().trim();
        String feedbackArg = message.split(" ")[1].toLowerCase();
        if (feedbackArg.equals(Feedback.T.toString()) || feedbackArg.equals(Feedback.TP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.TP);
        } else if (feedbackArg.equals(Feedback.F.toString()) || feedbackArg.equals(Feedback.FP.toString())) {
            PostUtils.storeFeedback(room, event, Feedback.FP);
        }
    }
}
