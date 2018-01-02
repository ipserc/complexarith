# complexarith
complexarith is an attempt to model the complex field arithmetic in Java to be used in mathematical developments.

The com.ipserc.arith.complex package contains the Complex class.
Complex.java is the class that incorporates the methods and operations to be able to work on complexes. The class allows the introduction of complex numbers naturally in text format, so the calse constructor supports the instantiation of complex numbers of the form "3-2i"
Complex.java incorporates all arithmetic operations with complexes, as well as numerous functions.

The com.ipserc.arith.matrixcomplex package contains the MatrixComplex class.
MatrixComplex extends the complexes to be incorporated into matrices. Provides methods to create complex field matrices and their operations.
MatrixComplex calculates determinants, solves systems of equations and allocates them using Gramm-Schmidt.
MatrixComplex calculates the characteristic polynomial of the matrix, and its eigenvalues ​​and eigenvectors.

The com.ipserc.arith.factorization package contains the classes
Diagfactor.java
LUfactor.java
QRfactor.java
SVDfactor.java
Diagfactor.java Factorize a matrix of the form A = P · D · P⁻¹ where D is a diagonal matrix.
LUfactor.java Facturizes a matrix of the form A = L U, where L is a lower triangular matrix and U is a superior triangular matrix.
QRfactor.java Factor a matrix of the form A = Q R where Q is an orthogonal matrix m times m, and R is a triangular matrix superior m times n.
SVDfactor.java Factor a matrix of the form A = U Σ V*, where Σ is a diagonal matrix mxn, and U and V are orthogonal matrices mxm and nxn respectively.

The com.ipserc.arith.polynom package contains the Polynom class.
Polynom.java provides the methods to operate with polynomials and calculate their roots in complex field.
Generates polynomials from Hermite, Legendre, Lagerre and Chebyshev.
It incorporates methods to graphically represent polynomials. It makes use of the JavaPlot library of Panayotis (http://javaplot.panayotis.com/)

The com.ipserc.arith.vector package contains the Vector class.
Vector.java introduces the necessary methods to operate with complex field vectors.
