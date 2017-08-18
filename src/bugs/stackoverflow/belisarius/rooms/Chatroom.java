package bugs.stackoverflow.belisarius.rooms;

import java.util.function.Consumer;

import bugs.stackoverflow.belisarius.services.MonitorService;
import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.*;


public interface Chatroom {
	
	public int getRoomId();
	public ChatHost getHost();
	public String getSiteName();
	public String getSiteUrl();
	
    public Consumer<UserMentionedEvent> getUserMentioned(Room room, MonitorService service);
    public Consumer<MessageReplyEvent> getPostedReply(Room room);
    public Consumer<MessagePostedEvent> getPostedMessage(Room room,  MonitorService service);

}