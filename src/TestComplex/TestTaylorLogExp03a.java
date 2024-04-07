/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *	clear;runJava.sh eclipse-workspace/complexarith/bin/TestComplex/TestTaylorLogExp03a.class
 *  Tests for arith.Complex.
 *	
 *  
 *  
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 ******************************************************************************/
package TestComplex;

import com.ipserc.arith.complex.Complex;
//import arith.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

import java.util.List;
import java.util.ArrayList;

/**
 * Parece demostrar que la convergencia del logaritmo está condicionada a que 
 * la parte real de los elementos de la riagonal principal sea positiva
 * En caso de que algún elemeto tenga cómo parte real el 0.0., el logaritmo
 * será convergente si el determinante es positivo
 */
public class TestTaylorLogExp03a {
	static int boxSize = 65;
	
	static public void calcLogs_00(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;

    	Complex.printBoxTitle(3, boxSize, "Natural logarithm Matrix Test");
     	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");
    	if (aMatrix.isPostiveSemiDefinite()) {
        	System.out.println("aMatrix is Postive SemiDefinite");
        	if (aMatrix.isPostiveDefinite()) {
            	System.out.println("aMatrix is Postive Definite");
        	}
    	}
    	else System.out.println("aMatrix NOT is Postive");
    	
		Eigenspace eigenSpace = new Eigenspace(aMatrix);
		eigenSpace.eigenvalues().println("Eigenvalues");


    	Complex.printBoxText(3, boxSize, "Taylor's Natural logarithm ");
 		try {
	    	bMatrix = aMatrix.logTaylor(); 
	    	bMatrix.println("b=logTaylor");
	       	if ( !bMatrix.isInfinite())
	       		bMatrix.exp().println("exp(b)");
		}
		catch (Exception excep) {
			System.out.println("Error in the calculation of the exponential");
		}
    	
    	Complex.printBoxText(4, boxSize, "Mercator's Natural logarithm ");
    		try {
    	    	bMatrix = aMatrix.logMercator(); 
    	    	bMatrix.println("b=logMercator");
            	if ( !bMatrix.isInfinite())
            		bMatrix.exp().println("exp(b)");
    		}
    		catch (Exception excep) {
    			System.out.println("Error in the calculation of the exponential");
    		}

    	/** /
    	Complex.printBoxText(5, boxSize, "Hyp Arctan's Natural logarithm ");
		try {
	    	bMatrix = aMatrix.logHat(); 
	    	bMatrix.println("b=logHat");
	    	if ( !bMatrix.isInfinite())
	    		bMatrix.exp().println("exp(b)");
		}
		catch (Exception excep) {
			System.out.println("Error in the calculation of the exponential");
		}
    	/**/

	}

	static public void calcLogs_01(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;
    	boolean taylor = true, mercator = false, hat = false;

    	Complex.printBoxTitle(3, boxSize, "Natural logarithm Matrix Test");
     	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");
    	if (aMatrix.isPostiveSemiDefinite()) {
        	System.out.println("aMatrix is Postive SemiDefinite");
        	if (aMatrix.isPostiveDefinite()) {
            	System.out.println("aMatrix is Postive Definite");
        	}
    	}
    	else System.out.println("aMatrix NOT is Postive");
    	
		Eigenspace eigenSpace = new Eigenspace(aMatrix);
		eigenSpace.eigenvalues().println("Eigenvalues");


		if (taylor) {
	 		try {
		    	bMatrix = aMatrix.logTaylor(); 
		       	if ( !bMatrix.isInfinite()) {
			    	Complex.printBoxText(3, boxSize, "Taylor's Natural logarithm ");
		       		bMatrix.println("b=logTaylor");
		       		bMatrix.exp().println("exp(b)");
		       	}
			}
			catch (Exception excep) {
				System.out.println("Error in the calculation of the exponential");
			}
		}
 		
		if (mercator) {
	    		try {
	    	    	bMatrix = aMatrix.logMercator(); 
	            	if ( !bMatrix.isInfinite()) {
	        	    	Complex.printBoxText(4, boxSize, "Mercator's Natural logarithm ");
		    	    	bMatrix.println("b=logMercator");
	            		bMatrix.exp().println("exp(b)");
	            	}
	    		}
	    		catch (Exception excep) {
	    			System.out.println("Error in the calculation of the exponential");
	    		}
		}

		if (hat) {
			try {
		    	bMatrix = aMatrix.logHat(); 
		    	if ( !bMatrix.isInfinite()) {
					Complex.printBoxText(5, boxSize, "Hyp Arctan's Natural logarithm ");
			    	bMatrix.println("b=logHat");
		    		bMatrix.exp().println("exp(b)");
		    	}
			}
			catch (Exception excep) {
				System.out.println("Error in the calculation of the exponential");
			}
		}
	}

