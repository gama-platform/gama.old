/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IExperimentAgent.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;

public interface IExperimentAgent extends ITopLevelAgent {

	@Override
	public abstract IExperimentPlan getSpecies();

	public String getWorkingPath();

	public List<String> getWorkingPaths();

	public abstract Boolean getWarningsAsErrors();

	public abstract Double getMinimumDuration();

	public abstract void setMinimumDuration(Double d);

	void closeSimulations();

	public abstract void closeSimulation(SimulationAgent simulationAgent);

	public abstract SimulationPopulation getSimulationPopulation();

	public boolean isMemorize();

	public boolean canStepBack();

	public abstract void informStatus();

	public abstract boolean isHeadless();

}
