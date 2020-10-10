package bugs.stackoverflow.belisarius.services;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.clients.Monitor;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.ChatUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.StackExchangeClient;
import org.sobotics.chatexchange.chat.event.EventType;

public class MonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

    private final boolean shouldOutput = new PropertyService().getShouldOutputMessage();
    private StackExchangeClient client;
    private int roomId;
    private Belisarius belisarius;
    private Room room;
    private ScheduledExecutorService executorService;

    public MonitorService(StackExchangeClient client, int chatroom, String sitename) {
        this.client = client;
        this.roomId = chatroom;
        this.belisarius = new Belisarius(sitename);
        if (this.roomId != 0) {
            this.room = this.client.joinRoom(ChatUtils.getChatHost(sitename), this.roomId);
            // Add event listeners
            room.addEventListener(EventType.USER_MENTIONED, event -> ChatUtils.handleMentionedEvent(event, this));
            room.addEventListener(EventType.MESSAGE_REPLY, event -> ChatUtils.handleReplies(room, event));
            room.addEventListener(EventType.MESSAGE_POSTED, event -> ChatUtils.handleMessagePostedEvent(event, this));
        }
    }

    public void runMonitor() {
        this.sendMessageToChat(Belisarius.README + "] started.");
        executorService = Executors.newSingleThreadScheduledExecutor();
        // Check posts from the API every 60 seconds
        executorService.scheduleAtFixedRate(this::execute, 0, 60, TimeUnit.SECONDS);
    }

    private void execute() {
        // using a try-catch here because of https://stackoverflow.com/a/24902026
        try {
            List<Post> posts = belisarius.getPosts();
            new Monitor().run(posts, this);
        } catch (Exception exception) {
            LOGGER.info("Exception occurred while executing a new monitor.", exception);
        }
    }

    public void executeOnce(String postId) {
        Post post = belisarius.getPost(postId);
        new Monitor().runOnce(post, this);
    }

    public void stop() {
        executorService.shutdown();
    }

    public void reboot() {
        this.stop();
        executorService = Executors.newSingleThreadScheduledExecutor();
        this.runMonitor();
        sendMessageToChat(Belisarius.README + "] rebooted at " + Instant.now());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void sendMessageToChat(String message) {
        if (shouldOutput) {
            room.send(message);
        } else {
            LOGGER.info(message);
        }
    }

    public void replyToMessage(long toReplyId, String message) {
        if (shouldOutput) {
            room.replyTo(toReplyId, message);
        } else {
            LOGGER.info(":" + String.valueOf(toReplyId) + " " + message);
        }
    }
}
