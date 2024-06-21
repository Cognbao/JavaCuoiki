package org.example.test.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getMessageHistory() {
        String sql = "SELECT * FROM chat_messages ORDER BY timestamp ASC";
        List<String> messages = new ArrayList<>();

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String sender = rs.getString("username");
                String message = rs.getString("message");
                messages.add(sender + ": " + message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return messages; // Return the processed data
    }

}