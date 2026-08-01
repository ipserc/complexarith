package com.ipserc.arith.vectorcomplex;

import java.util.ArrayList;

import com.ipserc.arith.complex.*; 
import com.ipserc.arith.matrixcomplex.*;

public class VectorComplex extends MatrixComplex {

	private final static String HEADINFO = "VectorComplex --- INFO: ";
	private final static String VERSION = "1.7 (2026_0801_0838)";
	/* VERSION Release Note
	 * 1.7 (2026_0801_0838)
	 * normalize()/projectionScalar()/projection()/angleps()/angle() divided by norm()/mod()
	 * without checking for a null vector, giving a silent NaN. Now throw
	 * IllegalArgumentException with an explicit "undefined for a null vector" message.
	 *
	 * 1.6 (2026_0801_0835)
	 * System.exit(1)/silent-null/warn-and-continue -> IllegalArgumentException, same pattern
	 * already applied to MatrixComplex.java: plus()/minus() (were System.exit(1)); constructors
	 * VectorComplex(int)/VectorComplex(String)/VectorComplex(MatrixComplex) (left
	 * complexMatrix=null in silence); innerprod()/dotprod()/crossprod() (warned on stderr but
	 * kept going with mismatched sizes).
	 *
	 * 1.5 (2026_0801_0830)
	 * Fix: VectorComplex(MatrixComplex row) only cloned the outer (row) array, leaving
	 * this.complexMatrix[0] aliased to row.complexMatrix[0] -- a later cell reassignment
	 * on row (e.g. row.setItem(0,col,Complex)) silently corrupted the already-built vector.
	 * Now clones the inner row array too, same pattern already used by MatrixComplex.getRow().
	 *
	 * 1.4 (2026_0731_1200)
	 * Migration from Vector to VectorComplex completed
	 * public VectorComplex rotationD(ArrayList<Double> rotator)
	 * public VectorComplex rotationC(ArrayList<Complex> angle)
	 * private VectorComplex rotatePlane(int i, int j, Complex angle)
	 * Fix: rotation2D() didn't initialize rotatorMat as identity, corrupting dimensions outside the rotation plane for dim()>2
	 *
	 * 1.3 (2024_0528_2000)
	 * public static Boolean checkBase(MatrixComplex base)
	 * public static MatrixComplex matBaseChg(MatrixComplex baseB1, MatrixComplex baseB2)
	 * public Vector baseChg(MatrixComplex baseB1, MatrixComplex baseB2)
	 * public Vector baseExchg(MatrixComplex baseB1, MatrixComplex baseB2)
	 * 
	 * 
	 * 1.2 (2022_0319_2359)
	 * public Vector orthogonal()
	 * public Vector orthogonal(int order)
	 * public void unitary()
	 * public void initialize(Complex cVal)
	 * public void setCoord(int coord, Complex cVal)
	 * public Vector normalize()
	 * public Vector clone()
	 * public Vector[] baseV()
	 * 
	 * 1.1 (2021_0308_2045)
	 * public Vector oppsite()
	 * 
	 * 1.0 (2020_0824_1800)
	 */


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
	 * ***********************************************
	 * 	CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Instantiates a new vector of dimension 1 initialized to zero
	 */
	public VectorComplex() {
		super();
		//this.complexMatrix = new Complex[0][0];
	}

	/**
	 * Instantiates a new vector of dimension "dim" initialized to zero
	 * @param dim The dimension of the vector
	 */
	public VectorComplex(int dim) {
		super(1, dim);
		if (dim < 1) {
			throw new IllegalArgumentException(HEADINFO + "Not valid dimension: " + dim);
		}
	}

	/**
	 * Instantiates a new vector initialized with the values given in "cadena". "cadena" is the string representation of the. The vector is a string with the coefficients separated by commas.
	 * If the vector has more than one row, the constructor returns null.
	 * @param cadena The vector with with the coefficients given in "cadena".
	 */
	public VectorComplex(String cadena) {
		super(cadena);
		int rowLen = this.rows();
		if (rowLen > 1) {
			throw new IllegalArgumentException(HEADINFO + "Not valid vector, more than one row: " + cadena);
		}
	}
	
	/**
	 * Instantiates a new vector initialized with the values given in the row. row is a MatrixComplex one row dimensional array.
	 * If the row has more than one row, the constructor returns null.
	 * @param cadena The vector with the coefficients given by row.
	 */
	public VectorComplex(MatrixComplex row) {
		super(1, row.cols());
		if (row.rows() > 1) {
			throw new IllegalArgumentException(HEADINFO + "Not valid vector, more than one row: " + row.rows());
		}
		else {
			// row.complexMatrix.clone() alone only clones the outer (row) array; since row has
			// exactly one row, this.complexMatrix[0] would stay the SAME array instance as
			// row.complexMatrix[0], so a later cell reassignment on row (e.g. row.setItem(0,col,Complex))
			// would silently corrupt this vector too. Clone the inner row array as well, same
			// pattern already used by MatrixComplex.getRow().
			this.complexMatrix = row.complexMatrix.clone();
			this.complexMatrix[0] = row.complexMatrix[0].clone();
		}
	}
		
	/*
	 * ***********************************************
	 * PRINTING
	 * ***********************************************
	 */

