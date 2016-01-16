package msi.gama.headless.runtime;

import msi.gama.headless.core.Simulation;

public interface SimulationRuntime {
	public int UNDEFINED_QUEUE_SIZE = -1;
	
	public void pushSimulation(Simulation s);
	public void closeSimulation(FakeApplication s);
	public SimulationState getSimulationState(String id);
	public boolean isPerformingSimulation();
	
}
