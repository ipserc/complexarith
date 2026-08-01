package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;
import com.ipserc.arith.complex.*;

public class TestBase01 {

	public static boolean isBase(MatrixComplex base) {
		if (base.determinant().isZero()) return false;
		return true;
	}

	public static MatrixComplex pushCoords(MatrixComplex base) {
		MatrixComplex newBase = base.clone();
		for ( int row = 1; row < base.rows(); ++row) {
			Complex tail = base.getItem(row, base.cols()-1);
			for (int col = 0; col < base.cols()-1; ++col) {
				newBase.setItem(row, col+1, base.getItem(row, col));
			}
			newBase.setItem(row, 0, tail);
		}
		return newBase;
	}
	
	public static MatrixComplex base(MatrixComplex coord1) {
		int dim = coord1.cols();
		MatrixComplex base;
		base = MatrixComplex.eye(dim);
		
		int count = 1;
		base.complexMatrix[0] = coord1.complexMatrix[0].clone();
		// base.println("ChekBase");
		while (!isBase(base)) {
			base = pushCoords(base);
			++count;
			// base.println("ChekBase");
			if (count == dim) return (new MatrixComplex(dim));
		}
		return base;
	}

	public static void main(String[] args) {
		//MatrixComplex coord1 = new MatrixComplex("0,0,-1,4");
		VectorComplex coord1 = new VectorComplex("-i,0,-1,2-4i");
		MatrixComplex base;
		
		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	Complex.numpPadPLUS();

		
		coord1.println("Crear una base con este vector"); 
		base = base(coord1);
		base.println("tengo una base");
		base.determinant().println("Det:");
		
		VectorComplex v1 = new VectorComplex("3,-2,0,-1");
		v1.println("v1 en base ortnormal:");
		v1.baseChg(base);
		v1.println("v1 en base(coord1)..:");
	}
}
