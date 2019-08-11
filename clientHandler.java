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

  @Override
  public void run(){
    String inputLine, outputLine;

    outputLine = "Connection established";
    out.println(outputLine);

    try{
      while((inputLine = in.readLine()) != null){
        System.out.println("Client " + this.id +": " + inputLine);

        outputLine = "response from server";
        if (inputLine.equals("exit")){
          System.out.println("Client " + this.s + " sends exit... "+
          "Closing this connection.");
          this.s.close();
          break;
        }
        out.println(outputLine);
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
