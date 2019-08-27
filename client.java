import java.net.*;
import java.io.*;
import java.util.*;

public class client{
  static BufferedReader in;

  public client(String hostName, int portNumber){
    /* usr names for connected clients (store as kvps in the client storage structure?)
     * search for a connected clients id based on username
     *
     */
    try{
      /*Initialise resources to connect to the server*/
      Socket kkSocket = new Socket(hostName, portNumber);
      PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
      in = new BufferedReader(
      new InputStreamReader(kkSocket.getInputStream()));
      BufferedReader stdIn =
      new BufferedReader(new InputStreamReader(System.in));


      String fromUser;
      /*setup a thread for listening for messages from the server*/
      serverListener sl = new serverListener();
      Thread t = new Thread(sl);

      t.start();
      while(true){
        fromUser = stdIn.readLine();
        if(fromUser.toUpperCase().equals("EXIT")){
          out.println(fromUser);
          break;
        }else if(fromUser != null){
          //System.out.println("Sending message " + fromUser);
          out.println(fromUser);
        }
      }
    }catch(UnknownHostException e){
      System.err.println("Invalid host " + hostName);
      System.exit(1);
    }catch(IOException e){
      System.err.println("Couldn't get I/O for the connection to " +
      hostName);
      System.exit(1);
    }
  }
}
/*
 *
 */
class serverListener extends Thread{
  String fromServer;
  public void run(){
    try{
      while((fromServer = client.in.readLine()) != null){
        if(fromServer.equals("CLOSE -0")){
          break;
        }
        System.out.println("Server: " + fromServer);
      }
    }catch(Exception e){
      System.out.println(e.toString());
    }

  }
}
