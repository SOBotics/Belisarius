package bugs.stackoverflow.belisarius.finders;


import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.utils.PostUtils;

public class VandalismFinder {

	private Post post;
	
	public VandalismFinder(Post post) {
		this.post = post;
	}
	
	public VandalisedPost findReasons() {
		return PostUtils.getVandalisedPost(this.post);
	}
	
}
