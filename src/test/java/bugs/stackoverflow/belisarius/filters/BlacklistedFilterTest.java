package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        // body would be caught if post was a question
        Post post5 = FilterTestUtils.getSamplePost(
            "This is my answer. answer: do this",
            "This was my answer. It was quite big.",
            "title",
            null,
            "removed some characters from body",
            "answer"
        );

        assertEquals(new BlacklistedFilter(0, post1).isHit(), true);
        assertEquals(new BlacklistedFilter(0, post2).isHit(), false);
        assertEquals(new BlacklistedFilter(0, post3).isHit(), false);
        assertEquals(new BlacklistedFilter(0, post5).isHit(), false);

        BlacklistedFilter filter4 = new BlacklistedFilter(0, post4);
        assertEquals(filter4.isHit(), true);
        // 1 (title) + 1 (edit summary) + 2 (post body) = 4
        assertEquals(filter4.getTotalScore(), 4.0);

        List<String> reasons = List.of(
            "Contains blacklisted word: (?i)(problem|error|issue)\\s+(re|now\\s+)?(solved|fixed)",
            "Contains blacklisted word: (?:problem|error|issue).{0,10}(?<!n't|not)\\s+(?:now\\s+)?fixed(?!\\W*\\?)",
            "Contains blacklisted word: (?i)(problem|error|issue)\\s+(re|now\\s+)?(solved|fixed)",
            "Contains blacklisted word: (?i)(answer -|answer:)"
        );
        List<String> actual = filter4.getReasonName();

        assertTrue(actual.containsAll(reasons) && reasons.containsAll(actual));
        assertEquals(
            filter4.getFormattedReasonMessage(),
            "**Body contains blacklisted words:** (?i)(problem|error|issue)\\s+(re|now\\s+)?(solved|fixed), "
                + "(?i)(answer -|answer:) **Edit summary contains blacklisted words:** "
                + "(?i)(problem|error|issue)\\s+(re|now\\s+)?(solved|fixed), "
                + "(?:problem|error|issue).{0,10}(?<!n't|not)\\s+(?:now\\s+)?fixed(?!\\W*\\?)"
        );
    }
}