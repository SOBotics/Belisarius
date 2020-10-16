package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bugs.stackoverflow.belisarius.utils.CheckUtils;

import org.junit.jupiter.api.Test;

public class CheckUtilsTest {

    // Blacklisted and offensive words strings
    private final String blacklistedQuestionWords = "problem now solved  problem has been now fixed  found my solution  "
                                                  + "answer:  approval overridden  solved:  fixed -";
    private final String blacklistedAnswerWords = "help me  approval overridden";
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
    private final String fewUniquesSecond = "aaaaabbbbbcccccdddddeeeeefffffggggghhhhhiiiiijjjjj"
                                          + "kkkkklllllmmmmmnnnnnoooooabcdefghijklmnoabcabcdefghij";
    private final String notFewUniques = "This is some text a question could potentially have.";

    @Test
    public void blacklstedWordReasonTest() {
        assertTrue(CheckUtils.checkForBlackListedWords(blacklistedQuestionWords, "question").size() == 7);
        assertTrue(CheckUtils.checkForBlackListedWords(blacklistedAnswerWords, "answer").size() == 2);
        assertTrue(CheckUtils.checkForBlackListedWords(blacklistedQuestionTitleWords, "question_title").size() == 1);
        assertTrue(CheckUtils.checkForBlackListedWords(blacklistedWordsInHtml, "question").size() == 0);
    }

    @Test
    public void offensiveWordReasonTest() {
        assertTrue(CheckUtils.checkForOffensiveWords(someOffensiveWords).size() == 2);
    }

    @Test
    public void veryLongWordReasonTest() {
        assertNotNull(CheckUtils.checkForLongWords(veryLongWord));
        assertNull(CheckUtils.checkForLongWords(notLongWord));
        assertNull(CheckUtils.checkForLongWords(longWordInCode));
    }

    @Test
    public void fewUniqueCharactersReasonTest() {
        assertNotNull(CheckUtils.checkForFewUniqueCharacters(fewUniquesFirst));
        assertNotNull(CheckUtils.checkForFewUniqueCharacters(fewUniquesSecond));
        assertNull(CheckUtils.checkForFewUniqueCharacters(notFewUniques));
    }

    @Test
    public void checkIfNoCodeBlockTest() {
        assertTrue(CheckUtils.checkIfNoCodeBlock("Not a code block"));
        assertFalse(CheckUtils.checkIfNoCodeBlock("<code>This is a code block</code>"));
    }
}