package bugs.stackoverflow.belisarius.services;

import bugs.stackoverflow.belisarius.finders.VandalismFinder;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import fr.tunaki.stackoverflow.chat.Room;
import fr.tunaki.stackoverflow.chat.event.EventType;
import fr.tunaki.stackoverflow.chat.event.MessagePostedEvent;
import fr.tunaki.stackoverflow.chat.event.MessageReplyEvent;
import fr.tunaki.stackoverflow.chat.event.UserMentionedEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class MonitorService {

	private Room room;
	public static long lastPostTime = System.currentTimeMillis()/1000-1*60;
	private ScheduledExecutorService executorService;
	
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
		
		if (message.toLowerCase().startsWith("@bots alive")) {
			room.send("Yeah, I'm alive.");
		}
	}
	
	private void messageReply(Room room, MessageReplyEvent event) {
		//String message = event.getMessage().getPlainContent().trim();
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
				run(room, postId);
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
				for (int id : postIds) {
					Post post = getLatestRevision(id);
					if (postIsVandalised(post)) {
						sendVandalismFoundMessage(room, post);
					}
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void run(Room room, int postId) {
		
	    Post post = getLatestRevision(postId);
		
	    if (post.getRevisionNumber() != 1){
			if (postIsVandalised(post)) {
				sendVandalismFoundMessage(room, post);
			} else {
				sendNoVandalismFoundMessage(room, post);
			}
	    } else {
	    	sendPostNotEditedMessage(room, post);
		}

	}
	
	private Post getLatestRevision(int postId) {
		PostUtils postUtils = new PostUtils();
		Post editedPost = postUtils.getLastestRevisionByPostId(postId);
		
		return editedPost;
	}
	
	private boolean postIsVandalised(Post post) {

		 double titleQuantifier = 1;
		 double bodyQuantifier = 1;
		 
		 try {
			 if (post.getIsRollback() == false) {
				 if (post.getTitle() != null) {
					 VandalismFinder vandalismOnTitle = new VandalismFinder(post.getTitle(), post.getLastTitle(), titleQuantifier);
					 if (vandalismOnTitle.vandalismFound()) {
						 return true;
					 }
				 }
				 
				 if (post.getBody() != null) {
					 VandalismFinder vandalismnOnBody = new VandalismFinder(post.getBody(), post.getLastBody(), bodyQuantifier);
					 if (vandalismnOnBody.vandalismFound()) {
						 return true;
					 }
					 
				 }
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 
		 return false;
	}
	
	private void sendVandalismFoundMessage(Room room, Post post) {
		String message = "Potential vandalism found on [" + post.getPostType().toLowerCase() + "](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions)";
		//message += " Score " + NumberFormat.getNumberInstance().format(VandalismFinder.getScore());
		//implement a reason
		message += " @Bugs";
		room.send(message);
	}
	
	private void sendNoVandalismFoundMessage(Room room, Post post) {
		String message = "No vandalism has been found on [" + post.getPostType().toLowerCase() + "](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions)";
		//message += " Score " + NumberFormat.getNumberInstance().format(VandalismFinder.getScore());
		//implement a reason
		message += " @Bugs";
		room.send(message);
	}
	
	private void sendPostNotEditedMessage(Room room, Post post) {
		String message = "This [" + post.getPostType().toLowerCase() + "](https://stackoverflow.com/posts/" + String.valueOf(post.getPostId()) + "/revisions) has not been edited yet.";
		//message += " Score " + NumberFormat.getNumberInstance().format(VandalismFinder.getScore());
		//implement a reason
		message += " @Bugs";
		room.send(message);
	}
	
}
