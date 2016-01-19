package msi.gama.headless.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.core.IExperiment;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.IDescription;

public class LocalSimulationRuntime extends Observable implements SimulationRuntime, RuntimeContext {
	private Map<String, ExperimentJob> simulations;
	private ArrayList<FakeApplication> queue ;
	private ArrayList<FakeApplication> started ;
	private HashMap<String,IModel> loadedModels;
	private int allocatedProcessor ;
	private boolean isTraceKept;
	
	public boolean isTraceKept()
	{
		return this.isTraceKept;
	}
	public void keepTrace(boolean t)
	{
		this.isTraceKept=t;
	}
	
	
	
	public int getAllocatedProcessor() {
		return allocatedProcessor;
	}

	public void setAllocatedProcessor(int allocatedProcessor) {
		this.allocatedProcessor = allocatedProcessor;
	}
	
	public LocalSimulationRuntime(int proc) {
		simulations = new HashMap<String, ExperimentJob>();
		queue = new ArrayList<FakeApplication>();
		started = new ArrayList<FakeApplication>();
		loadedModels = new HashMap<String,IModel>();
		this.allocatedProcessor=proc;
	}
	
	public LocalSimulationRuntime() {
		simulations = new HashMap<String, ExperimentJob>();
		queue = new ArrayList<FakeApplication>();
		started = new ArrayList<FakeApplication>();
		loadedModels = new HashMap<String,IModel>();
		this.allocatedProcessor=UNDEFINED_QUEUE_SIZE;
	}
	
	public void listenMe(Observer v)
	{
		this.addObserver(v);
	}

	public void pushSimulation(ExperimentJob s) {
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
			FakeApplication p = queue.get(0);
			queue.remove(p);
			this.startSimulation(p);
		}
		if(!this.isTraceKept)
			simulations.remove(s.getExperimentJob().getExperimentID());
		this.notifyListener();
	}


	private void notifyListener()
	{
		this.setChanged();
		this.notifyObservers();
	}
	
	public SimulationState getSimulationState(String id) {
		ExperimentJob tmp = simulations.get(id);
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
	
	public synchronized IModel loadModel(File fl)
	{
		IModel mdl = HeadlessSimulationLoader.loadModel(fl);
		
		
		//IModel mdl = this.loadedModels.get(fl.getAbsolutePath());
//		
//		System.out.println("pouet  "+fl.getAbsolutePath());
//		
//		if(mdl == null)
//		{
//			this.loadedModels.put(fl.getAbsolutePath(), mdl);
//		}
		return mdl;
	}

	public IExperimentPlan buildExperimentPlan(String expName, IModel mdl)
	{
		IDescription des = mdl.getExperiment(expName).getDescription();
		IExperimentPlan expp= new ExperimentPlan(des);
		expp.setModel(mdl);
		return expp;
	}
	
	class FakeApplication extends Thread {// implements Runnable {

		private ExperimentJob si = null;
		private LocalSimulationRuntime runtime = null;

		ExperimentJob getExperimentJob()
		{
			return si;
		}
		public FakeApplication(final ExperimentJob sim, final LocalSimulationRuntime rn) {
			si = sim;
			this.runtime= rn;
		}
		@Override
		public void run() {
			try {
				si.loadAndBuild(this.runtime);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			si.play(); 
			runtime.closeSimulation(this);  
		}

	}


	
}
