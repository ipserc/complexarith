package TestComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
public class ScratchTimesDirectCheck01 {
    public static void main(String[] args) {
        MatrixComplex a6 = new MatrixComplex("1,2;3,4");
        MatrixComplex b6 = new MatrixComplex("1,2,3");
        try {
            MatrixComplex r = a6.times(b6);
            System.out.println("times() succeeded: " + r);
        } catch (Exception e) {
            System.out.println("times() threw: " + e);
        }
    }
}
