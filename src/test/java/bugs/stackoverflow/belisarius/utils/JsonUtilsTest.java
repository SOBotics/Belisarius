package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bugs.stackoverflow.belisarius.utils.JsonUtils;

import org.junit.jupiter.api.Test;

public class JsonUtilsTest {
    private final String revisionUrl = "https://stackoverflow.com/revisions/db207fcc-5e19-4bca-872c-add2541753e3/view-source";

    @Test
    public void escapeHtmlEncodingTest() {
        assertEquals(JsonUtils.escapeHtmlEncoding("John O&#39;Connor"), "John O'Connor");
        assertEquals(JsonUtils.escapeHtmlEncoding("Santiago Comesa&#241;a"), "Santiago Comesaña");
    }

    @Test
    public void getHtmlTest() throws Exception {
        assertNotNull(JsonUtils.getHtml(revisionUrl));
        assertThrows(IOException.class, () -> { JsonUtils.getHtml("https://stackoverflow.com/revisions/ABCDE/view-source"); });
    }
}