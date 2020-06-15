package com.iclp.mailserver;

import com.iclp.mailserver.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

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
                User user = new User();
                user.setUsername(credentials[1]);
                user.setPassword(credentials[2]);
                user.setLoggedIn(false);

                users.add(user);

                msg = msg + ", created user: " + user.toString();
                return msg;
            }catch(Exception e){
                return e.getMessage();
            }
        }



        return msg;
    }
}
