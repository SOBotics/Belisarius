package bugs.stackoverflow.belisarius.services;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.clients.Monitor;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.rooms.Chatroom;

import fr.tunaki.stackoverflow.chat.*;
import fr.tunaki.stackoverflow.chat.event.EventType;

import java.util.concurrent.*;
import java.time.Instant;
import java.util.*;

public class MonitorService {

	private StackExchangeClient client;
	private List<Chatroom> chatrooms;
	private List<Room> rooms;
	private Map<String, Belisarius> bots;
	
	private int presentInterval;
	
	private ScheduledExecutorService executorService;
	private ScheduledFuture<?> handle;	
	
	public MonitorService(StackExchangeClient client, List<Chatroom> chatrooms) {
		this.client = client;
		this.chatrooms = chatrooms;
		this.rooms = new ArrayList<>();
		this.bots = new HashMap<>();
		this.presentInterval = 60;
	}
	
	public void startMonitor() {
		
		for (Chatroom chatroom : chatrooms) {
			
			Room room = null;
			try {
				room = client.joinRoom(chatroom.getHost(), chatroom.getRoomId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (chatroom.getUserMentioned(room, this) != null) {
				room.addEventListener(EventType.USER_MENTIONED, chatroom.getUserMentioned(room, this));
			}
			
			if (chatroom.getPostedReply(room) != null) {
				room.addEventListener(EventType.MESSAGE_REPLY, chatroom.getPostedReply(room));
			}
			
			if (chatroom.getPostedMessage(room, this) != null) {
				room.addEventListener(EventType.MESSAGE_POSTED, chatroom.getPostedMessage(room, this));
			}
			
            String siteName = chatroom.getSiteName();
            String SiteUrl = chatroom.getSiteUrl();
			
            if (!bots.containsKey(siteName)) {
            	bots.put(siteName, new Belisarius(siteName, SiteUrl));
            }
			
			room.send("[ [Belisarius](" + Belisarius.readMe + ") ] started.");
			
			rooms.add(room);
		}
	
		executorService = Executors.newSingleThreadScheduledExecutor();
		 
	}
	
	public void runMonitor() {
		 handle = executorService.scheduleAtFixedRate(() -> execute(), 0, presentInterval, TimeUnit.SECONDS);
	}
	
    private void execute() {
    	Map<String, List<Post>> postMap = new HashMap<>();
    	
    	for (String site : bots.keySet()) {
    		postMap.put(site, bots.get(site).getPosts());
    	}

    	for (int i=0; i<rooms.size(); i++) {
    		Room room = rooms.get(i);
    		Chatroom chatroom = chatrooms.stream().filter((r) -> r.getRoomId() == room.getRoomId()).findFirst().orElse(null);
    		if (chatroom != null)
    		{
	    		List<Post> posts = postMap.get(chatroom.getSiteName());
	    		new Monitor().run(room, posts, chatroom.getSiteName(), chatroom.getSiteUrl());
    		}
    	}
    }
    
    public void executeOnce(String postId, Room room) {
    	Map<String, Post> postMap = new HashMap<>();
    	
    	for (String site : bots.keySet()) {
    		postMap.put(site, bots.get(site).getPost(postId));
    	}

		Chatroom chatroom = chatrooms.stream().filter((r) -> r.getRoomId() == room.getRoomId()).findFirst().orElse(null);
    	
		if (chatroom != null)
		{
			Post post = postMap.get(chatroom.getSiteName());
			new Monitor().runOnce(room, post, chatroom.getSiteName(), chatroom.getSiteUrl());
		}
    }
	
    public void stop(){
        executorService.shutdown();
    }
	
	public void reboot() {
        this.stop();
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.runMonitor();
        for(int i=0; i<rooms.size(); i++){
            Room room = rooms.get(i);
            room.send("[ [Belisarius](\\\" + Belisarius.readMe + \\\") ] rebooted at " + Instant.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
}
