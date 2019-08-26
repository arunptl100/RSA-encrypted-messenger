import java.net.*;
import java.io.*;
import java.util.*;

public class server{
  static LinkedList<KeyValuePair<Integer,clientHandler>> clientList;

  /*method returning a string of connected clients usernames and respective ids
  */
  public static String getListOfClients(int clientHandlerID){
    String out = "";
    for(int x = 0; x < clientList.size(); x++ ){
      int id = clientList.get(x).getKey();
      String username = clientList.get(x).getVal().getUsername();
      if(id != clientHandlerID){
        out += "[Username: " + username + " id: " + id  + "]\n";
      }
    }
    return out;
  }

  /*method returning the client ID of the first occurence of a client with the
  *desired username
  *Error code: -1 indicates the username coudnt find be found
  */
  public static int findUser(String username){
    for(int x = 0; x < clientList.size(); x++ ){
      int id = clientList.get(x).getKey();
      String CurrUsername = clientList.get(x).getVal().getUsername();
      if(CurrUsername == username){
        return id;
      }
    }
    return -1;
  }

  public static void main(String[] args) throws IOException{
    int portNumber = 25565;
    ServerSocket serverSocket = new ServerSocket(portNumber);
    int id = 0;
    clientList = new LinkedList<>();
    System.out.println("Initialised server on port " + portNumber + " ready to accept connections");
    while(true){
      Socket clientSocket;
      try{
        clientSocket = serverSocket.accept();
        System.out.println("new client connected " + clientSocket + " id = " + id);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // create a new thread object
        clientHandler client = new clientHandler(in, out, clientSocket, id);
        Thread t = new Thread(client);
        clientList.add(new KeyValuePair<Integer,clientHandler>(id, client));
        id++;
        // Invoking the start() method
        t.start();

      }catch(IOException e){
        System.out.println("Exception caught when trying to listen on port "
        + portNumber + " or listening for a connection");
        System.out.println(e.getMessage());
      }
    }


  }
}
