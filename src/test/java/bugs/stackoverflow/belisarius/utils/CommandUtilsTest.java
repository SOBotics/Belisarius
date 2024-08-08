package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandUtilsTest {
    @Test
    public void extractDataTest() {
        assertEquals(CommandUtils.extractData("@Belisarius check 12345"), "12345");
        assertEquals(CommandUtils.extractData("@Belisarius reboot"), "");
    }
}