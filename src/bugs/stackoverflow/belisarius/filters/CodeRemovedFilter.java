package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import fr.tunaki.stackoverflow.chat.Room;

public class CodeRemovedFilter implements Filter {

	private Room room;
	private Post post;
	private int reasonId;
	
	public CodeRemovedFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
		if (post.getLastBody() != null && post.getBody() != null) {
			return !CheckUtils.checkIfNoCodeBlock(post.getLastBody()) && CheckUtils.checkIfNoCodeBlock(post.getBody());
		}
		return false;
	}
	
	@Override
	public double getScore() {
		return 1.0;
	}

	@Override
	public String getFormattedReasonMessage() {
		return "**Code removed**";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}

	@Override
	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), this.reasonId, this.getScore());
	}
}