package examenes;

import com.ipserc.arith.complex.*;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.polynom.*;

public class ecuaciones01 {

	public static void showresults(MatrixComplex Sistema) {
		MatrixComplex Solutions;

		Sistema.println("Sistema");
		Solutions = Sistema.solve();
		Solutions.println("Soluciones");
	}

	public static void analyzesolve(MatrixComplex Sistema, Polynom Discriminante, int paramRow, int paramCol) {
		MatrixComplex SolDisc;
		Complex param;

		Discriminante.println("Discriminante"); 
		SolDisc = Discriminante.solve();
		param = SolDisc.complexMatrix[0][0];
		param.println("Parámetro m");
		param = param.minus(1);
		param.println("Parámetro < m");
		Sistema.complexMatrix[paramRow][paramCol] = param;
		showresults(Sistema);	
		param = param.plus(1);
		param.println("Parámetro = m");
		Sistema.complexMatrix[paramRow][paramCol] = param;
		showresults(Sistema);	
		param = param.plus(1);
		param.println("Parámetro > m");
		Sistema.complexMatrix[paramRow][paramCol] = param;
		showresults(Sistema);	
	}
	
	public static void main(String[] args) {
		MatrixComplex Sistema;
		Polynom Discriminante;
		
		Complex.setFormatON();

		System.out.println("Discute los siguientes sistemas según el valor del parámetro m");
		Discriminante = new Polynom("49,-245");
		Sistema = new MatrixComplex("0,7,5,-7;3,4,1,-1;7,0,5,7");		
		analyzesolve(Sistema, Discriminante, 1, 2);
		System.out.println("Discriminante = 5  Para todo m");
		Sistema = new MatrixComplex("0,1,-1,1;1,-2,1,1;3,4,-2,-3");		
		System.out.println("m = 0");
		showresults(Sistema);
		Sistema = new MatrixComplex("1,1,-1,1;1,-2,1,1;3,4,-2,-3");		
		System.out.println("m = 1");
		showresults(Sistema);

		System.out.println("Discute los siguientes sistemas según el valor del parámetro a");
		Discriminante = new Polynom("5,-38");
		Sistema = new MatrixComplex("2,-1,4,0;1,1,7,0;1,-1,12,0");		
		analyzesolve(Sistema, Discriminante, 0, 1);

		
		System.out.println("Resuelve aplicando Cramer");
		Sistema = new MatrixComplex("3,-1,0,2;2,1,1,0;0,3,2,-1");
		showresults(Sistema);	
		Sistema = new MatrixComplex("2,1,1,-2;1,-2,-3,1;-1,-1,1,-3");
		showresults(Sistema);

		System.out.println("Etudia y resuelve cuando sea posible aplicando la regla de Cramer");
		Sistema = new MatrixComplex("3,1,-1,0;1,1,1,0;0,1,-1,1");
		showresults(Sistema);
		Sistema = new MatrixComplex("1,-2,1,-2;-2,1,1,-2;1,1,-2,-2");
		showresults(Sistema);
	}

}
