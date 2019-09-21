package TestComplex;

import com.ipserc.geom.*;
import com.ipserc.arith.complex.Complex;

public class TestPlane {
	public static void showResults(Plane planeA, Point pointP) {
		System.out.println("_________________________________________________");		
		System.out.println("___________________PLANE_POINT___________________");
		planeA.println("plane A");
		planeA.generalEq().println("  General Eq:");
		pointP.println("point P");
		System.out.printf("Distancia Punto al plano:%f\n", planeA.distance(pointP));
		planeA.projection(pointP).println("Proyección punto P sobre plano A: ");
	}
	
	public static void showResults(Plane planeA, Line lineA) {
		System.out.println("_________________________________________________");		
		System.out.println("___________________PLANE_LINE____________________");
		planeA.println("plane A");
		planeA.generalEq().println("  General Eq:");
		lineA.println("line A");
		planeA.projection(lineA.point()).println("Proyección punto linea A sobre plano A: ");
		planeA.projection(planeA.point().minus(lineA.point())).println("Proyección vector linea A sobre plano A: ");
		System.out.printf("Distancia Recta al plano:%f\n", planeA.distance(lineA));
		System.out.printf("Angulo recta plano:%f\n", planeA.angle(lineA));
		planeA.intersection(lineA).println("Intersección recta con plano: ");
	}

	public static void showResults(Plane planeA, Plane planeB) {
		Line line;
		System.out.println("_________________________________________________");		
		System.out.println("___________________TWO_PLANES____________________");
		planeA.println("plane A");
		planeA.generalEq().println("  General Eq:");
		planeB.println("plane B");
		planeB.generalEq().println("  General Eq:");
		System.out.printf("Angulo Plano A al Plano B:%f\n", planeA.angle(planeB));
		System.out.printf("Distancia Plano A al Plano B:%f\n", planeA.distance(planeB));
		line = planeA.intersection(planeB);
		line.println("line intersection");
		line = new Line(planeB.normal(), planeB.point());
		line.println("Plane B line");
		planeA.intersection(line).println("Interseccion Recta B al plano A:");
		line = new Line(planeA.normal(), planeA.point());
		line.println("Plane A line");
		planeB.intersection(line).println("Interseccion Recta A al plano B:");
	}

	public static void main(String[] args) {
		Plane planeA, planeB;
		Point pointP;
		Line lineA;

		Complex.setFormatON();
		Complex.setFixedON(3);
		
		planeA = new Plane("4,2,-4", "0,0,0.75");
		pointP = new Point("-2,0,3");
		showResults(planeA, pointP);
		
		pointP = new Point("0,-1.5,0");
		showResults(planeA, pointP);
		
		planeA = new Plane("4-3i,2,-4i,3i", "i,0,2+i,5");
		pointP = new Point("-2,0,3,5");
		showResults(planeA, pointP);
		
		planeA = new Plane("1,1,-2", "-3,0,0");
		lineA = new Line("1,1,1", "2,0,-1");
		showResults(planeA, lineA);

		planeA = new Plane("0,0,1", "0,0,0");
		lineA = new Line("1,0,1", "1,2,3");
		showResults(planeA, lineA);
		
		planeA = new Plane("1,3,-1", "-1,2,0");
		lineA = new Line("-3,0,1", "-1,2,-3");
		showResults(planeA, lineA);

		planeA = new Plane("-1,2,1", "0,1,-1");
		planeB = new Plane("3,-1,2", "1,2,-2");
		showResults(planeA, planeB);

		planeA = new Plane("1,-2,1", "0,0,-1");
		planeB = new Plane("1,-1,-1", "0,0,2");
		showResults(planeA, planeB);

		planeA = new Plane("-4,-2,3", "4,-5,-1");
		planeB = new Plane("2,-1,-3", "1,-3,2");
		showResults(planeA, planeB);

		planeA = new Plane("4,2,-4,0,2,-1,4", "0,0,0.75,2,-1,0,-3");
		pointP = new Point("-2,0,3,-1,-4,0,3");
		showResults(planeA, pointP);

		planeA = new Plane("2,-3,1", "-1,0,0");
		pointP = new Point("-1,2,0");
		showResults(planeA, pointP);

		planeA = new Plane("2,-1,3", "0,1,0");
		pointP = new Point("4,1,-3");
		showResults(planeA, pointP);
		
		planeA = new Plane("1,-2,1", "0,0,-1");
		planeB = new Plane("-3,6,-3", "3,-5,8");
		showResults(planeA, planeB);

	}
}