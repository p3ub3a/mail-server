package com.iclp.mailserver.pojos;

public class Message {
    private Integer id;
    private String message;
    private String from;

    public Message(Integer id, String message, String from){
        this.id = id;
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }
}
