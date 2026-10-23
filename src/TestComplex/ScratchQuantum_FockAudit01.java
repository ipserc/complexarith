package TestComplex;

import java.util.Random;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Fock;
import com.ipserc.arith.quantum.Qubits;

/**
 * Audit of Fock.java (basis/dimension/permanent/multiPhotonUnitary/measure, and since Fock 1.1 the
 * fermionic side: fermionBasis/determinant/multiFermionUnitary), built for the Hong-Ou-Mandel effect (see Claude/ComplexArithRev.md, Cuadragesimonovena sesion). Every check
 * uses an oracle independent of the code under test: the literal sum-over-permutations definition
 * for the permanent, the group-homomorphism property for Phi_n(U), and the textbook HOM output
 * state worked out by hand.
 */
public class ScratchQuantum_FockAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		// 1. basis(n,m) has dimension(n,m) = C(n+m-1,n) distinct states, each summing to n.
		boolean basisOk = true;
		int[][] expectedDims = { { 1, 1, 1 }, { 1, 4, 4 }, { 2, 2, 3 }, { 2, 4, 10 }, { 3, 3, 10 }, { 4, 3, 15 }, { 3, 5, 35 } };
		for (int[] c : expectedDims) {
			int[][] b = Fock.basis(c[0], c[1]);
			if (Fock.dimension(c[0], c[1]) != c[2] || b.length != c[2]) { basisOk = false; }
			for (int k = 0; k < b.length; ++k) {
				int sum = 0;
				for (int x : b[k]) sum += x;
				if (sum != c[0] || Fock.indexOf(b[k]) != k) { basisOk = false; }
			}
		}
		check("basis(n,m): dimension C(n+m-1,n), totals and indexOf round-trip", basisOk);

		// 2. Order: 2 photons in 2 modes -> |2,0>, |1,1>, |0,2>.
		int[][] b22 = Fock.basis(2, 2);
		check("basis(2,2) order is |2,0>,|1,1>,|0,2>", Fock.basisLabel(b22[0]).equals("|2,0>")
				&& Fock.basisLabel(b22[1]).equals("|1,1>") && Fock.basisLabel(b22[2]).equals("|0,2>"));

		// 3. permanent(all-ones nxn) = n!.
		boolean onesOk = true;
		double fact = 1.0;
		for (int n = 1; n <= 7; ++n) {
			fact *= n;
			MatrixComplex ones = new MatrixComplex(n, n);
			for (int i = 0; i < n; ++i) for (int j = 0; j < n; ++j) ones.setItem(i, j, 1.0);
			Complex p = Fock.permanent(ones);
			if (Math.abs(p.rep() - fact) > 1e-9 * fact || Math.abs(p.imp()) > 1e-9) { onesOk = false; }
		}
		check("permanent(ones(n)) == n!, n=1..7", onesOk);

		// 4. Ryser permanent == literal sum over permutations, random complex nxn, n=1..6.
		Random rnd = new Random(1234);
		boolean ryserOk = true;
		for (int n = 1; n <= 6; ++n) {
			for (int trial = 0; trial < 5; ++trial) {
				MatrixComplex a = new MatrixComplex(n, n);
				for (int i = 0; i < n; ++i) for (int j = 0; j < n; ++j)
					a.setItem(i, j, new Complex(rnd.nextGaussian(), rnd.nextGaussian()));
				Complex ryser = Fock.permanent(a);
				Complex brute = brutePermanent(a, 0, new boolean[n]);
				if (ryser.minus(brute).abs() > 1e-9 * Math.max(1.0, brute.abs())) { ryserOk = false; }
			}
		}
		check("permanent() (Ryser) == sum over permutations, random complex, n=1..6", ryserOk);

		// 5. Phi_1(U) == U (one photon: the Fock operator IS the single-photon unitary).
		MatrixComplex f3 = dft(3);
		check("multiPhotonUnitary(U,1) == U (DFT 3x3)", Fock.multiPhotonUnitary(f3, 1).equals(f3, 10));

		// 6. Homomorphism Phi_n(UV) == Phi_n(U) Phi_n(V), n=2..4 photons, 3 modes, non-commuting
		//    U, V (a DFT and a phase-dressed DFT) -- an oracle independent of the permanent formula.
		MatrixComplex d = new MatrixComplex(3, 3);
		d.setItem(0, 0, new Complex(Math.cos(0.3), Math.sin(0.3)));
		d.setItem(1, 1, new Complex(Math.cos(1.1), Math.sin(1.1)));
		d.setItem(2, 2, new Complex(Math.cos(-2.0), Math.sin(-2.0)));
		MatrixComplex u = f3.times(d);
		MatrixComplex v = d.times(f3).times(d);
		boolean homOk = true;
		for (int n = 2; n <= 4; ++n) {
			MatrixComplex lhs = Fock.multiPhotonUnitary(u.times(v), n);
			MatrixComplex rhs = Fock.multiPhotonUnitary(u, n).times(Fock.multiPhotonUnitary(v, n));
			if (lhs.minus(rhs).norm() > 1e-9) { homOk = false; }
		}
		check("Phi_n(UV) == Phi_n(U)*Phi_n(V), n=2..4, 3 modes", homOk);

		// 7. HOM: Phi_2(H)|1,1> == (|2,0> - |0,2>)/sqrt(2), amplitude on |1,1> exactly 0.
		MatrixComplex out = Fock.multiPhotonUnitary(Qubits.hadamard(), 2).times(Fock.ket(1, 1));
		double s = 1.0 / Math.sqrt(2.0);
		boolean homStateOk = Math.abs(out.getItem(0, 0).rep() - s) < 1e-12
				&& out.getItem(1, 0).abs() < 1e-12
				&& Math.abs(out.getItem(2, 0).rep() + s) < 1e-12
				&& Math.abs(out.getItem(0, 0).imp()) < 1e-12 && Math.abs(out.getItem(2, 0).imp()) < 1e-12;
		check("HOM: Phi_2(H)|1,1> == (|2,0>-|0,2>)/sqrt(2)", homStateOk);

		// 8. Non-unitary input rejected (fail-loud).
		boolean rejected = false;
		MatrixComplex notUnitary = new MatrixComplex(2, 2);
		notUnitary.setItem(0, 0, 2.0);
		notUnitary.setItem(1, 1, 1.0);
		try { Fock.multiPhotonUnitary(notUnitary, 2); } catch (IllegalArgumentException e) { rejected = true; }
		check("multiPhotonUnitary() rejects a non-unitary U", rejected);

		// 9. measure() on a dimension-3 state (not a power of 2) never lands on a zero-amplitude
		//    state, and splits ~50/50 between |2,0> and |0,2>.
		int shots = 20000;
		int[] counts = Fock.measure(out, shots, new Random(42));
		check("measure(): |1,1> never measured after HOM, ~50/50 bunching",
				counts[1] == 0 && Math.abs(counts[0] - shots / 2) < 4 * Math.sqrt(shots / 4.0));

		// ---- Fermions (Fock 1.1) ----

		// 10. fermionBasis(n,m): dimension C(m,n), occupations 0/1 summing to n, indexOf round-trip,
		//     and the order [1,1,0],[1,0,1],[0,1,1] for 2 fermions in 3 modes.
		boolean fBasisOk = true;
		int[][] fermionDims = { { 1, 1, 1 }, { 1, 4, 4 }, { 2, 2, 1 }, { 2, 3, 3 }, { 2, 4, 6 }, { 3, 5, 10 }, { 3, 6, 20 }, { 4, 4, 1 } };
		for (int[] c : fermionDims) {
			int[][] b = Fock.fermionBasis(c[0], c[1]);
			if (Fock.fermionDimension(c[0], c[1]) != c[2] || b.length != c[2]) { fBasisOk = false; }
			for (int k = 0; k < b.length; ++k) {
				int sum = 0;
				for (int x : b[k]) { sum += x; if (x != 0 && x != 1) fBasisOk = false; }
				if (sum != c[0] || Fock.fermionIndexOf(b[k]) != k) { fBasisOk = false; }
			}
		}
		int[][] f23 = Fock.fermionBasis(2, 3);
		fBasisOk &= Fock.basisLabel(f23[0]).equals("|1,1,0>") && Fock.basisLabel(f23[1]).equals("|1,0,1>")
				&& Fock.basisLabel(f23[2]).equals("|0,1,1>");
		check("fermionBasis(n,m): dimension C(m,n), 0/1 occupations, order, fermionIndexOf round-trip", fBasisOk);

		// 11. Pauli: more fermions than modes, or an occupation 2, is rejected.
		boolean pauliOk = false, occOk = false;
		try { Fock.fermionBasis(3, 2); } catch (IllegalArgumentException e) { pauliOk = true; }
		try { Fock.fermionKet(2, 0); } catch (IllegalArgumentException e) { occOk = true; }
		check("Pauli: fermionBasis(3,2) and fermionKet(2,0) rejected", pauliOk && occOk);

		// 12. determinant() == literal signed sum over permutations, random complex nxn, n=1..6,
		//     plus an exactly singular matrix (two equal rows) -> 0.
		boolean detOk = true;
		for (int n = 1; n <= 6; ++n) {
			for (int trial = 0; trial < 5; ++trial) {
				MatrixComplex a = new MatrixComplex(n, n);
				for (int i = 0; i < n; ++i) for (int j = 0; j < n; ++j)
					a.setItem(i, j, new Complex(rnd.nextGaussian(), rnd.nextGaussian()));
				Complex gauss = Fock.determinant(a);
				Complex brute = bruteDeterminant(a, 0, new boolean[n], new int[n]);
				if (gauss.minus(brute).abs() > 1e-9 * Math.max(1.0, brute.abs())) { detOk = false; }
			}
		}
		MatrixComplex singular = new MatrixComplex(3, 3);
		for (int j = 0; j < 3; ++j) {
			singular.setItem(0, j, new Complex(j + 1.0, 0.5));
			singular.setItem(1, j, new Complex(j + 1.0, 0.5));
			singular.setItem(2, j, new Complex(-j, 2.0));
		}
		detOk &= Fock.determinant(singular).abs() == 0.0;
		check("determinant() (Gauss) == signed sum over permutations, n=1..6; equal rows -> 0", detOk);

		// 13. Lambda_1(U) == U and homomorphism Lambda_n(UV) == Lambda_n(U) Lambda_n(V), 4 modes
		//     (DFT 4x4 dressed with phases), n=2..3.
		MatrixComplex f4 = dft(4);
		MatrixComplex d4 = new MatrixComplex(4, 4);
		double[] ph = { 0.3, 1.1, -2.0, 0.7 };
		for (int i = 0; i < 4; ++i) d4.setItem(i, i, new Complex(Math.cos(ph[i]), Math.sin(ph[i])));
		MatrixComplex u4 = f4.times(d4), v4 = d4.times(f4).times(d4);
		boolean fHomOk = Fock.multiFermionUnitary(f4, 1).equals(f4, 10);
		for (int n = 2; n <= 3; ++n) {
			MatrixComplex lhs = Fock.multiFermionUnitary(u4.times(v4), n);
			MatrixComplex rhs = Fock.multiFermionUnitary(u4, n).times(Fock.multiFermionUnitary(v4, n));
			if (lhs.minus(rhs).norm() > 1e-9) { fHomOk = false; }
		}
		check("Lambda_1(U) == U; Lambda_n(UV) == Lambda_n(U)*Lambda_n(V), n=2..3, 4 modes", fHomOk);

		// 14. Fermionic HOM: Lambda_2(H)|1,1> == det(H)|1,1> == -|1,1> (only one basis state).
		MatrixComplex fOut = Fock.multiFermionUnitary(Qubits.hadamard(), 2).times(Fock.fermionKet(1, 1));
		check("fermionic HOM: Lambda_2(H)|1,1> == -|1,1>", fOut.rows() == 1
				&& Math.abs(fOut.getItem(0, 0).rep() + 1.0) < 1e-12 && Math.abs(fOut.getItem(0, 0).imp()) < 1e-12);

		// 15. Sign convention (anticommutation): swapping the order of 2 creation operators flips the
		//     sign. Built by hand: a_1^+ a_0^+ |0> = -|1,1>, so applying U = SWAP (a_0 <-> a_1) to
		//     |1,1> = a_0^+ a_1^+|0> gives a_1^+ a_0^+|0> = -|1,1>, i.e. det(SWAP) = -1; for bosons
		//     Phi_2(SWAP)|1,1> = +|1,1>.
		MatrixComplex swap = Qubits.pauliX();
		MatrixComplex fSwap = Fock.multiFermionUnitary(swap, 2).times(Fock.fermionKet(1, 1));
		MatrixComplex bSwap = Fock.multiPhotonUnitary(swap, 2).times(Fock.ket(1, 1));
		check("exchange sign: fermions SWAP|1,1> == -|1,1>, bosons SWAP|1,1> == +|1,1>",
				Math.abs(fSwap.getItem(0, 0).rep() + 1.0) < 1e-12
				&& Math.abs(bSwap.getItem(Fock.indexOf(1, 1), 0).rep() - 1.0) < 1e-12);

		System.out.println("\nScratchQuantum_FockAudit01: " + ok + " OK, " + fail + " FAIL");
		if (fail > 0) System.exit(1);
	}

	/** Literal definition: sum over permutations of prod_i a[i][perm(i)], by recursion on rows. */
	private static Complex brutePermanent(MatrixComplex a, int row, boolean[] used) {
		int n = a.rows();
		if (row == n) return new Complex(1.0, 0.0);
		Complex total = new Complex(0.0, 0.0);
		for (int col = 0; col < n; ++col) {
			if (used[col]) continue;
			used[col] = true;
			total = total.plus(a.getItem(row, col).times(brutePermanent(a, row + 1, used)));
			used[col] = false;
		}
		return total;
	}

	/** Literal definition: sum over permutations of sign(perm) prod_i a[i][perm(i)]. */
	private static Complex bruteDeterminant(MatrixComplex a, int row, boolean[] used, int[] perm) {
		int n = a.rows();
		if (row == n) {
			int inversions = 0;
			for (int i = 0; i < n; ++i) for (int j = i + 1; j < n; ++j) if (perm[i] > perm[j]) inversions++;
			Complex prod = new Complex(inversions % 2 == 0 ? 1.0 : -1.0, 0.0);
			for (int i = 0; i < n; ++i) prod = prod.times(a.getItem(i, perm[i]));
			return prod;
		}
		Complex total = new Complex(0.0, 0.0);
		for (int col = 0; col < n; ++col) {
			if (used[col]) continue;
			used[col] = true;
			perm[row] = col;
			total = total.plus(bruteDeterminant(a, row + 1, used, perm));
			used[col] = false;
		}
		return total;
	}

	/** Unitary DFT matrix of size n, F[j][k] = e^(2*pi*i*j*k/n)/sqrt(n). */
	private static MatrixComplex dft(int n) {
		MatrixComplex f = new MatrixComplex(n, n);
		for (int j = 0; j < n; ++j) for (int k = 0; k < n; ++k) {
			double ang = 2.0 * Math.PI * j * k / n;
			f.setItem(j, k, new Complex(Math.cos(ang) / Math.sqrt(n), Math.sin(ang) / Math.sqrt(n)));
		}
		return f;
	}

	static void check(String label, boolean condition) {
		if (condition) { ok++; System.out.println("[OK]   " + label); }
		else { fail++; System.out.println("[FAIL] " + label); }
	}
}
