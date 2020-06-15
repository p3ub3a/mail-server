package com.iclp.mailserver;

import java.util.ArrayList;

public class UserManager {
    private static ArrayList<User> users = new ArrayList<>();

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static void deleteUser(User user) {
        users.remove(user);
    }
}
