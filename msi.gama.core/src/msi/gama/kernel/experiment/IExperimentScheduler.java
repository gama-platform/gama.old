/*******************************************************************************************************
 *
 * IExperimentScheduler.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.IScope;

/**
 * The Interface IExperimentScheduler.
 */
public interface IExperimentScheduler {

	/**
	 * Start.
	 */
	void start();

	/**
	 * Pause.
	 */
	default void pause() {}

	/**
	 * Unpause.
	 */
	default void resume() {}

	/**
	 * Step by step.
	 */
	default void stepByStep() {}

	/**
	 * Step back.
	 */
	default void stepBack() {}

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	default boolean paused() {
		return false;
	}

	/**
	 * Schedule.
	 *
	 * @param experimentAgent
	 *            the experiment agent
	 * @param ownScope
	 *            the own scope
	 */
	void schedule(IStepable experimentAgent, IScope ownScope);

}