	public static void printHeader(MatrixComplex aMatrix) {
    	Complex.printBoxTitle(3, boxSize, "Natural logarithm Matrix Test");
     	aMatrix.println("aMatrix");
    	System.out.println("aMatrix = " + aMatrix.toMatrixComplex());
    	aMatrix.determinant().println("Det=");
    	if (aMatrix.isPostiveSemiDefinite()) {
        	System.out.println("aMatrix is Postive SemiDefinite");
        	if (aMatrix.isPostiveDefinite()) {
            	System.out.println("aMatrix is Postive Definite");
        	}
    	}
    	else System.out.println("aMatrix NOT is Postive");
    	
		Eigenspace eigenSpace = new Eigenspace(aMatrix);
		eigenSpace.eigenvalues().println("Eigenvalues");		
	}
	
	static public void calcLogs(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;
    	boolean taylor = true, mercator = false, hat = false;


		if (taylor) {
	 		try {
		    	bMatrix = aMatrix.logTaylor(); 
		       	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) {
		       		printHeader(aMatrix);
			    	Complex.printBoxText(3, boxSize, "Taylor's Natural logarithm ");
		       		bMatrix.println("b=logTaylor");
		       		bMatrix.exp().println("exp(b)");
		            System.out.println();
		       	}
			}
			catch (Exception excep) {
				System.out.println("Error in the calculation of the exponential");
			}
		}
 		
