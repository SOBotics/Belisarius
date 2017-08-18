package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.filters.*;
import bugs.stackoverflow.belisarius.filters.Filter.Severity;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.services.*;
import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.User;
import fr.tunaki.stackoverflow.chat.event.PingMessageEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class PostUtils {
	
	private ApiService apiService;
	
	public PostUtils() {
		
		PropertyService ps = new PropertyService();
		apiService = new ApiService(ps.getSite());
	}
	

	public static boolean postBeenEdited(JsonElement post) {
		if (post.getAsJsonObject().has("last_edit_date")) {
			long lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
			long lastEditDate = post.getAsJsonObject().get("last_edit_date").getAsLong();
			if(lastActivityDate == lastEditDate) {
				return true;
			}
		}
		
		return false; 
	}
	
	public static boolean editorAlsoOwner(JsonElement post) {
		try{
			if (!post.getAsJsonObject().has("owner")) {
				return true;
			} else {
				JsonObject ownerJSON = post.getAsJsonObject().get("owner").getAsJsonObject();
				JsonObject editorJSON = post.getAsJsonObject().get("last_editor").getAsJsonObject();
				
				long ownerId = ownerJSON.get("user_id").getAsLong();
				long editorId = editorJSON.get("user_id").getAsLong();
				
				if (ownerId == editorId) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
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
	        np.setTitle(post.get("title").getAsString());
        }
        
        if (post.has("last_title")) {
            np.setLastBody(post.get("last_title").getAsString());
        }
        
        np.setIsRollback(post.get("is_rollback").getAsBoolean());
        np.setPostType(post.get("post_type").getAsString());
        
        if (post.has("comment")) {
        	np.setComment(post.get("comment").getAsString());
        }
        
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
	
	public static void storeFeedback(Room room, PingMessageEvent event, String feedbackType) {
		long repliedTo = event.getParentMessageId();
		Message repliedToMessage = room.getMessage(repliedTo);
		String reason = repliedToMessage.getPlainContent().split("Reason:")[1].substring(0,repliedToMessage.getPlainContent().split("Reason:")[1].indexOf(";")).replace("*","").trim();
		String score = repliedToMessage.getPlainContent().split("Score:")[1].replace("*","").trim().substring(0,3);
		String postId = getPostIdFromMessage(repliedToMessage.getPlainContent().trim());
		String postType = repliedToMessage.getPlainContent().contains("[tag:question]") ? "Q" : "A";
		handleFeedback(event.getMessage().getUser(), postId, postType, feedbackType, reason, score);
	}
	
	public static String getPostIdFromMessage(String message) {
        message = message.split("//stackoverflow.com/posts/")[1];
        return message.substring(0,message.indexOf("/"));
	}
	 
	public static void handleFeedback(User user, String postId, String postType, String feedbackType, String reason, String score) {
		String outputCSVLogFile = "./logs/output.csv";
		try {
			String loggedAsTp = FileUtils.readLineFromFileStartswith(outputCSVLogFile, "tp," + postId);
			String loggedAsFp = FileUtils.readLineFromFileStartswith(outputCSVLogFile, "fp," + postId);
			
			if (loggedAsTp == null || loggedAsFp == null) {
				FileUtils.appendToFile(outputCSVLogFile, postId + "," + postType + "," + feedbackType + "," + reason + "," + score);
			}
		} catch (IOException e){
			e.printStackTrace();
	    }
	}
	
    public static VandalisedPost getVandalisedPost(Post post) {
        List<Filter> filters = new ArrayList<Filter>(){{
            add(new BlacklistedFilter(post));
            add(new VeryLongWordFilter(post));
            add(new CodeRemovedFilter(post));
            add(new TextRemovedFilter(post));
            add(new FewUniqueCharactersFilter(post));
            add(new OffensiveWordFilter(post));
            add(new RepeatedWordFilter(post));
        }};
       
        Severity severity = null;
       
        Map<String, Double> reasons = new HashMap<String, Double>();
        for(Filter filter: filters){
            if(filter.isHit()){
            	reasons.put(filter.getDescription(), filter.getScore());
            	if (severity == null) {
            		severity = filter.getSeverity();
            	} else if (severity == Severity.LOW && (filter.getSeverity() == Severity.MEDIUM || filter.getSeverity() == Severity.HIGH)) {
            		severity = filter.getSeverity();
            	} else if (severity == Severity.MEDIUM && filter.getSeverity() == Severity.HIGH) {
            		severity = filter.getSeverity();
            	}
            }
        }

        return new VandalisedPost(post, reasons, severity);
    }
	
}