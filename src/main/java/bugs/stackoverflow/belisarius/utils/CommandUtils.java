package bugs.stackoverflow.belisarius.utils;

import java.util.Arrays;

public class CommandUtils {
    public static String extractData(String message) {
        String[] parts = message.split(" ");

        return String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
    }

}
