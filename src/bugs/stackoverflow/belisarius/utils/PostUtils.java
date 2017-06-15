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
			long lastEditDate = 0;
			long maxActivityDate = lastPostTime;
			int page = 1;
			do {
			
				postsJSON = apiService.getPostIdsByActivityDesc(page);
	            for (JsonElement post : postsJSON.get("items").getAsJsonArray()) {
	            	lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
	            	if (post.getAsJsonObject().has("last_edit_date")) {
		            	lastEditDate = post.getAsJsonObject().get("last_edit_date").getAsLong();
		            	if(lastActivityDate == lastEditDate) {
		            		int postId = post.getAsJsonObject().get("post_id").getAsInt();
		            		if (!postIds.contains(postId)) {
		            			postIds.add(postId);
		            		}
		            	}
	            	}
	            	if (maxActivityDate < lastActivityDate) {
	            		maxActivityDate = lastActivityDate;
	            	}
	            }
	            page++;
			} while (lastPostTime < lastActivityDate && page<10);
		
           	MonitorService.lastPostTime = maxActivityDate;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return postIds;
	}
	
	public Post getLastRevisionByPostId(int postId) {
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
	

	public static Post getPost(JsonObject post){

        Post np = new Post();

        np.setPostID(post.get("post_id").getAsInt());
        
        if (post.has("body")) {
	        np.setBody(post.get("body").getAsString());
	        np.setLastBody(post.get("last_body").getAsString());
        }
        
        if (post.has("title")) {
	        np.setBody(post.get("title").getAsString());
	        np.setLastBody(post.get("last_title").getAsString());
        }
        
        np.setIsRollback(post.get("is_rollback").getAsBoolean());
        np.setPostType(post.get("post_type").getAsString());
        
        JsonObject editorJSON = post.get("user").getAsJsonObject();
        SOUser editor = new SOUser();
       
        try{
        	editor.setReputation(editorJSON.get("reputation").getAsLong());
        	editor.setUsername(JsonUtils.escapeHtmlEncoding(editorJSON.get("display_name").getAsString()));
        	editor.setUserType(editorJSON.get("user_type").getAsString());
        	editor.setUserId(editorJSON.get("user_id").getAsInt());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        np.setEditor(editor);
        
        return np;
    }
	
}