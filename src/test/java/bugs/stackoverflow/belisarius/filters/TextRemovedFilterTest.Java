package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RepeatedWordFilterTest {
    @Test
    public void hitTest() throws IOException {
        Post post1 = FilterTestUtils.getSamplePost(
            "<p>This is a normal edit to my question</p>",
            "<p>This is my question</p>",
            "title",
            null,
            "added some important info",
            "question"
        );

        // https://higgs.sobotics.org/Hippo/report/84303
        Post post2 = FilterTestUtils.getSamplePost(
            "This text has nothing to do with the above one.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
                + "sed do eiusmod tempor incididunt ut labore et dolore magna "
                + "aliqua. Ut enim ad minim veniam, quis nostrud exercitation "
                + "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis "
                + "aute irure dolor in reprehenderit in voluptate velit esse cillum "
                + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat "
                + "non proident, sunt in culpa qui officia deserunt mollit anim id "
                + "est laborum.",
            "Does this code work?",
            null,
            "deleted 169 characters in body",
            "answer"
        );

        Post post3 = FilterTestUtils.getSamplePost(
            "I removed my question",
            "<p>" + "This is a valid question no doubt. ".repeat(100) + "</p>",
            "How can I do this?",
            null,
            "deleted my question",
            "question"
        );


        assertEquals(new TextRemovedFilter(0, post1).isHit(), false);
        assertEquals(new TextRemovedFilter(0, post2).isHit(), true);

        TextRemovedFilter filter3 = new TextRemovedFilter(0, post3);
        assertEquals(filter3.isHit(), true);
        // total weight is always 1.0
        assertEquals(filter3.getTotalScore(), 1.0);
    }
}