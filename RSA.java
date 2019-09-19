import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RSA{

  /* Function that returns the gcd of 2 ints
   * source: https://www.java67.com/2012/08/java-program-to-find-gcd-of-two-numbers.html
   */
  private static int findGCD(int number1, int number2) {
    //base case
    if(number2 == 0){
      return number1;
    }
    return findGCD(number2, number1%number2);
  }

  /* Function to determine if 2 numbers are relatively prime
   * returns true if the gcd of the 2 numbers is 1 , false otherwise
   */
  public static boolean relativelyPrime(int a , int b){
    return (findGCD(a, b) == 1) ? true : false;
  }

  /* Function to determine if the param n is prime
   * returns true if prime , false otherwise
   * method adapted from src:
   *  https://www.javatpoint.com/prime-number-program-in-java
   */
  public static boolean checkPrime(int n){
    int i,m=0,flag=0;
    m=n/2;
    if(n==0||n==1){
      return false;
    }else{
      for(i=2;i<=m;i++){
        if(n%i==0){
          return false;
        }
      }
      if(flag==0)  { return true; }
    }
    return false;
  }


  /*
  * p and q are large prime numbers
  * e must be relatively prime to (p-1) and (q-1) and (p-1)(q-1)
  * 1 < e < (p-1)(q-1)
  */
  public static void main(String[] args) {
    System.out.println("====================RSA encrypted messenger====================");
    Scanner sin = new Scanner(System.in);
    int p = 1;
    int q = 1;
    int e = 1;
    // p = 97;
    // q = 101;
    // e = 251;

    // Reciever rc1 = new Reciever(97, 101, 251);
    // Reciever rc2 = new Reciever(11, 13, 23);
    // Sender s1 = new Sender(rc2.getN(), rc2.getE());
    // BigInteger[] testC = s1.encryptString("Test hello");
    // String testM = rc2.decryptString(testC);
    // System.out.println(testM);
    // System.exit(0);

    while(!(checkPrime(p)) && !(checkPrime(q))){
      System.out.println("Enter public key (p), must be large and prime");
      p = Integer.parseInt(sin.nextLine());
      System.out.println("Enter public key (q), must be large and prime");
      q = Integer.parseInt(sin.nextLine());
    }

    while( !((relativelyPrime(e,(p-1))) && (relativelyPrime(e, (q-1)))
    && (e > 1) && (e < (p-1)*(q-1)) && (relativelyPrime(e,((p-1)*(q-1))))) ){
      System.out.println("Enter public key (e)"+
      "\ne must be relatively prime to ("+(p-1)+") and ("+(q-1)+") and "+((p-1)*(q-1))+
      " . 1 < e < "+((p-1)*(q-1))+")");
      e = Integer.parseInt(sin.nextLine());
    }

    client cl = new client("localhost",25565, p, q, e);


    // Reciever rc = new Reciever(p,q,e);
    // Sender signDigsig = new Sender(rc.getN(),rc.getPrivateKey());
    // BigInteger[] encryptedDigSig = signDigsig.encryptString("test");
    // Reciever rcDigsig = new Reciever(rc.getN(), rc.getE());
    // String recievedDigSig = rcDigsig.decryptString(encryptedDigSig);
    // System.out.println("Decrypted digital signature into : " + recievedDigSig);

    //
    //
    // Sender sender = new Sender(rcvr.getN(), rcvr.getE());
    //
    // String message = "hello world";
    // BigInteger[] cipherText = sender.encryptString(message);
    // System.out.println("Encrypted message " + message + " -> ");
    // for (int x = 0 ;x < cipherText.length ;x++ ) {
    //   System.out.print(" " + cipherText[x]);
    // }
    // System.out.println();
    // String decrypt = rcvr.decryptString(cipherText);
    // System.out.println("Result of decryption --> " + decrypt);


    // String ciphertext = sender.encryptString(message);
    // System.out.println("Encrypted message " + message + " -> " + ciphertext);
    // String decryptMessage = rcvr.decryptString(ciphertext);
    // System.out.println("Decrypted ciphertext " + ciphertext + " -> " + decryptMessage);
    // BigInteger cipherT = sender.encrypt(BigInteger.valueOf(message));
    // BigInteger decryptMess = rcvr.decrypt(cipherT);
    //System.out.println("Result of Decryption " + decryptMess);
    System.out.println("===================================================================");
    sin.close();
    System.exit(0);
  }
}
