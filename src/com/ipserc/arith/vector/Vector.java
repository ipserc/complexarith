package com.ipserc.arith.vector;

import com.ipserc.arith.complex.*; 
import com.ipserc.arith.matrixcomplex.*;

public class Vector extends MatrixComplex {

	private final static String HEADINFO = "Vector --- INFO: ";
	private final static String VERSION = "1.3 (2024_0528_2000)";
	/* VERSION Release Note
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
	public Vector() {
		super();
		//this.complexMatrix = new Complex[0][0];
	}

	/**
	 * Instantiates a new vector of dimension "dim" initialized to zero
	 * @param dim The dimension of the vector
	 */
	public Vector(int dim) {
		super(1,dim);
		/*
    	this.complexMatrix = new Complex[1][dim];
    	for (int col = 0; col < dim; ++col)
    		this.complexMatrix[0][col] = new Complex();
		 */
	}

	/**
	 * Instantiates a new vector initialized with the values given in "cadena". "cadena" is the string representation of the. The vector is a string with the coefficients separated by commas.
	 * If the vector has more than one row, the constructor returns null.
	 * @param cadena The vector with with the coefficients given in "cadena".
	 */
	public Vector(String cadena) {
		super(cadena);
		int rowLen = this.rows();
		if (rowLen > 1) {
			System.err.println(HEADINFO + "Not valid vector: vector set to null vector.");
			this.complexMatrix = null;
		}
	}
	
