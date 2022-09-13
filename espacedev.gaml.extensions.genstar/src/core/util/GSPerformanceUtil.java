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

	public enum Level {
		INFO, ERROR, WARN, DEBUG, TRACE;
	}

	private int stempCalls;
	private long latestStemp;
	private double cumulStemp;

	private boolean firstSyso;
	private String performanceTestDescription;

	private double objectif;

	private final Level level;

	private static final String END = "END OF PROCESS";

	public GSPerformanceUtil(final String performanceTestDescription) {
		this(performanceTestDescription, Level.INFO);
	}

	public GSPerformanceUtil(final String performanceTestDescription, final Level level) {
		this.level = level;
		resetStemp();
		this.performanceTestDescription = performanceTestDescription;
	}

	////////////////////////////////////////////////

	public String getStempPerformance(final String message) {
		long thisStemp = System.currentTimeMillis();
		double timer = (thisStemp - latestStemp) / 1000d;
		if (latestStemp != 0l) { cumulStemp += timer; }
		this.latestStemp = thisStemp;
		if (message == END) {
			double cumul = (double) Math.round(cumulStemp * 1000) / 1000;
			this.resetStemp();
			return END + " -> overall time = " + cumul + " s.";
		}
		return message + " -> " + timer + " s / " + (double) Math.round(cumulStemp * 1000) / 1000 + " s";
	}

	public String getStempPerformance(final int stepFoward) {
		stempCalls += stepFoward;
		return getStempPerformance(stepFoward == 0 ? "Init." : "Step " + stempCalls);
	}

	public String getStempPerformance(final double proportion) {
		if (proportion == 1.0) return getStempPerformance(END);
		return getStempPerformance(Math.round(Math.round(proportion * 100)) + "%");
	}

	public void sysoStempPerformance(final int step, final Object caller) {
		sysoStempMessage(getStempPerformance(step), caller);
	}

	public void sysoStempPerformance(final int step, final String message, final Object caller) {
		sysoStempMessage(getStempPerformance(step) + " | " + message, caller);
	}

	public void sysoStempPerformance(final double proportion, final Object caller) {
		sysoStempMessage(getStempPerformance(proportion), caller);
	}

	public void sysoStempPerformance(final double proportion, final String message, final Object caller) {
		sysoStempMessage(getStempPerformance(proportion) + " | " + message, caller);
	}

	public void sysoStempPerformance(final String message, final Object caller) {
		sysoStempMessage(getStempPerformance(message), caller);
	}

	// MESSAGE

	public void sysoStempMessage(final String message) {
		this.printLog(message, Level.INFO);
	}

	public void sysoStempMessage(final String message, final Level level) {
		this.printLog(message, level);
	}

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

	public void sysoStempMessage(final String message, final Object... fillers) {
		// logger.printf(level, message, fillers);
		DEBUG.OUT(level + "->" + message);
	}

	// OBJECTIF PART (to compute advancement toward a goal)

	public void setObjectif(final double objectif) { this.objectif = objectif; }

	public double getObjectif() { return objectif; }

	public void resetStemp() {
		this.resetStempCalls();
		firstSyso = true;
		performanceTestDescription = "no reason";
	}

	public void resetStempCalls() {
		stempCalls = 0;
		latestStemp = 0l;
		cumulStemp = 0d;
	}

	// LOGGER PART

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
