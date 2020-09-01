package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {


    public static String readLineFromFileStartswith(String filename, String message) throws IOException {
        List<String> lines = readFile(filename);
        for (String line : lines) {
            if (line.trim().toLowerCase().startsWith(message.trim().toLowerCase())) {
                return line.trim();
            }
        }
        return null;
    }

    public static List<String> readFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename));
    }

    public static void appendToFile(String filename, String word) throws IOException {
        Files.write(Paths.get(filename), Arrays.asList(word), StandardOpenOption.APPEND, StandardOpenOption.WRITE);
    }

    public static void removeFromFile(String filename, String message) throws IOException {
        List<String> lines = readFile(filename);
        List<String> newLines = new ArrayList<>();
        for (String line:lines) {
            if (line.trim().toLowerCase().equals(message.trim().toLowerCase())) {
                continue;
            }
            newLines.add(line);
        }
        Files.write(Paths.get(filename), newLines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

}
