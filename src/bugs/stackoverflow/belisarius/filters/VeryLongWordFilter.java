package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class VeryLongWordFilter implements Filter {

	private Post post;
	private int reasonId;
	private String listedWord;
	
	public VeryLongWordFilter(Post post, int reasonId) {
		this.post = post;
		this.reasonId = reasonId;
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
	public String getFormattedReasonMessage() {
		return "**Contains very long word - ** " + this.listedWord.substring(0, 50) + "...";
	}

	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}

	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.reasonId, this.getScore());
	}

}
