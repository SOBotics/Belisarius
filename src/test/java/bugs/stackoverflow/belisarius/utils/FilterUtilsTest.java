package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FilterUtilsTest {
    @Test
    public void pluraliseTest() {
        assertEquals(FilterUtils.pluralise("word", 1), "word");
        assertEquals(FilterUtils.pluralise("word", 0), "words");
        assertEquals(FilterUtils.pluralise("word", 2), "words");
    }
}