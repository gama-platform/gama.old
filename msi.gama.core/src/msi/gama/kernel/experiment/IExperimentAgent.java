package msi.gama.kernel.experiment;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;

public interface IExperimentAgent extends IAgent {

	// public abstract boolean isLoading();

	public abstract RandomUtils getRandomGenerator();

	public abstract void closeSimulation();

}
