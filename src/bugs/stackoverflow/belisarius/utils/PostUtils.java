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

import org.jsoup.Jsoup;

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
		if (post.getAsJsonObject().has("last_edit_date")) {
			long lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
			long lastEditDate = post.getAsJsonObject().get("last_edit_date").getAsLong();
			if(lastActivityDate == lastEditDate) {
				return true;
			}
		}
		
		return false; 
	}
	
	private boolean editorAlsoOwner(JsonElement post) {
		try{
			JsonObject ownerJSON = post.getAsJsonObject().get("owner").getAsJsonObject();
			JsonObject editorJSON = post.getAsJsonObject().get("last_editor").getAsJsonObject();
			
			long ownerId = ownerJSON.get("user_id").getAsLong();
			long editorId = editorJSON.get("user_id").getAsLong();
			
			if (ownerId == editorId) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return false;
	}
	
	public Post getLastestRevisionByPostId(int postId) {
		Post revision = new Post();
		JsonObject postsJson;
		try {
			postsJson = apiService.getLastestRevisionByPostId(postId);
			if (postsJson.has("items")) {
				revision = PostUtils.getPost(postsJson.get("items").getAsJsonArray().get(0).getAsJsonObject());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return revision;
	}
	

	public static Post getPost(JsonObject post){

        Post np = new Post();

        np.setPostID(post.get("post_id").getAsInt());

    	//some revisions don't have a revision number, e.g. vote based revisions (undeleted)
        if (post.has("revision_number")) {
        	np.setRevisionNumber(post.get("revision_number").getAsInt());
        } else {
        	np.setRevisionNumber(1);
        }
        
        if (post.has("body")) {
	        np.setBody(Jsoup.parse(post.get("body").getAsString()).text());
        }
        
        if (post.has("last_body")) {
	        np.setLastBody(Jsoup.parse(post.get("last_body").getAsString()).text());
        }
        
        if (post.has("title")) {
	        np.setTitle(post.get("title").getAsString());
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