/**
 *  Complex es una clase que permite definir y operar con números complejos.
 *  <p>
 *  Incorpora funciones aritméticas como la suma, la resta, el producto y la división por complejos.
 *  Se ha definido el producto 
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *	
 *  Based on http://introcs.cs.princeton.edu/java/97data/Complex.java.html
 *  from http://introcs.cs.princeton.edu/java/32class/
 *  
 *  
 *  
 *  
 *
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *  
 *
 */
package com.ipserc.arith.complex;

import java.lang.Double;
import java.lang.Math;
import java.math.*;
import java.util.function.Function;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Complex class to work with complex numbers
 * @author ipserc
 *
 */
public class Complex {
	/*
	 * ***********************************************
	 * CONSTANTS
	 * ***********************************************
	 */
	private static String __NUMPAD__ = " "; // To align numbers. It works with scientific notation
	
	public static void numpPadNONE() {
		__NUMPAD__ = "";
	}

	public static void numpPadBLANK() {
		__NUMPAD__ = " ";
	}

	public static void numpPadPLUS() {
		__NUMPAD__ = "+";
	}

	/** Package-private accessor for {@link ComplexFormat}, which cannot see the private field. */
	static String numpad() {
		return __NUMPAD__;
	}
	
	private final static String HEADINFO = "Complex --- INFO: ";
	private final static String VERSION = "1.40 (2026_0811_0300)";
	/* VERSION Release Note
	 * 1.40 (2026_0811_0300)
	 * Bug real preexistente arreglado (investigacion dedicada, a peticion del usuario): isPureReal()
	 * devolvia false para el propio cero exacto (rePartNull() siendo cierto lo excluia en vez de
	 * incluirlo), y de forma simetrica isPureImaginary() tambien excluia el cero. Efecto en cadena
	 * confirmado con pruebas antes/despues: isInteger() decia que 0 no es entero (encontrado la
	 * sesion anterior, disparo del bug en besselY(0,z)); gamma_zones(0)/gamma_integral(0)/
	 * gamma_weiertrass(0)/gamma_euler(0) daban -Infinity en vez de +Infinity (el signo correcto del
	 * polo, su guarda isIntegerNegativeZero() fallaba en z=0 exacto -- gamma(0), el default via
	 * gamma_fast/Lanczos, NO usa esa guarda y sigue dando -Infinity, bug preexistente DISTINTO, sin
	 * relacion con isPureReal, NO arreglado aqui); MatrixComplex.isHermitian()/isAntiHermitian() (y
	 * por extension isPostiveDefinite()/isPostiveSemiDefinite()/isNegtiveDefinite()/
	 * isNegtiveSemiDefinite(), que dependen de isHermitian()) clasificaban mal CUALQUIER matriz
	 * Hermitiana/anti-Hermitiana con un cero exacto en la diagonal -- caso nada raro. Arreglado
	 * quitando la guarda extra de cada metodo (isPureReal()=imPartNull() a secas,
	 * isPureImaginary()=rePartNull() a secas) -- cero pasa a ser, correctamente, tanto puramente
	 * real como puramente imaginario a la vez (la convencion matematica estandar). Verificado con
	 * bateria de ~37 ficheros (incluidos los 24 que tocan Hermitian/Definite) comparada contra un
	 * build de referencia pre-fix: sin regresiones, mas las mejoras esperadas confirmadas.
	 * <p>
	 * 2 hallazgos colaterales, reportados pero NO arreglados aqui (fuera de alcance, cada uno una
	 * investigacion propia aparte): (1) {@code gamma(0)} (default, via gamma_fast/Lanczos) sigue
	 * dando -Infinity -- no pasa por isPureReal/isIntegerNegativeZero en absoluto, bug distinto en
	 * el propio manejo del polo de Lanczos. (2) {@code MatrixComplex.power(double 0.0)}/
	 * {@code power_(Complex 0)} puede devolver la matriz CERO en vez de la identidad para matrices
	 * no diagonalizables -- confirmado que {@code power(0.0)} ya daba el resultado incorrecto ANTES
	 * de este fix, llamando directamente al overload double (bug independiente en power()/exp()/
	 * log(), no causado por isPureReal()). Ver Claude/ComplexArithRev.md para el detalle de ambos.
	 *
	 * 1.39 (2026_0811_0200)
	 * Nuevos instrumentos matematicos, Bloque E (funciones de Bessel, ultimo de la hoja de ruta) de
	 * la hoja de ruta anotada durante el Bloque 1 de la auditoria matematica dedicada (ver
	 * Claude/ComplexArithRev.md): besselJ(Complex nu, Complex z) y besselY(Complex nu, Complex z).
	 * J_nu(z) generaliza a orden Complex reutilizando gamma(); orden entero negativo usa la
	 * reflexion J_(-n)=(-1)^n*J_n en vez de la serie directa. Y_nu(z): orden no entero via la
	 * formula de reflexion directa (sin digamma); orden entero no negativo via la serie limite
	 * estandar (Abramowitz &amp; Stegun 9.1.11), que si necesita digamma (Bloque A) porque
	 * sin(n*pi)=0 deja la reflexion en 0/0. Orden entero negativo para Y NO implementado (alcance
	 * deliberadamente acotado segun el plan aprobado) -- falla alto. Verificado
	 * (ScratchBesselAudit01.java): J_0(0)=1, J_n(0)=0 (n&gt;0), recurrencia de 3 terminos
	 * J_(n-1)+J_(n+1)==(2n/z)*J_n, ecuacion diferencial de Bessel via derivada numerica doble,
	 * Wronskiano J_n*Y_n'-J_n'*Y_n==2/(pi*z), valores tabulados conocidos.
	 *
	 * 1.38 (2026_0811_0100)
	 * Nuevos instrumentos matematicos, Bloque D (polilogaritmo) de la hoja de ruta anotada durante
	 * el Bloque 1 de la auditoria matematica dedicada (ver Claude/ComplexArithRev.md):
	 * polylog(Complex s, Complex z) = Li_s(z) = Sum z^k/k^s, via la serie directa. Alcance
	 * deliberadamente acotado (misma decision de alcance que beta/gamma_integral con dominio
	 * documentado): solo |z|&lt;1, donde la serie converge; falla alto con IllegalArgumentException
	 * fuera de ahi, salvo el caso especial z=1 con Re(s)&gt;1 (devuelve zeta(s) directamente, la
	 * identidad Li_s(1)=zeta(s)). La continuacion analitica general (formula de inversion de
	 * Jonquiere, con Hurwitz-zeta y polinomios de Bernoulli) no esta implementada -- proyecto
	 * aparte, no pedido. Verificado (ScratchPolylogAudit01.java): Li_1(z)==-log(1-z), Li_2(1)==
	 * zeta(2)==pi^2/6, Li_s(z)~=z para |z| pequeno, excepcion lanzada de forma consistente para
	 * |z|&gt;=1 (excepto el caso especial).
	 *
	 * 1.37 (2026_0811_0000)
	 * Nuevos instrumentos matematicos, Bloque C (eta de Dirichlet) de la hoja de ruta anotada
	 * durante el Bloque 1 de la auditoria matematica dedicada (ver Claude/ComplexArithRev.md):
	 * eta(s) = Sum (-1)^(k-1)/k^s = (1-2^(1-s))*zeta(s), con s=1 resuelto al limite ln(2).
	 * Hallazgo de reutilizacion: zeta_havil(s) ya calculaba internamente, como paso previo a
	 * dividir por (1-2^(1-s)), exactamente la serie de Sondow/Euler-transformada que define eta --
	 * extraida a su propio metodo en vez de duplicar el bucle; zeta_havil ahora delega en eta() y
	 * solo anade esa division. Verificado (ScratchEtaAudit01.java): eta(1)=ln(2), eta(2)=pi^2/12,
	 * identidad eta(s)==(1-2^(1-s))*zeta(s), suma alternada directa como referencia independiente,
	 * y regresion de zeta_havil sobre los puntos ya cubiertos por
	 * ScratchComplexFunctionsAudit01/02.java tras la refactorizacion.
	 *
	 * 1.36 (2026_0810_2300)
	 * Nuevos instrumentos matematicos, Bloque B (funcion de error) de la hoja de ruta anotada
	 * durante el Bloque 1 de la auditoria matematica dedicada (ver Claude/ComplexArithRev.md):
	 * erf(z)/erfc(z). Serie de Maclaurin (2/sqrt(pi))*Sum (-1)^n*z^(2n+1)/(n!(2n+1)), funcion
	 * entera por lo que converge para cualquier z; decaimiento factorial una vez n&gt;|z|^2, asi
	 * que el criterio de parada "ultimo termino por debajo del umbral" (igual que zeta_havil) es
	 * valido sin necesitar la correccion de cola de zeta_re/gamma_weiertrass. Limite de dominio
	 * documentado, no resuelto: para |z| grande la serie sufre cancelacion catastrofica antes de
	 * converger (mismo tipo de limite ya aceptado en gamma_nemes). Verificado
	 * (ScratchErfAudit01.java): erf(0)=0, funcion impar, valores tabulados conocidos en el eje
	 * real, erf'(z)==(2/sqrt(pi))*exp(-z^2) via derivada numerica, cruzado contra integracion
	 * numerica directa de exp(-t^2).
	 *
	 * 1.35 (2026_0810_2200)
	 * Nuevos instrumentos matematicos, Bloque A (digamma/poligamma) de la hoja de ruta anotada
	 * durante el Bloque 1 de la auditoria matematica dedicada (ver Claude/ComplexArithRev.md):
	 * digamma psi(z) y poligamma psi^(n)(z), companeros directos de gamma. Un unico motor
	 * polygamma(int n, Complex z) (n=0 es digamma), via la recurrencia estandar
	 * psi^(n)(z)=psi^(n)(z+m)-(-1)^n*n!*Sum 1/(z+k)^(n+1) para desplazar z a la zona segura
	 * Re(z)&gt;=10, mas la serie asintotica de Stirling alli -- reutiliza los mismos numeros de
	 * Bernoulli B_2..B_10 ya usados por zeta_re para su cola de Euler-Maclaurin. Verificado
	 * (ScratchDigammaAudit01.java): psi(1)=-EULER_MASC, psi(2)=1-EULER_MASC,
	 * psi(1/2)=-EULER_MASC-2*ln2, recurrencia psi(z+1)-psi(z)==1/z, reflexion
	 * psi(1-z)-psi(z)==pi*cot(pi*z), psi'(1)==zeta(2)==pi^2/6, cruzado contra la derivada numerica
	 * de log(gamma(z)) via ComplexCalculus.derivative.
	 *
	 * 1.34 (2026_0810_2100)
	 * Auditoria matematica dedicada de ComplexFunctions.java (Vigesimosexta sesion, ver
	 * Claude/ComplexArithRev.md) -- 4 bugs reales encontrados y arreglados, cada uno verificado
	 * numericamente antes y despues:
	 * - factorial(Complex n): devolvia gamma(n)=(n-1)! en vez de gamma(n+1)=n! (mismo patron
	 *   off-by-one ya arreglado en binomialCoef(Complex,Complex) en la Sexta sesion). Sin
	 *   llamadores en el proyecto -- bug real pero latente.
	 * - arccos(Complex z): la formula log() independiente violaba la identidad
	 *   asin(z)+acos(z)==pi/2 en 12 de 19 puntos barridos (error hasta 6.3). Reescrito para
	 *   derivar de pi/2-arcsin(z), el mismo principio que arccosExtreme() ya usaba correctamente
	 *   para |z| extremo.
	 * - gamma_integral(Complex z): resultados muy erroneos para 0&lt;Re(z)&lt;1 (error hasta 4
	 *   ordenes de magnitud en Re(z)=0.1) por la singularidad integrable de t^(z-1) en t=0, sin
	 *   manejar por la cuadratura de Simpson de ComplexCalculus.integrate(). Arreglado con la
	 *   recurrencia estandar Gamma(z)=Gamma(z+1)/z para desplazar z a la zona segura Re(z)&gt;=1.
	 * - gamma_weiertrass()/gamma_euler(): el criterio de parada ("ultimo delta pequeno") no
	 *   escalaba con los decimales pedidos -- mismo defecto de cola O(1/k^2) ya diagnosticado y
	 *   arreglado en zeta_re (Sexta sesion), nunca aplicado aqui. Arreglado dividiendo el umbral
	 *   por el indice de iteracion (autocalibrado, sin derivar la constante de cola exacta).
	 *
	 * 1.33 (2026_0809_1906)
	 * plusEqRaw(Complex)/syncPolar(): nuevos primitivos in-place para el candidato "Camino A"
	 * de rendimiento (Vector API, ver Claude/ComplexArithRev.md). El cuello de botella real de
	 * los bucles Taylor/Mercator de MatrixComplexFunctions.java (medido en la Decimoctava sesion,
	 * Fase 5) no era la alocacion sino que plusEq(Complex) recalcula mod/pha/cre (Math.hypot +
	 * Math.atan2 + normalizacion) en CADA paso de una suma en cadena, aunque esos valores
	 * intermedios nunca se leen -- se sobrescriben en el siguiente paso antes de que nadie los
	 * use. plusEqRaw() muta solo rep/imp (cero trigonometria); syncPolar() expone
	 * setPolCoord() para cerrar la cadena una sola vez. Provisto matematicamente sin perdida
	 * (plusEq(Complex) nunca lee mod/pha/cre de 'that', solo rep/imp -- saltarse los
	 * setPolCoord() intermedios no cambia nada observable una vez se llama syncPolar()).
	 * Metodos existentes (plusEq/minusEq/timesEq) SIN TOCAR. Ver MatrixComplex.VERSION para el
	 * primer consumidor (timesEqRaw()).
	 *
	 * 1.9 (2023_0514_2000)
	 * public static void printBoxTitle(int boxId, int size, String title) {
	 * public static void printBoxText(int boxId, int size, String title) {
	 * public static Complex ChebyshevZero(int n, int k)
	 * private void setRecCoord() { . . . if (this.imPartNull()) this.imp = 0.0;
	 * public static Complex zeta(Complex s) {
	 * public static Complex zeta_re(Complex s) {
	 * public static Complex zeta_ext(Complex s) {
	 * public static Complex zeta_reflex(Complex s) {
	 * public static Complex zeta_primes(Complex s) {
	 * public static Complex zeta_riemann_siegel(Complex s) {
	 * public static Complex zeta_analytic_continuation(Complex s) {
	 * public static Complex zeta_havil(Complex s) {
	 * public static Complex binomialCoef(int n, int k) {
	 * public static Complex binomialCoef(Complex n, Complex k) {
	 * public static double factorial(int n) {
	 * public static Complex factorial(Complex n) {
	 * private static String __NUMPAD__ = " "; // To align numbers. It works with scientific notation
	 * public static void numpPadNONE()
	 * public static void numpPadBLANK()
	 * public static void numpPadPLUS()
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 1.8 (2022_0928_0000)
	 * public boolean isInteger()
	 * public boolean isIntegerPositive()
	 * public boolean isIntegerNegative()
	 * public boolean isIntegerPositiveZero()
	 * public boolean isIntegerNegativeZero()
	 * public static Complex gamma(Complex z)
	 * public static Complex gamma_integral(double n)
	 * public static Complex gamma_integral(Complex z)
	 * public static Complex gamma_integral2(Complex z)
	 * public static Complex gamma_weiertrass(Complex z)
	 * public static Complex gamma_euler(Complex z)
	 * public static Complex gamma_nemes(Complex z)
	 * public static Complex beta(Complex p, Complex q)
	 * REPRESENTATION enumeration to select the default representation of the complex number between rectangular or polar coordinates
	 * public static void setRepres(Representation Repres)
	 * public static String getRepres()
	 * public static void restoreRepres()
	 * public String toString()
	 * public void printRec()
	 * public void printPol()
	 * public void printlnRec()
	 * public void printlnPol()
	 * 
	 * 1.7 (2022_0911_1130)
	 * New management of EXACT/APPROXIMATED settings. Now ZERO_THRESHOLD holds the current value of the threshold for EXACT/APPROXIMATED
	 * All the methods regarding ZERO_THRESHOLD_XXXX have been modified to include this optimization
	 * public static boolean exact() {
	 * public static void exact(boolean value) {
	 * public static double zero_treshold_exact() {
	 * public static void zero_threshold_exact(double value) {
	 * public static double zero_threshold_approx() {
	 * public static void zero_threshold_approx(double value) {
	 * and all the methods which are using the above ones
	 * 
	 * 1.6 (2022_0202_2100)
	 * To use approximated equality. true use EXACT equality. false use APPROX equality.
	 * 
	 * public static boolean Exact = true; Eliminate Reduced local class member. Complex.Exact rules all the clasess
	 * 
	 * Now SIGNIFICATIVE is 8 decimals maximum. Machine significatives is 15, 7 decimals are for carry the rounding error
	 * 
	 * Correction factor for equality comparisons. I hate these kinds of things that seem to work but have no way to justify or prove
	 * public final static int CORRECTION_FACTOR = 100; 
	 * 
	 * All the flavors of equaslred disappear. Now equals decide the use of ZERO_THRESHOLD  or ZERO_THRESHOLD_R in function of the value of Exact
	 * public boolean isZero()
	 * public boolean imPartNull() 
	 * public boolean rePartNull()
	 * public boolean equals(Complex cNum)
	 * public boolean equals(double n1, double n2) 
	 * public boolean equals(Complex cNum, int numDecs) 
	 * public Boolean isPureReal()
	 * public Boolean isPureImaginary()
	 * All the xxxxxxxxRed are DEPRECATED and will be removed in the next release
	 * public boolean isZeroRed__() 
	 * public boolean imPartNullRed__()
	 * public boolean rePartNullRed__()
	 * public boolean equalsred__(Complex cNum)
	 * public boolean equalsred__(double n1, double n2) 
	 * public boolean equalsred__(double n1, double n2, int numDecs)
	 * public boolean equalsred__(Complex cNum, int numDecs)
	 * 
	 * public static Complex sqrt(Complex z)		New Square Root Method
	 * public static Complex sqrt(Complex z, int k) New Square Root Method
	 * DERECATED
	 * public static Complex sqrroot__(Complex z)
	 * public static Complex sqrroot__(Complex z, int k)
	 * 
	 * public static void showPrecision() Renamed to camel style
	 * public static void restorePrecisionFactorySettings() Included from now on
	 * 
	 * public String toStringRecWolfram() replace("E", "*10^"); TO-DO with polar representation
	 * 
	 * 1.5 (2021_0929_2100)
	 * added trunc method to truncate a double value.
	 * added trunc method to truncate a Complex value.
	 * round method uses BigDecimal.setScale(decs, RoundingMode.HALF_UP); for rounding the double
	 * round(Complex complex, int decs) uses the round method with the modulus of the complex leaving the phase unaltered
	 * 
	 */

