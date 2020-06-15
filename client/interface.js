const net = require('net');
const readline = require('readline');

const CREATE_ACCOUNT_MSG = "create_account";
const LOGIN_MSG = "login";
const LOGOUT_MSG = "logout";
const SEND_MSG = "send";
const READ_MAILBOX_MSG = "read_mailbox";
const READ_MESSAGE_MSG = "read_message";
const EXIT_MSG = "exit";
const FORCE_LOGOUT_MSG = "force_logout";
const NEW_MESSAGE_IN_MAILBOX_MSG = "new_message_in_mailbox";

const actionQuestion = "Please choose one action: ";
const actions = [CREATE_ACCOUNT_MSG, LOGIN_MSG, LOGOUT_MSG, SEND_MSG, READ_MAILBOX_MSG,READ_MESSAGE_MSG,EXIT_MSG];
const createAccountQuestion = "Enter the desired username and password *username password*: ";

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
  });

var options = {
    port: 5432,
    host: "localhost"
}

start();
var socket = new net.Socket();
socket.connect(options);
var isLoggedIn = false;

async function start(){
    var input;
    if(isLoggedIn){
        input = await getUserInput(1, 6);
    }else{
        input = await getUserInput(0, 1);
        console.log("ajungerwqhrq");
    }
    
    var msg = "";

    switch(input){
        //create account
        case "0":
            var credentials = await getCredentials(createAccountQuestion);
            console.log(credentials);
            sendMsg(socket, CREATE_ACCOUNT_MSG + " " + credentials + "\n");
            break;
        // login username password
        case "1":
            var credentials = await getCredentials(createAccountQuestion);
            console.log(credentials);
            sendMsg(socket, LOGIN_MSG + " " + credentials + "\n");
            break;
        // logout
        case "2":
            isLoggedIn = false;
            console.log(input);
            break;
        // send users msg
        case "3":
            console.log(input);
            break;
        // read mailbox
        case "4":
            console.log(input);
            break;
        // read message id
        case "5":
            console.log(input);
            break;
        // exit
        case "6":
            console.log(input);
            process.exit();
        default:
            console.warn("\ninput not known\n");
    }

    start();
}

function getUserInput(min, max){
    return new Promise((resolve, reject) => {
        askQuestion(resolve);
    }).catch(err => {
        console.log(err);
        askQuestion(resolve);
    });

    function askQuestion(resolve) {
        var question = actionQuestion;
        for (i = min; i <= max; i++) {
            question += "\n" + i + ")" + actions[i];
        }
        question += "\n";
        rl.question(question, (action) => {
            if (action >= min && action <= max) {
                return resolve(action);
            }
            else {
                console.log("Please choose an action ranged " + min + " - " + max + "\n");
                askQuestion(resolve);
            }
        });
    }
}

function getCredentials(question){
    return new Promise((resolve, reject) => {
        rl.question( question, (action) => {
            return resolve(action);
        });
    }).catch(err => {
        console.log(err);
        getUserInput();
    });
}

async function sendMsg(socket, text){
    socket.write(text);
}

socket.on("data", (data) => {
    if(data.includes(LOGOUT_MSG) && data.includes("OK")){
        socket.end();
    }

    if(data.includes(LOGIN_MSG) && data.includes("OK")){
        isLoggedIn = true;
    }
    console.log( "----> server says: " + data);
});