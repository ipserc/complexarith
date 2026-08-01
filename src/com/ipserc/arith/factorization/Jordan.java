package com.ipserc.arith.factorization;

import com.ipserc.arith.matrixcomplex.Eigenspace;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.complex.Complex;

public class Jordan extends Eigenspace {

	private final static String HEADINFO = "Jordan --- INFO:";
	private final static String VERSION = "1.1 (2026_0801_0954)";
	/* VERSION Release Note
	 *
	 * 1.1 (2026_0801_0954)
	 * Compile fix: values()/vectors() no longer exist in Eigenspace, replaced with roots()/
	 * solutions() (NOT eigenvalues()/eigenvectors(), which have a different row count/ordering --
	 * see the comment in factorize()).
	 * Bug fix: appendRows() returns a NEW MatrixComplex, doesn't mutate in place -- 4 call sites
	 * (eigenvectors(), factorize()) called it as a bare statement, discarding the result, crashing
	 * with ArrayIndexOutOfBoundsException for any defective eigenvalue.
	 * Bug fix: block()'s superdiagonal condition checked global matrix bounds instead of "still
	 * inside this eigenvalue's own chain" -- silently corrupted the Jordan matrix (wrong result,
	 * not even NaN) whenever a simple eigenvalue (arithMult=1) wasn't the last one processed.
	 * Verified with P*J*P^-1 reconstruction: correct for every eigenvalue with geometric
	 * multiplicity exactly 1. KNOWN LIMITATION, documented not fixed: eigenvalues with geometric
	 * multiplicity greater than 1 (needing more than one Jordan block) are still built wrong --
	 * see factorize()'s own Javadoc.
	 *
	 * 1.0 (2020_0627_1130)
	 */

	private MatrixComplex cJ;
	private MatrixComplex cP;
	private boolean factorized = false;

	/*
	 * 	CONSTRUCTORS 
	 */
	/**
	 * Instantiates a complex square array of length len.
	 * @param len The length of the square array.
	 */
	/*
	public Jordan(int len) {
		super(len);
	}
	*/
	
	/**
	 * Instantiates a complex array from a string, rows are separated with ";", cols are separated with ",".
	 * @param strMatrix the string with the rows and columns.
	 */
	public Jordan(String strMatrix) {
		super(strMatrix);
	}

	/**
	 * Instantiates a Diagfactor array from a MatrixComplex.
	 * @param matrix the MatrixComplex already instantiated.
	 */
	public Jordan(MatrixComplex matrix) {
		super(matrix);
		//this.complexMatrix = matrix.complexMatrix.clone();
	}

	/*
	 * 	GETTERS 
	 */

