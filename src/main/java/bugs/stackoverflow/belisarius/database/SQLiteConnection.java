package bugs.stackoverflow.belisarius.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bugs.stackoverflow.belisarius.utils.PathUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteConnection.class);

    Connection conn;

    public SQLiteConnection() {
        conn = connect();
    }

    public Connection getConnection() {
        return conn;
    }

    public Connection connect() {
        try {
            conn = DriverManager.getConnection(PathUtils.DATABASE_FILE);
        } catch (SQLException exception) {
            LOGGER.info("Error occured while trying to connect to the SQLite database.", exception);
        }
        return conn;
    }

}
