package com.iclp.mailserver.managers;

import com.iclp.mailserver.pojos.User;
import com.iclp.mailserver.utils.Constants;
import com.iclp.mailserver.utils.Monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class ClientManager implements Runnable{
    private int id;
    private static int idCounter;
    private static Object lock = new Object();

    private Socket request;
    private BufferedReader input;
    private PrintWriter output;

    private ArrayList<User> users = UserManager.getUsers();

    private User activeUser;

    public ClientManager(Socket socket) throws IOException{
        synchronized (lock){
            id = idCounter++;
        }
        this.request = socket;
        input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        output = new PrintWriter(request.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            System.out.println("\u001B[33m Server is accepting requests for client " + id + " \u001B[0m");
            while(true){
//                output.println("waiting for request");
                String requestContent =  input.readLine();

                if(requestContent == null) {
                    output.println("\u001B[31m request content is null \u001B[0m");
                    break;
                }

                String response = parseRequest(requestContent);

                output.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            try{
                String credentials[] = requestContent.split("\\s+");
                if(UserManager.isUnique(credentials[1])){
                    User user = new User(credentials[1], credentials[2], false);
                    users.add(user);
                    msg += "created user: " + user.toString();
                }else{
                    msg = Constants.ERR_MSG + Constants.DUPLICATE_USER_MSG;
                }
            }catch(Exception e){
                msg = Constants.ERR_MSG + e.getMessage();
            }
        }

        if(requestContent.startsWith(Constants.LOGIN_MSG)){
            try{
                String credentials[] = requestContent.split("\\s+");

                Optional<User> userOpt = UserManager.getUser(credentials[1]);
                if(userOpt.isPresent()){
                    User user = userOpt.get();

                    if(user.getPassword().equals(credentials[2])){
                        forceLogout(user);

                        createLogoutThread(user);

                        msg += Constants.LOGIN_MSG + " " + credentials[1];
                    }else{
                        msg = Constants.ERR_MSG + Constants.INVALID_PASSWORD_MSG + user.getUsername();
                    }

                }else{
                    msg = Constants.ERR_MSG + Constants.USER_NOT_FOUND_MSG + credentials[1];
                }
            }catch(Exception e){
                msg = Constants.ERR_MSG + e.getMessage();
            }
        }

        if(requestContent.startsWith(Constants.LOGOUT_MSG)){
            if(activeUser.isLoggedIn()){
                activeUser.setLoggedIn(false);
                msg += activeUser.getUsername() + " " + Constants.LOGOUT_MSG;
            }else{
                msg = Constants.ERR_MSG + Constants.USER_NOT_LOGGEDIN_MSG;
            }
        }

        System.out.println(msg);
        return msg;
    }

    private void forceLogout(User user) {
        Optional<Monitor> optionalMonitor = LogoutManager.getMonitor(user.getUsername());

        if(optionalMonitor.isPresent()){
            Monitor.wakeupThread(optionalMonitor.get());
            user.setLoggedIn(false);
        }
    }

    private void createLogoutThread(User user) {
        Runnable logoutManager = new LogoutManager(user.getUsername(), output);
        Thread logoutManagerThread = new Thread(logoutManager);
        logoutManagerThread.start();
        user.setLoggedIn(true);
        activeUser = user;
    }
}
