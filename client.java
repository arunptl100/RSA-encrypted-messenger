import java.net.*;
import java.io.*;
import java.util.*;
import java.math.BigInteger;

public class client{
  static ObjectInputStream in;
  static String encryptMessage;

  public client(String hostName, int portNumber, int pubkey_n, int pubkey_e){
    try{
      /*Initialise resources to connect to the server*/
      Socket kkSocket = new Socket(hostName, portNumber);
      ObjectOutputStream out = new ObjectOutputStream(kkSocket.getOutputStream());
      in = new ObjectInputStream(kkSocket.getInputStream());


      Scanner sin = new Scanner(System.in);

      String fromUser;
      /*setup a thread for listening for messages from the server*/
      serverListener sl = new serverListener();
      Thread t = new Thread(sl);
      t.start();
      //send the users public key (n,e)
      String pubKey = "("+pubkey_n+","+pubkey_e+")";
      out.writeUTF(pubKey);
      out.flush();
      while(true){
        fromUser = sin.nextLine();

        if(fromUser.toUpperCase().equals("EXIT")){
          out.writeUTF(fromUser); //send the command to the server
          out.flush();
          //alowing for the connection to be closed server side
          break; //break to close the connection client side

        /* encrypt command
         * This command first needs to be captured client side then server side
         * Firstly, the client process asks the client for the message to be sent
         * storing the result in the static global variable encryptMessage
         * Next , the command is sent to the server , where the server asks the
         * client for the recipient ID. It then sends the client process the
         * public key for the recipient. The clients serverListener thread
         * will detect this response from the server : -PKEY01 (n,e)
         * and will instantiate a sender object with the recieved pkey
         * and encrypt encryptMessage sending the result to the server
         * where it will forward it on to the recipient
         */
        }else if(fromUser.toUpperCase().equals("ENCRYPT")){
          //first get the message to be sent
          System.out.println("Client: Enter message to be sent");
          this.encryptMessage = sin.nextLine();
          out.writeUTF(fromUser);
          out.flush();
          continue;
        }else if(fromUser != null){
          //System.out.println("Sending message " + fromUser);
          out.writeUTF(fromUser);
          out.flush();
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
/* class dedicated listening to incoming messages from the server
 * Instantiated and ran in a seperate thread.
 * On recieval of a message , it prints the message to the screen
 */
class serverListener extends Thread{
  String fromServer;
  public void run(){
    try{
      //iterate whilst there is something being sent from the server
      while((fromServer = client.in.readUTF()) != null){
        //CLOSE -0 is a message sent from the server indicating
        //that the associated client has requested to exit
        if(fromServer.equals("CLOSE -0")){
          //breaking from the loop will cause the thread to end
          break;
        }else if(fromServer.substring(0,7).equals("-PKEY01")){
          //example input -PKEY01(5,11)
          //the client is in the process of encrypting a message
          //the server has sent the public key for the recipient
          //decode the public key and encrypt client.encryptMessage with it
          //send the result back to the server
          int n = Integer.parseInt(fromServer.substring(8, fromServer.indexOf(",")));
          int e = Integer.parseInt(fromServer.substring((fromServer.indexOf(",")+1), fromServer.indexOf(")")));
          System.out.println("Client: decoded server public key for recipient n = " + n + ", e = " + e);
          Sender encr = new Sender(n, e);
          System.out.print("Client: Ciphertext: ");
          BigInteger[] encryptedMess = encr.encryptString(client.encryptMessage);
          for (int x = 0; x < encryptedMess.length; x++ ) {
            System.out.print(encryptedMess[x] + ",");
          }
          continue;
          //bug: if clients disconnect, getIndexOfId (I THNK) returns -1 when searching for a valid id
          //exception in this case line 137 clientHandler.java
        }else{
          System.out.println("Server: " + fromServer);
        }
      }
    }catch(Exception e){
      System.out.println(e.toString());
    }

  }
}
