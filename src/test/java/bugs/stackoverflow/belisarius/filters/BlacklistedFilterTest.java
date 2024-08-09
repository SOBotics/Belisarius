package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;
import bugs.stackoverflow.belisarius.utils.PostUtils;
import bugs.stackoverflow.belisarius.services.ApiService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

public class BlacklistedFilterTest {
    private final ApiService apiService = new ApiService("stackoverflow");

    @Test
    public void hitTest() throws IOException {
        // blacklisted word added after edit
        Post post1 = getSamplePost(
            "This was my question. PROBLEM SOLVED: do this",
            "This is my question. It is quite big.",
            "title",
            "title",
            "edit"
        );

        // blacklisted word existed before edit:
        Post post2 = getSamplePost(
            "Minor edit, This is my question, Minor edit. PrObLEM fIXeD: do this",
            "This is my question. PrObLEM fIXeD: do this",
            "title",
            "title",
            "edit"
        );

        // blacklisted word inside HTML tag
        Post post3 = getSamplePost(
            "This was my question. <code>PROBLEM SOLVED</code>: do this",
            "This is my question. It is quite big.",
            "title",
            "title",
            "edit"
        );

        // more than one blacklisted words
        Post post4 = getSamplePost(
            "This was my question. problem solved. answer: do this",
            "This is my question. It is quite big.",
            "title",
            "[SOLVED] title",
            "problem fixed, approval overriden"
        );

        assertEquals(new BlacklistedFilter(0, post1).isHit(), true);
        assertEquals(new BlacklistedFilter(0, post2).isHit(), false);
        assertEquals(new BlacklistedFilter(0, post3).isHit(), false);

        BlacklistedFilter filter4 = new BlacklistedFilter(0, post4);
        assertEquals(filter4.isHit(), true);
        // 1 (title) + 1 (edit summary) + 2 (post body) = 4
        assertEquals(filter4.getTotalScore(), 4.0);
    }

    private Post getSamplePost(
        String body,
        String lastBody,
        String title,
        String lastTitle,
        String summary
    ) throws IOException {
        // choosing this post because it is locked
        // if a new revision appears, edit .get(2) accordingly
        JsonObject json = apiService.getLatestRevisions("2276572", 1)
            .get("items").getAsJsonArray()
            .get(2).getAsJsonObject();

        json.addProperty("body", body);
        json.addProperty("last_body", lastBody);
        json.addProperty("title", title);
        json.addProperty("last_title", lastTitle);
        json.addProperty("comment", summary);

        return PostUtils.getPost(json, "stackoverflow", "title");
    }
}