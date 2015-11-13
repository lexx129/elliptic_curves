import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.function.BiFunction;

/**
 * Created by admin on 13.11.2015.
 */
public class EllCurves {

    private BigInteger ONE = BigInteger.ONE;
    private BigInteger FOUR = new BigInteger(String.valueOf(4));
    private SecureRandom rand;
    int n;

    public EllCurves (int n){
        this.n = n;
        this.rand = new SecureRandom();
        BigInteger p = randomNumber(true, n);
        while (!p.mod(FOUR).equals(ONE))
            p = randomNumber(true, n);

    }

    private BigInteger randomNumber(boolean prime, int size) {
        if (prime)
            return BigInteger.probablePrime(size, rand);
        BigInteger number = null;
        byte bNumber[] = new byte[(int) Math.ceil(size / 8.0)];
        do {
            rand.nextBytes(bNumber);
            number = new BigInteger(bNumber);
        } while (number.compareTo(BigInteger.ZERO) <= 0);
        return number;
    }
    private BigInteger quos (BigInteger a, BigInteger n){
        if (n.compareTo(BigInteger.ZERO) == -1  ){
            System.err.println("????????????? ??????");
        }
        return (a.subtract(a.mod(a))).divide(n);
    }
    private BigInteger grem (BigInteger[] w, BigInteger[] z){
        BigInteger n1 = (z[0].multiply(z[0]).add(z[1].multiply(z[1])));
        if (n1.equals(BigInteger.ZERO)) {
            System.err.println("??????? ?? ????");
            System.exit(-1);
        }
        BigInteger u0 = quos(w[0].multiply(z[0]))

    }


    private BigInteger[] decompositePrime(BigInteger prime) {
        BigInteger sq = Math.
    }

}
