package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;

public class TestKernel01 {

	static public void shoResults(MatrixComplex aMatrix) {
		int boxSize = 65;
		MatrixComplex aMatrixKer;

		System.out.println(Complex.boxTitleRandom(boxSize, "KERNEL TEST"));
		aMatrix.println("Lineal Transformation");
		System.out.println("aMatrix:" + aMatrix.toMatrixComplex());
		System.out.println("Octave [kern] = null(" + aMatrix.toOctave()+")");		
		System.out.println("Vect.Space Dimension:" +  aMatrix.rows());
		System.out.println("Vect.Space Rank     :" +  aMatrix.rank());
		System.out.println("Vect.Space Nullity  :" +  aMatrix.nullity());

		aMatrixKer = aMatrix.ker();
		System.out.println(Complex.boxTextRandom(boxSize, "Kernel subspace"));
		aMatrixKer.println("Matrix Kernel");
		System.out.println("Matrix Kernel Rank:" + aMatrixKer.rank());
		
		System.out.println(Complex.boxTextRandom(boxSize, "Check Kernel subspace"));
		for (int i = 0; i < aMatrix.nullity(); ++i) {
			aMatrixKer.getRow(i).println("vector "+i);
			aMatrixKer.getRow(i).times(aMatrix.transpose()).println("In vetor row");
			//aMatrix.times(aMatrixKer.getRow(i).transpose()).println("In vetor col");
		}
	}
	
    public static int random(int max)
    {
        // define the range
        int min = 0;
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }

