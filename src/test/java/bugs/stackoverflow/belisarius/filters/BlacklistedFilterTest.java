package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BlacklistedFilterTest {
    @Test
    public void hitTest() throws IOException {
        // blacklisted word added after edit
        Post post1 = FilterTestUtils.getSamplePost(
            "This was my question. PROBLEM SOLVED: do this",
            "This is my question. It is quite big.",
            "title",
            "title",
            "edit",
            "question"
        );

        // blacklisted word existed before edit:
        Post post2 = FilterTestUtils.getSamplePost(
            "Minor edit, This is my question, Minor edit. PrObLEM fIXeD: do this",
            "This is my question. PrObLEM fIXeD: do this",
            "title",
            "title",
            "edit",
            "question"
        );

        // blacklisted word inside HTML tag
        Post post3 = FilterTestUtils.getSamplePost(
            "This was my question. <code>PROBLEM SOLVED</code>: do this",
            "This is my question. It is quite big.",
            "title",
            "title",
            "edit",
            "question"
        );

        // more than one blacklisted words
        Post post4 = FilterTestUtils.getSamplePost(
            "This was my question. problem solved. answer: do this",
            "This is my question. It is quite big.",
            "title",
            "[SOLVED] title",
            "problem fixed, approval overriden",
            "question"
        );

        assertEquals(new BlacklistedFilter(0, post1).isHit(), true);
        assertEquals(new BlacklistedFilter(0, post2).isHit(), false);
        assertEquals(new BlacklistedFilter(0, post3).isHit(), false);

        BlacklistedFilter filter4 = new BlacklistedFilter(0, post4);
        assertEquals(filter4.isHit(), true);
        // 1 (title) + 1 (edit summary) + 2 (post body) = 4
        assertEquals(filter4.getTotalScore(), 4.0);
    }
}