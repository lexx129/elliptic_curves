package Shnorr_electronic_coin;

import EllCurve.Pair;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lexx on 10.12.2015.
 */
public class Main {
//    byte array = new byte[]{(byte)188da80e, }

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

    public byte[] hexToBin(byte[] hex) {
        byte[] res = new byte[160];
        //array of bits is of SHA1 hash string bit length
//        bits = new byte[160];
//        int counter = 0;
        for (int i = 0; i < 20; i++) {
            int sourceByte = 0xFF & (int) hex[i];//convert byte to unsigned int
            int mask = 0x80;
            for (int j = 0; j < 8; j++) {
                int maskResult = sourceByte & mask;  // Extract the single bit
                if (maskResult > 0) {
                    res[8 * i + j] = 1;
                } else {
                    res[8 * i + j] = 0;  // Unnecessary since array is initiated to zero but good documentation
                }
                mask = mask >> 1;
            }
        }
//        System.out.print("Hash string in bits:   ");
//        for (int i = 0; i < 160; i++) {
//            System.out.print(bits[i]);
//        }
        return res;
    }

    public static void main(String[] args) {
        String message = "Hello_world!";
//        String message = "1994";
        byte[] hash = toSHA1(message.getBytes());
        System.out.println("Coin generating started...");
        Coin1 coin = new Coin1(hash, new BigInteger("17"));
//        coin.test();
//        System.exit(0);
        coin.generateBankOpen();
        System.out.println("***Bank's R` was sent to client***");
        coin.generateClientOpen();
        System.out.println("***Client's m` was sent to bank***");
        coin.makeSign();
        System.out.println("***Bank sent a signature to client***");
        if (!coin.checkSign()) {
            System.out.println("Bank's signature is not validated");
        } else {
            System.out.println("Bank's signature is validated, unmasking message..");
            coin.unMaskSignature();

            BigInteger m = coin.getMessage();
            Pair R = coin.getR();
            BigInteger signature = coin.getS();

            System.out.println("Electronic coin: ");
            System.out.println("(" + m + "; (" + R.f0() + ", " + R.f1() + "); " + signature + ")");
            if (coin.checkCoin(m, R, signature))
                System.out.println("Coin is validated by shop");
            else System.out.println("Coin is not validated by shop");
        }
    }
}
