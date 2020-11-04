package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bugs.stackoverflow.belisarius.utils.CheckUtils;

import org.junit.jupiter.api.Test;

public class CheckUtilsTest {

    // Blacklisted and offensive words strings
    private final String blacklistedQuestionWords = "problem now solved  problem has been now fixed  found my solution  "
                                                  + "answer:  approval overridden  solved: ";
    private final String blacklistedAnswerWords = "approval overridden";
    private final String blacklistedQuestionTitleWords = "How do to Y with Z? [SOLVED]";
    private final String blacklistedWordsInHtml = "<pre><code>Blacklisted word in code: answer:</code></pre>";
    private final String someOffensiveWords = "shitty question  bullshit";

    // Other reason's test strings
    private final String veryLongWord = "thisisaveryverylongwordwhichismorethanfiftycharacters";
    private final String notLongWord = "thisisnotalongword";
    private final String longWordInCode = "<pre><code>thisisaveryverylongwordmorethanfiftycharacterswhichisincode</code></pre>"
                                        + "<a href=\"https://Verylongwordwhichisahrefwhichisstippedblablahhblahblah\">"
                                        + "Verylongwordwhichisinatagwhichisstippedblahblahblah</a>";
    private final String fewUniquesFirst = "aaaaaabbbbbbccccccddddddeeeeeeabcde";
    private final String fewUniquesSecond = "aaaaaabbbbbbccccccddddddeeeeeeffffffgggggghhhhhhiiiiijjjjjjkkkkkkllllll";
    private final String notFewUniques = "This is some text a question could potentially have.";
    private final String repeatedWords = "dddddddddd dddddddddd dddddddddd dddddddddd";
    private final String notRepeatedWords = "This is is just some some normal text.";

    @Test
    public void blacklistedWordReasonTest() {
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedQuestionWords, "question").size(), 6);
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedAnswerWords, "answer").size(), 1);
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedQuestionTitleWords, "question_title").size(), 1);
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedWordsInHtml, "question").size(), 0);
    }

    @Test
    public void offensiveWordReasonTest() {
        assertEquals(CheckUtils.checkForOffensiveWords(someOffensiveWords).size(), 2);
    }

    @Test
    public void veryLongWordReasonTest() {
        assertEquals(veryLongWord, CheckUtils.checkForLongWords(veryLongWord));
        assertNull(CheckUtils.checkForLongWords(notLongWord));
        assertNull(CheckUtils.checkForLongWords(longWordInCode));
    }

    @Test
    public void fewUniqueCharactersReasonTest() {
        assertEquals("abcde", CheckUtils.checkForFewUniqueCharacters(fewUniquesFirst));
        assertEquals("abcdefghijkl", CheckUtils.checkForFewUniqueCharacters(fewUniquesSecond));
        assertNull(CheckUtils.checkForFewUniqueCharacters(notFewUniques));
    }

    @Test
    public void checkIfNoCodeBlockTest() {
        assertTrue(CheckUtils.checkIfNoCodeBlock("Not a code block"));
        assertFalse(CheckUtils.checkIfNoCodeBlock("<code>This is a code block</code>"));
    }

    @Test
    public void repeatedWordTest() {
        assertEquals(CheckUtils.checkRepeatedWords(repeatedWords).size(), 1);
        assertEquals(CheckUtils.checkRepeatedWords(notRepeatedWords).size(), 6);
    }
}