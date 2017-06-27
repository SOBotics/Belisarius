package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.PathUtils;

public class BlacklistedFilter implements Filter {

	private Post post;
	private List<String> blacklistedWords = new ArrayList<String>();
	
	public BlacklistedFilter(Post post) {
		this.post = post;
	}
	
	@Override
	public boolean isHit() {
		
		if (post.getPostType().equals(Post.postTypeQuestion)) {
			if (post.getTitle() != null) {
				blacklistedWords = CheckUtils.checkForBlackListedWords(post.getTitle(), PathUtils.blacklistTitleFile);
			}
		
			if (post.getBody() != null) {
				blacklistedWords = CheckUtils.checkForBlackListedWords(post.getBody(), PathUtils.blacklistQuestionFile);
			}
		} else if (post.getPostType().equals(Post.postTypeAnswer)) {
			if (post.getBody() != null) {
				blacklistedWords = CheckUtils.checkForBlackListedWords(post.getBody(), PathUtils.blacklistAnswerFile);
			}
		}
		
		return this.blacklistedWords.size()>0;

	}

	@Override
	public double getScore() {
		return this.blacklistedWords.size();
	}

	@Override
	public String getDescription() {
		return "**Contains blacklisted " + (this.blacklistedWords.size()>1 ? "words" : "word") + ":** " + getBlacklistedWords();
	}

	private String getBlacklistedWords() {
		String words = "";
		
		for (String word : this.blacklistedWords) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}
	
}
