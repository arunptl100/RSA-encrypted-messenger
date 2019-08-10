import java.net.*;
import java.io.*;

public class server{

  public static void main(String[] args) {
    int portNumber = 25565;
    try{
      ServerSocket serverSocket = new ServerSocket(portNumber);
      Socket clientSocket = serverSocket.accept();
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      String inputLine, outputLine;

      outputLine = "hello from server ";
      out.println(outputLine);

      while ((inputLine = in.readLine()) != null) {
        outputLine = "response from server";
        if (inputLine.equals("exit")){
          out.println("CLOSING");
          break;
        }
        out.println(outputLine);
      }
    }catch(IOException e){
      System.out.println("Exception caught when trying to listen on port "
      + portNumber + " or listening for a connection");
      System.out.println(e.getMessage());
    }
  }
}
