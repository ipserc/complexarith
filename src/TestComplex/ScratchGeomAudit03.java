package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.geom.Line;
import com.ipserc.arith.geom.Plane;
import com.ipserc.arith.geom.Point;

/**
 * Auditoria matematica dedicada de geom.* (Vigesimosexta sesion, bloque 6 de la hoja de ruta
 * "Matematicas Aplicadas"), continuacion de ScratchGeomAudit01/02.java (Decimoctava sesion).
 * Cubre terreno NO examinado en las 2 pasadas anteriores -- no solo lectura de codigo.
 */
public class ScratchGeomAudit03 {

	static int pass = 0, fail = 0;

	static void checkClose(String label, double actual, double expected, double tol) {
		double err = Math.abs(actual - expected);
		boolean ok = err < tol;
		if (ok) ++pass; else ++fail;
		System.out.printf("%-70s %-4s actual=%.8f expected=%.8f err=%.3e%n", label, ok ? "OK" : "***FAIL***", actual, expected, err);
	}

	public static void main(String[] args) {
		Complex.setFormatOFF();

		System.out.println("=== Point: distance() == norma euclidea manual ===");
		checkClose("distance((0,0,0),(3,4,0)) == 5", new Point("0,0,0").distance(new Point("3,4,0")), 5.0, 1e-9);

		System.out.println("\n=== Line: distancia punto-recta, caso conocido a mano ===");
		Line xAxis = new Line("1,0,0", "0,0,0");
		checkClose("distance(recta eje X, punto (0,5,0)) == 5", xAxis.distance(new Point("0,5,0")), 5.0, 1e-9);

		System.out.println("\n=== Line: angle(Line) -- dos rectas ORTOGONALES en dimension 8 (mas alla del limite 7D de crossprod) ===");
		Line l1 = new Line("1,0,0,0,0,0,0,0", "0,0,0,0,0,0,0,0");
		Line l2 = new Line("0,1,0,0,0,0,0,0", "0,0,0,0,0,0,0,0");
		double angle8D = l1.angle(l2);
		System.out.println("angle(e1,e2) en R^8 = " + angle8D + " (esperado pi/2=" + (Math.PI / 2) + ")");
		checkClose("angle(Line) en dim=8 (ortogonales) == pi/2", angle8D, Math.PI / 2, 1e-6);
		double angle8DRef = l1.direction().angle(l2.direction());
		checkClose("angle(Line) dim=8 coincide con VectorComplex.angle() (referencia independiente)", angle8D, angle8DRef, 1e-6);

		System.out.println("\n=== Line: angle(Line) -- dos rectas NO ortogonales en dimension 8 ===");
		Line l3 = new Line("1,1,0,0,0,0,0,0", "0,0,0,0,0,0,0,0");
		Line l4 = new Line("1,0,1,0,0,0,0,0", "0,0,0,0,0,0,0,0");
		double angle8Db = l3.angle(l4);
		System.out.println("angle(d1,d2) en R^8 = " + angle8Db + " (esperado pi/3=" + (Math.PI / 3) + ", <d1,d2>=1, |d1|=|d2|=sqrt(2) -> cos=1/2)");
		checkClose("angle(Line) en dim=8 (60 grados) == pi/3", angle8Db, Math.PI / 3, 1e-6);
		double angle8DbRef = l3.direction().angle(l4.direction());
		checkClose("angle(Line) dim=8 no-ortogonal coincide con VectorComplex.angle()", angle8Db, angle8DbRef, 1e-6);

		System.out.println("\n=== Plane: proyeccion/distancia, caso conocido a mano ===");
		Plane xyPlane = new Plane("0,0,1", "0,0,0");
		checkClose("distance(plano XY, punto (1,2,3)) == 3", xyPlane.distance(new Point("1,2,3")), 3.0, 1e-9);

		System.out.println("\n=== Plane: distance(Plane) -- planos NO paralelos, exactos ===");
		Plane xzPlane = new Plane("0,1,0", "0,0,0");
		checkClose("distance(plano XY, plano XZ) == 0 (no paralelos, se cortan)", xyPlane.distance(xzPlane), 0.0, 1e-9);

		System.out.println("\n=== Plane: intersection(Plane) -- dos planos que se cortan, la recta resultante esta en ambos ===");
		Line interLine = xyPlane.intersection(xzPlane);
		Point onLine = interLine.point(1.0);
		checkClose("interseccion(XY,XZ) esta en el plano XY (distancia 0)", xyPlane.distance(onLine), 0.0, 1e-9);
		checkClose("interseccion(XY,XZ) esta en el plano XZ (distancia 0)", xzPlane.distance(onLine), 0.0, 1e-9);

		System.out.println("\n=== Plane: intersection(Line) -- recta que atraviesa el plano ===");
		Line vertical = new Line("0,0,1", "2,3,-5");
		Point crossPt = xyPlane.intersection(vertical);
		checkClose("interseccion(recta vertical, plano XY): x==2", crossPt.getRow(0).getItem(0, 0).rep(), 2.0, 1e-9);
		checkClose("interseccion(recta vertical, plano XY): y==3", crossPt.getRow(0).getItem(0, 1).rep(), 3.0, 1e-9);
		checkClose("interseccion(recta vertical, plano XY): z==0", crossPt.getRow(0).getItem(0, 2).rep(), 0.0, 1e-9);

		System.out.println("\n===================================");
		System.out.println("TOTAL: " + pass + " OK, " + fail + " FAIL");
	}
}
