package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import bugs.stackoverflow.belisarius.services.PropertyService;

public class FileUtilsTest {
    private final PropertyService propertyService = new PropertyService();
    private final Path propertiesFilePath = Paths.get(propertyService.getFilePath()); 

    @Test
    public void readFileTest() {
        // Values acquired reading the properties file should be the same as the ones received from PropertyService
        FileUtils.readFile(propertiesFilePath).forEach(line -> {
            String[] lineSplit = line.split("=");
            assertEquals(propertyService.getProperty(lineSplit[0]), lineSplit[1]);
        });
    }

    @Test
    public void splitLineTest() {
        FileUtils.readFile(FileUtils.BLACKLISTED_WORDS_FILE).forEach(line -> {
            List<String> matches = FileUtils.splitLine(line);

            String wordId = matches.get(0);
            String wordRegex = matches.get(1);
            String postType = matches.get(2);

            String[] expectedTypesArr = { "question", "question_title", "answer" };
            List<String> expectedTypes = Arrays.asList(expectedTypesArr);

            assertDoesNotThrow(() -> Integer.valueOf(wordId)); // the id must be a number
            assertDoesNotThrow(() -> Pattern.compile(wordRegex)); // the regex must be valid
            assertTrue(expectedTypes.contains(postType)); // post type must be either question or answer
        });
    }
}