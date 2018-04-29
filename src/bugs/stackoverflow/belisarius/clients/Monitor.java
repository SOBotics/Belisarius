package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.finders.VandalismFinder;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import fr.tunaki.stackoverflow.chat.Room;
import io.swagger.client.ApiException;

public class Monitor {

	public void run(Room room, List<Post> posts, boolean outputMessage) {
		
		try {
			for (Post post : posts) {
		        if (post.getRevisionNumber() != 1 && !post.getIsRollback()) {
		        	VandalisedPost vandalisedPost = getVandalisedPost(room, post);
		        	if (vandalisedPost != null && vandalisedPost.getSeverity() != null) {
		        		if(!PostUtils.checkVandalisedPost(room, vandalisedPost))
		        		{
                            int higgsId = HiggsService.getInstance().registerVandalisedPost(vandalisedPost);
		        			PostUtils.storeVandalisedPost(room, vandalisedPost, higgsId);
		        			if (outputMessage) {
								sendVandalismFoundMessage(room, post, vandalisedPost, higgsId);
							}
		        		}
		        	}
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void runOnce(Room room, Post post) {
		
		try {
	        if (post.getRevisionNumber() != 1 && !post.getIsRollback()) {
	        	VandalisedPost vandalisedPost = getVandalisedPost(room, post);
	        	if (vandalisedPost != null && vandalisedPost.getSeverity() != null) {
	        		sendVandalismFoundMessage(room, post, vandalisedPost);
	        	}
	        	else
	        	{
	        		sendNoVandalismFoundMessage(room, post);
	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void runOnceLocal(Room room, Post post) {
		
		try {
	        if (post.getRevisionNumber() != 1 && !post.getIsRollback()) {
	        	VandalisedPost vandalisedPost = getVandalisedPost(room, post);
	        	if (vandalisedPost != null && vandalisedPost.getSeverity() != null) {
	        		sendVandalismFoundMessage(room, post, vandalisedPost);
	        	}
	        	else
	        	{
	        		sendNoVandalismFoundMessage(room, post);
	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private VandalisedPost getVandalisedPost(Room room, Post post) {
		 try {
			  {
		         VandalismFinder vandalismFinder = new VandalismFinder(room, post);
		         return vandalismFinder.findReasons();
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return null;
	}
		
	private void sendVandalismFoundMessage(Room room, Post post, VandalisedPost vandalisedPost) {
		String message = "[ [Belisarius](" + Belisarius.readMe + ") ]";
		message += " [tag:" + post.getPostType().toLowerCase() + "] ";
		message += " [tag:severity-" + vandalisedPost.getSeverity() +  "]";
		message += " Potentially harmful edit found. Reason: " + vandalisedPost.getReasonMessage();
		message += " [All revisions](" + post.getAllRevisionsUrl() + ").";
		message += " Revision: [" + post.getRevisionUrl() + ").";
		room.send(message);
	}

	private void sendVandalismFoundMessage(Room room, Post post, VandalisedPost vandalisedPost, int higgsId) throws ApiException {
		String message = "[ [Belisarius](" + Belisarius.readMe + ") ]";
		message += "[ [Hippo](" + HiggsService.getInstance().getUrl()  + "/report/" + String.valueOf(higgsId) + ") ]";
		message += " [tag:" + post.getPostType().toLowerCase() + "] ";
		message += " [tag:severity-" + vandalisedPost.getSeverity() +  "]";
		message += " Potentially harmful edit found. Reason: " + vandalisedPost.getReasonMessage();
		message += " [All revisions](" + post.getAllRevisionsUrl() + ").";
		message += " Revision: [" + post.getRevisionUrl() + ").";
		room.send(message);
	}
	
	private void sendNoVandalismFoundMessage(Room room, Post post) {
		String message = "[ [Belisarius](" + Belisarius.readMe + ") ]";
		message += " [tag:" + post.getPostType().toLowerCase() + "] No issues have been found.";
		message += " [All revisions](" + post.getAllRevisionsUrl() + ").";
		message += " Revision: [" + post.getRevisionUrl() + ").";
		room.send(message);
	}
		
}
