package bugs.stackoverflow.belisarius.rooms;

import java.util.function.Consumer;

import bugs.stackoverflow.belisarius.commandlists.BelisariusCommandList;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.ChatUtils;
import fr.tunaki.stackoverflow.chat.ChatHost;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.MessagePostedEvent;
import fr.tunaki.stackoverflow.chat.event.MessageReplyEvent;
import fr.tunaki.stackoverflow.chat.event.UserMentionedEvent;

public class BelisariusHQ implements Chatroom {

	@Override
	public int getRoomId() {
		return 151589;
	}

	@Override
	public ChatHost getHost() {
		return ChatHost.STACK_OVERFLOW;
	}

	@Override
	public String getSiteName() {
		return "stackoverflow";
	}

	@Override
	public String getSiteUrl() {
		return "stackoverflow.com";
	}

	@Override
	public Consumer<UserMentionedEvent> getUserMentioned(Room room, MonitorService service) {
		return event->new BelisariusCommandList().mention(room, event, service, getSiteName(), getSiteUrl());
	}

	@Override
	public Consumer<MessageReplyEvent> getPostedReply(Room room) {
		return event-> ChatUtils.reply(room, event, getSiteName(), getSiteUrl());
	}

	@Override
	public Consumer<MessagePostedEvent> getPostedMessage(Room room, MonitorService service) {
		return event->new BelisariusCommandList().posted(room, event, service);
	}

}
