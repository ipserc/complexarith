package TestComplex;

import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.arith.vector.Vector;
import com.ipserc.arith.complex.*;

public class SchurCalc {

	public SchurCalc() {
		// TODO Auto-generated constructor stub
		MatrixComplex A;
		A = new MatrixComplex("5,-3,3;4,-2,3;4,-4,5");
		A = new MatrixComplex("5,-3,3;4,-2,4;4,-4,5");
		Eigenspace eigenA = new Eigenspace(A);
		eigenA.values().println("Eigenvalues");
		eigenA.vectors().println("Eigenvectors");
		MatrixComplex eiValArray = MatrixComplex.diagonal(eigenA.values());
		eiValArray.println("Eigenval Array");
		eigenA.vector(0).println("eigenA.vector(0) as MatrixComplex");
		Vector eigenVector = new Vector(eigenA.vector(0));
		eigenVector.println("eigenA.vector(0) as Vector");
		eigenA.vector(0).divides(eigenA.vector(0).norm()).println("eigenA.vector(0) as MatrixComplex Normalized");
		eigenVector.div(eigenVector.norm()).println("eigenA.vector(0) as Vector Normalized");
		System.out.println("eigenA.vector(0) Normalized Modulus=" + eigenVector.div(eigenVector.norm()).mod());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Complex.setFormatON();
		Complex.setFixedON(3);
		
		SchurCalc schur = new SchurCalc();

	}

}
