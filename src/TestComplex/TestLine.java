package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.geom.*;

public class TestLine {
	public static void showResults(Line line, Point point) {
		int boxSize = 65;

		System.out.println(Complex.boxText(boxSize, "Line point Test"));
		line.println("Line A:");
		point.println("point P:");
		System.out.printf("Distancia recta y punto:%f\n", line.distance(point));
		System.out.printf("Distancia2 recta y punto:%f\n", line.distance2(point));
		Point normalPoint = line.normalPoint(point);
		normalPoint.println("Normal point to line A Q:");
		Line perpendicular = line.perpendicular(point);
		perpendicular.println("Perpendicular to line A with point");
		line.direction().crossprod(perpendicular.direction()).println("Lin dir x Perp dir:");
		line.direction().dotprod(perpendicular.direction()).print("Lin dir · Perp dir:"); System.out.println();
	}

	public static void showResults(Line lineA, Line lineB) {
		int boxSize = 65;

		System.out.println(Complex.boxText(boxSize, "Two Line Test"));
		lineA.println("line A:");
		lineB.println("line B:");
		System.out.printf("distance lineA lineB:%f\n", lineA.distance(lineB));	
		System.out.printf("Angle lineA lineB:%f\n", lineA.angle(lineB));
		System.out.printf("Angle lineA lineB:%s\n", Complex.rad_DMS(lineA.angle(lineB)));
		lineA.intersection(lineB).println("Intersection:");
	}

	public static void showResults(Point pointA, Point pointB) {
		Line lineAB;
		Point point;
		int boxSize = 65;

		lineAB = new Line(pointA, pointB);
		System.out.println(Complex.boxText(boxSize, "Two Points Line Test"));
		pointA.println("point A:");
		pointB.println("point B:");
		lineAB.println("line AB:");
		System.out.println("--- Calculus of the line's points ---");
		for (int l = -2; l <= 2; ++l)
			lineAB.point(l).println("point("+l+"):");
	}

	public static void main(String[] args) {
		//Complex.setFixedON(3);
		Complex.setFormatON();
		Line lineA;
		Line lineB;
		Point pointA;
		Point pointB;
		int boxSize = 65;

		System.out.println(Complex.boxTitle(boxSize, "LINE & POINT CLASS TEST"));
		
		lineA = new Line("3,-2", "1,1");
		Point point = new Point("1,3");
		showResults(lineA, point);

		lineA = new Line("4,-3", "0,0");
		point = new Point("2,-1");
		showResults(lineA, point);

		lineA = new Line("4,-3,1,3", "6,-2,3,-2");
		point = new Point("2,-1,0,8");
		showResults(lineA, point);

		lineA = new Line("2,1,-2", "2,1,-1");
		point = new Point("1,-2,-3");
		showResults(lineA, point);
		
		lineA = new Line("3,-2", "1,1");
		lineB = new Line("6,-4", "0,0");
		showResults(lineA, lineB);

		lineA = new Line("3,-2", "1,1");
		lineB = new Line("4.00001,6", "0,0");
		showResults(lineA, lineB);
		
		lineA = new Line("2,1,-2", "2,1,-1");
		lineB = new Line("-2,5,0", "-2,1,1");
		showResults(lineA, lineB);

		lineA = new Line("2,1,-2", "2,1,-1");
		lineB = new Line("-4,-2,4", "-2,0,5");
		showResults(lineA, lineB);

		lineA = new Line("2,1,-2", "2,1,-1");
		lineB = new Line("-4,-2,4", "-2,0,5");
		showResults(lineA, lineB);

		lineA = new Line("2,1,-2,3,0,0,0", "2,1,-1,3,0,0,0");
		lineB = new Line("-2,5,0,-6,0,0,0", "2,1,-2,0,-2,0,0");
		showResults(lineA, lineB);

		System.out.println(Complex.boxTitle(boxSize, "TWO POINT LINE TEST"));

		pointA = new Point("-5,+7");
		pointB = new Point("-9,-4");
		showResults(pointA, pointB);

		pointA = new Point("4,-1");
		pointB = new Point("5,2");
		showResults(pointA, pointB);

		pointA = new Point("3+2i,-5+4i");
		pointB = new Point("-3+4i,3-7i");
		showResults(pointA, pointB);

		pointA = new Point("-5+7i,-3i");
		pointB = new Point("-9-4i,+4");
		showResults(pointA, pointB);

		pointA = new Point("-5,7,3");
		pointB = new Point("-9,-4,0");
		showResults(pointA, pointB);

		pointA = new Point("3,29,9");
		pointB = new Point("-9,-4,0");
		showResults(pointA, pointB);

		pointA = new Point("-5,7,3,-1");
		pointB = new Point("-9,-4,0,2");
		showResults(pointA, pointB);
	
	}
}