	/**
	 * Gets the diagonal matrix
	 * @return J
	 */
	public MatrixComplex J() {
		return cJ;
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
	 * 
	 * @param order
	 * @param arithMult
	 * @param eigenValue
	 * @return
	 */
	private MatrixComplex block(int order, int arithMult, Complex eigenValue) {
		MatrixComplex block = new MatrixComplex(this.rows(), this.cols());
		block.initMatrix(0, 0);
		// The superdiagonal 1 must connect consecutive rows WITHIN this eigenvalue's own chain
		// only (i+1 < arithMult), never past its last row. The previous condition
		// (row+1 < this.cols()-1) checked global matrix bounds instead, unrelated to which
		// eigenvalue's block a given row belongs to -- it happened to look right whenever the
		// last-processed eigenvalue's block landed at the tail of the matrix (purely a
		// coincidence of position), but inserted a spurious superdiagonal 1 after a genuinely
		// simple eigenvalue (arithMult=1, no chain to continue) whenever that eigenvalue wasn't
		// last, silently corrupting the Jordan matrix (confirmed with "0,3,1;2,-1,-1;-2,-1,-1":
		// eigenvalue 2 has arithMult=1 and is processed first, so row+1 < this.cols()-1 was true
		// and wrongly added a 1 next to it -- P*J*P^-1 no longer reconstructed the original
		// matrix, silently wrong, not even NaN).
		for (int i = 0, row = order; i < arithMult; ++i, ++row) {
			block.setItem(row, row, eigenValue);
			if (i+1 < arithMult) block.setItem(row, row+1, Complex.ONE);
		}
		return block;
	}

	/**
	 * Calculates the eigenvector or characteristic vector using as generating space (M-lI)^a 
	 * where M is the coef matrix, l is the eigenvalue, I is the identity matrix and a i the power to raise the  
	 * @param eigenval The eigenvalue to calculate the eigenvector
	 * @param arithMult The arithmetic multiplicity
	 * @return A MatrixComplex with the eigenvectors
	 */
	public MatrixComplex eigenvectors(Complex eigenval, int arithMult) {
		int order = arithMult;
		MatrixComplex I = this.eye();
		MatrixComplex eigenVect = new MatrixComplex(0,0);
		MatrixComplex cMatrix;
		MatrixComplex sols;
		
		// appendRows() returns a NEW MatrixComplex, it does not mutate the receiver -- the 4
		// call sites below used to call it as a bare statement, discarding the result, so
		// eigenVect/cP never actually grew (confirmed with a direct repro: eigenVect stayed at
		// 0 rows through the whole loop, crashing with ArrayIndexOutOfBoundsException at the
		// eigenVect.getRow(...) below). Fixed by reassigning, same pattern already used
		// correctly elsewhere in the project (Examenes/FR_2014_0629_espacio_vectorial_cociente.java).
		for (order = arithMult; order > 1; --order) {
			cMatrix = ((this.minus(I.times(eigenval))).power(order)).augment(); //.heap();
			cMatrix.println("------------------[f-I]^" + order);
			sols = cMatrix.solve();
			eigenVect = eigenVect.appendRows(sols.getRow(0));
		}
		cMatrix = ((this.minus(I.times(eigenval))).power(order)).augment(); //.heap();
		cMatrix.println("------------------[f-I]^" + order);
		if (arithMult > 1) {
			sols = cMatrix.unkMatrix().times(eigenVect.getRow(arithMult-order-1).transpose()).transpose();
			eigenVect = eigenVect.appendRows(sols);
			eigenVect.transrow();
		}
		else {
			cMatrix = (this.minus(I.times(eigenval))).augment(); //.heap();
			cMatrix.println("------------------[f-I]^" + order);
			sols = cMatrix.solve();
			eigenVect = eigenVect.appendRows(sols);
		}
		return eigenVect;
	}

	/**
	 * 
	 * @param eigenValArray
	 * @return
	 */
	public MatrixComplex jordanForm(MatrixComplex eigenValArray) {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		MatrixComplex jordanForm = new MatrixComplex(rowLen, colLen);
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			MatrixComplex jordanBlock = this.block(i, arithMult, eigenval);
			jordanForm = jordanForm.plus(jordanBlock);
			i += arithMult;
		}
		return jordanForm;
	}

