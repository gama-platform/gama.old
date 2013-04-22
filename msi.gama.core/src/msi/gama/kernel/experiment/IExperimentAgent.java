package msi.gama.kernel.experiment;

import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

public interface IExperimentAgent extends IAgent {

	public abstract boolean isRunning();

	public abstract boolean isLoading();

	public abstract boolean isPaused();

	public abstract RandomUtils getRandomGenerator();

	public abstract IScope getExecutionScope();

	public abstract void releaseScope(IScope scope);

	public abstract IScope obtainNewScope();

	public abstract void userPauseExperiment();

	public void userStopExperiment();

	public void userStepExperiment();

	public abstract void startSimulation();

}
