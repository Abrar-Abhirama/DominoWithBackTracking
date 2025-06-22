import java.util.Arrays;
import java.util.Objects;

public class Domino {
    private int val1;
    private int val2;

    public Domino(int val1, int val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public int getVal1() {
        return val1;
    }

    public int getVal2() {
        return val2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domino domino = (Domino) o;
        return (val1 == domino.val1 && val2 == domino.val2) ||
               (val1 == domino.val2 && val2 == domino.val1);
    }

    @Override
    public int hashCode() {
        int[] vals = {val1, val2};
        Arrays.sort(vals);
        return Objects.hash(vals[0], vals[1]);
    }
    
    @Override
    public String toString() {
        return "[" + val1 + "|" + val2 + "]";
    }
}