package bugs.stackoverflow.belisarius.services;

import bugs.stackoverflow.belisarius.*;
import bugs.stackoverflow.belisarius.clients.Monitor;
import bugs.stackoverflow.belisarius.models.*;

import org.sobotics.chatexchange.chat.event.EventType;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;

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
            Chatroom chatroom = chatrooms.stream().filter(r -> r.getRoomId() == room.getRoomId()).findFirst().orElse(null);
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

        Chatroom chatroom = chatrooms.stream().filter(r -> r.getRoomId() == room.getRoomId()).findFirst().orElse(null);

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
