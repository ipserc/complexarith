package TestComplex;

import java.util.*;
import com.ipserc.arith.combinatoric.CombinationNoReps;

public class ScratchCombinationAudit01 {

	static int pass = 0, fail = 0;

	static void check(String label, boolean ok, String detail) {
		if (ok) { pass++; System.out.println("OK   " + label); }
		else { fail++; System.out.println("FAIL " + label + " -- " + detail); }
	}

	// brute-force reference: all order-subsets of {0..grade-1} in the same
	// "colex within lex-of-first-differing-index" order the class claims to produce
	static List<long[]> bruteForce(int grade, int order) {
		List<long[]> result = new ArrayList<>();
		if (order == 0) { result.add(new long[0]); return result; }
		if (order > grade) return result;
		int[] v = new int[order];
		for (int i = 0; i < order; ++i) v[i] = i;
		while (true) {
			long[] item = new long[order];
			for (int i = 0; i < order; ++i) item[i] = v[i];
			result.add(item);
			int i = order - 1;
			while (i >= 0 && v[i] == grade - order + i) --i;
			if (i < 0) break;
			v[i]++;
			for (int j = i + 1; j < order; ++j) v[j] = v[i] + j - i;
		}
		return result;
	}

	static String toStr(long[] a) {
		StringBuilder sb = new StringBuilder("[");
		for (long x : a) sb.append(x).append(",");
		return sb.append("]").toString();
	}

	static void checkGrade(CombinationNoReps c, int grade, int order) {
		List<long[]> expected = bruteForce(grade, order);
		long[][] got = c.getCollection(grade, order);
		boolean ok = got.length == expected.size();
		StringBuilder detail = new StringBuilder();
		if (ok) {
			for (int i = 0; i < got.length; ++i) {
				if (!Arrays.equals(got[i], expected.get(i))) {
					ok = false;
					detail.append("idx ").append(i).append(" got=").append(toStr(got[i]))
						.append(" expected=").append(toStr(expected.get(i)));
					break;
				}
			}
		} else {
			detail.append("length got=").append(got.length).append(" expected=").append(expected.size());
		}
		check("getCollection(grade=" + grade + ",order=" + order + ")", ok, detail.toString());
	}

	public static void main(String[] args) {
		CombinationNoReps c = new CombinationNoReps();

		// 1) exhaustive check against brute force for a spread of grade/order
		for (int grade = 0; grade <= 8; ++grade) {
			for (int order = 0; order <= grade; ++order) {
				checkGrade(c, grade, order);
			}
		}

		// 2) numberOf() matches C(grade,order) via an independent (non-factorial) computation
		for (int grade = 0; grade <= 15; ++grade) {
			for (int order = 0; order <= grade; ++order) {
				long expectedC = binomial(grade, order);
				int got = c.numberOf(grade, order);
				check("numberOf(" + grade + "," + order + ")", got == expectedC,
					"got=" + got + " expected=" + expectedC);
			}
		}

		// 3) getItem(grade,order,n) matches getCollection(grade,order)[n] for all valid n
		for (int grade = 2; grade <= 6; ++grade) {
			for (int order = 0; order <= grade; ++order) {
				long[][] coll = c.getCollection(grade, order);
				for (int n = 0; n < coll.length; ++n) {
					long[] item = c.getItem(grade, order, n);
					check("getItem(" + grade + "," + order + "," + n + ")",
						Arrays.equals(item, coll[n]),
						"got=" + toStr(item) + " expected=" + toStr(coll[n]));
				}
			}
		}

		// 4) regression: numberOf() no longer overflows silently for grade>=21 (was: numberOf(25,1)==0)
		int g = 25, o = 1;
		long expected25 = 25;
		int got25 = c.numberOf(g, o);
		check("numberOf(25,1) no longer overflows", got25 == expected25,
			"got=" + got25 + " expected=" + expected25);

		// 5) regression: order > grade now throws a clean IllegalArgumentException, no StackOverflowError
		boolean threwIAE = false, stackOverflow = false;
		try {
			c.numberOf(3, 5); // order > grade
		} catch (IllegalArgumentException e) {
			threwIAE = true;
		} catch (StackOverflowError e) {
			stackOverflow = true;
		}
		check("numberOf(grade=3,order=5) throws IllegalArgumentException", threwIAE && !stackOverflow,
			"threwIAE=" + threwIAE + " stackOverflow=" + stackOverflow);

		// 6) regression: factorial(negative) throws a clean IllegalArgumentException, no StackOverflowError
		boolean facThrewIAE = false, facStackOverflow = false;
		try {
			c.factorial(-1);
		} catch (IllegalArgumentException e) {
			facThrewIAE = true;
		} catch (StackOverflowError e) {
			facStackOverflow = true;
		}
		check("factorial(-1) throws IllegalArgumentException", facThrewIAE && !facStackOverflow,
			"threwIAE=" + facThrewIAE + " stackOverflow=" + facStackOverflow);

		// 7) numberOf() still matches C(grade,order) for a spread up to grade=40 (beyond the old overflow point)
		for (int grade = 16; grade <= 40; ++grade) {
			for (int order = 0; order <= grade; order += Math.max(1, grade / 5)) {
				long expectedC = binomial(grade, order);
				if (expectedC > Integer.MAX_VALUE) continue; // numberOf() returns int, out of its contract
				int got = c.numberOf(grade, order);
				check("numberOf(" + grade + "," + order + ") [wide range]", got == expectedC,
					"got=" + got + " expected=" + expectedC);
			}
		}

		System.out.println();
		System.out.println("TOTAL pass=" + pass + " fail=" + fail);
	}

	static long binomial(int n, int k) {
		if (k < 0 || k > n) return 0;
		long r = 1;
		for (int i = 0; i < k; ++i) {
			r = r * (n - i) / (i + 1);
		}
		return r;
	}
}
