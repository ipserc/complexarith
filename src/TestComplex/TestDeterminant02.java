package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;


public class TestDeterminant02 {

	private static void showResults(MatrixComplex fMatrix) {
		int dim = (int)Math.sqrt(fMatrix.dim());
		boolean setFormatOff = (dim < 8) ? false : true; 
	   	int boxSize = 65;

       	System.out.println("");
       	System.out.println(Complex.boxTitleRandom(boxSize, "DETERMINANT TEST"));
       	fMatrix.println(Complex.boxTextRandom(boxSize, "Matrix Dimension: " + dim + "x" + dim));
		if (setFormatOff) {
			Complex.storeFormatStatus();
	       	Complex.setFormatOFF();
	    	Complex.setFixedOFF();
		}
       	Chronometer gaussChrono = new Chronometer();
		System.out.println("detGauss(fMatrix) = " + fMatrix.determinantGauss().toString());
		gaussChrono.stop();
    	System.out.println("El cálculo del determinante ha llevado " + gaussChrono.toString());
    	if (fMatrix.dim() < 800) {
           	Chronometer adjChrono = new Chronometer();
    	   	System.out.println("detAdj(fMatrix) = " + fMatrix.determinantAdj().toString());
    	   	adjChrono .stop();
        	System.out.println("El cálculo del determinante ha llevado " + adjChrono .toString());
    	}
		if (setFormatOff) {
			Complex.restoreFormatStatus();
		}
 	}

	public static void main(String[] args) {
    	MatrixComplex fMatrix;
    	MatrixComplex hMatrix;
    	MatrixComplex gMatrix;
    	MatrixComplex UMatrix;
    	MatrixComplex VMatrix;

    	Complex.setFormatON();
    	
    	fMatrix = new MatrixComplex("0,1,-1;1,1,0;-1,0,1");
    	showResults(fMatrix);
    	
       	hMatrix = fMatrix.clone();
    	hMatrix.Ftransf(0, 1);
       	showResults(fMatrix);
        
       	hMatrix = fMatrix.clone();
    	hMatrix.Ftransf(0, 2);
       	showResults(fMatrix);
        
       	hMatrix = fMatrix.clone();
    	hMatrix.Ftransf(0, 1);
    	hMatrix.Ftransf(1, 2);
       	showResults(fMatrix);
        
    	fMatrix = new MatrixComplex("3,11;2,6");
       	showResults(fMatrix);
            	
    	fMatrix = new MatrixComplex("0.30992784166,0.844767893,-0.42649233740000003,-0.024001315265,0.08854469413999999;-0.7753907800000001,0.08482144595999999,-0.29123447042,0.13218024361,0.5378525143;0.5370004641,-0.42784750320000003,-0.3450551376,0.22161960222,0.6003321742000001;0.10623044737,0.18956360263,0.6007185231000001,-0.5119176766,0.5743329008;-0.05531242977,-0.24532566878999998,-0.5033497565,-0.8190099904,-0.11232768877999999");
       	showResults(fMatrix);
            	
    	fMatrix = new MatrixComplex("0.15294023625,0.6318361661,0.26300869573999996,-0.5765194576,-0.4193376734;0.48157254250000003,-0.5715184268,0.6409292214,-0.16628845699,-0.05488584756;0.6487544609,0.014952765743,-0.422226411,0.3696036111,-0.5138210707;-0.5599745947,-0.3687405117,0.028321974776,-4.948601281E-4,-0.7413882310000001;0.10120905613999999,-0.3714250891,-0.5839168457,-0.7094831851000001,0.08645725856");
       	showResults(fMatrix);

    }

}
