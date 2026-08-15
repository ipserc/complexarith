/**
 * 
 */
package TestComplex;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.quantum.Qubits;

/**
 * 
 */
public class TestQuantum_QFT01 {
	
	static int ok = 0, fail = 0;

	static void check(String label, boolean condition) {
		System.out.println((condition ? "OK   " : "FAIL ") + label);
		if (condition) { ok++; } else { fail++; }
	}
	
	/*
	 * QFT para fines didácticos 
	 * QFTmatrix construye la matriz entrada a entrada, con un coste O(dim²) y 
	 * sin pasar por ninguna puerta cuántica — no es "un circuito", es la tabla de valores de la transformación.
	 */
	static MatrixComplex QFTmatrix(int N) {
		int dim = 1 << N;

		double s = 1.0 / Math.sqrt(dim);

		MatrixComplex qftMatrix = new MatrixComplex(dim, dim);

		for (int j = 0; j < dim; j++) {
		    for (int k = 0; k < dim; k++) {
		        double angle = 2.0 * Math.PI * j * k / dim;

		        qftMatrix.setItem(
		            k,
		            j,
		            new Complex(s * Math.cos(angle),
		                       s * Math.sin(angle))
		        );
		    }
		}
		return qftMatrix;
	}
	
	/*
	 * QFT práctica
	 * Se calcula componiendo puertas elementales (H, fase controlada, SWAP), que es como se implementaría de verdad 
	 * en un ordenador cuántico real (con O(n²) puertas
	 */
	static MatrixComplex QFTcircuit(int N) {
	    int dim = 1 << N;
	    MatrixComplex result = MatrixComplex.eye(dim);

	    for (int j = 0; j < N; j++) {
	    	// Cálculo del perador Hadamard local al qubit j, ya "levantado" (operatorOnQubit) a una matriz 2Nx2N que actúa sobre el qubit j y deja el resto intactos (H (x) I)
	    	// Se acumula al resultado anterior operador Hadamard(j).times(result)
	        result = Qubits.operatorOnQubit(Qubits.hadamard(), j, N).times(result);
	        for (int k = j + 1; k < N; k++) {
	        	// aplica phaseGate(k - j + 1) (R_(k - j + 1) = diag(1,i)) al qubit k solo si el qubit j vale |1>.
	            MatrixComplex cphase = Qubits.controlledGate(Qubits.phaseGate(k - j + 1), k, j, N);
	            // acumula cphase a result
	            result = cphase.times(result);
	        }
	    }

	    for (int i = 0; i < N / 2; i++) {
	        int a = i, b = N - 1 - i;
	        /*
	         *   El SWAP no es "un arreglo cutre" — es una parte reconocida del algoritmo estándar (aparece en Nielsen & Chuang exactamente así): 
	         *   sin él, obtendrías una matriz unitaria válida, pero que calcula la QFT con los bits de salida invertidos, no la QFT de verdad. 
	         *   Por eso en la Etapa 2 (n=1) no hacía falta: con un solo bit no hay "orden" que invertir.
	         *   ¿Por qué pauliX para el SWAP y no de otra forma?
	         *   Porque pauliX() es la puerta NOT cuántica (X|0>=|1>, X|1>=|0>), y SWAP se construye como una identidad puramente algebraica de 3 CNOT encadenados con control/target alternados:
	         *   SWAP = CNOT(control=a,target=b) · CNOT(control=b,target=a) · CNOT(control=a,target=b)
	         *   Esto se hace para cada elemento sobre la diagonal 
	         */
	        MatrixComplex swap = Qubits.controlledGate(Qubits.pauliX(), a, b, N)
	                .times(Qubits.controlledGate(Qubits.pauliX(), b, a, N))
	                .times(Qubits.controlledGate(Qubits.pauliX(), a, b, N));
	        result = swap.times(result);
	    }

	    return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
     	Complex.setFormatON();
     	Complex.setFixedON(3);
     	int boxMargin = 65;
     	int boxShape = 3;
     	MatrixComplex phaseGate;
     	
     	/*
     	 * Parámetros el experimento
     	 */
     	int N; // número total de qubits del registro sobre el que trabaja el circuito completo

		/*
		 *  Etapa 1 — phaseGate R_k for different k
		 */
     	Complex.printBoxText(boxShape, boxMargin, "Etapa 1 — phaseGate R_k for different k");
		for (int k = 0; k < 5; k++) {
			phaseGate = Qubits.phaseGate(k);
			phaseGate.println("phaseGate - puerta R_" + k);
		}

		/*
		 * Etapa 2 — Construir la matriz " + N + "x" + N + " de la fórmula literal de la QFT para N="+N+", y compararla contra hadamard():
		 */
		N = 1;
		Complex.printBoxText(boxShape, boxMargin, "Etapa 2 — Construir la matriz 2x2 de la fórmula literal de la QFT para N, y compararla contra hadamard()");
		MatrixComplex qft1 = QFTmatrix(N);
		qft1.println("QFT de " + N + " qubit (fórmula literal)");
		
		check("QFT(" + N + " qubit) == hadamard()", qft1.equals(Qubits.hadamard(), 10));
		
		/*
		 * Etapa 3 — QFT de 2 qubits a mano (H + fase controlada + SWAP montado con 3 controlledGate(pauliX(),...)).
		 * Este bloque solo vale para N = 2
		 */
		N = 2;
		Complex.printBoxText(boxShape, boxMargin, "Etapa 3a — QFT de " + N + " qubits a mano (H + fase controlada + SWAP montado con 3 controlledGate(pauliX(),...))");
		// el operador Hadamard local al qubit 0, ya "levantado" (operatorOnQubit) a una matriz 4x4 que actúa sobre el qubit 0 y deja el qubit 1 intacto (H (x) I)
		MatrixComplex h0 = Qubits.operatorOnQubit(Qubits.hadamard(), 0, N);
		h0.println("--- Operador Hadamard local al qubit 0 ---");
		// lo mismo pero al revés — Hadamard sobre el qubit 1, dejando el qubit 0 intacto (I (x) H).
		MatrixComplex h1 = Qubits.operatorOnQubit(Qubits.hadamard(), 1, N);
		h1.println("--- Operador Hadamard local al qubit 1 ---");
		
		/*
		 * la puerta de fase controlada del paso 2 del algoritmo — aplica phaseGate(N) (R_N = diag(1,i)) al qubit 0 solo si el qubit 1 vale |1>. 
		 * Es la pieza que introduce el acoplamiento entre los 2 qubits que comentamos antes (la fase de uno depende del valor del otro) — sin ella, 
		 * qft2 sería simplemente H⊗H, que es unitaria pero NO es la QFT (te perderías toda la parte de "mezcla de bits" que distingue 
		 * la QFT de una superposición ingenua)
		 */
		MatrixComplex cphase = Qubits.controlledGate(Qubits.phaseGate(N), 1, 0, N);

		MatrixComplex swap = Qubits.controlledGate(Qubits.pauliX(), 0, 1, N)
		        .times(Qubits.controlledGate(Qubits.pauliX(), 1, 0, N))
		        .times(Qubits.controlledGate(Qubits.pauliX(), 0, 1, N));

		MatrixComplex qftN = swap.times(h1).times(cphase).times(h0);
		qftN.println("QFT de " + N + " qubits (circuito)");

		// Verificación contra la fórmula literal, igual que en la Etapa 2:
		Complex.printBoxText(boxShape, boxMargin, "Etapa 3b — Verificación contra la fórmula literal, igual que en la Etapa 2 para N = " + N);
		MatrixComplex qftN_Matrix = QFTmatrix(N);
		qftN_Matrix.println("Matriz QFT(" + N + " qubits)");
		check("QFT(" + N + " qubits) circuito qftN == qftN_Matrix fórmula literal", qftN.equals(qftN_Matrix, 10));

		/*
		 * Etapa 4 — QFT de 1, 2, 3 y 4 qubits mediante QFTcircuit (H + fase controlada + SWAP montado con 3 controlledGate(pauliX(),...)).
		 */
		Complex.printBoxText(boxShape, boxMargin, "Etapa 4 — QFT de 1, 2, 3 y 4 qubits mediante QFTcircuit (H + fase controlada + SWAP montado con 3 controlledGate(pauliX(),...))");

		for (int i = 1; i < 6; ++i) {
			Complex.printBoxText(boxShape, boxMargin, "Etapa 4." + i + " — QFT de " + i + " qubits");
			MatrixComplex QFTcircuit = QFTcircuit(i);
			MatrixComplex qftMatrix = QFTmatrix(i);
			
			QFTcircuit.println("--- QFTcircuit ---");
			qftMatrix.println("--- qftMatrix ---");
			check("QFT(" + i + " qubits) circuito QFTcircuit == qftMatrix fórmula literal", QFTcircuit.equals(qftMatrix, 10));
		}
		Complex.printBoxText(boxShape, boxMargin, ok + " Test OK of " + (ok + fail) + " Tests. " + fail + " Test failed.");
	}

}
