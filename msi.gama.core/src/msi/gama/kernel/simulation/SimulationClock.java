/*********************************************************************************************
 *
 * 'SimulationClock.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.kernel.simulation;

import java.time.DateTimeException;
import java.time.temporal.ChronoUnit;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.operators.Dates;

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

	final StringBuilder infoStringBuilder = new StringBuilder();

	/** The number of simulation cycles elapsed so far. */
	private volatile int cycle = 0;

	/**
	 * The current value of time in the model timescale. The base unit is the second (see <link>IUnits</link>). This
	 * value is normally always equal to step * cycle. Note that time can take values smaller than 1 (in case of a step
	 * in milliseconds, for instance), but not smaller than 0.
	 */
	// AD: not kept anymore as the whole computation is based on dates
	// private double time = 0d;

	/**
	 * The length (in model time) of the interval between two cycles. Default is 1 (or 1 second if time matters). Step
	 * can be smaller than 1 (to express an interval smaller than one second).
	 */
	// AD: kept as an expression to allow temporal expressions to be evaluated
	// in the context of the starting_date
	// private IExpression step = new ConstantExpression(1);
	private double step = Dates.DATES_TIME_STEP.getValue();

	/** The duration (in milliseconds) of the last cycle elapsed. */
	protected long duration = 0;

	/**
	 * The total duration in milliseconds since the beginning of the simulation. Since it is the addition of the
	 * consecutive durations of cycles, note that it may be different from the actual duration of the simulation if the
	 * user chooses to pause it, for instance.
	 */
	protected long totalDuration = 0;

	/**
	 * A variable used to compute duration (holds the time, in milliseconds, of the beginning of a cycle).
	 */
	private long start = 0;

	/**
	 * Whether to display the number of cycles or a more readable information (in model time)
	 */
	private volatile boolean displayCycles = true;

	private GamaDate startingDate = null;
	private GamaDate currentDate = null;

	private final boolean outputCurrentDateAsDuration;

	private final IScope scope;

	public SimulationClock(final IScope scope) {
		final IModel model = scope.getModel();
		outputCurrentDateAsDuration =
				model == null ? true : !((ModelDescription) model.getDescription()).isStartingDateDefined();
		this.scope = scope;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Sets a new value to the cycle.
	 * @param i
	 *            the new value
	 */

	// FIXME Make setCycle() or incrementCycle() advance the other variables as
	// well, so as to allow writing
	// "cycle <- cycle + 1" in GAML and have the correct information computed.
	public void setCycle(final int i) throws GamaRuntimeException {
		if (i < 0) { throw GamaRuntimeException.error("The current cycle of a simulation cannot be negative", scope); }
		if (i < cycle) { throw GamaRuntimeException.error("The current cycle of a simulation cannot be set backwards",
				scope); }
		final int previous = cycle;
		cycle = i;
		setCurrentDate(getCurrentDate().plus(step, cycle - previous, ChronoUnit.SECONDS));
	}

	public void incrementCycle() {
		cycle++;
		setCurrentDate(getCurrentDate().plus(step, ChronoUnit.SECONDS));
	}

	public void resetCycles() {
		cycle = 0;
		startingDate = null;
		currentDate = null;
	}

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
	 * @param i
	 *            a positive double
	 */
	// AD cannot be set anymore
	// public void setTime(final double i) throws GamaRuntimeException {
	// if (i < 0) {
	// throw GamaRuntimeException
	// .error("The current time of a simulation cannot be set. Please set
	// starting_date instead", scope);
	// }
	// // time = i;
	// }

	/**
	 * Gets the current value of time in the simulation
	 * 
	 * @return a positive double
	 */
	public double getTimeElapsedInSeconds() {
		return getStartingDate().until(getCurrentDate(), ChronoUnit.SECONDS);
	}

	/**
	 * @throws GamaRuntimeException
	 *             Sets the value of the current step duration (in model time) of the simulation. Cannot be negative.
	 *
	 * @throws GamaRuntimeException
	 * @param i
	 *            a positive double
	 */

	public void setStep(final double exp) throws GamaRuntimeException {
		if (exp <= 0) { throw GamaRuntimeException
				.error("The interval between two cycles of a simulation cannot be negative or null", scope); }
		step = exp;

		// step = i <= 0 ? 1 : i;
	}

	/**
	 * Return the current value of step
	 * 
	 * @return a positive double
	 */
	public double getStepInSeconds() {
		return step;
	}

	public long getStepInMillis() {
		return (long) (step * 1000);
	}

	/**
	 * Initializes start at the beginning of a step
	 */
	public void resetDuration() {
		start = System.currentTimeMillis();
		// duration = 0;
	}

	public void resetTotalDuration() {
		resetDuration();
		duration = 0;
		totalDuration = 0;
	}

	/**
	 * Computes the duration by subtracting start to the current time in milliseconds
	 */
	private void computeDuration() {
		duration = System.currentTimeMillis() - start;
		totalDuration += duration;
	}

	/**
	 * Gets the duration (in milliseconds) of the latest cycle elapsed so far
	 * 
	 * @return a duration in milliseconds
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Gets the average duration (in milliseconds) over
	 * 
	 * @return a duration in milliseconds
	 */
	public double getAverageDuration() {
		if (cycle == 0) { return 0; }
		return (double) totalDuration / (double) cycle;
	}

	/**
	 * Gets the total duration in milliseconds since the beginning of the current simulation.
	 * 
	 * @return a duration in milliseconds
	 */
	public long getTotalDuration() {
		return totalDuration;
	}

	public void step(final IScope scope) {
		incrementCycle();
		computeDuration();
		waitDelay();
	}

	public void waitDelay() {
		final double delay = getDelayInMilliseconds();
		if (delay == 0d) { return; }
		try {
			if (duration >= delay) { return; }
			Thread.sleep((long) delay - duration);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void reset() throws GamaRuntimeException {
		resetCycles();
		resetTotalDuration();
	}

	public void toggleDisplay() {
		displayCycles = !displayCycles;
	}

	public void beginCycle() {
		resetDuration();
	}

	public String getInfo() {
		final int cycle = getCycle();
		final ITopLevelAgent agent = scope.getRoot();
		infoStringBuilder.setLength(0);
		infoStringBuilder.append(agent.getName()).append(": ").append(cycle).append(cycle == 1 ? " cycle " : " cycles ")
				.append("elapsed ");

		try {
			final String date = outputCurrentDateAsDuration ? Dates.asDuration(getStartingDate(), getCurrentDate())
					: getCurrentDate().toString("yyyy-MM-dd HH:mm:ss");
			infoStringBuilder.append("[").append(date).append("]");
		} catch (final DateTimeException e) {}
		return infoStringBuilder.toString();
	}

	public static class ExperimentClock extends SimulationClock {

		public ExperimentClock(final IScope scope) {
			super(scope);
		}

		@Override
		public void waitDelay() {}

		/**
		 * @param totalDuration
		 */
		public void setTotalDuration(final long totalDuration) {
			this.totalDuration = totalDuration;
		}

		public void setLastDuration(final long duration) {
			this.duration = duration;
		}

		@Override
		public String getInfo() {
			final int cycle = getCycle();
			final String info = "Experiment: " + cycle + (cycle == 1 ? " cycle " : " cycles ") + "elapsed";
			return info;
		}

	}

	public double getDelayInMilliseconds() {
		return scope.getExperiment().getMinimumDuration() * 1000;
	}

	public GamaDate getCurrentDate() {
		if (currentDate == null) {
			currentDate = getStartingDate();
		}
		return currentDate;
	}

	public GamaDate getStartingDate() {
		if (startingDate == null)
			setStartingDate(Dates.DATES_STARTING_DATE.getValue());
		return startingDate;
	}

	public void setStartingDate(final GamaDate starting_date) {
		this.startingDate = starting_date;
		this.currentDate = starting_date;
	}

	public void setCurrentDate(final GamaDate date) {
		currentDate = date;
	}

	public boolean outputAsDuration() {
		return outputCurrentDateAsDuration;
	}

}
