/*******************************************************************************************************
 *
 * IExperimentController.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

/**
 * Class IExperimentController.
 *
 * @author drogoul
 * @since 6 déc. 2015
 *
 */
public interface IExperimentController {

	/** The open. */
	int _OPEN = 0;

	/** The start. */
	int _START = 1;

	/** The step. */
	int _STEP = 2;

	/** The pause. */
	int _PAUSE = 3;

	/** The reload. */
	int _RELOAD = 6;

	/** The back. */
	int _BACK = 8;

	/**
	 * @return
	 */
	IExperimentPlan getExperiment();

	/**
	 * @return
	 */
	IExperimentScheduler getScheduler();

	/**
	 *
	 */
	void userStep();

	/**
	 *
	 */
	void stepBack();

	/**
	 *
	 */
	void startPause();

	/**
	 *
	 */
	void close();

	/**
	 *
	 */
	void userStart();

	/**
	 *
	 */
	void directPause();

	/**
	 *
	 */
	void dispose();

	/**
	 *
	 */
	void directOpenExperiment();

	/**
	 *
	 */
	void userReload();

	/**
	 *
	 */
	void userPause();

	/**
	 *
	 */
	void userOpen();

	/**
	 * Checks if is disposing.
	 *
	 * @return true, if is disposing
	 */
	boolean isDisposing();

}