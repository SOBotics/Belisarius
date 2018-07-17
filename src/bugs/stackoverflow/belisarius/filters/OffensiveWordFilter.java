package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import org.sobotics.chatexchange.chat.Room;

public class OffensiveWordFilter implements Filter {
	
	private Room room;
	private Post post;
	private int reasonId;
	
	private HashMap<Integer, String> offensiveWords = new HashMap<>();
	
	public OffensiveWordFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {

		if (this.post.getComment() != null) {
			offensiveWords = CheckUtils.checkForOffensiveWords(this.post.getComment());
		}
		
		return getScore()>0;
	}
	
	@Override
	public double getScore() {
		return offensiveWords.size();
	}

	@Override
	public String getFormattedReasonMessage() {
		return "**Comment contains offensive " + (this.offensiveWords.size() > 0 ? "words" : "word") + " - ** " + getOffensiveWords();
	}
	
	private String getOffensiveWords() {
		StringBuilder words = new StringBuilder();

		for(String word : offensiveWords.values()) {
			words.append(word);
		}

		return words.toString();
	}

	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
	}
	
	private List<Integer> getCaughtOffensiveWordIds() {
		return new ArrayList<>(offensiveWords.keySet());
	}

	@Override
	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), this.reasonId, this.getScore());
		this.getCaughtOffensiveWordIds().forEach(id ->
			DatabaseUtils.storeCaughtOffensiveWord(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), id)
        );
	}
}