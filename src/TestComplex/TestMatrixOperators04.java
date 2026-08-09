package TestComplex;

/*
 * qué significa que dos matrices tengan los mismos autovalores y el mismo determinante
 * https://chatgpt.com/share/67d8a8cf-efd0-8005-adcc-16d8bf8ad638
 * 
 * 
 */

import com.ipserc.arith.complex.Complex;
//import arith.Complex;
import com.ipserc.arith.matrixcomplex.*;
import com.ipserc.chronometer.Chronometer;

import java.util.List;
import java.util.ArrayList;

public class TestMatrixOperators04 {

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
		int boxSize = 65;
		Chronometer chrono = new Chronometer();

		Complex.resetFormatStatus();
		Complex.restorePrecisionFactorySettings();
    	Complex.setFormatON();
    	MatrixComplex.debugOFF();
    	MatrixComplex.doPlotON();
    	Complex.digits(10000000);
    	Complex.setScientificON(4);
    	Complex.numpPadPLUS();
    	
    	Complex.showPrecision();
    	Complex.printFormatStatus();

    	Complex.printBoxTitleRandom(boxSize, "MATRIX OPERATORS");
    	System.out.println();

   		aMatrix = new MatrixComplex("2.0000E+00,5.0000E+00,-6.0000E+00,9.0000E+00;-9.0000E+00,4.0000E+00,5.0000E+00,-9.0000E+00;6.0000E+00,5.0000E+00,6.0000E+00,-3.0000E+00;-8.0000E+00,6.0000E+00,-8.0000E+00,7.0000E+00");
   		//aMatrix = new MatrixComplex("3.0000E+00,5.0000E+00,6.0000E+00,2.0000E+00;-8.0000E+00,9.0000E+00,-3.0000E+00,-2.0000E+00;-1.0000E+00,3.0000E+00,6.0000E+00,-1.0000E+00;-1.0000E+00,7.0000E+00,2.0000E+00,5.0000E+00");
   		//aMatrix = new MatrixComplex("6.0000E+00,9.0000E+00,-3.0000E+00,-4.0000E+00;-8.0000E+00,8.0000E+00,-9.0000E+00,1.0000E+00;5.0000E+00,6.0000E+00,4.0000E+00,-6.0000E+00;-6.0000E+00,-5.0000E+00,6.0000E+00,5.0000E+00");
   		//aMatrix = new MatrixComplex("-1.7000E+01,7.7000E+01,9.4000E+01,5.6000E+01;4.1000E+01,4.2000E+01,-9.0000E+01,9.1000E+01;-1.1000E+01,5.1000E+01,6.7000E+01,8.1000E+01;5.1000E+01,-1.4000E+01,-7.9000E+01,4.7000E+01");
   		//aMatrix = new MatrixComplex("8.0000E+00+4.0000E+00i,1.0000E+00-7.0000E+00i,-9.0000E+00+8.0000E+00i,-7.0000E+00+8.0000E+00i;2.0000E+00-8.0000E+00i,8.0000E+00+1.0000E+00i,1.0000E+00-4.0000E+00i,-7.0000E+00-2.0000E+00i;3.0000E+00-5.0000E+00i,-3.0000E+00-3.0000E+00i,8.0000E+00-6.0000E+00i,4.0000E+00+2.0000E+00i;7.0000E+00+9.0000E+00i,5.0000E+00+9.0000E+00i,-7.0000E+00+8.0000E+00i,-3.0000E+00-5.0000E+00i");
   		//aMatrix = new MatrixComplex("3.0000E+00-5.0000E+00i,2.0000E+00-5.0000E+00i,-5.0000E+00+7.0000E+00i,2.0000E+00-9.0000E+00i;-3.0000E+00-1.0000E+00i,8.0000E+00-4.0000E+00i,-1.0000E+00+7.0000E+00i,-1.0000E+00+1.0000E+00i;3.0000E+00+3.0000E+00i,-7.0000E+00+5.0000E+00i,2.0000E+00+1.0000E+00i,8.0000E+00+4.0000E+00i;-3.0000E+00-1.0000E+00i,4.0000E+00+9.0000E+00i,-6.0000E+00+9.0000E+00i,2.0000E+00+4.0000E+00i");
   		//aMatrix = new MatrixComplex("6.0000E+00-4.0000E+00i,-2.0000E+00+3.0000E+00i,7.0000E+00+3.0000E+00i,1.0000E+00+5.0000E+00i;1.0000E+00-6.0000E+00i,7.0000E+00+8.0000E+00i,-9.0000E+00+2.0000E+00i,7.0000E+00-9.0000E+00i;8.0000E+00+4.0000E+00i,-3.0000E+00-4.0000E+00i,5.0000E+00+4.0000E+00i,-4.0000E+00-4.0000E+00i;-8.0000E+00+9.0000E+00i,7.0000E+00-6.0000E+00i,-5.0000E+00+1.0000E+00i,3.0000E+00-8.0000E+00i");
   		//aMatrix = new MatrixComplex("-9.0000E+00-4.0000E+00i,-3.0000E+00-6.0000E+00i,5.0000E+00-7.0000E+00i,-4.0000E+00+6.0000E+00i;-4.0000E+00-6.0000E+00i,1.0000E+00+2.0000E+00i,-8.0000E+00-1.0000E+00i,5.0000E+00-1.0000E+00i;5.0000E+00+5.0000E+00i,-9.0000E+00-9.0000E+00i,6.0000E+00-3.0000E+00i,9.0000E+00+7.0000E+00i;-6.0000E+00-6.0000E+00i,6.0000E+00+9.0000E+00i,-7.0000E+00-1.0000E+00i,-7.0000E+00-5.0000E+00i");
   		//aMatrix = new MatrixComplex("4.0000E+00+6.0000E+00i,-4.0000E+00-2.0000E+00i,-1.0000E+00-8.0000E+00i,-5.0000E+00+3.0000E+00i;7.0000E+00-1.0000E+00i,3.0000E+00+6.0000E+00i,-8.0000E+00-1.0000E+00i,4.0000E+00+6.0000E+00i;-9.0000E+00-5.0000E+00i,2.0000E+00+2.0000E+00i,2.0000E+00+1.0000E+00i,6.0000E+00-2.0000E+00i;2.0000E+00-1.0000E+00i,5.0000E+00-7.0000E+00i,-7.0000E+00+5.0000E+00i,7.0000E+00-6.0000E+00i");
   		//aMatrix = new MatrixComplex("5.0000E+00+9.0000E+00i,4.0000E+00+3.0000E+00i,9.0000E+00-4.0000E+00i,4.0000E+00+3.0000E+00i;-5.0000E+00-1.0000E+00i,6.0000E+00+3.0000E+00i,-2.0000E+00+7.0000E+00i,-6.0000E+00-7.0000E+00i;-5.0000E+00+8.0000E+00i,-3.0000E+00-3.0000E+00i,8.0000E+00-8.0000E+00i,3.0000E+00+1.0000E+00i;2.0000E+00+4.0000E+00i,1.0000E+00-9.0000E+00i,3.0000E+00-8.0000E+00i,8.0000E+00+6.0000E+00i");
   		//aMatrix = new MatrixComplex("6.0000E+00-8.0000E+00i,-1.0000E+00+4.0000E+00i,2.0000E+00-6.0000E+00i,-6.0000E+00-6.0000E+00i;-9.0000E+00-2.0000E+00i,9.0000E+00+3.0000E+00i,7.0000E+00+2.0000E+00i,-6.0000E+00-3.0000E+00i;-2.0000E+00-7.0000E+00i,-5.0000E+00+1.0000E+00i,6.0000E+00+2.0000E+00i,3.0000E+00+9.0000E+00i;-3.0000E+00-5.0000E+00i,-6.0000E+00+2.0000E+00i,7.0000E+00+7.0000E+00i,9.0000E+00+9.0000E+00i");
   		//aMatrix = new MatrixComplex("8.8000E+00+8.8000E+00i,-3.1000E+00+8.1000E+00i,4.8000E+00+5.2000E+00i,-3.3000E+00-7.0000E+00i;-7.7000E+00+5.2000E+00i,4.9000E+00-7.2000E+00i,-3.0000E-01-9.4000E+00i,-1.9000E+00+9.8000E+00i;1.2000E+00-6.5000E+00i,-1.5000E+00+1.0000E+00i,8.8000E+00-6.2000E+00i,-3.0000E-01-4.8000E+00i;3.6000E+00-9.1000E+00i,-1.9000E+00+6.3000E+00i,-6.3000E+00-5.0000E+00i,7.7000E+00+4.6000E+00i");
   		//aMatrix = new MatrixComplex("9.9965E-01+8.2455E-01i,-8.4504E-02+5.1901E-01i,5.4669E-01-9.8895E-01i,-5.5181E-01-6.0087E-03i;1.1722E-01+1.9899E-01i,8.5415E-01+5.9168E-01i,5.5369E-01-9.4032E-01i,-7.5868E-02-7.9599E-01i;-3.7707E-01+1.9485E-01i,-6.6817E-01-8.8771E-01i,6.3446E-01+6.0898E-02i,-3.6794E-01-5.7115E-01i;-1.6360E-01-8.2016E-01i,6.1370E-01-5.2810E-01i,-4.9716E-01-8.3041E-01i,8.0638E-01+3.1857E-01i");
   		//aMatrix = new MatrixComplex("1.1722E-01-1.5053E-01i,-5.5884E-01-3.8447E-01i,-9.2112E-01-9.7696E-01i,-1.4038E-01+8.8072E-01i;8.7358E-01-4.8810E-02i,8.6025E-01-9.1187E-01i,3.8844E-01+5.8819E-02i,1.3533E-01-4.3100E-01i;2.7529E-01-1.5703E-01i,2.6324E-02+8.7946E-01i,7.4067E-01+5.4014E-02i,2.3589E-01+2.3846E-01i;-3.0995E-01-8.3048E-01i,2.8727E-01+6.2480E-02i,1.9300E-03+3.1645E-01i,8.4044E-01-9.8260E-01i");
   		//aMatrix = new MatrixComplex("5.0539E-01-5.6537E-01i,3.9249E-01+1.3240E-01i,-4.4088E-01+5.1132E-01i,-9.0087E-02-5.7859E-01i;-1.1502E-01-2.1595E-01i,4.7827E-01-6.6181E-01i,9.0946E-01-8.0134E-01i,4.8726E-01+3.9232E-01i;-5.5107E-02+6.8987E-02i,3.8854E-01-5.9182E-01i,6.5047E-01-2.1865E-01i,-8.1978E-01-2.6071E-01i;-4.5971E-01-8.5725E-01i,4.0398E-01-7.5306E-01i,6.8209E-01-3.9559E-01i,2.7947E-01-9.8192E-01i");
   		//aMatrix = new MatrixComplex("9.1000E+01+3.4000E+01i,-4.5000E+01+2.3000E+01i,-9.9000E+01-8.1000E+01i,-4.9000E+01+7.4000E+01i;-4.3000E+01-7.0000E+00i,9.9000E+01-8.0000E+00i,6.3000E+01+6.9000E+01i,-1.1000E+01-1.1000E+01i;9.1000E+01+3.3000E+01i,3.7000E+01+1.5000E+01i,7.8000E+01-4.0000E+01i,-1.3000E+01+8.1000E+01i;4.7000E+01+1.2000E+01i,7.7000E+01-5.0000E+00i,9.0000E+00-1.6000E+01i,3.7000E+01+7.2000E+01i");
    	
