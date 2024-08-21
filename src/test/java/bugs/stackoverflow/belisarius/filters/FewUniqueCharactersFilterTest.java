package bugs.stackoverflow.belisarius.filters;

import java.io.IOException;

import bugs.stackoverflow.belisarius.models.Post;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FewUniqueCharactersFilterTest {
    @Test
    public void hitTest() throws IOException {
        // https://metasmoke.erwaysoftware.com/post/493704
        Post post1 = FilterTestUtils.getSamplePost(
            "<p>.......................................................... ...</p>",
            "<p>nobody cares</p>",
            "Mmmmmmmmmmmmmmmmmmm",
            "nobody cares",
            "vandalised my post",
            "question"
        );

        // last body is null
        Post post2 = FilterTestUtils.getSamplePost(
            "<p>??????????????????????????????</p>",
            "<p>This is some text</p>",
            "Does this code work?",
            null,
            "edit",
            "answer"
        );

        // https://higgs.sobotics.org/Hippo/report/88186
        Post post3 = FilterTestUtils.getSamplePost(
            "DeletedDeletedDeletedDeletedDeletedDeletedDeletedDeletedDeletedDeletedDeletedDeleted",
            "This was the last question body.",
            "DeletedDeletedDeletedDeletedDeleted",
            null,
            "Deleted",
            "question"
        );

        // few unique characters in code
        Post post4 = FilterTestUtils.getSamplePost(
            "<p>Code: <code>dddddddddddddddddddddddddddddddddddddddd</code></p>",
            "<p>Question text includes <code>some inline code</code>"
                + ", some <pre><code>blocks of code</code></pre> "
                + ", and <blockquote>some quotes</blockquote> as well</p>",
            "title",
            null,
            "removed 20 characters from body",
            "question"
        );

        assertEquals(new FewUniqueCharactersFilter(0, post1).isHit(), true);
        assertEquals(new FewUniqueCharactersFilter(0, post2).isHit(), true);
        assertEquals(new FewUniqueCharactersFilter(0, post4).isHit(), false);

        FewUniqueCharactersFilter filter3 = new FewUniqueCharactersFilter(0, post3);
        assertEquals(filter3.isHit(), true);
        // total score is always 1
        assertEquals(filter3.getTotalScore(), 1.0);
    }
}