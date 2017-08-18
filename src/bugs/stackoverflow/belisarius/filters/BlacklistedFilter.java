package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.PathUtils;

public class BlacklistedFilter implements Filter {

	private Post post;
	private List<String> blacklitedWordsTitle = new ArrayList<String>();
	private List<String> blacklitedWordsQuestion = new ArrayList<String>();
	private List<String> blacklitedWordsAnswer = new ArrayList<String>();
	private List<String> blacklitedWordsEditSummary = new ArrayList<String>();
	
	public BlacklistedFilter(Post post) {
		this.post = post;
	}
	
	@Override
	public boolean isHit() {
		
		if (post.getPostType().equals(Post.postTypeQuestion)) {
			if (post.getTitle() != null) {
				blacklitedWordsTitle = CheckUtils.checkForBlackListedWords(post.getTitle(), PathUtils.blacklistTitleFile);
			}
		
			if (post.getBody() != null) {
				blacklitedWordsQuestion = CheckUtils.checkForBlackListedWords(post.getBody(), PathUtils.blacklistQuestionFile);
			}
			
			if (post.getComment() != null) {
				blacklitedWordsEditSummary = CheckUtils.checkForBlackListedWords(post.getComment(), PathUtils.blacklistQuestionEditSummaryFile);
			}
			
		} else if (post.getPostType().equals(Post.postTypeAnswer)) {
			if (post.getBody() != null) {
				blacklitedWordsAnswer = CheckUtils.checkForBlackListedWords(post.getBody(), PathUtils.blacklistAnswerFile);
			}
			
			if (post.getComment() != null) {
				blacklitedWordsEditSummary = CheckUtils.checkForBlackListedWords(post.getComment(), PathUtils.blacklistAnswerEditSummaryFile);
			}
		}
		
		return getScore()>0;

	}

	@Override
	public double getScore() {
		return this.blacklitedWordsTitle.size() + this.blacklitedWordsQuestion.size() + this.blacklitedWordsAnswer.size() + this.blacklitedWordsEditSummary.size();
	}

	@Override
	public String getDescription() {
		String message = "";
		
		if (this.blacklitedWordsTitle.size()>0) {
			message += "**Title contains blacklisted " + (this.blacklitedWordsTitle.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsTitle() + " ";
		}
		
		if (this.blacklitedWordsQuestion.size()>0) {
			message += "**Question contains blacklisted " + (this.blacklitedWordsQuestion.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsQuestion() + " ";
		}
		
		if (this.blacklitedWordsAnswer.size()>0) {
			message += "**Answer contains blacklisted " + (this.blacklitedWordsAnswer.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsAnswer() + " ";
		}
		
		if (this.blacklitedWordsEditSummary.size()>0) {
			message += "**Edit summary contains blacklisted " + (this.blacklitedWordsEditSummary.size()>1 ? "words" : "word") + ":** " + getBlacklistedWordsComment() + " ";
		}
		return message.trim();
	}

	private String getBlacklistedWordsTitle() {
		String words = "";
		
		for (String word : this.blacklitedWordsTitle) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}
	
	private String getBlacklistedWordsQuestion() {
		String words = "";
		
		for (String word : this.blacklitedWordsQuestion) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}
	
	private String getBlacklistedWordsAnswer() {
		String words = "";
		
		for (String word : this.blacklitedWordsAnswer) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}
	
	private String getBlacklistedWordsComment() {
		String words = "";
		
		for (String word : this.blacklitedWordsEditSummary) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}

	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}
	
}
