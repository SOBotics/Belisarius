package bugs.stackoverflow.belisarius.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bugs.stackoverflow.belisarius.Belisarius;
import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.models.VandalisedPost;
import bugs.stackoverflow.belisarius.services.ApiService;

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
    public void getPostObjectTest() throws IOException {
        JsonObject sample = apiService.getLatestRevisions("4", 1)
            .get("items").getAsJsonArray()
            .get(0).getAsJsonObject();

        Post postObject = PostUtils.getPost(sample.getAsJsonObject(), "stackoverflow", "test");
        postObject.setPreviousRevisionGuid("ABCDE12345");

        assertNotNull(postObject);

        assertEquals(4, postObject.getPostId());
        assertEquals("question", postObject.getPostType());
        assertEquals("test", postObject.getTitle());
        assertEquals("ABCDE12345", postObject.getPreviousRevisionGuid());

        Post locked = belisarius.getPost("2276572");
        assertEquals(
            locked.getBody(),
            "<p>How do I comment a block of lines in YAML?</p>\n"
        );
        assertEquals(
            locked.getLastBody(),
            "<p>Does anyone know how to comment a block of lines in yaml?</p>\n"
        );
        assertEquals(locked.getRevisionGuid(), "ec79a719-0ebf-48ca-ae89-27739738f2b7".toUpperCase());
        assertEquals(locked.getPreviousRevisionGuid(), "0db0a737-a330-4625-a537-2bf9e73916a3".toUpperCase());
        assertEquals(locked.getComment(), "Active reading [&lt;http://en.wikipedia.org/wiki/YAML&gt;].");
        assertEquals(locked.getTitle(), "How do you do block comments in YAML?");
        assertEquals(locked.getLastTitle(), "How do you do block comment in yaml?");
    }

    @Test
    public void getVandalisedPostTest() {
        VandalisedPost noVandalism = PostUtils.getVandalisedPost(belisarius.getPost("4"));
        assertNull(noVandalism.getSeverity());

        Post deletedPost = belisarius.getPost("1");
        assertNull(deletedPost);

        Map<String, List<String>> vandalisedPosts = new HashMap<>();
        List<String> low = Arrays.asList(
            "66373993", // removed code Q
            "63575223", // text removed Q
            "64296039", // text removed A
            "63769100" // both of the above
        );

        List<String> medium = Arrays.asList(
            // blacklisted word(s)
            "63193055",
            "31883097",
            "64643310",
            "64123548" // very long word
        );

        List<String> high = Arrays.asList("62812593"); // offensive word

        vandalisedPosts.put("low", low);
        vandalisedPosts.put("medium", medium);
        vandalisedPosts.put("high", high);

        for (Map.Entry<String, List<String>> entry : vandalisedPosts.entrySet()) {
            for (String postId : entry.getValue()) {
                Post post = belisarius.getPost(postId);
                VandalisedPost vandalisedPost = PostUtils.getVandalisedPost(post);

                assertEquals(entry.getKey(), vandalisedPost.getSeverity());
            }
        }
    }
}
