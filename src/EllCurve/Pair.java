package EllCurve;

import java.math.BigInteger;

/**
 * Created by admin on 13.11.2015.
 */
public class Pair {

    private BigInteger n1;
    private BigInteger n2;

    public Pair (BigInteger n1, BigInteger n2){
        this.n1 = n1;
        this.n2 = n2;
    }





    public BigInteger f0(){
        return n1;
    }

    public BigInteger f1(){
        return n2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair pair = (Pair) o;

        return n1.equals(pair.n1) && n2.equals(pair.n2);

    }


}