	// public final static double PI = Math.PI; 			// 3.1415926535897932384626433832795;
	public final static double TWO_PI = 2 * Math.PI;	// 2 * 3.1415926535897932384626433832795;
	public final static double DOS_PI = TWO_PI;			// 2 * 3.1415926535897932384626433832795;
	public final static double HALF_PI =  Math.PI / 2; 	// 3.1415926535897932384626433832795 / 2;
	public final static double EULER_MASC = 0.5772156649015328606065120900824024310421; // Constant of Euler-Mascheroni
	public final static double LIM_INF = 2147483647; //2147483647

	public static enum Representation {RECTANGULAR, POLAR};

	/*
	 * ***********************************************
	 * MEMBER VARS
	 * ***********************************************
	 */

	// Per-thread configuration (EXACT, PRECISION, zero thresholds, number formatting flags,
	// default representation) lives in ComplexState (extracted Sexta sesion, paso 2, Fase 2.2).
	// The few call sites still in Complex.java (setCre(), the toString*/formatNbr formatting
	// methods, the equals/isZero family, and a couple of FUNCTIONS-section call sites) now go
	// through ComplexState's package-private static accessors instead of a local state().

	public final static Complex i = new Complex(0,1);
	public final static Complex j = i; // For Electric Engineering
	public final static Complex ZERO = new Complex(0,0);
	public final static Complex ONE = new Complex(1,0);
	public final static Complex mONE = new Complex(-1,0);
	public final static Complex PI = new Complex(Math.PI,0);
	public final static Complex DOSPI = new Complex(DOS_PI,0);
	public final static Complex TWOPI = DOSPI;
	public final static Complex HALFPI = new Complex(HALF_PI,0);

	// FIXED - Correction factor for equality comparisons. I hate these kind of things that seem to work but have no way to justify or prove
	public final static int CORRECTION_FACTOR = 10;
	// FIXED - The same feeling as Einstein before the cosmological constant
	// FIXED: PRECISION = 1E-13

	private double rep;	// the real part
	private double imp;	// the imaginary part
	private double mod;	// the modulus
	private double pha;	// the phase
	private double cre; // sgn*modulus sgn=any func. Used to compare Complex

	/*
	 * ***********************************************
	 * VERSION
	 * ***********************************************
	 */
	
	/*
	 * Prints the version of the class
	 */
	public static void version() {
		System.out.println(HEADINFO + "VERSION:" + VERSION); 
	}

	public static void facts() {
		System.out.println(HEADINFO + "VERSION..............:" + VERSION); 
		showPrecision();
		System.out.println(HEADINFO + "REPRESENTATION.......:" + getRepres()); 
	}

	/*
	 * ***********************************************
	 * CONSTRUCTORS 
	 * ***********************************************
	 */

	/**
	 * Creates a new complex object initialized to 0+0i
	 */
	public Complex() {
		rep = 0.0;
		imp = 0.0;
		mod = 0.0;
		pha = 0.0;
		cre = 0.0;
	}

