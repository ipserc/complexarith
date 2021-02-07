/**
 * Una matriz cuadrada "A" se dice que es diagonalizable si es semejante a una matriz diagonal. 
 * Es decir, si mediante un cambio de base puede reducirse a una forma diagonal. 
 * En este caso, la matriz podrá descomponerse de la forma A=P·D·P⁻¹. En donde "P" es una matriz invertible cuyos 
 * vectores columna son vectores propios de A, y D es una matriz diagonal formada por los valores propios de A.
 * 
 * Si la matriz A es semejante ortogonalmente a una matriz diagonal, es decir, si la matriz P es ortogonal 
 * se dice entonces que la matriz A es diagonalizable ortogonalmente, pudiendo escribirse como A=PDP^t. 
 * El teorema espectral garantiza que cualquier matriz cuadrada simétrica con coeficientes reales es ortogonalmente diagonalizable. 
 * En este caso P está formada por una base ortonormal de vectores propios de la matriz siendo los valores propios reales. 
 * La matriz P es por tanto ortogonal y los vectores filas de P⁻¹ son los vectores columnas de P.
 * 
 * Sea A ∈ M(n×n) de (K) una matriz cuadrada con valores en un cuerpo K, se dice que la matriz A es diagonalizable si, y sólo si, 
 * A se puede descomponer de la forma:
 * 	A=P·D·P⁻¹
 * Donde:
 *	D es una matriz diagonal cuya diagonal principal está formada por los elementos de σ(A), 
 *  apareciendo cada uno tantas veces como indique su multiplicidad algebraica, siendo σ(A) 
 *  el espectro de A, es decir, el conjunto de autovalores de la matriz A:
 *  	σ(A) = {λi ∈ K | A·v = λi·v ∀ i = 1, 2,..., n} 
 *  P es la matriz cuyas columnas son los vectores que constituyen una base del subespacio 
 *  propio asociado a cada λi, siguiendo el orden establecido en D,
 *  esto es, los vectores que forman el núcleo de la matriz (A − λ i I):
 *  	P = (v1|v2|...|vn ), vj ∈ ker(A − λi·I) ∀ i, j = 1,..., n
 */
package com.ipserc.arith.factorization;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.Polynom;

/**
 * @author ipserc
 *
 */
public class Diagfactor extends MatrixComplex {
	private MatrixComplex cD;
	private MatrixComplex cP;
	private boolean factorized = false;
	private final static String HEADINFO = "Diagfactor --- INFO: ";
	private final static String VERSION = "1.0 (2020_0824_1800)";

	/*
	 * ***********************************************
	 * 	VERSION 
	 * ***********************************************
	 */
	
	/**
	 * Prints Class Version
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}

	/*
	 * 	CONSTRUCTORS 
	 */
	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public Diagfactor(String strMatrix) {
		super(strMatrix);
		diagonalize();
	}

	/**
	 * Instantiates a Diagfactor array from a MatrixComplex.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public Diagfactor(MatrixComplex matrix) {
		super();
		this.complexMatrix = matrix.complexMatrix.clone();
		diagonalize();
	}

	/*
	 * 	GETTERS 
	 */

	/**
	 * Gets the diagonal matrix
	 * @return D
	 */
	public MatrixComplex D() {
		return cD;
	}

	/**
	 * Gets the eigenvector matrix
	 * @return P
	 */
	public MatrixComplex P() {
		return cP;
	}
	

	/**
	 * Gets the class member variable with the status of the factorization.
	 * @return The factorization status.
	 */
	public boolean factorized() {
		return factorized;
	}
	
	/**
	 * Indicates if the Matrix would be or not diagonalizable 
	 * If for every eigenvalue of A, the geometric multiplicity equals the algebraic multiplicity, then A is said to be diagonalizable.
	 * @param eigenvalueArray
	 * @return true if the matrix is diagonalizable, otherwise false
	 */
	public boolean isDiagonalizable(Eigenspace eigenspace) {
		int arithMult, geomMult;
		int sumArithMult = 0;
		Complex eigenValue = eigenspace.values().getItem(0, 0);

		arithMult = eigenspace.arithmeticMultiplicity(eigenValue);
		geomMult = eigenspace.geometricMultiplicity(eigenValue);
		if (arithMult != geomMult) return false;

		sumArithMult += arithMult;
		for (int row = 1; row < this.rows(); ++row) {
			if (eigenValue.equalsred(eigenspace.values().getItem(row, 0))) continue;
			else eigenValue = eigenspace.values().getItem(row, 0);
			arithMult = eigenspace.arithmeticMultiplicity(eigenValue);
			geomMult = eigenspace.geometricMultiplicity(eigenValue);
			if (arithMult != geomMult) return false;
			sumArithMult += arithMult;
		}
		if (sumArithMult == this.rows()) return true;
		
		return false;
	}

	/**
	 * Factorizes the matrix using a diagonal matrix of eigenvectors (D) and a eigenvalue matrix (P)
	 * The factorization gives A=P·D·P⁻¹
	 */
	public void diagonalize() {
		int rowLen = this.complexMatrix.length; 
		int colLen= this.complexMatrix[0].length;
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Diagonal Matrix");
			System.exit(-1);
		}
		Eigenspace eigenspace = new Eigenspace(this);
		//MatrixComplex eigenVal = this.eigenvalues();
		//eigenVal.quicksort(0);
		
		if (!this.isDiagonalizable(eigenspace)) {
			factorized = false;
			System.out.println(HEADINFO + "The Matrix cannot be diagonalized");
			return;
		}
		else System.out.println(HEADINFO + "The Matrix CAN BE diagonalized");

		// P: Transformation Matrix, the eigenvalues in columns
		cP = eigenspace.vectors().transpose();

		// D: diagonal eigenvalues square root matrix
		cD = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < colLen; ++i)
			cD.complexMatrix[i][i] = eigenspace.values().complexMatrix[i][0];
		//cD.println("--- Diagonal");
		//(cP.times(cD).times(cP.inverse())).println("A=P·D·P⁻¹");
		factorized = true;
	}

}
