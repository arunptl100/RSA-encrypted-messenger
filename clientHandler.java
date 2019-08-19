import java.io.*;
import java.util.*;
import java.net.*;

public class clientHandler extends Thread{
  final BufferedReader in;
  final PrintWriter out;
  final Socket s;
  int id;

  public int getID(){
    return this.id;
  }
  public clientHandler(BufferedReader in, PrintWriter out, Socket s, int id){
    this.in = in;
    this.out = out;
    this.s = s;
    this.id = id;
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
    return Integer.parseInt(message.substring(message.indexOf("{") + 1, message.indexOf("}")));
  }

  public String getMessageString(String message){
    return message.substring(message.indexOf("{"));
  }

  public void removeSelf(){
    System.out.println("Removing " + this.id + " from client list");
    server.clientList.remove((getIndexOfId(this.id)));
  }

  @Override
  public void run(){
    String inputLine, outputLine;

    outputLine = "Connection established";
    out.println(outputLine);

    try{
      while((inputLine = in.readLine()) != null){
        //System.out.println("Client " + this.id +": " + inputLine);
        //pass the message onto the relevant client
        if(inputLine.equals("list")){
          outputLine = "";
          System.out.println("Client " + this.s + " sends list "+
          "generating string of client ids");
          for(int x = 0; x < server.clientList.size(); x++ ){
            int id = server.clientList.get(x).getKey();
            if(id != this.id){
              outputLine += " Client:" + id;
            }
          }
          out.println(outputLine);
          continue;
        }
        if(inputLine.equals("exit")){
          System.out.println("Client " + this.s + " sends exit... "+
          "Closing this connection.");
          removeSelf();
          this.s.close();
          break;
        }
        int recipient = getRecipientFromMessage(inputLine);
        String msg = getMessageString(inputLine);
        System.out.println("sending message " + msg + " to client id " + reciever);
        (getRecipient(recipient)).sendMessage("Client " + id + " sent you: "+ msg);


      }
    }catch(IOException e){
      e.printStackTrace();
    }

    try
    {
      // closing resources
      this.in.close();
      this.out.close();

    }catch(IOException e){
      e.printStackTrace();
    }

  }
}
