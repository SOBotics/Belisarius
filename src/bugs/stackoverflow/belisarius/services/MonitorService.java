package bugs.stackoverflow.belisarius.services;

import bugs.stackoverflow.belisarius.finders.VandalismFinder;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.*;

import java.util.concurrent.*;
import java.util.List;

public class MonitorService {

	private Room room;
	public static long lastPostTime = System.currentTimeMillis()/1000-1*60;
	private ScheduledExecutorService executorService;
	
	private static String readMe = "https://github.com/SOBotics/Belisarius";
	
	private String commands = "    alive          - Test to check if bot is alive or not.\n" +
	                          "    check 'idx'    - Checks a post for potential vandalism.\n" +
			                  "    commands       - Returns this list of commands.\n" +                  
			                  "    help           - Returns description of the bot.\n" +
			                  "    leave          - Asks the bot to leave the room (must be a room owner).\n" +
			                  "    quota          - Returns the current quota\n" +
			                  "    reboot         - Stops and starts the bot (must be a room owner).\n" +
			                  "    stop           - Stops bot (must be a room owner).\n";
	
	public MonitorService(Room room) {
		this.room = room;
		executorService = Executors.newSingleThreadScheduledExecutor();
	}
	
	public void startMonitor() {
		
		room.addEventListener(EventType.MESSAGE_POSTED, event->messagePosted(room, event));
		room.addEventListener(EventType.MESSAGE_REPLY, event->messageReply(room, event));
		room.addEventListener(EventType.USER_MENTIONED, event->userMentioned(room, event));
		 
		room.send("Belisarius started.");
		
		PostUtils postUtils = new PostUtils();
		Runnable monitor = () -> run(room, postUtils);
		executorService.scheduleAtFixedRate(monitor, 0, 1, TimeUnit.MINUTES);
		 
	}

	private void messagePosted(Room room, MessagePostedEvent event) {
		String message = event.getMessage().getPlainContent().trim();
		
		int cp = Character.codePointAt(message, 0);
		if (message.toLowerCase().startsWith("@bots alive")) {
			room.send("Yeah, I'm alive.");
		} else {
			if (cp == 128642 || (cp>=128644 && cp<=128650)) {
				room.send("\uD83D\uDE83");
			}
		}
		
	}
	
	private void messageReply(Room room, MessageReplyEvent event) {
		String message = event.getMessage().getPlainContent().trim();
		System.out.println(message);
	}
	
	private void userMentioned(Room room, UserMentionedEvent event) {
		String message = event.getMessage().getPlainContent().trim();
		
        if (message.toLowerCase().contains("alive")) {
			room.send("Yeah, I'm alive.");
			
		} else if (message.toLowerCase().contains("commands")) {
			room.send(commands);
			
		} else if (message.toLowerCase().contains("check")) {
			try{
				int postId = Integer.parseInt(message.split("check")[1].trim());
				run(room, String.valueOf(postId));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		} else if (message.toLowerCase().contains("help")) {
			room.send("I'm a bot that monitors for potential vandalism on posts.");
			
		} else if (message.toLowerCase().contains("leave")){
			if (room.getUser(event.getUserId()).isRoomOwner()) {
				leaveRoom(room);
			} else {
				room.send("Only room owners can make that call.");
			}
			
		} else if (message.toLowerCase().contains("quota")) {
			room.send("Current quota is " + String.valueOf(ApiService.getQuota() + "."));
			
		} else if (message.toLowerCase().contains("reboot")) {
			if (room.getUser(event.getUserId()).isRoomOwner()) {
				rebootMonitor(room);
			} else {
				room.send("Only room owners can make that call.");
			}

		} else if (message.toLowerCase().contains("stop")) {
			if (room.getUser(event.getUserId()).isRoomOwner()) {
				stopMonitor(room, true);
			} else {
				room.send("Only room owners can make that call.");
			}
		}
	}
	
	private void leaveRoom(Room room) {
		room.leave();
	}
	
	private void rebootMonitor(Room room) {
		stopMonitor(room, false);
		startMonitor();
	}
	
	private void stopMonitor(Room room, boolean leaveRoom) {
		if (leaveRoom == true) {
			leaveRoom(room);
		}
		executorService.shutdown();
	}
	
	private void run(Room room, PostUtils postUtils) {
		try {
			List<Integer> postIds = postUtils.getPostIdsByActivity(lastPostTime);
			if (postIds.size() > 0) {
				String postIdInput = "";
				for (int id : postIds) {
					postIdInput += id + ";";
				}
				postIdInput = postIdInput.substring(0, postIdInput.length()-1);
				List<Post> posts = getLatestRevisions(postIdInput);
				for (Post post : posts) {
					String message =  getVandalismMessage(post);
					if (message != "") {
						sendVandalismFoundMessage(room, post, message);
					}
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void run(Room room, String postIdInput) {
		
		List<Post> revisions = getLatestRevisions(postIdInput);
  
		for (Post post : revisions) {
			String message =  getVandalismMessage(post);
		  
			if (message != "") {
				sendVandalismFoundMessage(room, post, message);
			} else {
	  			sendNoVandalismFoundMessage(room, post);
	  		}
		}
  	}
	
	private String getVandalismMessage(Post post) {
		
		String message = "";
		
        if (post.getRevisionNumber()!= 1 && post.getIsRollback() == false) {
        	VandalisedPost vandalisedPost = getVandalisedPost(post);

        	for (String reason : vandalisedPost.getReasons()) {
        		if (message != "") {
        			message += "; ";
        		}
	    		message += reason;
	    	}
        }
        
        return message;
		
	}
	
	private List<Post> getLatestRevisions(String postIdInput) {
		PostUtils postUtils = new PostUtils();
		return postUtils.getLastestRevisions(postIdInput);
	}
	
	private VandalisedPost getVandalisedPost(Post post) {
 		VandalisedPost vandalisedPost = new VandalisedPost(post);
		
		 try {
			  {
		         VandalismFinder vandalismFinder = new VandalismFinder(post);
		         vandalisedPost = vandalismFinder.findReasons();
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return vandalisedPost;
	}
	
	private void sendVandalismFoundMessage(Room room, Post post, String reason) {
		String message = "[ [Belisarius](" + readMe + ") ]";
		message += " Potential vandalism found. Reason: " + reason;
		message += " [Link to " + post.getPostType().toLowerCase() + "](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions).";
		message += " Revision: " + String.valueOf(post.getRevisionNumber());
		message += " @Bugs";
		room.send(message);
	}
	
	private void sendNoVandalismFoundMessage(Room room, Post post) {
		String message = "[ [Belisarius](" + readMe + ") ]";
		message += " No vandalism has been found.";
		message += " [Link to " + post.getPostType().toLowerCase() + "](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions).";
		message += " Revision: " + String.valueOf(post.getRevisionNumber());
		message += " @Bugs";
		room.send(message);
	}
	
}
