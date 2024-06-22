package org.example.test.service;

import org.example.test.model.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageService {
    private static final Logger logger = Logger.getLogger(ManageService.class.getName());

    public boolean sendFriendRequest(String requester, String recipient) throws SQLException {
        String sql = "INSERT INTO friend_requests (requester, recipient) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, requester);
            stmt.setString(2, recipient);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean acceptFriendRequest(String requester, String recipient) throws SQLException {
        // Step 1: Remove the friend request from the friend_requests table
        String deleteSql = "DELETE FROM friend_requests WHERE requester = ? AND recipient = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setString(1, requester);
            stmt.setString(2, recipient);
            stmt.executeUpdate();
        }

        // Step 2: Insert the friendship into the friendships table
        String insertSql = "INSERT INTO friendships (user1, user2) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setString(1, requester);
            stmt.setString(2, recipient);
            stmt.executeUpdate();

            stmt.setString(1, recipient);
            stmt.setString(2, requester);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean declineFriendRequest(String requester, String recipient) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE requester = ? AND recipient = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, requester);
            stmt.setString(2, recipient);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public List<String> getPendingFriendRequests(String username) {
        String sql = "SELECT requester FROM friend_requests WHERE recipient = ?";
        List<String> requests = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(rs.getString("requester"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting pending friend requests", e);
        }
        return requests;
    }

    public List<String> getFriendList(String username) throws SQLException {
        List<String> friends = new ArrayList<>();
        String sql = "SELECT user2 FROM friendships WHERE user1 = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friends.add(rs.getString("user2"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving friend list", e);
        }
        return friends;
    }

    public boolean areFriends(String user1, String user2) throws SQLException {
        String sql = "SELECT * FROM friendships WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a row is found (they are friends)
            }
        }
    }

}
