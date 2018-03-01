package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;

public class CodeRemovedFilter implements Filter {

	private Post post;
	private int reasonId;
	
	public CodeRemovedFilter(Post post, int reasonId) {
		this.post = post;
		this.reasonId = reasonId;
	}
	
	@Override
	public boolean isHit() {
		if (post.getLastBody() != null && post.getBody() != null) {
			return !CheckUtils.checkIfNoCodeBlock(post.getLastBody()) && CheckUtils.checkIfNoCodeBlock(post.getBody());
		}
		return false;
	}
	
	@Override
	public double getScore() {
		return 1.0;
	}

	@Override
	public String getFormattedReasonMessage() {
		return "**Code removed**";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.MEDIUM;
	}

	@Override
	public void storeHit() {
		DatabaseUtils.storeReasonCaught(this.post.getPostId(), this.post.getRevisionNumber(), this.reasonId, this.getScore());
	}
}