   		Complex.printBoxTitleRandom(boxSize, "MATRIX LOGARITHMS");
   		/** /
   		aMatrix = new MatrixComplex("+5.0000E+00,-1.0000E+00;-7.0000E+00,-9.0000E+00");
   		bMatrix = new MatrixComplex("+8.0000E+00,+6.0000E+00;-9.0000E+00,-4.0000E+00");
   		/**/
   		
   		/** /
   		aMatrix = new MatrixComplex("-9.0000E+00,-6.0000E+00;-7.0000E+00,-1.0000E+00");
   		bMatrix = new MatrixComplex("+5.0000E+00,-1.0000E+00;-7.0000E+00,-9.0000E+00");
   		/**/
   		
   		/** /
   		aMatrix = new MatrixComplex("2.0000E+00,5.0000E+00,-6.0000E+00,9.0000E+00;-9.0000E+00,4.0000E+00,5.0000E+00,-9.0000E+00;6.0000E+00,5.0000E+00,6.0000E+00,-3.0000E+00;-8.0000E+00,6.0000E+00,-8.0000E+00,7.0000E+00").divides(100);
  		bMatrix = new MatrixComplex("6.0000E+00,9.0000E+00,-3.0000E+00,-4.0000E+00;-8.0000E+00,8.0000E+00,-9.0000E+00,1.0000E+00;5.0000E+00,6.0000E+00,4.0000E+00,-6.0000E+00;-6.0000E+00,-5.0000E+00,6.0000E+00,5.0000E+00").divides(100);
   		/**/
   	 
