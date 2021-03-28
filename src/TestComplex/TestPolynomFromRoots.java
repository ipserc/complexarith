package TestComplex;

import com.ipserc.arith.polynom.*;
import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.*;
import java.util.ArrayList;
import java.util.List;

public class TestPolynomFromRoots {
	
	private static void showResults(Polynom polynom) {
		MatrixComplex roots = new MatrixComplex();
		int boxSize = 65;
		
		System.out.println(Complex.boxTextRandom(boxSize, "Polynomial Solutions"));   	
		roots = polynom.solve();
		roots.println("Roots");
		polynom.println("POLYNOM:");
		System.out.println(polynom.toMaxima_poly("MAXIMA : "));
		System.out.println(polynom.toOctave_poly("OCTAVE : "));
		System.out.println(polynom.toWolfram_poly("WOLFRAM: "));
		System.out.println(polynom.toPolynom());
	}

	public static Polynom calcPolynom(Polynom[] term) {
		Polynom polynom;
		int i = 0;
		
		polynom = term[i++];
		while (term[i] != null) {
			polynom = polynom.times(term[i++]);
		}
		return polynom;
	}

	public static Polynom calcPolynom2(List<Polynom> listTerm) {
		Polynom polynom = new Polynom("1");
		for (Polynom term : listTerm) {
			polynom = polynom.times(term);
		}
		return polynom;
	}

	public static void main(String[] args) {
		Polynom polynom;
		int i;
		Polynom[] term = new Polynom[10];
		int boxSize = 65;
		
		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL FROM ROOTS TEST"));   	
	
		Complex.setFormatON();
		Complex.setFixedON(3);
		i = 0;
		term[i++] = new Polynom("1, -2");
		term[i++] = new Polynom("1-i, 1");
		term[i++] = new Polynom("1, -2+1i");
		term[i++] = new Polynom("2, 1");
		term[i++] = null;
		polynom = calcPolynom(term);
		showResults(polynom);
		
		i = 0;   
		term[i++] = new Polynom("1, -2");
		term[i++] = new Polynom("1,  i");
		term[i++] = new Polynom("1, -2i");
		term[i++] = new Polynom("1,  2");
		term[i++] = null;		
		polynom = calcPolynom(term);
		showResults(polynom);
        
		i = 0;   
		term[i++] = new Polynom("1,  i");
		term[i++] = new Polynom("1,  0.5i");
		term[i++] = new Polynom("1, -2i");
		term[i++] = new Polynom("1, -i");
		term[i++] = null;		
		polynom = calcPolynom(term);
		showResults(polynom);
		
		i = 0;   
		term[i++] = new Polynom("1,  i");
		term[i++] = new Polynom("1, -i");
		term[i++] = new Polynom("1, -2i");
		term[i++] = new Polynom("1,  2i");
		term[i++] = null;		
		polynom = calcPolynom(term);
		showResults(polynom);
        
		i = 0;   
		term[i++] = new Polynom("1,  i");
		term[i++] = new Polynom("1, -i");
		term[i++] = new Polynom("1, -2");
		term[i++] = new Polynom("1,  3");
		term[i++] = null;		
		polynom = calcPolynom(term);
		showResults(polynom);
		
		i = 0;   
		term[i++] = new Polynom("1, -1");
		term[i++] = new Polynom("1, -2");
		term[i++] = new Polynom("1, -3");
		term[i++] = new Polynom("1, -4");
		term[i++] = null;		
		polynom = calcPolynom(term);
		showResults(polynom);
        
		i = 0;   
		term[i++] = new Polynom("1, -5");
		term[i++] = new Polynom("1,  1-2i");
		term[i++] = new Polynom("1, -3");
		term[i++] = new Polynom("1,  4+4i");
		term[i++] = new Polynom("1,  2");
		term[i++] = new Polynom("1,  4");
		term[i++] = null;		
		polynom = calcPolynom(term);
		showResults(polynom);

		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL FROM TERMS (x - root) TEST"));   	
		List<Polynom> listTerm = new ArrayList<Polynom>();
		listTerm.add(new Polynom("1, -5"));
		listTerm.add(new Polynom("1,  1-2i"));
		listTerm.add(new Polynom("1, -3"));
		listTerm.add(new Polynom("1,  4+4i"));
		listTerm.add(new Polynom("1,  2"));
		listTerm.add(new Polynom("1,  4"));
		polynom = calcPolynom2(listTerm);
		showResults(polynom);
		listTerm.clear();

		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL FROM COMPLEX LIST ROOTS TEST"));   	
		List<Complex> listRoots = new ArrayList<Complex>();
		listRoots.add(new Complex("-5"));
		listRoots.add(new Complex("1-2i"));
		listRoots.add(new Complex("-3"));
		listRoots.add(new Complex("4+4i"));
		listRoots.add(new Complex("2"));
		listRoots.add(new Complex("4"));
		polynom = polynom.fromRoots(listRoots);
		showResults(polynom);
		listTerm.clear();
		
		System.out.println(Complex.boxTitleRandom(boxSize, "POLYNOMIAL FROM COMMA SEP. STRING ROOTS TEST"));   	
		polynom = polynom.fromRoots("5,-1+2i,3,-4-4i,-2,-4");
		showResults(polynom);
		
	}

}
