package Shnorr_electronic_coin;

import EllCurve.Pair;

import java.math.BigInteger;

/**
 * Created by Lexx on 10.12.2015.
 */
public class Coin {
    public BigInteger ZERO = BigInteger.ZERO;
    public BigInteger ONE = BigInteger.ONE;
    private BigInteger TWO = new BigInteger(String.valueOf(2));
    private BigInteger THREE = new BigInteger(String.valueOf(3));
    public BigInteger std1 = new BigInteger(String.valueOf(999999999));
    public BigInteger std2 = new BigInteger(String.valueOf(888888888));
    // It's a P192 Elliptic curve's data from NIST specification

    private BigInteger p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
    private BigInteger n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");
    private BigInteger x0 = new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16);
    private BigInteger y0 = new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811",16);
    private Pair G0 = new Pair(x0, y0);
    private BigInteger k; // it's a secret
    private Pair P;

    public Coin(BigInteger k) {
        this.k = k;
        BigInteger a = (((y0.multiply(y0)).subtract(x0.multiply(x0.multiply(x0)))).
                multiply(x0.modInverse(p))).mod(p);
        this.P = mult(k, G0, a, p);
    }

    public Pair add (Pair A, Pair B, BigInteger a, BigInteger p){
        BigInteger x1 = A.f0();
        BigInteger y1 = A.f1();
        BigInteger x2 = B.f0();
        BigInteger y2 = B.f1();
        BigInteger x3,y3;
        BigInteger lam;
        if (A.equals(new Pair(std1, std2))){
            return B;
        }
        if (B.equals(new Pair(std1, std2)))
            return A;
        if (A.equals(B)){
            if (y1.equals(ZERO))
                return new Pair(std1, std2);
            lam = (x1.multiply(x1).multiply(THREE).add(a)).
                    multiply(y1.multiply(TWO).modInverse(p)).mod(p);
            x3 = (lam.multiply(lam).subtract(x1.multiply(TWO))).mod(p);
            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
            return new Pair(x3, y3);
        } else {
            if (x1.equals(x2))
                return new Pair(std1, std2);
            lam = ((y2.subtract(y1)).
                    multiply((x2.subtract(x1).modInverse(p)))).mod(p);
            x3 = (lam.multiply(lam).subtract(x2).subtract(x1)).mod(p);
            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
            return new Pair(x3, y3);
        }
    }

    public Pair mult (BigInteger k, Pair X, BigInteger a, BigInteger p){
        Pair s = X;
        BigInteger i = ZERO;
        for (i = ZERO; i.compareTo(k) < 0; i = i.add(ONE)) {
            s = add(s, X, a, p);
            if (s.equals(new Pair(std1, std2))){
                System.out.println("ee =" + k + "\n i = " + i);
                return s;
            }
        }
        return s;
    }

}
