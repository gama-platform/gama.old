/**
 * Created by drogoul, 27 janv. 2016
 *
 */
package msi.gama.metamodel.agent;

import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.AbstractScope;
import msi.gama.runtime.IScope;

public class SimulationScope extends AbstractScope {

	volatile boolean interrupted = false;

	public SimulationScope(final ITopLevelAgent agent) {
		super(agent);
	}

	public SimulationScope(final ITopLevelAgent agent, final String additionalName) {
		super(agent, additionalName);
	}

	@Override
	protected boolean _root_interrupted() {
		return interrupted || getRoot().dead();
	}

	@Override
	public void setInterrupted(final boolean interrupted) {
		this.interrupted = true;
	}

	@Override
	public IScope copy(final String additionalName) {
		return new SimulationScope(getRoot(), additionalName);
	}

	/**
	 * Method getRandom()
	 * 
	 * @see msi.gama.runtime.IScope#getRandom()
	 */
	@Override
	public RandomUtils getRandom() {
		final SimulationAgent a = this.getSimulationScope();
		return a == null ? null : a.getRandomGenerator();
	}

}