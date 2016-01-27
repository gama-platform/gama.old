/**
 * Created by drogoul, 27 janv. 2016
 *
 */
package msi.gama.metamodel.agent;

import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.*;

public class SimulationScope extends AbstractScope {

	volatile boolean interrupted = false;

	public SimulationScope(final ITopLevelAgent agent) {
		super(agent);
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
	public IScope copy() {
		return new SimulationScope(getRoot());
	}

	/**
	 * Method getRandom()
	 * @see msi.gama.runtime.IScope#getRandom()
	 */
	@Override
	public RandomUtils getRandom() {
		SimulationAgent a = this.getSimulationScope();
		return a == null ? null : a.getRandomGenerator();
	}

}