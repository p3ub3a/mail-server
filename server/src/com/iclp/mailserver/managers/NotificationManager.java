package com.iclp.mailserver.managers;

import com.iclp.mailserver.pojos.Notification;
import com.iclp.mailserver.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager implements Runnable{
    private boolean isOffline = false;

    private static List<Notification> notifications = new ArrayList<>();

    public static void addNotification(Notification notification){
        synchronized (NotificationManager.class){
            notifications.add(notification);
        }
    }

    @Override
    public void run() {

        System.out.println("Notification service started");

        while(!isOffline){
            for(Notification n : notifications){
                n.getOutput().println(n.getMessage());
                System.out.println(n.getMessage());
            }

            try {
                notifications.clear();
                Thread.currentThread().sleep(Constants.NOTIFICATION_DEALY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
