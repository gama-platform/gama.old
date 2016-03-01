/*********************************************************************************************
 *
 *
 * 'SimulationClock.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.simulation;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.operators.Strings;
import msi.gaml.types.Types;

/**
 * The class GamaRuntimeInformation.
 *
 * @author drogoul
 * @since 13 d�c. 2011
 *
 */
/**
 * @author administrateur
 *
 */
public class SimulationClock {

	// Minimum duration of a cycle in seconds
	// private double currentCycleDelay = 0d;

	/**
	 * OLD
	 * The delay, a value between 0 and 1, that can be introduced by the user (through the graphical
	 * interface) for each cycle. A value of 1 means no delay, while a delay of 0 will pause the
	 * simulation during 1 second for each cycle. Any intermediate value will be treated as a
	 * percentage of a second equal to (1 - delay) * 100
	 * OLD
	 *
	 * delay is the number of milliseconds that represents the minimum duration of a simulation cycle. It can be defined
	 * through the user interface or by the model. A value of 0 now means no delay, a value of 1 means 1 second of
	 * delay, and any value above the exact number of milliiseconds
	 */
	// private double delay = 0d;

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
	protected long duration = 0;

	/**
	 * The total duration in milliseconds since the beginning of the simulation. Since it is the
	 * addition of the consecutive durations of cycles, note that it may be different from the
	 * actual duration of the simulation if the user chooses to pause it, for instance.
	 */
	protected long total_duration = 0;

	/**
	 * A variable used to compute duration (holds the time, in milliseconds, of the beginning of a
	 * cycle).
	 */
	private long start = 0;

	/**
	 * Whether to display the number of cycles or a more readable information (in model time)
	 */
	private boolean displayCycles = true;

	private GamaDate current_date = null;

	private GamaDate starting_date = null;

	private final SimulationAgent simulation;

	public SimulationClock(final SimulationAgent sim) {
		simulation = sim;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Sets a new value to the cycle.
	 * @param i the new value
	 */

	// FIXME Make setCycle() or incrementCycle() advance the other variables as well, so as to allow writing
	// "cycle <- cycle + 1" in GAML and have the correct information computed.
	public void setCycle(final int i) throws GamaRuntimeException {
		if ( i < 0 ) { throw GamaRuntimeException.error("The current cycle of a simulation cannot be negative",
			simulation.getScope()); }

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
		if ( i < 0 ) { throw GamaRuntimeException.error("The current time of a simulation cannot be negative",
			simulation.getScope()); }
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
			.error("The interval between two cycles of a simulation cannot be negative", simulation.getScope()); }
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
	 * Computes the duration by subtracting start to the current time in milliseconds
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

	public void step(final IScope scope) {
		setCycle(cycle + 1);
		setTime(time + step);
		if ( current_date != null ) {
			current_date.addSeconds((int) step);
		}
		computeDuration();
		waitDelay();
	}

	public void waitDelay() {
		double delay = getDelayInMilliseconds();
		if ( delay == 0d ) { return; }
		try {
			if ( duration >= delay ) { return; }
			Thread.sleep((long) delay - duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void reset() throws GamaRuntimeException {
		setCycle(0);
		setTime(0d);
		total_duration = 0;
		step = 1;
	}

	public void toggleDisplay() {
		displayCycles = !displayCycles;
	}

	public void beginCycle() {
		resetDuration();
	}

	public String getInfo() {
		int cycle = getCycle();
		String info = displayCycles ? "" + cycle + (cycle == 1 ? " cycle " : " cycles ") + "elapsed"
			: starting_date == null ? Strings.asDate(time, null) : current_date.toString();
			return info;
	}

	public static class ExperimentClock extends SimulationClock {

		public ExperimentClock() {
			super(null);
		}

		@Override
		public void waitDelay() {}

		/**
		 * @param totalDuration
		 */
		public void setTotalDuration(final long totalDuration) {
			this.total_duration = totalDuration;
		}

		public void setLastDuration(final long duration) {
			this.duration = duration;
		}

	}

	public double getDelayInMilliseconds() {
		if ( simulation == null ) { return 0.0; }
		return simulation.getExperiment().getMinimumDuration() * 1000;
		// return currentCycleDelay * 1000;
	}

	public GamaDate getCurrentDate() {
		return current_date;
	}

	public void setCurrentDate(final GamaDate date) {
		this.current_date = date;
	}

	public GamaDate getStartingDate() {
		return starting_date;
	}

	public void setStartingDate(final GamaDate starting_date) {
		setCurrentDate((GamaDate) Types.DATE.cast(null, starting_date, null, true));
		this.starting_date = starting_date;
	}

}
