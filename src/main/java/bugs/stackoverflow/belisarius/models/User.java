package bugs.stackoverflow.belisarius.models;

/**
 * Created by bhargav.h on 01-Oct-16.
 */
public class User {
    private String username;
    private int userId;
    private long reputation;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getReputation() {
        return reputation;
    }

    public void setReputation(long reputation) {
        this.reputation = reputation;
    }
}
