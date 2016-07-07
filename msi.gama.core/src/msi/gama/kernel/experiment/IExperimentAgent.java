/*********************************************************************************************
 *
 *
 * 'IExperimentAgent.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;

public interface IExperimentAgent extends ITopLevelAgent {

	@Override
	public abstract IExperimentPlan getSpecies();

	public String getWorkingPath();

	public List<String> getWorkingPaths();

	/**
	 * @return
	 */
	public abstract Boolean getWarningsAsErrors();

	/**
	 * @return
	 */
	public abstract Double getMinimumDuration();

	/**
	 * @param d
	 */
	public abstract void setMinimumDuration(Double d);

	void closeSimulations();

	public abstract void closeSimulation(SimulationAgent simulationAgent);

	/**
	 * @return
	 */
	public abstract SimulationPopulation getSimulationPopulation();

	public boolean isMemorize();

	public boolean canStepBack();

	public abstract void informStatus();

}
