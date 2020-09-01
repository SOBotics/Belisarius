package bugs.stackoverflow.belisarius.filters;

import org.apache.commons.lang3.StringUtils;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import org.sobotics.chatexchange.chat.Room;

public class VeryLongWordFilter implements Filter {

	private Room room;
	private Post post;
	private int reasonId;
	private String listedWord;
	
	public VeryLongWordFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
		this.listedWord = "";
        if (post.getBody() != null) {
        	this.listedWord = CheckUtils.checkForLongWords(StringUtils.difference(post.getLastBody(), post.getBody()));
	        if(this.listedWord!=null){
	            return true;
	        }
        }
        return false;
	}

	@Override
	public double getScore() {
		return 1.0;
	}

	@Override
	public String getFormattedReasonMessage() {
		return "**Contains very long word - ** " + this.listedWord.substring(0, 50) + "...";
	}

    @Override
    public String getReasonName() {
        return "Contains very long word";
    }

	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}

    @Override
    public void storeHit() {
        long postId = this.post.getPostId();
        int revisionNumber = this.post.getRevisionNumber();
        int roomId = this.room.getRoomId();
        if (!DatabaseUtils.checkReasonCaughtExists(postId, revisionNumber, roomId, this.reasonId)) {
            DatabaseUtils.storeReasonCaught(postId, revisionNumber, roomId, this.reasonId, this.getScore());
        }
    }

}
