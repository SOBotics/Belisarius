package bugs.stackoverflow.belisarius;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sobotics.chatexchange.chat.Room;

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

    public static void buildMessage(Room room, int higgsId, String postType, String severity,
                                      String status, String allRevs, int revNum, String revUrl) {
        String message = README;
        if (higgsId != 0) {
            message += HIGGS_URL + String.valueOf(higgsId) + ") ";
        }
        message += "] [tag:" + postType + "]";
        if (severity != null) {
            message += " [tag:severity-" + severity + "]";
        }
        message += " " + status + " [All revisions](" + allRevs + "). Revision: [" + String.valueOf(revNum) + "](" + revUrl + ")";
        room.send(message);
    }

    public List<Post> getPosts() {
        Map<Long, String> postIdsAndTitles = getPostIdsAndTitles();
        List<Post> posts = new ArrayList<>();

        if (postIdsAndTitles.size() > 0) {
            List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postIdsAndTitles);
            if (postsWithLatestRevisions.size() > 0) {
                posts.addAll(postsWithLatestRevisions);
            }
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
            if (postId != null) {
                List<Post> postsWithLatestRevisions = getPostsWithLatestRevision(postIdAndTitle);
                if (postsWithLatestRevisions.size() == 1) {
                    post = postsWithLatestRevisions.get(0);
                }
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
        Map<Long, String> postIdsAndTitles = new HashMap<>();

        JsonObject postsJson;
        try {
            long lastActivityDate = 0;
            long maxActivityDate = this.lastPostTime;
            int page = 1;
            do {
                postsJson = apiService.getPostIdsByActivityDesc(page);
                for (JsonElement post : postsJson.get("items").getAsJsonArray()) {
                    lastActivityDate = post.getAsJsonObject().get("last_activity_date").getAsLong();

                    if (PostUtils.postBeenEdited(post) && PostUtils.editorAlsoOwner(post) && lastPostTime < lastActivityDate) {
                        long postId = post.getAsJsonObject().get("post_id").getAsLong();
                        String title = post.getAsJsonObject().get("title").getAsString();
                        if (!postIdsAndTitles.containsKey(postId) && !postIdsAndTitles.containsValue(title)) {
                            postIdsAndTitles.put(postId, title);
                        }
                    }

                    if (maxActivityDate < lastActivityDate) {
                        maxActivityDate = lastActivityDate;
                    }
                }
                page++;
            } while (lastPostTime < lastActivityDate & page < 10);
            this.lastPostTime = maxActivityDate;
        } catch (Exception exception) {
            LOGGER.info("Error while trying to fetch posts by activity.", exception);
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
        String[] postIds = idsAndTitles.keySet().stream().map(set -> set.toString()).toArray(String[]::new);
        boolean hasMore = false;
        Iterator<Map.Entry<Long, String>> iter = idsAndTitles.entrySet().iterator();

        do {
            try {
                JsonObject postsJson = apiService.getLatestRevisions(String.join(";", postIds));
                hasMore = postsJson.get("has_more").getAsBoolean();

                while (iter.hasNext()) {
                    Map.Entry<Long, String> entry = iter.next();
                    Long postId = entry.getKey();
                    String title = entry.getValue();
                    int revisionNo = 0;
                    Post revision = null;
                    JsonObject getPostJson = null;
                    for (JsonElement post : postsJson.get("items").getAsJsonArray()) {
                        JsonObject postJson = post.getAsJsonObject();
                        if (postJson.get("post_id").getAsInt() == postId.intValue() && postJson.has("revision_number")) {
                            int currentRevisionNumber = postJson.get("revision_number").getAsInt();
                            // if revisionNo is 0, then it's the post's latest revision
                            // else it is the second most recent revision
                            if (revisionNo == 0) {
                                revisionNo = currentRevisionNumber;
                                getPostJson = postJson;
                            } else if (revisionNo == currentRevisionNumber + 1) {
                                String prevRevisionGuid = postJson.get("revision_guid").getAsString();
                                revision = PostUtils.getPost(getPostJson, site, title, prevRevisionGuid);
                                break;
                            }
                        }
                    }
                    if (revision != null) {
                        revisions.add(revision);
                    }
                    iter.remove();
                }
            } catch (Exception exception) {
                LOGGER.info("Error while trying to get latest revisions for some posts", exception);
            }
        } while (hasMore && postIds.length > 0);

        return revisions;
    }

}
