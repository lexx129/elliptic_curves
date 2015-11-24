import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    public static void main(String[] args) throws IOException {
        int n = 16;
        System.out.println("Please wait, while generating...");
        EllCurves l = new EllCurves(n);
        BigInteger p, d, e, m, ee = ZERO;
        BigInteger x0, y0;
        BigInteger a;
        while (true) {
            p = l.getP();
//            p = BigInteger.valueOf(52321);
            System.out.println("Generated p = " + p);
            Pair pair = l.sq2p(p);
            e = pair.f0().abs();
            d = pair.f1().abs();
            System.out.println("p was decomposited; \n " +
                    "e = " + e + "; d = " + d);
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
                flag |= (l.div(E[i], TWO).isProbablePrime(10));
//                flag |= (E[i].divide(TWO)).isProbablePrime(10);
            }
            System.out.println("flag is " + flag);
            if (flag)
                break;
            else {
                p = l.regenerateP(true, n);
                l.setP(p);
            }
        }
        while (true) {

            x0 = l.randomNumber(false, p.bitLength());
            y0 = l.randomNumber(false, p.bitLength());
//            x0 = BigInteger.valueOf(15227);
//            y0 = BigInteger.valueOf(4523);
            System.out.println("Generated point: (" + x0 + ", " + y0 + ")");
            BigInteger temp = l.gcdExtended(x0, p)[1];
            a = (((y0.multiply(y0)).subtract(x0.multiply(x0.multiply(x0)))).
                    multiply(temp)).mod(p);
            System.out.println("a is " + a);
            Pair tPair = l.check(a, p, d, e);
            ee = tPair.f0();
            m = tPair.f1();
            System.out.println("#E(GF(" + p + ") = " + ee);
            if (!ee.equals(l.std1)) {
                BigInteger[] t = l.mult(ee, new Pair(x0, y0), a, p);
                BigInteger t1 = t[0];
                BigInteger t2 = t[1];
                if (new Pair(t1, t2).equals(new Pair(l.std1, l.std2)))
                    break;
            }
        }
//        BigInteger[] t = l.mult(ee.divide(m), new Pair(x0, y0), a, p);
        BigInteger[] t = l.mult(l.div(ee, m), new Pair(x0, y0), a, p);
        BigInteger t1 = t[0];
        BigInteger t2 = t[1];
        Pair G = new Pair(t1, t2);
        System.out.println("G = " + G.f0() + "; " + G.f1());
        System.out.println("m = " + m);
        Pair S = G;
        OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(".\\output_x.txt"));
        OutputStreamWriter osr1 = new OutputStreamWriter(new FileOutputStream(".\\output_y.txt"));
        for (BigInteger i = ZERO; i.compareTo(m) < 0; i = i.add(ONE)) {
            S = l.add(S, G, a, p);
            if (!S.f0().equals(l.std1))
            osr.write(String.valueOf(S.f0()) + "\n");
            if (!S.f1().equals(l.std2))
            osr1.write(String.valueOf(S.f1()) + "\n");
//            System.out.println("x = " + S.f0() + "    y = " + S.f1());
        }
        osr.flush();
        osr1.flush();
        osr.close();
        osr1.close();
        System.out.println("Programm finished correctly. Check results in output files.");
    }


}
