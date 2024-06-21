package org.example.test.service;

import org.example.test.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/UserDB?useUnicode=true&characterEncoding=UTF-8", "root", "Bin141005");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String salt = BCrypt.gensalt();
            String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt); // Store the salt along with the hashed password

            logger.info("Registering user: " + user.getUsername());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/UserDB?useUnicode=true&characterEncoding=UTF-8", "root", "Bin141005");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");
                    String salt = rs.getString("salt"); // Retrieve the stored salt

                    logger.info("Authenticating user: " + username);

                    // Check password using the retrieved salt
                    return BCrypt.checkpw(password, storedHashedPassword);
                } else {
                    logger.warning("Authentication failed for user: " + username + " (user not found)");
                    return false; // User not found
                }
            }
        }
    }
}
