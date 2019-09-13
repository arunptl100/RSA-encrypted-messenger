import java.io.*;
import java.util.*;
import java.net.*;
import java.math.BigInteger;

public class clientHandler extends Thread{
  final ObjectInputStream in;
  final ObjectOutputStream out;
  /*OUTPUT I/O STREAMS ARE NOT THREAD SAFE !*/
  final Socket s;
  int id;
  private String username;
  private int pubkey_n;
  private int pubkey_e;

  public int getID(){
    return this.id;
  }
  public int getPubKeyN(){
    return this.pubkey_n;
  }
  public int getPubKeyE(){
    return this.pubkey_e;
  }
  public clientHandler(ObjectInputStream in, ObjectOutputStream out, Socket s, int id){
    this.in = in;
    this.out = out;
    this.s = s;
    this.id = id;
  }
  public String getUsername(){
    return this.username;
  }
  public void setUsername(String username){
    this.username = username;
  }
  public void sendMessage(String msg){
    try{
      this.out.writeUTF(msg);
      this.out.flush();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  /* Method returning the index of a client id in the client structure
  * (linked list)
  */
  public int getIndexOfId(int id){
    //iterate throught the linked list of clientHandlers
    for (int x = 0; x < server.clientList.size(); x++) {
      //check if the key (storing client id) matches the target
      if(server.clientList.get(x).getKey().equals(id)){
        //return the index
        return x;
      }
    }
    return -1;
  }

  /* Method returning the client handler for the corresponding passed client id
  * Accesses the index of the linked list where the client id is stored.
  * This index is found using the getIndexOfId method
  */
  public clientHandler getRecipient (int id){
    return server.clientList.get(getIndexOfId(id)).getVal();
  }

  /* Method returning the client recipient id part of the message to be sent
  * expected input:
  *    string of the form "{recipient id} message"
  * returns int > 0
  *    value returned represents the id of the recipient
  * returns -1
  *    value returned represents an error code indicating the recipient
  *    could not be found in the structure and so is not connected to the server
  *    (invalid recipient)
  */
  public int getRecipientFromMessage(String message){
    //messages will be of the form: {recipient_client_id} message
    try{
      return Integer.parseInt(message.substring(message.indexOf("{") + 1, message.indexOf("}")));
    }catch(Exception e){
      return -1;
    }
  }

  /* Method returning message part of the of the message to be sent
  * expected input:
  *    string of the form "{recipient id} message"
  */
  public String getMessageString(String message){
    return message.substring(message.indexOf("}")+2);
  }

  /* Method that removes the current clientHandler instance from the client
  * structure.
  */
  public void removeSelf(){
    System.out.println(server.GenConsoleMessage("Removing " + this.id + " from client list"));
    server.clientList.remove((getIndexOfId(this.id)));
  }

  /* Method that prepares and sends a message to the client associated with
  * the current clientHandler
  * Messages are prepared by attaching the current time and date to the start
  * of the message
  */
  public void sendServerMessage(String msg){
    sendMessage(server.GenConsoleMessage(msg));
  }


  @Override
  public void run(){
    String inputLine, outputLine;
    try{
      sendServerMessage(ConsoleColours.GREEN + "Connection established" + ConsoleColours.RESET);
      clientpkey cpkey = (clientpkey) in.readObject();
      this.pubkey_n = cpkey.n;
      this.pubkey_e = cpkey.e;
      sendServerMessage("Public key stored on server as (n=" + this.pubkey_n + ",e=" + this.pubkey_e + ")");
      outputLine = "Please enter username";
      sendServerMessage(ConsoleColours.GREEN + outputLine + ConsoleColours.RESET);
      inputLine = in.readUTF();
      this.username = inputLine;
      sendServerMessage("Username set to " + this.username);

      while((inputLine = in.readUTF()) != null){
        //System.out.println("Client " + this.id +": " + inputLine);
        //pass the message onto the relevant client
        //first check if the user has entered a command
        if(inputLine.toUpperCase().equals("ENCRYPT")){
          System.out.println(server.GenConsoleMessage("initiating encrypt command for client " + this.id));
          try{
            //cient will send empty clientpKey object with recipient id
            int recipient_id = in.readInt();
            if((getIndexOfId(recipient_id)) < 0){
              //the recipient id does not exist in the data structure
              //send a failure message
              sendServerMessage(ConsoleColours.RED +
              "The client id you have entered could not be found on the server, " +
              "use the command" + ConsoleColours.GREEN + " 'list' " +
              ConsoleColours.RED + "for a list of connected clients "
              + ConsoleColours.RESET);
              continue;
            }else{
              //get the public key of the the reciever and send it to the client
              clientHandler recipient = getRecipient(recipient_id);
              clientpkey recipPkey = new clientpkey(recipient.getPubKeyN(),recipient.getPubKeyE());
              recipPkey.id = recipient_id;
              //write the object to the client
                //first prep the client for recieving a public key by sending the -PKEY01 message
              out.writeUTF("-PKEY01");
              out.flush();
              //now the client is expecting the public key,send the object
              out.writeObject(recipPkey);
              out.flush();
              System.out.println(server.GenConsoleMessage(
              "sending public key of client " + recipient_id + " public key (" +recipPkey.n+","+recipPkey.e+ ") to client "+ this.id));
              //wait for the client to compute ciphertext and to send it here
              BigInteger[] cipherText = (BigInteger[]) in.readObject();
              System.out.print(server.GenConsoleMessage(
              "Recieved ciphertext from client " + this.id + ": "));
              for (int x = 0; x < cipherText.length; x++ ) {
                System.out.print(cipherText[x] + ",");
              }
              System.out.println();
              //get the clientHandler instnace for the recipient
              clientHandler msg_recipient = getRecipient(recipient_id);
              //prepare the recipient client for recieving ciphertext
              msg_recipient.sendMessage("-ENCRYPTEDMSG01");
              //send the recipient the ciphertext
              msg_recipient.out.writeObject(cipherText);
              System.out.println(server.GenConsoleMessage("Sending client " + recipient_id) + " ciphertext " +
              "generated by client " + this.id);
              sendServerMessage(ConsoleColours.GREEN + " Encrypted message delivered to client " + recipient_id + ConsoleColours.RESET);
              //send the recipient client data about the sender, (id and username)
              clientpkey senderData = new clientpkey(0, 0);
              senderData.username = this.username;
              senderData.id = this.id;
              msg_recipient.out.writeObject(senderData);
            }
          }catch(Exception e){
            sendServerMessage(ConsoleColours.RED + "Invalid client id" + ConsoleColours.RESET);
          }
          continue;
        }
        /* list command
        * returns to the client a list of connected clients in the form
        * [username,id] ...
        */
        if(inputLine.toUpperCase().equals("LIST")){
          System.out.println(server.GenConsoleMessage("Client " + this.s + " sends list ")+
          "generating string of client ids");
          sendServerMessage(server.getListOfClients(this.id));
          continue;
        }
        /* exit command
        * removes the current client instance from the structure and sends the
        * close message to the client, iniating closure on the client side
        * the client socket is also closed
        */
        if(inputLine.toUpperCase().equals("EXIT")){
          System.out.println(server.GenConsoleMessage(ConsoleColours.RED + "Client " + this.s + " requests exit "+
          "Closing this connection." + ConsoleColours.RESET));
          removeSelf();
          sendServerMessage("CLOSE -0"); //close code for client 0 indicates closure from exit request
          this.s.close();
          break;
        }
        /* search command
        * requests a username from the client and searches the structure for that
        * username. If found, the corresponding id for that username is returned to the client
        * ONLY the first occurence of the username is returned
        */
        if(inputLine.toUpperCase().equals("SEARCH")){
          sendServerMessage("Please enter username to search for ");
          inputLine = in.readUTF();
          int id = server.findUser(inputLine);
          if(id >= 0){
            sendServerMessage("Found client with username " + inputLine + " with id " + id);
          }else{
            sendServerMessage("A client with username " + inputLine + " is not currently connected to the server ");
          }
          continue;
        }
        /* help/commands command
        * helper command listing all commands and their functions
        */
        if(inputLine.toUpperCase().equals("HELP") || inputLine.toUpperCase().equals("COMMANDS")){
          sendServerMessage("\nhelp : generates a list of available commands and their functions\n"
          +"commands : generates a list of available commands and their functions\n"
          +"list : generates a list of currently connected clients, displaying their username and id\n"
          +"search : asks for a username and attempts to find the relevant id of that user\n"
          +"exit : closes the connection to the server\n"
          +"encrypt : allows the user to encrypt a message for another specified user");
          continue;
        }
        /* if no command was entered then the client must have sent a message to be sent to another client
        * OR the client has sent an invalid message
        */
        //decode the message into recipient and message parts
        int recipient = getRecipientFromMessage(inputLine);
        //if the recipient is < 0 then the client has sent an invalid message or
        //the recipient client id does not exist in the structure
        if(recipient >= 0){
          sendServerMessage("Attempting to send message to user " + recipient);
          System.out.println(server.GenConsoleMessage("decoded message into recipient num " + recipient));
          String msg = getMessageString(inputLine);
          System.out.println(server.GenConsoleMessage("sending message {" + msg + "} to client id " + recipient));
          //get the recipient clientHandler object and send the intended message to the recipient
          //prepare the message by calling server.GenConsoleMessage with the message
          (getRecipient(recipient)).sendMessage(server.GenConsoleMessage("Recieved message from client ["+username
          + ",id:" + this.id + "] : "+ msg));
          sendServerMessage(ConsoleColours.GREEN + "Message delivered to client id : " + recipient + ConsoleColours.RESET);
        }else{
          //inform the user that their input is invalid.
          sendServerMessage(ConsoleColours.RED + "Messages must be of the form: '{recipient id} message'." +
          " For a list of connected client ids, use command: "+ ConsoleColours.GREEN +"'list' or 'help'" + ConsoleColours.RESET);
        }

      }

    }catch(Exception e){
      try{
        System.out.println(server.GenConsoleMessage(ConsoleColours.RED + "Client " + this.s +
        " has exited, closing the connection" + ConsoleColours.RESET));
        removeSelf();
        this.s.close();
      }catch(Exception f){
        f.printStackTrace();
      }

    }

    try
    {// closing resources
      this.in.close();
      this.out.close();

    }catch(IOException e){
      e.printStackTrace();
    }

  }
}
