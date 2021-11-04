package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.factorization.Diagfactor;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.matrixcomplex.MatrixComplex.outputFormat;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.chronometer.Chronometer;

public class TestIllConditioned01 {

	private static void checkSol(MatrixComplex fMatrix, MatrixComplex solution) {
		MatrixComplex indTerm = fMatrix.indMatrix().transpose();
		MatrixComplex uknMatix = fMatrix.unkMatrix().transpose();
		MatrixComplex unitMatrix = new MatrixComplex(solution.rows(),1);
		unitMatrix.initMatrix(1, 0);
		
		solution.println                                                 ("Solution                                      ");
		solution.times(uknMatix).minus(unitMatrix.times(indTerm)).println("Check: solution.times(uknMatix).minus(indTerm)");
	}

	private static void solve1(Syseq fMatrix) {
		MatrixComplex solution;
		int boxSize = 65;
		
		fMatrix.print(Complex.boxTextRandom(boxSize,"Equation System"));
		
     	System.out.println(Complex.boxTextRandom(boxSize, "System Solve Commands"));
		fMatrix.printSystemEqSolve(outputFormat.MAXIMA, true);
		fMatrix.printSystemEqSolve(outputFormat.OCTAVE, true);
		fMatrix.printSystemEqSolve(outputFormat.WOLFRAM, true);
		System.out.println("OCTAVE : rank("+fMatrix.toOctave()+")");
		System.out.println("OCTAVE : rank("+fMatrix.unkMatrix().toOctave()+")");

		fMatrix.printSol(Complex.boxTextRandom(boxSize, "System Solutions"));
		switch (fMatrix.typeEqSys()) {
			case MatrixComplex.INCONSISTENT: break ;
			case MatrixComplex.DETERMINATE: {
				System.out.println("Matrix Cond Nbr:" + fMatrix.unkMatrix().cond());
				System.out.println("Matrix Cond Nbr inf:" + fMatrix.unkMatrix().cond_inf());
				System.out.println("Matrix Cond Nbr euclidean:" + fMatrix.unkMatrix().cond_f());
				System.out.println("Matrix Cond Nbr 3:" + fMatrix.unkMatrix().cond_p(3));
		     	System.out.println(Complex.boxTextRandom(boxSize, "Solutions check"));				
				checkSol(fMatrix, fMatrix.solution());
				break ;
			} 
			default: { // MatrixComplex.INDETERMINATE
		     	System.out.println(Complex.boxTextRandom(boxSize, "Solutions check"));				
				for (int i = -5; i < 6; ++i) {
					double n = i/10.0;
					System.out.println(Complex.repeat("*", 20) + " Sol nbr:" + i); 
					solution = fMatrix.solution(n);
					checkSol(fMatrix, solution);
				}		
			}
		}
	}
	public TestIllConditioned01() {
		// TODO Auto-generated constructor stub
	}

	private MatrixComplex solve3(MatrixComplex calcMatrix) {
		MatrixComplex solMatrix = new MatrixComplex(calcMatrix.nbrOfSolutions(), calcMatrix.rows());
		MatrixComplex tmpMatrix = calcMatrix.clone();
		MatrixComplex tMatrix = tmpMatrix.triangle();
		MatrixComplex unkMatrix = tmpMatrix.unkMatrix();
		MatrixComplex indMatrix = tmpMatrix.indMatrix();
		MatrixComplex newcalcMatrix = new MatrixComplex(calcMatrix.rows()-1, calcMatrix.cols()-1);
		int row, col;
		
		row = 0;
		if (tMatrix.isNullRow(tMatrix.rows()-1)) {
			solMatrix.setItem(row,tMatrix.rows()-1, Complex.ZERO);
		}
		else {
			solMatrix.setItem(row,tMatrix.rows()-1, tMatrix());
		}
		
		for (row = 0; row < newcalcMatrix.rows(); ++row) {
			for (col = 0; col < newcalcMatrix.cols()-1; ++col) {
				newcalcMatrix.setItem(row, col, tMatrix.getItem(row, col));
			}
			//System.out.println("row:" + row + " - col:" + col);
			newcalcMatrix.setItem(row, col, 
					tMatrix.getItem(row, col).minus(
							tMatrix.getItem(row, col-1).times(solMatrix.getItem(0, col))));				
		}
		newcalcMatrix.println("newcalcMatrix");
		if (newcalcMatrix.rows() > 0)
			solMatrix = solve3(calcMatrix);
		return solMatrix;
	}

	
	public static void main(String[] args) {
		MatrixComplex calcMatrix, homoMatrix;
		Syseq aMatrix;

       	Complex.setFormatON();
    	Complex.setFixedON(3);
  	
    	/** /

		calcMatrix = new MatrixComplex(
				  "-1.000,-1.000, 1.000,-1.000, 1.000, 1.000;"
				+ "-1.000,-1.000, 1.000,-1.000,-1.000,-1.000;"
				+ "-1.000,-1.000, 1.000,-1.000, 1.000, 1.000;"
				+ "-1.000, 1.000,-1.000, 1.000, 1.000, 1.000;"
				+ " 1.000,-1.000,-1.000,-1.000, 1.000, 1.000");

		homoMatrix = new MatrixComplex(
				  "-1.000,-1.000, 1.000,-1.000, 1.000, 0.000;"
				+ "-1.000,-1.000, 1.000,-1.000,-1.000, 0.000;"
				+ "-1.000,-1.000, 1.000,-1.000, 1.000, 0.000;"
				+ "-1.000, 1.000,-1.000, 1.000, 1.000, 0.000;"
				+ " 1.000,-1.000,-1.000,-1.000, 1.000, 0.000");

		System.out.println("calcMatrix.rank()="+ calcMatrix.rank());
		System.out.println("homoMatrix.rank()="+ homoMatrix.rank());

		System.out.println("calcMatrix.rank1()="+ calcMatrix.rank1());
		System.out.println("homoMatrix.rank1()="+ homoMatrix.rank1());
		
		calcMatrix.triangle().println("calcMatrix.triangle()");
		homoMatrix.triangle().println("homoMatrix.triangle()");
			
		aMatrix = new Syseq(calcMatrix); // Sist Compatible determinado
		solve1(aMatrix);

		aMatrix = new Syseq(
				 "-1,-1, 1, 1;"
				+" 0, 2,-2,-2;"
				+" 0, 0,-2, 2"); // Sist Compatible determinado
		solve1(aMatrix);


		aMatrix = new Syseq(
				  "2, 5,-3, 4;"
				+"-3, 3,-3,-1;"
				+" 1, 0,-1, 2"); // Sist Compatible determinado
		solve1(aMatrix);

		/**/
		aMatrix = new Syseq(
				 " 1276929.38, -1930737.61, -617.40, 10.00;"
				+"-1930737.61,  3316209.17, 1755.30, 20.00;"
				+"    -617.40,     1755.30,    2.00, 30.00"); 
		solve1(aMatrix);

		/** /
		aMatrix = new Syseq(
				 " 127.692938, -193.073761, -617.40, 10.00;"
				+"-193.073761,  331.620917, 1755.30, 20.00;"
				+"    -617.40,     1755.30,    2.00, 30.00"); 
		solve1(aMatrix);

		MatrixComplex solMatrix = new MatrixComplex(1, calcMatrix.rows());
	/**/

	}

}