	/**
	 * Creates a new complex object from its string representation.
	 * @param numC String representing a complex in Rectangular "a+bi" or Polar "a|b" coordinates. a,b are doubles.
	 */
	public Complex(String numC) {
		this.setComplex(numC);
	}

	/**
	 * Creates a new complex object with the given real and imaginary or modulus and phase parts.
	 * @param coord 'C' or 'c' for Rectangular, 'P' or 'p' for Polar.
	 * @param n1 coordinate #1
	 * @param n2 coordinate #2
	 */
	public Complex(char coord, double n1, double n2) {
		this.setComplex(coord, n1, n2);
	}

	/**
	 * Creates a new complex object with the given real and imaginary in Rectangular coordinates parts by default.
	 * @param n1 Real Part.
	 * @param n2 Imaginary Part.
	 */
	public Complex(double n1, double n2) {
		this.setComplex('C', n1, n2);
	}

	/**
	 * Creates a new complex object with the given real part in Rectangular coordinates by default.
	 * @param n1 Real Part.
	 */
	public Complex(double n1) {
		this.setComplex('C', n1, 0);
	}

	/*
	 * ***********************************************
	 * INITIALIZERS & SETTERS
	 * ***********************************************
	 */

	/**
	 * Sets the complex object from its string representation.
	 * @param numC String representing a complex in Rectangular "a+bi" or Polar "a|b" coordinates. a,b are doubles.
	 * @return The Complex Object.
	 */
	public Complex setComplex(String numC) {
		rep = 0.0;
		imp = 0.0;
		mod = 0.0;
		pha = 0.0;
		cre = 0.0;

		// Regex pattern-matching/interpretation delegated to ComplexParser (extracted Sexta
		// sesion, paso 2, Fase 2.3); the actual field mutation + setRecCoord()/setPolCoord() call
		// stays here since those mutate 'this' directly.
		ComplexParser.Parsed parsed = ComplexParser.parse(numC);
		if (parsed.polar) {
			mod = parsed.a;
			pha = parsed.b;
			this.normalizePhase();
			this.setRecCoord();
		} else {
			rep = parsed.a;
			imp = parsed.b;
			this.setPolCoord();
		}
		return this;
	}

	/**
	 * Private Method. Sets the value of the complex for making Compare REal .
	 * Used to update the Complex Object.
	 */
	private void setCre() {
		//double sgn = this.rep == 0.0 ? Math.signum(this.imp): Math.signum(Math.cos(pha));
		//double sgn = (Math.abs(this.rep) <= ZERO_THRESHOLD_R) ? Math.signum(this.imp): Math.signum(Math.cos(pha));
		if ((Math.abs(this.imp) <= ComplexState.zero_threshold_approx())) this.cre = this.rep;
		else {
			//double sgn = Math.signum(this.rep);
			double sgn = Math.signum(this.rep*this.imp);
			this.cre = sgn * this.mod;
		}
	}

	/**
	 * Private Method. Sets the Polar representation of a Complex object from its Rectangular values.
	 * Used to update the Complex Object.
	 */
	private void setPolCoord() {
		this.mod = Math.hypot(this.rep, this.imp); //Math.sqrt(this.rep*this.rep + this.imp*this.imp );
		this.pha = Math.atan2(this.imp, this.rep);
		// Mirrors setRecCoord()'s rep/imp purification in the other direction: snap the phase to
		// the nearest axis-aligned value (0, +-HALF_PI, PI) when the other rectangular component
		// is negligible by the same rePartNull()/imPartNull() predicates, instead of carrying a
		// meaningless residual angle (e.g. atan2(1e-8,1000)~=1e-11) that would otherwise propagate
		// through phase-additive ops like times/timesEq/divides.
		if (this.imPartNull()) this.pha = (this.rep >= 0.0) ? 0.0 : Math.PI;
		else if (this.rePartNull()) this.pha = (this.imp >= 0.0) ? HALF_PI : -HALF_PI;
		this.normalizePhase();
		this.setCre();
	}

	/**
	 * Private Method. Sets the Rectangular representation of a Complex object from its Polar values.
	 * Used to update the Complex Object.
	 */
	private void setRecCoord() {
		this.rep = this.mod * Math.cos(this.pha);
		if (this.rePartNull()) this.rep = 0.0;
		this.imp = this.mod * Math.sin(this.pha);
		if (this.imPartNull()) this.imp = 0.0;
		this.setCre();
	}

	/**
	 * Private factory. Builds a Complex directly from already-known rectangular AND polar
	 * components, skipping the trigonometric recomputation that the 'C'/'P' constructors perform.
	 * Callers (times/divides) must guarantee rep,imp,mod,pha are mutually consistent and that
	 * phase is normalized before calling this.
	 * @param rep The real part.
	 * @param imp The imaginary part.
	 * @param mod The modulus (must equal hypot(rep,imp)).
	 * @param normalizedPha The phase, already normalized to (-pi, pi].
	 * @return The new Complex Object.
	 */
	private static Complex raw(double rep, double imp, double mod, double normalizedPha) {
		Complex z = new Complex();
		z.rep = rep;
		z.imp = imp;
		z.mod = mod;
		z.pha = normalizedPha;
		z.setCre();
		return z;
	}

	/**
	 * Private Method. Sets the Rectangular/Polar representation of a Complex object from its current values.
	 * Used to update the Complex Object .
	 * @param coordType 'C' or 'c' for Rectangular, 'P' or 'p' for Polar.
	 * @param n1 coordinate #1
	 * @param n2 coordinate #2
	 */
	private void setComplex(char coordType, double n1, double n2) {
		switch (coordType) {
		case 'C': // For Cartesian Coordinates
		case 'c':
			this.rep = n1;
			this.imp = n2;
			this.setPolCoord();
			break;
		case 'P': // For Polar Coordinates
		case 'p':
			this.mod = Math.abs(n1);
			this.pha = n2;
			this.normalizePhase();
			this.setRecCoord();
			break;
		default:
			throw new IllegalArgumentException("Invalid type of coordinates:" + coordType);
		}
	}

	/**
	 * Sets the Real and Imaginary parts of a Complex Object.
	 * @param cRep The Real Part.
	 * @param cImp The Imaginary Part.
	 */
	public void setComplexRec(double cRep, double cImp) {
		this.setComplex('C', cRep, cImp);
	}

	/**
	 * Sets the modulus and Phase of a Complex Object.
	 * @param cMod The Modulus.
	 * @param cPha The Phase in radians.
	 */
	public void setComplexPol(double cMod, double cPha) {
		this.setComplex('P', cMod, cPha);
	}

	/**
	 * Initializes the Complex Object with two random doubles from -base .. base.
	 * @param coordType  'C' or 'c' for Rectangular, 'P' or 'p' for Polar.
	 * @param base Base to generate the random complex.
	 */
	private void setComplexRandom(char coordType, int base) {
		Double n1, n2;
		int sign1, sign2;

		sign1 = Math.random() > 0.5 ? 1 : -1;
		sign2 = Math.random() < 0.5 ? 1 : -1;

		n1 = Math.random() * base * sign1;
		n2 = Math.random() * ((coordType == 'P' || coordType == 'p') ? Math.PI : 1) * base * sign2;
		this.setComplex(coordType, n1, n2);
	}

	private void setComplexRandomInt(char coordType, int base) {
		Double n1, n2;
		int sign1, sign2;

		sign1 = Math.random() > 0.5 ? 1 : -1;
		sign2 = Math.random() < 0.5 ? 1 : -1;

		n1 = Math.random() < 0.5 ? 0.0 : 1 * base * sign1;
		n2 = Math.random() * ((coordType == 'P' || coordType == 'p') ? Math.PI : 1) * base * sign2;
		this.setComplex(coordType, n1, n2);
	}
	
	/**
	 * Initializes the Complex Object in Rectangular with two random doubles from -base .. base.
	 * @param base Base to generate the random complex.
	 */
	public void setComplexRandomRec(int base) {
		this.setComplexRandom('C', base);
	}

	/**
	 * private Method. Returns the closest integer value as a double.
	 * @param value The value.
	 * @return The closest integer value as a double.
	 */
	private double integrize(double value) {
		int sign = value > 0 ? -1 : 1;
		double uval = sign * value;

		return Math.floor(uval) * sign;
	}

	/**
	 * Initializes the Complex Object in Rectangular with two random integers from -base .. base.
	 * @param base Base to generate the random integer.
	 */
	public void setComplexRandomRecInt(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C',this.integrize(this.rep), this.integrize(this.imp));
	}

	/**
	 * Initializes the Complex Object in Polar with two random doubles from -base .. base.
	 * @param base Base to generate the random complex.
	 */
	public void setComplexRandomPol(int base) {
		this.setComplexRandom('P', base);
	}

	/**
	 * Initializes the Complex Object in Polar with two random integers from -base .. base.
	 * @param base Base to generate the random integer.
	 */
	public void setComplexRandomPolInt(int base) {
		this.setComplexRandom('P', base);
		this.setComplex('P',this.integrize(this.mod), this.pha);
	}

	/**
	 * Initializes the Complex Object in Rectangular with a REAL random double from -base .. base.
	 * Imaginary part is set to 0.
	 * @param base Base to generate the random real.
	 */
	public void setComplexRandomReal(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C',this.rep, 0);
	}

	/**
	 * Initializes the Complex Object in Rectangular with an INTEGER random from -base .. base.
	 * Imaginary part is set to 0.
	 * @param base Base to generate the random integer.
	 */
	public void setComplexRandomInt(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C',this.integrize(this.rep), 0);
	}

	/**
	 * Initializes the Complex Object in Rectangular as an IMAGINARY PURE NUMBER with a REAL random double from -base .. base.
	 * Real part is set to 0.
	 * @param base Base to generate the random pure imaginary.
	 */
	public void setComplexRandomImag(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C', 0, this.imp);
	}

	/**
	 * Initializes the Complex Object in Rectangular as an IMAGINARY PURE NUMBER with an INTEGER random from -base .. base.
	 * Real part is set to 0.
	 * @param base Base to generate the random pure imaginary integer.
	 */
	public void setComplexRandomImagInt(int base) {
		this.setComplexRandom('C', base);
		this.setComplex('C', 0,this.integrize(this.imp));
	}

	/*
	 * ***********************************************
	 * GETTERS
	 * ***********************************************
	 */

	/**
	 * Gets the Complex REal value for comparisons.
	 * @return The Complex REal value.
	 */
	public double cre() { 
		return cre; }

	/**
	 * Gets the real part of the Complex Object.
	 * @return The real part.
	 */
	public double rep() { 
		return rep; }

	/**
	 * Gets the imaginary part of the Complex Object.
	 * @return The imaginary part.
	 */
	public double imp() { 
		return imp; }

	// returns abs/modulus/magnitude
	/**
	 * Gets the modulus of the Complex Object.
	 * @return The modulus.
	 */
	public double mod() { 
		return mod; }

