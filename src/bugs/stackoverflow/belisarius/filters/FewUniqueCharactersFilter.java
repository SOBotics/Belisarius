package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;

public class FewUniqueCharactersFilter implements Filter {

	private Post post;
	private String listedWord;
	
	public FewUniqueCharactersFilter(Post post) {
		this.post = post;
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
	public String getDescription() {
		return "**Few unique characters detected - ** " + this.listedWord;
	}

	@Override
	public Severity getSeverity() {
		return Severity.HIGH;
	}

}
