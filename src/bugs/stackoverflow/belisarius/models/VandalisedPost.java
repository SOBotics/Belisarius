package bugs.stackoverflow.belisarius.models;

import java.util.List;
import java.util.ArrayList;

public class VandalisedPost {

	private Post post;
	private List<String> reasons = new ArrayList<String>();
	
	public VandalisedPost(Post post) {
		this.post = post;
	}
	
	public Post getPost() {
		return this.post;
	}
	
	public void addReason(String reason) {
		if (!this.reasons.contains(reason)) {
			this.reasons.add(reason);
		}
	}
	
	public List<String> getReasons() {
		return this.reasons;
	}
	
}
