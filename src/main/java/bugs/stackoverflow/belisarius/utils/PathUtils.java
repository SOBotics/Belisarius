package bugs.stackoverflow.belisarius.utils;

public class PathUtils {

    public static final String SOBOTICS_PROPERTIES_FILE = "./properties/SOBotics.properties";
    public static final String LOGIN_PROPERTIES_FILE = "./properties/login.properties";
    public static final String DATABASE_FILE = "jdbc:sqlite:./database/belisarius.db";

    private static final String BLACKLIST_TITLE_FILE = "./ini/BlackListedTitleWords.txt";
    private static final String BLACKLIST_QUESTION_FILE = "./ini/BlackListedQuestionWords.txt";
    private static final String BLACKLIST_ANSWER_FILE = "./ini/BlackListedAnswerWords.txt";
    private static final String BLACKLIST_QUESTION_COMMENT_FILE = "./ini/BlackListedQuestionEditSummaryWords.txt";
    private static final String BLACKLIST_ANSWER_COMMENT_FILE = "./ini/BlackListedAnswerEditSummaryWords.txt";
    private static final String OFFENSIVE_COMMENT_FILE = "./ini/OffensiveEditSummaryWords.txt";

    public String getBlacklistTitleFile() {
        return BLACKLIST_TITLE_FILE;
    }

    public String getBlacklistQuestionFile() {
        return BLACKLIST_QUESTION_FILE;
    }

    public String getBlacklistAnswerFile() {
        return BLACKLIST_ANSWER_FILE;
    }

    public String getBlacklistQuestionEditSummaryFile() {
        return BLACKLIST_QUESTION_COMMENT_FILE;
    }

    public String getBlacklistAnswerEditSummaryFile() {
        return BLACKLIST_ANSWER_COMMENT_FILE;
    }

    public String offensiveEditSummaryFile() {
        return OFFENSIVE_COMMENT_FILE;
    }

}
