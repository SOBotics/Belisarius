package bugs.stackoverflow.belisarius.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.service.ApiService;
import bugs.stackoverflow.belisarius.service.PropertyService;
import bugs.stackoverflow.belisarius.util.PostUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Posts {

	private Instant previousTimestamp;
	private ApiService apiService;
	
	public Posts() {
		this.previousTimestamp = Instant.now().minusSeconds(1);
		
		PropertyService ps = new PropertyService();
		apiService = new ApiService(ps.getSite());
	}
	
	public List<Integer> getPostIdsByActivity() {
		List<Integer> postIds = new ArrayList<Integer>();
		
		JsonObject postsJSON = null;
		try {
			Instant lastActivity = Instant.now();
			Integer page = 1;
			do {
			
				 postsJSON = apiService.getPostIdsByActivity(previousTimestamp, page);
	            for (JsonElement post : postsJSON.get("items").getAsJsonArray()) {
	            	if (post.getAsJsonObject().has("last_edit_date")) {
	            		postIds.add(post.getAsJsonObject().get("post_id").getAsInt());
	            	}
            		lastActivity = Instant.ofEpochSecond(post.getAsJsonObject().get("last_activity_date").getAsInt());
	            }
	            page++;
			} while (!postsJSON.get("has_more").getAsBoolean() && page < 10);
			
	            if (previousTimestamp.getEpochSecond() < lastActivity.getEpochSecond()) {
	            	previousTimestamp = lastActivity;
	            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return postIds;
	}
	
	public Post getLastRevisionByPostId(Integer postId) {
		Post revision = new Post();
		
		try {
			JsonObject postsJson = apiService.getLastRevisionByPostId(postId);
            for (JsonElement post : postsJson.get("items").getAsJsonArray()) {
            		revision = PostUtils.getPost(post.getAsJsonObject());
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return revision;
	}
	
}
