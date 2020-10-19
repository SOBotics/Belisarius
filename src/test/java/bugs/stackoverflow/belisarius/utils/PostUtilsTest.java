package bugs.stackoverflow.belisarius.utils;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Test
    public void getVandalisedPostTest() {
        try {
            VandalisedPost noVandalism = PostUtils.getVandalisedPost(belisarius.getPost("4"));
            assertEquals(null, noVandalism.getSeverity());
            Post deletedPost = belisarius.getPost("1");
            assertEquals(null, deletedPost);

            Map<String, VandalisedPost> vandalisedPosts = new HashMap<>();
            vandalisedPosts.put("low", PostUtils.getVandalisedPost(belisarius.getPost("64163328"))); // removed code Q
            vandalisedPosts.put("low", PostUtils.getVandalisedPost(belisarius.getPost("60420830"))); // text removed Q
            vandalisedPosts.put("low", PostUtils.getVandalisedPost(belisarius.getPost("64296039"))); // text removed A
            vandalisedPosts.put("low", PostUtils.getVandalisedPost(belisarius.getPost("63769100"))); // both of the above

            // blacklisted word(s)
            vandalisedPosts.put("medium", PostUtils.getVandalisedPost(belisarius.getPost("31638488")));
            vandalisedPosts.put("medium", PostUtils.getVandalisedPost(belisarius.getPost("63938251")));
            vandalisedPosts.put("medium", PostUtils.getVandalisedPost(belisarius.getPost("63965719")));
            vandalisedPosts.put("medium", PostUtils.getVandalisedPost(belisarius.getPost("57907645")));
            vandalisedPosts.put("medium", PostUtils.getVandalisedPost(belisarius.getPost("64123548"))); // very long word

            vandalisedPosts.put("high", PostUtils.getVandalisedPost(belisarius.getPost("62812593"))); // offensive word

            for (Map.Entry<String, VandalisedPost> post : vandalisedPosts.entrySet()) {
                assertEquals(post.getKey(), post.getValue().getSeverity());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}