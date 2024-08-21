package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;
import java.util.List;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OffensiveWordFilterTest {
    @Test
    public void hitTest() throws IOException {
        // offensive word in 
        Post post1 = FilterTestUtils.getSamplePost(
            "This is my question. It is also quite big.",
            "This was my question. It is quite big.",
            "title",
            "title",
            "fuck off everybody",
            "question"
        );

        // answer
        Post post2 = FilterTestUtils.getSamplePost(
            "This is my very helpful lengthy answer.",
            "This is my very helpful lengthy answer.",
            "title",
            null,
            "get lost dumbass",
            "answer"
        );

        // multiple offensive words
        Post post3 = FilterTestUtils.getSamplePost(
            "This is my question. It is also quite big.",
            "This was my question. It is quite big.",
            "title",
            null,
            "shitty question fuck off jerk off this is spam",
            "answer"
        );

        // offensive word in body shouldn't be caught
        Post post4 = FilterTestUtils.getSamplePost(
            "<p>My very interesting question. you suck</p>",
            "<p>This is my question. It is quite big.</p>",
            "title",
            null,
            "normal edit summary",
            "answer"
        );

        assertEquals(new OffensiveWordFilter(0, post1).isHit(), true);
        assertEquals(new OffensiveWordFilter(0, post2).isHit(), true);
        assertEquals(new OffensiveWordFilter(0, post4).isHit(), false);

        OffensiveWordFilter filter3 = new OffensiveWordFilter(0, post3);
        assertEquals(filter3.isHit(), true);
        assertEquals(filter3.getTotalScore(), 5.0);

        assertEquals(
            filter3.getFormattedReasonMessage(),
            "**Edit summary contains offensive words:** (?i)shitty\\squestion, "
                + "(?i)fuck\\soff, (?i)\\bf([\\Wkcuf]*)ck(s|ers?|ed)?, (?i)jerk\\soff, (?i)this\\s*(\\w+)?\\s*spam"
        );

        List<String> reasons = List.of(
            "Contains offensive word: (?i)shitty\\squestion",
            "Contains offensive word: (?i)fuck\\soff",
            "Contains offensive word: (?i)\\bf([\\Wkcuf]*)ck(s|ers?|ed)?",
            "Contains offensive word: (?i)jerk\\soff",
            "Contains offensive word: (?i)this\\s*(\\w+)?\\s*spam"
        );
        List<String> actual = filter3.getReasonName();
        assertTrue(actual.containsAll(reasons) && reasons.containsAll(actual));
    }
}