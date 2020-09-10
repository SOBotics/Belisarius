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
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create VandalisedPost table.", exception);
        }
    }

    public static void createHiggsTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Higgs(BotId integer, \n"
                                                   + " SecretKey text, \n"
                                                   + " Url text, \n"
                                                   + " PRIMARY KEY(BotId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create Higgs table.", exception);
        }
    }

    public static void createRoomTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Room(RoomId integer, \n"
                                                  + " Site text, \n"
                                                  + " OutputMessage integer, \n"
                                                  + " PRIMARY KEY(RoomId))";

        try (Connection conn = connection.getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create Room table.", exception);
        }
    }

    public static void createReasonTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Reason(ReasonId integer, \n"
                                                    + " Reason integer, \n"
                                                    + " PRIMARY KEY(ReasonId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create Reason table.", exception);
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
                                                          + " FOREIGN KEY(PostId, RevisionId, RoomId)"
                                                          + " REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                          + " FOREIGN KEY(ReasonId) REFERENCES Reason(ReasonId), \n"
                                                          + " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create ReasonCaught table.", exception);
        }
    }

    public static void createBlacklistedWordTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS BlacklistedWord(BlacklistedWordId integer, \n"
                                                             + " BlacklistedWord text, \n"
                                                             + " PostType text, \n"
                                                             + " PRIMARY KEY(BlacklistedWordId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create BlacklistWord table.", exception);
        }
    }

    public static void createBlacklistedWordCaughtTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS BlacklistedWordCaught(PostId integer, \n"
                                                                   + " RevisionId integer, \n"
                                                                   + " RoomId integer, \n"
                                                                   + " BlacklistedWordId integer, \n"
                                                                   + " PRIMARY KEY(PostId, RevisionId, RoomId, BlacklistedWordId), \n "
                                                                   + " FOREIGN KEY(PostId, RevisionId, RoomId)"
                                                                   + " REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                                   + " FOREIGN KEY(BlacklistedWordId)"
                                                                   + " REFERENCES BlacklistedWord(BlacklistedWordId), \n"
                                                                   + " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create BlacklistedWordCaught table.", exception);
        }
    }

    public static void createOffensiveWordTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS OffensiveWord(OffensiveWordId integer, \n"
                                                           + " OffensiveWord text, \n"
                                                           + " PRIMARY KEY(OffensiveWordId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create OffensiveWord table.", exception);
        }
    }

    public static void createOffensiveWordCaughtTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS OffensiveWordCaught(PostId integer, \n"
                                                                 + " RevisionId integer, \n"
                                                                 + " RoomId integer, \n"
                                                                 + " OffensiveWordId integer, \n"
                                                                 + " PRIMARY KEY(PostId, RevisionId, RoomId), \n "
                                                                 + " FOREIGN KEY(PostId, RevisionId, RoomId)"
                                                                 + " REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                                 + " FOREIGN KEY(OffensiveWordId) REFERENCES OffensiveWord(OffensiveWordId), \n"
                                                                 + " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create OffensiveWordCaught table.", exception);
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
                                                      + " FOREIGN KEY(PostId, RevisionId, RoomId)"
                                                      + " REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n"
                                                      + " FOREIGN KEY(RoomId) REFERENCES VandalisedPost(RoomId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.info("Failed to create Feedback table.", exception);
        }
    }

    static boolean checkVandalisedPostExists(long postId, int revisionId, int roomId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM VandalisedPost WHERE PostId = ? AND RevisionId = ? AND RoomId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("Found");
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to check for vandalised post. PostId: " + String.valueOf(postId) + "; "
                      + "RevisionId: " + String.valueOf(revisionId) + ".", exception);
        }
        return false;
    }

    public static boolean checkReasonCaughtExists(long postId, int revisionId, int roomId, int reasonId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM ReasonCaught WHERE PostId = ? AND RevisionId = ? AND RoomId = ? AND ReasonId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setInt(4, reasonId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("Found");
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to check for ReasonCaught. PostId: " + String.valueOf(postId) + "; ReasonId: " + String.valueOf(reasonId) + ".", exception);
        }
        return false;
    }

    public static boolean checkBlacklistedWordCaughtExists(long postId, int revisionId, int roomId, int blacklistedWordId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM BlacklistedWordCaught WHERE PostId = ? AND RevisionId = ? AND RoomId = ? \n"
                   + "AND BlacklistedWordId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setInt(4, blacklistedWordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("Found");
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to check for blacklisted word. PostId: " + String.valueOf(postId) + "; "
                      + "Word id: " + String.valueOf(blacklistedWordId), exception);
        }
        return false;
    }

    static void storeVandalisedPost(long postId, long creationDate, int revisionId, int roomId, long ownerId, String title,
                                    String lastTitle, String body, String lastBody, boolean isRollback, String postType,
                                    String comment, String site, String severity, int higgsId, String revisionGuid,
                                    String previousRevisionGuid, String lastBodyMarkdown, String bodyMarkdown) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO VandalisedPost VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.setLong(2, creationDate);
            preparedStatement.setInt(3, revisionId);
            preparedStatement.setInt(4, roomId);
            preparedStatement.setLong(5, ownerId);
            preparedStatement.setString(6, title);
            preparedStatement.setString(7, lastTitle);
            preparedStatement.setString(8, body);
            preparedStatement.setString(9, lastBody);
            preparedStatement.setInt(10, isRollback ? 1 : 0);
            preparedStatement.setString(11, postType);
            preparedStatement.setString(12, comment);
            preparedStatement.setString(13, site);
            preparedStatement.setString(14, severity);
            preparedStatement.setInt(15, higgsId);
            preparedStatement.setString(16, revisionGuid);
            preparedStatement.setString(17, previousRevisionGuid);
            preparedStatement.setString(18, lastBodyMarkdown);
            preparedStatement.setString(19, bodyMarkdown);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.info("Failed to store vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + ".", exception);
        }
    }

    public static void storeReasonCaught(long postId, int revisionId, int roomId, int reasonId, double score) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO ReasonCaught VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setInt(4, reasonId);
            preparedStatement.setDouble(5, score);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.info("Failed to store reason caught. Post id: " + String.valueOf(postId) + "; Reason id: " + String.valueOf(reasonId), exception);
        }
    }

    static void storeFeedback(long postId, int revisionId, int roomId, String feedback, long userId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT OR REPLACE INTO Feedback VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setLong(4, userId);
            preparedStatement.setString(5, feedback);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.info("Failed to store feedback for vandalised post. PostId: " + String.valueOf(postId) + "; "
                      + "RevisionId: " + String.valueOf(revisionId) + "; Feedback: " + feedback + ".", exception);
        }
    }

    public static Higgs getHiggs(int botId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT Url, SecretKey FROM Higgs WHERE BotId = ?;";

        Higgs higgs = new Higgs();
        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, botId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                higgs.setUrl(resultSet.getString("Url"));
                higgs.setKey(resultSet.getString("SecretKey"));
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to get Higgs.", exception);
        }
        return higgs;
    }

    public static int getHiggsId(long postId, int revisionId, int roomId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT HiggsId FROM VandalisedPost WHERE PostId = ? AND RevisionId = ? AND RoomId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("HiggsId");
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to get HiggsId. PostId: " + String.valueOf(postId) + "; "
                      + "RevisionId: " + String.valueOf(revisionId) + "; RoomId: " + String.valueOf(roomId), exception);
        }

        return 0;
    }

    static Map<Integer, String> getBlacklistedWords(String postType) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT BlacklistedWordId, BlacklistedWord FROM BlacklistedWord WHERE PostType = ?;";

        Map<Integer, String> blacklistedWords = new HashMap<>();
        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, postType);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                blacklistedWords.put(resultSet.getInt("BlacklistedWordId"), resultSet.getString("BlacklistedWord"));
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to get blacklisted words.", exception);
        }
        return blacklistedWords;
    }

    static Map<Integer, String> getOffensiveWords() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT OffensiveWordId, OffensiveWord FROM OffensiveWord;";

        Map<Integer, String> offensiveWords = new HashMap<>();
        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                offensiveWords.put(resultSet.getInt("OffensiveWordId"), resultSet.getString("OffensiveWord"));
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to get offensive words.", exception);
        }
        return offensiveWords;
    }

    static int getReasonId(String reason) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT ReasonId FROM Reason WHERE Reason = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, reason);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ReasonId");
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to get reason Id.", exception);
        }
        return 0;
    }

    public static void storeCaughtBlacklistedWord(long postId, int revisionId, int roomId, int blacklistedWordId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO BlacklistedWordCaught VALUES (?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setInt(4, blacklistedWordId);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.info("Failed to store caught blacklisted word. Post: " + String.valueOf(postId) + "; Word: " + String.valueOf(blacklistedWordId), exception);
        }
    }

    public static void storeCaughtOffensiveWord(long postId, int revisionId, int roomId, int offensiveWordId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO OffensiveWordCaught VALUES (?, ?, ?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setInt(4, offensiveWordId);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.info("Failed to store caught offensive word. PostId: " + String.valueOf(postId) + "; WordId: " + String.valueOf(offensiveWordId), exception);
        }
    }

    public static Post getPost(long postId, int revisionId, int roomId) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT PostId, \n"
                   + "       CreationDate, \n"
                   + "       RevisionId, \n"
                   + "       OwnerId, \n"
                   + "       Title, \n"
                   + "       LastTitle, \n"
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
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                post = PostUtils.getPost(resultSet.getInt("PostId"), resultSet.getLong("CreationDate"),
                                         resultSet.getInt("RevisionId"), resultSet.getString("Title"),
                                         resultSet.getString("LastTitle"), resultSet.getString("Body"),
                                         resultSet.getString("LastBody"), resultSet.getBoolean("IsRollback"),
                                         resultSet.getString("PostType"), resultSet.getString("Comment"),
                                         resultSet.getInt("OwnerId"), resultSet.getString("Site"),
                                         resultSet.getString("RevisionGuid"), resultSet.getString("PreviousRevisionGuid"));
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to find vandalised post. PostId: " + String.valueOf(postId) + "; "
                      + "RevisionId: " + String.valueOf(revisionId) + "; RoomId: " + String.valueOf(roomId) + ".", exception);
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
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Chatroom chatroom = new Chatroom(resultSet.getInt("RoomId"), ChatUtils.getChatHost(resultSet.getString("Site")),
                                                 resultSet.getString("Site"), resultSet.getBoolean("OutputMessage"));
                chatrooms.add(chatroom);
            }
        } catch (SQLException exception) {
            LOGGER.info("Failed to find rooms.", exception);
        }

        return chatrooms;
    }
}
