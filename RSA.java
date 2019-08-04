import java.math.BigInteger;

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

  /*
  * p and q are large prime numbers
  * e must be relatively prime to (p-1) and (q-1) and (p-1)(q-1)
  * 1 < e < (p-1)(q-1)
  */
  public static void main(String[] args) {
    System.out.println("====RSA encryption implemtation====");
    int p , q , e;
    p = 97;
    q = 101;
    e = 251;
    int message = 450;
    if((relativelyPrime(e,(p-1))) && (relativelyPrime(e, (q-1))) && (relativelyPrime(e,((p-1)*(q-1)))) ){
      System.out.println("e meets the requirements");
    }else{
      System.out.println("e does NOT the requirements");
    }

    //public Reciever(int p , int q, int e)
    Reciever rcvr = new Reciever(p,q,e);
    Sender sender = new Sender(rcvr.getN(), rcvr.getE());
    BigInteger cipherT = sender.encrypt(BigInteger.valueOf(message));
    BigInteger decryptMess = rcvr.decrypt(cipherT);
    System.out.println("Result of Decryption " + decryptMess);

  }
}
