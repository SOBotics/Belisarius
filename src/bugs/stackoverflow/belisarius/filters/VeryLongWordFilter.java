package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;

public class VeryLongWordFilter implements Filter {

	private Post post;
	private String listedWord;
	
	public VeryLongWordFilter(Post post) {
		this.post = post;
	}
	
	@Override
	public boolean isHit() {
		this.listedWord = "";
        if (post.getBody() != null) {
        	this.listedWord = CheckUtils.checkForLongWords(post.getBody());
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
		return "**Contains very long word:** " + this.listedWord.substring(0, 50) + "...";
	}

}
