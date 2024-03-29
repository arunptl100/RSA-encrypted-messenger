import java.net.*;
import java.io.*;
import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;

public class client{

  /* Method to hash a string using SHA256
  * Returns the result in hex representation
  */
  public static String getSHA256Hash(String data) {
    String result = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(data.getBytes("UTF-8"));
      // bytes to hex
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public client(String hostName, int portNumber, int pubkey_p, int pubkey_q, int pubkey_e){
    try{
      /*Initialise resources to connect to the server*/
      Socket kkSocket = new Socket(hostName, portNumber);
      ObjectOutputStream out = new ObjectOutputStream(kkSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(kkSocket.getInputStream());

      Scanner sin = new Scanner(System.in);

      String fromUser;
      /*setup a thread for listening for messages from the server*/
      serverListener sl = new serverListener(in,out,pubkey_p,pubkey_q,pubkey_e);
      Thread t = new Thread(sl);
      t.start();
      //send the users public key (n,e)
      clientpkey cpkey = new clientpkey((pubkey_p*pubkey_q), pubkey_e);
      out.writeObject(cpkey);
      out.flush();

      while(true){
        fromUser = sin.nextLine();

        if(fromUser.toUpperCase().equals("EXIT")){
          out.writeUTF(fromUser); //send the command to the server
          out.flush();
          //alowing for the connection to be closed server side
          break; //break to close the connection client side

          /* encrypt command
          * allows the user to encrypt a message with the recipients public key
          * Requests the plaintext message and recipient id
          * next the encrypt message command is sent to the server to initiate
          *  the encrypted message process server side
          * next ONLY the recipient id is sent to the server
          * the server sends back to the client the public key of the recipient
          * encrypt command continues in class serverListener in run method
          */
        }else if(fromUser.toUpperCase().equals("ENCRYPT")){
          try{

            int recipient_id;
            System.out.println("Client: Enter message to be sent");
            sl.encryptedMessage = sin.nextLine();
            System.out.println("Client: Enter client id to sent message to");
            recipient_id = Integer.parseInt(sin.nextLine());
            out.writeUTF(fromUser); //sends the command encrypt to the server
            out.flush();

            //send the server the recipient id
            out.writeInt(recipient_id);
            out.flush();
            //the server will send the public key of the recipient
            //continue in the serverListener thread


          }catch(Exception e){
            e.printStackTrace();
          }
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
  ObjectInputStream in;
  ObjectOutputStream out;
  Reciever rcvr;
  volatile String encryptedMessage;

  public serverListener(ObjectInputStream in, ObjectOutputStream out, int pubkey_p, int pubkey_q, int pubkey_e){
    this.in = in;
    this.out = out;
    this.rcvr = new Reciever(pubkey_p, pubkey_q, pubkey_e);
  }

  public void run(){
    try{
      //iterate whilst there is something being sent from the server
      while((fromServer = this.in.readUTF()) != null){
        //CLOSE -0 is a message sent from the server indicating
        //that the associated client has requested to exit
        if(fromServer.equals("CLOSE -0")){
          //breaking from the loop will cause the thread to end
          break;

        }else if(fromServer.toUpperCase().equals("-PKEY01")){
          /* encrypt command continuation
          * the server has sent the message to the client "-PKEY01"
          * triggering this thread to continue processing the encrypt command
          * firstly the clientpkey object is read in from the server
          *  which stores the recipients public key
          * next a reciever object is instantiated with the recipients public key
          * the plaintext message is encrypted using the object
          * ciphertext is then sent back to the server
          * the server will then forward the message onto the recipient
          * the server will respond with a success message ending the command
          */
          clientpkey recipPkey = (clientpkey) this.in.readObject();
          int n = recipPkey.n;
          int e = recipPkey.e;
          System.out.println("Client: Recieved public key from server of recipient with id "+recipPkey.id+" n = " + n + ", e = " + e);
          Sender encr = new Sender(n, e);
          System.out.print("Client: Generated ciphertext: ");
          BigInteger[] encryptedMess = encr.encryptString(this.encryptedMessage);
          for (int x = 0; x < encryptedMess.length; x++ ) {
            System.out.print(encryptedMess[x] + ",");
          }
          System.out.println();
          //send the server the ciphertext
          this.out.writeObject(encryptedMess);
          System.out.println("Client: ciphertext sent to server");
          /* now generate digital signature (integrity + authentication)
          * This is done by computing the hash of the message
          * Then encrypting the hash using the senders PRIVATE key
          * the reciever then decrypts the digital signature using the senders PUBLIC key
          *    this authenticates the sender as only the sender has that private key
          * the reciever computes a hash of the recieved message and compares it to the recieved digital signature
          * if they match then the integrity of the message can be ensured (the message wasnt tampered with
          * during transit across the unsage network)
          */
          String digSig = client.getSHA256Hash(this.encryptedMessage);
          //intitialise a Sender object with the senders PRIVATE key
          //Encrypt the digital signature with the senders private key
          //Operation performed C == M^d mod n
          Sender digSigSender = new Sender(this.rcvr.getN(),this.rcvr.getPrivateKey());
          BigInteger[] encryptedDigSig = digSigSender.encryptString(digSig);
          //send the encypted digital signature to the server
          this.out.writeObject(encryptedDigSig);
          System.out.println("Client: digital signature encrypted, generated and sent to server\n" + digSig);

        }else if(fromServer.toUpperCase().equals("-ENCRYPTEDMSG01")){
          /* encrypt command continuation (Reciever)
          * the server has sent the message to the client "-ENCRYPTEDMSG01"
          * triggering this thread to decrypt recieved ciphertext on the recipient client
          * Decrypt the recieved ciphertext with the clients private key
          */
          BigInteger[] ciphertext = (BigInteger[]) this.in.readObject();
          BigInteger[] recDigSig = (BigInteger[]) this.in.readObject();
          clientpkey senderData = (clientpkey) this.in.readObject();
          String senderInfo = "(Username:"+senderData.username+",id:"+senderData.id+")";
          System.out.print("Client: Recieved ciphertext: ");
          for (int x = 0; x < ciphertext.length; x++ ) {
            System.out.print(ciphertext[x] + ",");
          }
          System.out.println(" from " + senderInfo);
          String message = rcvr.decryptString(ciphertext);
          System.out.println("Client: Decrypted ciphertext (from "+senderInfo+") into message:\n-->: " + message);
          //decrypt the recieved digital signature
          /*  The recieved digital signature is encrypted with the senders private key
           *  To decrypt use the senders public key, stored in sender data
           */
          Reciever digSigReciever = new Reciever(senderData.n,senderData.e);
          //operation performed M == C^e mod n 
          String decryptedSig = digSigReciever.decryptString(recDigSig);
          System.out.println("\tDecrypted recieved digital signature: " +decryptedSig);
          if(decryptedSig.equals(client.getSHA256Hash(message))){
            System.out.println("\tClient: " + ConsoleColours.GREEN +
            "Digital signature analysis: message integrity verified and sender authenticated" +
            ConsoleColours.RESET);
          }else{
            System.out.println("\tClient: " + ConsoleColours.RED +
            "Digital signature analysis: message integrity and sender authentication could not be verified " +
            ",the message may have been tampered with during transit across network or sender may be an imposter" +
            ConsoleColours.RESET);
          }
        }else{
          System.out.println("Server: " + fromServer);
        }
      }
    }catch(Exception e){
      System.out.println(e.toString());
    }


  }
}
