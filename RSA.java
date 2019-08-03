import java.math.BigInteger;

public class RSA{

  public static BigInteger CongruenceModulo(double b , double n){
    // returns a in a == b mod n
    double k = 1;
    double a = (k * n) + b;
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
    // double x = CongruenceModulo(4,10);
    // System.out.println(x + " == " + " 4 mod 10" );
    BigInteger n, p, q, e, d, Message, Ciphertext;
    Message = new BigInteger("88");
    p = new BigInteger("17");
    q = new BigInteger("11");
    n = p.multiply(q); // n pubic key = p * q

    System.out.println("Using public key " + n);
    // e must be relatively prime to (p-1) and (q-1) and (p-1)(q-1)
    // 1 < e < (p-1)(q-1)
    e = new BigInteger("7");
    //private key d -> e * d == 1 mod ((p-1)(q-1))
    d = (CongruenceModulo(1, ((p.subtract(new BigInteger("1"))).multiply(q.subtract(new BigInteger("1"))))))/e;

    System.out.println("private key " + d);
    System.out.println("Encrypting Message " + Message + " with public key " + n);
    //Ciphertext == M^e mod n
    Ciphertext = (new BigInteger(Message)).pow((int)e).mod(n);

    //Ciphertext = (Math.pow(Message, e))%n;
    System.out.println("Ciphertext " + Ciphertext);
    System.out.println("Decrypting message "+ Ciphertext + "^" + d + "mod" + n);
    Message = Ciphertext.pow((int)d) % n;
    System.out.println("Decrypted message "+ Message);
    //Decryption -> M == C^d mod n

  }
}
