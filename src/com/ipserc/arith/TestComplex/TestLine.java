package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.geom.*;

public class TestLine {
	public static void showResults(Line line, Point point) {
		System.out.println("_________________________________________________");		
		System.out.println("_________________LINE_POINT______________________");
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
		System.out.println("_________________________________________________");		
		System.out.println("__________________TWO_LINES______________________");
		lineA.println("line A:");
		lineB.println("line B:");
		System.out.printf("distance lineA lineB:%f\n", lineA.distance(lineB));	
		System.out.printf("Angle lineA lineB:%f\n", lineA.angle(lineB));
		lineA.intersection(lineB).println("Intersection:");
	}
	
	public static void main(String[] args) {
		//Complex.setFixedON(3);
		Complex.setFormatON();
		Line lineA;
		Line lineB;
		
		lineA = new Line("3,-2", "1,1");
		Point point = new Point("1,3");
		showResults(lineA, point);
		lineB = new Line("6,-4", "0,0");
		showResults(lineA, lineB);
		lineB = new Line("4.00001,6", "0,0");
		showResults(lineA, lineB);
		
		lineA = new Line("4,-3", "0,0");
		point = new Point("2,-1");
		showResults(lineA, point);

		lineA = new Line("4,-3,1,3", "6,-2,3,-2");
		point = new Point("2,-1,0,8");
		showResults(lineA, point);

		lineA = new Line("2,1,-2", "2,1,-1");
		point = new Point("1,-2,-3");
		showResults(lineA, point);
		
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
		
		Point pointA = new Point("3+2i");
		Point pointB = new Point("-3+4i");
		lineA = new Line(pointA, pointB);
		System.out.println("_________________________________________________");		
		System.out.println("__________________TWO_POINTS_____________________");
		lineA.println("line A:");
		
	}
}
