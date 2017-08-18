package bugs.stackoverflow.belisarius.services;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.clients.Monitor;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.rooms.Chatroom;

import fr.tunaki.stackoverflow.chat.*;
import fr.tunaki.stackoverflow.chat.event.EventType;

import java.util.concurrent.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class MonitorService {

	private StackExchangeClient client;
	private List<Chatroom> rooms;
	private List<Room> chatrooms;
	private Map<String, Belisarius> bots;
	
	private int presentInterval;
	
	private ScheduledExecutorService executorService;
	private ScheduledFuture<?> handle;	
	
	public MonitorService(StackExchangeClient client, List<Chatroom> rooms) {
		this.client = client;
		this.rooms = rooms;
		this.chatrooms = new ArrayList<>();
		this.bots = new HashMap<>();
		this.presentInterval = 60;
	}
	
	public void startMonitor() {
		
		for (Chatroom room : rooms) {
			
			Room chatroom = null;
			try {
				chatroom = client.joinRoom(room.getHost(), room.getRoomId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (room.getUserMentioned(chatroom, this) != null) {
				chatroom.addEventListener(EventType.USER_MENTIONED, room.getUserMentioned(chatroom, this));
			}
			
			if (room.getPostedReply(chatroom) != null) {
				chatroom.addEventListener(EventType.MESSAGE_REPLY, room.getPostedReply(chatroom));
			}
			
			if (room.getPostedMessage(chatroom, this) != null) {
				chatroom.addEventListener(EventType.MESSAGE_POSTED, room.getPostedMessage(chatroom, this));
			}
			
            String siteName = room.getSiteName();
            String SiteUrl = room.getSiteUrl();
			
            if (!bots.containsKey(siteName)) {
            	bots.put(siteName, new Belisarius(siteName, SiteUrl));
            }
			
			chatroom.send("[ [Belisarius](" + Belisarius.readMe + ") ] started.");
			
			chatrooms.add(chatroom);
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
    		Chatroom room = rooms.get(i);
    		Room chatroom = chatrooms.get(i);
    		List<Post> posts = postMap.get(room.getSiteName());
    		new Monitor().runOnce(chatroom, posts, room.getSiteName(), room.getSiteUrl());
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
            Room room = chatrooms.get(i);
            room.send("[ [Belisarius](\\\" + Belisarius.readMe + \\\") ] rebooted at " + Instant.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
}
