package TestComplex;

import com.ipserc.arith.polynom.*;
import com.ipserc.arith.matrixcomplex.*;
import java.util.ArrayList;
import java.util.List;

public class MixRealComplexEqs01 {
	
	private static void showResults(Polynom polynom) {
		MatrixComplex roots = new MatrixComplex();

		roots = polynom.solve();
		roots.println("Raices");
		polynom.println("The result");
		System.out.println("WOLFRAM:"+polynom.toWolfram());
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

		polynom = polynom.fromRoots("5,-1+2i,3,-4-4i,-2,-4");
		showResults(polynom);
		
	}

}
