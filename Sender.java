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
    System.out.println("Encrypting Message " + Message + " with public key " + this.n_pubKey);
    return (Message).pow(this.e_pubKey).mod(BigInteger.valueOf(this.n_pubKey));
  }

  public int CongruenceModulo(int b , int n){
    // returns a in a == b mod n
    int k = 1;
    int a = (k * n) + b;
    while((a % n) != (b % n)){
      k++;
      a = (k * n) + b;
      if(k == 10){return -1;} //threshold for k reached , quit
    }
    return a;

    //congruence a == b mod n
    // a = k*n + b
    // in 4 mod 10
      // a = k * 10 + 4 ... taking k = 2
      // a = 24 ... 24 == 4 mod 10
  }
}