	/**
	 * Displays the vector with the coefficients separated by commas and enclosed between parenthesis.
	 */
	public void print() {
		System.out.print(" ( ");
		for (int col = 0; col < this.cols(); ++col) {
			System.out.print(this.complexMatrix[0][col]);
			System.out.print(col == this.cols()-1 ? " )" : " , ");
		}
	}

	/**
	 * Displays the vector with the coefficients separated by commas and enclosed between parenthesis with a carriage return.
	 */
	public void println() {
		this.print();
		System.out.print('\n');
	}

	/**
	 * Displays the caption given by "caption" and then in a new line the vector with the coefficients separated by commas and enclosed between parenthesis.
	 * @param caption The caption above the vector
	 */
	public void print(String caption) {
		System.out.print(caption);
		this.print();
	}	

	/**
	 * Displays the caption given by "caption" and then in a new line the vector with the coefficients separated by commas and enclosed between parenthesis with a carriage return.
	 * @param caption The caption above the vector
	 */
	public void println(String caption) {
		System.out.print(caption);
		this.println();
	}

	/*
	 * ***********************************************
	 * OPERATORS & OPERATIONS WITH VECTORS
	 * ***********************************************
	 */
	
	/**
	 * Returns the opposite of the vector
	 * @return The new opposite vector
	 */
	public VectorComplex oppsite() {
		VectorComplex result = new VectorComplex(this.cols());

		for (int col = 0; col < this.cols(); ++col) {
			result.complexMatrix[0][col] = this.complexMatrix[0][col].opposite();
		}
		return result;
	}
	
	/**
	 * Checks whether A VECTOR OF A BASE is normal, tha is, its norm is equals 1
	 * @param row The row of the vector in the base
	 * @return True if is normal, false otherwise
	 */
	public static boolean isNormal(MatrixComplex base, int row) {
		if ((base.getRow(row).norm() - 1.0) < Complex.zero_treshold()) return true;
		return false;
	}

	/**
	 * Checks whether a vector is normal, tha is, its norm is equals 1
	 * @param row The row of the vector in the base
	 * @return True if is normal, false otherwise
	 */
	public boolean isNormal() {
		if ((this.getVector(0).norm() - 1.0) < Complex.zero_treshold()) return true;
		return false;
	}

	/**
	 * Using the Gram-Schmidt method, a new orthogonal basis is calculated from a vector space basis.  
	 * @return The orhogonal basis
	 */
	public MatrixComplex orthogonal() {
		return this.base().gramSchmidt();
	}

	/**
	 * Return the i-th vector 
	 * @param order
	 * @return
	 */
	public VectorComplex __orthogonal(int order) {
		MatrixComplex vectBase = new MatrixComplex(this.dim());
		vectBase = this.base().gramSchmidt();
		return new VectorComplex(vectBase.getRow(order));
	}
	
	/**
	 * 
	 */
	public void unitary() {
		this.initialize(Complex.ONE);
	}
	
	/**
	 * 
	 * @param cVal
	 */
	public void initialize(Complex cVal) {
		this.setRow(0, cVal);
	}
	
	/**
	 * 
	 * @param coord
	 * @param cVal
	 */
	public void setCoord(int coord, Complex cVal) {
		this.complexMatrix[0][coord] = cVal;
	}
		
	/**
	 * 
	 * @param coord
	 * @return
	 */
	public Complex getCoord(int coord) {
		return this.complexMatrix[0][coord];
	}

	/**
	 * Retrieves a vector from a base
	 * @param row The row of the vector in the base 
	 * @return The vector form the base at row 'row'
	 */
	public VectorComplex getVector(int row) {
		return new VectorComplex(this.getRow(row));
	}
	
	/**
	 * 
	 */
	public VectorComplex normalize() {
		double norm = this.norm();
		if (norm == 0) {
			throw new IllegalArgumentException(HEADINFO + "normalize: a null vector has no defined direction");
		}
		return (this.div(norm)).clone();
	}
	
	/**
	 * 
	 */
	public VectorComplex clone() {
		VectorComplex newVector = new VectorComplex(this.getRow(0));
		return newVector;
	}
	
	/**
	 * DEPRECATED Calculates an orthonormal base using as first element the normalized vector 
	 * @return the orthonormal base
	 */
	public VectorComplex[] DEPRECATEDbaseV() {
		VectorComplex base[] = new VectorComplex[this.dim()];
		
		MatrixComplex matbase = new MatrixComplex(this.dim());
		matbase.setRow(0, this.getRow(0));
		matbase = base(matbase);
		
		for(int i = 0; i < this.dim(); ++i) {
			base[i] = new VectorComplex(this.dim());
			base[i].setRow(0, matbase.getRow(i));
		}
		
		return base;		
	}

	/**
	 * Checks whether base is a basis of the vectorial space or not.
	 * @param base the basis to checked.
	 * @return true if is a basis, false other wise.
	 */
	public static boolean isBase(MatrixComplex base) {
		if (!base.isSquare()) return false;
		if (base.determinant().isZero()) return false;
		if (base.rank() != base.rows()) return false;
		return true;
	}

