/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IExperimentPlan.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.Map;

import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.FileOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 31 mai 2011
 *
 * @todo Description
 *
 */
public interface IExperimentPlan extends ISpecies {

	String BATCH_CATEGORY_NAME = "Exploration method";
	String TEST_CATEGORY_NAME = "Configuration of tests";
	String EXPLORABLE_CATEGORY_NAME = "Parameters to explore";
	String SYSTEM_CATEGORY_PREFIX = "Random number generation";

	IModel getModel();

	void setModel(final IModel model);

	IOutputManager getOriginalSimulationOutputs();

	void refreshAllOutputs();

	void pauseAllOutputs();

	void resumeAllOutputs();

	void synchronizeAllOutputs();

	void unSynchronizeAllOutputs();

	void closeAllOutputs();

	IOutputManager getExperimentOutputs();

	boolean isGui();

	boolean hasParameter(String name);

	ExperimentAgent getAgent();

	IScope getExperimentScope();

	void open();

	void reload();

	SimulationAgent getCurrentSimulation();

	Map<String, IParameter> getParameters();

	IExploration getExplorationAlgorithm();

	FileOutput getLog();

	boolean isBatch();

	boolean isMemorize();

	Map<String, Batch> getExplorableParameters();

	IExperimentController getController();

	/**
	 * @return
	 */
	boolean isHeadless();

	void setHeadless(boolean headless);

	String getExperimentType();

	boolean keepsSeed();

	boolean keepsSimulations();

	boolean hasParametersOrUserCommands();

	void recomputeAndRefreshAllOutputs();

	Iterable<IOutputManager> getActiveOutputManagers();

	boolean isAutorun();

	boolean isTest();

	@Override
	ExperimentDescription getDescription();

	boolean shouldBeBenchmarked();

}