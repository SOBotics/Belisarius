package bugs.stackoverflow.belisarius.filters;

public interface Filter {
    public boolean isHit();

    public double getScore();

    public String getFormattedReasonMessage();

    public String getReasonName();

    public Severity getSeverity();

    public enum Severity {
        HIGH, MEDIUM, LOW
    }

    public void storeHit();
}

