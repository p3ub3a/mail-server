package com.iclp.mailserver.utils;

public class Constants {
    public static final int PORT = 5432;
    public static final int SERVER_THREAD_NR = 5;
    public static final int NOTIFICATION_DEALY = 1000;

    public static final String CREATE_ACCOUNT_MSG = "create_account";
    public static final String LOGIN_MSG = "login";
    public static final String LOGOUT_MSG = "logout";
    public static final String SEND_MSG = "send";
    public static final String READ_MAILBOX_MSG = "read_mailbox";
    public static final String READ_MESSAGE_MSG = "read_message";
    public static final String EXIT_MSG = "exit";
    public static final String FORCE_LOGOUT_MSG = "force_logout";
    public static final String NEW_MESSAGE_IN_MAILBOX_MSG = "new_message_in_mailbox";

    public static final String OK_MSG = "\u001B[32mOK:\u001B[0m ";
    public static final String ERR_MSG = "\u001B[31mERROR:\u001B[0m ";
    public static final String INVALID_PASSWORD_MSG = "Invalid password for user ";
    public static final String DUPLICATE_USER_MSG = "The username is taken";
    public static final String USER_NOT_FOUND_MSG = "Could not find user ";
    public static final String USER_NOT_LOGGEDIN_MSG = "User is not logged in";
}
