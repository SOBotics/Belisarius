package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class JsonUtilsTest {
    @Test
    public void escapeHtmlEncodingTest() {
        assertEquals(JsonUtils.escapeHtmlEncoding("John O&#39;Connor"), "John O'Connor");
        assertEquals(JsonUtils.escapeHtmlEncoding("Santiago Comesa&#241;a"), "Santiago Comesaña");
    }

    @Test
    public void sanitizeChatMessageTest() {
        assertEquals("\\[\\]\\*\\_\\`", JsonUtils.sanitizeChatMessage("[]*_`"));
        // test case from https://chat.stackoverflow.com/messages/51803561/history
        assertEquals("reio09ug\\[t8", JsonUtils.sanitizeChatMessage("reio09ug[t8"));
    }

    @Test
    public void getHtmlTest() throws IOException {
        // this revision was chosen because the post is locked and it is short
        String markdown = JsonUtils.getHtml(
            "https://stackoverflow.com/revisions/272c30b5-e20b-407f-ab1a-a379e312af0d/view-source"
        );
        assertEquals(
            markdown,
            "How can I redirect the user from one page to another using jQuery or pure JavaScript?"
        );

        assertNull(JsonUtils.getHtml("https://stackoverflow.com/revisions/ABCDE/view-source"));
    }
}