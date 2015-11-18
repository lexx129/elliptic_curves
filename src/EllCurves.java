import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by admin on 13.11.2015.
 */
public class EllCurves {

    private BigInteger ZERO = BigInteger.ZERO;
    private BigInteger ONE = BigInteger.ONE;
    private BigInteger TWO = new BigInteger(String.valueOf(2));
    private BigInteger THREE = new BigInteger(String.valueOf(3));
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

    public BigInteger[] gcdExtended(BigInteger a, BigInteger b){
        BigInteger x = ZERO;
        BigInteger lastX = ONE;
        BigInteger y = ONE;
        BigInteger lastY = ZERO;

        while (!b.equals(ZERO)){
            BigInteger[] quotientAndRemainder = a.divideAndRemainder(b);
            BigInteger quotient = quotientAndRemainder[0];

            BigInteger temp = a;
            a = b;
            b = quotientAndRemainder[1];

            temp = x;
            x = lastX.subtract(quotient.multiply(x));
            lastX = temp;

            temp = y;
            y = lastY.subtract(quotient.multiply(y));
            lastY = temp;
        }
        return new BigInteger[]{a, lastX, lastY};
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

    public BigInteger generateMod4(boolean prime, int size) {
        BigInteger res = randomNumber(prime, size);
        while (!res.mod(FOUR).equals(ZERO))
            res = randomNumber(prime, size);
        return res;
    }

    private BigInteger mods(BigInteger a, BigInteger n) {
        if (n.compareTo(ZERO) <= 0) {
            System.err.println("Отрицательный модуль");
            System.exit(-1);
        }
        a = a.mod(n);
        if (a.multiply(TWO).compareTo(n) == 1)
            a = a.subtract(n);
        return a;
    }

    private BigInteger powMods(BigInteger a, BigInteger r, BigInteger n) {
        BigInteger res = ONE;
        while (r.compareTo(ZERO) == 1) {
            if (r.mod(TWO).compareTo(ONE) == 0) {
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

    public Pair sq2p(BigInteger p) {
        BigInteger a = root4(p);
        return ggcd(new Pair(p, ZERO), new Pair(a, ONE));
    }

    public Pair add (Pair A, Pair B, BigInteger a, BigInteger p){
        BigInteger x1 = A.f0();
        BigInteger y1 = A.f1();
        BigInteger x2 = B.f0();
        BigInteger y2 = B.f1();
        BigInteger x3,y3;
        BigInteger lam;
        if (A.equals(new Pair(null, null))){
            return B;
        }
        if (B.equals(new Pair(null, null)))
            return A;
        if (A.equals(B)){
            if (y1.equals(ZERO))
                return new Pair(null, null);
            lam = (x1.multiply(x1).multiply(THREE).add(a)).
                    multiply(gcdExtended(y1.multiply(TWO), p)[1]).mod(p);
            x3 = (lam.multiply(lam).subtract(x1.multiply(TWO))).mod(p);
            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
            return new Pair(x3, y3);
        } else {
            if (x1.equals(x2))
                return new Pair(null, null);
            lam = ((y2.subtract(y1)).
                    multiply(gcdExtended(x2.subtract(x1), p)[1])).mod(p);
            x3 = (lam.multiply(lam).subtract(x2).subtract(x1)).mod(p);
            y3 = (lam.multiply(x1.subtract(x3)).subtract(y1)).mod(p);
            return new Pair(x3, y3);
        }
    }

    public BigInteger[] mult (BigInteger k, Pair X, BigInteger a, BigInteger p){
        Pair s = X;
        BigInteger i = ZERO;
        for (i = ZERO; i.compareTo(k) <= 0; i = i.add(ONE)) {
            s = add(s, X, a, p);
            if (s.equals(new Pair(null, null))){
                System.out.println("ee =" + k + "\n i = " + i);
                return new BigInteger[]{s.f0(), s.f1(), i};
            }
        }
        return new BigInteger[]{s.f0(), s.f1(), i};
    }
    
    public Pair check(BigInteger a, BigInteger p, BigInteger d, BigInteger e) {
        BigInteger e1, e2;
        BigInteger x = p.subtract(a).modPow(p.subtract(ONE).divide(TWO), p);
        if (x.equals(ONE)) {
            e1 = p.add(ONE).add(TWO.multiply(d));
            e2 = p.add(ONE).subtract(TWO.multiply(d));
            if (e1.mod(FOUR).equals(ZERO))
                return new Pair(e1, e1.divide(FOUR));
            if (e2.mod(FOUR).equals(ZERO))
                return new Pair(e2, e2.divide(FOUR));
        }
        if (x.equals(p.subtract(ONE))) {
            e1 = p.add(ONE).add(TWO.multiply(e));
            e2 = p.add(ONE).subtract(TWO.multiply(e));
            if (e1.divide(TWO).isProbablePrime(15))
                return new Pair(e1, e1.divide(TWO));
            if (e2.divide(TWO).isProbablePrime(15))
                return new Pair(e2, e2.divide(TWO));
        }
        return new Pair(null, null);
    }

    public static void main(String[] args) {
        EllCurves m = new EllCurves(16);


//        Pair res = m.sq2p(p);
//        System.out.println(res.f0() + " " + res.f1());
    }
}
