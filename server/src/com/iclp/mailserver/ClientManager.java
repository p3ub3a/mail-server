package com.iclp.mailserver;

import com.iclp.mailserver.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class ClientManager implements Runnable{
    private Socket request;
    private BufferedReader input;
    private PrintWriter output;
    private ArrayList<User> users = UserManager.getUsers();

    public ClientManager(Socket socket) throws IOException{
        this.request = socket;
        input = new BufferedReader(new InputStreamReader(request.getInputStream()));
        output = new PrintWriter(request.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while(true){
                output.println("waiting for request");
                String requestContent =  input.readLine();

                if(requestContent == null) {
                    output.println("request content is null");
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
                    msg += ", created user: " + user.toString();
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
                    user.setLoggedIn(true);

                    msg += Constants.LOGIN_MSG + " " + credentials[1];
                }else{
                    msg = Constants.ERR_MSG + Constants.USER_NOT_FOUND_MSG + credentials[1];
                }
            }catch(Exception e){
                msg = Constants.ERR_MSG + e.getMessage();
            }
        }

        System.out.println(msg);
        return msg;
    }
}
