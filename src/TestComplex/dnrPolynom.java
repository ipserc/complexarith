package TestComplex;

import com.ipserc.arith.polynom.*;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;

public class dnrPolynom {

	public static void showResults(Polynom myPolynom) {
		MatrixComplex roots;
		double loLimit, upLimit;

		System.out.println("_____________________________________________________________________________________");		
		System.out.println("________________________ CALCULATE POLYNOMIAL ROOTS _________________________________");		
		myPolynom.println();
		System.out.println("Polynom:" + myPolynom.toPolynom());
		/*
		int degree = myPolynom.degree();
		double grade = Math.log(degree/10);
		int div = grade >= 10.0 ? (int)grade/10 : 1;
		roots = myPolynom.divides(div).solve();
		roots = roots.times(div);
		*/
		roots = myPolynom.solve();
		roots.quicksort(0);
		roots.println("roots");
		loLimit = myPolynom.getMinRoot(roots);		
		upLimit = myPolynom.getMaxRoot(roots);
		myPolynom.setSampleBase(6000);
		myPolynom.plot(loLimit, upLimit);
		//myPolynom.plotReIm(loLimit, upLimit);     	
     	//myPolynom.plotAbs(loLimit, upLimit);     	
     	//myPolynom.plotPhase(loLimit, upLimit);		
	}

	public static void main(String[] args) {
		Polynom myPolynom;

     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	
     	myPolynom = new Polynom();
     	
     	//myPolynom = myPolynom.fromRoots("-6,-4,-2,2,4,6");
     	//myPolynom = new Polynom("1,0,-56,0,784,0,-2304");
		//showResults(myPolynom);		
		
     	//Polynom:new Polynom("1,0,-8,0,16,0,0");
     	//Polynom:new Polynom("1,0,-40,0,528,0,-2560,0,4096,0,0");
     	//Polynom:new Polynom("1,0,-112,0,4704,0,-92416,0i,872704,0,-3612672,0i,5308416,0,0");
     	//Polynom:new Polynom("1,0,-240,0,23136,0,-1153280,0,31969536,0,-493854720,0,4042326016,0,-15476981760,0,21743271936,0,0");

     	/**/
		//myPolynom = myPolynom.fromRoots("-2,-2,2,2");
		myPolynom = new Polynom("1,0,-8,0,16");
		showResults(myPolynom);	

		myPolynom = myPolynom.fromRoots("-4,-4,-2,-2,2,2,4,4");
		//myPolynom = new Polynom("1,0,-40,0,528,0,-2560,0,4096");
		showResults(myPolynom);	

		//myPolynom = myPolynom.fromRoots("-6,-6,-4,-4,-2,-2,2,2,4,4,6,6");
		myPolynom = new Polynom("1,0,-112,0,4704,0,-92416,0,872704,0,-3612672,0,5308416");
		showResults(myPolynom);	

		//myPolynom = myPolynom.fromRoots("-8,-8,-6,-6,-4,-4,-2,-2,2,2,4,4,6,6,8,8");
		myPolynom = new Polynom("1,0,-240,0,23136,0,-1153280,0,31969536,0,-493854720,0,4042326016,0,-15476981760,0,21743271936");
		showResults(myPolynom);	
		/**/
     	
     	/*
		myPolynom = myPolynom.fromRoots("-1,-1,1,1");
		//myPolynom = new Polynom("1,0,-8,0,16");
		showResults(myPolynom);	

		myPolynom = myPolynom.fromRoots("-1,-1,-0.9,-0.9,0.9,0.9,1,1");
		//myPolynom = new Polynom("1,0,-40,0,528,0,-2560,0,4096");
		showResults(myPolynom);	

		myPolynom = myPolynom.fromRoots("-1,-1,-0.9,-0.9,-0.8,-0.8,0.8,0.8,0.9,0.9,1,1");
		//myPolynom = new Polynom("1,0,-112,0,4704,0,-92416,0,872704,0,-3612672,0,5308416");
		showResults(myPolynom);	
		*/

		myPolynom = myPolynom.fromRoots("-1,-1,-0.9,-0.9,-0.8,-0.8,-0.7,-0.7,0.7,0.7,0.8,0.8,0.9,0.9,1,1");
		//myPolynom = new Polynom("1,0,-240,0,23136,0,-1153280,0,31969536,0,-493854720,0,4042326016,0,-15476981760,0,21743271936");
		showResults(myPolynom);

    	Complex.setFixedON(1);
		myPolynom = myPolynom.fromRoots(""
				+ "-1  ,-1,"
				+ "-0.9,-0.9,"
				+ "-0.8,-0.8,"
				+ "-0.7,-0.7,"
				+ "-0,6,-0.6,"
				+ "-0.5,-0.5,"
				+ "-0.4,-0.4,"
				+ "-0.3,-0.3,"
				+ "-0.2,-0.2,"
				+ "-0.1,-0.1,"
				+ " 0.1, 0.1,"
				+ " 0.2, 0.2,"
				+ " 0.3, 0.3,"
				+ " 0.4, 0.4,"
				+ " 0.5, 0.5,"
				+ " 0,6, 0.6,"
				+ " 0.7, 0.7,"
				+ " 0.8, 0.8,"
				+ " 0.9, 0.9,"
				+ " 1  , 1");
		//myPolynom = new Polynom("1,0,-240,0,23136,0,-1153280,0,31969536,0,-493854720,0,4042326016,0,-15476981760,0,21743271936");
		//myPolynom = new Polynom("1.0,-12.0,28.7,88.1,-239.8,-293.6,831.8,589.1,-1701.1,-794.7,2320.5,762.9,-2243.8,-538.2,1590.9,284.1,-843.0,-113.3,337.1,34.2,-102.1,-7.8,23.3,1.3,-4.0,-0.2,0.5,0.0,-0.0,-0.0,0.0,0.0,-0.0,-0.0,0.0,0.0,-0.0,-0.0,0.0,0.0,-0.0,0.0,0.0");
		showResults(myPolynom);	
	}

}
