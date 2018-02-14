package examenes;

import com.ipserc.arith.matrixcomplex.*;

public class ejercicios01 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MatrixComplex A = new MatrixComplex("2,3;1,2");
		MatrixComplex C = new MatrixComplex("1,1;2,3");
		MatrixComplex X;
		MatrixComplex Ainv; Ainv = A.inverse();
		
		A.println("Matriz A");
		C.println("Matriz C");
		
		System.out.println("Calcular Matriz X tal que A·X·A = C");
		System.out.println("X = Ainv·C·Ainv");
		Ainv.println("Matriz Ainv");
		X = Ainv.times(C);
		X.println("Ainv·C");		
		X = X.times(Ainv);
		X.println("X=Ainv·C·Ainv");
		System.out.println("---------------------------------");	
		System.out.println("Resolvemos mediante el sistema de ecuaciones");
		System.out.println("Calcular A·X·A = C");
		System.out.println("[3.0*x4+6.0*x3+2.0*x2+4.0*x1,	6.0*x4+9.0*x3+4.0*x2+6.0*x1]");
		System.out.println("[2.0*x4+4.0*x3+1.0*x2+2.0*x1,	4.0*x4+6.0*x3+2.0*x2+3.0*x1]");
		MatrixComplex sistEcs = new MatrixComplex("4,2,6,3,1;6,4,9,6,1;2,1,4,2,2;3,2,6,4,3");
		sistEcs.println("Sistema de Ecuaciones");
		sistEcs.solve().println("Soluciones");;
		/*
		MatrixComplex sistSols;
		sistSols = sistEcs.solve();
		sistSols.println("Soluciones");
		*/
	}

}
