package bugs.stackoverflow.belisarius.filters;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.CheckUtils;

public class CodeRemovedFilter implements Filter {

	private Post post;
	
	public CodeRemovedFilter(Post post) {
		this.post = post;
	}
	
	@Override
	public boolean isHit() {
		if (post.getLastBody() != null && post.getBody() != null) {
			if (!CheckUtils.checkIfNoCodeBlock(post.getLastBody()) && CheckUtils.checkIfNoCodeBlock(post.getBody())) {
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
		return "**Code removed**";
	}

}
