import java.net.*;
import java.io.*;
import java.util.*;

public class server{
  static LinkedList<KeyValuePair<Integer,clientHandler>> clientList;

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
