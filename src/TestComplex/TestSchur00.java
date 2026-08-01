package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vectorcomplex.*;

public class TestSchur00 {

	private static int boxSize = 65;
	private static int iteracion = 0;
	private static boolean __DEBUG__ = false;
	
	public static MatrixComplex Schur_(MatrixComplex aMat) {
    	aMat.println("aMat");
		Eigenspace aMatEigen = new Eigenspace(aMat);
		aMatEigen.eigenvalues().println("aMatEigen.eigenvalues()");
		aMatEigen.eigenvectors().println("aMatEigen.eigenvectors()");
		aMatEigen.eigenvector(0).println("aMatEigen.eigenvector(0)");
		System.out.println("aMatEigen.eigenvector(0).norm():"+aMatEigen.eigenvector(0).norm());
		
		MatrixComplex baseW1 = new MatrixComplex(aMat.rows(), aMat.cols());
		// Tomamos λ1 autovalor de A y w1 autovector de A con autovalor λ1 y kw1 k2 = 1.
		baseW1.setRow(0, aMatEigen.eigenvector(0));
		// Completando {w1 } a una base de Kn 
		for (int i = 1; i < aMat.rows(); ++i) {
			for (int j = 0; j < aMat.cols(); ++j) {
				baseW1.setItem(i, j, i == j ? Complex.ONE : Complex.ZERO);
			}
		}
		//baseW1.setRow(2, new MatrixComplex("0,0,1"));
		baseW1.println("baseW1");
		// y aplicando ortonormalización de Gram-Schmidt, podemos obtener una base ortonormal de Cn ,
		MatrixComplex baseW1ortn = baseW1.gramSchmidt().normalize();
		baseW1ortn.println("baseW1ortn");
		for (int i = 0; i < aMat.rows(); ++i) {		
			System.out.println("¬¬¬baseW1ortn.getRow("+i+").norm():"+baseW1ortn.getRow(0).norm());
		}
		// Construimos la matriz U1 tomando estos vectores como columnas de la matriz. Como la primera columna de AU1 es λ1 w (1) , obtenemos
		MatrixComplex schur = baseW1ortn.adjoint().times(aMat).times(baseW1ortn);
		schur.println("Schur");
			
		baseW1ortn.times(schur).times(baseW1ortn.adjoint()).println("ChecK");

		return schur;
	}

	public static MatrixComplex SchurAdjoint(MatrixComplex aMat, int iteracion, int rows) {
		Complex.printBoxTextRandom(boxSize, "ITERACION " + (iteracion));
    	aMat.println("aMat");
		Eigenspace aMatEigen = new Eigenspace(Complex.ONE, aMat);
		aMatEigen.eigenvalues().println("aMatEigen.eigenvalues()");
		aMatEigen.eigenvectors().println("aMatEigen.eigenvectors()");
		aMatEigen.eigenvector(0).println("aMatEigen.eigenvector(0)");
		System.out.println("aMatEigen.eigenvector(0).norm():"+aMatEigen.eigenvector(0).norm());
		
		/** /
		MatrixComplex baseW1 = new MatrixComplex(aMat.rows(), aMat.cols());
		// Tomamos λ1 autovalor de A y w1 autovector de A con autovalor λ1 y norma2(w1) = 1.
		baseW1.setRow(0, aMatEigen.eigenvector(0));
		// Completando baseW1 {w1 } a una base de Kn
		for (int i = 1; i < aMat.rows(); ++i) {
			for (int j = 0; j < aMat.cols(); ++j) {
				baseW1.setItem(i, j, i == j ? Complex.ONE : Complex.ZERO);
			}
		}
		baseW1.println("baseW1");
		// y aplicando ortonormalización de Gram-Schmidt, podemos obtener una base ortonormal de Cn (baseW1ortn) ,
		// Construimos la matriz U1 tomando estos vectores como columnas de la matriz. Como la primera columna de AU1 es λ1 w (1) , obtenemos
		MatrixComplex baseW1ortn = baseW1.gramSchmidt().normalize().transpose();
		/**/
		/**/
		VectorComplex eigenVector = new VectorComplex(aMatEigen.eigenvector(0)).normalize();
		// y aplicando ortonormalización de Gram-Schmidt, podemos obtener una base ortonormal de Cn (baseW1ortn) ,
		// Construimos la matriz U1 tomando estos vectores como columnas de la matriz. Como la primera columna de AU1 es λ1 w (1) , obtenemos
		MatrixComplex baseW1ortn = eigenVector.base().normalize().transpose();
		/**/
		baseW1ortn.println("baseW1ortn");
		for (int i = 0; i < aMat.rows(); ++i) {		
			System.out.println("= = = = baseW1ortn.getRow("+i+").norm():"+baseW1ortn.getRow(0).norm());
		}
		MatrixComplex schur = baseW1ortn.adjoint().times(aMat).times(baseW1ortn);
		schur.println("Schur");
			
		baseW1ortn.times(schur).times(baseW1ortn.adjoint()).println("ChecK");

		MatrixComplex V = MatrixComplex.eye(rows);
		for (int i = iteracion; i <  rows; ++i)
			for (int j = iteracion; j <  rows; ++j)
				V.setItem(i, j, baseW1ortn.getItem(i-iteracion, j-iteracion));

		if (aMat.dim() > 1) { 			
			// Reiteramos el proceso si la dimensión de schur es mayor que 1
			MatrixComplex aMat2 = schur.minor(0,0);
			V = V.times(SchurAdjoint(aMat2, ++iteracion, rows));
			return V;
		}
		else {
			return V;
		}
	}

