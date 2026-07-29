/**
 * runJava.sh ~/eclipse-workspace/complexarith/bin/TestComplex/TestSpline01.class
 */
package TestComplex;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import com.ipserc.arith.complex.Complex;
import com.ipserc.arith.matrixcomplex.MatrixComplex;
import com.ipserc.arith.polynom.*;
import com.panayotis.gnuplot.JavaPlot;

/**
 * @author ipserc
 *
 */
public final class TestSpline01 {
	
	private static List<MatrixComplex> doSpline(int order, String pares, List<MatrixComplex> pointsList) {
		switch (order) {
			case 1: return doSpline1( pares, pointsList);
			case 3: return doSpline3( pares, pointsList);
			default:
				pointsList = doSpline1( pares, pointsList);
				pointsList = doSpline3( pares, pointsList);
				return pointsList;
		}
	}

	private static List<MatrixComplex> doSpline1(String pares, List<MatrixComplex> pointsList) {
		MatrixComplex points;
		MatrixComplex pTable;
		pTable = new MatrixComplex(pares);
		pTable.println("Points");
		Spline interpolSpline1 = new Spline(1, pTable);
		interpolSpline1.interpolate(true);
		interpolSpline1.print();
		points = interpolSpline1.walkInterval();
		pointsList.add(points);
		return pointsList;
	}

	private static List<MatrixComplex> doSpline3(String pares, List<MatrixComplex> pointsList) {
		MatrixComplex points;
		MatrixComplex pTable;
		pTable = new MatrixComplex(pares);
		pTable.println("Points");
		Spline interpolSpline3 = new Spline(3, pTable);
		interpolSpline3.interpolate(true);
		interpolSpline3.print();
		interpolSpline3.eval(new Complex(1.7)).println("Spline3[1.7]: ");
		points = interpolSpline3.walkInterval();
		//points.println("Spline(3, pTable)");
		pointsList.add(points);
		return pointsList;
	}
	
