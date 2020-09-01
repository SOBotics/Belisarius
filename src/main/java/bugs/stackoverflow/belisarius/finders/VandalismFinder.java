package bugs.stackoverflow.belisarius.finders;

import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import org.sobotics.chatexchange.chat.Room;

public class VandalismFinder {

    private Post post;
    private Room room;

    public VandalismFinder(Room room, Post post) {
        this.room = room;
        this.post = post;
    }

    public VandalisedPost findReasons() {
        return PostUtils.getVandalisedPost(this.room, this.post);
    }

}