    public static MatrixComplex generateEVbase(int nbrUnknowns) {
    	MatrixComplex aMatrix;
		int nbrNullRows = nbrUnknowns-2;
		
		aMatrix = new MatrixComplex(nbrUnknowns);
		aMatrix.initMatrixRandomInteger(7);
		
		//aMatrix.setItem(0, aMatrix.cols()-1, Complex.ZERO);
		for (int row = 1; row < aMatrix.rows(); ++row) {
			//aMatrix.setItem(row, aMatrix.cols()-1, Complex.ZERO);
			for (int col = 0; col < row; ++col) {
				aMatrix.setItem(row, col, Complex.ZERO);
			}
			//aMatrix.setItem(row, aMatrix.rows()-1, Complex.ZERO);
		}
		
		for (int nullRow = 0; nullRow < nbrNullRows; ++nullRow) {
			int row = random(nbrNullRows)+1;
			for (int col = 0; col < aMatrix.cols(); ++col) {
				aMatrix.setItem(row, col, Complex.ZERO);
			}
		}
		aMatrix.heap();
		
		for (int row = 1; row < aMatrix.rows(); ++row) {
			int aRow = random(nbrNullRows)+1;
			int coef = random(5)+1; coef = random(1) == 1 ? coef : -coef;
			aMatrix.Ftransf(aRow, 0, coef);
		}
		
		for (int row = 0; row < aMatrix.rows(); ++row) {
			int aRow = random(aMatrix.rows()-1);
			int coef = random(5)+1; coef = random(1) == 1 ? coef : -coef;
			aMatrix.Ftransf(aRow, row, coef);
		}
		return aMatrix;
    }

	
	public static void main(String[] args) {
		MatrixComplex aMatrix;
		
		Complex.setFormatON();
		Complex.setFixedON(3);
		Complex.Exact = true;
		
		/* * /
		aMatrix = new MatrixComplex(""
				+ "1, 0,-3, 0, 2,-8;"
				+ "0, 1, 5, 9,-1, 4;"
				+ "0, 0, 0, 1, 7,-9;"
				+ "0, 0, 0, 0, 0, 0;"
				+ "0, 0, 0, 0, 0, 0;"
				+ "0, 0, 0, 0, 0, 0");
		shoResults(aMatrix);
		//From wikipedia https://en.wikipedia.org/wiki/Kernel_(linear_algebra)
		MatrixComplex kernSol1 = new MatrixComplex("-2, 1, 0, -7, 1, 0");
		kernSol1.getRow(0).times(aMatrix.transpose()).println();
		/* */
		
		/* * /
		aMatrix = new MatrixComplex(""
				+ "1,2,3;"
				+ "4,5,6;"
				+ "7,8,9");
		shoResults(aMatrix);
		/* */

		/* * /
		aMatrix= new MatrixComplex(""
				+ "1, 0,-3, 0, 2,-8;"
				+ "0, 1, 5, 9,-1, 4;"
				+ "0, 0, 0, 1, 7,-9;"
				+ "1, 0, 2, 0,-4, 0;"
				+ "0, 0, 0, 0, 0, 0;"
				+ "0, 0, 0, 0, 0, 0");
		shoResults(aMatrix);
		/* */
		
		/* * /
		//aMatrix = new MatrixComplex("4.0,-6.0,-4.0,-4.0,2.0,-3.0,5.0,5.0,-2.0;168.0,-250.0,-167.0,-174.0,90.0,-131.0,211.0,205.0,-88.0;112.0,-160.0,-153.0,-163.0,44.0,-113.0,81.0,75.0,-99.0;-96.0,144.0,96.0,96.0,-48.0,72.0,-120.0,-120.0,48.0;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0;-32.0,48.0,32.0,32.0,-16.0,24.0,-40.0,-40.0,16.0;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0;36.0,-54.0,-36.0,-36.0,18.0,-27.0,45.0,45.0,-18.0;-56.0,80.0,79.0,83.0,-20.0,57.0,-37.0,-35.0,51.0");
		aMatrix = new MatrixComplex("-66.0,-61.0,-42.0,2.0,-9.0,-40.0,-46.0,-39.0,101.0;360.0,187.0,161.0,126.0,-79.0,323.0,331.0,-5.0,-402.0;30.0,15.0,13.0,11.0,-7.0,27.0,28.0,-1.0,-33.0;6.0,3.0,2.0,7.0,-5.0,-1.0,3.0,-6.0,-2.0;-162.0,-81.0,-54.0,-54.0,27.0,-108.0,-162.0,-27.0,189.0;-180.0,-90.0,-78.0,-66.0,42.0,-162.0,-168.0,6.0,198.0;120.0,60.0,40.0,40.0,-20.0,80.0,120.0,20.0,-140.0;-30.0,-15.0,-10.0,-10.0,5.0,-20.0,-30.0,-5.0,35.0;0.0,0.0,0.0,15.0,-12.0,-15.0,-9.0,-21.0,27.0");
		//aMatrix = new MatrixComplex("0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0;4.0,-5.0,1.0,3.0,5.0,-2.0,3.0,1.0,3.0;-32.0,40.0,-8.0,-24.0,-40.0,16.0,-24.0,-8.0,-24.0;68.0,-85.0,17.0,51.0,113.0,-58.0,11.0,25.0,83.0;12.0,-15.0,3.0,9.0,8.0,0.0,13.0,-4.0,7.0;24.0,-30.0,6.0,18.0,30.0,-12.0,18.0,6.0,18.0;-12.0,15.0,-3.0,-9.0,-8.0,0.0,-19.0,-1.0,-1.0;24.0,-30.0,6.0,18.0,30.0,-12.0,18.0,6.0,18.0;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0");
		shoResults(aMatrix);
		/* */
		
		/* */
		aMatrix = generateEVbase(6);
		//aMatrix = new MatrixComplex("-10.0,2.0,-12.0,-4.0,-10.0,-6.0;-5.0,1.0,-6.0,-2.0,-5.0,-3.0;100.0,-20.0,144.0,12.0,128.0,44.0;-10.0,2.0,-12.0,-4.0,-10.0,-6.0;35.0,-7.0,60.0,-7.0,56.0,18.0;-25.0,5.0,-30.0,-10.0,-25.0,-9.0");
		//aMatrix = new MatrixComplex("-75.0,-105.0,-15.0,-45.0,15.0,45.0;-20.0,-28.0,-4.0,-12.0,4.0,12.0;-10.0,-14.0,-8.0,0.0,8.0,2.0;-125.0,-175.0,-43.0,-57.0,43.0,70.0;50.0,70.0,10.0,30.0,-10.0,-30.0;135.0,189.0,45.0,63.0,-45.0,-76.0");
		//aMatrix = new MatrixComplex("7.0,-7.0,-2.0,7.0,5.0,-6.0;-231.0,233.0,89.0,-104.0,-76.0,176.0;980.0,-1004.0,-197.0,893.0,781.0,-863.0;0.0,0.0,-5.0,-20.0,-16.0,4.0;-245.0,251.0,49.0,-224.0,-196.0,216.0;-35.0,35.0,10.0,-35.0,-25.0,30.0");
		shoResults(aMatrix);
		/* */

		/* * /
		aMatrix = new MatrixComplex("-7.0,7.0,1.0,-5.0,2.0,-5.0,5.0,2.0,-1.0;-63.0,63.0,9.0,-45.0,18.0,-45.0,45.0,18.0,-9.0;1211.0,-1211.0,-18.0,989.0,-315.0,989.0,-741.0,-253.0,297.0;-35.0,35.0,10.0,-21.0,11.0,-21.0,29.0,13.0,-1.0;0.0,0.0,0.0,0.0,4.0,-3.0,-1.0,7.0,-4.0;14.0,-14.0,-2.0,10.0,-4.0,10.0,-10.0,-4.0,2.0;0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0;203.0,-203.0,-4.0,165.0,-53.0,165.0,-125.0,-43.0,49.0;0.0,0.0,0.0,0.0,-36.0,27.0,9.0,-63.0,57.0");
		aMatrix = new MatrixComplex(""
				+ "-7.0,7.0,1.0,-5.0,2.0,-5.0,5.0,2.0,-1.0;"
				+ "-63.0,63.0,9.0,-45.0,18.0,-45.0,45.0,18.0,-9.0;"
				+ "1211.0,-1211.0,-18.0,989.0,-315.0,989.0,-741.0,-253.0,297.0;"
				+ "-35.0,35.0,10.0,-21.0,11.0,-21.0,29.0,13.0,-1.0;"
				+ "0.0,0.0,0.0,0.0,4.0,-3.0,-1.0,7.0,-4.0;"
				+ "14.0,-14.0,-2.0,10.0,-4.0,10.0,-10.0,-4.0,2.0;"
				+ "0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0;"
				+ "203.0,-203.0,-4.0,165.0,-53.0,165.0,-125.0,-43.0,49.0;"
				+ "0.0,0.0,0.0,0.0,-36.0,27.0,9.0,-63.0,57.0");
		System.out.println("aMatrix:" + aMatrix.toMatrixComplex());
		System.out.println("aMatrix rank():" + aMatrix.rank());


		MatrixComplex kernBase = new MatrixComplex(""
				+"-0.7845,-0.2217, 0.1345, 0.0711,-0.1367;"
				+"-0.5436, 0.6249,-0.0032, 0.1403, 0.0284;"
				+"-0.2042,-0.1971,-0.1684,-0.5235, 0.5435;"
				+" 0.1884, 0.3541, 0.4498, 0.4226, 0.3783;"
				+" 0.0473, 0.1556, 0.6011,-0.5387,-0.3234;"
				+" 0.0899, 0.3549,-0.3344,-0.1882,-0.5545;"
				+"-0.0394,-0.4959, 0.2801, 0.3470,-0.3457;"
				+" 0.0059,-0.0076,-0.4468, 0.2768,-0.1022;"
				+" 0,0,0,0,0");
		kernBase = kernBase.transpose();
		System.out.println("kernBase:" + kernBase.toMatrixComplex());
		System.out.println("kernBase rank():" + kernBase.rank());
		
		for (int row = 0; row < kernBase.rows(); ++row) {
			aMatrix.times(kernBase.getRow(row).transpose()).transpose().println("Check");
		}
		/* */

	}

}
