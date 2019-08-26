import java.io.*;
import java.util.*;
import java.net.*;

public class clientHandler extends Thread{
  final BufferedReader in;
  final PrintWriter out;
  final Socket s;
  int id;
  private String username;

  public int getID(){
    return this.id;
  }
  public clientHandler(BufferedReader in, PrintWriter out, Socket s, int id){
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
    this.out.println(msg);
  }
  public int getIndexOfId(int id){
    //iterate throught the linked list of clientHandlers
    for (int x = 0; x < server.clientList.size(); x++) {
      //check if the key (storing client id) matches the target
      if(server.clientList.get(x).getKey() == id){
        //return the index
        return x;
      }
    }
    return -1;
  }

  public clientHandler getRecipient (int id){
    return server.clientList.get(getIndexOfId(id)).getVal();
  }

  public int getRecipientFromMessage(String message){
    //messages will be of the form: {recipient_client_id} message
    try{
      return Integer.parseInt(message.substring(message.indexOf("{") + 1, message.indexOf("}")));
    }catch(StringIndexOutOfBoundsException e){
      return -1;
    }
  }

  public String getMessageString(String message){
    return message.substring(message.indexOf("{"));
  }

  public void removeSelf(){
    System.out.println("Removing " + this.id + " from client list");
    server.clientList.remove((getIndexOfId(this.id)));
  }

  public void sendServerMessage(String msg){
    out.println(server.GenConsoleMessage(msg));
  }


  @Override
  public void run(){
    String inputLine, outputLine;
    try{
      outputLine = "Connection established, please enter username";
      sendServerMessage(outputLine);
      inputLine = in.readLine();
      this.username = inputLine;
      sendServerMessage("Username set to " + this.username);
    }catch(IOException e){
      e.printStackTrace();
    }

    try{
      while((inputLine = in.readLine()) != null){
        //System.out.println("Client " + this.id +": " + inputLine);
        //pass the message onto the relevant client
        //first check if the user has entered a command
        if(inputLine.toUpperCase().equals("LIST")){
          System.out.println(server.GenConsoleMessage("Client " + this.s + " sends list ")+
          "generating string of client ids");
          sendServerMessage(server.getListOfClients(this.id));
          continue;
        }
        if(inputLine.toUpperCase().equals("EXIT")){
          System.out.println(server.GenConsoleMessage("Client " + this.s + " requests exit "+
          "Closing this connection."));
          sendServerMessage("CLOSE -0"); //close code for client 0 indicates closure from exit request
          removeSelf();
          this.s.close();
          break;
        }
        if(inputLine.toUpperCase().equals("SEARCH")){
          System.out.println(server.GenConsoleMessage("Please enter username to search for "));
          inputLine = in.readLine();
          int id = server.findUser(inputLine);
          if(id >= 0){
            sendServerMessage("Found client with username " + inputLine + " with id " + id);
          }else{
            sendServerMessage("A client with username " + inputLine + " is not currently connected to the server ");
          }
          continue;
        }
        if(inputLine.toUpperCase().equals("HELP") || inputLine.toUpperCase().equals("COMMANDS")){
          sendServerMessage("help : generates a list of available commands and their functions\n"
          +"commands : generates a list of available commands and their functions\n"
          +"list : generates a list of currently connected clients, displaying their username and id\n"
          +"search : asks for a username and attempts to find the relevant id of that user\n"
          +"exit : closes the connection to the server");
          continue;
        }

        //decode the message into recipient and message parts
        int recipient = getRecipientFromMessage(inputLine);
        if(recipient >= 0){
          System.out.println(server.GenConsoleMessage("decoded message into recipient num " + recipient));
          String msg = getMessageString(inputLine);
          System.out.println(server.GenConsoleMessage("sending message " + msg + " to client id " + recipient));
          (getRecipient(recipient)).sendMessage("Client " + id + " sent you: "+ msg);
        }else{
          sendServerMessage("Messages must be of the form: '{recipient id} message'." +
          " For a list of connected client ids, use command: 'list'");
        }


      }
    }catch(IOException e){
      e.printStackTrace();
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
