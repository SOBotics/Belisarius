package bugs.stackoverflow.belisarius.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bugs.stackoverflow.belisarius.database.SQLiteConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtils {

    // The variable holds a map that has a map (Id, BlacklistedWord) and the post type
    public static final Map<String, Map<Integer, String>> BLACKLISTED_WORDS = getBlacklistedWords();
    public static final Map<Integer, String> OFFENSIVE_WORDS = getOffensiveWords();
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
                                                            + " RevisionGuid text, \n"
                                                            + " PRIMARY KEY(PostId, RevisionId, RoomId));";

        try (Connection conn = connection.getConnection();
            Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            LOGGER.error("Failed to create VandalisedPost table.", exception);
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
            LOGGER.error("Failed to create ReasonCaught table.", exception);
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
            LOGGER.error("Failed to create BlacklistedWordCaught table.", exception);
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
            LOGGER.error("Failed to create OffensiveWordCaught table.", exception);
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
            LOGGER.error("Failed to create Feedback table.", exception);
        }
    }

    public static boolean checkVandalisedPostExists(long postId, int revisionId, int roomId) {
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
            LOGGER.error("Failed to check for vandalised post. PostId: " + postId + "; "
                      + "RevisionId: " + revisionId + ".", exception);
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
            LOGGER.error("Failed to check for ReasonCaught. PostId: " + postId + "; ReasonId: " + reasonId + ".", exception);
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
            LOGGER.error("Failed to check for blacklisted word. PostId: " + postId + "; "
                      + "Word id: " + blacklistedWordId, exception);
        }
        return false;
    }

    public static boolean checkOffensiveWordCaughtExists(long postId, int revisionId, int roomId, int offensiveWordId) {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT (COUNT(*) > 0) As Found FROM OffensiveWordCaught WHERE PostId = ? AND RevisionId = ? AND RoomId = ? \n"
                   + "AND OffensiveWordId = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, postId);
            preparedStatement.setInt(2, revisionId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setInt(4, offensiveWordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("Found");
            }
        } catch (SQLException exception) {
            LOGGER.error("Failed to check for offensive word. PostId: " + postId + "; "
                      + "Word id: " + offensiveWordId, exception);
        }
        return false;
    }

    public static void storeVandalisedPost(long postId, long creationDate, int revisionId, int roomId, long ownerId, String title,
                                           String lastTitle, String body, String lastBody, boolean isRollback, String postType,
                                           String comment, String site, String severity, String revisionGuid) {

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "INSERT INTO VandalisedPost VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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
            preparedStatement.setString(15, revisionGuid);

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            LOGGER.error("Failed to store vandalised post. PostId: " + postId + "; RevisionId: " + revisionId + ".", exception);
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
            LOGGER.error("Failed to store reason caught. Post id: " + postId + "; Reason id: " + reasonId, exception);
        }
    }

    public static void storeFeedback(long postId, int revisionId, int roomId, String feedback, long userId) {

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
            LOGGER.error("Failed to store feedback for vandalised post. PostId: " + postId + "; "
                      + "RevisionId: " + revisionId + "; Feedback: " + feedback + ".", exception);
        }
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
            LOGGER.error("Failed to store caught blacklisted word. Post: " + postId + "; Word: " + blacklistedWordId, exception);
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
            LOGGER.error("Failed to store caught offensive word. PostId: " + postId + "; WordId: " + offensiveWordId, exception);
        }
    }

    public static Map<String, Map<Integer, String>> getBlacklistedWords() {
        Map<String, Map<Integer, String>> blacklistedWordsMap = new HashMap<>();
        blacklistedWordsMap.put("question", new HashMap<>());
        blacklistedWordsMap.put("answer", new HashMap<>());
        blacklistedWordsMap.put("question_title", new HashMap<>());

        FileUtils.readFile(FileUtils.BLACKLISTED_WORDS_FILE).forEach(line -> {
            List<String> matches = FileUtils.splitLine(line);
            blacklistedWordsMap.get(matches.get(2)).put(Integer.parseInt(matches.get(0)), matches.get(1));
        });
        return blacklistedWordsMap;
    }

    public static Map<Integer, String> getOffensiveWords() {
        Map<Integer, String> offensiveWordsMap = new HashMap<>();
        FileUtils.readFile(FileUtils.OFFENSIVE_WORDS_FILE).forEach(line -> {
            List<String> matches = FileUtils.splitLine(line);
            offensiveWordsMap.put(Integer.parseInt(matches.get(0)), matches.get(1));
        });
        return offensiveWordsMap;
    }

    public static Map<Integer, String> getBlacklistedWordsByType(String postType) {
        return BLACKLISTED_WORDS.get(postType);
    }
}
