package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;
import com.ipserc.arith.complex.*;

public class TestBase02 {

	public static void main(String[] args) {
		//MatrixComplex coord1 = new MatrixComplex("0,0,-1,4");
		VectorComplex coord1 = new VectorComplex("0,0,-3,2");
		MatrixComplex base;
		
		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	Complex.digits(10000000);
    	Complex.setFixedON(2);
    	Complex.numpPadBLANK();

		coord1.println("Crear una base con este vector"); 
		base = coord1.base();
		base.println("tengo una base");
		base.determinant().println("Det:");
		
		VectorComplex v1 = new VectorComplex("3,-2,0,-1");
		v1.println("v1 en base ortnormal:");
		v1.baseChg(base);
		v1.println("v1 en base(coord1)..:");
	}
}
