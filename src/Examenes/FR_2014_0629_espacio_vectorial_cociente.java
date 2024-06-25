package Examenes;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.syseq.Syseq;
import com.ipserc.arith.vector.*;

public class FR_2014_0629_espacio_vectorial_cociente {

	static int boxSize = 65;

	public static void main(String[] args) {
		Complex.setFormatON();
		Syseq subEspVectF = new Syseq(" 1, 1,-1, 2, 0; 1,-1, 3, 6, 0");
    	Complex.printBoxTitle(1, boxSize, "Espacio vectorial cociente");
		System.out.println("\nhttps://fernandorevilla.es/2014/06/29/espacio-vectorial-cociente/\n");
    	Complex.printBoxText (1, boxSize, "Enunciado");
		System.out.println("Se considera el subespacio F de R⁴ 'subEspVectF'");
		subEspVectF.print("subEspVectF:");
		System.out.println("");

		/* ************************************************
		 * 1) Hallar una base de F.
		 ************************************************ */
    	Complex.printBoxText(2, boxSize, "1) Hallar una base de F.");
		// MatrixComplex baseF = subEspVectF.completeRows().ker();
    	MatrixComplex baseF = subEspVectF.solution();

		// Results
    	subEspVectF.print("la base generatriz serán las soluciones del sistema de ecuaciones F:");
    	baseF.println("Base F:");
		System.out.println("Rank(Base F) = " + baseF.rank() + " -> Base F es una BASE de F 'subEspVectF'");

		/* ************************************************
		 * 2) Hallar una base de R⁴/F.
		 ************************************************ */
	   	Complex.printBoxText(2, boxSize, "2) Hallar una base de R⁴/F.");
	   	/* APROX 1
		// complete the ker() with more vectors LI till reach the max dimension
		MatrixComplex baseFaug = new MatrixComplex(baseF.cols());
		for (int row = 0; row < baseFaug.rows(); ++row)
			if (row < baseF.rows())
				baseFaug.setRow(row, baseF.getRow(row));
			else
				for(int col = 0; col < baseFaug.cols(); ++col)
					baseFaug.setItem(row, col, col == row ? Complex.ONE : Complex.ZERO); 
		*/
	   	int row, baseFRow;
		MatrixComplex baseR4F = MatrixComplex.eye(subEspVectF.cols()-1); //MatrixComplex.eye(subEspVectF.unkMatrix().cols()); 
		//for (row = 2, baseFRow = 0; row < baseFaug.rows(); ++row, ++baseFRow) {
		for (row = 0, baseFRow = 0; row < baseR4F.rows(); ++row, ++baseFRow) {
			if (baseFRow >= baseF.rows()) break;
			baseR4F.setRow(row, baseF.getRow(baseFRow));
		}	
	   	
		/*
		MatrixComplex baseFaug = new MatrixComplex(baseR4.rows()-row, baseR4.cols());
		for (int rowFaug = row, baseFaugRow = 0; rowFaug < baseR4.rows(); ++rowFaug, ++baseFaugRow) {
			baseFaug.setRow(baseFaugRow, baseR4.getRow(rowFaug)); 
		}
		*/
		
		
		// Results
    	baseR4F.println("Base R4: Base F completada con filas linealmente independientes hasta el rango de R⁴");
		System.out.println("Rank(Base R4 F) = " + baseR4F.rank() + " -> Base F aug es una BASE de R⁴");
		// MatrixComplex baseR4F = baseFaug.plus(baseF);
		baseR4F.println("base R⁴/F");
		
		

		/* ************************************************
		 * 3) Hallar las coordenadas del vector (1,-3,2,6)+F en la base hallada en el apartado anterior.
		 ************************************************ */
	   	Complex.printBoxText(2, boxSize, "3) Las coordenadas del vector (1,-3,2,6)+F en la base hallada en el apartado anterior.");
		Vector vector = new Vector("1,-3,2,6");
		Vector vector2 = vector.clone();
		
		vector.println("Vector");
		baseR4F.println("Base");
		vector2.baseChg(baseR4F).println("Vector en Base de R⁴/F");
		vector2.baseExchg(baseR4F).println("Vector en Base original");
		vector2.baseChg(baseR4F);
		
		//MatrixComplex mat = new MatrixComplex( "-1.0 , 2.0 , 1.0 , 0.0; -4.0 , 2.0 , 0.0 , 1.0; 2.666666666666667 , 0.8333333333333333 , 2.0 , 6.0");
		MatrixComplex mat = baseR4F.appendRows((MatrixComplex)vector2);
		mat.println("Base F ampliada con Vector en Base de R⁴/F");
		System.out.println("Rango " + mat.rank());
		
	}

}
