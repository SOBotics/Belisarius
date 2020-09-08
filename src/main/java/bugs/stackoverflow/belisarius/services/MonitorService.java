package bugs.stackoverflow.belisarius.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.clients.Monitor;
import bugs.stackoverflow.belisarius.models.Chatroom;
import bugs.stackoverflow.belisarius.models.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import org.sobotics.chatexchange.chat.event.EventType;

public class MonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

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
            } catch (Exception exception) {
                LOGGER.info("Failed to join room " + String.valueOf(chatroom.getRoomId()) + " on " + chatroom.getHost(), exception);
            }

            if (room != null) {
                if (chatroom.getUserMentioned(room, this) != null) {
                    room.addEventListener(EventType.USER_MENTIONED, chatroom.getUserMentioned(room, this));
                }

                if (chatroom.getPostedReply(room) != null) {
                    room.addEventListener(EventType.MESSAGE_REPLY, chatroom.getPostedReply(room));
                }

                if (chatroom.getPostedMessage(room) != null) {
                    room.addEventListener(EventType.MESSAGE_POSTED, chatroom.getPostedMessage(room));
                }

                String site = chatroom.getSiteName();

                if (!bots.containsKey(site)) {
                    bots.put(site, new Belisarius(site));
                }

                room.send(Belisarius.README + "] started.");

                rooms.add(room);
            }
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

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            Chatroom chatroom = chatrooms.stream().filter(chatRoom -> chatRoom.getRoomId() == room.getRoomId()).findFirst().orElse(null);
            if (chatroom != null) {
                List<Post> posts = postMap.get(chatroom.getSiteName());
                new Monitor().run(room, posts, chatroom.getOutputMessage());
            }
        }
    }

    public void executeOnce(String postId, Room room) {
        Map<String, Post> postMap = new HashMap<>();

        for (String site : bots.keySet()) {
            postMap.put(site, bots.get(site).getPost(postId));
        }

        Chatroom chatroom = chatrooms.stream().filter(chatRoom -> chatRoom.getRoomId() == room.getRoomId()).findFirst().orElse(null);

        if (chatroom != null) {
            Post post = postMap.get(chatroom.getSiteName());
            new Monitor().runOnce(room, post, chatroom.getOutputMessage());
        }
    }

    public void stop() {
        executorService.shutdown();
    }

    public void reboot() {
        this.stop();
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.runMonitor();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            room.send(Belisarius.README + "] rebooted at " + Instant.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

}
