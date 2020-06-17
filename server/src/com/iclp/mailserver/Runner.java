package com.iclp.mailserver;

import com.iclp.mailserver.managers.ClientManager;
import com.iclp.mailserver.utils.Constants;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    private static ArrayList<ClientManager> clients = new ArrayList<>();
    private static ExecutorService clientThreadPool = Executors.newFixedThreadPool(Constants.SERVER_THREAD_NR);

    public static void main( String[] args){
        try{
            ServerSocket serverSocket = new ServerSocket(Constants.PORT);

            while(true){
                System.out.println("\u001B[36m[" + Constants.SDF.format(new Date(System.currentTimeMillis())) + "]\u001B[0m " + "listening on port \u001B[33m" + serverSocket.getLocalPort() + "\u001B[0m ...");
                Socket socket = serverSocket.accept();

                ClientManager client = new ClientManager(socket);
                clients.add(client);

                clientThreadPool.execute(client);
            }
        }catch(Exception e) {
            System.out.println("\u001B[36m[" + Constants.SDF.format(new Date(System.currentTimeMillis())) + "]\u001B[0m " + "something went wrong");
            e.printStackTrace();
        }
    }
}
