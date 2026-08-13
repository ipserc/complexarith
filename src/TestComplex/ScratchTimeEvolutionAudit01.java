package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;
import com.ipserc.arith.quantum.TimeEvolution;

/**
 * Audit of TimeEvolution.java (unitary(H,t)=exp(-i*H*t), evolve(), expectationValue()) -- candidate
 * 1 of the Rol Fisica/Mecanica Cuantica roadmap catalogued at the close of the Trigesimosexta
 * sesion. Checks that don't rely on trusting a specific Bloch-sign convention by hand (learned the
 * hard way with the CHSH angle convention in BellTest): unitarity, U(0)=I, semigroup property
 * U(t1)*U(t2)=U(t1+t2), exact stationarity of energy eigenstates, and a finite-difference check of
 * the Schrodinger equation i*d|psi>/dt = H|psi> itself.
 */
public class ScratchTimeEvolutionAudit01 {

	static int ok = 0, fail = 0;

	public static void main(String[] args) {
		MatrixComplex Z = Qubits.pauliZ();
		MatrixComplex X = Qubits.pauliX();
		MatrixComplex I2 = Qubits.identity2();

		// 1. U(0) = I for several Hamiltonians
		check("U(0)=I (Z)", TimeEvolution.unitary(Z, 0.0).minus(I2).norm() < 1e-12);
		check("U(0)=I (X)", TimeEvolution.unitary(X, 0.0).minus(I2).norm() < 1e-12);
		MatrixComplex H0 = Z.times(1.3).plus(X.times(0.7));
		check("U(0)=I (Z*1.3+X*0.7)", TimeEvolution.unitary(H0, 0.0).minus(I2).norm() < 1e-12);

		// 2. Unitarity: U(t)^dagger * U(t) = I, for several H and t
		double[] times = { 0.0, 0.3, 1.0, 2.7, -1.5, 10.0 };
		MatrixComplex[] hams = { Z, X, Qubits.pauliY(), H0, Z.times(0.0) };
		boolean allUnitary = true;
		for (MatrixComplex H : hams) {
			for (double t : times) {
				MatrixComplex U = TimeEvolution.unitary(H, t);
				MatrixComplex shouldBeI = U.adjoint().times(U);
				if (shouldBeI.minus(I2).norm() > 1e-9) { allUnitary = false; }
			}
		}
		check("U(t)^dagger U(t)=I for 5 H x 6 t", allUnitary);

		// 3. Semigroup: U(t1)*U(t2) == U(t1+t2) (same H always commutes with itself)
		boolean semigroupOk = true;
		for (MatrixComplex H : hams) {
			double t1 = 0.4, t2 = 1.1;
			MatrixComplex lhs = TimeEvolution.unitary(H, t1).times(TimeEvolution.unitary(H, t2));
			MatrixComplex rhs = TimeEvolution.unitary(H, t1 + t2);
			if (lhs.minus(rhs).norm() > 1e-9) { semigroupOk = false; }
		}
		check("U(t1)U(t2)=U(t1+t2) for 5 Hamiltonians", semigroupOk);

		// 4. Energy eigenstate stationarity: H=pauliZ, eigenkets |0>,|1> with eigenvalues +1,-1 exactly
		//    exp(-i*H*t)|0> should equal exp(-i*t)|0> (a pure global phase), elementwise.
		MatrixComplex ket0 = Qubits.ket0();
		MatrixComplex ket1 = Qubits.ket1();
		boolean eigenstateOk = true;
		for (double t : new double[] { 0.3, 1.0, 2.7, 5.0 }) {
			MatrixComplex evolved0 = TimeEvolution.evolve(ket0, Z, t);
			MatrixComplex expected0 = ket0.times(new Complex(Math.cos(-t), Math.sin(-t)));
			if (evolved0.minus(expected0).norm() > 1e-9) { eigenstateOk = false; }

			MatrixComplex evolved1 = TimeEvolution.evolve(ket1, Z, t);
			MatrixComplex expected1 = ket1.times(new Complex(Math.cos(t), Math.sin(t)));
			if (evolved1.minus(expected1).norm() > 1e-9) { eigenstateOk = false; }
		}
		check("energy eigenstates |0>/|1> under H=Z pick up exact e^(-i*E*t) phase", eigenstateOk);

		// 5. Stationary expectation values: for an eigenstate, <op> doesn't change with t
		double e0z = TimeEvolution.expectationValue(TimeEvolution.evolve(ket0, Z, 3.0), Z);
		check("<Z> stationary at +1 for |0> under H=Z", Math.abs(e0z - 1.0) < 1e-9);
		double e0x = TimeEvolution.expectationValue(TimeEvolution.evolve(ket0, Z, 3.0), X);
		check("<X> stationary at 0 for |0> under H=Z", Math.abs(e0x - 0.0) < 1e-9);

		// 6. Schrodinger equation itself, finite-difference: i*(psi(t+eps)-psi(t))/eps ~= H*psi(t)
		boolean schrodingerOk = true;
		MatrixComplex plusX = ket0.plus(ket1).normalizeByCols(); // a non-eigenstate, to exercise real dynamics
		for (MatrixComplex H : new MatrixComplex[] { Z, H0 }) {
			double t = 0.7, eps = 1e-6;
			MatrixComplex psiT = TimeEvolution.evolve(plusX, H, t);
			MatrixComplex psiTplus = TimeEvolution.evolve(plusX, H, t + eps);
			MatrixComplex finiteDiff = psiTplus.minus(psiT).times(new Complex(0.0, 1.0 / eps));
			MatrixComplex rhs = H.times(psiT);
			if (finiteDiff.minus(rhs).norm() > 1e-4) { schrodingerOk = false; }
		}
		check("finite-difference Schrodinger equation i*dpsi/dt=H*psi holds", schrodingerOk);

		// 7. Non-Hermitian Hamiltonian must fail loud
		MatrixComplex nonHermitian = new MatrixComplex(2, 2);
		nonHermitian.setItem(0, 0, 1.0); nonHermitian.setItem(0, 1, new Complex(0.0, 1.0));
		nonHermitian.setItem(1, 0, 1.0); nonHermitian.setItem(1, 1, 1.0);
		try {
			TimeEvolution.unitary(nonHermitian, 1.0);
			check("non-Hermitian Hamiltonian rejected", false);
		} catch (IllegalArgumentException e) {
			check("non-Hermitian Hamiltonian rejected", true);
		}

		// 8. BellTest.correlation still consistent after the refactor to reuse TimeEvolution.expectationValue
		MatrixComplex bell = Qubits.bellPhiPlus();
		double corrZZ = com.ipserc.arith.quantum.BellTest.correlation(bell, Z, Z);
		check("BellTest.correlation(bell,Z,Z)==1 after refactor to reuse TimeEvolution.expectationValue",
				Math.abs(corrZZ - 1.0) < 1e-9);

		System.out.println();
		System.out.println(ok + "/" + (ok + fail) + " OK");
		if (fail > 0) { System.exit(1); }
	}

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
}
