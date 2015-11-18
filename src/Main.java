import java.math.BigInteger;

/**
 * Created by admin on 13.11.2015.
 */
public class Main {
    private static BigInteger ZERO = BigInteger.ZERO;
    private static BigInteger ONE = BigInteger.ONE;
    private static BigInteger TWO = new BigInteger(String.valueOf(2));
    private static BigInteger FOUR = new BigInteger(String.valueOf(4));
    private BigInteger mONE = new BigInteger(String.valueOf(-1));

    public static void main(String[] args) {
        int n = 16;
        EllCurves l = new EllCurves(n);
        BigInteger p, d, e, m, ee = ZERO;
        while (true) {
            p = l.generateMod4(true, n);
            System.out.println("Generated p = " + p);
            Pair pair = l.sq2p(p);
            e = pair.f0();
            d = pair.f1();
            System.out.println("p was decomposited; \n " +
                    "e = " + e + " ;d = " + d);
            BigInteger[] E = new BigInteger[]{
                    p.add(ONE).add(d.multiply(TWO)),
                    p.add(ONE).subtract(d.multiply(TWO)),
                    p.add(ONE).add(e.multiply(TWO)),
                    p.add(ONE).subtract(e.multiply(TWO))
            };
            boolean flag = false;
            for (int i = 0; i <= 1; i++) {
                flag |= (E[i].mod(FOUR).equals(ZERO));
            }
            for (int i = 2; i < 4; i++) {
                flag |= (E[i].divide(TWO)).isProbablePrime(10);
            }
            System.out.println("flag is " + flag);
            if (flag)
                break;
        }
        while (true){
            BigInteger x0, y0;
            x0 = l.randomNumber(false, p.bitLength());
            y0 = l.randomNumber(false, p.bitLength());
            System.out.println("Generated point: (" + x0 + ", " + y0 + ")");
            BigInteger temp = l.gcdExtended(x0, p)[1];
            BigInteger a = (((y0.multiply(y0)).subtract(x0.multiply(x0.multiply(x0)))).
                    multiply(temp)).mod(p);
            System.out.println("a is " + a);
            Pair tPair = l.check(a, p, d, e);
            ee = tPair.f0();
            m = tPair.f1();
            System.out.println("#E(GF(" + p + ") = " + ee);
//            if (ee.equals(null)){
//                if ()
//            }
        }

    }


}
