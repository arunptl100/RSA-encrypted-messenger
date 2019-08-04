import java.math.BigInteger;

public class RSA{

  public static int CongruenceModulo(int b , int n){
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

  public static void main(String[] args) {
    System.out.println("====RSA encryption implemtation====");
    //public Reciever(int p , int q, int e)
    Reciever rcvr = new Reciever(17,11,7);
    Sender sender = new Sender(rcvr.getN(), rcvr.getE());
    System.out.println("encrypting message '115'");
    BigInteger cipherT = sender.encrypt(new BigInteger("115"));
    System.out.println("decrypting Ciphertext " + cipherT);
    BigInteger decryptMess = rcvr.decrypt(cipherT);
    System.out.println("Result of Decryption " + decryptMess);

  }
}