	/**
	 * Instantiates a new vector initialized with the values given in the row. row is a MatrixComplex one row dimensional array.
	 * If the row has more than one row, the constructor returns null.
	 * @param cadena The vector with the coefficients given by row.
	 */
	public Vector(MatrixComplex row) {
		super(1, row.cols());
		if (row.rows() > 1) {
			System.err.println(HEADINFO + "Not valid vector: vector set to null vector.");
			this.complexMatrix = null;
		}
		else {
			this.complexMatrix = row.complexMatrix.clone();
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
	public Vector oppsite() {
		Vector result = new Vector(this.cols());

		for (int col = 0; col < this.cols(); ++col) {
			result.complexMatrix[0][col] = this.complexMatrix[0][col].opposite();
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public Vector orthogonal() {
		return orthogonal(0);
	}

	/**
	 * 
	 * @param order
	 * @return
	 */
	public Vector orthogonal(int order) {
		Vector vectBase[] = new Vector[this.dim()];
		vectBase = this.baseV();
		return vectBase[order];
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
	 * 
	 */
	public Vector normalize() {
		return (this.div(this.norm())).clone();
	}
	
	/**
	 * 
	 */
	public Vector clone() {
		Vector newVector = new Vector(this.getRow(0));
		return newVector;
	}
	
	/**
	 * Calculates an orthonormal base using as first element the normalized vector 
	 * @return the orthonormal base
	 */
	public Vector[] baseV() {
		Vector base[] = new Vector[this.dim()];
		
		MatrixComplex matbase = new MatrixComplex(this.dim());
		matbase.setRow(0, this.getRow(0));
		matbase = matbase.base();
		
		for(int i = 0; i < this.dim(); ++i) {
			base[i] = new Vector(this.dim());
			base[i].setRow(0, matbase.getRow(i));
		}
		
		return base;		
	}

	/**
	 * Calculates the addiction of two vectors
	 * @param vector The vector to add to this
	 * @return The result of the sum
	 */
	public Vector plus(Vector vector) {
		int dimA1 = this.cols();
		int dimA2 = vector.cols();

		if (dimA1 != dimA2 ) {
			System.err.println(HEADINFO + "Not valid sum: vectors of different size.");
			System.exit(1);
		}

		Vector result = new Vector(dimA1);

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
	public Vector minus(Vector vector) {
		int dimA1 = this.cols();
		int dimA2 = vector.cols();

		if (dimA1 != dimA2 ) {
			System.err.println(HEADINFO + "Not valid differentation: vectors of different size.");
			System.exit(1);
		}

		Vector result = new Vector(dimA1);

		for (int col = 0; col < dimA1; ++col) {
			result.complexMatrix[0][col] = this.complexMatrix[0][col].minus(vector.complexMatrix[0][col]);
		}
		return result;
	}

	/**
	 * Calculates the inner product of two vectors
	 * An inner product is a generalization of the dot product. In a vector space, it is a way to multiply vectors together, with the result of this multiplication being a scalar.
	 * @param vector The vector to multiply
	 * @return The scalar resultant of the product, a Complex.
	 */
	public Complex innerprod(Vector vector) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = vector.rows();
		int colLenC = vector.cols();

		if (rowLen != 1 || rowLenC != 1) {
			System.err.println(HEADINFO + "dotprod/innerprod: " + "One of the componentes isn't a vector");
		}

		if (colLen != colLenC) {
			System.err.println(HEADINFO + "dotprod/innerprod: " + "Vectors of different size");
		}

		return this.times(vector.adjoint()).complexMatrix[0][0];
	}
	
	/**
	 * Shortcut to the method innerprod(Vector vector)
	 * Inner product spaces generalize Euclidean spaces (in which the inner product is the dot product, also known as the scalar product) to vector spaces of any (possibly infinite) dimension
	 * @param vector The vector to multiply.
	 * @return The scalar resultant of the product, a Complex.
	 */
	public Complex dotprod(Vector vector) {
		return this.innerprod(vector);
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
	public MatrixComplex tensorprod(Vector vector) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = vector.rows();
		int colLenC = vector.cols();

		if (rowLen != 1 || rowLenC != 1) {
			System.err.println(HEADINFO + "outerprod: " + "One of the componentes isn't a vector");
		}

		if (colLen != colLenC) {
			System.err.println(HEADINFO + "outerprod: " + "Vectors of different size");
		}

		return vector.adjoint().times(this);
	}

	/**
	 * 
	 * @param vector
	 * @return
	 */
	public MatrixComplex outerprod(Vector vector) {
		return tensorprod(vector);
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
	public Vector vectorprod(Vector aVector) {
		int coef[] = new int[3];
		Vector result = new Vector(this.dim());
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
	private Vector crossprodP(Vector vector, int dim) {
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
		Vector result = new Vector(dim);

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
	public Vector crossprod(Vector vector) {
		int rowLen = this.rows();
		int colLen = this.cols();
		int rowLenC = vector.rows();
		int colLenC = vector.cols();
		Vector result = new Vector();

		if (rowLen != 1 || rowLenC != 1) {
			System.err.println(HEADINFO + "crossprod: " + "One of the componentes isn't a vector");
		}

		if (colLen != colLenC) {
			System.err.println(HEADINFO + "crossprod: " + "Vectors of different size");
		}

		switch (colLen) {
			case 1: 
				return result = new Vector(1);
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
	public Complex tripleprod(Vector v1, Vector v2) {
		return this.dotprod(v1.crossprod(v2));
	}

	/**
	 * The scalar triple product (also called the mixed product, box product, or triple scalar product) is defined as the dot product of one of the vectors with the cross product of the other two.
	 * @param v1 Second component of the mixed product
	 * @param v2 Third component of the mixed product
	 * @return The result of "this · (v1 x v2)"
	 */
	public Complex mixedprod(Vector v1, Vector v2) {
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
	public Vector prod(Complex cNum) {
		Vector vector = new Vector();
		vector.complexMatrix = this.times(cNum).complexMatrix.clone();
		return vector;
	}

	/**
	 * Calculates the product of the vector by a real number.
	 * @param num the real number.
	 * @return the product of the vector by a real number.
	 */
	public Vector prod(double num) {
		Vector vector = new Vector();
		vector.complexMatrix = this.times(num).complexMatrix.clone();
		return vector;
	}

	/**
	 * Calculates the division of the vector by a complex number.
	 * @param cNum the complex number.
	 * @return the division of the vector by a complex number.
	 */
	public Vector div(Complex cNum) {
		Vector vector = new Vector();
		vector.complexMatrix = this.divides(cNum).complexMatrix.clone();
		return vector;
	}

	/**
	 * Calculates the division of the vector by a real number.
	 * @param num the real number.
	 * @return the division of the vector by a real number.
	 */
	public Vector div(double num) {
		Vector vector = new Vector();
		vector.complexMatrix = this.divides(num).complexMatrix.clone();
		return vector;
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
		return base.determinant().equals(Complex.ZERO);
	}
	
	/**
	 * Changes the base of the vector and calculate its coordinates in the new base "base".
	 * The vector must be expressed in the standard basis (orthonormal unitary canonical basis).
	 * @param base The base in which we would like to express the new coordinates.
	 * @return The vector referred to the new base.
	 */
	public Vector baseChg(MatrixComplex base) {
		this.complexMatrix = (base.dividesleft(this.transpose())).transpose().complexMatrix.clone();
		return this;
	}

	/**
	 * Changes the base of the vector expressed in "base" and calculate its coordinates in the standard basis.
	 * The vector must be expressed in the base given by the parameter "base".
	 * @param base The base in which the vector is referred.
	 * @return The vector referred standard basis.
	 */
	public Vector baseExchg(MatrixComplex base) {
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
	public Vector baseChg(MatrixComplex baseB1, MatrixComplex baseB2) {
		return new Vector(matBaseChg(baseB1, baseB2).times(this.transpose()).transpose());
	}

	/**
	 * 
	 * @param baseB1
	 * @param baseB2
	 * @return
	 */
	public Vector baseExchg(MatrixComplex baseB1, MatrixComplex baseB2) {
		return new Vector(matBaseChg(baseB2, baseB1).times(this.transpose()).transpose());
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
	public Complex projectionScalar(Vector vector) {
		Vector vectorU = new Vector();
		vectorU = vector.div(vector.mod());
		return this.dotprod(vectorU);
	}

	/**
	 * Calculates the projection of the vector on the vector component.
	 * @param vector the vector component.
	 * @return the vector projection.
	 */
	public Vector projection(Vector vector) {
		Vector vectorU = new Vector();
		vectorU = vector.div(vector.mod());
		return vectorU.prod(this.projectionScalar(vector));
	}

	/**
	 * Calculates the angle between the vector and the vector component using scalar projection formula.
	 * @param vector the vector component.
	 * @return the angle between the vector and the vector component in radians.
	 */
	public double angleps(Vector vector) {
		double ps = this.projectionScalar(vector).mod();
		double mod = this.mod();
		return Math.acos(ps/mod);
	}

	/**
	 * Calculates the angle between the vector and the vector component.
	 * @param vector the vector component.
	 * @return the angle between the vector and the vector component in radians.
	 */
	public double angle(Vector vector) {
		return Math.acos(this.dotprod(vector).mod()/this.norm()/vector.norm());
	}

	/**
	 * Calculates the rejection of the vector on the vector component.
	 * The rejection is an orthogonal vector of the vector component.
	 * @param vector the vector component.
	 * @return the vector rejection.
	 */
	public Vector rejection(Vector vector) {
		Vector projection = new Vector();
		projection = this.projection(vector);
		return this.minus(projection);
	}

	/**
	 * Calculates the distance between the vector and the vector component using the euclidean norm.
	 * @param vector the vector component.
	 * @return the distance.
	 */
	public double distance(Vector vector) {
		return this.rejection(vector).norm();
	}
	
}

