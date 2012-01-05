/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.simulation;

/**
 * The class GamaRuntimeInformation.
 * 
 * @author drogoul
 * @since 13 déc. 2011
 * 
 */
public class SimulationClock {

	public static boolean TREAT_ERRORS_AS_FATAL = false;
	public static boolean TREAT_WARNINGS_AS_ERRORS = false;
	private static double currentDelay = 1d;
	private static int currentCycle = 0;
	private static int currentTime = 0;
	private static int currentStep = 1;
	private static long stepLength = 0;
	private static long startTime = 0;

	public static void setCycle(final int i) {
		currentCycle = i;
	}

	private static int incrementCycle() {
		currentCycle++;
		return currentCycle;
	}

	public static int getCycle() {
		return currentCycle;
	}

	public static void setTime(final int i) {
		currentTime = i;
	}

	private static int incrementTime() {
		currentTime += currentStep;
		return currentTime;
	}

	public static int getTime() {
		return currentTime;
	}

	public static void setStep(final int i) {
		currentStep = i <= 0 ? 1 : i;
	}

	public static int getStep() {
		return currentStep;
	}

	public static void resetStepLength() {
		startTime = System.currentTimeMillis();
	}

	private static void computeStepLength() {
		stepLength = System.currentTimeMillis() - startTime;
	}

	public static long getStepLength() {
		return stepLength;
	}

	public static void step() {
		incrementCycle();
		incrementTime();
		computeStepLength();
		waitDelay();
	}

	public static void waitDelay() {
		if ( currentDelay == 1d ) { return; }
		try {
			Thread.sleep((long) (1000 - currentDelay * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void reset() {
		setCycle(0);
		setTime(0);
	}

	/**
	 * @param selection
	 */
	public static void setDelay(final double selection) {
		// From 0 (slowest) to 1 (fastest)
		currentDelay = selection;
	}

	public static double getDelay() {
		return currentDelay;
	}

}
