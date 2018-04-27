package bugs.stackoverflow.belisarius.filters;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import fr.tunaki.stackoverflow.chat.Room;

public class BlacklistedFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlacklistedFilter.class);
	
	private Room room;
	private Post post;
	private int reasonId;
	private HashMap<Integer, String> blacklistedWordsTitle = new HashMap<> ();
	private HashMap<Integer, String> blacklistedWordsBody = new HashMap<>();
	private HashMap<Integer, String> blacklistedWordsEditSummary = new HashMap<>();
	
	public BlacklistedFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
		
		if (post.getTitle() != null) {
			blacklistedWordsTitle = CheckUtils.checkForBlackListedWords(StringUtils.difference(post.getLastTitle(), post.getTitle()), post.getPostType());
		}
		
		if (post.getBody() != null) {
			blacklistedWordsBody = CheckUtils.checkForBlackListedWords(StringUtils.difference(post.getLastBody(), post.getBody()), post.getPostType());
		}
		
		if (post.getComment() != null) {
			blacklistedWordsEditSummary = CheckUtils.checkForBlackListedWords(post.getComment(), post.getPostType());
		}
		
		return getScore()>0;
	}
	
	@Override
	public double getScore() {
		return this.blacklistedWordsTitle.size() + this.blacklistedWordsBody.size() + this.blacklistedWordsEditSummary.size();
	}

	@Override
	public String getFormattedReasonMessage() {
		String message = "";
		
		try {
			if (this.blacklistedWordsTitle.size()>0) {
				message += "**Title contains blacklisted " + (this.blacklistedWordsTitle.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsTitle() + " ";
			}
			
			if (this.blacklistedWordsBody.size()>0) {
				message += "**Body contains blacklisted " + (this.blacklistedWordsBody.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsBody() + " ";
			}
		
			if (this.blacklistedWordsEditSummary.size()>0) {
				message += "**Edit summary contains blacklisted " + (this.blacklistedWordsEditSummary.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsComment() + " ";
			}
		} catch (Exception e)
		{
			LOGGER.info("Failed to get formatted reason message.", e);
		}
		return message.trim();
	}

	private String getBlacklistedWordsTitle() {
		StringBuilder words = new StringBuilder();

		for(String word : blacklistedWordsTitle.values()) {
		    words.append(word);
        }

		return words.toString();
	}
	
	private String getBlacklistedWordsBody() {
		StringBuilder words = new StringBuilder();

        for(String word : blacklistedWordsBody.values()) {
            words.append(word);
        }

		return words.toString();
	}
		
	private String getBlacklistedWordsComment() {
        StringBuilder words = new StringBuilder();

        for(String word : blacklistedWordsEditSummary.values()) {
            words.append(word);
        }

        return words.toString();
    }

	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}
	
	private List<Integer> getCaughtBlacklistedWordIds() {
		List<Integer> blacklistedWordIds = new ArrayList<>();

		blacklistedWordIds.addAll(blacklistedWordsTitle.keySet());
		blacklistedWordIds.addAll(blacklistedWordsBody.keySet());
		blacklistedWordIds.addAll(blacklistedWordsEditSummary.keySet());
		
		return blacklistedWordIds;
	}

	@Override
	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), this.reasonId, this.getScore());
		this.getCaughtBlacklistedWordIds().forEach(id ->
			DatabaseUtils.storeCaughtBlacklistedWord(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), id)
		);
	}
}
