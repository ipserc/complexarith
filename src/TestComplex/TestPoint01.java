package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.geom.*;

public class TestPoint01 {

	public static void showResults(Point pointA, Point pointB) {
		Double distance;
		int norm;
		int boxSize = 65;

		System.out.println(Complex.boxTextRandom(boxSize, "Point A & Point B"));
		pointA.println("Point A");
		pointB.println("Point B");
		distance = pointA.distance(pointB);
		System.out.printf("distance Eclidean:%f\n", distance);
		
		for (norm = 1; norm < 11; ++norm) {
			distance = pointA.distance(pointB, norm);
			System.out.printf("distance norm %d:%f\n", norm, distance);
		}		
	}
	
	public static void main(String[] args) {
		Point pointA, pointB;
		int boxSize = 65;

		System.out.println(Complex.boxTitleRandom(boxSize, "POINT TEST"));
		
		pointA = new Point("-1,-1,0");
		pointB = new Point("2,-2,0");
		showResults(pointA, pointB);
		
		pointA = new Point("-1,-1,0,5,-9");
		pointB = new Point("-3,2,3,7,6");
		showResults(pointA, pointB);
		
		pointA = new Point("2,3i,-1,4,7");
		pointB = new Point("-2,-2,1i,0,3+4i");
		showResults(pointA, pointB);
	}
}
