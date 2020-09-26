package bugs.stackoverflow.belisarius.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private ScheduledExecutorService executorService;

    public MonitorService(StackExchangeClient client, List<Chatroom> chatrooms) {
        this.client = client;
        this.chatrooms = chatrooms;
        this.rooms = new ArrayList<>();
        this.bots = new HashMap<>();
    }

    public void startMonitor() {

        for (Chatroom chatroom : chatrooms) {
            // for every room: join, add event listeners and post a "started" message

            Room room = client.joinRoom(chatroom.getHost(), chatroom.getRoomId());

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
        // Check posts from the API every 60 seconds
        executorService.scheduleAtFixedRate(this::execute, 0, 60, TimeUnit.SECONDS);
    }

    private void execute() {
        // using a try-catch here because of https://stackoverflow.com/a/24902026
        try {
            Map<String, List<Post>> postMap = new HashMap<>();

            for (Map.Entry<String, Belisarius> bot : bots.entrySet()) {
                postMap.put(bot.getKey(), bot.getValue().getPosts());
            }

            for (Room room : rooms) {
                Chatroom chatroom = chatrooms.stream().filter(chatRoom -> chatRoom.getRoomId() == room.getRoomId()).findFirst().orElse(null);
                if (chatroom != null) {
                    List<Post> posts = postMap.get(chatroom.getSiteName());
                    new Monitor().run(room, posts, chatroom.getOutputMessage());
                }
            }
        } catch (Exception exception) {
            LOGGER.info("Exception occurred while executing a new monitor.", exception);
        }
    }

    public void executeOnce(String postId, Room room) {
        Map<String, Post> postMap = new HashMap<>();

        for (Map.Entry<String, Belisarius> bot : bots.entrySet()) {
            postMap.put(bot.getKey(), bot.getValue().getPost(postId));
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
        for (Room room : rooms) {
            room.send(Belisarius.README + "] rebooted at " + Instant.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

}
