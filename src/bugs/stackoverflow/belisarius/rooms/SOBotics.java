package bugs.stackoverflow.belisarius.rooms;

import java.util.function.Consumer;

import bugs.stackoverflow.belisarius.commandlists.SOBoticsCommandList;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.*;
import fr.tunaki.stackoverflow.chat.*;
import fr.tunaki.stackoverflow.chat.event.*;

public class SOBotics implements Chatroom {
	
	
	@Override
	public int getRoomId() {
		return 111347;
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
		return event->new SOBoticsCommandList().mention(room, event, service, getSiteName(), getSiteUrl());
	}

	@Override
	public Consumer<MessageReplyEvent> getPostedReply(Room room) {
		return event-> ChatUtils.reply(room, event, getSiteName(), getSiteUrl());
	}

	@Override
	public Consumer<MessagePostedEvent> getPostedMessage(Room room, MonitorService service) {
		return event->new SOBoticsCommandList().posted(room, event, service);
	}

}