	public static MatrixComplex SchurInverse(MatrixComplex aMat, int iteracion, int rows) {
		Complex.printBoxTextRandom(boxSize, "ITERACION " + (iteracion));
    	aMat.println("aMat");
    	
		Eigenspace aMatEigen = new Eigenspace(aMat);
		
		if (__DEBUG__) {
			aMatEigen.eigenvalues().println("aMatEigen.eigenvalues()");
			aMatEigen.eigenvectors().println("aMatEigen.eigenvectors()");
			aMatEigen.eigenvector(0).println("aMatEigen.eigenvector(0)");
			System.out.println("aMatEigen.eigenvector(0).norm():"+aMatEigen.eigenvector(0).norm());
		}
		/* 	METHOD ONE	*/
		MatrixComplex baseW1 = new MatrixComplex(aMat.rows(), aMat.cols());
		// Tomamos λ1 autovalor de A y w1 autovector de A con autovalor λ1 y norma2(w1) = 1.
		baseW1.setRow(0, aMatEigen.eigenvector(0));
		// Completando baseW1 {w1 } a una base de Kn
		for (int i = 1; i < aMat.rows(); ++i) {
			for (int j = 0; j < aMat.cols(); ++j) {
				baseW1.setItem(i, j, i == j ? Complex.ONE : Complex.ZERO);
			}
		}
		baseW1.println("baseW1");
		// y aplicando ortonormalización de Gram-Schmidt, podemos obtener una base ortonormal de Cn (baseW1ortn) ,
		// Construimos la matriz U1 tomando estos vectores como columnas de la matriz. Como la primera columna de AU1 es λ1 w (1) , obtenemos
		MatrixComplex baseW1ortn = baseW1.gramSchmidt().normalize().transpose();
		/*	METHOD ONE	*/

		
		/*	METHOD TWO	* /
		MatrixComplex baseW1 = new MatrixComplex(aMat.rows(), aMat.cols());
		baseW1.setRow(0, aMatEigen.eigenvector(0));
		MatrixComplex baseW1ortn = baseW1.base(); //!!!!!!!!!!!!!!!! HAY QUE REVISAR base()
		/*	METHOD TWO	*/
		
		if (__DEBUG__) {
			baseW1ortn.println("baseW1ortn");
			for (int i = 0; i < aMat.rows(); ++i) {		
				System.out.println("= = = = baseW1ortn.getRow("+i+").norm():"+baseW1ortn.getRow(0).norm());
			}
		}
		
		MatrixComplex schur = baseW1ortn.inverse().times(aMat).times(baseW1ortn);

		if (__DEBUG__) {		
			schur.println("Schur");
			baseW1ortn.times(schur).times(baseW1ortn.inverse()).println("ChecK");
		}
		
		MatrixComplex V = MatrixComplex.eye(rows);
		for (int i = iteracion; i <  rows; ++i)
			for (int j = iteracion; j <  rows; ++j)
				V.setItem(i, j, baseW1ortn.getItem(i-iteracion, j-iteracion));

		if (aMat.dim() > 1) { 			
			// Reiteramos el proceso si la dimensión de schur es mayor que 1
			MatrixComplex aMat2 = schur.minor(0,0);
			V = V.times(SchurInverse(aMat2, ++iteracion, rows));
			return V;
		}
		else {
			return V;
		}
	}

