package com.ipserc.combinatoric;

import java.util.List;
import java.util.*;

public class CombinationNoReps {
	
	public long factorial(int num) {
		long factorial;
		if (num == 0) return 1;
		else factorial = num * factorial(--num);
		return factorial;
	}
	
	public int numberOf(int grade, int order) {
		return (int) (factorial(grade)/factorial(order)/factorial(grade-order));
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