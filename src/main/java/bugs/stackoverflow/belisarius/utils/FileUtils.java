package bugs.stackoverflow.belisarius.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    public static final String LOGIN_PROPERTIES_FILE = "./properties/login.properties";
    public static final Path OFFENSIVE_WORDS_FILE = Paths.get("./ini/OffensiveWords.csv");
    public static final Path BLACKLISTED_WORDS_FILE = Paths.get("./ini/BlacklistedWords.csv");
    public static final Path REASONS_FILE = Paths.get("./ini/Reasons.csv");
    public static final String DATABASE_FILE = "jdbc:sqlite:./database/belisarius.db";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
    private static final Pattern CSV_REGEX = Pattern.compile("(?:,|\\n|^)(\"(?:(?:\"\")*[^\"]*)*\"|[^\",\\n]*|(?:\\n|$))");

    public static List<String> readFile(Path filepath) {
        List<String> fileContent = new ArrayList<>();
        try {
            fileContent = Files.readAllLines(filepath);
        } catch (IOException exception) {
            LOGGER.error("Failed to read " + filepath.toString() + ". Perhaps the file doesn't exist?", exception);
        }
        return fileContent;
    }

    public static List<String> splitLine(String line) {
        List<String> parts = new ArrayList<>();
        Matcher splitLine = CSV_REGEX.matcher(line);
        while (splitLine.find()) {
            parts.add(splitLine.group(1).replace("\"", ""));
        }
        return parts;
    }
}
