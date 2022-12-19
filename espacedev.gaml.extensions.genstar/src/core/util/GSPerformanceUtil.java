/*******************************************************************************************************
 *
 * GSPerformanceUtil.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util;

import ummisco.gama.dev.utils.DEBUG;

/**
 * Should be called with the logger of the caller; for instance if the caller logger is "gospl.sampler.hierarchical",
 * performance will be logged into "gospl.sampler.hierarchical.performance" with an INFO level of verbosity.
 *
 * @author Kevin Chapuis
 * @author Samuel Thiriot
 */
public class GSPerformanceUtil {

	/**
	 * The Enum Level.
	 */
	public enum Level {

		/** The info. */
		INFO,
		/** The error. */
		ERROR,
		/** The warn. */
		WARN,
		/** The debug. */
		DEBUG,
		/** The trace. */
		TRACE;
	}

	/** The stemp calls. */
	private int stempCalls;

	/** The latest stemp. */
	private long latestStemp;

	/** The cumul stemp. */
	private double cumulStemp;

	/** The first syso. */
	private boolean firstSyso;

	/** The performance test description. */
	private String performanceTestDescription;

	/** The objectif. */
	private double objectif;

	/** The level. */
	private final Level level;

	/** The Constant END. */
	private static final String END = "END OF PROCESS";

	/**
	 * Instantiates a new GS performance util.
	 *
	 * @param performanceTestDescription
	 *            the performance test description
	 */
	public GSPerformanceUtil(final String performanceTestDescription) {
		this(performanceTestDescription, Level.INFO);
	}

	/**
	 * Instantiates a new GS performance util.
	 *
	 * @param performanceTestDescription
	 *            the performance test description
	 * @param level
	 *            the level
	 */
	public GSPerformanceUtil(final String performanceTestDescription, final Level level) {
		this.level = level;
		resetStemp();
		this.performanceTestDescription = performanceTestDescription;
	}

	////////////////////////////////////////////////

	/**
	 * Gets the stemp performance.
	 *
	 * @param message
	 *            the message
	 * @return the stemp performance
	 */
	public String getStempPerformance(final String message) {
		long thisStemp = System.currentTimeMillis();
		double timer = (thisStemp - latestStemp) / 1000d;
		if (latestStemp != 0l) { cumulStemp += timer; }
		this.latestStemp = thisStemp;
		if (END.equals(message)) {
			double cumul = (double) Math.round(cumulStemp * 1000) / 1000;
			this.resetStemp();
			return END + " -> overall time = " + cumul + " s.";
		}
		return message + " -> " + timer + " s / " + (double) Math.round(cumulStemp * 1000) / 1000 + " s";
	}

	/**
	 * Gets the stemp performance.
	 *
	 * @param stepFoward
	 *            the step foward
	 * @return the stemp performance
	 */
	public String getStempPerformance(final int stepFoward) {
		stempCalls += stepFoward;
		return getStempPerformance(stepFoward == 0 ? "Init." : "Step " + stempCalls);
	}

	/**
	 * Gets the stemp performance.
	 *
	 * @param proportion
	 *            the proportion
	 * @return the stemp performance
	 */
	public String getStempPerformance(final double proportion) {
		if (proportion == 1.0) return getStempPerformance(END);
		return getStempPerformance(Math.round(proportion * 100) + "%");
	}

	/**
	 * Syso stemp performance.
	 *
	 * @param step
	 *            the step
	 * @param caller
	 *            the caller
	 */
	public void sysoStempPerformance(final int step, final Object caller) {
		sysoStempMessage(getStempPerformance(step), caller);
	}

	/**
	 * Syso stemp performance.
	 *
	 * @param step
	 *            the step
	 * @param message
	 *            the message
	 * @param caller
	 *            the caller
	 */
	public void sysoStempPerformance(final int step, final String message, final Object caller) {
		sysoStempMessage(getStempPerformance(step) + " | " + message, caller);
	}

	/**
	 * Syso stemp performance.
	 *
	 * @param proportion
	 *            the proportion
	 * @param caller
	 *            the caller
	 */
	public void sysoStempPerformance(final double proportion, final Object caller) {
		sysoStempMessage(getStempPerformance(proportion), caller);
	}

	/**
	 * Syso stemp performance.
	 *
	 * @param proportion
	 *            the proportion
	 * @param message
	 *            the message
	 * @param caller
	 *            the caller
	 */
	public void sysoStempPerformance(final double proportion, final String message, final Object caller) {
		sysoStempMessage(getStempPerformance(proportion) + " | " + message, caller);
	}

	/**
	 * Syso stemp performance.
	 *
	 * @param message
	 *            the message
	 * @param caller
	 *            the caller
	 */
	public void sysoStempPerformance(final String message, final Object caller) {
		sysoStempMessage(getStempPerformance(message), caller);
	}

	// MESSAGE

	/**
	 * Syso stemp message.
	 *
	 * @param message
	 *            the message
	 */
	public void sysoStempMessage(final String message) {
		this.printLog(message, Level.INFO);
	}

	/**
	 * Syso stemp message.
	 *
	 * @param message
	 *            the message
	 * @param theLevel
	 *            the level
	 */
	public void sysoStempMessage(final String message, final Level theLevel) {
		this.printLog(message, theLevel);
	}

	/**
	 * Syso stemp message.
	 *
	 * @param message
	 *            the message
	 * @param caller
	 *            the caller
	 */
	private void sysoStempMessage(final String message, final Object caller) {
		String callerString = caller.getClass().getSimpleName();
		if (caller.getClass().equals(String.class)) { callerString = caller.toString(); }

		if (firstSyso) {
			this.printLog("\nMethod caller: " + callerString + "\n-------------------------\n"
					+ performanceTestDescription + "\n-------------------------", null);
			firstSyso = false;
		}
		this.printLog(message, this.level);
	}

	/**
	 * Syso stemp message.
	 *
	 * @param message
	 *            the message
	 * @param fillers
	 *            the fillers
	 */
	public void sysoStempMessage(final String message, final Object... fillers) {
		// logger.printf(level, message, fillers);
		DEBUG.OUT(level + "->" + message);
	}

	// OBJECTIF PART (to compute advancement toward a goal)

	/**
	 * Sets the objectif.
	 *
	 * @param objectif
	 *            the new objectif
	 */
	public void setObjectif(final double objectif) { this.objectif = objectif; }

	/**
	 * Gets the objectif.
	 *
	 * @return the objectif
	 */
	public double getObjectif() { return objectif; }

	/**
	 * Reset stemp.
	 */
	public void resetStemp() {
		this.resetStempCalls();
		firstSyso = true;
		performanceTestDescription = "no reason";
	}

	/**
	 * Reset stemp calls.
	 */
	public void resetStempCalls() {
		stempCalls = 0;
		latestStemp = 0l;
		cumulStemp = 0d;
	}

	// LOGGER PART

	/**
	 * Prints the log.
	 *
	 * @param message
	 *            the message
	 * @param loglevel
	 *            the loglevel
	 */
	private void printLog(final String message, Level loglevel) {
		if (loglevel == null) { loglevel = this.level; }
		if (Level.ERROR.equals(loglevel)) {
			DEBUG.ERR(message);
		} else if (Level.WARN.equals(loglevel) || !Level.INFO.equals(loglevel)) {
			DEBUG.OUT(message);
		} else {
			DEBUG.LOG(message);
		}
	}

}
