package org.example.test.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "Bin141005"; // Replace with your MySQL password

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertMessage(String username, String message) {
        String sql = "INSERT INTO chat_messages(username, message, timestamp) VALUES(?, ?, NOW())";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ResultSet getMessageHistory() {
        String sql = "SELECT * FROM chat_messages ORDER BY timestamp ASC";
        ResultSet rs = null;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            // Close ResultSet if not null
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
    }
}
