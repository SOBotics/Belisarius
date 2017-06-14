package bugs.stackoverflow.belisarius.util;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.SOUser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PostUtils {

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