package bugs.stackoverflow.belisarius;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
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
    private final ApiService apiService;
    private final String site;

    public Belisarius(String site) {
        this.lastPostTime = System.currentTimeMillis() / 1000 - 60;
        this.apiService = new ApiService(site);
        this.site = site;
    }

    public static String buildMessage(
        int higgsId,
        Post post,
        String severity,
        String status
    ) {
        String postType = post.getPostType().toLowerCase();
        String allRevs = post.getAllRevisionsUrl();
        String revUrl = post.getRevisionUrl();

        int revNum = post.getRevisionNumber();

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

        return message;
    }

    public Post getPost(String postId) {
        String title = this.getPostTitle(postId);

        if (title == null) {
            return null;
        }

        List<Post> posts = this
            .getRevisions(
                Map.of(Long.valueOf(postId), title)
            );

        return posts.isEmpty()
            ? null
            : posts.get(0);
    }

    public List<Post> getPosts() {
        // title *needs* to be stored, since it's not always returned
        // in /posts/{ids}/revisions
        Map<Long, String> postIds = getActivePosts();

        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }

        return getRevisions(postIds);
    }

    private Map<Long, String> getActivePosts() {
        // Fetch the most active posts in the last minute
        // { id: title }
        Map<Long, String> allPosts = new HashMap<>();
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

                    if (PostUtils.postBeenEdited(postJson) // post must have been edited
                        && PostUtils.editorAlsoOwner(postJson) // by its owner
                        && !allPosts.containsKey(postId)
                        && !allPosts.containsValue(title)
                    ) {
                        allPosts.put(postId, title);
                    }
                }

                // Check if posts is null to avoid NPE
                if (posts.size() > 0) {
                    this.lastPostTime = posts
                        .get(0).getAsJsonObject()
                        .get("last_activity_date").getAsLong();
                }

                // loop again if there are more results (has_more is true)
                hasMore = postsJson.get("has_more").getAsBoolean();
                page++;
            } while (hasMore);
        } catch (IOException exception) {
            LOGGER.error("Failed to fetch post ids by activity.", exception);
        }

        return allPosts;
    }

    private String getPostTitle(String postId) {
        try {
            JsonObject postJson = apiService.getMorePostInformation(postId);
            JsonArray items = postJson.get("items").getAsJsonArray();

            if (items.isEmpty()) {
                return null;
            } else {
                return items.get(0).getAsJsonObject().get("title").getAsString();
            }
        } catch (IOException exception) {
            LOGGER.error(
                "Error occurred while trying to get post title for post " + postId,
                exception
            );
        }

        return null;
    }

    private List<Post> getRevisions(Map<Long, String> idsAndTitles) {
        Map<Long, Post> postsByIds = new HashMap<>();

        String postIds = String.join(
            ";",
            idsAndTitles
                .keySet()
                .stream()
                .map(Object::toString)
                .toArray(String[]::new)
        );

        boolean hasMore = true;
        int page = 1;

        // for each post we are interested in, fetch the 2 most recent revisions
        // (we need the older revision GUID to fetch the markdown of the post)
        try {
            while (hasMore) {
                JsonObject response = apiService.getLatestRevisions(postIds, page);
                JsonArray items = response.get("items").getAsJsonArray();

                for (JsonElement revision : items) {
                    JsonObject json = revision.getAsJsonObject();
                    long postId = json.get("post_id").getAsLong();

                    // only edits have a revision number, avoid NPEs
                    if (!json.has("revision_number")) {
                        continue;
                    }

                    // we have encountered a revision of this post before
                    // store the revision guid
                    if (postsByIds.containsKey(postId)) {
                        String prevRevGuid = json.get("revision_guid").getAsString();

                        postsByIds
                            .get(postId)
                            .setPreviousRevisionGuid(prevRevGuid);
                    } else {
                        Post post = PostUtils.getPost(
                            json,
                            this.site,
                            idsAndTitles.get(postId)
                        );

                        postsByIds.put(postId, post);
                    }
                }

                // loop again if there are more results (has_more is true)
                hasMore = response.get("has_more").getAsBoolean();
                page++;
            }
        } catch (IOException exception) {
            LOGGER.error("Error while trying to get latest revisions for some posts", exception);
        }

        return new ArrayList<>(postsByIds.values());
    }
}
