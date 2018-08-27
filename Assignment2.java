import java.math.*;
import java.io.*;
import javax.crypto.*;
import java.util.*;
import java.security.*;


public class Assignment2{


    public static BigInteger gcd(BigInteger x, BigInteger y){

        if(y.compareTo(BigInteger.ZERO) == 0){
            return x;
        }else return gcd(y, x.mod(y));
    }


    public static BigInteger[] extendedGCD(BigInteger x, BigInteger y){

        //base case
        if(y.equals(BigInteger.ZERO)){
            return new BigInteger[]{x, BigInteger.ONE, BigInteger.ZERO};
        }

        BigInteger [] ans = extendedGCD(y, x.mod(y));
        BigInteger a = ans[0];
        BigInteger b = ans[2];
        BigInteger c = ans[1].subtract((x.divide(y)).multiply(ans[2]));

        return new BigInteger[]{a, b, c};
    }

    /* In addition to lecture notes on CRT, I used the following 2 videos
    *  https://www.youtube.com/watch?v=6mP0ViInd0Q
    *  https://www.coursera.org/lecture/number-theory-cryptography/chinese-remainder-theorem-Cunn2
    */
    public static BigInteger decryptCRT(BigInteger p, BigInteger q, BigInteger c, BigInteger d){

        BigInteger a_1 = c.modPow(d.mod(p.subtract(BigInteger.ONE)),p);
        BigInteger a_2 = c.modPow(d.mod(q.subtract(BigInteger.ONE)),q);

        BigInteger x = extendedGCD(q, p)[1];

        BigInteger ans = a_2.add(q.multiply(x.multiply(a_1.subtract(a_2)).mod(p)));

        return ans;
    }

    public static void main(String [] args){

        // generate p and q, using BigInteger.probablePrime as described here:
        // https://www.tutorialspoint.com/java/math/biginteger_probableprime.htm
        BigInteger p = BigInteger.probablePrime(512, new Random());
        BigInteger q = BigInteger.probablePrime(512, new Random());

        //check p is greater of 2, setup for CRT
        if(p.compareTo(q) < 0){
            BigInteger tmp = p;
            p = q;
            q = tmp;
        }

        BigInteger e = new BigInteger("65537");

       //create N as the product of p and q
       BigInteger N = q.multiply(p);

       //euler totient function phi(N)
       BigInteger phiOfN = BigInteger.ZERO;
       if(p.compareTo(q) == 0){
           phiOfN = BigInteger.ZERO;
       }else{
           phiOfN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
       }

       //Get the GCD of e = 65537 and phiOfN
       BigInteger gcdValue = BigInteger.ZERO;
       if(phiOfN.compareTo(e) < 0){
           gcdValue = gcd(e, phiOfN);
       }else{
           gcdValue = gcd(phiOfN, e);
       }

       //check that e = 65537 is relatively prime to phiOfN, recalculate p and q if not
       while(gcdValue.compareTo(BigInteger.ONE) != 0){
           p = BigInteger.probablePrime(512, new Random());
           q = BigInteger.probablePrime(512, new Random());
           N = q.multiply(p);

           if(p.compareTo(q) == 0){
               phiOfN = BigInteger.ZERO;
           }else{
               phiOfN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
           }

           if(phiOfN.compareTo(e) < 0){
               gcdValue = gcd(e, phiOfN);
           }else{
               gcdValue = gcd(phiOfN, e);
           }

       }

       //calculate d, multiplicative inverse of  e(mod phi(N)) using custom implementation of Euclidean GCD algorithm
        BigInteger d = BigInteger.ZERO;

        BigInteger x = BigInteger.ZERO;
        BigInteger y = phiOfN;

        BigInteger xx = BigInteger.ONE;
        BigInteger yy = e;

        BigInteger temp = BigInteger.ZERO;

        while(y.compareTo(BigInteger.ONE)!= 0){
            BigInteger z = y.divide(yy);

            temp = x;
            x = xx;
            xx = temp.subtract(z.multiply(xx));

            temp = y;
            y = yy;
            yy = temp.subtract(z.multiply(yy));
        }

        if(x.compareTo(BigInteger.ZERO) < 0){
            x = x.add(phiOfN);
            d = x;
        }else{
            d = x;
        }



        BigInteger multiInverse = extendedGCD(phiOfN,e)[1].mod(q);


        //read in the zip file, get the hash and sign it
        BigInteger out = BigInteger.ZERO;
        try {
            FileInputStream input;
            File f = new File("input.zip");
            //byte array equal to size of the file
            byte[] bytes_ = new byte[(int) f.length()];

            input = new FileInputStream("input.zip");
            int check = input.read(bytes_);
            if (check == 1) {
                input.close();
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes_);
            byte[] hashedOut = md.digest();
            out = new BigInteger(1, hashedOut);

        }catch (IOException | NoSuchAlgorithmException exc){
            System.out.println("Error hashing the file, make sure input.zip is present");
        }



        BigInteger signedResult = decryptCRT(p, q, out, d);

        //write the values needed for submission to a file
        try {
            PrintWriter writer = new PrintWriter("results.txt", "UTF-8");

            String nHex = N.toString(16);
            writer.println("Value of N: " + nHex);
            writer.println();

            String dHex = d.toString(16);
            writer.println("Value of d: " + dHex);
            writer.println();

            String digestHex = out.toString(16);
            writer.println("Digest is: " + digestHex);
            writer.println();

            String signedHex = signedResult.toString(16);
            writer.println("Signed digest is: " + signedHex);
            writer.println();

            writer.close();
        }catch(FileNotFoundException | UnsupportedEncodingException e2){
            System.out.println("Error writing results to file");
        }

    }

}