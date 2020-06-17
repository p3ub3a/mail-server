package com.iclp.mailserver.managers;

import com.iclp.mailserver.pojos.Message;
import com.iclp.mailserver.pojos.User;
import com.iclp.mailserver.utils.Constants;

import java.io.PrintWriter;
import java.util.*;

public class UserManager {
    private static ArrayList<User> users = new ArrayList<>();

    public static ArrayList<User> getUsers() {
        return users;
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

    public static String sendMessage(User activeUser, String[] users, String messageText) {
        String result = "";
        boolean shouldSend = true;
        List<User> userList = new ArrayList<>();

        for(int i=0; i< users.length; i++){
            Optional<User> user = getUser(users[i]);

            if(user.isPresent()){
                if(activeUser.getUsername().equals(user.get().getUsername())){
                    shouldSend = false;
                    result = Constants.ERR_MSG + Constants.SEND_MESSAGE_TO_SAME_USER_MSG;
                    break;
                }

                userList.add(user.get());
            }else{
                shouldSend = false;
                result = Constants.ERR_MSG + Constants.USER_NOT_FOUND_MSG + "\u001B[33m" + users[i] + "\u001B[0m";
                break;
            }
        }

        if(shouldSend){
            for(User user: userList) {
                synchronized (ClientManager.getLock()) {
                    Map<Integer, Message> mailbox = user.getMailbox();
                    Integer id = generateId(mailbox);

                    Message message = new Message(id, messageText, activeUser.getUsername());
                    mailbox.put(id, message);

                    //if user is logged in, send notification to the client
                    if (user.isLoggedIn()) {
                        PrintWriter output = ClientManager.getUsernameOutputMap().get(user.getUsername());
                        output.println(Constants.NEW_MESSAGE_IN_MAILBOX_MSG + " " + id);
                    }
                }
            }

            result = Constants.OK_MSG + "message sent";
        }

        return result;
    }

    private static Integer generateId(Map<Integer, Message> mailbox){
        Random random = new Random();
        Integer id = random.nextInt(Constants.MAX_ID_NR);

        if(mailbox.containsKey(id)){
            generateId(mailbox);
        }

        return id;
    }
}