	// returns angle/phase/argument
	/**
	 * Gets the phase of the Complex Object.
	 * @return The phase.
	 */
	public double pha() { 
		return pha; }

	/**
	 * Gets the phase of the Complex Object.
	 * @return The phase.
	 */
	public double phase() { 
		return pha; }

	/**
	 * Gets the absolute value or modulus of the Complex Object.
	 * @return The absolute value or modulus.
	 */
	public double abs() {
		return mod; }

	/**
	 * Package-private raw phase mutator for {@code ComplexCalculus.limit_inf}, which needs to
	 * overwrite only the phase of an already-built (typically infinite-modulus) result without
	 * recomputing rep/imp/mod from it (unlike {@link #setComplexPol}). Callers are responsible for
	 * the resulting rep/imp/mod/pha consistency, same contract as the private {@code raw()} factory.
	 */
	void setPhaRaw(double newPha) {
		this.pha = newPha;
	}

	/*
	 * ***********************************************
	 * PRESENTATION
	 * ***********************************************
	 */

	/*
	 * Delegates to ComplexState (extracted Sexta sesion, paso 2, Fase 2.2): config/precision/
	 * format/representation methods, unchanged public signatures.
	 */

	public static void setFormatON(boolean printStat) { ComplexState.setFormatON(printStat); }
	public static void setFormatON() { ComplexState.setFormatON(); }
	public static void setFixedON(int decimals, boolean printStat) { ComplexState.setFixedON(decimals, printStat); }
	public static void setFixedON(int decimals) { ComplexState.setFixedON(decimals); }
	public static void setScientificON(int decimals, boolean printStat) { ComplexState.setScientificON(decimals, printStat); }
	public static void setScientificON(int decimals) { ComplexState.setScientificON(decimals); }
	public static void setFormatOFF(boolean printStat) { ComplexState.setFormatOFF(printStat); }
	public static void setFormatOFF() { ComplexState.setFormatOFF(); }
	public static Boolean getFortmatStatus() { return ComplexState.getFortmatStatus(); }
	public static void setFixedOFF(boolean printStat) { ComplexState.setFixedOFF(printStat); }
	public static void setFixedOFF() { ComplexState.setFixedOFF(); }
	public static Boolean getFixedStatus() { return ComplexState.getFixedStatus(); }
	public static void setScientificOFF(boolean printStat) { ComplexState.setScientificOFF(printStat); }
	public static void setScientificOFF() { ComplexState.setScientificOFF(); }
	public static Boolean getScientificStatus() { return ComplexState.getScientificStatus(); }
	public static int getMaxDecimals() { return ComplexState.getMaxDecimals(); }
	public static int getSignificative() { return ComplexState.getSignificative(); }
	public static void printFormatStatus() { ComplexState.printFormatStatus(); }
	public static void storeFormatStatus() { ComplexState.storeFormatStatus(); }
	public static void restoreFormatStatus() { ComplexState.restoreFormatStatus(); }
	public static void resetFormatStatus() { ComplexState.resetFormatStatus(); }
	public static void setDigits() { ComplexState.setDigits(); }
	public static void setRepres(Representation Repres) { ComplexState.setRepres(Repres); }
	public static String getRepres() { return ComplexState.getRepres(); }
	public static void restoreRepres() { ComplexState.restoreRepres(); }

	/**
	 * Private Method. Normalizes the phase between (-pi, pi]
	 * @param phase to normalize.
	 * @return phase normalized.
	 * @apiNote Used to dispatch to one of three normalizedPhase_X variants ([-pi,pi] / (-pi,pi] /
	 * [0,2pi)). The other two ({@code normalizePhase_0}, {@code normalizePhase_2}) had zero
	 * callers anywhere in this codebase (grepped across all 7 files of the split) -- only this
	 * one, (-pi,pi], was ever actually selected -- so they were removed instead of kept as
	 * documented dead code, consistent with how {@code zeta_riemann_siegel}/
	 * {@code zeta_analytic_continuation}/{@code zeta_reflex} were handled earlier this session
	 * (private, no public API impact either way, unlike those three).
	 */
	private static double normalizePhase(double phase) {
		int sign = phase < 0.0 ? -1 : 1;
		phase *= sign;
		while (phase > Math.PI) phase -= DOS_PI;
		if (phase == Math.PI) return Math.PI;
		return phase * sign;
	}

	/**
	 * Private Method. Normalizes the Complex Object phase between -pi and pi.
	 */
	private void normalizePhase() {
		this.pha = Complex.normalizePhase(this.pha);
	}

	/**
	 * Returns the Rectangular string representation of the Complex Object.
	 * @return The string representation in Rectangular coordinates.
	 */
	public String toString() {
		return ComplexFormat.toString(this);
	}

	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS.
	 * @return The string representation of a complex number using scientific notation.
	 */
	public String toStringRec() {
		return ComplexFormat.toStringRec(this);
	}

	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS.
	 * @return The string representation of a complex number using scientific notation.
	 */
	public String toStringRecWolfram() {
		return ComplexFormat.toStringRecWolfram(this);
	}

	/**
	 * Returns the Rectangular string representation of the Complex Object. '1.0i' is represented as 'i.
	 * @return The string representation of a complex number in rectangular coordinates.
	 */
	public String toStringRecI() {
		return ComplexFormat.toStringRecI(this);
	}

	/**
	 * Builds the string representation of a complex number using scientific notation with MAX_DECIMALS.
	 * Corrects the VERY VERY BAD fact of having two ways to represent decimals and thousands in English/Latin way using commas for points or viceversa
	 * I would like to promote a worldwide amendment to adopt the English way for number representation and by the way the use of YYYY/MM/DD for dates as default
	 * @return The string representation of a complex number using scientific notation.
	 */
	public String toStringPol() {
		return ComplexFormat.toStringPol(this);
	}

	/**
	 * Express a complex number in GNUPlot format {<real>,<imag>}, where <real> and <imag> must be numerical constants.
	 * @return The string representation of a complex number in GNUPlot format
	 */
	public String toStringGNUPlot() {
		return ComplexFormat.toStringGNUPlot(this);
	}

	/**
	 * Prints the complex number to the output console
	 */
	public void print() {
		System.out.print(this.toString());
	}

	public void printRec() {
		System.out.print(this.toStringRec());
	}

	public void printPol() {
		System.out.print(this.toStringPol());
	}

	/**
	 * Prints the complex number with a new line to the output console
	 */
	public void println() {
		System.out.println(this.toString());
	}

	public void printlnRec() {
		System.out.println(this.toStringRec());
	}
	public void printlnPol() {
		System.out.println(this.toStringPol());
	}
	
	/**
	 * Prints the complex number to the output console with a test before it.
	 * @param str The text to put before
	 */
	public void print(String str) {
		System.out.print(str);
		System.out.print(this.toString());
	}

	public void printRec(String str) {
		System.out.print(str);
		System.out.print(this.toStringRec());
	}

	public void printPol(String str) {
		System.out.print(str);
		System.out.print(this.toStringPol());
	}
	/**
	 * Prints the complex number with a new line to the output console with a test before it.
	 * @param str The text to put before
	 */
	public void println(String str) {
		System.out.print(str);
		System.out.println(this.toString());
	}

	public void printlnRec(String str) {
		System.out.print(str);
		System.out.println(this.toStringRec());
	}
	public void printlnPol(String str) {
		System.out.print(str);
		System.out.println(this.toStringPol());
	}

	/*
	 * ***********************************************
	 * PRECISION
	 * ***********************************************
	 */
	
	/**
	 * Shows the Precision parameters used
	 */
	public static void showPrecision() {
		System.out.println(HEADINFO + "PRECISION............:" + precision());
		System.out.println(HEADINFO + "ZERO_THRESHOLD_EXACT.:" + zero_treshold_exact());
		System.out.println(HEADINFO + "ZERO_THRESHOLD_APPROX:" + zero_threshold_approx());
		System.out.println(HEADINFO + "SIGNIFICATIVE........:" + significative());
		System.out.println(HEADINFO + "DIGITS...............:" + digits());
		System.out.println(HEADINFO + "LIM_INF..............:" + LIM_INF);
		System.out.println(HEADINFO + "LIM_NUMDECS..........:" + LIM_NUMDECS);
		System.out.println(HEADINFO + "LIM_PRECISION........:" + LIM_PRECISION);
	}

	/*
	 * Delegates to ComplexState (extracted Sexta sesion, paso 2, Fase 2.2): unchanged public
	 * signatures.
	 */
	public static double precision() { return ComplexState.precision(); }
	public static void precision_(double value) { ComplexState.precision_(value); }
	public static void precision(double value) { ComplexState.precision(value); }
	public static double zero_treshold_exact() { return ComplexState.zero_treshold_exact(); }
	public static void zero_threshold_exact(double value) { ComplexState.zero_threshold_exact(value); }
	public static void zero_threshold_exact_prec(double value) { ComplexState.zero_threshold_exact_prec(value); }
	public static double zero_threshold_approx() { return ComplexState.zero_threshold_approx(); }
	public static void zero_threshold_approx(double value) { ComplexState.zero_threshold_approx(value); }
	public static int significative() { return ComplexState.significative(); }
	public static void significative_(int value) { ComplexState.significative_(value); }
	public static void significative(int value) { ComplexState.significative(value); }
	public static long digits() { return ComplexState.digits(); }
	public static void digits(long value) { ComplexState.digits(value); }
	public static void storePrecision() { ComplexState.storePrecision(); }
	public static void restorePrecision() { ComplexState.restorePrecision(); }
	public static void restorePrecisionFactorySettings() { ComplexState.restorePrecisionFactorySettings(); }

	/*
	 * ***********************************************
	 * BOXES & TITLES
	 * ***********************************************
	 * Delegates to ComplexBoxArt (extracted Sexta sesion, paso 2, Fase 2.1) -- zero coupling to
	 * Complex's instance fields or thread-local state, so the whole section moved verbatim behind
	 * these one-line public delegators. Signatures unchanged: external callers (Syseq, Spline,
	 * many TestComplex/* files use Complex.repeat(...)) need no changes.
	 */

	/**
	 * Substitute of String.repeat(int n). This method is not available for Java 1.8 on Windows
	 * @param str The String to repeat
	 * @param n The number of times to repeat the string
	 * @return The final String
	 */
	public static String repeat(String str, int n) {
		return ComplexBoxArt.repeat(str, n);
	}

	/**
	 * Prints a random BoxTitle from the ones defined
	 * @param size
	 * @param title
	 */
	public static void printBoxTitleRandom(int size, String title) {
		System.out.println(boxTitleRandom(size, title));
	}

	/**
	 * Generates a random BoxTitle from the ones defined
	 * @param size
	 * @param title
	 * @return
	 */
	public static String boxTitleRandom(int size, String title) {
		return ComplexBoxArt.boxTitleRandom(size, title);
	}

