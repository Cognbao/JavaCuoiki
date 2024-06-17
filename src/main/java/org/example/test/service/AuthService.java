package org.example.test.service;

import org.example.test.model.User;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/UserDB";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Bin141005";

    public boolean registerUser(User user) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/UserDB", "root", "Bin141005")) {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword()); // Consider hashing the password before storing it
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/UserDB", "root", "Bin141005")) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password); // Make sure this is a hashed comparison in a real app
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // True if user exists and password matches
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}