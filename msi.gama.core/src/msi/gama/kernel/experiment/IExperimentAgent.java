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

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;

public interface IExperimentAgent extends IAgent {

	@Override
	public abstract IExperimentPlan getSpecies();

	public abstract RandomUtils getRandomGenerator();

	public abstract void closeSimulation();

	public String getWorkingPath();

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

}
