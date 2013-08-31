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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.simulation;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Strings;

/**
 * The class GamaRuntimeInformation.
 * 
 * @author drogoul
 * @since 13 d�c. 2011
 * 
 */
public class SimulationClock {

	/**
	 * The delay, a value between 0 and 1, that can be introduced by the user (through the graphical
	 * interface) for each cycle. A value of 1 means no delay, while a delay of 0 will pause the
	 * simulation during 1 second for each cycle. Any intermediate value will be treated as a
	 * percentage of a second equal to (1 - delay) * 100
	 */
	private double delay = 1d;

	/** The number of simulation cycles elapsed so far. */
	private int cycle = 0;

	/**
	 * The current value of time in the model timescale. The base unit is the second (see
	 * <link>IUnits</link>). This value is normally always equal to step * cycle. Note that time can
	 * take values smaller than 1 (in case of a step in milliseconds, for instance), but not smaller
	 * than 0.
	 */
	private double time = 0d;

	/**
	 * The length (in model time) of the interval between two cycles. Default is 1 (or 1 second if
	 * time matters). Step can be smaller than 1 (to express an interval smaller than one second).
	 */
	private double step = 1d;

	/** The duration (in milliseconds) of the last cycle elapsed. */
	private long duration = 0;

	/**
	 * The total duration in milliseconds since the beginning of the simulation. Since it is the
	 * addition of the consecutive durations of cycles, note that it may be different from the
	 * actual duration of the simulation if the user chooses to pause it, for instance.
	 */
	private long total_duration = 0;

	/**
	 * A variable used to compute duration (holds the time, in milliseconds, of the beginning of a
	 * cycle).
	 */
	private long start = 0;

	/**
	 * Whether to display the number of cycles or a more readable information (in model time)
	 */
	private boolean displayCycles = true;

	/**
	 * @throws GamaRuntimeException
	 *             Sets a new value to the cycle.
	 * @param i the new value
	 */

	// FIXME Make setCycle() or incrementCycle() advance the other variables as well, so as to allow writing
	// "cycle <- cycle + 1" in GAML and have the correct information computed.
	private void setCycle(final int i) throws GamaRuntimeException {
		if ( i < 0 ) { throw GamaRuntimeException.error("The current cycle of a simulation cannot be negative"); }

		cycle = i;
	}

	// /**
	// * Increments the cycle by 1.
	// * @return the new value of cycle
	// */
	//
	// private int incrementCycle() {
	// cycle++;
	// return cycle;
	// }

	/**
	 * Returns the current value of cycle
	 */
	public int getCycle() {
		return cycle;
	}

	/**
	 * Sets the value of the current time of the simulation. Cannot be negative.
	 * 
	 * @throws GamaRuntimeException
	 * @param i a positive double
	 */
	public void setTime(final double i) throws GamaRuntimeException {
		if ( i < 0 ) { throw GamaRuntimeException.error("The current time of a simulation cannot be negative"); }
		time = i;
	}

	/**
	 * Gets the current value of time in the simulation
	 * @return a positive double
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Sets the value of the current step duration (in model time) of the simulation.
	 *             Cannot be
	 *             negative.
	 * 
	 * @throws GamaRuntimeException
	 * @param i a positive double
	 */

	public void setStep(final double i) throws GamaRuntimeException {
		if ( i < 0 ) { throw GamaRuntimeException
			.error("The interval between two cycles of a simulation cannot be negative"); }
		step = i <= 0 ? 1 : i;
	}

	/**
	 * Return the current value of step
	 * @return a positive double
	 */
	public double getStep() {
		return step;
	}

	/**
	 * Initializes start at the beginning of a step
	 */
	public void resetDuration() {
		start = System.currentTimeMillis();
	}

	/**
	 * Computes the duration by substracting start to the current time in milliseconds
	 */
	private void computeDuration() {
		duration = System.currentTimeMillis() - start;
		total_duration += duration;
	}

	/**
	 * Gets the duration (in milliseconds) of the latest cycle elapsed so far
	 * @return a duration in milliseconds
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Gets the average duration (in milliseconds) over
	 * @return a duration in milliseconds
	 */
	public double getAverageDuration() {
		if ( cycle == 0 ) { return 0; }
		return (double) total_duration / (double) cycle;
	}

	/**
	 * Gets the total duration in milliseconds since the beginning of the current simulation.
	 * @return a duration in milliseconds
	 */
	public long getTotalDuration() {
		return total_duration;
	}

	public void step() {
		setCycle(cycle + 1);
		setTime(time + step);
		computeDuration();
		waitDelay();
	}

	public void waitDelay() {
		if ( delay == 1d ) { return; }
		try {
			long max = (long) (1000 - delay * 1000);
			if ( duration >= max ) { return; }
			Thread.sleep((max - duration));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void reset() throws GamaRuntimeException {
		setCycle(0);
		setTime(0d);
		setDelay(1d);
		total_duration = 0;
		step = 1;
	}

	/**
	 * @param selection
	 */
	public void setDelay(final double value) {
		// From 0 (slowest) to 1 (fastest)
		delay = value < 0 ? 0d : value > 1d ? 1d : value;
	}

	public double getDelay() {
		return delay;
	}

	public void toggleDisplay() {
		displayCycles = !displayCycles;
	}

	public void beginCycle() {
		resetDuration();
		// String info = displayCycles ? "cycle " + getCycle() : Strings.asDate(time, null);
		// if ( !GAMA.getExperiment().isBatch() ) {
		// GuiUtils.informStatus(info);
		// }
	}

	public String getInfo() {
		int cycle = getCycle();
		String info =
			displayCycles ? "" + cycle + (cycle == 1 ? " cycle " : " cycles ") + "elapsed" : Strings.asDate(time, null);
		return info;
	}

	public static class ExperimentClock extends SimulationClock {
		//
		// @Override
		// public void beginCycle() {
		// resetDuration();
		// // String info = displayCycles ? "cycle " + getCycle() : Strings.asDate(time, null);
		// // if ( !GAMA.getExperiment().isBatch() ) {
		// // GuiUtils.informStatus(info);
		// // }
		// }
	}

}
