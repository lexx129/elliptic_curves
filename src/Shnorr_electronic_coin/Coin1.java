package Shnorr_electronic_coin;

import EllCurve.Pair;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Lexx on 10.12.2015.
 */
public class Coin1 {
    private BigInteger mONE = new BigInteger(String.valueOf(-1));
    public BigInteger ZERO = BigInteger.ZERO;
    public BigInteger ONE = BigInteger.ONE;
    private BigInteger TWO = new BigInteger(String.valueOf(2));
    private BigInteger THREE = new BigInteger(String.valueOf(3));
    public BigInteger std1 = new BigInteger(String.valueOf(999999999));
    public BigInteger std2 = new BigInteger(String.valueOf(888888888));
    private SecureRandom rand;

    // It's a P192 Elliptic curve's data from NIST specification

    private BigInteger p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
    //    private BigInteger p = new BigInteger("41");
//    private BigInteger r = new BigInteger("35");
    private BigInteger r = new BigInteger("6277101735386680763835789423176059013767194773182842284081");
    //    private BigInteger x0 = new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16);
//    private BigInteger y0 = new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16);
    private BigInteger x0 = new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16);
    private BigInteger y0 = new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16);
//    private BigInteger x0 = new BigInteger("1");
//    private BigInteger y0 = new BigInteger("1");
    private Pair G0 = new Pair(x0, y0);
    private BigInteger l;   // it's a secret value
    private BigInteger kBank; // it's a bank's open value
    private BigInteger alpha; // it's a client's open values
    private BigInteger beta;
    private Pair P;         //it's a bank's EllCurve point: P = lQ
    private BigInteger a;

    private Pair bankR;     // it's a bank's EllCurve point for communication
    private Pair R;         // it's a client's EllCurve point for communication
    private BigInteger message;
    private BigInteger anotherM;
    private BigInteger s;
    private BigInteger sBank;

    public Coin1() {
    }

    public Coin1(byte[] message, BigInteger l) {
        this.message = new BigInteger(message);
        this.l = l;
//        a = (((y0.multiply(y0)).subtract(x0.multiply(x0.multiply(x0)))).
//                multiply(gcdExtended(x0, p)[1])).mod(p);
        a = new BigInteger("-3");
        System.out.println("a = " + a);
//        printPoint(mult2(G0, l), "**testing for P ");
        this.P = mult2(G0, l);
        System.out.println("P = (" + P.f0() + ", " + P.f1() + ")");
        this.rand = new SecureRandom();
    }

    public void generateBankOpen() {
        System.out.println("Generating bank open key...");
        kBank = randomNumber(false, r.bitLength()).mod(r);
//        kBank = new BigInteger("6");
        System.out.println("k` = " + kBank);

//        printPoint(mult2(G0, kBank), "***test mult for bankR");

        bankR = mult2(G0, kBank);
        printPoint(bankR, "R` = ");
        while (openFunction(bankR).equals(ZERO)) {
            System.out.println("generated k` is not suitable. Gonna try again.");
            kBank = randomNumber(false, r.bitLength()).mod(r);
            bankR = mult2(G0, kBank);
            printPoint(bankR, "recalculated R` = ");
        }
    }

    public void generateClientOpen() {
        System.out.println("Generating client open key...");
        alpha = randomNumber(false, r.bitLength()).mod(r);
//        alpha = new BigInteger("8");
        System.out.println("generated alpha = " + alpha);
        R = mult2(bankR, alpha);
        while (openFunction(R).equals(ZERO)) {
            alpha = randomNumber(false, r.bitLength()).mod(r);
            R = mult2(bankR, alpha);
        }
        System.out.println("R = (" + R.f0() + ", " + R.f1() + ")");
        beta = openFunction(R).multiply(openFunction(bankR).modInverse(r)).mod(r);
        System.out.println("generated beta = " + beta);
        anotherM = (alpha.modInverse(r).multiply(beta).multiply(message)).mod(r);
        System.out.println("M` = " + anotherM);
    }

    public void makeSign() {
        sBank = kBank.add(l.multiply(anotherM).multiply(openFunction(bankR))).mod(r);
        System.out.println("s` = " + sBank);
    }

    public boolean checkSign() {

        Pair first = mult2(G0, sBank);
        Pair second = mult2(P, anotherM.multiply(openFunction(bankR)));
        Pair third = add(bankR, second, a, p);
        printPoint(first, "first part = ");
        printPoint(third, "second part = ");
        return first.equals(third);
    }

    public Pair getR() {
        return R;
    }

    public BigInteger getMessage() {
        return message;
    }

    public BigInteger getS() {
        return s;
    }

    public void unMaskSignature() {
        s = alpha.multiply(sBank).mod(r);
    }

    public boolean checkCoin(BigInteger m, Pair R, BigInteger s) {
        if (m.equals(ZERO))
            return false;
        if (openFunction(R).equals(ZERO))
            return false;
        Pair part1 = mult2(G0, s);
        Pair part2 = mult2(P, m.multiply(openFunction(R)));
        Pair part3 = add(R, part2, a, p);
        System.out.println("first part = " + part1.f0() + ", " + part1.f1());
        System.out.println("second part = " + part3.f0() + ", " + part3.f1());
        return part1.equals(part3);
    }

    public void test() {
        Pair x = new Pair(new BigInteger("1"), new BigInteger("2"));
        Pair y = new Pair(new BigInteger("1"), new BigInteger("3"));
        BigInteger k = new BigInteger("2");
//        BigInteger k = randomNumber(false, p.bitLength()).mod(p);
        System.out.println("k = " + k);
        printPoint(mult(r, G0, a, p), "old mult: ");
        printPoint(mult2(G0, r), "true mult: ");
//        printPoint(mult3(G0, r), "another mult: ");
        printPoint(add(x, y, a, p), "add: ");
    }

    private BigInteger openFunction(Pair a) {
        if (a.equals(new Pair(std1, std2)))
            return ZERO;
        return a.f0().add(a.f1()).mod(r);
    }

    private void printPoint(Pair a, String message) {
        System.out.println(message + "(" + a.f0() + ", " + a.f1() + ")");
    }

    public Pair add(Pair A, Pair B, BigInteger a, BigInteger p) {
        BigInteger x1 = A.f0();
        BigInteger y1 = A.f1();
        BigInteger x2 = B.f0();
        BigInteger y2 = B.f1();
        BigInteger x3, y3;
        BigInteger lam;
        if (A.equals(new Pair(std1, std2))) {
            return B;
        }
        if (B.equals(new Pair(std1, std2)))
            return A;
        if (A.equals(B)) {
            if (y1.equals(ZERO))
                return new Pair(std1, std2);
            lam = (x1.multiply(x1.multiply(THREE)).add(a))
                    .multiply((y1.multiply(TWO)).modInverse(p)).mod(p);
//            lam = ((x1.multiply(x1).multiply(THREE).add(a))
//                    .multiply(y1.multiply(TWO).modInverse(p))).mod(p);
            x3 = (lam.multiply(lam).subtract(x1.multiply(TWO))).mod(p);
            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
            return new Pair(x3, y3);
        } else {
            if (x1.equals(x2))
                return new Pair(std1, std2);
            lam = (y2.subtract(y1).multiply((x2.subtract(x1)).modInverse(p))).mod(p);
//            lam = ((y2.subtract(y1)).
//                    multiply(x2.subtract(x1)).modInverse(p)).mod(p);
            x3 = (lam.multiply(lam).subtract(x2).subtract(x1)).mod(p);
            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
            return new Pair(x3, y3);
        }
    }

