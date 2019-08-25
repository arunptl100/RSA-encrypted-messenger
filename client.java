import java.net.*;
import java.io.*;
import java.util.*;

public class client{
  static BufferedReader in;

  public client(String hostName, int portNumber){

    try{
      Socket kkSocket = new Socket(hostName, portNumber);
      PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
      in = new BufferedReader(
      new InputStreamReader(kkSocket.getInputStream()));
      BufferedReader stdIn =
      new BufferedReader(new InputStreamReader(System.in));


      String fromUser;
      /* Problem- the client waits for user input neglecting messages from the server
      * Once the input is given, execution continue and server input is checked
      * This leads to a backlog of messages not sent to the client whilst waiting for input
      */
      /* Create a thread for listening to server messages and outputting them to console
      * Another thread for listening to user input and reacting accordingly
      */
      serverListener sl = new serverListener();
      Thread t = new Thread(sl);
      t.start();
      while (true) {
        fromUser = stdIn.readLine();
        if (fromUser != null) {
          System.out.println("Client: " + fromUser);
          out.println(fromUser);
        }
      }
    } catch (UnknownHostException e) {
      System.err.println("Invalid host " + hostName);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to " +
      hostName);
      System.exit(1);
    }
  }
}

class serverListener extends Thread{
  String fromServer;
  public void run(){
    try{
      while((fromServer = client.in.readLine()) != null){
        System.out.println("Server: " + fromServer);
      }
    }catch(Exception e){
      System.out.println(e.toString());
    }

  }
}
