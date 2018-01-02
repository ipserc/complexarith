package TestComplex;

import com.ipserc.arith.polynom.*;
import com.ipserc.arith.matrixcomplex.*;

public class plotPolynom {

	public static void showResults(Polynom myPolynom) {
		MatrixComplex roots;
		double loLimit, upLimit;

		myPolynom.println();
		roots = myPolynom.solve();
		roots.println("roots");
		loLimit = myPolynom.getMinRoot(roots);
		upLimit = myPolynom.getMaxRoot(roots);
		myPolynom.setSampleBase(600);
		myPolynom.plot(loLimit, upLimit);
		myPolynom.plotReIm(loLimit, upLimit);     	
     	myPolynom.plotAbs(loLimit, upLimit);     	
     	myPolynom.plotPhase(loLimit, upLimit);		
	}
	
	public static void main(String[] args) {
		Polynom myPolynom;
		
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
     	//myPolynom = new Polynom("-1.0,3.0+2.0i,-23.0-8.0i,-123.0-42.0i,-50.0+152.0i,912.0000000000001+136.0i,1440.0000000000002-480.00000000000006i");
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
		int degree = 5;
     	myPolynom = new Polynom().hermite(degree);
     	myPolynom.println("Hermite " + degree);
		showResults(myPolynom);
     	
     	myPolynom = new Polynom().legendre(degree);
     	myPolynom.println("Legendre " + degree);
		showResults(myPolynom);
   	
     	myPolynom = new Polynom().laguerre(degree);
     	myPolynom.println("Laguerre " + degree);
		showResults(myPolynom);

     	int alpha = 2;
     	myPolynom = new Polynom().laguerre(degree, alpha);
     	myPolynom.println("Laguerre " + degree + "," + alpha);
		showResults(myPolynom);

     	myPolynom = new Polynom().chebyshev1(degree);
     	myPolynom.println("Chebyshev1 " + degree);
		showResults(myPolynom);
     	
     	myPolynom = new Polynom().chebyshev2(degree);
     	myPolynom.println("Chebyshev2 " + degree);
		showResults(myPolynom);

     	int kind = 3;
     	myPolynom = new Polynom().chebyshev(degree, kind);
     	myPolynom.println("Chebyshev " + degree + "," + kind);
		showResults(myPolynom);
     	/* */
     	
     	myPolynom = new Polynom().fromRoots("3-3i,-3+3i,2-2i,-2+2i,1-3i,-1+3i,0,3,-3,5,-5");
		showResults(myPolynom);
		
		myPolynom = new Polynom("1,10,-30.49,-445.14,199.54,5727.08,-1890.73,-21585.78,6065.64,20638.8");
		showResults(myPolynom);
		
		myPolynom = new Polynom("1,-7+8i,4+4i,-8+9i,-7+7i,-3-6i,1-8i,5-7i,-9-8i,8+7i,3+8i,6+8i,6+8i,-3-3i,2-i,-6-6i");
		showResults(myPolynom);
	}

}
