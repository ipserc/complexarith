package examenes;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.factorization.*;
import com.ipserc.arith.complex.*;

public class examen_2019_06 {

	public static void showResults(SVDfactor A) {
    	MatrixComplex S;
    	MatrixComplex V;
    	MatrixComplex U;
    	S = A.getS();
    	V = A.getV();
    	U = A.getU();
    	System.out.println("\n-------------- Solutions Section --------------");
    	A.println("A");
    	S.println("S");
    	U.println("U");
    	V.println("V");
    	System.out.println("\n---------------- Check Section ----------------");
    	(U.times(S)).times(V.adjoint()).println("U·S·V*");
    	}

	public static void showResults(Diagfactor A) {
    	MatrixComplex D;
    	MatrixComplex P;
    	D = A.D();
    	P = A.P();
    	System.out.println("\n-------------- Solutions Section --------------");
    	D.println("Diagonal Matrix");
    	P.println("Pass matrix");
    	System.out.println("\n---------------- Check Section ----------------");
    	(P.times(D)).times(P.inverse()).println("P·D·P⁻¹");
    	}

/****************************************************************************************************************
	public static void examen_2019_06_1() {
		// TODO Auto-generated constructor stub
		
		// Ejercicio 2
		MatrixComplex fMatrix = new MatrixComplex(3);
		MatrixComplex X;
		MatrixComplex T;
		
		System.out.println("_____________________________________________");
		System.out.println("________________ Ejercicio 2 ________________");
		System.out.println();
		System.out.println("Apartado (a) Determina su matriz respecto la Base canónica de R3");
		X = new MatrixComplex("2,0,0;0,3,0;0,0,1");
		T = new MatrixComplex("2,0,0;0,3,0;1,-2,2");
		X.println("Matriz de puntos");
		T.println("Matriz de transformados");
		fMatrix = T.transpose().divides(X);
		fMatrix.println("Matriz de transformación f");
		System.out.println("---- Check");
		X = new MatrixComplex("2,0,0");
		fMatrix.times(X.transpose()).transpose().println("f(2,0,0)=");
		X = new MatrixComplex("0,3,0");
		fMatrix.times(X.transpose()).transpose().println("f(0,3,0)=");
		X = new MatrixComplex("0,0,1");
		fMatrix.times(X.transpose()).transpose().println("f(0,0,1)=");
		System.out.println();
		System.out.println("Apartado (b) Estudia si es diagonalizable");		
		System.out.println("Apartado (c) Halla tantos autovectores de f linealmente independientes como sea posible.");
		fMatrix.charactPoly().println("Polinomio Caracteristico:");
		Diagfactor Ad = new Diagfactor(fMatrix);
		Ad.diagonalize();
    	showResults(Ad);
				
		/* * /
		SVDfactor Asvd = new SVDfactor(fMatrix);
    	Asvd.SVDfactorize();
    	showResults(Asvd);
    	/* * /
	}
****************************************************************************************************************/
	public static void Ejercicio2() {
		// Ejercicio 2
		MatrixComplex fMatrix = new MatrixComplex(3);
		MatrixComplex X;
		MatrixComplex T;
		
		System.out.println("_____________________________________________");
		System.out.println("________________ Ejercicio 2 ________________");
		System.out.println();
		System.out.println("Apartado (a) Determina su matriz respecto la Base canónica de R3");
		X = new MatrixComplex("2,0,0;0,3,0;0,0,1");
		T = new MatrixComplex("2,0,0;0,3,0;1,-2,2");
		X.println("Matriz de puntos X");
		T.println("Matriz de transformados T");
		System.out.println("Calculo Matriz Transformación: T.transpose().divides(X).transpose()");
		fMatrix = T.transpose().divides(X).transpose();
		fMatrix.println("Matriz de transformación f");
		System.out.println("---- Check");
		X = new MatrixComplex("2,0,0");
		X.times(fMatrix).println("f(2,0,0)=");
		X = new MatrixComplex("0,3,0");
		X.times(fMatrix).println("f(0,3,0)=");
		X = new MatrixComplex("0,0,1");
		X.times(fMatrix).println("f(0,0,1)=");
		System.out.println();
		System.out.println();
		System.out.println("Apartado (b) Estudia si es diagonalizable");		
		/* */
		System.out.println("--------------------------");
		System.out.println("---------- Diagonalización");
		System.out.println("--------------------------");
		Diagfactor Ad = new Diagfactor(fMatrix);
		Ad.diagonalize();
    	showResults(Ad);
		/* */	
		/* */
		System.out.println("----------------------------");
		System.out.println("---------- Factorización SVD");
		System.out.println("----------------------------");
		SVDfactor Asvd = new SVDfactor(fMatrix);
    	Asvd.SVDfactorize();
    	showResults(Asvd);
    	/* */
		System.out.println();
		System.out.println();
		System.out.println("Apartado (c) Halla tantos autovectores de f linealmente independientes como sea posible.");
		fMatrix.charactPoly().println("Polinomio Caracteristico:");
		MatrixComplex eigenvalues = fMatrix.eigenvalues();
		MatrixComplex eigenvectors = fMatrix.eigenvectors(eigenvalues);
		eigenvalues.println("Auto Valores");
		eigenvectors.println("Auto Vectores");
	}

	public static void Ejercicio1() {
		MatrixComplex subespacio = new MatrixComplex( "1,1,0,1;"
													+ "2,2,0,2;"
													+ "1,0,1,1;"
													+ "0,0,0,0;"
													+ "1,0,1,1");

		System.out.println("_____________________________________________");
		System.out.println("________________ Ejercicio 1 ________________");
		System.out.println();
		System.out.println("Apartado (a) Hallar la dimensión y una base de S");
		MatrixComplex base = subespacio.triangle().heap();
		base = base.removeNullRows();
		base.println("Base");
		System.out.println("Dimensión Base:" + base.rank());
		System.out.println("Apartado (b) Determinar unas ecuaciones paramétricas de S");
		System.out.println("		No implementado");
		System.out.println("Apartado (c) Determinar unas ecuaciones cartesianas de S");
		System.out.println("		No implementado");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       	Complex.setFormatON();
    	Complex.setFixedON(3);

    	Ejercicio1();
    	Ejercicio2();
	}

}