	private static List<MatrixComplex> directriz(List<MatrixComplex> pointsList, String pares) {
		MatrixComplex points;
		MatrixComplex pTable;
		pTable = new MatrixComplex(pares);
		Complex lolimit = pTable.getItem(0, 0);
		Complex uplimit = pTable.getItem(pTable.rows()-1, 0);		
		Polynom directriz = new Polynom();
		directriz = directriz.interpolationNewton(pTable);
		directriz.println("Interpolación de Newton:");
		System.out.println(directriz.toWolfram_poly("Interpolación de Newton (Geogebra): "));
		System.out.println(directriz.toPolynom("Ec. aproximación Primos: "));
		points = directriz.walkInterval(lolimit, uplimit);
		pointsList.add(points);
		return pointsList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<MatrixComplex> pointsList = new ArrayList<MatrixComplex>();
		int sigDigits = 12;
		Polynom interpol = new Polynom(2);
		String pares;
		int order = 3;

		//Complex.setFormatOFF();
		//Complex.precision(1E-150);
		
		Complex.exact(false);
		Complex.setScientificON(sigDigits);
		//Complex.setFixedON(5);
		Polynom.sampleBase = 500;

		Spline.version();
		
		/* */
		pares = 	""
					+	"  0,  1;  1,  2;  2,  3;  3,  5;  4,  7;  5, 11;  6, 13;  7, 17;  8, 19;  9, 23;"
					+ 	" 10, 29; 11, 31; 12, 37; 13, 41; 14, 43; 15, 47; 16, 53; 17, 59; 18, 61; 19, 67;"
					+ 	" 20, 71; 21, 73; 22, 79; 23, 83; 24, 89; 25, 97; 26,101; 27,103; 28,107; 29,109;"
					+ 	" 30,113; 31,127; 32,131; 33,137; 34,139; 35,149; 36,151; 37,157; 38,163; 39,167;"
					+ 	" 40,173; 41,179; 42,181; 43,191; 44,193; 45,197; 46,199; 47,211; 48,223; 49,227 ";
		pointsList = doSpline(order, pares, pointsList);

		pares = 	"49,227;"
					+	"50,229;51,233;52,239;53,241;54,251;55,257;56,263;57,269;58,271;59,277;"
					+	"60,281;61,283;62,293;63,307;64,311;65,313;66,317;67,331;68,337;69,347;"
					+	"70,349;71,353;72,359;73,367;74,373;75,379;76,383;77,389;78,397;79,401;"
					+	"80,409;81,419;82,421;83,431;84,433;85,439;86,443;87,449;88,457;89,461;"
					+	"90,463;91,467;92,479;93,487;94,491;95,499;96,503;97,509;98,521;99,523";
		pointsList = doSpline(order, pares, pointsList);

		pares =		"99,523;"
					+	"100,541;101,547;102,557;103,563;104,569;105,571;106,577;107,587;108,593;109,599;"
					+	"110,601;111,607;112,613;113,617;114,619;115,631;116,641;117,643;118,647;119,653;"
					+	"120,659;121,661;122,673;123,677;124,683;125,691;126,701;127,709;128,719;129,727;"
					+	"130,733;131,739;132,743;133,751;134,757;135,761;136,769;137,773;138,787;139,797;"
					+	"140,809;141,811;142,821;143,823;144,827;145,829;146,839;147,853;148,857;149,859";
		
		pointsList = doSpline(order, pares, pointsList);

		pares =		"149,859;"
					+	"150,863;151,877;152,881;153,883;154,887;155,907;156,911;157,919;158,929;159,937;"
					+	"160,941;161,947;162,953;163,967;164,971;165,977;166,983;167,991;168,997;169,1009;"
					+	"170,1013;171,1019;172,1021;173,1031;174,1033;175,1039;176,1049;177,1051;178,1061;179,1063;"
					+	"180,1069;181,1087;182,1091;183,1093;184,1097;185,1103;186,1109;187,1117;188,1123;189,1129;"
					+	"190,1151;191,1153;192,1163;193,1171;194,1181;195,1187;196,1193;197,1201;198,1213;199,1217";
		pointsList = doSpline(order, pares, pointsList);

		pares =		"199,1217;"
				+	"200,1223;201,1229;202,1231;203,1237;204,1249;205,1259;206,1277;207,1279;208,1283;209,1289;"
				+	"210,1291;211,1297;212,1301;213,1303;214,1307;215,1319;216,1321;217,1327;218,1361;219,1367;"
				+	"220,1373;221,1381;222,1399;223,1409;224,1423;225,1427;226,1429;227,1433;228,1439;229,1447;"
				+	"230,1451;231,1453;232,1459;233,1471;234,1481;235,1483;236,1487;237,1489;238,1493;239,1499;"
				+	"240,1511;241,1523;242,1531;243,1543;244,1549;245,1553;246,1559;247,1567;248,1571;249,1579";
		pointsList = doSpline(order, pares, pointsList);

		pares =		"249,1579;"
				+	"250,1583;251,1597;252,1601;253,1607;254,1609;255,1613;256,1619;257,1621;258,1627;259,1637;"
				+	"260,1657;261,1663;262,1667;263,1669;264,1693;265,1697;266,1699;267,1709;268,1721;269,1723;"
				+	"270,1733;271,1741;272,1747;273,1753;274,1759;275,1777;276,1783;277,1787;278,1789;279,1801;"
				+	"280,1811;281,1823;282,1831;283,1847;284,1861;285,1867;286,1871;287,1873;288,1877;289,1879;"
				+	"290,1889;291,1901;292,1907;293,1913;294,1931;295,1933;296,1949;297,1951;298,1973;299,1979";
		pointsList = doSpline(order, pares, pointsList);

		pares =		"299,1979;"
				+	"300,1987;301,1993;302,1997;303,1999;304,2003;305,2011;306,2017;307,2027;308,2029;309,2039;"
				+	"310,2053;311,2063;312,2069;313,2081;314,2083;315,2087;316,2089;317,2099;318,2111;319,2113;"
				+	"320,2129;321,2131;322,2137;323,2141;324,2143;325,2153;326,2161;327,2179;328,2203;329,2207;"
				+	"330,2213;331,2221;332,2237;333,2239;334,2243;335,2251;336,2267;337,2269;338,2273;339,2281;"
				+	"340,2287;341,2293;342,2297;343,2309;344,2311;345,2333;346,2339;347,2341;348,2347;349,2351";
		pointsList = doSpline(order, pares, pointsList);

		pares =		"349,2351;"
				+	"350,2357;351,2371;352,2377;353,2381;354,2383;355,2389;356,2393;357,2399;358,2411;359,2417;"
				+	"360,2423;361,2437;362,2441;363,2447;364,2459;365,2467;366,2473;367,2477;368,2503;369,2521;"
				+	"370,2531;371,2539;372,2543;373,2549;374,2551;375,2557;376,2579;377,2591;378,2593;379,2609;"
				+	"380,2617;381,2621;382,2633;383,2647;384,2657;385,2659;386,2663;387,2671;388,2677;389,2683;"
				+	"390,2687;391,2689;392,2693;393,2699;394,2707;395,2711;396,2713;397,2719;398,2729;399,2731";
		pointsList = doSpline(order, pares, pointsList);

		pares =		"399,2731;"
				+	"400,2741;401,2749;402,2753;403,2767;404,2777;405,2789;406,2791;407,2797;408,2801;409,2803;"
				+	"410,2819;411,2833;412,2837;413,2843;414,2851;415,2857;416,2861;417,2879;418,2887;419,2897;"
				+	"420,2903;421,2909;422,2917;423,2927;424,2939;425,2953;426,2957;427,2963;428,2969;429,2971;"
				+	"430,2999;431,3001;432,3011;433,3019;434,3023;435,3037;436,3041;437,3049;438,3061;439,3067;"
				+	"440,3079;441,3083;442,3089;443,3109;444,3119;445,3121;446,3137;447,3163;448,3167;449,3169";
		pointsList = doSpline(order, pares, pointsList);

		/* */
		pares =		"449,3169;"
				+	"450,3181;451,3187;452,3191;453,3203;454,3209;455,3217;456,3221;457,3229;458,3251;459,3253;"
				+	"460,3257;461,3259;462,3271;463,3299;464,3301;465,3307;466,3313;467,3319;468,3323;469,3329;"
				+	"470,3331;471,3343;472,3347;473,3359;474,3361;475,3371;476,3373;477,3389;478,3391;479,3407;"
				+	"480,3413;481,3433;482,3449;483,3457;484,3461;485,3463;486,3467;487,3469;488,3491;489,3499;"
				+	"490,3511;491,3517;492,3527;493,3529;494,3533;495,3539;496,3541;497,3547;498,3557;499,3559";
		pointsList = doSpline(order, pares, pointsList);
		/* */

		pares = 	"0 ,1; 6, 13; 18, 61; 36,151; 59,277; 87,449; 119,653; 154,887; 191,1153; 229,1447; 269,1723; 307,2027;"
				+ 	"344,2311; 379,2609; 411,2833; 439,3067; 462,3271; 480,3413; 492,3527; 499,3559";
		pointsList = directriz(pointsList, pares);
		/*
		 * Para calcular los puntos directriz
		for(int i = 0; i < 19; ++i) {
			double v1 = (0-499.0)/2.0;
			double v2 = (0+499.0)/2.0;
			
			//Complex.ChebyshevZero(19, i).println("zero("+i+"):");
			System.out.println(Math.floor(Complex.ChebyshevZero(19, i).times(v1).plus(v2).rep())) ;
		}
		*/
		interpol.plotRe(pointsList, "Re Spline Prime Numbers Function : "+sigDigits+" Significatives Digits");
	}
}
