package bugs.stackoverflow.belisarius.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.services.ApiService;
import bugs.stackoverflow.belisarius.utils.PostUtils;

import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PostUtilsTest {
    private final ApiService apiService = new ApiService("stackoverflow");
    private final Belisarius belisarius = new Belisarius("stackoverflow");
    private final JsonObject examplePostObject = JsonParser.parseString("{"
            + "\"owner\": {\"reputation\": 6170, \"user_id\": 4450024, \"display_name\": \"Nu&#241;ito de la Calzada\"},"
            + "\"last_editor\": {\"reputation\": 6170, \"user_id\": 4450024, \"display_name\": \"Nu&#241;ito de la Calzada\"},"
            + "\"last_edit_date\": 1601402403, \"last_activity_date\": 1601402403, \"creation_date\": 1601401000,"
            + "\"post_type\": \"question\", \"post_id\": 64124799, \"title\": \"&quot;; expected &quot;., (&quot;; running JPA query\","
            + "\"link\": \"https://stackoverflow.com/q/64124799\"}").getAsJsonObject();

    @Test
    public void postBeenEditedTest() {
        assertTrue(PostUtils.postBeenEdited(examplePostObject));
        examplePostObject.remove("last_edit_date");
        // Remove last_edit_date (used in postBeenEdited()) and test again
        assertFalse(PostUtils.postBeenEdited(examplePostObject));
        examplePostObject.addProperty("last_edit_date", 1601402403); // add again
    }

    @Test
    public void editorAlsoOwnerTest() {
        assertTrue(PostUtils.editorAlsoOwner(examplePostObject));
        examplePostObject.get("last_editor").getAsJsonObject().addProperty("user_id", 1234);
        assertFalse(PostUtils.editorAlsoOwner(examplePostObject));
    }

    @Test
    public void getPostObjectTest() {
        try {
            JsonObject exampleRevisionObject = apiService.getLatestRevisions("4", 1).get("items").getAsJsonArray().get(0).getAsJsonObject();
            Post postObject = PostUtils.getPost(exampleRevisionObject.getAsJsonObject(), "stackoverflow", "test", "ABCDE12345");
            assertNotNull(postObject);
            assertEquals(4, postObject.getPostId());
            assertEquals("question", postObject.getPostType());
            assertEquals("test", postObject.getTitle());
            assertEquals("ABCDE12345", postObject.getPreviousRevisionGuid());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
    * These test are may become not valid over time since posts changes (new edits/post deleted), for now I'm (@Petter) removing them. Static content would be needed.
    @Test
    public void getVandalisedPostTest() {
        try {
            VandalisedPost noVandalism = PostUtils.getVandalisedPost(belisarius.getPost("4"));
            assertNull(noVandalism.getSeverity());
            Post deletedPost = belisarius.getPost("1");
            assertNull(deletedPost);

            Map<String, List<VandalisedPost>> vandalisedPosts = new HashMap<>();
            List<VandalisedPost> lowSeverityPosts = new ArrayList<>();
            lowSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("66373993"))); // removed code Q
            lowSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("63575223"))); // text removed Q
            lowSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("64296039"))); // text removed A
            lowSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("63769100"))); // both of the above

            List<VandalisedPost> mediumSeverityPosts = new ArrayList<>();
            // blacklisted word(s)
            mediumSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("27421094")));
            mediumSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("31883097")));
            mediumSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("64643310")));
            mediumSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("64123548"))); // very long word

            List<VandalisedPost> highSeverityPosts = new ArrayList<>();
            highSeverityPosts.add(PostUtils.getVandalisedPost(belisarius.getPost("62812593"))); // offensive word

            vandalisedPosts.put("low", lowSeverityPosts);
            vandalisedPosts.put("medium", mediumSeverityPosts);
            vandalisedPosts.put("high", highSeverityPosts);

            for (Map.Entry<String, List<VandalisedPost>> post : vandalisedPosts.entrySet()) {
                for (VandalisedPost vandalisedPost : post.getValue()) {
                    assertEquals(post.getKey(), vandalisedPost.getSeverity());
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    */
}
