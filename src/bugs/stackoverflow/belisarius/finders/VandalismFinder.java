package bugs.stackoverflow.belisarius.finders;

import java.util.ArrayList;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.reasonlists.*;
import bugs.stackoverflow.belisarius.reasons.*;

public class VandalismFinder {

	private Post post;
	
	public VandalismFinder(Post post) {
		this.post = post;
	}
	
	public VandalisedPost findReasons() {
		VandalisedPost vandalisedPost = new VandalisedPost(this.post);
    	
    	if (post.getTitle() != null && post.getLastTitle() != null)
    	{
    		for (String reason : getReasons(post.getTitle(), post.getLastTitle(), true, false, 2.0)) {
    			vandalisedPost.addReason(reason);
    		}
    	} 
    	
    	if (post.getBody() != null && post.getLastBody() != null) {
    		for (String reason : getReasons(post.getBody(), post.getLastBody(), false, true, 1.0)) {
    			vandalisedPost.addReason(reason);
    		}
    	}
		
		return vandalisedPost;
	}
	
	private List<String> getReasons(String target, String original, boolean isCheckOnTitle, boolean isCheckOnBody, double quantifier) {
		List<String> reasons = new ArrayList<String>();
		
		if (target != "" && original != "") {
			ReasonList reasonList = new SOBoticsReasonList(target, original, isCheckOnTitle, isCheckOnBody, quantifier);
			for (Reason reason : reasonList.reasons()) {
				if (reason.isHit()) {
					reasons.add(reason.getDescription());
				}
			}
		}
		
		return reasons;
	}
	
}
