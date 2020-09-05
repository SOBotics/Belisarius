package bugs.stackoverflow.belisarius.filters;

public interface Filter {
    boolean isHit();

    double getScore();

    String getFormattedReasonMessage();

    String getReasonName();

    Severity getSeverity();

    enum Severity {
        HIGH, MEDIUM, LOW
    }

    void storeHit();
}

