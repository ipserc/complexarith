package TestComplex;

import com.ipserc.arith.polynom.*;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class plotPolynom01 {

	public static void showResults(Polynom myPolynom) {
		MatrixComplex roots;
		double loLimit, upLimit;

		myPolynom.println();
		System.out.println("Polynom:" + myPolynom.toPolynom());
		System.out.println("Maxima:" + myPolynom.toMaxima());
		roots = myPolynom.solve();
		roots.quicksort(0);
		roots.println("roots");
		loLimit = myPolynom.getMinRoot(roots);		
		upLimit = myPolynom.getMaxRoot(roots);
		myPolynom.setSampleBase(1600);
		//myPolynom.plot(loLimit, upLimit);
		//myPolynom.plotReIm(loLimit, upLimit);     	
     	//myPolynom.plotAbs(loLimit, upLimit);     	
     	//myPolynom.plotPhase(loLimit, upLimit);		
	}
	
	public static void main(String[] args) {
		Polynom myPolynom, aPolynom;
		int degree;
     	int kind;
     	int alpha;

     	Complex.setFormatON();
     	Complex.setScientificON(5);
     	
     	aPolynom = new Polynom();
     	myPolynom = new Polynom();
     	
		/*
    	myPolynom = new Polynom("1.0,-1.5i,2.0,-1.5i,1.0");
		showResults(myPolynom);
     	* /
     	myPolynom = new Polynom("1.0,1.0,-5.0,1.0,-6.0");
		showResults(myPolynom);

     	myPolynom = new Polynom("2.0-2.0i,-3.0+9.0i,-6.0-5.0i,6.0-9.0i,4.0-2.0i");
		showResults(myPolynom);
     	/*     	
		myPolynom = new Polynom("1.0,-10.0,35.0,-50.0,24.0");
		showResults(myPolynom);
     	
     	myPolynom = new Polynom("1.0i,0.0,-5.0i,0.0,4.0i");
		showResults(myPolynom);

     	myPolynom = new Polynom("1.0i,0.0,-5.0i,0.0,4.0");
		showResults(myPolynom);
		* /

     	myPolynom = new Polynom("1.0i,0.0,-5.0,0.0,4.0i");
		showResults(myPolynom);
     	* /
		myPolynom = new Polynom("1.0,1.0,6.0-23.0i,47.0+20.0i,219.0+109.0i,46.0-330.0i,-1536+432i");
		showResults(myPolynom);
     	* /
		myPolynom = new Polynom("1.0,6.0,30.0-14.0i,68.0-8.0i,61.0+30.0i,-222.0-96.0i,-1080.0+360.0i");
		showResults(myPolynom);
     	/** /
 		/ **/
		/* * / 
     	//myPolynom = new Polynom("-1.0,3.0+2.0i,-23.0-8.0i,-123.0-42.0i,-50.0+152.0i,91200000001+136.0i,144000000002-480000000006i");
     	myPolynom = new Polynom("-1.0,3.0+2.0i,-23.0-8.0i,-123.0-42.0i,-50.0+152.0i,912.01+136.0i,0");
		myPolynom.setFixFloatON(3);
		roots = myPolynom.solve();
		showResults(myPolynom);
     	/ ** /
		
		myPolynom = new Polynom("1.0,1.0+5.0i,-25.0-9.0i,-37.0-63.0i,36.0-115.0i,48.0-66.0i,360.0+1080.0i");
		showResults(myPolynom);

		myPolynom = new Polynom("1,10,-30.49,-445.14,199.54,5727.08,-1890.73,-21585.78,6065.64,20638.8");
		showResults(myPolynom);
		
     	myPolynom = new Polynom().fromRoots("-3,1-3i,-3-i,3,2i,5");
		showResults(myPolynom);
     	/ * */

     	/* * /
		degree = 5;
     	myPolynom = new Polynom().hermite(degree);
     	myPolynom.println("Hermite " + degree);
		showResults(myPolynom);
     	/* * /
     	
     	myPolynom = new Polynom().legendre(degree);
     	myPolynom.println("Legendre " + degree);
		showResults(myPolynom);
   	
     	myPolynom = new Polynom().laguerre(degree);
     	myPolynom.println("Laguerre " + degree);
		showResults(myPolynom);

     	alpha = 2;
     	myPolynom = new Polynom().laguerre(degree, alpha);
     	myPolynom.println("Laguerre " + degree + "," + alpha);
		showResults(myPolynom);

     	myPolynom = new Polynom().chebyshev1(degree);
     	myPolynom.println("Chebyshev1 " + degree);
		showResults(myPolynom);
     	
     	myPolynom = new Polynom().chebyshev2(degree);
     	myPolynom.println("Chebyshev2 " + degree);
		showResults(myPolynom);
    	/* */

     	/* * /
		degree = 5;
     	kind = 3;
     	myPolynom = new Polynom().chebyshev(degree, kind);
     	myPolynom.println("Chebyshev " + degree + "," + kind);
		showResults(myPolynom);
     	
     	myPolynom = new Polynom().fromRoots("3-3i,-3+3i,2-2i,-2+2i,1-3i,-1+3i,0,3,-3,5,-5");
		showResults(myPolynom);
		
		myPolynom = new Polynom("1,10,-30.49,-445.14,199.54,5727.08,-1890.73,-21585.78,6065.64,20638.8");
		showResults(myPolynom);
		
		myPolynom = new Polynom("1,-7+8i,4+4i,-8+9i,-7+7i,-3-6i,1-8i,5-7i,-9-8i,8+7i,3+8i,6+8i,6+8i,-3-3i,2-i,-6-6i");
		showResults(myPolynom);
     	myPolynom = new Polynom(13);
		myPolynom.initMatrixRandomRec(7);
		showResults(myPolynom);

		/* */

     	/*
		degree = 4;
       	myPolynom = new Polynom().legendre(degree);
     	myPolynom.println("Legendre " + degree);
		showResults(myPolynom);
		*/
     	
     	/*
		degree = 3;
     	alpha = 1;
       	myPolynom = new Polynom().laguerre(degree, alpha);
     	myPolynom.println("Laguerre " + degree + "," + alpha);
		showResults(myPolynom);
		*/
     	
		/*
		degree = 0;
     	kind = 1;
     	myPolynom = new Polynom().chebyshev(degree, kind);
     	myPolynom.println("Chebyshev " + degree + "," + kind);
		showResults(myPolynom);
		*/

		/*
		myPolynom = new Polynom("1,2,-1,-2");
		showResults(myPolynom);

		myPolynom = new Polynom("1.3,1.7,-1.3,-1.7");
		showResults(myPolynom);

		myPolynom = new Polynom("1.45,1.55,-1.45,-1.55");
		showResults(myPolynom);
		
		aPolynom = new Polynom("1,2,-1,-2");
		myPolynom = new Polynom("1.45,1.55,-1.45,-1.55");
		myPolynom = myPolynom.times(aPolynom);
		showResults(myPolynom);

		aPolynom = new Polynom("1,2,-1,-2");
		myPolynom = new Polynom("1,2");
		aPolynom = myPolynom.times(aPolynom);
		myPolynom = new Polynom("1.45,1.55,-1.45,-1.55");
		myPolynom = myPolynom.times(aPolynom);
		showResults(myPolynom);
		*/
		//MatrixComplex polGen = new MatrixComplex(); polGen.
		
     	/*
     	String strRoots = "1,2,3,5,7,11,13,17,19,23,29,31,37,41";
    	myPolynom = myPolynom.fromRoots(strRoots);
    	myPolynom.println("fromRoots(\""+strRoots+"\")");
		showResults(myPolynom);

		myPolynom = new Polynom("-1.66666666666666666667E-01,1.50000E+00,-3.00000E+00,1.00000E+00");
		showResults(myPolynom);
		*/
     	/*
		myPolynom = new Polynom("1.66667E-01-2.50000E+00i,-1.00000E+00+1.80000E+01i,-1.66667E-01-3.95000E+01i,3.00000E+00+2.60000E+01i");
		showResults(myPolynom);
		
		myPolynom = new Polynom("-2.23214E-03,6.98413E-02,-8.92361E-01,5.99306E+00,-2.26580E+01,4.77347E+01,-5.09474E+01,2.17024E+01,1.00000E+00");
		showResults(myPolynom);
		*/
		myPolynom = new Polynom("-3.55000E-06,2.56760E-04,-8.19242E-03,1.51722E-01,-1.80582E+00,1.44435E+01,-7.88203E+01,2.91676E+02,-7.11779E+02,1.08015E+03,-9.05087E+02,3.12074E+02,1.00000E+00");
		showResults(myPolynom);
		
	}

}
