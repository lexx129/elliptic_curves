import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by admin on 13.11.2015.
 */
public class EllCurves {

    private BigInteger ZERO = BigInteger.ZERO;
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger TWO = new BigInteger(String.valueOf(2));
    private BigInteger FOUR = new BigInteger(String.valueOf(4));
    private BigInteger mONE = new BigInteger(String.valueOf(-1));
    private SecureRandom rand;
    int n;

    public EllCurves(int n) {
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

    private BigInteger mods(BigInteger a, BigInteger n){
        if (n.compareTo(ZERO) <= 0){
            System.err.println("Отрицательный модуль");
            System.exit(-1);
        }
        a = a.mod(n);
        if (a.multiply(TWO).compareTo(n) == 1)
            a = a.subtract(n);
        return a;
    }

    private BigInteger powMods(BigInteger a, BigInteger r, BigInteger n){
        BigInteger res = ONE;
        while (r.compareTo(ZERO) == 1){
            if (r.mod(TWO).compareTo(ONE) == 0){
                r = r.subtract(ONE);
                res = mods(res.multiply(a), n);
            }
            r = r.divide(TWO);
            a = mods(a.multiply(a), n);
        }
        return res;
    }

    private BigInteger quos(BigInteger a, BigInteger n) {
        if (n.compareTo(ZERO) <= 0) {
            System.err.println("Отрицательный модуль");
        }
        return (a.subtract(mods(a, n))).divide(n);
    }

    private Pair grem(Pair w, Pair z) {
        BigInteger w0 = w.f0();
        BigInteger w1 = w.f1();
        BigInteger z0 = z.f0();
        BigInteger z1 = z.f1();
        BigInteger n1 = (z0.multiply(z0).add(z1.multiply(z1)));
        if (n1.equals(ZERO)) {
            System.err.println("Деление на 0");
            System.exit(-1);
        }
        BigInteger u0 = quos(w0.multiply(z0).add(w1.multiply(z1)), n1);
        BigInteger u1 = quos(w1.multiply(z0).subtract(w0.multiply(z1)), n1);
        return new Pair(w0.subtract(z0.multiply(u0)).add((z1).multiply(u1)),
                w1.subtract((z0).multiply(u1)).subtract((z1).multiply(u0)));
    }

    private Pair ggcd(Pair w, Pair z) {
        while (!z.equals(new Pair(ZERO, ZERO))) {
            Pair temp = w;
            w = z;
            z = grem(temp, z);
        }
        return w;
    }

    private BigInteger root4(BigInteger p) {
        BigInteger a, b;
        if (p.compareTo(ONE) == -1) {
            System.err.println("too small");
            System.exit(-1);
        }
        if (!p.mod(FOUR).equals(ONE)) {
            System.err.println("Не конгруэнтно");
            System.exit(-1);
        }
        BigInteger k = p.divide(FOUR);
        BigInteger j = TWO;
        while (true) {
            a = powMods(j, k, p);
            b = mods(a.multiply(a), p);
            if (b.equals(mONE))
                return a;
            if (!b.equals(ONE)) {
                System.err.println("Не простое");
                System.exit(-1);
            }
            j = j.add(ONE);
        }
    }

    private Pair sq2p(BigInteger p) {
        BigInteger a = root4(p);
        return ggcd(new Pair(p, ZERO), new Pair(a, ONE));
    }

    public static void main(String[] args) {
        EllCurves m = new EllCurves(160);
        BigInteger p = new BigInteger(String.valueOf(30));
        Pair res = m.sq2p(p);
        System.out.println(res.f0() + " " + res.f1());
    }
}
