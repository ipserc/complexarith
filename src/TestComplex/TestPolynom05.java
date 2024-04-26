package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.polynom.Polynom;
import com.ipserc.chronometer.*;

public class TestPolynom05 {

	public static void main(String[] args) {
		Polynom myPolynom;		
		int boxSize = 65;
    	Chronometer chron = new Chronometer();
  
		Complex.setFormatON();
		Complex.setFixedON(3);

		myPolynom = new Polynom("-.07-.01i,.06+.03i,.05-.05i,.04-.03i,-.03i,.02+.07i,-.01+.03i");
		myPolynom.println("polynomial:");

		System.out.println(Complex.boxTextRandom(boxSize, "Checking Horner vs Factors Evaluations"));
    	for (int i = -10; i < 11; ++i) {
    		System.out.println("fH(" + i + ")=" + myPolynom.evalHorner(i));
			System.out.println("fF(" + i + ")=" + myPolynom.evalFact(i));
    	}
    	
		int nbrPoitns = 9000000;
		System.out.println(Complex.boxTextRandom(boxSize, "Horner vs Factors time comparison for " + nbrPoitns + " points"));
    	chron.start();
    	for (int i = 0; i < nbrPoitns; ++i) {
    		myPolynom.evalHorner(i);
    	}
    	chron.stop();
    	System.out.println("Chrono Horner: " + chron.toString());

    	chron.start();
    	for (int i = 0; i < nbrPoitns; ++i) {
    		myPolynom.evalFact(i);
    	}
    	chron.stop();
    	System.out.println("Chrono Factor: " + chron.toString());

    	chron.start();
    	for (int i = 0; i < nbrPoitns; ++i) {
    		myPolynom.evalNorm(i);
    	}
    	chron.stop();
    	System.out.println("Chrono Norm  : " + chron.toString());
	}
}
