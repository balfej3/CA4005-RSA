My submission for CA4005 assignment 2, implemented in Java

CA4005 Cryptography and Security Protocols
=========================================

Assignment 2
-------------------

**Digital Signature Using RSA**

  
The aim of this assignment is to implement a digital signature using RSA. Before the digital signature can be implemented, you will need to set up an appropriate public/private RSA key pair. This should be done as follows:

1.  Generate two distinct 512\-bit _probable primes_ _p_ and _q_
2.  Calculate the product of these two primes _N = pq_
3.  Calculate the Euler totient function phi(_N_)
4.  You will be using an encryption exponent _e_ = 65537, so you will need to ensure that this is relatively prime to phi(_N_). If it is not, go back to step 1 and generate new values for _p_ and _q_
5.  Compute the value for the decryption exponent _d_, which is the multiplicative inverse of _e_ (mod phi(_N_)). This should use your own implementation of the extended Euclidean GCD algorithm to calculate the inverse rather than using a library method for this purpose.

You should then write code to implement a decryption method which calculates _cd_ (mod _N_). You should use your own implementation of the Chinese Remainder Theorem to calculate this more efficiently; this can also make use of your multiplicative inverse implementation.

Once your implementation is complete, you should create a zip file containing all your code and digitally sign a digest of this file as follows:

1.  Generate a 256\-bit digest of the zip file using SHA\-256.
2.  Apply your decryption method to this digest. Note that for the purpose of this assignment no padding should be added to the digest.


The implementation language must be Java. You can make use of the BigInteger class (java.math.BigInteger), the security libraries (java.security.\*) and the crypto libraries (javax.crypto.\*). You must not make use of the multiplicative inverse or GCD methods provided by the BigInteger class; you will need to implement these yourself. You can however make use of the crypto libraries to perform the SHA\-256 hashing.
