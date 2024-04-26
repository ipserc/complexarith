/******************************************************************************
 *  Compilation:  javac TestComplex.java
 *  Execution:    java TestComplex
 *	clear;runJava.sh eclipse-workspace/complexarith/bin/TestComplex/TestTaylorLogExp03b.class
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
 * la parte real de los elementos de la diagonal principal sea positiva
 * En caso de que algún elemeto tenga cómo parte real el 0.0., el logaritmo
 * será convergente si el determinante es positivo
 */
public class TestTaylorLogExp06 {
	static int boxSize = 65;
	
	public static void printHeader(MatrixComplex aMatrix) {
    	Complex.printBoxTitle(3, boxSize, "logarithm base 10 of Matrix Test");
     	aMatrix.println("aMatrix");
     	System.out.println("aMatrix W = " + aMatrix.toWolfram());
     	System.out.println("aMatrix M = " + aMatrix.toMaxima());
    	System.out.println("aMatrix C = " + aMatrix.toMatrixComplex());
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
	
	public static void calcLogs(MatrixComplex aMatrix) {
    	MatrixComplex bMatrix;

 		try {
	    	bMatrix = aMatrix.log10(); 
	       	if (!bMatrix.isInfinite() && !bMatrix.isNaN()) {
	       		printHeader(aMatrix);
		    	Complex.printBoxText(3, boxSize, "logarithm in base 10");
	       		bMatrix.println("b=log10");
	         	System.out.println("bMatrix W = " + bMatrix.toWolfram());
	         	System.out.println("bMatrix M = " + bMatrix.toMaxima());
	        	System.out.println("bMatrix C = " + bMatrix.toMatrixComplex());
	       		MatrixComplex.power(10, bMatrix).println("10^b");
	            System.out.println();
	       	}
		}
		catch (Exception excep) {
			System.out.println("Error in the calculation of the exponential");
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

    	Complex.printBoxTitle(1, boxSize, "MATRIX COMPLEX LOG10 EXP");
    	MatrixComplex.version();
    	System.out.println();
   		   		
   	
    	aMatrix = new MatrixComplex("32.0,25.0,30.0;13.0,20.0,-21.0;-2.0,-1.0,31.0");
    	calcLogs(aMatrix);
        		
    	bMatrix = new MatrixComplex("1.50515, 1.39794, 1.47712; 1.11394, 1.30103, 1.32222 + 1.36438i; 0.30103 + 1.36438i, 0.0 + 1.36438i, 1.49136");
     	System.out.println("bMatrix W = " + bMatrix.toWolfram());
     	System.out.println("bMatrix M = " + bMatrix.toMaxima());
    	System.out.println("bMatrix W = " + bMatrix.toMatrixComplex());   	
    	MatrixComplex.power(10, bMatrix).println("10^b W");
    }
}
