package bugs.stackoverflow.belisarius.models;

import java.util.function.Consumer;

import bugs.stackoverflow.belisarius.commandlists.CommandList;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.ChatUtils;

import org.sobotics.chatexchange.chat.ChatHost;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.MessagePostedEvent;
import org.sobotics.chatexchange.chat.event.MessageReplyEvent;
import org.sobotics.chatexchange.chat.event.UserMentionedEvent;

public class Chatroom {

    private int roomId;
    private ChatHost chatHost;
    private String site;
    private boolean outputMessage;

    public Chatroom(int roomId, ChatHost chatHost, String site, boolean outputMessage) {
        this.roomId = roomId;
        this.chatHost = chatHost;
        this.site = site;
        this.outputMessage = outputMessage;
    }

    public int getRoomId() {
        return roomId;
    }

    public ChatHost getHost() {
        return chatHost;
    }

    public String getSiteName() {
        return site;
    }

    public boolean getOutputMessage() {
        return outputMessage;
    }

    public Consumer<UserMentionedEvent> getUserMentioned(Room room, MonitorService service) {
        return event -> new CommandList().mention(room, event, service);
    }

    public Consumer<MessageReplyEvent> getPostedReply(Room room) {
        return event -> ChatUtils.reply(room, event);
    }

    public Consumer<MessagePostedEvent> getPostedMessage(Room room) {
        return event -> new CommandList().posted(room, event);
    }

}