		if (mercator) {
	    		try {
	    	    	bMatrix = aMatrix.logMercator(); 
	            	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) {
			       		printHeader(aMatrix);
	        	    	Complex.printBoxText(4, boxSize, "Mercator's Natural logarithm ");
		    	    	bMatrix.println("b=logMercator");
	            		bMatrix.exp().println("exp(b)");
	                    System.out.println();
	            	}
	    		}
	    		catch (Exception excep) {
	    			System.out.println("Error in the calculation of the exponential");
	    		}
		}

		if (hat) {
			try {
		    	bMatrix = aMatrix.logHat(); 
		    	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) {
		       		printHeader(aMatrix);
					Complex.printBoxText(5, boxSize, "Hyp Arctan's Natural logarithm ");
			    	bMatrix.println("b=logHat");
		    		bMatrix.exp().println("exp(b)");
		            System.out.println();
		    	}
			}
			catch (Exception excep) {
				System.out.println("Error in the calculation of the exponential");
			}
		}
	}

    public static List<MatrixComplex> obtenerCombinaciones(MatrixComplex aMatrix) {
        List<MatrixComplex> combinaciones = new ArrayList<>();
        int filas = aMatrix.rows();
        int columnas = aMatrix.cols();
        
        /* *****************************************
         * Por lo tanto, en este contexto, 1 << (filas * columnas) se utiliza para generar 
         * un número entero que tiene un bit 1 en la posición más significativa y ceros en 
         * todas las demás posiciones, lo que es útil para generar todas las combinaciones 
         * posibles de elementos positivos y negativos en la matriz.
         * **************************************** */
        /* ***************************************** /
	        int prod1 = filas * columnas;
	        int desp1 = 1 << prod1;
        /***************************************** */
        // Recorremos todas las combinaciones posibles        
        for (int i = 0; i < (1 << (filas * columnas)); i++) {
            MatrixComplex combinacion = new MatrixComplex(filas,columnas);
            int contador = 0;
            
            // Construimos la matriz de combinación
            for (int fila = 0; fila < filas; fila++) {
                for (int columna = 0; columna < columnas; columna++) {
                    Complex valor = aMatrix.getItem(fila,columna);
                    /* *****************************************
                     * (i >> contador) & 1 se utiliza para verificar 
                     * si el bit en la posición contador del número i es 1 o 0.
                     * ***************************************** */
                    /* ***************************************** /
                    	int desp2 = i >> contador;
                		int and2 = desp2 & 1;
                    /***************************************** */
                    if (((i >> contador) & 1) == 1) {
                        combinacion.setItem(fila, columna, valor);
                    } else {
                    	combinacion.setItem(fila, columna, valor.opposite()); // Cambiamos el signo
                    }
                    contador++;
                }
            }
            
            // Verificamos si la combinación es única
            if (!combinaciones.contains(combinacion)) {
                combinaciones.add(combinacion);
            }
        }
        
        return combinaciones;
    }

    public static void doCalcs(MatrixComplex aMatrix) {
        List<MatrixComplex> combinaciones = obtenerCombinaciones(aMatrix);
        
        // Imprimimos las combinaciones obtenidas
        for (MatrixComplex combinacion : combinaciones) {
        	//combinacion.println("Matriz");
        	calcLogs(combinacion);
          }
    }

    
    public static void main(String[] args) {
    	MatrixComplex aMatrix;
    	MatrixComplex bMatrix;
    	MatrixComplex cMatrix;
    	MatrixComplex dMatrix;
    	MatrixComplex eMatrix;
    	MatrixComplex fMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex iMatrix;
    	MatrixComplex sMatrix;
		Chronometer chrono = new Chronometer();

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	//Complex.setScientificON(8);;
    	Complex.setFormatON();
    	Complex.exact(true);
    	MatrixComplex.debugOFF();
    	Complex.digits(100000);
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitle(1, boxSize, "MATRIX COMPLEX NATURAL LOG EXP");
    	System.out.println();
    	
    	boolean complex = false;
    	aMatrix = new MatrixComplex(2);
    	if (complex) 
    		aMatrix.initMatrixRandomRecInt(9); // Complex Numbers
    	else
    		aMatrix.initMatrixRandomInteger(9); // Integer Numbers
    	// aMatrix = aMatrix.hermitian();
    	//aMatrix.abs();
		
    	doCalcs(aMatrix);
    	if (complex) doCalcs(aMatrix.conjugate());
	
    	/** /
    	aMatrix = new MatrixComplex(2);
   		// aMatrix.initMatrixRandomRecInt(9);
   		aMatrix.initMatrixRandomInteger(99);
    	//aMatrix = aMatrix.hermitian();
    	//aMatrix.abs();
    	doCalcs(aMatrix);
    	/**/
    	
    	/** /
    	//Complex.significative(7);
   		aMatrix = new MatrixComplex(""
   				+ "  .1,  .1;"
   				+ "  .1,  .1");
    	doCalcs(aMatrix);
    	/**/

    	/** /
    	aMatrix = new MatrixComplex(""
    			+ "1.6,  1.3;"
    			+ "1.2,  1.7");
    	doCalcs(aMatrix);
    	/**/

   	 	/*
   	 	 * [ 1.0 , 8.0 ]
   	 	 * [ 4.0 , 7.0 ]
   	 	 */
    	/** /
   		aMatrix = new MatrixComplex(""
   				+ " 1.0, 8.0;"
   				+ " 4.0, 7.0");
   		doCalcs(aMatrix);
   		/**/
   		
    	/*
   	 	 * [ 7.0 , -7.0 ]
   	 	 * [ -2.0 , 1.0 ]
   	 	 */
   		/** /
   		aMatrix = new MatrixComplex(""
   				+ "7.0, 7.0;"
   				+ "2.0, 1.0");
    	doCalcs(aMatrix);
    	/**/
    	
   	 	/*
   	 	 * [ 1.0 , -8.0 ]
   	 	 * [ 4.0 , -7.0 ]
   	 	 */
    	/** /
   		aMatrix = new MatrixComplex(""
   				+ " 1.0, 8.0;"
   				+ " 4.0, 7.0");
    	doCalcs(aMatrix);
    	/**/
    	
   	 	/*
   	 	 * [ 8.0 , -8.0 ]
   	 	 * [ 7.0 , -5.0 ]
   	 	 */
    	/** /
    	aMatrix = new MatrixComplex(""
    			+ "8.0, 8.0;"
    			+ "7.0, 5.0");
    	doCalcs(aMatrix);
    	/**/
    	
    	/*
   	 	 * [ 18.0 , 1.0 ]
   	 	 * [ 1.0 , 8.0 ]
   	 	 */
    	/** /
   		aMatrix = new MatrixComplex(""
   				+ "18.0, 1.0;"
   				+ "1.0, 8.0");
    	doCalcs(aMatrix);
    	/** /
    	
    	//[ 10.0 , -2.0 ]
    	//[ -2.0 , 14.0 ]
    	/** /
    	aMatrix = new MatrixComplex(""
    			+ "10.0, 2.0;"
    			+ " 2.0,14.0");
    	doCalcs(aMatrix);
    	/**/
    	
    	/*
    	 * [ 9.0 , -5.0 ]
    	 * [ -7.0 , 6.0 ]
    	 */
    	/** /
    	//Complex.digits(10000);
    	aMatrix = new MatrixComplex(""
    			+ "9.0,5.0;"
    			+ "7.0,6.0");
    	doCalcs(aMatrix);
    	/**/
    	
    	/** /
     	aMatrix = new MatrixComplex(""
    			+ "1, 1;"
    			+ " 1,1");
    	doCalc(aMatrix);
    	/***/
    	
		/*
		 * [ 5.34375039E+00-4.47944030E-01i , 1.89518912E+00-1.06609312E+00i ]
		 * [ 1.72210060E-01+9.58859700E-02i , 5.79278846E+00-1.16982627E+00i ]
		 */ 
    	/** /
		aMatrix = new MatrixComplex(""
				+ "5.34375039E+00-4.47944030E-01i,1.89518912E+00-1.06609312E+00i;"
				+ "1.72210060E-01+9.58859700E-02i,5.79278846E+00-1.16982627E+00i");
		doCalcs(aMatrix);
		doCalcs(aMatrix.adjoint());
		/**/

    	/*
		aMatrix = new MatrixComplex(""
				+ "5-4i,1+1i;"
				+ "1+2i,5-1i");
		doCalcs(aMatrix);
		doCalcs(aMatrix.adjoint());
		*/
    	
    	/*
		aMatrix = new MatrixComplex(""
				+ "3+1i,2-2i;"
				+ "3+4i,5-6i");
		doCalcs(aMatrix);
		doCalcs(aMatrix.adjoint());
		*/

    	/** /
    	Complex.digits(10000);
    	aMatrix = new MatrixComplex(""
    			+ "0.0,5.0;"
    			+ "7.0,6.0");
    	doCalcs(aMatrix);
    	/**/

    	/** /
    	//Complex.digits(10000);
    	aMatrix = new MatrixComplex(""
    			+ "0.0,5.0;"
    			+ "7.0,0.0");
    	doCalcs(aMatrix);
    	/**/

		/** /
    	//Complex.digits(10000);
    	aMatrix = new MatrixComplex(""
    			+ "0.0,5.0i;"
    			+ "7.0i,0.0");
    	doCalcs(aMatrix);
    	/**/

    	/** /
    	//Complex.digits(10000);
    	aMatrix = new MatrixComplex(""
    			+ "6.0,5.0i;"
    			+ "7.0i,2.0");
    	doCalcs(aMatrix);
    	/**/

    }
}