   		/** /
   		aMatrix = new MatrixComplex("+5.0000E+00");
   		bMatrix = new MatrixComplex("+8.0000E+00");
   		/**/
   		
   		/** /
   		aMatrix = new MatrixComplex("+5.0000E+00,-1.0000E+00;-7.0000E+00,-9.0000E+00");
   		bMatrix = new MatrixComplex("+8.0000E+00,+6.0000E+00;-9.0000E+00,-4.0000E+00");
   		/**/
   		
   		/** /
   		aMatrix = new MatrixComplex("-7.0000E+00,+7.0000E+00,+7.0000E+00;-6.0000E+00,+9.0000E+00,-9.0000E+00;-8.0000E+00,+7.0000E+00,+7.0000E+00");
   		bMatrix = new MatrixComplex("+7.0000E+00,+2.0000E+00,-5.0000E+00;-9.0000E+00,+9.0000E+00,-8.0000E+00;-2.0000E+00,+7.0000E+00,+9.0000E+00");
   		/**/
   		
   		/** /
   		aMatrix = new MatrixComplex("+9.0000E+00,-8.0000E+00,-6.0000E+00;+7.0000E+00,+7.0000E+00,-1.0000E+00;+3.0000E+00,-4.0000E+00,+3.0000E+00");
  		bMatrix = new MatrixComplex("+7.0000E+00,+2.0000E+00,-5.0000E+00;-9.0000E+00,+9.0000E+00,-8.0000E+00;-2.0000E+00,+7.0000E+00,+9.0000E+00");
   		/**/

