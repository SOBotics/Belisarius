package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.finders.VandalismFinder;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import fr.tunaki.stackoverflow.chat.Room;

public class Monitor {

	public void run(Room chatroom, List<Post> posts, String site, String siteUrl) {
		
		try {
			for (Post post : posts) {
		        if (post.getRevisionNumber()!= 1 && post.getIsRollback() == false) {
		        	VandalisedPost vandalisedPost = getVandalisedPost(post);
		        	if (vandalisedPost.getSeverity() != null) {
		        		if(!PostUtils.checkVandalisedPost(vandalisedPost))
		        		{
		        			PostUtils.storeVandalisedPost(vandalisedPost);
		        			sendVandalismFoundMessage(chatroom, post, vandalisedPost);
		        		}
		        	}
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void runOnce(Room chatroom, Post post, String site, String siteUrl) {
		
		try {
	        if (post.getRevisionNumber()!= 1 && post.getIsRollback() == false) {
	        	VandalisedPost vandalisedPost = getVandalisedPost(post);
	        	if (vandalisedPost.getSeverity() != null) {
	        		sendVandalismFoundMessage(chatroom, post, vandalisedPost);
	        	}
	        	else
	        	{
	        		sendNoVandalismFoundMessage(chatroom, post);
	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private VandalisedPost getVandalisedPost(Post post) {
		 try {
			  {
		         VandalismFinder vandalismFinder = new VandalismFinder(post);
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
		message += " [Link to revisions](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions).";
		message += " Revision: " + String.valueOf(post.getRevisionNumber());
		room.send(message);
	}
	
	private void sendNoVandalismFoundMessage(Room room, Post post) {
		String message = "[ [Belisarius](" + Belisarius.readMe + ") ]";
		message += " [tag:" + post.getPostType().toLowerCase() + "] No issues have been found.";
		message += " [Link to revisions](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions).";
		message += " Revision: " + String.valueOf(post.getRevisionNumber());
		room.send(message);
	}
		
}
