package bugs.stackoverflow.belisarius.models;

import java.util.Map;
import java.util.HashMap;

public class VandalisedPost {

	private Post post;
	private Map<String, Double> reasons = new HashMap<String, Double>();
	
	public VandalisedPost(Post post, Map<String, Double> reasons) {
		this.post = post;
		this.reasons = reasons;
	}
	
	public Post getPost() {
		return this.post;
	}
	
    public String getReasons() {
    	String reasons = "";
    	double score = 0;
    	
    	for (String key : this.reasons.keySet()) {
    		reasons += key + ", ";
    	}
    	
    	for (double value : this.reasons.values()) {
    		score += value;
    	}
    	
    	if (reasons != "" && score > 0) {
    		reasons = reasons.substring(0, reasons.trim().length()-1) + "; Score: " + score;
    	}
    	
    	return reasons;
    }	
}