package com.iclp.mailserver.pojos;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private boolean isLoggedIn;
    private Map<Integer, Message> mailbox = new HashMap<>();

    public User(String username, String password, boolean isLoggedIn) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Map<Integer, Message> getMailbox() {
        return mailbox;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }
}
