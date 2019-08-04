import java.math.BigInteger;

public class Sender{
  //encrypts a message with a Reciever class instance public key and value e
  public int n_pubKey;
  public int e_pubKey;

  public Sender(int n, int e){
    this.n_pubKey = n;
    this.e_pubKey = e;
  }
  public void setPubKey(int n, int e){
    this.n_pubKey = n;
    this.e_pubKey = e;
  }

  public BigInteger encrypt(BigInteger Message){
    //Ciphertext == M^e mod n
    //Ciphertext can be large so use BigInteger
    System.out.println("Encrypting Message " + Message + " with public key " + this.n_pubKey
    + " --> " + Message + "^" + this.e_pubKey + " mod " + this.n_pubKey );
    return ((Message).pow(this.e_pubKey)).mod(BigInteger.valueOf(this.n_pubKey));
  }

}
