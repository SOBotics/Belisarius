package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.PropertyService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.JsonUtils;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Room;

public class Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    public void run(Room room, List<Post> posts, boolean outputMessage) {

        for (Post post : posts) {
            if (post.getRevisionNumber() != 1) {
                VandalisedPost vandalisedPost = getVandalisedPost(room, post);
                boolean postExists = PostUtils.checkVandalisedPost(room, post);

                if (vandalisedPost != null && vandalisedPost.getSeverity() != null && !postExists) {
                    reportPost(room, vandalisedPost, post, outputMessage);
                }
            }
        }

    }

    public void runOnce(Room room, Post post, boolean outputMessage) {

        if (post.getRevisionNumber() != 1) {
            VandalisedPost vandalisedPost = getVandalisedPost(room, post);
            // Check if the post exists
            if (PostUtils.checkVandalisedPost(room, post)) {
                LOGGER.info("The given post has already been reported.");
                int higgsId = DatabaseUtils.getHiggsId(post.getPostId(), post.getRevisionNumber(), room.getRoomId());
                sendPostAlreadyReportedMessage(room, post, higgsId, vandalisedPost.getSeverity());
            } else if (vandalisedPost != null && vandalisedPost.getSeverity() != null) {
                reportPost(room, vandalisedPost, post, outputMessage);
            } else {
                LOGGER.info("No vandalism was found for given post.");
                sendNoVandalismFoundMessage(room, post);
            }
        } else {
            LOGGER.info("No vandalism found was found for given post.");
            sendNoVandalismFoundMessage(room, post);
        }

    }

    private VandalisedPost getVandalisedPost(Room room, Post post) {
        return PostUtils.getVandalisedPost(room, post);
    }

    private void reportPost(Room room, VandalisedPost vandalisedPost, Post post, boolean outputMessage) {
        try {
            String lastBodyMarkdown = null;
            String bodyMarkdown = null;
            String previousRevisionSourceUrl = "https://" + post.getSite() + ".com/revisions/" + post.getPreviousRevisionGuid() + "/view-source";
            String currentRevisionSourceUrl = "https://" + post.getSite() + ".com/revisions/" + post.getRevisionGuid() + "/view-source";
            if (post.getLastBody() != null) {
                lastBodyMarkdown = JsonUtils.getHtml(previousRevisionSourceUrl);
            }
            if (post.getBody() != null) {
                bodyMarkdown = JsonUtils.getHtml(currentRevisionSourceUrl);
            }

            PropertyService propertyService = new PropertyService();
            int higgsId;
            if (propertyService.getUseHiggs()) {
                higgsId = HiggsService.getInstance().registerVandalisedPost(vandalisedPost, vandalisedPost.getPost(), lastBodyMarkdown, bodyMarkdown);
            } else {
                higgsId = 0;
            }
            PostUtils.storeVandalisedPost(room, vandalisedPost, higgsId, lastBodyMarkdown, bodyMarkdown);
            if (outputMessage) {
                sendVandalismFoundMessage(room, post, vandalisedPost, higgsId);
            }
        } catch (Exception exception) {
            LOGGER.info("Error while trying to reportPost.", exception);
        }
    }

    private void sendVandalismFoundMessage(Room room, Post post, VandalisedPost vandalisedPost, int higgsId) {
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
