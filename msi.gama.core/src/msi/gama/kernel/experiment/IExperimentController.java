/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IExperimentController.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	int _OPEN = 0;
	int _START = 1;
	int _STEP = 2;
	int _PAUSE = 3;
	int _RELOAD = 6;
	int _BACK = 8;

	/**
	 * @return
	 */
	IExperimentPlan getExperiment();

	/**
	 * @return
	 */
	ExperimentScheduler getScheduler();

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

	boolean isDisposing();

}