	public static void main(String[] args) {
		MatrixComplex aMat;
		// Complex.resetFormatStatus();
		// Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	// Complex.digits(10000000);
    	Complex.setScientificON(4);
    	//Complex.setFixedON(4);
    	Complex.numpPadPLUS();

		//aMat = new MatrixComplex("7,16,-28;-8,28,-22;2,-7,19").divides(9);
		//aMat = new MatrixComplex("-5+i,-15;2,6+i");
    	//aMat = new MatrixComplex("-5,-15;2,7");
  		//aMat = new MatrixComplex(" 4.000000,-1.000000, 6.000000; 0.000000, 1.000000, 6.000000; 0.000000, 0.000000, 8.000000");
    	//aMat = new MatrixComplex(" 4.000,-1.000, 1.000; 0.000, 1.000, 3.000; 0.000, 2.000, 2.000");
    	//aMat = new MatrixComplex(" 1.000,-1.000, 2.000;-1.000, 1.000,-2.000; 2.000,-2.000, 4.000");
	    //aMat = new MatrixComplex(" 1.000i, 1.000, 1.000i;-1.000,-1.000i,-1.000; 1.000i, 1.000,-1.000i");
	    //aMat = new MatrixComplex(" 4.000,-1.000, 1.000; 0.000, 3.000, 3.000; 0.000, 2.000, 2.000");
    	//aMat = new MatrixComplex("+5.0814E+01-4.0915E+01i,-3.3344E+01+3.2794E+01i;-2.7166E+01-4.3478E+01i,-4.7661E+01+2.1337E+00i");
    	//aMat = new MatrixComplex("-8.4000E+01,+4.5000E+01,-4.8000E+01;-7.4000E+01,+9.9000E+01,-1.6000E+01;+1.4000E+01,-2.1000E+01,+5.6000E+01");
    	//aMat = new MatrixComplex("+6.0000E+00,+1.0000E+00,+7.0000E+00;-2.0000E+00,+1.0000E+00,+1.0000E+00;-1.0000E+00,-4.0000E+00,-4.0000E+00").divides(1);
    	//aMat = new MatrixComplex(" 1.0000, 2.0000, 0.0000,-1.0000, 3.0000;-0.1333, 0.7333, 0.0000, 0.1333,-0.4000; 0.0000, 0.0000, 1.0000, 0.0000, 0.0000; 0.0909, 0.0000, 0.0000, 0.9091, 0.2727;-0.3000, 0.0000, 0.0000, 0.0000, 0.1000");
    	//aMat = new MatrixComplex("+5.0000E+00,-7.0000E+00,-4.0000E+00,-6.0000E+00;-4.0000E+00,+4.0000E+00,+5.0000E+00,-3.0000E+00;-2.0000E+00,+7.0000E+00,-1.0000E+00,+5.0000E+00;+2.0000E+00,-7.0000E+00,+3.0000E+00,-3.0000E+00");

    	/*
    	aMat = new MatrixComplex(""
    			+ "-1.6012815000E-01+4.8038446000E-01i, 0.0000000000E+00, 8.0064077000E-01-3.2025631000E-01i, 0.0000000000E+00; "
    			+ "0.0000000000E+00, 1.0000000000E+00i, 0.0000000000E+00, 0.0000000000E+00;"
    			+ " 5.5700665000E-01-6.5828059000E-01i, 0.0000000000E+00, 5.0636968000E-01, 0.0000000000E+00;"
    			+ " 0.0000000000E+00, 0.0000000000E+00, 0.0000000000E+00, 1.0000000000E+00i");
    			
    	aMat = new MatrixComplex("+2.0000E+00,+0.0000E+00,-1.0000E+00,+0.0000E+00,-2.0000E+00;+0.0000E+00,+6.0000E+00,+0.0000E+00,+0.0000E+00,+0.0000E+00;-1.0000E+00,+0.0000E+00,+4.0000E+00,+0.0000E+00,-4.0000E+00;+0.0000E+00,+0.0000E+00,+0.0000E+00,+2.0000E+00,+4.0000E+00;-2.0000E+00,+0.0000E+00,-4.0000E+00,+4.0000E+00,-6.0000E+00");
		aMat = new MatrixComplex("+2.0000E+00,-2.0000E+00,-1.0000E+00,-1.0000E+00,-3.0000E+00,+5.0000E+00,+3.0000E+00;-2.0000E+00,-4.0000E+00,-2.0000E+00,-6.0000E+00,-2.0000E+00,-3.0000E+00,+1.0000E+00;-1.0000E+00,-2.0000E+00,+4.0000E+00,+5.0000E+00,-4.0000E+00,-3.0000E+00,+0.0000E+00;-1.0000E+00,-6.0000E+00,+5.0000E+00,+6.0000E+00,-4.0000E+00,+1.0000E+00,-2.0000E+00;-3.0000E+00,-2.0000E+00,-4.0000E+00,-4.0000E+00,+2.0000E+00,+2.0000E+00,+0.0000E+00;+5.0000E+00,-3.0000E+00,-3.0000E+00,+1.0000E+00,+2.0000E+00,+6.0000E+00,+0.0000E+00;+3.0000E+00,+1.0000E+00,+0.0000E+00,-2.0000E+00,+0.0000E+00,+0.0000E+00,+6.0000E+00");
  		    aMat = new MatrixComplex(""
  		    		+ "+2.0,-3.0,-2.0,+3.0,-1.0;"
  		    		+ "+1.0,+2.0,+1.0,-1.0,+1.0;"
  		    		+ "-1.0,+1.0,+1.0,+1.0,-1.0;"
  		    		+ "+1.0,-3.0,-1.0,+3.0,+1.0;"
  		    		+ "-2.0,+2.0,+1.0,-2.0,+1.0");

    	*/
    	
    	Complex.printFormatStatus();
    	Complex.showPrecision();
    	
    	int iteration = 0;
  		MatrixComplex schur;
  		MatrixComplex U;
  		boolean retry = true;
  		boolean adjoint_variant = false;
  		
  		do {
  			++iteration;
  	    	aMat = new MatrixComplex(3); aMat.initMatrixRandomInt(9);
  	    	//aMat = aMat.hermitian(); 

  	    	//aMat = new MatrixComplex("+7.0000E+00,+6.0000E+00,+2.0000E+00;+7.0000E+00,+6.0000E+00,-6.0000E+00;-4.0000E+00,+4.0000E+00,-7.0000E+00");

  	    	if (adjoint_variant) {
		  		try {
					Complex.printBoxTitleRandom(boxSize, "FACTORIZACIÓN SCHUR ADJOINT iteration:" + iteration);
			    	System.out.println("aMat = "+aMat.toMatrixComplex());
					U = SchurAdjoint(aMat, 0, aMat.rows());
					U.println(Complex.boxTextRandom(boxSize, "SOLUTION U Adjoint"));
					schur = U.adjoint().times(aMat).times(U);
					schur.println(Complex.boxTextRandom(boxSize, "SOLUTION SCHUR Adjoint:"));
					retry = false;
					Complex.printBoxTitleRandom(boxSize, "CHECK FACTORIZACIÓN SCHUR ADJOINT (ZERO)");
					aMat.minus(U.times(schur).times(U.adjoint())).println("Check Adjoint aMat.minus(U.times(schur).times(U.inverse()))");
					
		  		}
		  		catch (Exception exc) {
		  			continue;
		  		}
  	    	}
	
	  		try {
				Complex.printBoxTitleRandom(boxSize, "FACTORIZACIÓN SCHUR INVERSE iteration:" + iteration);
		    	System.out.println("aMat = "+aMat.toMatrixComplex());
				U = SchurInverse(aMat, 0, aMat.rows());
				U.println(Complex.boxTextRandom(boxSize, "SOLUTION U Inverse"));
				Complex.printBoxTextRandom(boxSize, " U is unitary:" + U.isUnitary());
				schur = U.inverse().times(aMat).times(U);
				schur.println(Complex.boxTextRandom(boxSize, "SOLUTION SCHUR Inverse:"));
				retry = false;
				Complex.printBoxTitleRandom(boxSize, "CHECK FACTORIZACIÓN SCHUR INVERSE (ZERO)");
				aMat.minus(U.times(schur).times(U.inverse())).println("Check Inverse aMat.minus(U.times(schur).times(U.inverse()))");
	  		}
	  		catch (Exception exc) {
	  			
	  		}
	  		Runtime.getRuntime().gc();
  		} while (retry);	
	}
}
