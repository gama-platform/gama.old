/*******************************************************************************************************
 *
 * SimulationRuntime.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import msi.gama.headless.job.ExperimentJob;

/**
 * The Interface SimulationRuntime.
 */
public interface SimulationRuntime {
	
	/** The undefined queue size. */
	int UNDEFINED_QUEUE_SIZE = Integer.MAX_VALUE;

	/**
	 * Push simulation.
	 *
	 * @param s the s
	 */
	void pushSimulation(ExperimentJob s);

	/**
	 * Checks if is performing simulation.
	 *
	 * @return true, if is performing simulation
	 */
	boolean isPerformingSimulation();

}

// OLD

// boolean isTraceKept();

// void keepTrace(boolean t);

// public void closeSimulation(ExperimentJob s);
// public SimulationState getSimulationState(String id);

// HashMap<String, Double> getSimulationState();