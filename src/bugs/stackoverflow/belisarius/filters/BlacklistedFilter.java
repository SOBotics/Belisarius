package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import fr.tunaki.stackoverflow.chat.Room;
<<<<<<< HEAD


=======
>>>>>>> 07716059bda34d7a7da9c5e47b728127712b2453

public class BlacklistedFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlacklistedFilter.class);
	
	private Room room;
	private Post post;
	private int reasonId;
	private HashMap<Integer, String> blacklitedWordsTitle = new HashMap<Integer, String> ();
	private HashMap<Integer, String> blacklitedWordsBody = new HashMap<Integer, String>();
	private HashMap<Integer, String>  blacklitedWordsEditSummary = new HashMap<Integer, String>();
	
	public BlacklistedFilter(Room room, Post post, int reasonId) {
		this.room = room;
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
		
		if (post.getTitle() != null) {
			LOGGER.debug(StringUtils.difference(post.getLastTitle(), post.getTitle()));
			blacklitedWordsTitle = CheckUtils.checkForBlackListedWords(StringUtils.difference(post.getLastTitle(), post.getTitle()), post.getPostType());
		}
		
		if (post.getBody() != null) {
			LOGGER.debug(StringUtils.difference(post.getLastTitle(), post.getTitle()));
			blacklitedWordsBody = CheckUtils.checkForBlackListedWords(StringUtils.difference(post.getLastBody(), post.getBody()), post.getPostType());
		}
		
		if (post.getComment() != null) {
			LOGGER.debug(StringUtils.difference(post.getLastTitle(), post.getTitle()));
			blacklitedWordsEditSummary = CheckUtils.checkForBlackListedWords(post.getComment(), post.getPostType());
		}
		
		return getScore()>0;
	}
	
	@Override
	public double getScore() {
		return this.blacklitedWordsTitle.size() + this.blacklitedWordsBody.size() + this.blacklitedWordsEditSummary.size();
	}

	@Override
	public String getFormattedReasonMessage() {
		String message = "";
		
		try {
			if (this.blacklitedWordsTitle.size()>0) {
				message += "**Title contains blacklisted " + (this.blacklitedWordsTitle.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsTitle() + " ";
			}
			
			if (this.blacklitedWordsBody.size()>0) {
				message += "**Body contains blacklisted " + (this.blacklitedWordsBody.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsBody() + " ";
			}
		
			if (this.blacklitedWordsEditSummary.size()>0) {
				message += "**Edit summary contains blacklisted " + (this.blacklitedWordsEditSummary.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsComment() + " ";
			}
		} catch (Exception e)
		{
			LOGGER.info("Failed to get formatted reason message.", e);
		}
		return message.trim();
	}

	private String getBlacklistedWordsTitle() {
		String words = "";
		
		Iterator iterator = this.blacklitedWordsTitle.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Integer, String> word = (Map.Entry<Integer, String>)iterator.next();
			words += word.getValue() + ", ";
		}

		return words.substring(0, words.trim().length()-1);
	}
	
	private String getBlacklistedWordsBody() {
		String words = "";
		
		Iterator iterator = this.blacklitedWordsBody.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Integer, String> word = (Map.Entry<Integer, String>)iterator.next();
			words += word.getValue() + ", ";
		}

		return words.substring(0, words.trim().length()-1);
	}
		
	private String getBlacklistedWordsComment() {
		String words = "";
		
		Iterator iterator = this.blacklitedWordsEditSummary.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Integer, String> word = (Map.Entry<Integer, String>)iterator.next();
			words += word.getValue() + ", ";
		}

		return words.substring(0, words.trim().length()-1);
	}

	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}
	
	private List<Integer> getCaughtBlacklistedWordIds() {
		List<Integer> blacklistedWordIds = new ArrayList<Integer>();
		
		try {
			Iterator iterator = this.blacklitedWordsTitle.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<Integer, String> blackListedWord = (Map.Entry<Integer, String>)iterator.next();
				blacklistedWordIds.add(blackListedWord.getKey());
			}
		} catch (Exception e) {
			LOGGER.info("Failed to get Ids from blacklistedWordsTitle.", e);
		}
		
		try {
			Iterator iterator = this.blacklitedWordsBody.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<Integer, String> blackListedWord = (Map.Entry<Integer, String>)iterator.next();
				blacklistedWordIds.add(blackListedWord.getKey());
			}
		} catch (Exception e) {
			LOGGER.info("Failed to get Ids from blacklitedWordsBody.", e);
		}
		
		try {
			Iterator iterator = this.blacklitedWordsEditSummary.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<Integer, String> blackListedWord = (Map.Entry<Integer, String>)iterator.next();
				blacklistedWordIds.add(blackListedWord.getKey());
			}
		} catch (Exception e) {
			LOGGER.info("Failed to get Ids from blacklitedWordsEditSummary.", e);
		}
		
		return blacklistedWordIds;
	}

	@Override
	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), this.reasonId, this.getScore());
		this.getCaughtBlacklistedWordIds().stream().forEach(id -> {
			DatabaseUtils.storeCaughtBlacklistedWord(this.post.getPostId(), this.post.getRevisionNumber(), this.room.getRoomId(), id);	
		});
	}
}
