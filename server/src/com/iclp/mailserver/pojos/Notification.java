package com.iclp.mailserver.pojos;

import java.io.PrintWriter;

public class Notification {
    private String message;
    private PrintWriter output;

    public Notification(String message, PrintWriter output){
        this.message = message;
        this.output = output;
    }

    public String getMessage() {
        return message;
    }

    public PrintWriter getOutput() {
        return output;
    }
}
