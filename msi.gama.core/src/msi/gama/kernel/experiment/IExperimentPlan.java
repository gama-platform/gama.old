/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IExperimentPlan.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
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

	static final String BATCH_CATEGORY_NAME = "Exploration method";
	static final String TEST_CATEGORY_NAME = "Configuration of tests";
	static final String EXPLORABLE_CATEGORY_NAME = "Parameters to explore";
	static final String FIXED_CATEGORY_NAME = "Fixed parameters";
	static final String SYSTEM_CATEGORY_PREFIX = "Random number generation";

	public abstract IModel getModel();

	public abstract void setModel(final IModel model);

	public abstract IOutputManager getOriginalSimulationOutputs();

	public abstract void refreshAllOutputs();

	public abstract void pauseAllOutputs();

	public abstract void resumeAllOutputs();

	public abstract void synchronizeAllOutputs();

	public abstract void unSynchronizeAllOutputs();

	public abstract void closeAllOutputs();

	public abstract IOutputManager getExperimentOutputs();

	public abstract boolean isGui();

	public abstract boolean hasParameter(String name);

	public ExperimentAgent getAgent();

	public abstract IScope getExperimentScope();

	public abstract void open();

	public abstract void reload();

	public abstract SimulationAgent getCurrentSimulation();

	public abstract Map<String, IParameter> getParameters();

	public abstract IExploration getExplorationAlgorithm();

	public FileOutput getLog();

	public abstract boolean isBatch();

	public abstract boolean isMemorize();

	public abstract Map<String, Batch> getExplorableParameters();

	public abstract IExperimentController getController();

	/**
	 * @return
	 */
	public abstract boolean isHeadless();

	public abstract void setHeadless(boolean headless);

	public abstract String getExperimentType();

	public abstract boolean keepsSeed();

	public abstract boolean keepsSimulations();

	public abstract boolean hasParametersOrUserCommands();

	public abstract void recomputeAndRefreshAllOutputs();

	public abstract Iterable<IOutputManager> getActiveOutputManagers();

	public abstract boolean isAutorun();

	public abstract boolean isTest();

	@Override
	public ExperimentDescription getDescription();

	public abstract boolean shouldBeBenchmarked();

}