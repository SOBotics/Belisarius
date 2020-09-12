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
import bugs.stackoverflow.belisarius.models.StackOverflowUser;
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

    public static boolean postBeenEdited(JsonObject post) {
        return post.has("last_edit_date");
    }

    public static boolean editorAlsoOwner(JsonObject post) {
        if (!post.has("last_editor")) {
            return false;
        }
        long ownerId = post.get("owner").getAsJsonObject().get("user_id").getAsLong();
        long editorId = post.get("last_editor").getAsJsonObject().get("user_id").getAsLong();

        return ownerId == editorId;
    }

    public static Post getPost(JsonObject post, String site, String title, String previousRevisionGuid) {

        Post newPost = new Post();

        newPost.setPostId(post.get("post_id").getAsInt());

        newPost.setRevisionNumber(post.get("revision_number").getAsInt());
        newPost.setCreationDate(post.get("creation_date").getAsLong());

        newPost.setRevisionUrl("https://" + site + ".com/revisions/" + post.get("post_id").getAsString()
                             + "/" + post.get("revision_number").getAsString());
        newPost.setAllRevisionsUrl("https://" + site + ".com/posts/" + post.get("post_id").getAsString() + "/revisions");

        newPost.setTitle(title);
        if (post.has("last_title")) {
            newPost.setLastTitle(post.get("last_title").getAsString());
        }

        if (post.has("body")) {
            newPost.setBody(post.get("body").getAsString());
        }

        if (post.has("last_body")) {
            newPost.setLastBody(post.get("last_body").getAsString());
        }

        newPost.setIsRollback(post.get("is_rollback").getAsBoolean());
        newPost.setPostType(post.get("post_type").getAsString());

        if (post.has("comment")) {
            newPost.setComment(post.get("comment").getAsString());
        }

        newPost.setRevisionGuid(post.get("revision_guid").getAsString());
        newPost.setPreviousRevisionGuid(previousRevisionGuid);

        JsonObject userJson = post.get("user").getAsJsonObject();
        StackOverflowUser user = new StackOverflowUser();

        try {
            user.setReputation(userJson.get("reputation").getAsLong());
            user.setUsername(JsonUtils.escapeHtmlEncoding(userJson.get("display_name").getAsString()));
            user.setUserType(userJson.get("user_type").getAsString());
            user.setUserId(userJson.get("user_id").getAsInt());
        } catch (Exception exception) {
            LOGGER.info("Error while creating a StackOverflowUser object.", exception);
        }

        newPost.setUser(user);

        return newPost;
    }

    static Post getPost(int postId, long creationDate, int revisionId, String title, String lastTitle,
                        String body, String lastBody, boolean isRollback, String postType, String comment,
                        int ownerId, String site, String revisionGuid, String previousRevisionGuid) {

        Post newPost = new Post();

        newPost.setPostId(postId);
        newPost.setCreationDate(creationDate);
        newPost.setRevisionNumber(revisionId);
        newPost.setRevisionUrl("https://" + site + ".com/revisions/" + String.valueOf(postId) + "/" + String.valueOf(revisionId));
        newPost.setAllRevisionsUrl("https://" + site + ".com/posts/" + String.valueOf(postId) + "/revisions");
        newPost.setTitle(title);
        newPost.setLastTitle(lastTitle);
        newPost.setBody(body);
        newPost.setLastBody(lastBody);
        newPost.setIsRollback(isRollback);
        newPost.setPostType(postType);
        newPost.setComment(comment);
        newPost.setRevisionGuid(revisionGuid);
        newPost.setPreviousRevisionGuid(previousRevisionGuid);

        StackOverflowUser user = new StackOverflowUser();
        user.setUserId(ownerId);
        newPost.setUser(user);

        return newPost;
    }

    static void storeFeedback(Room room, PingMessageEvent event, Feedback feedback) {
        long repliedTo = event.getParentMessageId();
        Message repliedToMessage = room.getMessage(repliedTo);

        long postId = getPostIdFromMessage(repliedToMessage.getPlainContent().trim());
        int revisionNumber = getRevisionNumberFromMessage(repliedToMessage.getPlainContent().trim());

        DatabaseUtils.storeFeedback(postId, revisionNumber, room.getRoomId(), feedback.toString(), event.getMessage().getUser().getId());

        PropertyService propertyService = new PropertyService();
        try {
            if (propertyService.getUseHiggs()) {
                int higgsId = DatabaseUtils.getHiggsId(postId, revisionNumber, event.getRoomId());
                HiggsService.getInstance().sendFeedback(higgsId, (int) event.getMessage().getUser().getId(), feedback);
            }
        } catch (ApiException exception) {
            LOGGER.info("ApiException was thrown while trying to send feedback " + feedback.toString() + " to Higgs"
                      + " from " + String.valueOf(event.getMessage().getUser().getId()), exception);
        }
    }

    public static boolean checkVandalisedPost(Room room, Post post) {
        return DatabaseUtils.checkVandalisedPostExists(post.getPostId(), post.getRevisionNumber(), room.getRoomId());
    }

    public static void storeVandalisedPost(Room room, VandalisedPost vandalisedPost, int higgsId, String lastBodyMarkdown, String bodyMarkdown) {
        Post post = vandalisedPost.getPost();
        DatabaseUtils.storeVandalisedPost(post.getPostId(), post.getCreationDate(), post.getRevisionNumber(), room.getRoomId(),
                                          post.getUser().getUserId(), post.getTitle(), post.getLastTitle(), post.getBody(),
                                          post.getLastBody(), post.getIsRollback(), post.getPostType(), post.getComment(),
                                          post.getSite(), vandalisedPost.getSeverity(), higgsId, post.getRevisionGuid(),
                                          post.getPreviousRevisionGuid(), lastBodyMarkdown, bodyMarkdown);

    }

    private static long getPostIdFromMessage(String message) {
        message = message.split("//stackoverflow.com/posts/")[1];
        return Long.parseLong(message.substring(0, message.indexOf("/")));
    }

    private static int getRevisionNumberFromMessage(String message) {
        return Integer.parseInt(message.substring(message.length() - 2, message.length() - 1));
    }

    public static VandalisedPost getVandalisedPost(Room room, Post post) {
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new BlacklistedFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(BlacklistedFilter.class.getName()))));
        filters.add(new VeryLongWordFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(VeryLongWordFilter.class.getName()))));
        filters.add(new CodeRemovedFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(CodeRemovedFilter.class.getName()))));
        filters.add(new TextRemovedFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(TextRemovedFilter.class.getName()))));
        filters.add(new FewUniqueCharactersFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(FewUniqueCharactersFilter.class.getName()))));
        filters.add(new OffensiveWordFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(OffensiveWordFilter.class.getName()))));
        filters.add(new RepeatedWordFilter(room, post, DatabaseUtils.getReasonId(ClassUtils.getClassName(RepeatedWordFilter.class.getName()))));

        Severity severity = null;

        Map<String, Double> formattedReasonMessages = new HashMap<>();
        Map<String, Double> reasonNames = new HashMap<>();
        for (Filter filter: filters) {
            if (filter.isHit()) {
                filter.storeHit();
                formattedReasonMessages.put(filter.getFormattedReasonMessage(), filter.getScore());
                reasonNames.put(filter.getReasonName(), filter.getScore());
                severity = filter.getSeverity();
            }
        }

        return new VandalisedPost(post, formattedReasonMessages, severity, reasonNames);
    }
}
