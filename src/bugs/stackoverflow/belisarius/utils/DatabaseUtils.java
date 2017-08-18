package bugs.stackoverflow.belisarius.utils;

import java.sql.Connection;
import java.sql.SQLException;

import bugs.stackoverflow.belisarius.database.SQLiteConnection;

public class DatabaseUtils {

	public static void createNewDatabase() {
        SQLiteConnection connection = new SQLiteConnection();
				
        try (Connection conn = connection.getConnection()) {
            if (conn != null) {
                conn.getMetaData();
            }
         } catch (SQLException e) {
           e.printStackTrace();
        }
    }
	
	public static void createUserTable() {
		
	}
	
}
