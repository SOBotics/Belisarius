package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.SOUser;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostUtils {
	
	private ApiService apiService;
	
	public PostUtils() {
		
		PropertyService ps = new PropertyService();
		apiService = new ApiService(ps.getSite());
	}
	
	public List<Integer> getPostIdsByActivity(long lastPostTime) {
		List<Integer> postIds = new ArrayList<Integer>();
		
		JsonObject postsJSON = null;
		try {
			long lastActivityDate = 0;
			long maxActivityDate = lastPostTime;
			int page = 1;
			do {
				postsJSON = apiService.getPostIdsByActivityDesc(page);
	            for (JsonElement post : postsJSON.get("items").getAsJsonArray()) {
            		lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
	            	
            		if (postBeenEdited(post) && editorAlsoOwner(post) && lastPostTime < lastActivityDate) {
	            		int postId = post.getAsJsonObject().get("post_id").getAsInt();
	            		if (!postIds.contains(postId)) {
	            			postIds.add(postId);
	            		}
	            	}
            		
	                if (maxActivityDate < lastActivityDate) {
	            		maxActivityDate = lastActivityDate;
	            	}
	            }
	            page++;
			} while (lastPostTime < lastActivityDate & page<10);
		
           	MonitorService.lastPostTime = maxActivityDate;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return postIds;
	}
	
	private boolean postBeenEdited(JsonElement post) {
		
		boolean postBeenEdited = false;
		
		if (post.getAsJsonObject().has("last_edit_date")) {
			long lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
			long lastEditDate = post.getAsJsonObject().get("last_edit_date").getAsLong();
			if(lastActivityDate == lastEditDate) {
				postBeenEdited = true;
			}
		}
		
		return postBeenEdited; 
	}
	
	private boolean editorAlsoOwner(JsonElement post) {
		
		boolean editorAlsoOwner = false;
		
		JsonObject ownerJSON = post.getAsJsonObject().get("owner").getAsJsonObject();
		JsonObject editorJSON = post.getAsJsonObject().get("last_editor").getAsJsonObject();
		
		long ownerId = ownerJSON.get("user_id").getAsLong();
		long editorId = editorJSON.get("user_id").getAsLong();
		
		if (ownerId == editorId) {
			editorAlsoOwner = true;
		}
		
		return editorAlsoOwner;
	}
	
	public Post getLastestRevisionByPostId(int postId) {
		Post revision = new Post();
		
		try {
			JsonObject postsJson = apiService.getLastestRevisionByPostId(postId);
			revision = PostUtils.getPost(postsJson.get("items").getAsJsonArray().get(0).getAsJsonObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return revision;
	}
	

	public static Post getPost(JsonObject post){

        Post np = new Post();

        np.setPostID(post.get("post_id").getAsInt());
        
        np.setRevisionNumber(post.get("revision_number").getAsInt());
        
        if (post.has("body")) {
	        np.setBody(post.get("body").getAsString());
        }
        
        if (post.has("last_body")) {
	        np.setLastBody(post.get("last_body").getAsString());
        }
        
        if (post.has("title")) {
	        np.setBody(post.get("title").getAsString());
        }
        
        if (post.has("last_title")) {
            np.setLastBody(post.get("last_title").getAsString());
        }
        
        np.setIsRollback(post.get("is_rollback").getAsBoolean());
        np.setPostType(post.get("post_type").getAsString());
        
        JsonObject userJSON = post.get("user").getAsJsonObject();
        SOUser user = new SOUser();
       
        try{
        	user.setReputation(userJSON.get("reputation").getAsLong());
        	user.setUsername(JsonUtils.escapeHtmlEncoding(userJSON.get("display_name").getAsString()));
        	user.setUserType(userJSON.get("user_type").getAsString());
        	user.setUserId(userJSON.get("user_id").getAsInt());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        np.setUser(user);
        
        return np;
    }
	
}