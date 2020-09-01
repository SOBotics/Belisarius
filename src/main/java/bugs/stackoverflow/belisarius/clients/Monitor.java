package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.finders.VandalismFinder;
import bugs.stackoverflow.belisarius.models.*;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.JsonUtils;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import org.sobotics.chatexchange.chat.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.client.ApiException;

public class Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    public void run(Room room, List<Post> posts, boolean outputMessage) {

        try {
            for (Post post : posts) {
                if (post.getRevisionNumber() != 1 && !post.getIsRollback()) {
                    VandalisedPost vandalisedPost = getVandalisedPost(room, post);
                    boolean postExists = PostUtils.checkVandalisedPost(room, post);

                    if (vandalisedPost != null && vandalisedPost.getSeverity() != null && !postExists) {
                        reportPost(room, vandalisedPost, post, outputMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void runOnce(Room room, Post post, boolean outputMessage) {

        try {
            if (post.getRevisionNumber() != 1 && !post.getIsRollback()) {
                VandalisedPost vandalisedPost = getVandalisedPost(room, post);
                if (PostUtils.checkVandalisedPost(room, post)) { // the post already exists
                    LOGGER.info("The given post has already been reported.");
                    int higgsId = DatabaseUtils.getHiggsId(post.getPostId(), post.getRevisionNumber(), room.getRoomId());
                    sendPostAlreadyReportedMessage(room, post, higgsId, vandalisedPost.getSeverity());
                } else if (vandalisedPost != null && vandalisedPost.getSeverity() != null) {
                    reportPost(room, vandalisedPost, post, outputMessage);
                } else { // the revision is unlikely to be bad
                    LOGGER.info("No vandalism was found for given post.");
                    sendNoVandalismFoundMessage(room, post);
                }
            } else { // either revision number is one or the post is rollback
                LOGGER.info("No vandalism found was found for given post.");
                sendNoVandalismFoundMessage(room, post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private VandalisedPost getVandalisedPost(Room room, Post post) {
        try {
            VandalismFinder vandalismFinder = new VandalismFinder(room, post);
            return vandalismFinder.findReasons();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void reportPost(Room room, VandalisedPost vandalisedPost, Post post, boolean outputMessage) {
        try {
            String lastBodyMarkdown = null;
            String bodyMarkdown = null;
            if (post.getLastBody() != null) {
                lastBodyMarkdown = JsonUtils.getHtml("https://" + post.getSite() + ".com/revisions/" + post.getPreviousRevisionGuid() + "/view-source");
            }
            if (post.getBody() != null) {
                bodyMarkdown = JsonUtils.getHtml("https://" + post.getSite() + ".com/revisions/" + post.getRevisionGuid() + "/view-source");
            }

            int higgsId = HiggsService.getInstance().registerVandalisedPost(vandalisedPost, vandalisedPost.getPost(), lastBodyMarkdown, bodyMarkdown);
            PostUtils.storeVandalisedPost(room, vandalisedPost, higgsId, lastBodyMarkdown, bodyMarkdown);
            if (outputMessage) {
                sendVandalismFoundMessage(room, post, vandalisedPost, higgsId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendVandalismFoundMessage(Room room, Post post, VandalisedPost vandalisedPost, int higgsId) throws ApiException {
        Belisarius.buildMessage(room, higgsId, post.getPostType().toLowerCase(), vandalisedPost.getSeverity(),
                                Belisarius.POTENTIAL_VANDALISM + vandalisedPost.getReasonMessage(), post.getAllRevisionsUrl(),
                                post.getRevisionNumber(), post.getRevisionUrl());
    }

    private void sendNoVandalismFoundMessage(Room room, Post post) {
        Belisarius.buildMessage(room, 0, post.getPostType().toLowerCase(), null, Belisarius.NO_ISSUES, post.getAllRevisionsUrl(),
                                post.getRevisionNumber(), post.getRevisionUrl());
    }

    private void sendPostAlreadyReportedMessage(Room room, Post post, int higgsId, String severity) {
        Belisarius.buildMessage(room, higgsId, post.getPostType().toLowerCase(), severity, Belisarius.ALREADY_REPORTED, post.getAllRevisionsUrl(),
                                post.getRevisionNumber(), post.getRevisionUrl());
    }

}
