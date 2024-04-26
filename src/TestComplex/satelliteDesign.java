/**
 * 
 */
package TestComplex;

import com.ipserc.arith.matrixcomplex.*;

/**
 * @author ipserc
 *
 */
public class satelliteDesign {

	private static boolean Reduced = true;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		double batteryVoltage = 110; // Volts
		double batteryCapacity = 42000; // miliAmperes·hour
		double numEclipses = 42; // Eclipses / year
		double eclipseDuration = 71; // minutes / Eclipse
		double batteryCycle = 100; // Cycles
		double lifeTimeSatellite = 25; // years
		
		MatrixComplex circuit = new MatrixComplex(	" 2, 5, 0, 0, 0, 5;" 	// 2I1 + 5I2 = 5 volts
												+ 	" 0,-5, 3, 1, 0, 0;"	// 5I2 = 3I3 + 1I4
												+ 	" 0, 0, 0, 1,-2, 0;"	// 1I4 = 2I5
												+ 	" 1,-1,-1, 0, 0, 0;"	// I1 = I2 + I3
												+ 	" 0, 0, 1,-1,-1, 0");	// I3 = I4 + I5
		MatrixComplex intensities = new MatrixComplex();
		
		circuit.complexMatrix[0][5].setComplexRec(batteryVoltage, 0); // Sets the battery voltage in the systems equation
		intensities = circuit.solve();
		intensities.println("Intensidades");
		
		double intensity = intensities.complexMatrix[0][0].mod();
		
		System.out.println("Intensidad Total Amperios:" + intensity);
		
		batteryCapacity = batteryCapacity / 1000 * 60; // Amperes·minute 
		double batteryDischargeTime = batteryCapacity / intensity;
		System.out.println("tiempo Descarga Batería minutos:" + batteryDischargeTime);
		
		double totalEclipseTime = numEclipses * eclipseDuration * lifeTimeSatellite;
		System.out.println("tiempo total Eclipses minutos:" + totalEclipseTime);
		
		double dischargeCycles = totalEclipseTime / batteryDischargeTime;
		System.out.println("ciclos de Descarga:" + dischargeCycles);
		
		double numBatteries =  dischargeCycles / batteryCycle;
		System.out.println("Número de baterías:" + Math.ceil(numBatteries));	
	}

}
