package bugs.stackoverflow.belisarius.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CheckUtilsTest {
    private final double percentage = 0.8;

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
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedQuestionWords, null, "question").size(), 6);
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedAnswerWords, null, "answer").size(), 1);
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedQuestionTitleWords, null, "question_title").size(), 1);
        assertEquals(CheckUtils.checkForBlackListedWords(blacklistedWordsInHtml, null, "question").size(), 0);
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

    @Test
    public void jaroWinklerScoreTest() {
        double score1 = CheckUtils.getJaroWinklerScore(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
                + "sed do eiusmod tempor incididunt ut labore et dolore magna "
                + "aliqua. Ut enim ad minim veniam, quis nostrud exercitation "
                + "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis "
                + "aute irure dolor in reprehenderit in voluptate velit esse cillum "
                + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat "
                + "non proident, sunt in culpa qui officia deserunt mollit anim id "
                + "est laborum.",
            "This text has nothing to do with the above one.",
            percentage
        );
        assertTrue(score1 < 0.6); // this should be caught

        double score2 = CheckUtils.getJaroWinklerScore(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
                + "sed do <code>eiusmod tempor incididunt ut</code> labore et dolore magna "
                + "aliqua. <blockquote>Ut enim</blockquote> ad minim veniam, quis nostrud exercitation "
                + "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis "
                + "<pre>aute irure dolor in reprehenderit in voluptate velit esse cillum</pre> "
                + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat "
                + "non <a href=\"#\">proident</a>, sunt in culpa qui officia deserunt mollit anim id "
                + "est laborum.",
            "This text has nothing to do with the above one.",
            percentage
        );
        assertTrue(score2 < 0.6); // this should also be caught

        // and have the same score, as HTML tags are stripped
        assertEquals(score1, score2);

        double score3 = CheckUtils.getJaroWinklerScore(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
                + "sed do eiusmod tempor incididunt ut labore et dolore magna "
                + "aliqua. Ut enim ad minim veniam, quis nostrud exercitation "
                + "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis "
                + "aute irure dolor in reprehenderit in voluptate velit esse cillum "
                + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat "
                + "non proident, sunt in culpa qui officia deserunt mollit anim id "
                + "est laborum.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, "
                + "sed do eiusmoda tempor incididunt ut labore et dolore magna "
                + "aliqua. Ut enim ad minim veniamd, quis nostrud exercitation "
                + "ullamco laboris nisi ut aliquixp ex ea commodo consequat. Duis "
                + "aute irure dolor in reprehendxerit in voluptate velit esse cillum ",
            percentage
        );
        // edit removes text, but does not change body completely
        // => should not be reported
        assertTrue(score3 > 0.6);
    }
}