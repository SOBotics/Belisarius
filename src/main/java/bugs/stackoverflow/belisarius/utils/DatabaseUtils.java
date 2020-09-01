package bugs.stackoverflow.belisarius.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.database.SQLiteConnection;
import bugs.stackoverflow.belisarius.models.Chatroom;
import bugs.stackoverflow.belisarius.models.Higgs;
import bugs.stackoverflow.belisarius.models.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtils.class);

    public static void createVandalisedPostTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS VandalisedPost(PostId integer, \n"
                                                            + " CreationDate integer, \n"
                                                            + " RevisionId integer, \n"
                                                            + " RoomId integer, \n"
                                                            + " OwnerId integer, \n"
                                                            + " Title text, \n"
                                                            + " LastTitle text, \n"
                                                            + " Body text, \n"
                                                            + " LastBody text, \n"
                                                            + " IsRollback integer, \n"
                                                            + " PostType text, \n"
                                                            + " Comment text, \n"
                                                            + " Site text, \n"
                                                            + " Severity text, \n"
                                                            + " HiggsId integer, \n"
                                                            + " RevisionGuid text, \n"
                                                            + " PreviousRevisionGuid text, \n"
                                                            + " LastBodyMarkdown text, \n"
                                                            + " BodyMarkdown text, \n"
                                                            + " PRIMARY KEY(PostId, RevisionId, RoomId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create VandalisedPost table.", e);
        }
    }

    public static void createHiggsTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Higgs(BotId integer, \n"
                                                   + " SecretKey text, \n"
                                                   + " Url text, \n"
                                                   + " PRIMARY KEY(BotId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create Higgs table.", e);
        }
    }

    public static void createRoomTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Room(RoomId integer, \n"
                                                  + " Site text, \n"
                                                  + " OutputMessage integer, \n"
                                                  + " PRIMARY KEY(RoomId))";

        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create Room table.", e);
        }
    }

    public static void createReasonTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Reason(ReasonId integer, \n"
                                                    + " Reason integer, \n"
                                                    + " PRIMARY KEY(ReasonId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create Reason table.", e);
        }
    }

    public static void createReasonCaughtTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS ReasonCaught(PostId integer, \n"
                                                          + " RevisionId integer, \n"
                                                          + " RoomId integer, \n"
                                                          + " ReasonId text, \n"
                                                          + " Score integer, \n"
                                                          + " PRIMARY KEY(PostId, RevisionId, RoomId, ReasonId), \n"
                                                          + " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                          + " FOREIGN KEY(ReasonId) REFERENCES Reason(ReasonId), \n"
                                                          + " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create ReasonCaught table.", e);
        }
    }

    public static void createBlacklistedWordTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS BlacklistedWord(BlacklistedWordId integer, \n"
                                                             + " BlacklistedWord text, \n"
                                                             + " PostType text, \n"
                                                             + " PRIMARY KEY(BlacklistedWordId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create BlacklistWord table.", e);
        }
    }

    public static void createBlacklistedWordCaughtTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS BlacklistedWordCaught(PostId integer, \n"
                                                                  + " RevisionId integer, \n"
                                                                  + " RoomId integer, \n"
                                                                  + " BlacklistedWordId integer, \n"
                                                                  + " PRIMARY KEY(PostId, RevisionId, RoomId, BlacklistedWordId), \n "
                                                                  + " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                                  + " FOREIGN KEY(BlacklistedWordId) REFERENCES BlacklistedWord(BlacklistedWordId), \n"
                                                                  + " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create BlacklistedWordCaught table.", e);
        }
    }

    public static void createOffensiveWordTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS OffensiveWord(OffensiveWordId integer, \n"
                                                           + " OffensiveWord text, \n"
                                                           + " PRIMARY KEY(OffensiveWordId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create OffensiveWord table.", e);
        }
    }

    public static void createOffensiveWordCaughtTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS OffensiveWordCaught(PostId integer, \n"
                                                                 + " RevisionId integer, \n"
                                                                 + " RoomId integer, \n"
                                                                 + " OffensiveWordId integer, \n"
                                                                 + " PRIMARY KEY(PostId, RevisionId, RoomId), \n "
                                                                 + " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                                 + " FOREIGN KEY(OffensiveWordId) REFERENCES OffensiveWord(OffensiveWordId), \n"
                                                                 + " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create OffensiveWordCaught table.", e);
        }
    }

    public static void createFeedbackTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Feedback(PostId integer, \n"
                                                      + " RevisionId integer, \n"
                                                      + " RoomId integer, \n"
                                                      + " UserId integer, \n"
                                                      + " Feedback text, \n"
                                                      + " PRIMARY KEY(PostId, RevisionId, RoomId, UserId), \n"
                                                      + " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                      + " FOREIGN KEY(RoomId) REFERENCES VandalisedPost(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create Feedback table.", e);
        }
    }

    static boolean checkVandalisedPostExists(long postId, int revisionId, int roomId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM VandalisedPost WHERE PostId = ? AND RevisionId = ? AND RoomId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Found");
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to check for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + ".", e);
        }
        return false;
    }

    public static boolean checkReasonCaughtExists(long postId, int revisionId, int roomId, int reasonId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM ReasonCaught WHERE PostId = ? AND RevisionId = ? AND RoomId = ? AND ReasonId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);
            pstmt.setInt(4, reasonId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Found");
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to check for ReasonCaught. PostId: " + String.valueOf(postId) + "; ReasonId: " + String.valueOf(reasonId) + ".", e);
        }
        return false;
    }

    public static boolean checkBlacklistedWordCaughtExists(long postId, int revisionId, int roomId, int blacklistedWordId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM BlacklistedWordCaught WHERE PostId = ? AND RevisionId = ? AND RoomId = ? \n"
                   + "AND BlacklistedWordId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);
            pstmt.setInt(4, blacklistedWordId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("Found");
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to check for blacklisted word. PostId: " + String.valueOf(postId) + "; Word id: " + String.valueOf(blacklistedWordId), e);
        }
        return false;
    }

    static void storeVandalisedPost(long postId, long creationDate, int revisionId, int roomId, long ownerId, String title, String lastTitle, String body, String lastBody,
                                    boolean isRollback, String postType, String comment, String site, String severity, int higgsId, String revisionGuid,
                                    String previousRevisionGuid, String lastBodyMarkdown, String bodyMarkdown) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO VandalisedPost VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            pstmt.setLong(2, creationDate);
            pstmt.setInt(3, revisionId);
            pstmt.setInt(4, roomId);
            pstmt.setLong(5, ownerId);
            pstmt.setString(6, title);
            pstmt.setString(7, lastTitle);
            pstmt.setString(8, body);
            pstmt.setString(9, lastBody);
            pstmt.setInt(10, isRollback ? 1 : 0);
            pstmt.setString(11, postType);
            pstmt.setString(12, comment);
            pstmt.setString(13, site);
            pstmt.setString(14, severity);
            pstmt.setInt(15, higgsId);
            pstmt.setString(16, revisionGuid);
            pstmt.setString(17, previousRevisionGuid);
            pstmt.setString(18, lastBodyMarkdown);
            pstmt.setString(19, bodyMarkdown);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Failed to store vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + ".", e);
        }
    }

    public static void storeReasonCaught(long postId, int revisionId, int roomId, int reasonId, double score) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO ReasonCaught VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);
            pstmt.setInt(4, reasonId);
            pstmt.setDouble(5, score);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Failed to store reason caught. Post id: " + String.valueOf(postId) + "; Reason id: " + String.valueOf(reasonId), e);
        }
    }

    static void storeFeedback(long postId, int revisionId, int roomId, String feedback, long userId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT OR REPLACE INTO Feedback VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);
            pstmt.setLong(4, userId);
            pstmt.setString(5, feedback);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Failed to store feedback for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; Feedback: " + feedback + ".", e);
        }
    }

    public static Higgs getHiggs(int botId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT Url, SecretKey FROM Higgs WHERE BotId = ?;";

        Higgs higgs = new Higgs();
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, botId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                higgs.setUrl(rs.getString("Url"));
                higgs.setKey(rs.getString("SecretKey"));
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to get Higgs.", e);
        }
        return higgs;
    }

    public static int getHiggsId(long postId, int revisionId, int roomId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT HiggsId FROM VandalisedPost WHERE PostId = ? AND RevisionId = ? AND RoomId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("HiggsId");
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to get HiggsId. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; RoomId: " + String.valueOf(roomId), e);
        }

        return 0;
    }

    static Map<Integer, String> getBlacklistedWords(String postType) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT BlacklistedWordId, BlacklistedWord FROM BlacklistedWord WHERE PostType = ?;";

        Map<Integer, String> blacklistedWords = new HashMap<>();
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postType);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                blacklistedWords.put(rs.getInt("BlacklistedWordId"), rs.getString("BlacklistedWord"));
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to get blacklisted words.", e);
        }
        return blacklistedWords;
    }

    static Map<Integer, String> getOffensiveWords() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT OffensiveWordId, OffensiveWord FROM OffensiveWord;";

        Map<Integer, String> offensiveWords = new HashMap<>();
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                offensiveWords.put(rs.getInt("OffensiveWordId"), rs.getString("OffensiveWord"));
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to get offensive words.", e);
        }
        return offensiveWords;
    }

    static int getReasonId(String reason) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT ReasonId FROM Reason WHERE Reason = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reason);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ReasonId");
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to get reason Id.", e);
        }
        return 0;
    }

    public static void storeCaughtBlacklistedWord(long postId, int revisionId, int roomId, int blacklistedWordId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO BlacklistedWordCaught VALUES (?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);
            pstmt.setInt(4, blacklistedWordId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Failed to store caught blacklisted word. Post: " + String.valueOf(postId) + "; Word: " + String.valueOf(blacklistedWordId), e);
        }
    }

    public static void storeCaughtOffensiveWord(long postId, int revisionId, int roomId, int offensiveWordId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO OffensiveWordCaught VALUES (?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);
            pstmt.setInt(4, offensiveWordId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("Failed to store caught offensive word. PostId: " + String.valueOf(postId) + "; WordId: " + String.valueOf(offensiveWordId), e);
        }
    }

    public static Post getPost(long postId, int revisionId, int roomId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT PostId, \n"
                   + "       CreationDate, \n"
                   + "       RevisionId, \n"
                   + "       OwnerId, \n"
                   + "       Title, \n"
                   + "       LastTitle, \n "
                   + "       Body, \n"
                   + "       LastBody, \n"
                   + "       IsRollback, \n"
                   + "       PostType, \n"
                   + "       Comment, \n"
                   + "       Site \n"
                   + "       HiggsId \n"
                   + "       RevisionGuid \n"
                   + "       PreviousRevisionGuid \n"
                   + "  FROM VandalisedPost \n"
                   + " WHERE PostId = ? \n"
                   + "   AND RevisionId = ? \n"
                   + "   AND RoomId = ?;";

        Post post = null;
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            pstmt.setInt(2, revisionId);
            pstmt.setInt(3, roomId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                post = PostUtils.getPost(rs.getInt("PostId"), rs.getLong("CreationDate"), rs.getInt("RevisionId"), rs.getString("Title"),
                                         rs.getString("LastTitle"), rs.getString("Body"), rs.getString("LastBody"), rs.getBoolean("IsRollback"),
                                         rs.getString("PostType"), rs.getString("Comment"), rs.getInt("OwnerId"), rs.getString("Site"),
                                         rs.getString("RevisionGuid"), rs.getString("PreviousRevisionGuid"));
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to find vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; RoomId: " + String.valueOf(roomId) + ".", e);
        }

        return post;
    }

    public static List<Chatroom> getRooms() {

        List<Chatroom> chatrooms = new ArrayList<>();

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT RoomId, \n"
                   + "       Site, \n"
                   + "       OutputMessage \n"
                   + "  FROM Room;";

        Post post = null;
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Chatroom chatroom = new Chatroom(rs.getInt("RoomId"), RoomUtils.getChatHost(rs.getString("Site")), rs.getString("Site"),
                                                 rs.getBoolean("OutputMessage"));
                chatrooms.add(chatroom);
            }
        } catch (SQLException e) {
            LOGGER.info("Failed to find rooms.", e);
        }

        return chatrooms;
    }
}
