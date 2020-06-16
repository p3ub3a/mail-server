package com.iclp.mailserver.utils;

public class Monitor {
    private boolean waiting;

    private synchronized boolean isWaiting() {
        return waiting;
    }

    private synchronized void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public static void pauseThread(Monitor monitor) throws InterruptedException {
        monitor.setWaiting(true);
        synchronized (monitor){
            // keep on waiting in case of spurious wakeups
            while(monitor.isWaiting()){
                monitor.setWaiting(true);
                monitor.wait();
            }
        }
    }

    public static void wakeupThread(Monitor monitor) {
        synchronized (monitor){
            monitor.setWaiting(false);
            monitor.notify();
        }
    }
}