	/**
	 * Factorizes the matrix using a diagonal matrix of eigenvectors (D) and a eigenvalue matrix (P)
	 * The factorization gives A=P·J·P⁻¹
	 * <p>
	 * <b>KNOWN LIMITATION, documented not fixed:</b> verified correct (P·J·P⁻¹ reconstructs the
	 * original matrix exactly) only for eigenvalues with geometric multiplicity EXACTLY 1 (a
	 * repeated eigenvalue needing at most one Jordan chain) -- this includes the non-repeated
	 * case (algebraic multiplicity 1) trivially. {@link #jordanForm(MatrixComplex)}/{@link
	 * #block(int, int, Complex)} always build a single chain of length {@code arithMult} and have
	 * no notion of geometric multiplicity at all, so ANY eigenvalue with geometric multiplicity
	 * greater than 1 is built wrong: confirmed with an eigenvalue of algebraic multiplicity 2 and
	 * geometric multiplicity 2 (fully diagonalizable for that eigenvalue -- should be two separate
	 * 1x1 blocks, no superdiagonal 1 at all) getting a spurious {@code [[eigval,1],[0,eigval]]}
	 * chain instead, making {@code P} singular ({@code P.inverse()} gives {@code NaN}/{@code
	 * Infinity}); and with a single eigenvalue of algebraic multiplicity 5 and geometric
	 * multiplicity 2 (should split into e.g. a 3x3 + 2x2 block pair), where {@code P} comes out
	 * mostly-zero-rows singular for the same underlying reason. Determining the actual block-size
	 * structure (via the ranks of successive powers of (A-&lambda;I)) is real numerical linear
	 * algebra work, not attempted here.
	 * <p>
	 * A second, independent limitation compounds the same test case: {@link Eigenspace}'s root
	 * finder ({@code Polynom}/Durand-Kerner) is not robust for eigenvalues of high multiplicity --
	 * a genuine 5-fold root gets numerically split into 3-4 distinct-looking roots close to but not
	 * exactly equal to each other (e.g. {@code -1.999-0.004i}, {@code -1.997+0.010i}, {@code -2.000}),
	 * a classic ill-conditioning of repeated polynomial roots. This is a pre-existing limitation of
	 * the eigenvalue pipeline this class builds on, not specific to Jordan factorization.
	 */
	public void factorize() {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		// values()/vectors() no longer exist in Eigenspace (see the class' own VERSION history).
		// The correct modern replacements are roots()/solutions(), NOT eigenvalues()/eigenvectors():
		// roots() has one row PER REPETITION of each eigenvalue (dim rows total, e.g. [5,5,3] for
		// a defective 3x3 matrix with a double eigenvalue), matching the i/i+=arithMult indexing
		// this class already uses everywhere below -- eigenvalues() (rootCount rows, one per
		// DISTINCT eigenvalue) would silently skip eigenvalues under that same indexing.
		// roots() returns the live internal field (no defensive copy) -- clone before sorting it
		// in place, to avoid corrupting Eigenspace's own state as a side effect of factorize().
		MatrixComplex eigenValArray = this.roots().clone();
		eigenValArray.quicksort(0);
		MatrixComplex eigenVectArray = this.solutions();
		eigenVectArray.println(HEADINFO + "eigenVectArray");
		
		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(0, 0);
		
		cJ = jordanForm(eigenValArray);
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			//int geomMult = this.geometricMultiplicity(eigenval);
			
			System.out.println("\n" + HEADINFO + "Testing eigenval:" + eigenval.toString() + "\n");
			MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
			eigenVect.println("----------EIGENVECTORS for eigenvalue:" + eigenval.toString() + ", multiplicity:" + arithMult);
			cP = cP.appendRows(eigenVect);
			cP.println(HEADINFO + "+ + + + + 3. computing cP");
			i += arithMult;
		}
		cP = cP.transpose();
		cP.println("----------PASS MATRIX");
	}

	/**
	 * Alternate implementation of {@link #factorize()}: uses the eigenvectors already known from
	 * {@link Eigenspace} directly when an eigenvalue's geometric multiplicity equals its algebraic
	 * multiplicity (no defect), falling back to {@link #eigenvectors(Complex, int)} otherwise.
	 * Same KNOWN LIMITATION as {@link #factorize()} -- see its Javadoc -- for eigenvalues needing
	 * multiple separate Jordan blocks.
	 */
	public void factorize2() {
		int rowLen = this.rows(); 
		int colLen= this.cols();
		if (colLen != rowLen) {
			System.out.println(HEADINFO + "The Matrix MUST be square to be factorized as a Jordan Matrix");
			System.exit(-1);
		}
		// See factorize()'s comment above for why roots()/solutions() (not eigenvalues()/
		// eigenvectors()) are the correct replacements for the old values()/vectors().
		MatrixComplex eigenValArray = this.roots().clone();
		eigenValArray.quicksort(0);
		MatrixComplex eigenVectArray = this.solutions();
		eigenVectArray.println(HEADINFO + "eigenVectArray");

		cJ = new MatrixComplex(rowLen, colLen);
		cP = new MatrixComplex(rowLen, colLen);
		
		cJ = jordanForm(eigenValArray);
		cJ.println("----------JORDAN");
		
		for (int i = 0; i < eigenValArray.rows();) {
			Complex eigenval = eigenValArray.getItem(i, 0);
			int arithMult = this.arithmeticMultiplicity(eigenval);
			int geomMult = this.geometricMultiplicity(eigenval);
			
			System.out.println("\n" + HEADINFO + "Testing eigenval:" + eigenval.toString() + "\n");
			
			if (arithMult == geomMult) {
				for (int sol = 0; sol < arithMult; ++sol) {
					cP.complexMatrix[i+sol] = eigenVectArray.complexMatrix[i+sol].clone();
					cP.println(HEADINFO + "+ + + + + 1. computing cP");
				}
				
			}
			else {
				int offset = 0;
				for (int sol = 0; sol < geomMult; ++sol) {
					cP.complexMatrix[i+sol] = eigenVectArray.complexMatrix[i+sol].clone();
					cP.println(HEADINFO + "+ + + + + 2. computing cP");
					++offset;
				}
				
				MatrixComplex eigenVect = this.eigenvectors(eigenval, arithMult);
				eigenVect.println("----------EIGENVECTORS for eigenvalue:" + eigenval.toString() + ", multiplicity:" + arithMult);
				for (int sol = 0; sol < eigenVect.rows() - offset; ++sol) {
					cP.complexMatrix[sol+offset] = eigenVect.complexMatrix[sol].clone();
					cP.println(HEADINFO + "+ + + + + 3. computing cP");
				}
			}
			i += arithMult;
		}
		cP = cP.transpose();
		cP.println("----------PASS MATRIX");
	}

}
