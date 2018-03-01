package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.filters.*;
import bugs.stackoverflow.belisarius.filters.Filter.Severity;
import bugs.stackoverflow.belisarius.models.*;
import fr.tunaki.stackoverflow.chat.Message;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.PingMessageEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostUtils {
	
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
		
		long postId = getPostIdFromMessage(repliedToMessage.getPlainContent().trim());
		int revisionNumber = getRevisionNumberFromMessage(repliedToMessage.getPlainContent().trim());

		DatabaseUtils.storeFeedback(postId, revisionNumber, feedbackType, event.getMessage().getUser().getId());
	}
	
	public static boolean checkVandalisedPost(VandalisedPost vandalisedPost) {
		return DatabaseUtils.checkVandalisedPostExists(vandalisedPost.getPost().getPostId(), vandalisedPost.getPost().getRevisionNumber());
	}
	
	public static void storeVandalisedPost(VandalisedPost vandalisedPost) {
		Post post = vandalisedPost.getPost();
		DatabaseUtils.storeVandalisedPost(post.getPostId(), post.getRevisionNumber(), post.getUser().getUserId(), post.getTitle(), post.getLastTitle(), post.getBody(), post.getLastBody(),
				                          post.getIsRollback(), post.getPostType(), post.getComment(), post.getSite(), post.getSiteUrl(), vandalisedPost.getSeverity());
		
		
		
	}
	
	public static long getPostIdFromMessage(String message) {
        message = message.split("//stackoverflow.com/posts/")[1];
        return Long.parseLong(message.substring(0,message.indexOf("/")));
	}
	
	public static int getRevisionNumberFromMessage(String message) {
        message = message.split("Revision:")[1];
        return Integer.parseInt(message.trim());
	}
	 
    public static VandalisedPost getVandalisedPost(Post post) {
        List<Filter> filters = new ArrayList<Filter>(){{
            add(new BlacklistedFilter(post, DatabaseUtils.getReasonId(BlacklistedFilter.class.getName().substring(BlacklistedFilter.class.getName().lastIndexOf('.') + 1).trim())));
            add(new VeryLongWordFilter(post, DatabaseUtils.getReasonId(VeryLongWordFilter.class.getName().substring(VeryLongWordFilter.class.getName().lastIndexOf('.') + 1).trim())));
            add(new CodeRemovedFilter(post, DatabaseUtils.getReasonId(CodeRemovedFilter.class.getName().substring(CodeRemovedFilter.class.getName().lastIndexOf('.') + 1).trim())));
            add(new TextRemovedFilter(post, DatabaseUtils.getReasonId(TextRemovedFilter.class.getName().substring(TextRemovedFilter.class.getName().lastIndexOf('.') + 1).trim())));
            add(new FewUniqueCharactersFilter(post, DatabaseUtils.getReasonId(FewUniqueCharactersFilter.class.getName().substring(FewUniqueCharactersFilter.class.getName().lastIndexOf('.') + 1).trim())));
            add(new OffensiveWordFilter(post, DatabaseUtils.getReasonId(OffensiveWordFilter.class.getName().substring(OffensiveWordFilter.class.getName().lastIndexOf('.') + 1).trim())));
            add(new RepeatedWordFilter(post, DatabaseUtils.getReasonId(RepeatedWordFilter.class.getName().substring(RepeatedWordFilter.class.getName().lastIndexOf('.') + 1).trim())));
        }};
       
        Severity severity = null;
       
        HashMap<String, Double> formattedReasonMessages = new HashMap<String, Double>();
        for(Filter filter: filters){
            if(filter.isHit()){
            	filter.storeHit();
            	formattedReasonMessages.put(filter.getFormattedReasonMessage(), filter.getScore());
            	if (severity == null) {
            		severity = filter.getSeverity();
            	} else if (severity == Severity.LOW && (filter.getSeverity() == Severity.MEDIUM || filter.getSeverity() == Severity.HIGH)) {
            		severity = filter.getSeverity();
            	} else if (severity == Severity.MEDIUM && filter.getSeverity() == Severity.HIGH) {
            		severity = filter.getSeverity();
            	}
            }
        }

        return new VandalisedPost(post, formattedReasonMessages, severity);
    }
	
}