   		/** /
  		aMatrix = new MatrixComplex("+4.2953E+00,+1.5100E+00,+1.6673E+00;-8.4239E+00,+2.0814E+00,-4.3973E-01;+8.9421E+00,+1.8956E+00,+8.3553E+00");
  		bMatrix = new MatrixComplex("+7.0000E+00,+2.0000E+00,-5.0000E+00;-9.0000E+00,+9.0000E+00,-8.0000E+00;-2.0000E+00,+7.0000E+00,+9.0000E+00");
   		/**/

   		/** /
  		aMatrix = new MatrixComplex("+8.8448E+00,-7.7726E+00,+7.3759E+00;+3.5790E+00,+3.7082E+00,-5.1423E+00;+2.8127E-01,-2.7909E+00,+6.8404E+00");
  		bMatrix = new MatrixComplex("+4.2953E+00,+1.5100E+00,+1.6673E+00;-8.4239E+00,+2.0814E+00,-4.3973E-01;+8.9421E+00,+1.8956E+00,+8.3553E+00");
   		/**/

   		/** /
  		aMatrix = new MatrixComplex("-1.7119E+00,-6.8578E+00;+4.2826E+00,-9.2151E-01");
  		bMatrix = new MatrixComplex("-7.5412E+00,+5.5390E+00;-5.2840E+00,-4.5673E+00");
   		/**/

   		/** /
  		aMatrix = new MatrixComplex("-8.1403E-01,-8.4982E+00;+3.0633E+00,+6.2213E+00");
  		bMatrix = new MatrixComplex("-8.1403E-01,+3.0633E+00;-8.4982E+00,+6.2213E+00");
   		/**/

  		/** /
   		aMatrix = new MatrixComplex("-8.0000E+00,+4.0000E+00;+7.0000E+00,+8.0000E+00");
   		//aMatrix = new MatrixComplex("-8.0000E+00,+7.0000E+00;+4.0000E+00,+8.0000E+00");
   		bMatrix = new MatrixComplex("-8.0000E+00,+7.0000E+00;+4.0000E+00,+8.0000E+00");
  		/**/
   		
