package com.iclp.mailserver.utils;

import java.text.SimpleDateFormat;

public class Constants {
    public static final int PORT = 5432;
    public static final int SERVER_THREAD_NR = 5;
    public static final int MAX_ID_NR = 1000000;
    public static final SimpleDateFormat SDF = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    public static final String WHITE_SPACE_REGEX = "\\s+";
    public static final String CREDENTIALS_REGEX = "^(?=.{4,20}$)(?:[a-zA-Z\\d]+(?:(?:\\.|-|_)[a-zA-Z\\d])*)+$";
    public static final String MESSAGE_REGEX = "(.*?)\"((?:\\\\.|[^\"\\\\])*)\"";

    public static final String CREATE_ACCOUNT_MSG = "create_account";
    public static final String LOGIN_MSG = "login";
    public static final String LOGOUT_MSG = "logout";
    public static final String SEND_MSG = "send";
    public static final String READ_MAILBOX_MSG = "read_mailbox";
    public static final String READ_MESSAGE_MSG = "read_message";
    public static final String FORCE_LOGOUT_MSG = "force_logout";
    public static final String NEW_MESSAGE_IN_MAILBOX_MSG = "new_message_in_mailbox";

    public static final String OK_MSG = "\u001B[32mOK:\u001B[0m ";
    public static final String ERR_MSG = "\u001B[31mERROR:\u001B[0m ";
    public static final String INVALID_PASSWORD_MSG = "Invalid password for user ";
    public static final String INVALID_CREDENTIALS_MSG = "Bad format for username and/or password ";
    public static final String DUPLICATE_USER_MSG = "The username is taken";
    public static final String USER_NOT_FOUND_MSG = "Could not find user ";
    public static final String MESSAGE_NOT_FOUND_MSG = "Could not find message ";
    public static final String USER_NOT_LOGGEDIN_MSG = "User is not logged in";
    public static final String INVALID_REQUEST_FORMAT_MSG = "Invalid request format";
    public static final String SEND_MESSAGE_TO_SAME_USER_MSG = "You can't send message to yourself";
}