	/**
	 * Private method that shifts the coordinates of the vectors within the matrix to the immediately preceding column. The shift is circular. 
	 * @param base The basis to use.
	 * @param offsetRow The initial row from which the scroll begins.
	 * @return The new shifted basis
	 */
	private static MatrixComplex pushCoords(MatrixComplex base, int offsetRow) {
		if (!base.determinant().isZero()) return base;
		MatrixComplex newBase = base.clone();
		for ( int row = offsetRow; row < base.rows(); ++row) {
			Complex tail = base.getItem(row, base.cols()-1);
			for (int col = 0; col < base.cols()-1; ++col) {
				newBase.setItem(row, col+1, base.getItem(row, col));
			}
			newBase.setItem(row, 0, tail);
		}
		return newBase;
	}

	/**
	 * Constructs a basis starting from an initial vector placed in the first row of the basis. The remaining vectors used to fill the basis correspond to an orthonormal basis.
	 * @return The new basis.
	 */
	public MatrixComplex base() {
		int dim = this.cols();
		MatrixComplex base;
		base = MatrixComplex.eye(dim);
		
		int count = 1;
		base.complexMatrix[0] = this.complexMatrix[0].clone();
		// base.println("ChekBase");
		while (!isBase(base)) {
			base = pushCoords(base, 1);
			++count;
			// base.println("ChekBase");
			if (count == 2*dim-1) return (new MatrixComplex(dim));
		}
		return base;
	}

	/**
	 * Static version that constructs a basis starting from an initial vector placed in the first row of the basis. The remaining vectors used to fill the basis correspond to an orthonormal basis.
	 * @param coord1 the initial vector used to construct the basis.
	 * @return The new basis.
	 */
	public static MatrixComplex base(MatrixComplex coord1) {
		int dim = coord1.cols();
		MatrixComplex base;
		base = MatrixComplex.eye(dim);
		
		int count = 1;
		for (int row = 0; row < coord1.rows(); ++row)
			base.complexMatrix[row] = coord1.complexMatrix[row].clone();
		// base.println("ChekBase");
		while (base.determinant().isZero()) {
			base = pushCoords(base, coord1.rows());
			++count;
			// base.println("ChekBase");
			if (count == dim) return (new MatrixComplex(dim));
		}
		return base;
	}
	
	/**
	 * Calculates the addition of two vectors
	 * @param vector The vector to add to this
	 * @return The result of the sum
	 */
	public VectorComplex plus(VectorComplex vector) {
		int dimA1 = this.cols();
		int dimA2 = vector.cols();

		if (dimA1 != dimA2 ) {
			throw new IllegalArgumentException(HEADINFO + "Not valid sum: vectors of different size, " + dimA1 + " != " + dimA2);
		}

		VectorComplex result = new VectorComplex(dimA1);

		for (int col = 0; col < dimA1; ++col) {
			result.complexMatrix[0][col] = this.complexMatrix[0][col].plus(vector.complexMatrix[0][col]);
		}
		return result;
	}

	/**
	 * Calculates the difference of two vectors
	 * @param vector The vector to differentiate to this
	 * @return The result of the difference
	 */
	public VectorComplex minus(VectorComplex vector) {
		int dimA1 = this.cols();
		int dimA2 = vector.cols();

		if (dimA1 != dimA2 ) {
			throw new IllegalArgumentException(HEADINFO + "Not valid differentiation: vectors of different size, " + dimA1 + " != " + dimA2);
		}

		VectorComplex result = new VectorComplex(dimA1);

		for (int col = 0; col < dimA1; ++col) {
			result.complexMatrix[0][col] = this.complexMatrix[0][col].minus(vector.complexMatrix[0][col]);
		}
		return result;
	}

	/**
	 * In linear algebra, an outer product is the tensor product of two coordinate vectors, a special case of the Kronecker product of matrices. 
	 * The outer product of two coordinate vectors u and v, denoted u ⊗ v , is a matrix w such that the coordinates satisfy w(i,j) = u(i)·v(j). 
	 * The outer product for general tensors is also called the tensor product. 
	 * The outer product is the result of multiplying the adjoint of "vector" with this.
	 * Source: https://en.wikipedia.org/wiki/Outer_product
	 * @param vector The vector to multiply.
	 * @return an array with the outer product of the two vectors.
	 */
	public MatrixComplex outerprod(VectorComplex vector) {
		MatrixComplex outerprod = new MatrixComplex(this.cols(), vector.cols());

		for (int row = 0; row < this.cols(); ++row)
			for (int col = 0; col < vector.cols(); ++col)
				outerprod.setItem(row, col, this.getItem(0, row).times(vector.getItem(0, col)));

		return outerprod;
	}

	/**
	 * In linear algebra, an outer product is the tensor product of two coordinate vectors, a special case of the Kronecker product of matrices. 
	 * The outer product of two coordinate vectors u and v, denoted u ⊗ v , is a matrix w such that the coordinates satisfy w(i,j) = u(i)·v(j). 
	 * The outer product for general tensors is also called the tensor product. 
	 * The outer product is the result of multiplying the adjoint of "vector" with this.
	 * Source: https://en.wikipedia.org/wiki/Outer_product
	 * @param vector The vector to multiply.
	 * @return an array with the outer product of the two vectors.
	 */
	public MatrixComplex tensorprod(VectorComplex vector) {
		return this.outerprod(vector);
	}

