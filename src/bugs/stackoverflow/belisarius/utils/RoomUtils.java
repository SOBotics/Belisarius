package bugs.stackoverflow.belisarius.utils;

import fr.tunaki.stackoverflow.chat.ChatHost;

public class RoomUtils {

    private static final String siteStackoverflow = "stackoverflow";

    public static ChatHost getChatHost(String siteName) {
        switch (siteName)
        {
            case siteStackoverflow:
                return ChatHost.STACK_OVERFLOW;
            default:
                return ChatHost.STACK_OVERFLOW;
        }
    }
}