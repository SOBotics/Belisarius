package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bugs.stackoverflow.belisarius.utils.CommandUtils;

import org.junit.jupiter.api.Test;

public class CommandUtilsTest {
    @Test
    public void checkForCommandTest() {
        assertTrue(CommandUtils.checkForCommand("@Belisarius alive", "alive"));
        assertTrue(CommandUtils.checkForCommand("@Belisarius check 43829 ", "check"));
        assertFalse(CommandUtils.checkForCommand("@Belisarius fake", "stop"));
    }

    @Test
    public void extractDataTest() {
        assertEquals(CommandUtils.extractData("@Belisarius check 12345"), "12345");
        assertEquals(CommandUtils.extractData("@Belisarius reboot"), "");
    }
}