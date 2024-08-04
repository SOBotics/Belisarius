package bugs.stackoverflow.belisarius.models;

/**
 * Created by bhargav.h on 01-Oct-16.
 */
public class User {
    private String username;
    private int userId;
    private long reputation;

    public User(
        String username,
        int userId,
        long reputation
    ) {
        this.username = username;
        this.userId = userId;
        this.reputation = reputation;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public long getReputation() {
        return reputation;
    }
}
