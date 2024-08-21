package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VeryLongWordFilterTest {
    @Test
    public void hitTest() throws IOException {
        Post post1 = FilterTestUtils.getSamplePost(
            "<p>This is my answerwithaveryveryveryveryverylongwordthatwillbecaught</p>",
            "<p>This is my answer</p>",
            "title",
            null,
            "added some important info",
            "answer"
        );

        // very long word already existed
        Post post2 = FilterTestUtils.getSamplePost(
            "this is my question thisisaveryverylongwordwhichismorethanfiftycharacters",
            "thisisaveryverylongwordwhichismorethanfiftycharacters this is my question",
            "Does this code work?",
            null,
            "deleted some characters in body",
            "question"
        );

        Post post3 = FilterTestUtils.getSamplePost(
            "<p>This is my question and thisisnotalongword.</p>",
            "<p>This is my question</p>",
            "How can I do this?",
            null,
            "deleted my question",
            "question"
        );

        // very long word inside HTML tag
        Post post4 = FilterTestUtils.getSamplePost(
            "<p>This is my question and <code>thisisaveryverylongwordwhichismorethanfiftycharacters</code>.</p>",
            "<p>This is my question</p>",
            "How can I do this?",
            null,
            "deleted my question",
            "question"
        );

        assertEquals(new VeryLongWordFilter(0, post2).isHit(), false);
        assertEquals(new VeryLongWordFilter(0, post3).isHit(), false);
        assertEquals(new VeryLongWordFilter(0, post4).isHit(), false);

        VeryLongWordFilter filter1 = new VeryLongWordFilter(0, post1);
        assertEquals(filter1.isHit(), true);
        // total weight is always 1.0
        assertEquals(filter1.getTotalScore(), 1.0);
    }
}