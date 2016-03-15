package msi.gama.headless.runtime;

import java.util.HashMap;

import msi.gama.headless.job.ExperimentJob;

public interface SimulationRuntime {
	public int UNDEFINED_QUEUE_SIZE = -1;
	
	public void pushSimulation(ExperimentJob s);
	public boolean isTraceKept();
	public void keepTrace(boolean t);
	//public void closeSimulation(ExperimentJob s);
	public SimulationState getSimulationState(String id);
	public HashMap<String, Double> getSimulationState();
	public boolean isPerformingSimulation();
	
}
