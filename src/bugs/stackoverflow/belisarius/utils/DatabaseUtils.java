package bugs.stackoverflow.belisarius.utils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import bugs.stackoverflow.belisarius.models.Chatroom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import bugs.stackoverflow.belisarius.database.SQLiteConnection;
import bugs.stackoverflow.belisarius.models.Post;

public class DatabaseUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtils.class);
	
	public static void createVandalisedPostTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS VandalisedPost(PostId integer, \n" +
		                                                      " RevisionId integer, \n" +
		                                                      " RoomId integer, \n" +
				                                              " OwnerId integer, \n" +
				                                              " Title text, \n" +
		                                                      " LastTitle text, \n" +
				                                              " Body text, \n" +
		                                                      " LastBody text, \n" +
				                                              " IsRollback integer, \n" +
		                                                      " PostType text, \n" +
				                                              " Comment text, \n" +
		                                                      " Site text, \n" +
				                                              " Severity text, \n" +
				                                              " PRIMARY KEY(PostId, RevisionId, RoomId));";
		                                                        
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
        	stmt.execute(sql);
         } catch (SQLException e) {
			 LOGGER.info("Failed to create VandalisedPost table.", e);
         }
    }

    public static void createRoomTable() {
        SQLiteConnection connection = new SQLiteConnection();

        String sql = "CREATE TABLE IF NOT EXISTS Room(RoomId integer, \n" +
                                                    " Site text, \n" +
                                                    " OutputMessage integer, \n" +
                                                    " PRIMARY KEY(RoomId))";

        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.info("Failed to create Room table.", e);
        }
    }
	
	public static void createReasonTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS Reason(ReasonId integer, \n" +
		                                              " Reason integer, \n" +
				                                      " PRIMARY KEY(ReasonId));";

		                                                        	
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
        	stmt.execute(sql);
         } catch (SQLException e) {
			 LOGGER.info("Failed to create Reason table.", e);
         }
    }
	
	public static void createReasonCaughtTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS ReasonCaught(PostId integer, \n" +
		                                                    " RevisionId integer, \n" +
		                                                    " RoomId integer, \n" +
				                                            " ReasonId text, \n" +
				                                            " Score integer, \n" +
				                                            " PRIMARY KEY(PostId, RevisionId, RoomId, ReasonId), \n" +
						                                    " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n" +
						                                    " FOREIGN KEY(ReasonId) REFERENCES Reason(ReasonId), \n" +
                                                            " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";
		                                                        	
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
        	stmt.execute(sql);
         } catch (SQLException e) {
			 LOGGER.info("Failed to create ReasonCaught table.", e);
         }
    }
	
	public static void createBlacklistedWordTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS BlacklistedWord(BlacklistedWordId integer, \n" +
		                                                       " BlacklistedWord text, \n" +
				                                               " PostType text, \n" +
				                                               " PRIMARY KEY(BlacklistedWordId));";
		
		try (Connection conn = connection.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		 } catch(SQLException e) {
			 LOGGER.info("Failed to create BlacklistWord table.", e);
		 }
	}
	
	public static void createBlacklistedWordCaughtTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS BlacklistedWordCaught(PostId integer, \n" +
		                                                             " RevisionId integer, \n" +
		                                                             " RoomId integer, \n" +
				                                                     " BlacklistedWordId integer, \n" +
				                                                     " PRIMARY KEY(PostId, RevisionId, RoomId, BlacklistedWordId), \n " +
				                                                     " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n" +
				                                                     " FOREIGN KEY(BlacklistedWordId) REFERENCES BlacklistedWord(BlacklistedWordId), \n" +
                                                                     " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";
		
		try (Connection conn = connection.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		 } catch(SQLException e) {
			 LOGGER.info("Failed to create BlacklistedWordCaught table.", e);
		 }
	}
	
	public static void createOffensiveWordTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS OffensiveWord(OffensiveWordId integer, \n" +
		                                                     " OffensiveWord text, \n" +
				                                             " PRIMARY KEY(OffensiveWordId));";
		
		try (Connection conn = connection.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		 } catch(SQLException e) {
			 LOGGER.info("Failed to create OffensiveWord table.", e);
		 }
	}	
	
	public static void createOffensiveWordCaughtTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS OffensiveWordCaught(PostId integer, \n" +
		                                                           " RevisionId integer, \n" +
		                                                           " RoomId integer, \n" +
				                                                   " OffensiveWordId integer, \n" +
				                                                   " PRIMARY KEY(PostId, RevisionId, RoomId), \n " +
				                                                   " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n" +
				                                                   " FOREIGN KEY(OffensiveWordId) REFERENCES OffensiveWord(OffensiveWordId), \n" +
                                                                   " FOREIGN KEY(RoomId) REFERENCES Room(RoomId));";
		
		try (Connection conn = connection.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		 } catch(SQLException e) {
			 LOGGER.info("Failed to create OffensiveWordCaught table.", e);
		 }
	}

	public static void createFeedbackTable() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "CREATE TABLE IF NOT EXISTS Feedback(PostId integer, \n" +
		                                                " RevisionId integer, \n" +
		                                                " RoomId integer, \n" +
		                                                " UserId integer, \n" +
				                                        " Feedback text, \n" +
				                                        " PRIMARY KEY(PostId, RevisionId, RoomId, UserId), \n" +
				                                        " FOREIGN KEY(PostId, RevisionId, RoomId) REFERENCES VandalisedPost(PostId, RevisionId, RoomId), \n" +
				                                        " FOREIGN KEY(RoomId) REFERENCES VandalisedPost(RoomId));";
		                                                        	
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement()) {
        	stmt.execute(sql);
         } catch (SQLException e) {
			 LOGGER.info("Failed to create Feedback table.", e);
         }
    }
	
	public static boolean checkVandalisedPostExists(long postId, int revisionId, int roomId) {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "SELECT (COUNT(*) > 0) As Found FROM VandalisedPost WHERE PostId = ? AND RevisionId = ? AND RoomId = ?;";
		
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setLong(1, postId);
        	pstmt.setInt(2,  revisionId);
        	pstmt.setInt(3,  roomId);

        	ResultSet rs = pstmt.executeQuery();
        	while (rs.next()) {
        		return rs.getBoolean("Found");
        	}
        } catch (SQLException e) {
		 LOGGER.info("Failed to check for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + ".", e);
        }
		return false;
	}
	
	public static void storeVandalisedPost(long postId, int revisionId, int roomId, long ownerId, String title, String lastTitle, String body, String lastBody,
			                                boolean IsRollback, String postType, String comment, String site, String severity) {
		
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "INSERT INTO VandalisedPost VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setLong(1, postId);
        	pstmt.setInt(2,  revisionId);
        	pstmt.setInt(3,  roomId);
        	pstmt.setLong(4, ownerId);
        	pstmt.setString(5, title);
        	pstmt.setString(6, lastTitle);
        	pstmt.setString(7, body);
        	pstmt.setString(8, lastBody);
        	pstmt.setInt(9, (IsRollback) ? 1 : 0);
        	pstmt.setString(10, postType);
        	pstmt.setString(11, comment);
        	pstmt.setString(12, site);
        	pstmt.setString(14, severity);

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
			pstmt.setInt(2,  revisionId);
			pstmt.setInt(3,  roomId);
			pstmt.setInt(4, reasonId);
			pstmt.setDouble(5, score);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
 			 LOGGER.info("Failed to store reason for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; ReasonId: " + String.valueOf(reasonId) + ".", e);
		}
	}
	
	public static void storeFeedback(long postId, int revisionId, int roomId, String feedback, long userId) {

		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "INSERT OR REPLACE INTO Feedback VALUES (?, ?, ?, ?, ?);";
    	
		try (Connection conn = connection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, postId);
			pstmt.setInt(2,  revisionId);
			pstmt.setInt(3,  roomId);
			pstmt.setLong(4, userId);
			pstmt.setString(5, feedback);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.info("Failed to store feedback for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; Feedback: " + feedback + ".", e);
		}
	}
	
	public static HashMap<Integer, String> getBlacklistedWords(String postType) {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "SELECT BlacklistedWordId, BlacklistedWord FROM BlacklistedWord WHERE PostType = ?;";
		
		HashMap<Integer, String> blacklistedWords = new HashMap<Integer, String>();
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
	
	public static HashMap<Integer, String> getOffensiveWords() {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "SELECT OffensiveWordId, OffensiveWord FROM OffensiveWord;";
		
		HashMap<Integer, String> offensiveWords = new HashMap<Integer, String>();
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
	
	public static int getReasonId(String reason) {
		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "SELECT ReasonId FROM Reason WHERE Reason = ?;";
		
        try (Connection conn = connection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	pstmt.setString(1, reason);

        	ResultSet rs = pstmt.executeQuery();
        	while (rs.next()) {
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
			pstmt.setInt(2,  revisionId);
			pstmt.setInt(3,  roomId);
			pstmt.setInt(4, blacklistedWordId);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.info("Failed to store caught blacklisted word for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; BlacklistedWordId: " + String.valueOf(blacklistedWordId) + ".", e);
		}
	}
	
	public static void storeCaughtOffensiveWord(long postId, int revisionId, int roomId, int offensiveWordId) {

		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "INSERT INTO OffensivedWordCaught VALUES (?, ?, ?, ?);";
    	
		try (Connection conn = connection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, postId);
			pstmt.setInt(2,  revisionId);
			pstmt.setInt(3,  roomId);
			pstmt.setInt(4, offensiveWordId);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.info("Failed to store caught offensive word for vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; OffensiveWordId: " + String.valueOf(offensiveWordId) + ".", e);
		}
	}
	
	public static Post getPost(long postId, int revisionId, int roomId) {

		SQLiteConnection connection = new SQLiteConnection();
		
		String sql = "SELECT PostId, \n" +
		             "       RevisionId, \n" +
				     "       OwnerId, \n" +
		             "       Title, \n" +
				     "       LastTitle, \n " +
		             "       Body, \n" +
				     "       LastBody, \n" +
		             "       IsRollback, \n" +
				     "       PostType, \n" +
		             "       Comment \n" +
		             "  FROM VandalisedPost \n" +
				     " WHERE PostId = ? \n" +
		             "   AND RevisionId = ? \n" +
		             "   AND RoomId = ?;";
				    
    	Post post = null;
		try (Connection conn = connection.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setLong(1, postId);
			pstmt.setInt(2,  revisionId);
			pstmt.setInt(3,  roomId);

        	ResultSet rs = pstmt.executeQuery();
        	while (rs.next()) {
        		post = PostUtils.getPost(rs.getInt("PostId"), rs.getInt("RevisionId"), rs.getString("Title"), rs.getString("LastTitle"),
        			                     rs.getString("Body"),  rs.getString("LastBody"), rs.getBoolean("IsRollback"), rs.getString("PostType"), 
        			                     rs.getString("Comment"), rs.getInt("OwnerId"));
        	}
		} catch (SQLException e) {
			LOGGER.info("Failed to find vandalised post. PostId: " + String.valueOf(postId) + "; RevisionId: " + String.valueOf(revisionId) + "; RoomId: " + String.valueOf(roomId) + ".", e);
		}
		
		return post;
	}

    public static List<Chatroom> getRooms() {

        List<Chatroom> chatrooms = new ArrayList<>();

        SQLiteConnection connection = new SQLiteConnection();

        String sql = "SELECT RoomId, \n" +
                     "       Site, \n" +
                     "       OutputMessage \n" +
                     "  FROM Room;";

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