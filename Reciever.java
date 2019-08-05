import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Reciever{
  //reciever class
  //set up public and private key and value e
  private int p, q, d;
  public int n, e;

  public Reciever(int p , int q, int e){
    //p and q are large prime numbers
    this.p = p;
    this.q = q;
    // e must be relatively prime to (p-1) and (q-1) and (p-1)(q-1)
    // 1 < e < (p-1)(q-1)
    this.e = e;
    // n pubic key = p * q
    this.n = p * q;
    //private key d -> e * d == 1 mod ((p-1)(q-1))
    this.d = (CongruenceModulo(1, ((this.p-1)*(this.q-1)), this.e))/this.e;
    System.out.println("Initialised Reciever with public key (n="+n+",e="+e+")");
    System.out.println("Reciever Private key = " + this.d);
  }

  public int getN(){
    return this.n;
  }
  public int getE(){
    return this.e;
  }

  public int CongruenceModulo(int b , int n, int e){
    // returns a in a == b mod n
    int k = 1;
    int a = (k * n) + b;
    while((a % e != 0)){
      //System.out.println("trying a = " + a + "a mod " + e + " = " + (a % e) + " a mod n " + a%n + " b mod n " + b%n);
      k++;
      a = (k * n) + b;
    //  if(k == 10){return -1;} //threshold for k reached , quit

    }
    return a;

    //congruence a == b mod n
    // a = k*n + b
    // in 4 mod 10
      // a = k * 10 + 4 ... taking k = 2
      // a = 24 ... 24 == 4 mod 10
  }

  public BigInteger decrypt(BigInteger Ciphertext){
    //Decryption -> M == C^d mod n
    System.out.println("Decrypting ciphertext: "+ Ciphertext + " --> " +
    Ciphertext + "^" + this.d + " mod " + this.n);
    return (Ciphertext.pow(this.d)).mod(BigInteger.valueOf(this.n));
  }

  public String decryptString(BigInteger[] Ciphertext){
    char[] message = new char[Ciphertext.length];
    for (int x = 0; x < Ciphertext.length; x++) {
      message[x] = (char)((decrypt(Ciphertext[x])).intValue());
    }
    return new String(message);
  }
}
