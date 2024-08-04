package bugs.stackoverflow.belisarius.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.filters.BlacklistedFilter;
import bugs.stackoverflow.belisarius.filters.CodeRemovedFilter;
import bugs.stackoverflow.belisarius.filters.FewUniqueCharactersFilter;
import bugs.stackoverflow.belisarius.filters.Filter;
import bugs.stackoverflow.belisarius.filters.Filter.Severity;
import bugs.stackoverflow.belisarius.filters.OffensiveWordFilter;
import bugs.stackoverflow.belisarius.filters.RepeatedWordFilter;
import bugs.stackoverflow.belisarius.filters.TextRemovedFilter;
import bugs.stackoverflow.belisarius.filters.VeryLongWordFilter;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.User;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.models.VandalisedPost.Feedback;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.PropertyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;
import org.sobotics.chatexchange.chat.event.PingMessageEvent;

import com.google.gson.JsonObject;
import io.swagger.client.ApiException;

public class PostUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostUtils.class);
    private static final int ROOM_ID = Integer.parseInt(new PropertyService().getProperty("roomid"));

    public static boolean postBeenEdited(JsonObject post) {
        // the post hasn't been edited if JSON doesn't contain last_edit_date property
        return post.has("last_edit_date");
    }

    public static boolean editorAlsoOwner(JsonObject post) {
        // avoid NPEs by checking if last_editor and owner properties exist
        if (!post.has("last_editor") || !post.has("owner")) {
            return false;
        }
        long ownerId = post.get("owner").getAsJsonObject().get("user_id").getAsLong();
        long editorId = post.get("last_editor").getAsJsonObject().get("user_id").getAsLong();

        return ownerId == editorId;
    }

    public static Post getPost(JsonObject post, String site, String title, String previousRevisionGuid) {
        JsonObject userJson = post.get("user").getAsJsonObject();
        User user = new User(
            JsonUtils.escapeHtmlEncoding(userJson.get("display_name").getAsString()),
            userJson.get("user_id").getAsInt(),
            userJson.get("reputation").getAsLong()
        );

        Post newPost = new Post(
            post.get("post_id").getAsInt(),
            post.get("revision_number").getAsInt(),
            post.get("creation_date").getAsLong(),

            title,
            post.has("last_title") ? post.get("last_title").getAsString() : null,
            post.has("body") ? post.get("body").getAsString() : null,
            post.has("last_body") ? post.get("last_body").getAsString() : null,

            user,
            post.get("is_rollback").getAsBoolean(),
            post.get("post_type").getAsString(),
            post.has("comment") ? post.get("comment").getAsString() : null,

            site,
            post.get("revision_guid").getAsString(),
            previousRevisionGuid
        );

        return newPost;
    }

    public static void storeFeedback(Room room, PingMessageEvent event, Feedback feedback) {
        long repliedTo = event.getParentMessageId();
        Message repliedToMessage = room.getMessage(repliedTo);

        long postId = getPostIdFromMessage(repliedToMessage.getPlainContent().trim());
        int revisionNumber = getRevisionNumberFromMessage(repliedToMessage.getPlainContent().trim());

        DatabaseUtils.storeFeedback(postId, revisionNumber, ROOM_ID, feedback.toString(), event.getMessage().getUser().getId());

        PropertyService propertyService = new PropertyService();
        try {
            if (propertyService.getProperty("useHiggs").equals("true")) {
                int higgsId = getHiggsIdFromMessage(repliedToMessage.getPlainContent().trim());
                HiggsService.getInstance().sendFeedback(higgsId, (int) event.getMessage().getUser().getId(), feedback);
            }
        } catch (ApiException exception) {
            LOGGER.info("ApiException was thrown while trying to send feedback " + feedback.toString() + " to Higgs"
                      + " from " + event.getMessage().getUser().getId(), exception);
        }
    }

    public static boolean checkVandalisedPost(Post post) {
        return DatabaseUtils.checkVandalisedPostExists(post.getPostId(), post.getRevisionNumber(), ROOM_ID);
    }

    public static void storeVandalisedPost(VandalisedPost vandalisedPost, int higgsId, String lastBodyMarkdown, String bodyMarkdown) {
        Post post = vandalisedPost.getPost();
        DatabaseUtils.storeVandalisedPost(post.getPostId(), post.getCreationDate(), post.getRevisionNumber(), ROOM_ID,
                                          post.getUser().getUserId(), post.getTitle(), post.getLastTitle(), post.getBody(),
                                          post.getLastBody(), post.getIsRollback(), post.getPostType(), post.getComment(),
                                          post.getSite(), vandalisedPost.getSeverity(), post.getRevisionGuid());

    }

    private static long getPostIdFromMessage(String message) {
        message = message.split("//stackoverflow.com/posts/")[1];
        return Long.parseLong(message.substring(0, message.indexOf("/")));
    }

    private static int getRevisionNumberFromMessage(String message) {
        return Integer.parseInt(message.substring(message.length() - 2, message.length() - 1));
    }

    private static int getHiggsIdFromMessage(String message) {
        return Integer.parseInt(message.split("Hippo\\]\\(")[1].split("\\)")[0].substring(40));
    }

    public static VandalisedPost getVandalisedPost(Post post) {
        // create a list with all the filters
        List<Filter> filters = new ArrayList<>();
        filters.add(new BlacklistedFilter(ROOM_ID, post));
        filters.add(new VeryLongWordFilter(ROOM_ID, post));
        filters.add(new CodeRemovedFilter(ROOM_ID, post));
        filters.add(new TextRemovedFilter(ROOM_ID, post));
        filters.add(new FewUniqueCharactersFilter(ROOM_ID, post));
        filters.add(new OffensiveWordFilter(ROOM_ID, post));
        filters.add(new RepeatedWordFilter(ROOM_ID, post));

        Severity severity = null;

        Map<String, Double> formattedReasonMessages = new HashMap<>();
        Map<String, Double> reasonNames = new HashMap<>();
        for (Filter filter: filters) {
            // loop through the list and check if a filter catches the post
            // also create formattedReasonMessages for Higgs (reasonNames)
            if (filter.isHit()) {
                filter.storeHit();
                formattedReasonMessages.put(filter.getFormattedReasonMessage(), filter.getTotalScore());
                // We need a different reason for Higgs for each blacklisted/offensive word!
                filter.getReasonName().forEach(name -> reasonNames.put(name, filter.getScore()));
                severity = filter.getSeverity();
            }
        }

        return new VandalisedPost(post, formattedReasonMessages, severity, reasonNames);
    }
}
