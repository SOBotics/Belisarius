package bugs.stackoverflow.belisarius.models;

import java.util.HashMap;
import java.util.Map;

public class PostReport {
    Post post;
    private Map<String, Double> reasons = new HashMap<String, Double>();

    public PostReport(Post post, Map<String, Double> reasons) {
        this.post = post;
        this.reasons = reasons;
    }

    public Post getPost() {
        return post;
    }

    public Map<String, Double> getReasons() {
        return this.reasons;
    }

}
