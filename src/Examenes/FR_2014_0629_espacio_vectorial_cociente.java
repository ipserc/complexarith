package Examenes;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;

public class FR_2014_0629_espacio_vectorial_cociente {

	static int boxSize = 65;

	public static void main(String[] args) {
		MatrixComplex subEspVectF = new MatrixComplex(" 1, 1,-1, 2; 1,-1, 3, 6");
    	Complex.printBoxTitle(1, boxSize, "Espacio vectorial cociente");
		System.out.println("\nhttps://fernandorevilla.es/2014/06/29/espacio-vectorial-cociente/\n");
    	Complex.printBoxText (1, boxSize, "Enunciado                 ");
		System.out.println("Se considera el subespacio F de R⁴ 'subEspVectF'");
		subEspVectF.println("subEspVectF:");
		System.out.println("");

		/* ************************************************
		 * 1) Hallar una base de F.
		 ************************************************ */
    	Complex.printBoxText(2, boxSize, "1) Hallar una base de F.");
		MatrixComplex baseF = subEspVectF.completeRows().ker();

		// Results
    	baseF.println("Base F: subEspVectF.completeRows().ker()");
		System.out.println("Rank(Base F) = " + baseF.rank() + " -> Base F es una BASE de F 'subEspVectF'");

		/* ************************************************
		 * 2) Hallar una base de R⁴/F.
		 ************************************************ */
	   	Complex.printBoxText(3, boxSize, "2) Hallar una base de R⁴/F.");
		// complete the ker() with more vectors LI till reach the max dimension
		MatrixComplex baseFaug = new MatrixComplex(baseF.cols());
		for (int row = 0; row < baseFaug.rows(); ++row)
			if (row < baseF.rows())
				baseFaug.setRow(row, baseF.getRow(row));
			else
				for(int col = 0; col < baseFaug.cols(); ++col)
					baseFaug.setItem(row, col, col == row ? Complex.ONE : Complex.ZERO); 
		
		// Results
    	baseFaug.println("Base F aug: Base F completada con filas linealmente independientes hasta el rango de R⁴");
		System.out.println("Rank(Base F aug) = " + baseFaug.rank() + " -> Base F aug es una BASE de R⁴/F");

		/* ************************************************
		 * 3) Hallar las coordenadas del vector en la base hallada en el apartado anterior.
		 ************************************************ */
		
		
	}

}
