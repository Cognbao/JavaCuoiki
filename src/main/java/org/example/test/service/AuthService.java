package org.example.test.service;

import org.example.test.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/UserDB", "root", "Bin141005");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());

            logger.info("Registering user: " + user.getUsername());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/UserDB", "root", "Bin141005");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");

                    logger.info("Authenticating user: " + username);

                    System.out.println(password);
                    System.out.println(storedHashedPassword);
                    System.out.println(BCrypt.checkpw(password, storedHashedPassword));
                    return BCrypt.checkpw(password, storedHashedPassword);
                } else {
                    logger.warning("Authentication failed for user: " + username + " (user not found)");
                    return false; // User not found
                }
            }
        }
    }
}