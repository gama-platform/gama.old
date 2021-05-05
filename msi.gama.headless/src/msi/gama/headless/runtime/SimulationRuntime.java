package msi.gama.headless.runtime;

import msi.gama.headless.job.ExperimentJob;

public interface SimulationRuntime {
	int UNDEFINED_QUEUE_SIZE = Integer.MAX_VALUE;

	void pushSimulation(ExperimentJob s);

	// boolean isTraceKept();

	// void keepTrace(boolean t);

	// public void closeSimulation(ExperimentJob s);
	// public SimulationState getSimulationState(String id);

	// HashMap<String, Double> getSimulationState();

	boolean isPerformingSimulation();

}