	/**
	 * Calculates the inner product of two vectors
	 * An inner product is a generalization of the dot product. In a vector space, it is a way to multiply vectors together, with the result of this multiplication being a scalar.
	 * @param vector The vector to multiply
	 * @return The scalar resultant of the product, a Complex.
	 */
	public Complex innerprod(VectorComplex vector) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = vector.rows();
		int colLenC = vector.cols();

		if (rowLen != 1 || rowLenC != 1) {
			throw new IllegalArgumentException(HEADINFO + "dotprod/innerprod: " + "One of the components isn't a vector");
		}

		if (colLen != colLenC) {
			throw new IllegalArgumentException(HEADINFO + "dotprod/innerprod: " + "Vectors of different size, " + colLen + " != " + colLenC);
		}

		return this.times(vector.adjoint()).complexMatrix[0][0];
	}
	
	/**
	 * Shortcut to the method innerprod(Vector vector)
	 * Inner product spaces generalize Euclidean spaces (in which the inner product is the dot product, also known as the scalar product) to vector spaces of any (possibly infinite) dimension
	 * @param vector The vector to multiply.
	 * @return The scalar resultant of the product, a Complex.
	 */
	public Complex dotprod(VectorComplex vector) {
		return this.innerprod(vector);
	}

	/**
	 * 
	 * @param coef
	 * @return
	 */
	public int leviCivita(int[] vCoef) {
		int[] coef = vCoef.clone();
		int swaps = 0;
		int temp;

		for(int i = 1; i < coef.length; ++i)
			for(int j = 0 ; j < coef.length - 1; ++j)
				if (coef[j] > coef[j+1]) {
					temp = coef[j];
					coef[j] = coef[j+1];
					coef[j+1] = temp;
					++swaps;
				}

		temp = coef[0];
		for(int i = 1; i < coef.length; ++i)
			if (coef[i] == temp) return 0;
			else temp = coef[i];
		return swaps % 2 == 0 ? 1 : -1;
	}
	
	/**
	 * 
	 * @param aVector
	 * @return
	 */
	public VectorComplex vectorprod(VectorComplex aVector) {
		int coef[] = new int[3];
		VectorComplex result = new VectorComplex(this.dim());
		int lc; //Levi-Civita Symbol
		for(int col1 = 0; col1 < aVector.dim(); ++col1) {
			coef[0] = col1;
			for(int col2 = 0; col2 < aVector.dim(); ++col2) {
				coef[1] = col2;
				for(int col3 = 0; col3 < aVector.dim(); ++col3) {
					coef[2] = col3;
					lc = leviCivita(coef);
					//System.out.println("col1:"+col1);
					//this.getItem(col2).println("this.getItem("+col2+"):");
					//aVector.getItem(col3).println("aVector.getItem("+col3+"):");
					//System.out.println("LC:"+lc);
					result.setCoord(col1, result.getCoord(col1).plus(this.getCoord(col2).times(aVector.getCoord(col3).times(lc))));
				}
			}
		}
		return result;
	}
	
	/**
	 * There are more than one Beno Eckmann Coefficients Table in this world
	 * @param order
	 * @return the Beno Eckmann Table corresponding with the index
	 */
	private int[][] benoEckmann(int order) {
		// Beno Eckmann Matrix {(sign)Component}
		// As you can check is complete for 1,3 and 7 dimensions
		// For 2 dimensions the crossproduct requires the 3rd dimension
		// For 4, 5, & 6 dimensions the crossproduct requires the 7th dimension
		// This is the Beno Eckmann for our universe
		int [][] BE1 = {
				{ 0, 3,-2, 5,-4,-7, 6},
				{-3, 0, 1, 6, 7,-4,-5},
				{ 2,-1, 0, 7,-6, 5,-4},
				{-5,-6,-7, 0, 1, 2, 3},
				{ 4,-7, 6,-1, 0,-3, 2},
				{ 7, 4,-5,-2, 3, 0,-1},
				{-6, 5, 4,-3,-2, 1, 0}
				};

		switch (order) {
		case 1:
			// This is the Beno Eckmann for our universe
			return BE1;
		case 2:
			int [][] BE2 = {
					{ 0, 2,-3, 4,-5, 6,-7},
					{-2, 0, 1, 6, 7,-4,-5},
					{ 3,-1, 0, 7,-6, 5,-4},
					{-4,-6,-7, 0, 1, 2, 3},
					{ 5,-7, 6,-1, 0,-3, 2},
					{-6, 4,-5,-2, 3, 0,-1},
					{ 7, 5, 4,-3,-2, 1, 0}
					};
			return BE2;
		case 3:
			int [][] BE3 = {
					{ 0, 2,-3, 4,-5,-6, 7},
					{-2, 0, 1, 6, 7,-4,-5},
					{ 3,-1, 0, 7,-6, 5,-4},
					{-4,-6,-7, 0, 1, 2, 3},
					{ 5,-7, 6,-1, 0,-3, 2},
					{ 6, 4,-5,-2, 3, 0,-1},
					{-7, 5, 4,-3,-2, 1, 0}
					};
			return BE3;
		case 480:
			// Only algebras compatible with the physics in 3 dimensions are allowed
			// Only the 0,2,3,... - 0,3,2,... are supported by this program, the others produces errors
			int [][] BE480 = {
					{ 0, 4, 7,-2, 6,-5,-3},
					{-4, 0, 5, 1,-3, 7,-6},
					{-7,-5, 0, 6, 2,-4, 1},
					{ 2,-1,-6, 0, 7, 3,-5},
					{-6, 3,-2,-7, 0, 1, 4},
					{ 5,-7, 4,-3,-1, 0, 2},
					{ 3, 6,-1, 5,-4,-2, 0}
					};
			return BE480;
		default:
			return BE1;
		}
	}
	
	/**
	 * Private method. Calculates the cross product for vectors up to dimension 7. (Beno Eckmann)
	 * The cross product or vector product (occasionally directed area product to emphasize the geometric significance) 
	 * is a binary operation on two vectors in three-dimensional space (R3) and is denoted by the symbol ×. 
	 * Given two linearly independent vectors a and b, the cross product, a × b, is a vector that is perpendicular to both a and b and thus normal to the plane containing them. 
	 * It has many applications in mathematics, physics, engineering, and computer programming. 
	 * It should not be confused with dot product (projection product).
	 * In 1943 Beno Eckmann, using algebraic topology, gave the first proof of this result
	 * @param vector The multiplier vector
	 * @param dim the dimension of the product
	 * @return The vector resultant of the cross product
	 * Source https://francis.naukas.com/2012/12/22/el-producto-vectorial-en-un-espacio-euclideo-de-7-dimensiones/
	 * Source https://en.wikipedia.org/wiki/Seven-dimensional_cross_product
	 * Source http://www.thespectrumofriemannium.com/wp-content/uploads/2020/10/crossprod.pdf
	 */
	private VectorComplex crossprodP(VectorComplex vector, int dim) {
		// Beno Eckmann Matrix {(sign)Component}
		// As you can check is complete for 1,3 and 7 dimensions
		// For 2 dimensions the crossproduct requires the 3rd dimension
		// For 4, 5, & 6 dimensions the crossproduct requires the 7th dimension
		/* * /
		final int [][] BE = {
				{ 0, 3,-2, 5,-4,-7, 6},
				{-3, 0, 1, 6, 7,-4,-5},
				{ 2,-1, 0, 7,-6, 5,-4},
				{-5,-6,-7, 0, 1, 2, 3},
				{ 4,-7, 6,-1, 0,-3, 2},
				{ 7, 4,-5,-2, 3, 0,-1},
				{-6, 5, 4,-3,-2, 1, 0}};
		/* */

		/* */
		int [][] BE = benoEckmann(1);
		/* */
		
		int resCol, signCol;
		VectorComplex result = new VectorComplex(dim);

		Complex resultC = new Complex();
		for(int colA = 0; colA < this.cols(); ++colA) {
			for(int colB = 0; colB < vector.cols(); ++colB) {
				if (colA == colB) continue;
				resCol = BE[colA][colB];
				signCol = resCol < 0 ? -1 : 1;
				resCol *= signCol;
				resultC = this.complexMatrix[0][colA].times(vector.complexMatrix[0][colB]).times(signCol);
				result.complexMatrix[0][--resCol] = result.complexMatrix[0][resCol].plus(resultC);
			}
		}
		return result;
	}

	/**
	 * Calculates the cross product for vectors up to dimension 7.
	 * The cross product or vector product (occasionally directed area product to emphasize the geometric significance) 
	 * is a binary operation on two vectors in three-dimensional space (R3) and is denoted by the symbol ×. 
	 * Given two linearly independent vectors a and b, the cross product, a × b, is a vector that is perpendicular to both a and b and thus normal to the plane containing them. 
	 * It has many applications in mathematics, physics, engineering, and computer programming. 
	 * It should not be confused with dot product (projection product).
	 * @param vector The multiplier vector
	 * @return The vector resultant of the cross product
	 */
	public VectorComplex crossprod(VectorComplex vector) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = vector.rows();
		int colLenC = vector.cols();
		VectorComplex result = new VectorComplex();

		if (rowLen != 1 || rowLenC != 1) {
			throw new IllegalArgumentException(HEADINFO + "crossprod: " + "One of the components isn't a vector");
		}

		if (colLen != colLenC) {
			throw new IllegalArgumentException(HEADINFO + "crossprod: " + "Vectors of different size, " + colLen + " != " + colLenC);
		}

		switch (colLen) {
			case 1: 
				return result = new VectorComplex(1);
			case 2:
			case 3:
				return  this.crossprodP(vector, 3);
			case 4:
			case 5:
			case 6:
			case 7:
				return  this.crossprodP(vector, 7);
		}

		return result;
	}

	/**
	 * The scalar triple product (also called the mixed product, box product, or triple scalar product) is defined as the dot product of one of the vectors with the cross product of the other two.
	 * In dimension three Geometrically, the scalar triple product a ⋅ ( b × c ) is the (signed) volume of the parallelepiped defined by the three vectors given. 
	 * Here, the parentheses may be omitted without causing ambiguity, since the dot product cannot be evaluated first.
	 * If it were, it would leave the cross product of a scalar and a vector, which is not defined.
	 * @param v1 Second component of the mixed product
	 * @param v2 Third component of the mixed product
	 * @return The result of "this · (v1 x v2)"
	 */
	public Complex tripleprod(VectorComplex v1, VectorComplex v2) {
		return this.dotprod(v1.crossprod(v2));
	}

	/**
	 * The scalar triple product (also called the mixed product, box product, or triple scalar product) is defined as the dot product of one of the vectors with the cross product of the other two.
	 * @param v1 Second component of the mixed product
	 * @param v2 Third component of the mixed product
	 * @return The result of "this · (v1 x v2)"
	 */
	public Complex mixedprod(VectorComplex v1, VectorComplex v2) {
		return this.tripleprod(v1, v2);
	}
	
	/**
	 * Calculates the modulus of the vector.
	 * @return the modulus of the vector.
	 */
	public double mod() {
		return this.norm();
	}

	/**
	 * Calculates the product of the vector by a complex number.
	 * @param cNum the complex number.
	 * @return the product of the vector by a complex number.
	 */
	public VectorComplex prod(Complex cNum) {
		VectorComplex vector = new VectorComplex();
		vector.complexMatrix = this.times(cNum).complexMatrix.clone();
		return vector;
	}

	/**
	 * Calculates the product of the vector by a real number.
	 * @param num the real number.
	 * @return the product of the vector by a real number.
	 */
	public VectorComplex prod(double num) {
		VectorComplex vector = new VectorComplex();
		vector.complexMatrix = this.times(num).complexMatrix.clone();
		return vector;
	}

	/**
	 * Calculates the division of the vector by a complex number.
	 * @param cNum the complex number.
	 * @return the division of the vector by a complex number.
	 */
	public VectorComplex div(Complex cNum) {
		VectorComplex vector = new VectorComplex();
		vector.complexMatrix = this.divides(cNum).complexMatrix.clone();
		return vector;
	}

	/**
	 * Calculates the division of the vector by a real number.
	 * @param num the real number.
	 * @return the division of the vector by a real number.
	 */
	public VectorComplex div(double num) {
		VectorComplex vector = new VectorComplex();
		vector.complexMatrix = this.divides(num).complexMatrix.clone();
		return vector;
	}
	
	/**
	 * 
	 * @return
	 */
	public VectorComplex shift() {
		VectorComplex shiftVec = new VectorComplex(this.dim());

		for (int i = 0; i < this.dim()-1; ++i)
			shiftVec.setCoord(i+1, this.getCoord(i));

		return shiftVec;
	} 

	/**
	 * 
	 * @return
	 */
	public VectorComplex shiftr() {
		VectorComplex shiftVec = new VectorComplex(this.dim());
		int i;

		for (i = 0; i < this.dim()-1; ++i)
			shiftVec.setCoord(i+1, this.getCoord(i));

		shiftVec.setCoord(0, this.getCoord(i));
		return shiftVec;
	}

	public VectorComplex shiftr(int n) {
		VectorComplex shiftVec = this.clone();

		for (int i = 0; i < n; ++i)
			shiftVec = shiftVec.shiftr();

		return shiftVec;
	}



	/*
	 * ***********************************************
	 * BASE OPERATIONS
	 * ***********************************************
	 */
	
	/**
	 * 
	 * @param base
	 * @return
	 */
	public static Boolean checkBase(MatrixComplex base) {
		return !base.determinant().equals(Complex.ZERO);
	}
	
	/**
	 * Changes the base of the vector and calculate its coordinates in the new base "base".
	 * The vector must be expressed in the standard basis (orthonormal unitary canonical basis).
	 * @param base The base in which we would like to express the new coordinates.
	 * @return The vector referred to the new base.
	 */
	public VectorComplex baseChg(MatrixComplex base) {
		this.complexMatrix = (base.dividesleft(this.transpose())).transpose().complexMatrix.clone();
		return this;
	}

	/**
	 * Changes the base of the vector expressed in "base" and calculate its coordinates in the standard basis.
	 * The vector must be expressed in the base given by the parameter "base".
	 * @param base The base in which the vector is referred.
	 * @return The vector referred standard basis.
	 */
	public VectorComplex baseExchg(MatrixComplex base) {
		this.complexMatrix = base.times(this.transpose()).transpose().complexMatrix.clone();
		return this;
	}

	/**
	 * 
	 * @param baseB1
	 * @param baseB2
	 * @return
	 */
	public static MatrixComplex matBaseChg(MatrixComplex baseB1, MatrixComplex baseB2) {
		return baseB2.transpose().inverse().times(baseB1.transpose());
	}
	
	/**
	 * 
	 * @param vectB1
	 * @param baseB1
	 * @param baseB2
	 * @return
	 */
	public VectorComplex baseChg(MatrixComplex baseB1, MatrixComplex baseB2) {
		return new VectorComplex(matBaseChg(baseB1, baseB2).times(this.transpose()).transpose());
	}

	/**
	 * 
	 * @param baseB1
	 * @param baseB2
	 * @return
	 */
	public VectorComplex baseExchg(MatrixComplex baseB1, MatrixComplex baseB2) {
		return new VectorComplex(matBaseChg(baseB2, baseB1).times(this.transpose()).transpose());
	}

	
	/**
	 * Checks whether a Base is orthogonal or not
	 * @return True if the matrix is orthogonal, false otherwise
	 */
	public boolean isOrthogonal() {
		Boolean isOrthogonal = true;
		for (int row1 = 0; row1 < this.rows(); ++row1) {
			for (int row2 = 0; row2 < this.rows(); ++row2) {
				if (row1 == row2) continue;
        		// if (!v1.dotprod(v2).equals(Complex.ZERO, Complex.getMaxDecimals())) {
        		VectorComplex v1 = this.getVector(row1); 
        		VectorComplex v2 = this.getVector(row2);
				if (!(this.getVector(row1).dotprod(this.getVector(row2))).equals(Complex.ZERO, Complex.getMaxDecimals())) {
					isOrthogonal = false;
					v1.println("v1: ");
					v2.println("v2: ");
					System.out.println("v1.dotprod(v2): " + v1.dotprod(v2));
					break;
				}
			}
		}
		return isOrthogonal;
	}

	public static boolean isOrthogonal(MatrixComplex base) {
		Boolean isOrthogonal = true;
		VectorComplex v1, v2;
		for (int row1 = 0; row1 < base.rows(); ++row1) {
			for (int row2 = 0; row2 < base.rows(); ++row2) {
				if (row1 == row2) continue;
        		// if (!v1.dotprod(v2).equals(Complex.ZERO, Complex.getMaxDecimals())) {
        		v1 = new VectorComplex(base.getRow(row1)); 
        		v2 = new VectorComplex(base.getRow(row2));
				if (!(v1.dotprod(v2)).equals(Complex.ZERO, Complex.getMaxDecimals())) {
					isOrthogonal = false;
					v1.println("v1: ");
					v2.println("v2: ");
					System.out.println("v1.dotprod(v2): " + v1.dotprod(v2));
					break;
				}
			}
		}
		return isOrthogonal;
	}
	
	/**
	 * Checks whether a Base is orthogonal or not
	 * @return True if the matrix is orthonormal, false otherwise
	 */
	public boolean isOrthonormal() {
		if (!this.isSquare()) return false;
		//if (this.determinant().equals(Complex.ZERO)) return false;
		//return this.inverse().equals(this.adjoint());
		//return this.times(this.adjoint()).equals(eye(this.rows()));
		// Un conjunto de vectores S = {⃗v 1,⃗v 2, . . . ,⃗v p} de IRn es un conjunto ortonormal si es un conjunto ortogonal de vectores unitarios.
		// En álgebra lineal y física, un vector unitario o versor es un vector de módulo uno.
		if (!this.isOrthogonal()) return false;
		for (int baseVect = 0; baseVect < this.rows(); ++baseVect)
			if (!VectorComplex.isNormal(this, baseVect)) return false;
		return true;
	}
	
	public static boolean isOrthonormal(MatrixComplex base) {
		if (!base.isSquare()) return false;
		//if (this.determinant().equals(Complex.ZERO)) return false;
		//return this.inverse().equals(this.adjoint());
		//return this.times(this.adjoint()).equals(eye(this.rows()));
		// Un conjunto de vectores S = {⃗v 1,⃗v 2, . . . ,⃗v p} de IRn es un conjunto ortonormal si es un conjunto ortogonal de vectores unitarios.
		// En álgebra lineal y física, un vector unitario o versor es un vector de módulo uno.
		if (!base.isOrthogonal()) return false;
		for (int baseVect = 0; baseVect < base.rows(); ++baseVect)
			if (!VectorComplex.isNormal(base, baseVect)) return false;
		return true;
	}

	/*
	 * ***********************************************
	 * LINEAR ALGEBRA
	 * ***********************************************
	 */

	/**
	 * Calculates the scalar projection of the vector on the vector component.
	 * @param vector the vector component.
	 * @return the scalar projection.
	 */
	public Complex projectionScalar(VectorComplex vector) {
		double mod = vector.mod();
		if (mod == 0) {
			throw new IllegalArgumentException(HEADINFO + "projectionScalar: cannot project onto a null vector");
		}
		VectorComplex vectorU = vector.div(mod);
		return this.dotprod(vectorU);
	}

	/**
	 * Calculates the projection of the vector on the vector component.
	 * @param vector the vector component.
	 * @return the vector projection.
	 */
	public VectorComplex projection(VectorComplex vector) {
		double mod = vector.mod();
		if (mod == 0) {
			throw new IllegalArgumentException(HEADINFO + "projection: cannot project onto a null vector");
		}
		VectorComplex vectorU = vector.div(mod);
		return vectorU.prod(this.projectionScalar(vector));
	}

	/**
	 * Calculates the angle between the vector and the vector component using scalar projection formula.
	 * @param vector the vector component.
	 * @return the angle between the vector and the vector component in radians.
	 */
	public double angleps(VectorComplex vector) {
		double mod = this.mod();
		if (mod == 0) {
			throw new IllegalArgumentException(HEADINFO + "angleps: this is a null vector, angle is undefined");
		}
		double ps = this.projectionScalar(vector).mod(); // guards vector==0 internally
		return Math.acos(ps/mod);
	}

	/**
	 * Calculates the angle between the vector and the vector component.
	 * @param vector the vector component.
	 * @return the angle between the vector and the vector component in radians.
	 */
	public double angle(VectorComplex vector) {
		double modThis = this.norm();
		double modVector = vector.norm();
		if (modThis == 0 || modVector == 0) {
			throw new IllegalArgumentException(HEADINFO + "angle: undefined between a null vector and another vector");
		}
		return Math.acos(this.dotprod(vector).mod()/modThis/modVector);
	}

	/**
	 * Calculates the rejection of the vector on the vector component.
	 * The rejection is an orthogonal vector of the vector component.
	 * @param vector the vector component.
	 * @return the vector rejection.
	 */
	public VectorComplex rejection(VectorComplex vector) {
		VectorComplex projection = new VectorComplex();
		projection = this.projection(vector);
		return this.minus(projection);
	}

	/**
	 * Calculates the distance between the vector and the vector component using the euclidean norm.
	 * @param vector the vector component.
	 * @return the distance.
	 */
	public double distance(VectorComplex vector) {
		return this.rejection(vector).norm();
	}

	public VectorComplex rotation2D(Double rotator, VectorComplex normalPlanar) {
		VectorComplex newVector = new VectorComplex(dim());

		MatrixComplex thisVector = new MatrixComplex(1,2);
		thisVector.complexMatrix = this.complexMatrix.clone();
		MatrixComplex rotatorMat = new MatrixComplex(this.dim());
		rotatorMat.initMatrixDiag(1, 0);
		int c1 = 0, c2 = 0;
		for (c1 = 0; c1 < normalPlanar.dim(); ++c1) {
			normalPlanar.getCoord(c1).println("* * c1("+c1+"):");
			if (!normalPlanar.getCoord(c1).equals(Complex.ZERO)) {
				for (c2 = c1+1; c2 < normalPlanar.dim(); ++c2) {
					normalPlanar.getCoord(c2).println(" + +c2("+c2+"):");
					if (!normalPlanar.getCoord(c2).equals(Complex.ZERO))
						break;
				}
				break;
			}
		}
		rotatorMat.setItem(c1,c1, Complex.cos(rotator)); 
		rotatorMat.setItem(c1,c2, Complex.sin(-rotator)); 
		rotatorMat.setItem(c2,c1, Complex.sin(rotator)); 
		rotatorMat.setItem(c2,c2, Complex.cos(rotator)); 
		newVector.complexMatrix = (rotatorMat.times(thisVector.transpose()).transpose()).complexMatrix.clone();

		return newVector;
	}

	/**
	 * Private method. Rotates the vector within the (i,j) coordinate plane by "angle" radians (real or complex), leaving the remaining coordinates unchanged.
	 * @param i The first axis of the rotation plane.
	 * @param j The second axis of the rotation plane.
	 * @param angle The rotation angle.
	 * @return The rotated vector.
	 */
	private VectorComplex rotatePlane(int i, int j, Complex angle) {
		MatrixComplex rotator = MatrixComplex.eye(this.dim());
		rotator.setItem(i, i, Complex.cos(angle));
		rotator.setItem(i, j, Complex.sin(angle).opposite());
		rotator.setItem(j, i, Complex.sin(angle));
		rotator.setItem(j, j, Complex.cos(angle));
		return new VectorComplex(rotator.times(this.transpose()).transpose());
	}

	/**
	 * Rotates the vector in n dimensions using the generalized Euler decomposition: a chain of dim()-1 planar rotations,
	 * one for each consecutive pair of axes (0,1), (1,2), ..., (dim()-2,dim()-1), each by its corresponding angle in "rotator".
	 * @param rotator The dim()-1 rotation angles, one per consecutive axis pair.
	 * @return The rotated vector.
	 */
	public VectorComplex rotationD(ArrayList<Double> rotator) {
		if (rotator.size() != dim()-1 ) {
			System.err.println(HEADINFO + "rotationD: " + "The rotator has a dimension different of " + (dim()-1));
			return new VectorComplex(dim());
		}

		VectorComplex result = this;
		for (int i = 0; i < rotator.size(); ++i) {
			result = result.rotatePlane(i, i+1, new Complex(rotator.get(i)));
		}

		return result;
	}

	/**
	 * Rotates the vector in n dimensions using the generalized Euler decomposition with complex angles: a chain of dim()-1 planar rotations,
	 * one for each consecutive pair of axes (0,1), (1,2), ..., (dim()-2,dim()-1), each by its corresponding angle in "angle".
	 * @param angle The dim()-1 rotation angles, one per consecutive axis pair.
	 * @return The rotated vector.
	 */
	public VectorComplex rotationC(ArrayList<Complex> angle) {
		if (angle.size() != dim()-1 ) {
			System.err.println(HEADINFO + "rotationC: " + "The angle list has a dimension different of " + (dim()-1));
			return new VectorComplex(dim());
		}

		VectorComplex result = this;
		for (int i = 0; i < angle.size(); ++i) {
			result = result.rotatePlane(i, i+1, angle.get(i));
		}

		return result;
	}
}