 		/** /
  		aMatrix = new MatrixComplex("+2.0000E+00,+2.0000E+00,+5.0000E+00,-2.0000E+00,-5.0000E+00,+9.0000E+00,+2.0000E+00;+3.0000E+00,-1.0000E+00,+4.0000E+00,-9.0000E+00,+8.0000E+00,+1.0000E+00,-8.0000E+00;-2.0000E+00,-3.0000E+00,+8.0000E+00,-2.0000E+00,+2.0000E+00,+3.0000E+00,-4.0000E+00;+7.0000E+00,+6.0000E+00,+2.0000E+00,+7.0000E+00,+1.0000E+00,-9.0000E+00,+3.0000E+00;+1.0000E+00,-8.0000E+00,+8.0000E+00,+1.0000E+00,+6.0000E+00,-7.0000E+00,-9.0000E+00;-9.0000E+00,+1.0000E+00,+6.0000E+00,+9.0000E+00,+1.0000E+00,+6.0000E+00,-9.0000E+00;+5.0000E+00,+7.0000E+00,-4.0000E+00,-5.0000E+00,-4.0000E+00,+8.0000E+00,+5.0000E+00");
  		bMatrix = new MatrixComplex("+3.0000E+00,+2.0000E+00,-7.0000E+00,+3.0000E+00,-3.0000E+00,+3.0000E+00,+4.0000E+00;+1.0000E+00,+9.0000E+00,-1.0000E+00,-7.0000E+00,-5.0000E+00,+5.0000E+00,-9.0000E+00;+5.0000E+00,+4.0000E+00,+9.0000E+00,+3.0000E+00,-2.0000E+00,-3.0000E+00,-4.0000E+00;+4.0000E+00,-2.0000E+00,+4.0000E+00,+4.0000E+00,-7.0000E+00,+8.0000E+00,+3.0000E+00;+4.0000E+00,+4.0000E+00,-7.0000E+00,+5.0000E+00,+8.0000E+00,-4.0000E+00,-9.0000E+00;+8.0000E+00,+2.0000E+00,-8.0000E+00,-2.0000E+00,-3.0000E+00,+9.0000E+00,-6.0000E+00;-6.0000E+00,-2.0000E+00,+6.0000E+00,+9.0000E+00,+9.0000E+00,-3.0000E+00,+5.0000E+00");
 		/**/
 
 		/** /
  		aMatrix = new MatrixComplex("+8.0000E+00,+4.0000E+00,-6.0000E+00,+5.0000E+00,-5.0000E+00,+3.0000E+00;-7.0000E+00,+8.0000E+00,-7.0000E+00,+9.0000E+00,-5.0000E+00,+8.0000E+00;+2.0000E+00,+1.0000E+00,+2.0000E+00,+6.0000E+00,-9.0000E+00,-8.0000E+00;-4.0000E+00,-4.0000E+00,+7.0000E+00,+9.0000E+00,-8.0000E+00,-1.0000E+00;+2.0000E+00,-6.0000E+00,-2.0000E+00,+6.0000E+00,+4.0000E+00,+3.0000E+00;+7.0000E+00,-5.0000E+00,+5.0000E+00,-4.0000E+00,+4.0000E+00,+8.0000E+00");
  		bMatrix = new MatrixComplex("+3.0000E+00,+9.0000E+00,-8.0000E+00,+4.0000E+00,+2.0000E+00,-7.0000E+00;-8.0000E+00,+6.0000E+00,-2.0000E+00,+5.0000E+00,+4.0000E+00,+8.0000E+00;+3.0000E+00,-8.0000E+00,+9.0000E+00,+5.0000E+00,-1.0000E+00,-1.0000E+00;-4.0000E+00,+7.0000E+00,-5.0000E+00,+6.0000E+00,-2.0000E+00,-3.0000E+00;-9.0000E+00,-2.0000E+00,+2.0000E+00,+3.0000E+00,+8.0000E+00,+3.0000E+00;+2.0000E+00,+5.0000E+00,-6.0000E+00,+7.0000E+00,-4.0000E+00,-1.0000E+00");
 		/**/
 
 		/** /
  		aMatrix = new MatrixComplex("+7.0000E+00,-1.0000E+00,+5.0000E+00,+8.0000E+00;-2.0000E+00,+7.0000E+00,-2.0000E+00,-8.0000E+00;+2.0000E+00,-2.0000E+00,+7.0000E+00,+7.0000E+00;-7.0000E+00,-4.0000E+00,+2.0000E+00,+8.0000E+00");
  		bMatrix = new MatrixComplex("+7.0000E+00,+2.0000E+00,-2.0000E+00,+6.0000E+00;-5.0000E+00,+3.0000E+00,+2.0000E+00,-3.0000E+00;+3.0000E+00,+6.0000E+00,+6.0000E+00,-7.0000E+00;+6.0000E+00,+4.0000E+00,+4.0000E+00,+8.0000E+00");
 		/**/
 
