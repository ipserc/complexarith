/**
 * 
 */
package TestComplex;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.*;
import com.panayotis.gnuplot.JavaPlot;

/**
 * @author ipserc
 *
 */
public final class TestSpline02 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<MatrixComplex> pointsList = new ArrayList<MatrixComplex>();
		MatrixComplex points;
		MatrixComplex pTable;
		int sigDigits = 8;
		Polynom interpol = new Polynom(2);
		Complex lolimit;
		Complex uplimit;

		Complex.setFormatON();
		Complex.exact(false);
		Complex.setScientificON(sigDigits);
		//Complex.setFixedON(5);
		Polynom.sampleBase = 500;

		Spline.version();
		
		/**/
		pTable = new MatrixComplex("1,2.9; 2,1.3; 3,2.4; 4,1.9; 5,1.0; 6,2.1");
		//pTable = new MatrixComplex("1,3; 2,1; 3,2; 4,2; 5,1; 6,2");
		//pTable = new MatrixComplex("-2, 1.25; -0.5, 0.6; 0.5, 1; 1, 2; 2, 0; 4, -2; 6, -5.25; 7, -1.75; 8.8, 0.75; 10, 2");
		//pTable = new MatrixComplex("-2, 1.25; -0.5, 0.6; 0.5, 1; 1, 2; 2, 0; 4, -2; 6, -5.25; 7, -1.75; 8.8, 0.75; 10, 2");
		//pTable = new MatrixComplex("2,-1;3,2;5,-7;6,2");
		//pTable = new MatrixComplex("1, 5.7; 2, 4.7; 3,10.0; 4, 12.3; 5, 6.6; 6, 19.7; 7, 23.8; 8, 22.4; 9, 18.4; 10, 16.3; 11, 9.8; 12, 5.3"); // Temperatutas mes medias Madrid 1963
		//pTable = new MatrixComplex("2.3,6.38512;2.7,13.6218;2.9,18.676;3.2,28.2599;3.5,40.4082;3.7,49.9945"); // Temperatura en función de la presión.
		//pTable = new MatrixComplex("-1, 9.8; 0, 5.3; 1, 5.7; 2, 4.7; 3,10.0; 4, 12.3; 5, 6.6; 6, 19.7; 7, 23.8; 8, 22.4; 9, 18.4; 10, 16.3; 11, 9.8; 12, 5.3; 13, 5.7; 14, 4.7");
		//pTable = new MatrixComplex("1,3-2i; 2,1+i; 3,2-3i; 4,2-i; 5,1; 6,2+i");
		
		/*
		 * line: 
		  direction: ( 2.0-4.0i )
		  point: [ 2.0+1.0i ]
			Start Point: 2.0+1.0i
			End Point  : 4.0-3.0i
			point( 0.0): 2.0+1.0i
			point( 1.0): 2.2+0.6i
			point( 2.0): 2.4+0.2i
			point( 3.0): 2.6-0.2i
			point( 4.0): 2.8-0.6i
			point( 5.0): 3.0-1.0i
			point( 6.0): 3.2-1.4i
			point( 7.0): 3.4-1.8i
			point( 8.0): 3.6-2.2i
			point( 9.0): 3.8-2.6i
			point(10.0): 4.0-3.0i
		 */
		/* * /
		pTable = new MatrixComplex(""
				+ "2.0+1.0i ,3-2i; "
				+ "2.2+0.6i ,1+i; "
				+ "2.8-0.6i ,2-3i; "
				+ "3.2-1.4i ,2-i; "
				+ "3.6-2.2i ,1; "
				+ "4.0-3.0i ,2+i");
		/* */
		//pTable = new MatrixComplex("-5, 1; -4, -0.6; -3, 1; -2, -1; -1, 1; 0, -0.7; 1, 1; 2, -0.5; 3, 1; 4, -0.3; 5, 1");
		//pTable = new MatrixComplex("-5, 1; -4, -0.6; -3, 1.1; -2, -1; -1, 0.9; 0, -0.7; 1, 1.05; 2, -0.5; 3, 1.2; 4, -0.3; 5, .9");
		//pTable = new MatrixComplex("-2, 1; 0, -0.7;2, 0.5");

		pTable.println("Points");
		/* */
		Spline interpolSpline3 = new Spline(3, pTable);
		interpolSpline3.interpolate(true);
		interpolSpline3.print();
		interpolSpline3.eval(new Complex(1.7)).println("Spline3[1.7]: ");
		points = interpolSpline3.walkInterval();
		//points.println("Spline(3, pTable)");
		pointsList.add(points);
		/* */

		/* * /
		interpol = new Spline(1, pTable);
		interpol.interpolate();
		interpol.print();
		points = interpol.walkInterval();
		pointsList.add(points);
		/* */

		/* */
		Polynom interpolNewton = new Polynom();
		interpolNewton = interpolNewton.interpolationNewton(pTable);
		// System.out.println(interpolNewton.coefMatrix());
		interpolNewton.println("Newton Interpolation Polynom f(x)=");
		System.out.println(interpolNewton.toWolfram_poly("Newton Interpolation Polynom f[x_]:="));
		interpolNewton.eval(new Complex(1.7)).println("f[1.7]: ");
		lolimit = pTable.getItem(0,0).copy();
		uplimit = pTable.getItem(pTable.rows()-1,0).copy();
		//interpolNewton.plotExpression(lolimit.rep(), uplimit.rep());
		points = interpolNewton.walkInterval(lolimit, uplimit);
		pointsList.add(points);
		/* */
		
		/*
		 * Replicación del polinomio anterior a partir de sus coeficientes para visualizar el error
		 */
		/** /
		Polynom poly = new Polynom(interpolNewton.coefMatrix().toString().replace("[", "").replace("]", ""));
		poly.println("Replica Newton Interpolation Polynom g(x)=");
		System.out.println(poly.toWolfram_poly("Replica Newton Interpolation Polynom g[x_]:="));
		poly.eval(new Complex(5)).println("Replica g[5]: ");
		lolimit = pTable.getItem(0,0).copy();
		uplimit = pTable.getItem(pTable.rows()-1,0).copy();
		points = poly.walkInterval(lolimit, uplimit);
		pointsList.add(points);
		/**/

		/* * /
		Polynom interpolVandermonde = new Polynom();
		interpolVandermonde = interpolVandermonde.interpolationVandermonde(pTable, true);
		interpolVandermonde.println("Vandermonde Interpolation Polynom V(x)=");
		System.out.println(interpolVandermonde.toWolfram_poly("Vandermonde Interpolation Polynom V[x_]:="));
		interpolVandermonde.eval(new Complex(1.7)).println("V[1.7]: ");
		points = interpolVandermonde.walkInterval(lolimit, uplimit);
		pointsList.add(points);
		/* */
		
		/*
		 * La elección de los nodos y los polinomios de Chebyshev
		 * a partir de la curva de interpolación por Splines
		 */
		/**/
		MatrixComplex pTableCheby = interpolSpline3.chebyshevNodes(pTable);
		pTableCheby.println("Points Chebyshev");
		Polynom interpolNewtonCheby = new Polynom();
		interpolNewtonCheby = interpolNewtonCheby.interpolationNewton(pTableCheby);
		// System.out.println(interpolNewton.coefMatrix());
		interpolNewtonCheby.println("Newton Cheby Interpolation Polynom f(x)=");
		System.out.println(interpolNewtonCheby.toWolfram_poly("Newton Cheby Interpolation Polynom f[x_]:="));
		interpolNewtonCheby.eval(new Complex(1.7)).println("f[1.7]: ");
		lolimit = pTable.getItem(0,0).copy();
		uplimit = pTable.getItem(pTable.rows()-1,0).copy();
		//interpolNewton.plotExpression(lolimit.rep(), uplimit.rep());
		points = interpolNewtonCheby.walkInterval(lolimit, uplimit);
		pointsList.add(points);
		
		Complex.setScientificON(3);
		pTableCheby.println("Points Chebyshev");
		interpolNewtonCheby.println("Newton Cheby Interpolation Polynom f(x)=");
		System.out.println(interpolNewtonCheby.toWolfram_poly("Newton Cheby Interpolation Polynom f[x_]:="));

		/**/
		
		/* */
		interpol.plotRe(pointsList, "Re Polynomial Comparison Exact and Approx "+sigDigits+" Significatives Digits");
		interpol.plotIm(pointsList, "Im Polynomial Comparison Exact and Approx "+sigDigits+" Significatives Digits");
		interpol.plotMod(pointsList, "Mod Polynomial Comparison Exact and Approx "+sigDigits+" Significatives Digits");
		interpol.plotPha(pointsList, "Pha Polynomial Comparison Exact and Approx "+sigDigits+" Significatives Digits");
		/* */
		

	}
}
