package org.example.test.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/UserDB";
    private static final String USER = "root";
    private static final String PASS = "Bin141005";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Connection to MySQL failed: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "username VARCHAR(50) PRIMARY KEY,"
                + "password VARCHAR(50) NOT NULL"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}