package bugs.stackoverflow.belisarius.filters;

import java.util.*;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;

public class RepeatedWordFilter implements Filter {

	private Post post;
	private Set<String> repeatedWords = new HashSet<String>();
	
	public RepeatedWordFilter(Post post) {
		this.post = post;
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
	public String getDescription() {
		String message = "";
		
		if (this.repeatedWords.size()>0) {
			message += "**Question contains repeated " + (this.repeatedWords.size()>1 ? "words" : "word") + " - ** " + getRepeatedWords() + " ";
		}
		
		return message.trim();
	}
	
	private String getRepeatedWords() {
		String words = "";
		
		for (String word : this.repeatedWords) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}

	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
	}
	
}
