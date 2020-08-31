package bugs.stackoverflow.belisarius.utils;

import bugs.stackoverflow.belisarius.filters.*;
import bugs.stackoverflow.belisarius.filters.Filter.Severity;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.models.VandalisedPost.Feedback;
import bugs.stackoverflow.belisarius.services.HiggsService;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.PingMessageEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.client.ApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostUtils {
	
	public static boolean postBeenEdited(JsonElement post) {
		if (post.getAsJsonObject().has("last_edit_date")) {
			long lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();
			long lastEditDate = post.getAsJsonObject().get("last_edit_date").getAsLong();
			return (lastActivityDate == lastEditDate);
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

	
	public static Post getPost(JsonObject post, String site){

        Post np = new Post();

        np.setPostId(post.get("post_id").getAsInt());
        
        np.setRevisionNumber(post.get("revision_number").getAsInt());
        np.setCreationDate(post.get("creation_date").getAsLong());

        np.setRevisionUrl("https://" + site + ".com/revisions/" + post.get("post_id").getAsString() + "/" + post.get("revision_number").getAsString());
        np.setAllRevisionsUrl("https://" + site + ".com/posts/" + post.get("post_id").getAsString() + "/revisions");
        
        if (post.has("title")) {
	        np.setTitle(post.get("title").getAsString());
        }
        
        if (post.has("last_title")) {
            np.setLastTitle(post.get("last_title").getAsString());
        }
        
        if (post.has("body")) {
	        np.setBody(post.get("body").getAsString());
        }
        
        if (post.has("last_body")) {
	        np.setLastBody(post.get("last_body").getAsString());
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
    static Post getPost(int postId, long creationDate, int revisionId, String title, String lastTitle, String body, String lastBody, boolean isRollback,
                        String postType, String comment, int ownerId, String site){

        Post np = new Post();

        np.setPostId(postId);
        np.setCreationDate(creationDate);
        np.setRevisionNumber(revisionId);
        np.setRevisionUrl("https://" + site + ".com/revisions/" + String.valueOf(postId) + "/" + String.valueOf(revisionId));
        np.setAllRevisionsUrl("https://" + site + ".com/posts/" + String.valueOf(postId) + "/revisions");
        np.setTitle(title);
        np.setLastBody(lastTitle);
        np.setBody(body);
        np.setLastBody(lastBody);
        np.setIsRollback(isRollback);
        np.setPostType(postType);
        np.setComment(comment);

        SOUser user = new SOUser();
        user.setUserId(ownerId);
        np.setUser(user);
        
        return np;
    }
	
	static void storeFeedback(Room room, PingMessageEvent event, Feedback feedback) {
		long repliedTo = event.getParentMessageId();
		Message repliedToMessage = room.getMessage(repliedTo);
		
		long postId = getPostIdFromMessage(repliedToMessage.getPlainContent().trim());
		int revisionNumber = getRevisionNumberFromMessage(repliedToMessage.getPlainContent().trim());

		DatabaseUtils.storeFeedback(postId, revisionNumber, room.getRoomId(), feedback.toString(), event.getMessage().getUser().getId());

		try {
            int higgsId = DatabaseUtils.getHiggsId(postId, revisionNumber, event.getRoomId());
            HiggsService.getInstance().sendFeedback(higgsId, (int) event.getMessage().getUser().getId(), feedback);
        }
        catch (ApiException e) {
            e.printStackTrace();
        }
	}
	
    public static boolean checkVandalisedPost(Room room, Post post) {
        return DatabaseUtils.checkVandalisedPostExists(post.getPostId(), post.getRevisionNumber(), room.getRoomId());
    }
	
	public static void storeVandalisedPost(Room room, VandalisedPost vandalisedPost, int higgsId) {
		Post post = vandalisedPost.getPost();
		DatabaseUtils.storeVandalisedPost(post.getPostId(), post.getCreationDate(), post.getRevisionNumber(), room.getRoomId(), post.getUser().getUserId(), post.getTitle(), post.getLastTitle(), post.getBody(), post.getLastBody(),
				                          post.getIsRollback(), post.getPostType(), post.getComment(), post.getSite(), vandalisedPost.getSeverity(), higgsId);
		
		
		
	}
	
	private static long getPostIdFromMessage(String message) {
        message = message.split("//stackoverflow.com/posts/")[1];
        return Long.parseLong(message.substring(0,message.indexOf("/")));
	}
	
	private static int getRevisionNumberFromMessage(String message) {
        return Integer.parseInt(message.substring(message.length() - 2, message.length() - 1));
	}
	 
    public static VandalisedPost getVandalisedPost(Room room, Post post) {
        List<Filter> filters = new ArrayList<Filter>(){{
            add(new BlacklistedFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(BlacklistedFilter.class.getName()))));
            add(new VeryLongWordFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(VeryLongWordFilter.class.getName()))));
            add(new CodeRemovedFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(CodeRemovedFilter.class.getName()))));
            add(new TextRemovedFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(TextRemovedFilter.class.getName()))));
            add(new FewUniqueCharactersFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(FewUniqueCharactersFilter.class.getName()))));
            add(new OffensiveWordFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(OffensiveWordFilter.class.getName()))));
            add(new RepeatedWordFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(RepeatedWordFilter.class.getName()))));
        }};
       
        Severity severity = null;
       
        HashMap<String, Double> formattedReasonMessages = new HashMap<>();
        HashMap<String, Double> reasonNames = new HashMap<>();
        for(Filter filter: filters){
            if(filter.isHit()){
            	filter.storeHit();
            	formattedReasonMessages.put(filter.getFormattedReasonMessage(), filter.getScore());
                reasonNames.put(filter.getReasonName(), filter.getScore());
            	severity = filter.getSeverity();
            }
        }

        return new VandalisedPost(post, formattedReasonMessages, severity, reasonNames);
    }
}