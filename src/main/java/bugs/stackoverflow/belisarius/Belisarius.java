package bugs.stackoverflow.belisarius;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.services.MonitorService;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Belisarius {

    public static final String README = "[ [Belisarius](https://stackapps.com/questions/7473) ";
    public static final String HIGGS_URL = "| [Hippo](https://higgs.sobotics.org/Hippo/report/";
    public static final String ALREADY_REPORTED = "The post has already been reported.";
    public static final String NO_ISSUES = "No issues have been found.";
    public static final String POTENTIAL_VANDALISM = "Potentially harmful edit found. Reason: ";
    private static final Logger LOGGER = LoggerFactory.getLogger(Belisarius.class);

    private long lastPostTime;
    private ApiService apiService;
    private String site;

    public Belisarius(String site) {
        this.lastPostTime = System.currentTimeMillis() / 1000 - 60;
        this.apiService = new ApiService(site);
        this.site = site;
    }

    public static void buildMessage(int higgsId, String postType, String severity, String status,
                                    String allRevs, int revNum, String revUrl, MonitorService service) {
        // Builds the message that is sent to chat. There are three formats:
        // Example 1: potential vandalism - https://chat.stackoverflow.com/transcript/message/50551213
        // Example 2: post already reported - https://chat.stackoverflow.com/transcript/167908?m=50000163
        // Example 3: no issues with the revision - https://chat.stackoverflow.com/transcript/111347?m=50384193
        String message = README;
        if (higgsId != 0) {
            message += HIGGS_URL + higgsId + ") ";
        }
        message += "] [tag:" + postType + "]";
        if (severity != null) {
            message += " [tag:severity-" + severity + "]";
        }
        message += " " + status + " [All revisions](" + allRevs + "). Revision: [" + revNum + "](" + revUrl + ")";
        service.sendMessageToChat(message);
    }

    public List<Post> getPosts() {
        Map<Long, String> postIdsAndTitles = getPostIdsAndTitles();
        List<Post> posts = new ArrayList<>();

        try {
            if (postIdsAndTitles.size() > 0) {
                List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postIdsAndTitles);
                if (postsWithLatestRevisions.size() > 0) {
                    posts.addAll(postsWithLatestRevisions);
                }
            }
        } catch (Exception exception) {
            LOGGER.info("Failed to get posts", exception);
        }

        for (Post post : posts) {
            post.setSite(this.site);
        }

        return posts;
    }

    public Post getPost(String postId) {
        Post post = null;
        Map<Long, String> postIdAndTitle = new HashMap<>();
        postIdAndTitle.put(Long.valueOf(postId), getPostTitle(postId));
        try {
            List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postIdAndTitle);
            if (postsWithLatestRevisions.size() == 1) {
                post = postsWithLatestRevisions.get(0);
            }
        } catch (Exception exception) {
            LOGGER.info("Error while trying to get post with id " + postId, exception);
        }

        if (post != null) {
            post.setSite(this.site);
        }
        return post;
    }

    private Map<Long, String> getPostIdsAndTitles() {
        // Fetch the most active posts in the last minute
        Map<Long, String> postIdsAndTitles = new HashMap<>();
        int page = 1;
        boolean hasMore;

        try {
            do {
                JsonObject postsJson = apiService.getPostIdsByActivityDesc(page, this.lastPostTime);
                JsonArray posts = postsJson.get("items").getAsJsonArray();
                for (JsonElement post : posts) {
                    // for each of the returned items, find the title and the post id and store them in a map
                    JsonObject postJson = post.getAsJsonObject();
                    long postId = postJson.get("post_id").getAsLong();
                    String title = postJson.get("title").getAsString();

                    if (PostUtils.postBeenEdited(postJson) && PostUtils.editorAlsoOwner(postJson)
                        && !postIdsAndTitles.containsKey(postId) && !postIdsAndTitles.containsValue(title)) {
                        postIdsAndTitles.put(postId, title);
                    }
                }
                // Check if posts is null to avoid NPE
                if (posts.size() > 0) {
                    this.lastPostTime = posts.get(0).getAsJsonObject().get("last_activity_date").getAsLong();
                }
                // loop again if there are more results (has_more is true)
                hasMore = postsJson.get("has_more").getAsBoolean();
                page++;
            } while (hasMore);
        } catch (IOException exception) {
            LOGGER.info("Failed to fetch post ids by activity.", exception);
        }

        return postIdsAndTitles;
    }

    private String getPostTitle(String postId) {
        String title = null;
        try {
            JsonObject postJson = apiService.getMorePostInformation(postId);
            for (JsonElement post : postJson.get("items").getAsJsonArray()) {
                title = post.getAsJsonObject().get("title").getAsString();
            }
        } catch (Exception exception) {
            LOGGER.info("Error occurred while trying to get post title for post " + postId, exception);
        }
        return title;
    }

    private List<Post> getPostsWithLatestRevision(Map<Long, String> idsAndTitles) {
        List<Post> revisions = new ArrayList<>();
        Map<Long, List<JsonObject>> postIdsAndJsons = new HashMap<>();
        String[] postIds = idsAndTitles.keySet().stream().map(set -> set.toString()).toArray(String[]::new);
        String semicolonSeparatedIds = String.join(";", postIds);

        boolean hasMore;
        int page = 1;

        try {
            do {
                JsonObject revisionsJson = apiService.getLatestRevisions(semicolonSeparatedIds, page);
                JsonArray revisionsJsonArray = revisionsJson.get("items").getAsJsonArray();

                for (JsonElement revision : revisionsJsonArray) {
                    JsonObject revisionJson = revision.getAsJsonObject();
                    long postId = revisionJson.get("post_id").getAsLong();

                    // revisionList should contain the current and the previous revisions at the end of the loop
                    List<JsonObject> revisionList = postIdsAndJsons.get(postId);
                    // only edits have a revision number, avoid NPEs
                    if (revisionJson.has("revision_number")) {
                        if (revisionList == null) {
                            // if revisionList is null, then insert the most recent revision
                            List<JsonObject> firstRevision = new ArrayList<>();
                            firstRevision.add(revisionJson);
                            postIdsAndJsons.put(postId, firstRevision);
                        } else if (revisionList.size() < 2) {
                            // else insert the previous revision
                            revisionList.add(revisionJson);
                        }
                    }
                }

                // loop again if there are more results (has_more is true)
                hasMore = revisionsJson.get("has_more").getAsBoolean();
                page++;
            } while (hasMore);

            for (Map.Entry<Long, String> idAndTitle : idsAndTitles.entrySet()) {
                long postId = idAndTitle.getKey();
                String title = idAndTitle.getValue();
                List<JsonObject> revisionsList = postIdsAndJsons.get(postId);

                if (revisionsList.size() > 1) {
                    // fetch the previous revision guid from the second revisionList item (previous revision)
                    String previousRevisionGuid = revisionsList.get(1).get("revision_guid").getAsString();
                    revisions.add(PostUtils.getPost(revisionsList.get(0), this.site, title, previousRevisionGuid));
                }
            }

        } catch (Exception exception) {
            LOGGER.info("Error while trying to get latest revisions for some posts", exception);
        }

        return revisions;
    }

}
