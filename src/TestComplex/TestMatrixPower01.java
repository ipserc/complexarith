package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.chronometer.Chronometer;

public class TestMatrixPower01 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	MatrixComplex fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
    	MatrixComplex sMatrix;
		int boxSize = 65;
		Chronometer chrono = new Chronometer();
		

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	Complex.numpPadPLUS();
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();


    	Complex.printBoxTitleRandom(boxSize, "MATRIX COMPLEX NATURAL LOG EXP");
    	System.out.println();
    	
    	aMatrix = new MatrixComplex("32");
    	bMatrix = new MatrixComplex("-2+3i");
    	cMatrix = new MatrixComplex("3 , -2; -4, 7");
    	
    	int iExp = 999999999;
    	MatrixComplex thisMatrix = cMatrix.copy().divides(1E3);
    	
    	/* Funny but unuseful
    	{
    		chrono.start();
    		int signExp = iExp >= 0 ? 1 : -1;
    		iExp = iExp * signExp;
    		int powIter = (int)Math.floor(Math.log(iExp)/Math.log(2));
    		MatrixComplex powMatrix = thisMatrix.copy();
    		for (int i = 0; i < powIter; ++i) {
    			powMatrix = powMatrix.times(powMatrix);
    		}
    		
    		for (int i = 0; i < iExp - Math.pow(2, powIter); ++i ) {
    			powMatrix = powMatrix.times(thisMatrix);
    		}
    		if (signExp == -1) powMatrix = powMatrix.inverse();
    		powMatrix.println("powMatrix");
    		chrono.stop();
    		chrono.println("Method 1");
    	}
    	*/
    	
		chrono.start();
    	thisMatrix.power(iExp).println("thisMatrix.power(iExp)");
		chrono.stop();
		chrono.print("Method 1");

	}

}
