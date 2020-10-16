package bugs.stackoverflow.belisarius.filters;

import java.util.List;

public interface Filter {
    // check if a post is caught with a specific reason
    boolean isHit();

    // returns the score of the reason
    // e.g. for BlacklistedWord, it will return 1.0
    double getScore();

    // returns the total score of the reason
    // e.g. in case there are two blacklisted words in a post, it will return 2.0
    double getTotalScore();

    // gets the reason message formatted in markdown
    String getFormattedReasonMessage();

    // gets a list of reasons to use for Higgs
    List<String> getReasonName();

    Severity getSeverity();

    enum Severity {
        HIGH, MEDIUM, LOW
    }

    // stored reason caught in database
    void storeHit();
}

