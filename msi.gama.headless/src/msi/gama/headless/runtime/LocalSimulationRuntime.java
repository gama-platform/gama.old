package msi.gama.headless.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;


import msi.gama.headless.core.Simulation;

public class LocalSimulationRuntime extends Observable implements SimulationRuntime {
	Map<String, Simulation> simulations;
	Vector<FakeApplication> queue ;
	Vector<FakeApplication> started ;
	private int allocatedProcessor ;
	
	public int getAllocatedProcessor() {
		return allocatedProcessor;
	}

	public void setAllocatedProcessor(int allocatedProcessor) {
		this.allocatedProcessor = allocatedProcessor;
	}
	
	public LocalSimulationRuntime(int proc) {
		simulations = new HashMap<String, Simulation>();
		queue = new Vector<FakeApplication>();
		started = new Vector<FakeApplication>();
		this.allocatedProcessor=proc;
	}
	
	public LocalSimulationRuntime() {
		simulations = new HashMap<String, Simulation>();
		queue = new Vector<FakeApplication>();
		started = new Vector<FakeApplication>();
		this.allocatedProcessor=UNDEFINED_QUEUE_SIZE;
	}
	
	public void listenMe(Observer v)
	{
		this.addObserver(v);
	}

	public void pushSimulation(Simulation s) {
		simulations.put(s.getExperimentID(), s);
		FakeApplication f = new FakeApplication(s,this);
		if(started.size() < allocatedProcessor || allocatedProcessor==UNDEFINED_QUEUE_SIZE)
			this.startSimulation(f);
		else
			queue.add(f);
	}

	private void startSimulation(FakeApplication s)
	{
		started.add(s);
		s.start();
		this.notifyListener();
	}
	
	public void closeSimulation(FakeApplication s)
	{
		started.remove(s);
		if(queue.size() > 0)
		{
			FakeApplication p = queue.firstElement();
			queue.remove(p);
			this.startSimulation(p);
		}
		this.notifyListener();
	}
	
	private void notifyListener()
	{
		this.setChanged();
		this.notifyObservers();
	}
	
	public SimulationState getSimulationState(String id) {
		Simulation tmp = simulations.get(id);
		if(tmp ==null)
			return SimulationState.UNDEFINED;
		if(started.contains(tmp))
			return SimulationState.STARTED;
		if(queue.contains(tmp))
			return SimulationState.ENQUEUED;
		return SimulationState.ACHIEVED;
	}
	public boolean isPerformingSimulation()
	{
		return started.size() > 0 || queue.size() > 0;
	}
}
