package com.iclp.mailserver.managers;

import com.iclp.mailserver.pojos.Message;
import com.iclp.mailserver.pojos.User;
import com.iclp.mailserver.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientManager implements Runnable{
    private int id;
    private static int idCounter;
    private static Object lock = new Object();

    private Socket request;
    private BufferedReader input;
    private PrintWriter output;

    private ArrayList<User> users = UserManager.getUsers();
    private static Map<String, PrintWriter> usernameOutputMap = new HashMap<>();

    private User activeUser;

    public ClientManager(Socket socket) throws IOException{
        synchronized (lock){
            id = idCounter++;
        }
        this.request = socket;
        input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        output = new PrintWriter(request.getOutputStream(), true);
    }

    public static Map<String, PrintWriter> getUsernameOutputMap() {
        return usernameOutputMap;
    }

    public static Object getLock() {
        return lock;
    }

    @Override
    public void run() {
        try {
            System.out.println("\u001B[33m Server is accepting requests for client " + id + " \u001B[0m");
            while(true){
                String requestContent =  input.readLine();

                if(requestContent == null) {
                    output.println("\u001B[31m request content is null \u001B[0m");
                    break;
                }

                String response = parseRequest(requestContent);

                output.println(response);
            }

        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
            try {
                input.close();
                output.close();
                System.out.println("\u001B[31m Client " + id + " disconnected \u001B[0m");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String parseRequest(String requestContent) {
        String msg = Constants.OK_MSG;

        if(requestContent.startsWith(Constants.CREATE_ACCOUNT_MSG)){
            msg = createAccount(requestContent, msg);
        }

        if(requestContent.startsWith(Constants.LOGIN_MSG)){
            msg = login(requestContent, msg);
        }

        if(requestContent.startsWith(Constants.LOGOUT_MSG)){
            msg = logout(msg);
        }

        if(requestContent.startsWith(Constants.SEND_MSG)){
            msg = send(requestContent, msg);
        }

        if(requestContent.startsWith(Constants.READ_MAILBOX_MSG)){
            msg = readMailbox(msg);
        }

        if(requestContent.startsWith(Constants.READ_MESSAGE_MSG)){
            msg = readMessage(requestContent, msg);
        }

        System.out.println(msg);
        return msg;
    }

    private String createAccount(String requestContent, String msg) {
        try{
            String credentials[] = requestContent.split(Constants.WHITE_SPACE_REGEX);
            if(UserManager.isUnique(credentials[1])){
                User user = new User(credentials[1], credentials[2], false);
                users.add(user);

                msg += "created user: " + user.toString();
            }else{
                msg = Constants.ERR_MSG + Constants.DUPLICATE_USER_MSG;
            }
        }catch(Exception e){
            msg = Constants.ERR_MSG + Constants.INVALID_REQUEST_FORMAT_MSG;
        }
        return msg;
    }

    private String login(String requestContent, String msg) {
        try{
            String credentials[] = requestContent.split(Constants.WHITE_SPACE_REGEX);

            Optional<User> userOpt = UserManager.getUser(credentials[1]);
            if(userOpt.isPresent()){
                User user = userOpt.get();

                if(user.getPassword().equals(credentials[2])){
                    forceLogout(user);
                    user.setLoggedIn(true);
                    activeUser = user;

                    synchronized (lock){
                        usernameOutputMap.put(activeUser.getUsername(), output);
                    }

                    msg += Constants.LOGIN_MSG + " " + activeUser.getUsername();
                }else{
                    msg = Constants.ERR_MSG + Constants.INVALID_PASSWORD_MSG + user.getUsername();
                }

            }else{
                msg = Constants.ERR_MSG + Constants.USER_NOT_FOUND_MSG + credentials[1];
            }
        }catch(Exception e){
            msg = Constants.ERR_MSG + e.getMessage();
        }
        return msg;
    }

    private void forceLogout(User user) {
        if(user.isLoggedIn()){
            user.setLoggedIn(false);
            synchronized (lock){
                usernameOutputMap.get(user.getUsername()).println(Constants.FORCE_LOGOUT_MSG + " user " + user.getUsername());
                usernameOutputMap.remove(user.getUsername());
            }
        }
    }

    private String logout(String msg) {
        if(activeUser.isLoggedIn()){
            activeUser.setLoggedIn(false);
            synchronized (lock){
                usernameOutputMap.remove(activeUser.getUsername());
            }
            msg += activeUser.getUsername() + " " + Constants.LOGOUT_MSG;
        }else{
            msg = Constants.ERR_MSG + Constants.USER_NOT_LOGGEDIN_MSG;
        }
        return msg;
    }

    private String send(String requestContent, String msg){
        Pattern pattern = Pattern.compile(Constants.MESSAGE_REGEX);
        Matcher matcher = pattern.matcher(requestContent);

        if(matcher.matches()){
            String usersText = matcher.group(1);
            String messageText = matcher.group(2);

            String splitRequest[] = usersText.split(Constants.WHITE_SPACE_REGEX);

            // splitRequest contains the *send* word as well
            if(splitRequest.length > 1){
                String users[] = new String[splitRequest.length - 1];

                for(int i=0; i < splitRequest.length; i++){
                    if(i != 0){
                        users[i-1] = splitRequest[i];
                    }
                }

                msg = UserManager.sendMessage(activeUser, users, messageText);
            }else{
                msg = Constants.ERR_MSG + Constants.INVALID_REQUEST_FORMAT_MSG;
            }
        }else{
            msg = Constants.ERR_MSG + Constants.INVALID_REQUEST_FORMAT_MSG;
        }


        return msg;
    }

    private String readMailbox(String msg){
        return msg + activeUser.getMailbox().keySet().toString();
    }

    private String readMessage(String requestContent, String msg){
        try{
            String splitRequest[] = requestContent.split(Constants.WHITE_SPACE_REGEX);

            Message message = activeUser.getMailbox().get(Integer.valueOf(splitRequest[1]));

            if(message != null){
                msg += "\u001B[33mfrom:\u001B[0m " + message.getFrom() + " \u001B[33mtext:\u001B[0m " + message.getMessage();
            }
        }catch (Exception e){
            msg = Constants.ERR_MSG + Constants.INVALID_REQUEST_FORMAT_MSG;
        }

        return msg;
    }
}
