package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import fr.tunaki.stackoverflow.chat.Room;

public class OffensiveWordFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OffensiveWordFilter.class);
	
	private Room room;
	private Post post;
	private int reasonId;
	
	private HashMap<Integer, String> offensiveWords = new HashMap<Integer, String>();
	
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
		String words = "";
		
		Iterator iterator = this.offensiveWords.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Integer, String> word = (Map.Entry<Integer, String>)iterator.next();
			words += word.getValue() + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}

	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
	}
	
	private List<Integer> getCaughtOffensiveWordIds() {
		List<Integer> reasonIds = new ArrayList<Integer>();
		
		try {
			Iterator iterator = this.offensiveWords.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<Integer, String> offensiveWord = (Map.Entry<Integer, String>)iterator.next();
				reasonIds.add(offensiveWord.getKey());
			}
		} catch (Exception e) {
			LOGGER.info("Failed to get Ids from offensiveWords.", e);
		}
		
		return reasonIds;
	}
	
	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), this.reasonId, this.getScore());
		this.getCaughtOffensiveWordIds().stream().forEach(id -> {
			DatabaseUtils.storeCaughtOffensiveWord(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), id);	
		});
	}
}