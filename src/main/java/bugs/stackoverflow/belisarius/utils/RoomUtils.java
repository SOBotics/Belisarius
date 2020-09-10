package bugs.stackoverflow.belisarius.utils;

import org.sobotics.chatexchange.chat.ChatHost;

public class RoomUtils {

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
}
