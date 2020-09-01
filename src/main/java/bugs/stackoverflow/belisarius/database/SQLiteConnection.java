package bugs.stackoverflow.belisarius.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bugs.stackoverflow.belisarius.utils.PathUtils;

public class  SQLiteConnection {

    Connection conn = null;

    public SQLiteConnection() {
        conn = connect();
    }

    public Connection getConnection() {
        return conn;
    }

    public Connection connect() {
        try {
            conn = DriverManager.getConnection(PathUtils.dbFile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}