	/**
	 * Generates a BoxTitle from it components
	 * @param size
	 * @param title
	 * @param csi
	 * @param top
	 * @param csd
	 * @param msi
	 * @param msd
	 * @param mdi
	 * @param mdd
	 * @param cii
	 * @param bot
	 * @param cid
	 * @param nmid
	 * @return
	 */
	public static String  makeBoxTitle(int size, String title, String csi, String top, String csd, String msi, String msd, String mdi, String mdd, String cii, String bot, String cid, Boolean nmid) {
		return ComplexBoxArt.makeBoxTitle(size, title, csi, top, csd, msi, msd, mdi, mdd, cii, bot, cid, nmid);
	}

	/**
	 * Returns a Title inside of a single line box
	 * @param size The minimum size of the box
	 * @param title The text to put inside the box
	 * @return The String representation of the box title
	 */
	public static String boxTitle1(int size, String title) {
		return ComplexBoxArt.boxTitle1(size, title);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle2(int size, String title) {
		return ComplexBoxArt.boxTitle2(size, title);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle3(int size, String title) {
		return ComplexBoxArt.boxTitle3(size, title);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle4(int size, String title) {
		return ComplexBoxArt.boxTitle4(size, title);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle5(int size, String title) {
		return ComplexBoxArt.boxTitle5(size, title);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle6(int size, String title) {
		return ComplexBoxArt.boxTitle6(size, title);
	}

	/**
	 * Returns a text inside of a equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxTitle7(int size, String title) {
		return ComplexBoxArt.boxTitle7(size, title);
	}

	/**
	 * Prints a Title Box in the standar output
	 * @param boxId The box Id
	 * @param size The box size
	 * @param title The box title
	 */
	public static void printBoxTitle(int boxId, int size, String title) {
		ComplexBoxArt.printBoxTitle(boxId, size, title);
	}

	/**
	 * Prints a random BoxText from the ones defined
	 * @param size
	 * @param title
	 */
	public static void printBoxTextRandom(int size, String title) {
		System.out.println(boxTextRandom(size, title));
	}

	/**
	 * Generates a random BoxText from the ones defined
	 * @param size
	 * @param title
	 * @return
	 */
	public static String boxTextRandom(int size, String title) {
		return ComplexBoxArt.boxTextRandom(size, title);
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String makeBoxText(int size, String text, String csi, String top, String csd, String mdi, String mdd) {
		return ComplexBoxArt.makeBoxText(size, text, csi, top, csd, mdi, mdd);
	}

	/**
	 * Returns a text inside of a plus-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText1(int size, String text) {
		return ComplexBoxArt.boxText1(size, text);
	}

	/**
	 * Returns a text inside of a plus-minux-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText2(int size, String text) {
		return ComplexBoxArt.boxText2(size, text);
	}

	/**
	 * Returns a text inside of a X-*-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText3(int size, String text) {
		return ComplexBoxArt.boxText3(size, text);
	}

	/**
	 * Returns a text inside of a hash-equal-pipe box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText4(int size, String text) {
		return ComplexBoxArt.boxText4(size, text);
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText5(int size, String text) {
		return ComplexBoxArt.boxText5(size, text);
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText6(int size, String text) {
		return ComplexBoxArt.boxText6(size, text);
	}

	/**
	 * Returns a text inside of a colon (:) box
	 * @param size The minimum size of the box
	 * @param text The text to put inside the box
	 * @return The String representation of the box text
	 */
	public static String boxText7(int size, String text) {
		return ComplexBoxArt.boxText7(size, text);
	}

	/**
	 * Pints a box text in the standard output
	 * @param boxId The id ob the text box
	 * @param size The size of the text box
	 * @param text The text
	 */
	public static void printBoxText(int boxId, int size, String text) {
		ComplexBoxArt.printBoxText(boxId, size, text);
	}

	/*
	 * ***********************************************
	 * COPY & REPLICATION
	 * ***********************************************
	 */

	/**
	 * Copies the Complex Object values (imp, rep, mod, pha, cre) to a new one.
	 * @return The new Complex Object with the copied values.
	 */
	public Complex copy() {
		Complex cComplex = new Complex();
		cComplex.imp = this.imp();
		cComplex.rep = this.rep();
		cComplex.mod = this.mod();
		cComplex.pha = this.pha();
		cComplex.cre = this.cre();
		return cComplex;
	}

	/*
	 * ***********************************************
	 * UNARY OPERATIONS	import java.util.function.Function;
	 * ***********************************************
	 */

	/**
	 * Returns a new Complex Object with the opposite of this.
	 * @return The new Complex Object with the opposite.
	 */
	public Complex opposite() {
		return new Complex('C', -rep, -imp);
	}

	/**
	 * Returns a new Complex object which value is the conjugate of this.
	 * @return The new Complex Object with the conjugate.
	 */
	public Complex conjugate() {
		return new Complex('C', rep, -imp);
	}

	/** 
	 * Returns a new Complex object which value is the inverse of this (1/this).
	 * @return The new Complex Object with the inverse.
	 */
	public Complex inverse() {
		return new Complex('P', 1/this.mod, -this.pha);
	}

	/** 
	 * Shortcut to inverse.
	 * Returns a new Complex object which value is the inverse of this.
	 * @return The new Complex Object with the reciprocal.
	 */
	public Complex reciprocal() {
		return this.inverse();
	}

	/*
	 * ***********************************************
	 * BOOLEAN OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Cheks if the Complex is infinitely large in magnitude.
	 * @return true if this Complex value is infinitely large in magnitude, false otherwise
	 */
	public boolean isInfinite() {
		return Double.isInfinite(this.mod);
	}

	/**
	 * Checks if the Complex is a Not-a-Number (NaN).
	 * @return true if this Complex value is a Not-a-Number (NaN), false otherwise.
	 */
	public boolean isNaN() {
		return Double.isNaN(this.mod);
	}
	
	/**
	 * Checks if the Complex is zero.
	 * @return true if this Complex value is zero, false otherwise.
	 */
	public boolean isZero() {
		if (Math.abs(this.rep()) <= ComplexState.zero_treshold_exact()*CORRECTION_FACTOR && Math.abs(this.imp()) <= ComplexState.zero_treshold_exact()*CORRECTION_FACTOR) return true;
		else return false;
	}

	/**
	 * Checks if the imaginary part is zero.
	 * @return true if imaginary part is zero, false otherwise.
	 */
	public boolean imPartNull() {
		if (this.rep == 0.0) return this.imp == 0.0;
		if (Math.abs(imp/rep) <= ComplexState.zero_treshold_exact()*CORRECTION_FACTOR) return true;
		else return false;
	}

	/**
	 * Checks if the real part is zero.
	 * @return true if real part is zero, false otherwise.
	 */
	public boolean rePartNull() {
		if (this.imp == 0.0) return this.rep == 0.0;
		if (Math.abs(rep/imp) <= ComplexState.zero_treshold_exact()*CORRECTION_FACTOR) return true;
		else return false;
	}

	/**
	 * Compares the Complex Object with another using the equal operator.
	 * @param cNum Complex to compare.
	 * @return The result of the comparison.
	 */
	public boolean equals(Complex cNum) {
		return this.equals(cNum.rep, cNum.imp);
	}

	/**
	 * Overrides Object.equals so that Complex behaves correctly in collections (HashSet, HashMap,
	 * List.contains/indexOf, etc). Without this override only equals(Complex) exists, which is a
	 * plain overload: any equality check performed through a generic/Object-typed reference
	 * silently falls back to identity comparison instead of the tolerance-based comparison below.
	 * @param obj The object to compare.
	 * @return The result of the comparison using the same ZERO_THRESHOLD tolerance as equals(Complex).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Complex)) return false;
		return this.equals((Complex) obj);
	}

	/**
	 * Consistent with equals(Object): quantizes rep/imp to the current SIGNIFICATIVE precision
	 * so that values considered equal within ZERO_THRESHOLD tolerance are very likely to share
	 * a hash bucket. Not a mathematical guarantee (fuzzy equality can never map perfectly onto
	 * hashCode), but far safer than the inherited identity-based Object.hashCode.
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		long qRep = Math.round(this.rep * ComplexState.digits());
		long qImp = Math.round(this.imp * ComplexState.digits());
		return java.util.Objects.hash(qRep, qImp);
	}

	/**
	 * Compares the Complex Object with another given in Rectangular coordinates using the equal operator.
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @return The result of the comparison.
	 */
	public boolean equals(double n1, double n2) {
		return ((Math.abs(this.rep - n1) <= ComplexState.zero_treshold_exact()*CORRECTION_FACTOR) && (Math.abs(this.imp - n2) <= ComplexState.zero_treshold_exact()*CORRECTION_FACTOR));
	}

	/**
	 * Compares the Complex Object with another given in Rectangular coordinates using the equal
	 * operator, ignoring {@code numDecs} (kept for signature compatibility with real callers --
	 * {@code MatrixComplex}/{@code VectorComplex}/{@code MatrixComplexRank} -- but the comparison
	 * always uses {@link #equals(double, double)}'s fixed threshold, same as before this method's
	 * brief removal-then-restoral on 9 agosto 2026 confirmed it has live callers).
	 * @param n1 The real part.
	 * @param n2 The imaginary part.
	 * @param numDecs Unused.
	 * @return The result of the comparison.
	 */
	public boolean equals(double n1, double n2, int numDecs) {
		return equals(n1, n2);
	}

	/**
	 * Compares the Complex Object with another, rounding both to {@code numDecs} decimals first.
	 * @param cNum The complex number.
	 * @param numDecs The number of precision decimals.
	 * @return The result of the comparison.
	 */
	public boolean equals(Complex cNum, int numDecs) {
		Complex _this_ = Complex.round(this, numDecs);
		Complex _cNum_ = Complex.round(cNum, numDecs);
		return _this_.equals(_cNum_.rep, _cNum_.imp, numDecs);
	}

	/**
	 * Checks if a number is pure real
	 * @return True if the number is pure real
	 * @apiNote BUG FIXED (Vigesimoctava sesion, investigacion dedicada del bug reportado en el
	 * Bloque E de "nuevos instrumentos matematicos" -- ver Claude/ComplexArithRev.md): the previous
	 * body had an extra {@code if (rePartNull()) return false;} guard, which excluded the number
	 * ZERO from being "pure real" -- backwards, since zero is trivially a real number (and, by the
	 * same standard convention, also purely imaginary at the same time; it is the one value both
	 * predicates should agree on). Confirmed with {@code new Complex(0,0).isPureReal()==false}
	 * before this fix. Now simply "imaginary part is null", with no exception for zero.
	 */
	public Boolean isPureReal() {
		return imPartNull();
	}

	/**
	 * Checks if a number is pure imaginary
	 * @return True if the number is pure imaginary
	 * @apiNote BUG FIXED (Vigesimoctava sesion): same issue as {@link #isPureReal()}'s @apiNote,
	 * mirrored -- the extra {@code if (imPartNull()) return false;} guard excluded zero. Now simply
	 * "real part is null".
	 */
	public Boolean isPureImaginary() {
		return rePartNull();
	}

	/*
	 * ***********************************************
	 * ARITHMETIC OPERATIONS
	 * ***********************************************
	 */

	/**
	 * Returns a new Complex Object with the addition of 'this' and 'that' (this + that).
	 * @param that The Complex Object to add to 'this'.
	 * @return The new Complex Object with the result of the addition.
	 */
	public Complex plus(Complex that) {
		return new Complex('C', this.rep + that.rep, this.imp + that.imp);
	}

	/**
	 * Returns a new Complex Object with the addition of 'this' and the REAL number 'that' (this + that).
	 * @param that The REAL number to add to 'this'.
	 * @return The new Complex Object with the result of the addition.
	 */
	public Complex plus(double that) {
		return new Complex('C', this.rep + that, this.imp);
	}

	/**
	 * Returns a new Complex Object with the subtraction of 'this' minus 'that' (this - that).
	 * @param that The Complex Object to subtract to 'this'.
	 * @return The new Complex Object with the result of the subtraction.
	 */
	public Complex minus(Complex that) {
		return new Complex('C', this.rep - that.rep, this.imp - that.imp);
	}

	/**
	 * Returns a new Complex Object with the subtraction of 'this' minus the REAL number 'that' (this - that).
	 * @param that The REAL number to subtract to 'this'.
	 * @return The new Complex Object with the result of the subtraction.
	 */
	public Complex minus(double that) {
		return new Complex('C', this.rep - that, this.imp);
	}

	/**
	 * Returns a new Complex Object with the product of 'this' and 'that' (this * that).
	 * @param that The Complex Object to multiply to 'this'.
	 * @return The new Complex Object with the result of the product.
	 */
	public Complex times(Complex that) {
		// Rectangular product (a+bi)(c+di) = (ac-bd)+(ad+bc)i, reusing already-cached
		// moduli/phases for the polar pair instead of re-deriving them via cos/sin/atan2.
		double newRep = this.rep * that.rep - this.imp * that.imp;
		double newImp = this.rep * that.imp + this.imp * that.rep;
		double newMod = this.mod * that.mod;
		double newPha = normalizePhase(this.pha + that.pha);
		return raw(newRep, newImp, newMod, newPha);
	}

	/**
	 * Returns a new Complex Object with the product of 'this' and the REAL number 'alpha' (this * alpha).
	 * @param alpha The REAL number to multiply to 'this'.
	 * @return The new Complex Object with the result of the product.
	 */
	public Complex times(double alpha) {
		double newPha = alpha >= 0.0 ? this.pha : normalizePhase(this.pha + Math.PI);
		return raw(this.rep * alpha, this.imp * alpha, this.mod * Math.abs(alpha), newPha);
	}

	/**
	 * Returns a new Complex Object with the DOT product of 'this' and 'cNum' (this · cNum).
	 * The DOT product  is defined as the conjugated product (a* times b).
	 * @param cNum The Complex Object to multiply to 'this'.
	 * @return The new Complex Object with the result of the dot product.
	 */
	public Complex dotprod(Complex cNum) {
		return this.conjugate().times(cNum);
	}

	// returns a / b
	/**
	 * Returns a new Complex Object with the division of 'this' between 'that' (this / that).
	 * @param that The Complex Object to divide to 'this'.
	 * @return The new Complex Object with the result of the division.
	 */
	public Complex divides(Complex that) {
		// Division by complex zero: the rectangular formula below always yields rep=imp=NaN
		// here (0/0, since that.rep=that.imp=0 kills the numerator regardless of 'this'),
		// while newMod=this.mod/0 would independently give Infinity (this≠0) or NaN (this==0).
		// Left uncorrected, that mismatch produces a self-contradictory state: e.g. mod=Infinity
		// but pha a finite, meaningless value derived from that.pha (undefined for a modulus-0
		// divisor). Handled explicitly so all four fields agree on what is/isn't defined.
		if (that.mod == 0.0) {
			if (this.mod == 0.0) {
				// 0/0: truly indeterminate.
				return raw(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
			}
			// nonzero/0: magnitude is well-defined (Infinity), but a modulus-0 divisor has no
			// defined direction, so the resulting direction (rep/imp/pha) is undefined too.
			return raw(Double.NaN, Double.NaN, Double.POSITIVE_INFINITY, Double.NaN);
		}
		// Smith's algorithm for (a+bi)/(c+di): the previous formula computed
		// denom=that.mod*that.mod, i.e. squared an already-safe Math.hypot(c,d) result, which
		// overflows to Infinity for |that| beyond ~1.34e154 even though that.mod itself is still
		// finite -- silently zeroing newRep/newImp (finite/Infinity=0.0) while newMod (below,
		// computed independently from that.mod, never squared) stayed correct, leaving rep/imp
		// inconsistent with mod/pha. Smith's algorithm never forms c²+d² directly: it picks
		// whichever of c,d has the larger magnitude as pivot and uses the ratio of the other to
		// it (always <=1 in magnitude, so it can't itself overflow), keeping every intermediate
		// value near the actual working magnitudes instead of near c²+d².
		double a = this.rep, b = this.imp, c = that.rep, d = that.imp;
		double newRep, newImp;
		if (Math.abs(c) >= Math.abs(d)) {
			double r = d / c;
			double denom = c + d * r;
			newRep = (a + b * r) / denom;
			newImp = (b - a * r) / denom;
		} else {
			double r = c / d;
			double denom = d + c * r;
			newRep = (a * r + b) / denom;
			newImp = (b * r - a) / denom;
		}
		double newMod = this.mod / that.mod;
		double newPha = normalizePhase(this.pha - that.pha);
		return raw(newRep, newImp, newMod, newPha);
	}

	/**
	 * Returns a new Complex Object with the division of 'this' and the REAL number 'alpha' (this * alpha).
	 * @param alpha The REAL number to divide to 'this'.
	 * @return The new Complex Object with the result of the division.
	 */
	public Complex divides(double alpha) {
		double newPha = alpha >= 0.0 ? this.pha : normalizePhase(this.pha + Math.PI);
		return raw(this.rep / alpha, this.imp / alpha, this.mod / Math.abs(alpha), newPha);
	}

	/*
	 * ***********************************************
	 * IN-PLACE (MUTATING) ARITHMETIC OPERATIONS
	 * For accumulator-style hot loops (e.g. series/product summations) where reassigning to a
	 * freshly allocated Complex on every iteration is the dominant cost. These mutate 'this' and
	 * return 'this' for fluent chaining; they do NOT allocate. Unlike plus/minus/times/divides,
	 * calling these on a shared/cached Complex (e.g. Complex.ONE) would corrupt it - only use
	 * them on a private accumulator instance.
	 * ***********************************************
	 */

	/**
	 * In-place addition: mutates 'this' to 'this' + 'that'. Does not allocate.
	 * @param that The Complex Object to add to 'this'.
	 * @return 'this', for chaining.
	 */
	public Complex plusEq(Complex that) {
		this.rep += that.rep;
		this.imp += that.imp;
		this.setPolCoord();
		return this;
	}

	/**
	 * In-place addition with a REAL number: mutates 'this' to 'this' + 'that'. Does not allocate.
	 * @param that The REAL number to add to 'this'.
	 * @return 'this', for chaining.
	 */
	public Complex plusEq(double that) {
		this.rep += that;
		this.setPolCoord();
		return this;
	}

	/**
	 * In-place addition, RECTANGULAR ONLY: mutates 'rep'/'imp' to 'this'+'that', WITHOUT
	 * recomputing 'mod'/'pha'/'cre'. Does not allocate, zero trigonometric calls.
	 * <p>
	 * Use only for the intermediate steps of a chain of additions where the derived fields
	 * (mod/pha/cre) of every step but the last are never read -- e.g. accumulating a
	 * matrix-product cell over several terms. After the LAST {@code plusEqRaw()} of such a
	 * chain, call {@link #syncPolar()} once to bring mod/pha/cre back in sync before reading
	 * them; until then this object's mod/pha/cre are STALE (reflect a previous rep/imp, not the
	 * current one). Safe because {@code plusEq(Complex)} itself never reads {@code that}'s
	 * mod/pha/cre, only its rep/imp -- so skipping the intermediate {@link #setPolCoord()} calls
	 * changes nothing observable once {@link #syncPolar()} runs, it only skips recomputing values
	 * that would otherwise be overwritten by the next step before ever being read.
	 * @param that The Complex Object to add to 'this'.
	 * @return 'this', for chaining.
	 */
	public Complex plusEqRaw(Complex that) {
		this.rep += that.rep;
		this.imp += that.imp;
		return this;
	}

	/**
	 * Recomputes 'mod'/'pha'/'cre' from the current 'rep'/'imp'. Public entry point for
	 * {@link #setPolCoord()}, meant to close out a chain of {@link #plusEqRaw(Complex)} calls
	 * (or any other direct rep/imp mutation added in the future) before this object's polar
	 * fields are read.
	 * @return 'this', for chaining.
	 */
	public Complex syncPolar() {
		this.setPolCoord();
		return this;
	}

	/**
	 * In-place subtraction: mutates 'this' to 'this' - 'that'. Does not allocate.
	 * @param that The Complex Object to subtract from 'this'.
	 * @return 'this', for chaining.
	 */
	public Complex minusEq(Complex that) {
		this.rep -= that.rep;
		this.imp -= that.imp;
		this.setPolCoord();
		return this;
	}

	/**
	 * In-place subtraction with a REAL number: mutates 'this' to 'this' - 'that'. Does not allocate.
	 * @param that The REAL number to subtract from 'this'.
	 * @return 'this', for chaining.
	 */
	public Complex minusEq(double that) {
		this.rep -= that;
		this.setPolCoord();
		return this;
	}

	/**
	 * In-place product: mutates 'this' to 'this' * 'that'. Does not allocate; zero trigonometric
	 * calls, same rectangular-product shortcut as times(Complex).
	 * @param that The Complex Object to multiply 'this' by.
	 * @return 'this', for chaining.
	 */
	public Complex timesEq(Complex that) {
		double newRep = this.rep * that.rep - this.imp * that.imp;
		double newImp = this.rep * that.imp + this.imp * that.rep;
		double newMod = this.mod * that.mod;
		double newPha = normalizePhase(this.pha + that.pha);
		this.rep = newRep;
		this.imp = newImp;
		this.mod = newMod;
		this.pha = newPha;
		this.setCre();
		return this;
	}

	/**
	 * In-place product with a REAL number: mutates 'this' to 'this' * 'alpha'. Does not allocate.
	 * @param alpha The REAL number to multiply 'this' by.
	 * @return 'this', for chaining.
	 */
	public Complex timesEq(double alpha) {
		double newPha = alpha >= 0.0 ? this.pha : normalizePhase(this.pha + Math.PI);
		this.mod *= Math.abs(alpha);
		this.rep *= alpha;
		this.imp *= alpha;
		this.pha = newPha;
		this.setCre();
		return this;
	}

	/**
	 * In-place division: mutates 'this' to 'this' / 'that'. Does not allocate; zero trigonometric
	 * calls, same rectangular-division shortcut as divides(Complex).
	 * @param that The Complex Object to divide 'this' by.
	 * @return 'this', for chaining.
	 * @apiNote KNOWN BUG, fixed: this lacked the divide-by-complex-zero handling that
	 * {@link #divides(Complex)} already got in a previous session -- dividing by a modulus-0
	 * Complex used to fall straight through to the rectangular formula, giving rep=imp=NaN (0/0)
	 * while mod independently became Infinity or NaN, the same self-contradictory-state bug
	 * {@code divides(Complex)} was fixed for. Now shares the identical explicit handling. Also
	 * gains the Smith's-algorithm fix for large-|that| overflow -- see {@link #divides(Complex)}'s
	 * @apiNote for the full explanation.
	 */
	public Complex dividesEq(Complex that) {
		if (that.mod == 0.0) {
			if (this.mod == 0.0) {
				this.rep = Double.NaN; this.imp = Double.NaN; this.mod = Double.NaN; this.pha = Double.NaN;
			} else {
				this.rep = Double.NaN; this.imp = Double.NaN; this.mod = Double.POSITIVE_INFINITY; this.pha = Double.NaN;
			}
			this.setCre();
			return this;
		}
		double a = this.rep, b = this.imp, c = that.rep, d = that.imp;
		double newRep, newImp;
		if (Math.abs(c) >= Math.abs(d)) {
			double r = d / c;
			double denom = c + d * r;
			newRep = (a + b * r) / denom;
			newImp = (b - a * r) / denom;
		} else {
			double r = c / d;
			double denom = d + c * r;
			newRep = (a * r + b) / denom;
			newImp = (b * r - a) / denom;
		}
		double newMod = this.mod / that.mod;
		double newPha = normalizePhase(this.pha - that.pha);
		this.rep = newRep;
		this.imp = newImp;
		this.mod = newMod;
		this.pha = newPha;
		this.setCre();
		return this;
	}

	/**
	 * In-place division by a REAL number: mutates 'this' to 'this' / 'alpha'. Does not allocate.
	 * @param alpha The REAL number to divide 'this' by.
	 * @return 'this', for chaining.
	 */
	public Complex dividesEq(double alpha) {
		double newPha = alpha >= 0.0 ? this.pha : normalizePhase(this.pha + Math.PI);
		this.mod /= Math.abs(alpha);
		this.rep /= alpha;
		this.imp /= alpha;
		this.pha = newPha;
		this.setCre();
		return this;
	}

	/*
	 * ***********************************************
	 * FUNCTIONS
	 * ***********************************************
	 */

	/*
	 * Delegates to ComplexFunctions (extracted Sexta sesion, paso 2, Fase 2.5): special
	 * functions/trigonometry, unchanged public signatures.
	 */

	public static Complex sign(Complex z) { return ComplexFunctions.sign(z); }
	public static Complex inverse(Complex z) { return ComplexFunctions.inverse(z); }
	public static Complex signP(Complex z) { return ComplexFunctions.signP(z); }
	public static Complex signN(Complex z) { return ComplexFunctions.signN(z); }
	public static Complex log(Complex z) { return ComplexFunctions.log(z); }
	public static Complex log10(Complex z) { return ComplexFunctions.log10(z); }
	public static Complex logbase(Complex z, Complex base) { return ComplexFunctions.logbase(z, base); }
	public static Complex logbase(Complex z, double base) { return ComplexFunctions.logbase(z, base); }

	/**
	 * Calculates the value of 'this' raised to the Complex 'z'.
	 * @param z The Complex Object to raise 'this'.
	 * @return The new COmplex Object with the value of 'this' raised to 'z'.
	 */
	public Complex power(Complex z) {
		return ComplexFunctions.power(this, z);
	}

	/**
	 * Calculates the value of 'this' raised to the REAL number 'nExp'.
	 * @param nExp The Complex Object to raise 'this'.
	 * @return The new COmplex Object with the value of 'this' raised to 'nExp'.
	 */
	public Complex power(double nExp) {
		return ComplexFunctions.power(this, nExp);
	}

	/**
	 * Calculates the value of 'this' raised to the REAL number 'nExp'.
	 * @param nExp The Complex Object to raise 'this'.
	 * @return The new COmplex Object with the value of 'this' raised to 'nExp'.
	 */
	public Complex power(int iExp) {
		return ComplexFunctions.power(this, iExp);
	}

	public static Complex root(Complex z, double pot) { return ComplexFunctions.root(z, pot); }
	public static Complex root(Complex z, int pot, int k) { return ComplexFunctions.root(z, pot, k); }
	public static Complex sqrt(Complex z) { return ComplexFunctions.sqrt(z); }
	public static Complex sqrroot__(Complex z) { return ComplexFunctions.sqrroot__(z); }
	public static Complex sqrt(Complex z, int k) { return ComplexFunctions.sqrt(z, k); }
	public static Complex sqrroot__(Complex z, int k) { return ComplexFunctions.sqrroot__(z, k); }
	public static Complex exp(Complex z) { return ComplexFunctions.exp(z); }
	public static Complex exp(double d) { return ComplexFunctions.exp(d); }
	public static double mod(Complex z) { return ComplexFunctions.mod(z); }
	public static double abs(Complex z) { return ComplexFunctions.abs(z); }
	public static Complex positive(Complex z) { return ComplexFunctions.positive(z); }

	/**
	 * Checks whether the complex number is an integer/zero or not
	 * @return True if is integer, otherwise false
	 */
	public boolean isInteger() {
		return (isPureReal() && (Math.ceil(rep) == Math.floor(rep)));
	}
	
	/**
	 * Checks whether the complex number is an integer greater than zero or not
	 * @return True if is integer positive, otherwise false
	 */
	public boolean isIntegerPositive() {
		return (rep > 0 && isInteger());
	}
	
	/**
	 * Checks whether the complex number is an integer less than zero or not
	 * @return True if is integer negative, otherwise false
	 */
	public boolean isIntegerNegative() {
		return (rep < 0 && isInteger());	
	}

	/**
	 * Checks whether the complex number is an integer great or equal to zero or not
	 * @return True if is integer positive/zero, otherwise false
	 */
	public boolean isIntegerPositiveZero() {
		return (rep >= 0 && isInteger());
	}
	
	/**
	 * Checks whether the complex number is an integer less or equal to zero or not
	 * @return True if is integer negative/zero, otherwise false
	 */
	public boolean isIntegerNegativeZero() {
		return (rep <= 0 && isInteger());	
	}
	
	public static Complex gamma(double d) { return ComplexFunctions.gamma(d); }
	public static Complex gamma(Complex z) { return ComplexFunctions.gamma(z); }
	public static Complex gamma_zones(Complex z) { return ComplexFunctions.gamma_zones(z); }
	public static Complex gamma_integral(double n) { return ComplexFunctions.gamma_integral(n); }
	public static Complex gamma_integral(Complex z) { return ComplexFunctions.gamma_integral(z); }
	public static Complex gamma_integral2(Complex z) { return ComplexFunctions.gamma_integral2(z); }
	public static Complex gamma_weiertrass(Complex z) { return ComplexFunctions.gamma_weiertrass(z); }
	public static Complex gamma_euler(Complex z) { return ComplexFunctions.gamma_euler(z); }
	public static Complex gamma_nemes(Complex z) { return ComplexFunctions.gamma_nemes(z); }
	public static Complex gamma_fast(Complex z) { return ComplexFunctions.gamma_fast(z); }
	public static double factorial(int n) { return ComplexFunctions.factorial(n); }
	public static Complex factorial(Complex n) { return ComplexFunctions.factorial(n); }
	public static Complex beta(Complex p, Complex q) { return ComplexFunctions.beta(p, q); }
	public static Complex digamma(Complex z) { return ComplexFunctions.digamma(z); }
	public static Complex polygamma(int n, Complex z) { return ComplexFunctions.polygamma(n, z); }
	public static Complex erf(Complex z) { return ComplexFunctions.erf(z); }
	public static Complex erfc(Complex z) { return ComplexFunctions.erfc(z); }
	public static Complex eta(Complex s) { return ComplexFunctions.eta(s); }
	public static Complex polylog(Complex s, Complex z) { return ComplexFunctions.polylog(s, z); }
	public static Complex zeta(Complex s) { return ComplexFunctions.zeta(s); }
	public static Complex zeta_re(Complex s) { return ComplexFunctions.zeta_re(s); }
	public static Complex zeta_ext(Complex s) { return ComplexFunctions.zeta_ext(s); }
	public static Complex zeta_primes(Complex s) { return ComplexFunctions.zeta_primes(s); }
	public static Complex zeta_havil(Complex s) { return ComplexFunctions.zeta_havil(s); }
	public static Complex ChebyshevZero(int n, int k) { return ComplexFunctions.ChebyshevZero(n, k); }
	public static Complex binomialCoef(int n, int k) { return ComplexFunctions.binomialCoef(n, k); }
	public static Complex binomialCoef(Complex n, Complex k) { return ComplexFunctions.binomialCoef(n, k); }

	public static String deg_DMS(double deg) { return ComplexFunctions.deg_DMS(deg); }
	public static String rad_DMS(double rad) { return ComplexFunctions.rad_DMS(rad); }
	public static Complex sin(Complex z) { return ComplexFunctions.sin(z); }
	public static Complex sin(double zd) { return ComplexFunctions.sin(zd); }
	public static Complex csc(Complex z) { return ComplexFunctions.csc(z); }
	public static Complex csc(double zd) { return ComplexFunctions.csc(zd); }
	public static Complex cos(Complex z) { return ComplexFunctions.cos(z); }
	public static Complex cos(double zd) { return ComplexFunctions.cos(zd); }
	public static Complex sec(Complex z) { return ComplexFunctions.sec(z); }
	public static Complex sec(double zd) { return ComplexFunctions.sec(zd); }
	public static Complex tan(Complex z) { return ComplexFunctions.tan(z); }
	public static Complex tan(double zd) { return ComplexFunctions.tan(zd); }
	public static Complex cot(Complex z) { return ComplexFunctions.cot(z); }
	public static Complex cot(double zd) { return ComplexFunctions.cot(zd); }
	public static Complex sinh(Complex z) { return ComplexFunctions.sinh(z); }
	public static Complex sinh(double zd) { return ComplexFunctions.sinh(zd); }
	public static Complex csch(Complex z) { return ComplexFunctions.csch(z); }
	public static Complex csch(double zd) { return ComplexFunctions.csch(zd); }
	public static Complex cosh(Complex z) { return ComplexFunctions.cosh(z); }
	public static Complex cosh(double zd) { return ComplexFunctions.cosh(zd); }
	public static Complex sech(Complex z) { return ComplexFunctions.sech(z); }
	public static Complex sech(double zd) { return ComplexFunctions.sech(zd); }
	public static Complex tanh(Complex z) { return ComplexFunctions.tanh(z); }
	public static Complex tanh(double zd) { return ComplexFunctions.tanh(zd); }
	public static Complex coth(Complex z) { return ComplexFunctions.coth(z); }
	public static Complex coth(double zd) { return ComplexFunctions.coth(zd); }
	public static Complex arcsin(Complex z) { return ComplexFunctions.arcsin(z); }
	public static Complex arccos(Complex z) { return ComplexFunctions.arccos(z); }
	public static Complex arctan(Complex z) { return ComplexFunctions.arctan(z); }
	public static Complex acotan(Complex z) { return ComplexFunctions.acotan(z); }
	public static Complex arcsinh(Complex z) { return ComplexFunctions.arcsinh(z); }
	public static Complex arccosh(Complex z) { return ComplexFunctions.arccosh(z); }
	public static Complex arctanh(Complex z) { return ComplexFunctions.arctanh(z); }
	public static Complex acoth(Complex z) { return ComplexFunctions.acoth(z); }
	public static Complex sinc(Complex z) { return ComplexFunctions.sinc(z); }
	public static Complex cosc(Complex z) { return ComplexFunctions.cosc(z); }
	public static Complex tanc(Complex z) { return ComplexFunctions.tanc(z); }
	public static Complex chebyshev(int degree, Complex cx) { return ComplexFunctions.chebyshev(degree, cx); }
	public static Complex besselJ(Complex nu, Complex z) { return ComplexFunctions.besselJ(nu, z); }
	public static Complex besselY(Complex nu, Complex z) { return ComplexFunctions.besselY(nu, z); }

	/*
	 * ***********************************************
	 * INTEGRATION & DERIVATION
	 * ***********************************************
	 */
	
	/*
	 * Delegates to ComplexCalculus (extracted Sexta sesion, paso 2, Fase 2.6): unchanged public
	 * signatures.
	 */
	public static Complex integrate(double lolimit, double uplimit, Function <Complex, Complex> func, int numDec) { return ComplexCalculus.integrate(lolimit, uplimit, func, numDec); }
	public static Complex integrate(String slolimit, String suplimit, Function <Complex, Complex> func, int numDec) { return ComplexCalculus.integrate(slolimit, suplimit, func, numDec); }
	public static Complex integrate(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, int numDec) { return ComplexCalculus.integrate(lolimit, uplimit, func, numDec); }
	static public Complex derivative(Complex point, Function <Complex, Complex> func, double precision) { return ComplexCalculus.derivative(point, func, precision); }
	static public Complex derivative(double point, Function <Complex, Complex> func, double precision) { return ComplexCalculus.derivative(point, func, precision); }

	/*
	 * ***********************************************
	 * ROUND & INT-DEC PARTS OF A NUMBER
	 * ***********************************************
	 */
	
	/**
	 * Gets the decimal part of a double number
	 * @param num The number
	 * @return The decimal part
	 * @apiNote Not called anywhere in this codebase (grepped across all of src/). Also,
	 * {@link BigDecimal#intValue()} silently truncates/wraps for values outside the int range
	 * (per its own contract), so this has no overflow guard for large num. Left as-is and
	 * documented rather than removed, consistent with how other confirmed-dead methods in this
	 * class (e.g. the {@code *Red__} family) were handled.
	 */
	static public double getDecPart(double num) {
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(num));
		int intValue = bigDecimal.intValue();
		return num -  intValue;
	}

	/**
	 * Gets the integer part of a double number
	 * @param num The number
	 * @return The integre part
	 * @apiNote Not called anywhere in this codebase. Same overflow caveat as {@link #getDecPart(double)}.
	 */
	static public double getIntPart(double num) {
		BigDecimal bigDecimal = new BigDecimal(String.valueOf(num));
		int intValue = bigDecimal.intValue();
		return intValue;
	}

	/**
	 * Truncates a double number to decs decimals
	 * @param complex The number to round
	 * @param d The number of decimals
	 * @return The rounded number
	 * @apiNote Not called anywhere in this codebase except internally by {@link #trunc(Complex, int)},
	 * which itself has no external callers either. Despite the name, this rounds (via
	 * {@code String.format}'s HALF_UP-ish behaviour) rather than truncating/flooring -- for actual
	 * truncation to d decimals, use {@code Math.floor(num * 10^d) / 10^d} instead. Left undocumented
	 * behaviour aside, this is otherwise correct; kept as dead code rather than removed.
	 */
	static public double trunc(double num, int d) {
		String format = "%." + d +"f";
		String strNum = String.format(format, num).replace(",", ".");
		double roundNbr = Double.parseDouble(strNum);
		return roundNbr;
	}

	/**
	 * Truncates a complex from its Rectangular components
	 * @param num Complex to truncate
	 * @param d Nbr of decimals to keep
	 * @return the new truncated complex
	 * @apiNote Not called anywhere in this codebase (grepped across all of src/).
	 */
	static public Complex trunc(Complex num, int d) {
		Complex truncated = new Complex();
		truncated.setComplexRec(Complex.trunc(num.rep, d), Complex.trunc(num.imp, d));
		return truncated;
	}
	
	/**
	 * Rounds a double number to decs decimals
	 * @param complex The number to round
	 * @param d The number of decimals
	 * @return The rounded number
	 */
	public static double round(double value, int decs) {
	    if (decs < 0) throw new IllegalArgumentException();

	    Double DBval = value;
	    if (DBval.isNaN()) return value;
		if (DBval.isInfinite()) return value;

	    // new BigDecimal(double) (the previous code here) constructs from the EXACT binary value
	    // of the double, which for most decimal literals has dozens of extra digits invisible in
	    // the double's usual decimal rendering (e.g. 1.005 is actually
	    // 1.00499999999999989341...). Rounding THAT with HALF_UP silently rounds down whenever the
	    // "true" binary value sits just below the decimal boundary a caller would expect from the
	    // number's usual printed form -- confirmed: round(1.005,2) gave 1.0 instead of 1.01, and
	    // the same happened for 2.675/2, 0.145/2, 1.15/1, 0.35/1. Using the canonical decimal
	    // string (Double.toString, matching what the number "looks like") rounds based on that
	    // intended decimal value instead, which is what round(Complex,int)/roundRec/roundPol's
	    // callers (Eigenspace/Polynom, deduplicating eigenvalues/roots by rounded value) and
	    // limit()'s convergence check actually want.
	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(decs, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Rounds a complex number to decs decimals using default method
	 * @param complex The number to round
	 * @param decs The number of decimals
	 * @return The rounded number
	 * @apiNote Always delegates to {@link #roundRec(Complex, int)}, never {@link #roundPol(Complex, int)}.
	 * This is intentional, not an oversight: the two production callers of this method
	 * ({@code Eigenspace.java}, deduplicating/counting eigenvalue multiplicity, and
	 * {@code Polynom.java}, cleaning up polynomial roots) both work with values that are computed
	 * and compared as rectangular components; rounding rep/imp directly avoids the extra
	 * trig-function round-trip (and its associated FP noise) that converting to polar and back
	 * would introduce. Call {@link #roundPol(Complex, int)} directly if polar-based rounding is
	 * what's actually needed.
	 */
	static public Complex round(Complex complex, int decs) {
		if (complex.isNaN()) return complex;
		if (complex.isInfinite()) return complex;
		return roundRec(complex, decs);
	}

	/**
	 * Rounds a complex number to decs decimals using Rectangular Coordinates
	 * @param complex The number to round
	 * @param decs The number of decimals
	 * @return The rounded number
	 */
	static public Complex roundRec(Complex complex, int decs) {
		double rep, imp;
		rep = round(complex.rep, decs);
		imp = round(complex.imp, decs);
		return new Complex(rep, imp);
	}

	/**
	 * Rounds a complex number to decs decimals using Polar Coordinates
	 * @param num The complex number to round
	 * @param decs The number of decimals
	 * @return The rounded complex number
	 */
	static public Complex roundPol(Complex complex, int decs) {
		Complex rndComplex = new Complex();
		rndComplex.setComplexPol(round(complex.mod, decs), round(complex.pha, decs));
		return rndComplex;
	}

	/*
	 * ***********************************************
	 * LIMITS
	 * ***********************************************
	 */

	static int LIM_NUMDECS = 10; // Number of significative decimals for limits calculations
	static double LIM_PRECISION = Math.pow(10, -LIM_NUMDECS);
	// Hard iteration cap for limit_inf's point.mod-doubling search, see its Javadoc.
	static int LIM_INF_MAX_ITER = 2000;

	/*
	 * Delegates to ComplexCalculus (extracted Sexta sesion, paso 2, Fase 2.6): unchanged public
	 * signatures. LIM_NUMDECS/LIM_PRECISION/LIM_INF_MAX_ITER above stay here (read by
	 * showPrecision() too), referenced from ComplexCalculus as Complex.LIM_NUMDECS/etc.
	 */
	static public Complex limit(Function <Complex, Complex> func, Complex point) { return ComplexCalculus.limit(func, point); }
	static public Complex limit(Function <Complex, Complex> func, double point) { return ComplexCalculus.limit(func, point); }
	static public Complex limit_inf(Function <Complex, Complex> func) { return ComplexCalculus.limit_inf(func); }
	static public Complex limit_Minf(Function <Complex, Complex> func) { return ComplexCalculus.limit_Minf(func); }
	static public boolean isContinuous(Function <Complex, Complex> func, Complex point) { return ComplexCalculus.isContinuous(func, point); }
	static public boolean isContinuous(Function <Complex, Complex> func, double point) { return ComplexCalculus.isContinuous(func, point); }

	/*
	 * RESTOS
	 */
/**	
	public static Complex integrateCurv(Complex lolimit, Complex uplimit, Function <Complex, Complex> func, int numDec) {
		Complex vector = uplimit.minus(lolimit);
		double precision = Math.pow(10, -Math.abs(++numDec));
		
		Complex integral = new Complex();

		//Recorrer la distancia
		double phiOrigin = lolimit.pha <= uplimit.pha ? lolimit.pha : uplimit.pha;
		double phiEnd = lolimit.pha > uplimit.pha ? lolimit.pha : uplimit.pha;
		double phiStep = (phiEnd - phiOrigin) * precision;
		int iter = 0;
		
		System.out.println("phiOrigin:" + phiOrigin);
		System.out.println("phiEnd   :" + phiEnd);
		System.out.println("phiStep  :" + phiStep);
		
		
		while (phiOrigin+(iter+1)*phiStep < phiEnd) {
			System.out.println("iter:" + iter + "   phiEnd:" + phiEnd + "   phi:" + (phiOrigin+iter*phiStep));
			
			++iter;
		}
		
		return integral;
	}
	**/
}
