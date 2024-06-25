package org.example.test.util; // Adjust the package to your project structure

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public static List<String> loadMessageHistory(String username) throws Exception {
        List<String> messageHistory = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT content FROM messages WHERE sender_id = ? OR recipient_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String content = rs.getString("content");
                        messageHistory.add(content);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading message history from database", e);
            throw e;
        }
        return messageHistory;
    }

    public static void saveMessage(String senderId, String recipientId, String content) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO messages (sender_id, recipient_id, content) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, senderId);
                stmt.setString(2, recipientId);
                stmt.setString(3, content);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving message to database", e);
            throw e;
        }
    }
}
