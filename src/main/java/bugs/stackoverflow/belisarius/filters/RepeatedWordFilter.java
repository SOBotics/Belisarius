package bugs.stackoverflow.belisarius.filters;

import java.util.*;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import org.sobotics.chatexchange.chat.Room;

public class RepeatedWordFilter implements Filter {

	private Room room;
	private Post post;
	private int reasonId;
	private Set<String> repeatedWords = new HashSet<>();
	
	public RepeatedWordFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
	
		if (this.post.getBody() != null) {
			repeatedWords = CheckUtils.checkRepeatedWords(this.post.getBody());
		}
		
		double score = getScore();
		return score>0 && score<=5;
	}
	
	@Override
	public double getScore() {
		return repeatedWords.size();
	}

	@Override
	public String getFormattedReasonMessage() {
		String message = "";
		
		if (this.repeatedWords.size()>0) {
			message += "**Post contains repeated " + (this.repeatedWords.size()>1 ? "words" : "word") + " - ** " + getRepeatedWords() + " ";
		}
		
		return message.trim();
	}

	@Override
    public String getReasonName() {
        return "Contains repeated words";
    }

	private String getRepeatedWords() {
		StringBuilder words = new StringBuilder();

		for(String word : repeatedWords) {
			words.append(word);
		}

		return words.toString();
	}


	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
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