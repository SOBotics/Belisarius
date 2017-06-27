package bugs.stackoverflow.belisarius.filters;

public interface Filter {
    public boolean isHit();
    public double getScore();
    public String getDescription();
}

