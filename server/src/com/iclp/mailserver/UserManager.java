package com.iclp.mailserver;

import java.util.ArrayList;
import java.util.Optional;

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

    public static Optional<User> getUser(String name) {
        for(User user: users){
            if( name.equals(user.getUsername()) ){
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public static boolean isUnique(String name){
        for(User user: users){
            if( name.equals(user.getUsername()) ){
                return false;
            }
        }
        return true;
    }
}
