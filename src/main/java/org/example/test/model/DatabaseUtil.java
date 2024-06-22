package org.example.test.model; // Adjust the package to your project structure

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {

    private static final Logger logger = Logger.getLogger(DatabaseUtil.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/UserDB"; // Update with your actual database details
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Bin141005"; // Update with your actual password

    // Private constructor to prevent instantiation
    private DatabaseUtil() {
    }

    // Get a database connection
    public static Connection getConnection() throws SQLException {
        logger.info("Attempting to connect to the database...");

        try {
            // Load the MySQL JDBC driver (if needed)
            Class.forName("com.mysql.cj.jdbc.Driver"); // Replace with the correct driver class if using a different database
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error loading database driver", e);
            throw new SQLException("Could not load database driver.", e);
        }

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Connected to the database successfully!");
            return conn;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to the database", e);
            throw e;  // Re-throw the exception so the caller can handle it
        }
    }
}