//    public Pair add(Pair A, Pair B, BigInteger a, BigInteger p) {
//        BigInteger x1 = A.f0();
//        BigInteger y1 = A.f1();
//        BigInteger x2 = B.f0();
//        BigInteger y2 = B.f1();
//        BigInteger x3, y3;
//        BigInteger lam;
//        if (A.equals(new Pair(std1, std2))) {
//            return B;
//        }
//        if (B.equals(new Pair(std1, std2)))
//            return A;
//        if (A.equals(B)) {
//            if (y1.equals(ZERO))
//                return new Pair(std1, std2);
//            lam = (x1.multiply(x1).multiply(THREE).add(a)).
//                    multiply(gcdExtended(y1.multiply(TWO), p)[1]).mod(p);
//            x3 = (lam.multiply(lam).subtract(x1.multiply(TWO))).mod(p);
//            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
//            return new Pair(x3, y3);
//        } else {
//            if (x1.equals(x2))
//                return new Pair(std1, std2);
//            lam = ((y2.subtract(y1)).
//                    multiply(gcdExtended(x2.subtract(x1), p)[1])).mod(p);
//            x3 = (lam.multiply(lam).subtract(x2).subtract(x1)).mod(p);
//            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
//            return new Pair(x3, y3);
//        }
//    }

    public Pair mult(BigInteger k, Pair X, BigInteger a, BigInteger p) {
        Pair s = X;
        BigInteger i;
        for (i = ZERO; i.compareTo(k) < 0; i = i.add(ONE)) {
            s = add(s, X, a, p);
            if (s.equals(new Pair(std1, std2))) {
//                System.out.println("ee =" + k + "\n i = " + i);
                return s;
            }
        }
        return s;
    }


    public Pair mult2(Pair x, BigInteger n) {
        Pair res = new Pair(std1, std2);
        while (n.compareTo(ZERO) > 0) {
            if (n.mod(TWO).equals(ONE)) {
                res = add(res, x, a, p);
                n = n.subtract(ONE);
            } else {
                x = add(x, x, a, p);
                n = n.divide(TWO);
            }
        }
        return res;
    }

    public BigInteger randomNumber(boolean prime, int size) {
        if (prime)
            return BigInteger.probablePrime(size, rand);
        BigInteger number;
        byte bNumber[] = new byte[(int) Math.ceil(size / 8.0)];
        do {
            rand.nextBytes(bNumber);
            number = new BigInteger(bNumber);
        } while (number.compareTo(BigInteger.ZERO) <= 0);
        return number;
    }

    public static byte[] toSHA1(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
//        return byteArrayToHexString(md.digest(convertme));
        return md.digest(convertme);
    }


}
