package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RepeatedWordFilterTest {
    @Test
    public void hitTest() throws IOException {
        Post post1 = FilterTestUtils.getSamplePost(
            "<p>" + "these words are repeated ".repeat(10) + "</p>",
            "<p>nobody cares</p>",
            "title",
            null,
            "vandalised my post",
            "question"
        );

        // https://higgs.sobotics.org/Hippo/report/84303
        Post post2 = FilterTestUtils.getSamplePost(
            "<p>" + "Issue resolved\n".repeat(3) + "</p>",
            "<p>This is some text</p>",
            "Does this code work?",
            null,
            "deleted 169 characters in body",
            "answer"
        );

        Post post3 = FilterTestUtils.getSamplePost(
            "<p>" + "Deleted ".repeat(100) + "</p>",
            "This was the last question body.",
            "DeletedDeletedDeletedDeletedDeleted",
            null,
            "deleted my question",
            "question"
        );

        RepeatedWordFilter filter1 = new RepeatedWordFilter(0, post1);
        RepeatedWordFilter filter2 = new RepeatedWordFilter(0, post2);
        RepeatedWordFilter filter3 = new RepeatedWordFilter(0, post3);

        assertEquals(filter1.isHit(), true);
        assertEquals(filter2.isHit(), true);
        assertEquals(filter3.isHit(), true);

        assertEquals(filter1.getTotalScore(), 2.0);
        assertEquals(filter2.getTotalScore(), 4.0);
        assertEquals(filter3.getTotalScore(), 5.0);

        assertEquals(filter1.getReasonName().get(0), "Contains repeated words");

        assertEquals(
            filter1.getFormattedReasonMessage(),
            "**Post contains repeated words:** repeatedthesearewords"
        );
        assertEquals(
            filter2.getFormattedReasonMessage(),
            "**Post contains repeated words:** Issueresolved"
        );
        assertEquals(
            filter3.getFormattedReasonMessage(),
            "**Post contains repeated word:** Deleted"
        );
    }
}