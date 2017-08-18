package bugs.stackoverflow.belisarius.filters;

import java.util.ArrayList;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.PathUtils;

public class OffensiveWordFilter implements Filter {

	private Post post;
	
	private List<String> offensiveWords = new ArrayList<String>();
	
	public OffensiveWordFilter(Post post) {
		this.post = post;
	}
	
	@Override
	public boolean isHit() {

		if (this.post.getComment() != null) {
			offensiveWords = CheckUtils.checkForOffensiveWords(this.post.getComment(), PathUtils.offensiveEditSummaryFile);
		}
		
		return false;
	}

	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return offensiveWords.size();
	}

	@Override
	public String getDescription() {
		return "**Comment contains offensive " + (this.offensiveWords.size() > 0 ? "words" : "word") + " - ** " + getOffensiveWords();
	}
	
	private String getOffensiveWords() {
		String words = "";
		
		for (String word : this.offensiveWords) {
			words += word + ", ";
		}
		
		return words.substring(0, words.trim().length()-1);
	}

	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
	}

}
