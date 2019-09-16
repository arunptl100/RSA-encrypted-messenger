import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Sender{
  //encrypts a message with a Reciever class instance public key and value e
  public int n_pubKey;
  public int e_pubKey;

  public Sender(int n, int e){
    this.n_pubKey = n;
    this.e_pubKey = e;
    // System.out.println("Initialised sender with recievers public key (n=" +
    // this.n_pubKey + ",e=" + this.e_pubKey + ")" );
  }
  public void setPubKey(int n, int e){
    this.n_pubKey = n;
    this.e_pubKey = e;
  }

  public BigInteger encrypt(BigInteger Message){
    //Ciphertext == M^e mod n
    //Ciphertext can be large so use BigInteger
    //System.out.println("Encrypting Message " + Message + " with public key " + this.n_pubKey
    // + " --> " + Message + "^" + this.e_pubKey + " mod " + this.n_pubKey );
    return ((Message).pow(this.e_pubKey)).mod(BigInteger.valueOf(this.n_pubKey));
  }

  /* Function to encrypt a string with the class instances public key (n,e)
   * Returns a byte[] array of the encrypted message character by character
   */
  public BigInteger[] encryptString(String message){
    char[] messageArr = message.toCharArray();
    BigInteger[] result = new BigInteger[messageArr.length];
    for(int x = 0; x < messageArr.length; x++){
      result[x] = encrypt(BigInteger.valueOf((int)messageArr[x]));
    }
    return result;
  }

}
