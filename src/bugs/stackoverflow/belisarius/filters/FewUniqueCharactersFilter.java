package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import fr.tunaki.stackoverflow.chat.Room;

public class FewUniqueCharactersFilter implements Filter {

	private Room room;
	private Post post;
	private int reasonId;
	private String listedWord;
	
	public FewUniqueCharactersFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
		this.listedWord = "";
        if (post.getBody() != null) {
        	this.listedWord = CheckUtils.checkForFewUniqueCharacters(post.getBody());
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
		return "**Few unique characters detected - ** " + this.listedWord;
	}

	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
	}

	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), this.reasonId, this.getScore());
	}

}
