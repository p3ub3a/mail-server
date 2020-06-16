package com.iclp.mailserver.managers;

import com.iclp.mailserver.pojos.Notification;
import com.iclp.mailserver.utils.Constants;
import com.iclp.mailserver.utils.Monitor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LogoutManager implements Runnable{

    private String userName;
    private PrintWriter output;

    private static Map<String, Monitor> userMonitorMap = new HashMap<>();

    public LogoutManager(String userName, PrintWriter output){
        this.userName = userName;
        this.output = output;
    }

    public static Optional<Monitor> getMonitor(String name){
        if(userMonitorMap.get(name) != null){
            return Optional.of(userMonitorMap.get(name));
        }else{
            return Optional.empty();
        }
    }

    @Override
    public void run() {
        Monitor monitor = new Monitor();

        //remove old user if present
        userMonitorMap.remove(userName);

        userMonitorMap.put(userName, monitor);

        synchronized (this){
            try {
                System.out.println("Logout manager started, waiting for wake up");
                Monitor.pauseThread(monitor);

                String message = Constants.FORCE_LOGOUT_MSG + " user " + userName;

                NotificationManager.addNotification(new Notification(message, output));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
