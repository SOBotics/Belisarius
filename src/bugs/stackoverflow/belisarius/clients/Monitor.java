package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.finders.VandalismFinder;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import fr.tunaki.stackoverflow.chat.Room;

public class Monitor {

	public void run(Room room, List<Post> posts, String site, String siteUrl) {
		
		try {
			for (Post post : posts) {
		        if (post.getRevisionNumber()!= 1 && post.getIsRollback() == false) {
		        	VandalisedPost vandalisedPost = getVandalisedPost(room, post);
		        	if (vandalisedPost.getSeverity() != null) {
		        		if(!PostUtils.checkVandalisedPost(room, vandalisedPost))
		        		{
		        			PostUtils.storeVandalisedPost(room, vandalisedPost);
		        			sendVandalismFoundMessage(room, post, vandalisedPost);
		        		}
		        	}
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void runOnce(Room room, Post post, String site, String siteUrl) {
		
		try {
	        if (post.getRevisionNumber()!= 1 && post.getIsRollback() == false) {
	        	VandalisedPost vandalisedPost = getVandalisedPost(room, post);
	        	if (vandalisedPost.getSeverity() != null) {
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
