package com.ipserc.arith.combinatoric;

import java.util.List;
import java.util.*;

/**
 * Combinations without repetition of {@code grade} elements taken {@code order} at a time:
 * factorial/count helpers ({@link #factorial(int)}, {@link #numberOf(int, int)}) plus item-level
 * generation utilities that enumerate the combinations themselves.
 * @author ipserc
 */
public class CombinationNoReps {
	private final static String HEADINFO = "Combinatoric --- INFO: ";
	private final static String VERSION = "1.3 (2026_0811_2200)";
	/* VERSION Release Note
	 *
	 * 1.3 (2026_0811_2200)
	 * Reportado por el usuario tras revisar el HTML generado: la clase no tenía Javadoc real (sin
	 * bloque alguno antes del package/import, ni pegado a la declaración de la clase), así que su
	 * página en doc/ salía sin descripción -- mismo patrón ya visto y arreglado en Eigenspace.java.
	 * Añadido el Javadoc de clase real, sin cambios funcionales.
	 *
	 * 1.2 (2026_0811_1200)
	 * Comentarios Javadoc traducidos al inglés y corregidos (sin cambios funcionales), como parte
	 * de la generación de la documentación de la API.
	 *
	 * 1.1 (2026_0807_1300)
	 * - factorial(int): guarda num<0 -- antes recursaba infinitamente hasta StackOverflowError
	 *   en vez de fallar con un mensaje claro (alcanzable via numberOf() con order>grade).
	 * - numberOf(int,int): reescrito con la formula multiplicativa iterativa (sin pasar por
	 *   factorial()) para evitar el desbordamiento silencioso de long que ya ocurria para
	 *   grade>=21 (confirmado: numberOf(25,1) daba 0 en vez de 25); guarda order<0||order>grade
	 *   y grade<0 con IllegalArgumentException.
	 *
	 * 1.0 (2020_0627_1130)
	 */


	/*
	 * VERSION
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}
	
	public long factorial(int num) {
		if (num < 0) throw new IllegalArgumentException("factorial() no esta definido para negativos: " + num);
		long factorial;
		if (num == 0) return 1;
		else factorial = num * factorial(--num);
		return factorial;
	}

	public int numberOf(int grade, int order) {
		if (grade < 0) throw new IllegalArgumentException("numberOf(): grade debe ser >= 0 (grade=" + grade + ")");
		if (order < 0 || order > grade) throw new IllegalArgumentException("numberOf(): order debe estar en [0, grade] (grade=" + grade + ", order=" + order + ")");
		long result = 1;
		for (int i = 0; i < order; ++i) result = result * (grade - i) / (i + 1);
		return (int) result;
	}
	
/****************
 * Item Methods *
 ****************/

	public long[] initItem(long[] v) {
		for (int i = 0; i < v.length; ++i) v[i] = -1;
		return v;
	}

	public long[] extractItem(long[] item, long[] v, int order) {
		for(int i = 0; i < order; ++i) item[i] = v[i];
		return item;
	}
	
	public long[] getItem(int grade, int order, long itemNbr) {
		int i, j; 
		int itemCount = 0;
		long[] v = new long[grade];
		long[] item = new long[order];
		
		int numOfItems = numberOf(grade, order);
		
		v = initItem(v);
		item = extractItem(item, v, order);
		
		if (itemNbr + 1 > numOfItems) return extractItem(item, v, order); 

		if (order == 0) return extractItem(item, v, order);

		for(i = 0; i < grade; ++i) v[i]=i;
		if (itemNbr == 0) return extractItem(item, v, order);
		
		while (true) {
			i = order-1;
		    while (v[i] == grade-order+i && --i >= 0);
		    if (i < 0) break;
		    v[i] += 1;
		    for (j = i+1; j < order; ++j) v[j] = v[i]+j-i;
		    if (++itemCount == itemNbr) break;
		}
		return extractItem(item, v, order);
	}
	
	public String toStringItem(long[] item) {
		String combine = "";
		int i;
		if (item.length == 0) return "NULL";
		for(i = 0; i < item.length-1; ++i) combine = combine + item[i] + ",";
		combine = combine + item[i];
		return combine;
	}

	public void printlnItem(long[] item) {
		System.out.println(toStringItem(item));
	}
	
/**********************
 * Collection Methods *
 **********************/
	
	public void initCollection(long[][] collection) {
		for (int itemNbr = 0; itemNbr < collection.length; ++itemNbr) {
			collection[itemNbr] = initItem(collection[itemNbr]);
		}
	}
	
	public long[][] getCollection(int grade, int order) {
		int i, j; 
		int itemNbr = 0;
		int numOfItems = numberOf(grade, order);
		long[] v = new long[grade];
		long[][] collection = new long[numOfItems][order];
		
		if (order == 0) return collection;

		for(i = 0; i < grade; ++i) v[i]=i;
	    collection[itemNbr] = extractItem(collection[itemNbr], v, order);
	    ++itemNbr;
		while (true) {
			i = order-1;
		    while (v[i] == grade-order+i && --i >= 0);
		    if (i < 0) break;
		    v[i] += 1;
		    for (j = i+1; j < order; ++j) v[j] = v[i]+j-i;
		    collection[itemNbr] = extractItem(collection[itemNbr], v, order);
		    if (++itemNbr == numOfItems) break;
		}
		return collection;
	}
	
	public String[] toStringCollection(long[][] collection) {
		String[] strColl = new String[collection.length];
		for (int itemNbr = 0; itemNbr < collection.length; ++itemNbr) {
			strColl[itemNbr] = toStringItem(collection[itemNbr]);
		}
		return strColl;
	}
	
	public void printlnCollection(long[][] collection) {
		for (int itemNbr = 0; itemNbr < collection.length; ++itemNbr) {
			System.out.print("itemNbr:" + itemNbr + " - ");
			printlnItem(collection[itemNbr]);
		}
	}
	
/****************************
 * All Combinations Methods *
 ****************************/
	
	public List<long[][]> getAll(int grade) {
		List<long[][]> allCombinations = new ArrayList<>();
		for (int order = 0; order <= grade; ++order) {		
			allCombinations.add(getCollection(grade, order));
		}
		return allCombinations;
	}
	
	public String[][] toStringAll(List<long[][]> allCombinations) {
		String[][] strAllCombi = new String[allCombinations.size()][];
		for (int listItem = 0; listItem < allCombinations.size(); ++listItem) {
			strAllCombi[listItem] = toStringCollection((long[][])allCombinations.get(listItem));
		}
		return strAllCombi;
	}
	
	public void printlAll(List<long[][]> allCombinations) {
		for (int order = 0; order < allCombinations.size(); ++order) {
			System.out.print("order:" + order + "\n");			
			printlnCollection((long[][])allCombinations.get(order));
		}
	}
}