 		/** /
  		aMatrix = new MatrixComplex("+7.0000E+00,+8.0000E+00;+7.0000E+00,+9.0000E+00");
  		bMatrix = new MatrixComplex("+7.0000E+00,+8.0000E+00;+7.0000E+00,+4.0000E+00");
		/**/
 
 		/** /
  		aMatrix = new MatrixComplex("+5.2000E+01,-2.8000E+01;-1.2000E+01,+8.1000E+01");
  		bMatrix = new MatrixComplex("-2.1000E+01,+2.6000E+01;-8.4000E+01,-4.1000E+01");
  		/**/

 		/**/
  		bMatrix = new MatrixComplex("+5.2000E+01,-2.8000E+01;-1.2000E+01,+8.1000E+01");
  		aMatrix = new MatrixComplex("-2.1000E+01,+1.0000E+00;-0.0000E+00,-4.1000E+01");
  		/**/
   		
   		aMatrix.println("aMatrix");
		aMatrix.determinant().println("aMatrix.determinant():");
   		bMatrix.println("bMatrix");


		Complex.printBoxTitleRandom(boxSize, "LOG MATRICIAL BASE MATRICIAL");
		Complex.printBoxTextRandom(boxSize, "--- aMatrix.logbase(bMatrix) ---");
		cMatrix = aMatrix.logbase(bMatrix);
		cMatrix.println("cMatrix = aMatrix.logbase(bMatrix)");
		
		Complex.printBoxTextRandom(boxSize, "--- CHECK (1) bMatrix.power(cMatrix) ---");
		dMatrix = bMatrix.power(cMatrix);
		dMatrix.println("dMatrix = bMatrix.power(cMatrix)");
		dMatrix.determinant().println("dMatrix.determinant():");
		
		Complex.printBoxTextRandom(boxSize, "--- CHECK (2) bMatrix.power(cMatrix) ---");
		aMatrix.divides(dMatrix).println("aMatrix.divides(dMatrix)");
		aMatrix.divides(dMatrix).determinant().println("aMatrix.divides(dMatrix).determinant():");
		double aMatNorm = aMatrix.divides(dMatrix).norm();
		aMatrix.divides(dMatrix).divides(aMatNorm).println("aMatrix.divides(dMatrix).divides(aMatNorm)");
		aMatrix.trace().println("aMatrix.trace()");
		aMatrix.divides(dMatrix).divides(aMatrix.trace()).println("aMatrix.divides(dMatrix).divides(aMatrix.trace())");

		Complex.printBoxTextRandom(boxSize, "--- CHECK (3) bMatrix.power(cMatrix) ---");
		Eigenspace aMatrixEigen = new Eigenspace(aMatrix);
		aMatrixEigen.eigenvalues().println("aMatrixEigen.eigenvalues()");
		aMatrixEigen.eigenvectors().println("aMatrixEigen.eigenvectors()");
		
		Eigenspace dMatrixEigen = new Eigenspace(dMatrix);
		dMatrixEigen.eigenvalues().println("dMatrixEigen.eigenvalues()");
		dMatrixEigen.eigenvectors().println("dMatrixEigen.eigenvectors()");

		Eigenspace logbaseEigen = new Eigenspace(cMatrix);
		logbaseEigen.eigenvalues().println("logbaseEigen.eigenvalues()");

		Complex.printBoxTextRandom(boxSize, "--- CHECK (4) bMatrix.power(cMatrix) ---");
		eMatrix = dMatrix.logbase(bMatrix);
		eMatrix.println("eMatrix = dMatrix.logbase(bMatrix)");
		eMatrix.determinant().println("eMatrix.determinant():");
		cMatrix.println("cMatrix = aMatrix.logbase(bMatrix)");
		cMatrix.determinant().println("cMatrix.determinant():");
		fMatrix = bMatrix.power(eMatrix);
		fMatrix.println("fMatrix = bMatrix.power(eMatrix)");
		fMatrix.determinant().println("fMatrix.determinant():");
		dMatrix.println("dMatrix = bMatrix.power(cMatrix)");
		dMatrix.determinant().println("dMatrix.determinant():");
		aMatrix.println("aMatrix");
		aMatrix.determinant().println("aMatrix.determinant():");
	}
}
