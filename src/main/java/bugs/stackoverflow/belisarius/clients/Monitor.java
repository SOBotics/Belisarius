package bugs.stackoverflow.belisarius.clients;

import java.util.List;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.services.HiggsService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.services.PropertyService;
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
            if (post.getRevisionNumber() == 1) {
                continue;
            }

            VandalisedPost vandalisedPost = getVandalisedPost(post);
            boolean postExists = PostUtils.checkVandalisedPost(post);

            if (vandalisedPost.getSeverity() != null && !postExists) {
                reportPost(vandalisedPost, post);
            }
        }

    }

    public void runOnce(Post post, MonitorService service) {
        monitorService = service;

        if (post == null) {
            service.sendMessageToChat("The post has been deleted.");
            return;
        }

        if (post.getRevisionNumber() != 1) {
            VandalisedPost vandalisedPost = getVandalisedPost(post);
            // Check if the post exists
            if (PostUtils.checkVandalisedPost(post)) {
                LOGGER.info("The given post has already been reported.");

                sendPostAlreadyReportedMessage(post, vandalisedPost.getSeverity());
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
            String site = post.getSite();
            String revGuid = post.getRevisionGuid();
            String prevRevGuid = post.getPreviousRevisionGuid();

            String previous = "https://" + site + ".com/revisions/" + prevRevGuid + "/view-source";
            String current = "https://" + post.getSite() + ".com/revisions/" + revGuid + "/view-source";

            // Only fetch the markdown if last body and body exist!
            if (post.getLastBody() != null) {
                lastBodyMarkdown = JsonUtils.getHtml(previous);
            }
            if (post.getBody() != null) {
                bodyMarkdown = JsonUtils.getHtml(current);
            }

            PropertyService propertyService = new PropertyService();
            int higgsId;
            if (propertyService.getProperty("useHiggs").equals("true")) {
                higgsId = HiggsService
                    .getInstance()
                    .registerVandalisedPost(
                        vandalisedPost,
                        lastBodyMarkdown,
                        bodyMarkdown
                    );
            } else {
                higgsId = 0;
            }

            PostUtils.storeVandalisedPost(vandalisedPost, higgsId, lastBodyMarkdown, bodyMarkdown);
            sendVandalismFoundMessage(vandalisedPost, higgsId);
        } catch (Exception exception) {
            LOGGER.error("Error while trying to reportPost.", exception);
        }
    }

    private void sendVandalismFoundMessage(VandalisedPost vandalisedPost, int higgsId) {
        String message = Belisarius.buildMessage(
            higgsId,
            vandalisedPost.getPost(),
            vandalisedPost.getSeverity(),
            Belisarius.POTENTIAL_VANDALISM + vandalisedPost.getReasonMessage()
        );

        monitorService.sendMessageToChat(message);
    }

    private void sendNoVandalismFoundMessage(Post post) {
        String message = Belisarius.buildMessage(
            0,
            post,
            null,
            Belisarius.NO_ISSUES
        );

        monitorService.sendMessageToChat(message);
    }

    private void sendPostAlreadyReportedMessage(Post post, String severity) {
        String message = Belisarius.buildMessage(
            0,
            post,
            severity,
            Belisarius.ALREADY_REPORTED
        );

        monitorService.sendMessageToChat(message);
    }
}
