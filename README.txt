complexarith is a Java library for arithmetic and linear algebra over the complex field (C), with
no external dependencies. It started in 2017 as a personal project to model complex numbers in
Java and has grown to cover complex matrices, the classic matrix factorizations, polynomials,
equation systems, geometry and the usual signal transforms (Fourier, Laplace, Z) -- all built on
the same base Complex type.

The com.ipserc.arith.complex package contains the Complex class. Complex.java is the class that
incorporates the methods and operations to work with complex numbers. It has its own parser: it
accepts complex numbers naturally in text format ("3-2i") besides Complex(re, im). Complex.java
incorporates all arithmetic operations with complexes, as well as numerous functions (exponential,
logarithm, trigonometric and hyperbolic functions and their inverses, n-th roots).

The com.ipserc.arith.matrixcomplex package contains the MatrixComplex class, split into dedicated
classes for each concern (MatrixComplexUnary, MatrixComplexRank, MatrixComplexKernel,
MatrixComplexOrtho, MatrixComplexFunctions, MatrixComplexCharPoly, MatrixComplexEquationSystems...).
Provides methods to create complex field matrices and their operations: determinant, inverse,
rank, kernel, characteristic polynomial. Numeric singularity is detected via relative tolerance
(not an absolute epsilon), so large matrices with rounding residues that are small in absolute
terms but significant in the full determinant are not mistakenly treated as non-singular. Matrix
functions (exp, log, sqrt, hyperbolic sine/cosine...) are computed via diagonalization or Taylor/
Mercator series when the matrix is not diagonalizable. Eigenspace computes eigenvalues and
eigenvectors, algebraic and geometric multiplicity, grouping repeated roots by connected
components (not per-component rounding, which fragments legitimate clusters). MatrixComplexPlot
plots matrices, including a "square" presentation.

The com.ipserc.arith.factorization package contains the standard matrix factorizations, all over
complex matrices:
	LUfactor.java       A = L * U
	QRfactor.java        A = Q * R  (Gram-Schmidt, Householder, Givens)
	SVDfactor.java       A = U * Sigma * V^T
	Diagfactor.java      A = P * D * P^-1
	Hessenbergfactor.java  reduction to upper Hessenberg form by unitary similarity, A = Q*H*Q^H
	QRSchurfactor.java / Schurfactor.java  Schur factorization A = Q*T*Q^H via shifted QR
		iteration (Wilkinson shift) with deflation -- the LAPACK/MATLAB-style method for
		eigenvalues that never forms the characteristic polynomial explicitly
	Jordan.java          canonical Jordan form, including geometric multiplicity > 1

The com.ipserc.arith.polynom package contains the Polynom class. Polynom.java provides the methods
to operate with polynomials and calculate their roots in the complex field. Generates Hermite,
Legendre, Laguerre and Chebyshev polynomials of any degree. Roots are computed via companion
matrix + QRSchurfactor (solveQRCompanion), with an optional statistical root-grouping mode by
multiplicity (e_rootCalcMode). evalFromRoots() evaluates numerically stably through the factored
form, avoiding the precision loss of reconstructing coefficients via power()/times() for high
degree. Spline.java provides spline interpolation, backed by Syseq to solve the system.

The com.ipserc.arith.vectorcomplex package contains the VectorComplex class. VectorComplex.java
introduces the necessary methods to operate with complex field vectors: dot product, norm, and a
vector product generalized to n dimensions (vectorprodN) besides the classic binary 3D one.

The com.ipserc.arith.geom package contains Point, Line and Plane: affine geometry over C --
incidence, parallelism, distances (including line-to-line distance in dimension > 3).

The com.ipserc.arith.syseq package contains Syseq and Syseqnum: linear equation systems, both
direct solving (Syseq, homogeneous and non-homogeneous) and iterative numerical methods (Syseqnum)
-- conjugate gradient (congrad) and GMRES (genminres).

The com.ipserc.arith.signal package contains Fourier, Laplace, Z and Sigfunc: the classic signal
transforms and their filters, with graphical plotting of the results.

The com.ipserc.arith.combinatoric package contains CombinationNoReps: combinations without
repetition and their numbering.

The com.ipserc.arith.plot package contains SimpleGnuplot, a homegrown gnuplot launcher with no
external dependencies (replaces the old com.panayotis.gnuplot library, which used to block on
Process.waitFor()). Every plotting operation in the project exposes a xxxSync()/xxxAsync() pair
over a single generic method parameterized by SimpleGnuplot.e_syncMode.

Finally TestComplex has a lot of examples of how to use the classes and their methods, plus the
ScratchXxx/TestXxx drivers used to investigate and measure bugs during development -- all kept
around, even after being resolved, as reference.

# Configuration
Import the files in a new Java Project.
For Eclipse, in Properties -> Java Build Path tab Libraries Add Class Folder classes under
Classpath.

No third-party dependencies: everything, including plotting, is self-contained.
