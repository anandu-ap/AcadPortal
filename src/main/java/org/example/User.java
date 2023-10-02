package org.example;

import java.sql.Connection;
import java.sql.ResultSet;

public class User {
    public String username;
    public String role;
    public boolean isLoggedIn;
    protected Connection connection;
    protected DBFunctions db;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
        this.isLoggedIn = false;
        this.db = new DBFunctions();
        getDatabaseConnection();
    }

    protected void logout() {
        this.isLoggedIn = false;
    }

    private void getDatabaseConnection() {
        DBFunctions db = new DBFunctions();
        this.connection = db.connect_to_db();
    }
    public void loginUser() {
        this.isLoggedIn = true;
    }

}
