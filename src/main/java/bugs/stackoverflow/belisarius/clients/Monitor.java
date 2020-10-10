package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;
import bugs.stackoverflow.belisarius.utils.DatabaseUtils;
import bugs.stackoverflow.belisarius.utils.JsonUtils;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    private MonitorService monitorService;

    public void run(List<Post> posts, MonitorService service) {
        monitorService = service;
        for (Post post : posts) {
            // do not check the initial revision!
            if (post.getRevisionNumber() != 1) {
                VandalisedPost vandalisedPost = getVandalisedPost(post);
                boolean postExists = PostUtils.checkVandalisedPost(post);

                if (vandalisedPost.getSeverity() != null && !postExists) {
                    reportPost(vandalisedPost, post);
                }
            }
        }

    }

    public void runOnce(Post post, MonitorService service) {
        monitorService = service;

        if (post.getRevisionNumber() != 1) {
            VandalisedPost vandalisedPost = getVandalisedPost(post);
            // Check if the post exists
            if (PostUtils.checkVandalisedPost(post)) {
                LOGGER.info("The given post has already been reported.");
                int higgsId = DatabaseUtils.getHiggsId(post.getPostId(), post.getRevisionNumber(), new PropertyService().getRoomId());
                sendPostAlreadyReportedMessage(post, higgsId, vandalisedPost.getSeverity());
            } else if (vandalisedPost.getSeverity() != null) {
                // if the post hasn't been caught and it has been potentially vandalised report it
                reportPost(vandalisedPost, post);
            } else {
                // if none of the above are true, then the latest edit is probably not harmful
                LOGGER.info("No vandalism was found for given post.");
                sendNoVandalismFoundMessage(post);
            }
        } else {
            // revision 1; unlikely to be bad
            LOGGER.info("No vandalism found was found for given post.");
            sendNoVandalismFoundMessage(post);
        }

    }

    private VandalisedPost getVandalisedPost(Post post) {
        return PostUtils.getVandalisedPost(post);
    }

    private void reportPost(VandalisedPost vandalisedPost, Post post) {
        try {
            String lastBodyMarkdown = null;
            String bodyMarkdown = null;

            // The URLs to fetch the revision's markdown. Format: https://stackoverflow.com/revisions/guid/view-source
            String previousRevisionSourceUrl = "https://" + post.getSite() + ".com/revisions/" + post.getPreviousRevisionGuid() + "/view-source";
            String currentRevisionSourceUrl = "https://" + post.getSite() + ".com/revisions/" + post.getRevisionGuid() + "/view-source";

            // Only fetch the markdown if last body and body exist!
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

            PostUtils.storeVandalisedPost(vandalisedPost, higgsId, lastBodyMarkdown, bodyMarkdown);
            sendVandalismFoundMessage(post, vandalisedPost, higgsId);
        } catch (Exception exception) {
            LOGGER.info("Error while trying to reportPost.", exception);
        }
    }

    private void sendVandalismFoundMessage(Post post, VandalisedPost vandalisedPost, int higgsId) {
        Belisarius.buildMessage(higgsId, post.getPostType().toLowerCase(), vandalisedPost.getSeverity(),
                                Belisarius.POTENTIAL_VANDALISM + vandalisedPost.getReasonMessage(), post.getAllRevisionsUrl(),
                                post.getRevisionNumber(), post.getRevisionUrl(), monitorService);
    }

    private void sendNoVandalismFoundMessage(Post post) {
        Belisarius.buildMessage(0, post.getPostType().toLowerCase(), null, Belisarius.NO_ISSUES, post.getAllRevisionsUrl(),
                                post.getRevisionNumber(), post.getRevisionUrl(), monitorService);
    }

    private void sendPostAlreadyReportedMessage(Post post, int higgsId, String severity) {
        Belisarius.buildMessage(higgsId, post.getPostType().toLowerCase(), severity, Belisarius.ALREADY_REPORTED, post.getAllRevisionsUrl(),
                                post.getRevisionNumber(), post.getRevisionUrl(), monitorService);
    }

}
