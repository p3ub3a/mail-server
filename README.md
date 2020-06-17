## Mail server

### Server:

Start *Runner.main*, it accepts max 5 client connections by default. If the limit is reached, other clients will have to wait for a slot to be open.

*ClientManager* handles each request coming from the client. Possible requests | responses:

- **create_accout username password** | OK / Error + reason (*The username is taken*, *Bad format for username and/or password*, *Invalid request format*);
- **login username password** | OK / Error + reason (*Invalid password for user X*, *Could not find user X*, *Invalid request format*);
- **logout** | OK / Error + reason (*User is not logged in*);
- **send user1 user2 ... "message"** | OK / Error + reason(*You can't a send message to yourself*, *Could not find user X*, *Invalid request format*);
- **read_mailbox** | OK / Error + reason(*User is not logged in*);
- **read_msg id** | OK + from + message / Error + reason(*Invalid request format*).

Each client interacts with a different server thread. If a client process exits, the server will free one thread and it should be ready for a new client connection.

### Client:

Simply run *interface.js*. Once a socket connection is established, the client will wait for messages from the server.

The client can receive notifications(like *force logout* or *new message received*) even when it is waiting for a user input.

If the server stops working, *socket.error* is triggered and the client is disconnected.
