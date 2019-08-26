package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.vector.Vector;

public class matesJosete {
	
	public static void ej_2018_0917() {
		System.out.println("------------------------ 2018/09/17  ------------------------");
		System.out.println("------------------------ EJERCICIO 1 ------------------------");
		MatrixComplex matrixA = new MatrixComplex("1,1,1;1,2,-1;2,1,0");
		matrixA.println("Matrix A");
		MatrixComplex matrxAdj = matrixA.adjugate();
		matrxAdj.println("Matriz Adjunta");
		Complex det = matrixA.determinant();
		det.println("Determinante:");
		MatrixComplex matrxInv = matrxAdj.divides(det);
		matrxInv.println("matrxInv = Adjunta/determinante");
		matrixA.inverse().println("matrixA.inverse()");
	}

	public static void ej_2018_0924() {
		System.out.println("------------------------ 2018/09/24  ------------------------");
		System.out.println("------------------------ EJERCICIO 1 ------------------------");
		MatrixComplex matrixA = new MatrixComplex("2,0,1;0,1,-2;3,-3,0;0,0,8");
		matrixA.println("Matrix A");
		int rank = matrixA.rank();
		System.out.printf("Rank(Matrix A)=%d\n", rank);	
	}

	public static void ej_2018_1001() {
		System.out.println("------------------------ 2018/10/01  ------------------------");
		System.out.println("------------------------ EJERCICIO 1 ------------------------");
		MatrixComplex base = new MatrixComplex();
		base.base("1,1,0;0,2,1;1,0,1");
		base.println("Base");
		Vector vector = new Vector("2,-3,1");
		vector.println("vector");
		vector.baseChg(base).println("Vector en base");
		System.out.println("------------------------ EJERCICIO 2 ------------------------");
		base.base("2,0,2,9;1,2,1,3;1,0,1,3;2,4,2,6");
		base.println("Base");
		vector = new Vector("2,-3,1,-1");
		vector.println("vector");
		vector.baseChg(base).println("Vector en base");
		int rank = base.rank();
		System.out.printf("Rank(base)=%d\n", rank);			
		System.out.println("------------------------ EJERCICIO 3 ------------------------");
		MatrixComplex matrixA = new MatrixComplex("4,-1,0;2,1,-3;14,1,-9");
		matrixA.println("Matrix A");		
		rank = matrixA.rank();
		System.out.printf("Rank(Matrix A)=%d\n", rank);	
		matrixA = matrixA.transpose().subMatrix("0,1", "0,1,2,");
		matrixA.println("Sub Matrix A transpose");		
		matrixA.solve().println("Coeficientes C.L.");
	}

	public static void ej_2018_1002() {
		System.out.println("------------------------ 2018/10/02  ------------------------");
		System.out.println("------------------------ EJERCICIO 1 ------------------------");
		MatrixComplex subEspacio = new MatrixComplex("1,-1,1,2;1,3,-2,1");
		subEspacio.println("Subespacio");
		int rank = subEspacio.rank();
		System.out.printf("Rank(subEspacio)=%d\n", rank);
		Vector subVectorA = new Vector(); 
		subVectorA.complexMatrix = subEspacio.subMatrix("0", "0,1,2").complexMatrix.clone();
		Vector subVectorB = new Vector();
		subVectorB.complexMatrix = subEspacio.subMatrix("1", "0,1,2").complexMatrix.clone();
		MatrixComplex vectorA = subVectorA.crossprod(subVectorB);
		vectorA.println("Coeficientes x, y, z");
		subVectorA.complexMatrix = subEspacio.subMatrix("0", "0,1,3").complexMatrix.clone();
		subVectorB.complexMatrix = subEspacio.subMatrix("1", "0,1,3").complexMatrix.clone();
		vectorA = subVectorA.crossprod(subVectorB);
		vectorA.println("Coeficientes x, y, t");
  		System.out.println("------------------------ EJERCICIO 3 ------------------------");
		MatrixComplex base = new MatrixComplex();
		base.base("1,1,0;1,0,1;0,1,1");
		base.println("Base");
		Vector vector = new Vector("2,0,0");
		vector.println("vector");
		vector.baseChg(base).println("Vector en base");
		vector = new Vector("0,2,0");
		vector.println("vector");
		vector.baseChg(base).println("Vector en base");
	}
	
	public static void ej_2018_1009() {
		System.out.println("------------------------ 2018/10/09  ------------------------");
		System.out.println("------------------------ EJERCICIO 1 ------------------------");
		MatrixComplex base = new MatrixComplex();
		Vector vector = new Vector();
		base.base("1,1,;1,-1");
		base.println("Base");
		vector = new Vector("5,3");
		vector.println("vector");
		vector.baseChg(base).println("Vector en base");
		System.out.println("------------------------ EJERCICIO 2 ------------------------");
		MatrixComplex subEspacio = new MatrixComplex("4,4;-1,1;2,9");
		base = subEspacio.transpose();
		base.println("Base");
		System.out.println("------------------------ EJERCICIO 3 ------------------------");
		subEspacio = new MatrixComplex("2,0,3;1,2,0");
		base.base("1,1,0;0,1,1;0,0,1");
		subEspacio.println("subEspacio");
		base.println("Base");
		subEspacio.times(base.transpose()).println("Matriz Cambio base");
		System.out.println("------------------------ EJERCICIO 4 ------------------------");
		subEspacio = new MatrixComplex("1,0;0,1");
		base.base("1,1;1,-1");
		subEspacio.println("subEspacio");
		base.println("Base");
		subEspacio.times(base.transpose()).println("Matriz Cambio base");
		System.out.println("------------------------ EJERCICIO 5 ------------------------");
		subEspacio = new MatrixComplex("1,0;0,1");
		base.base("1,0;0,1");
		subEspacio.println("subEspacio");
		base.println("Base");
		subEspacio.times(base.transpose()).println("Matriz Cambio base");	
	}

	public static void ej_2018_1016() {
		System.out.println("------------------------ 2018/10/16  ------------------------");
		System.out.println("------------------------ EJERCICIO 1 ------------------------");
		Complex.setFixedON(3);
		Diagfactor matrixA = new Diagfactor("3,1,5;0,7,0;0,0,7");
		matrixA.println("Matrix A");
		matrixA.determinant().println("Det(A)=");
		matrixA.diagonalize();
		matrixA.P().println("P");
		matrixA.D().println("D");
		matrixA.P().inverse().println("P⁻¹");
		matrixA.P().determinant().println("Det(P)=");
		matrixA.P().times(matrixA.D().times(matrixA.P().inverse())).println("A=P·D·P⁻¹");
		
		Complex.setFixedOFF();
	}
	
	public static void main(String[] args) {
		Complex.setFormatON();
		ej_2018_0917();
		ej_2018_0924();
		ej_2018_1001();
		ej_2018_1002();
		ej_2018_1009();
		ej_2018_1016();
	}
}
