package org.example.test.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String password; // This will store the hashed password directly in the object
    private List<String> friends;

    // Constructor for registration (password will be hashed before being set)
    public User(String username, String password) {
        this.username = username;
        this.password = password; // Store the hashed password
        this.friends = new ArrayList<>();
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getFriends() {
        return new ArrayList<>(friends); // Return a copy to prevent external modification
    }

    // Method to add a friend (ensures no duplicates)
    public void addFriend(String friendUsername) {
        if (!friends.contains(friendUsername)) {
            friends.add(friendUsername);
        }
    }
}
