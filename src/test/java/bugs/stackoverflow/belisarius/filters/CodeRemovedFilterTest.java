package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CodeRemovedFilterTest {
    @Test
    public void hitTest() throws IOException {
        // body is null
        Post post1 = FilterTestUtils.getSamplePost(
            null,
            "don't care what <blockquote>this</blockquote> text <code>says</code>",
            "title1",
            "title",
            "edit",
            "question"
        );

        // last body is null
        Post post2 = FilterTestUtils.getSamplePost(
            "don't care what this text says",
            null,
            "title",
            null,
            "edit",
            "question"
        );

        // code removed in answer (shouldn't be considered)
        Post post3 = FilterTestUtils.getSamplePost(
            "This is the new answer body.",
            "This was the last answer body.",
            "title",
            null,
            "edit",
            "answer"
        );

        // code removed in question
        Post post4 = FilterTestUtils.getSamplePost(
            "<p>Oops! All code was removed!</p>",
            "<p>Question text includes <code>some inline code</code>"
                + ", but some <pre><code>blocks of code</code></pre>, too</p>",
            "title",
            null,
            "removed 20 characters from body",
            "question"
        );

        assertEquals(new CodeRemovedFilter(0, post1).isHit(), false);
        assertEquals(new CodeRemovedFilter(0, post2).isHit(), false);
        assertEquals(new CodeRemovedFilter(0, post3).isHit(), false);

        CodeRemovedFilter filter4 = new CodeRemovedFilter(0, post4);
        assertEquals(filter4.isHit(), true);
        // total score is always 1
        assertEquals(filter4.getTotalScore(), 1.0);
        assertEquals(filter4.getFormattedReasonMessage(), "**Code removed**");
        assertEquals(filter4.getReasonName().get(0), "Code removed");
    }
}