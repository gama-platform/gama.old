package msi.gama.headless.runtime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.IDescription;

public class LocalSimulationRuntime extends Observable implements SimulationRuntime, RuntimeContext {
	private Map<String, ExperimentJob> simulations;
	private ArrayList<FakeApplication> queue ;
	private ArrayList<FakeApplication> started ;
	private HashMap<String,ArrayList<IModel> >loadedModels;
	private HashMap<String,ArrayList<IModel>> availableLoadedModels;
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
		loadedModels = new HashMap<String,ArrayList<IModel>>();
		availableLoadedModels = new HashMap<String,ArrayList<IModel>>();
		if(proc == UNDEFINED_QUEUE_SIZE)
		{
			proc = getSatisfiedThreads();
		}
		this.allocatedProcessor=proc;
	}
	
	public LocalSimulationRuntime() {
		simulations = new HashMap<String, ExperimentJob>();
		queue = new ArrayList<FakeApplication>();
		started = new ArrayList<FakeApplication>();
		loadedModels = new HashMap<String,ArrayList<IModel>>();
		availableLoadedModels = new HashMap<String,ArrayList<IModel>>();
		this.allocatedProcessor=getSatisfiedThreads() ; //UNDEFINED_QUEUE_SIZE;
	}
	
	private static int getSatisfiedThreads() {
		int cpus = Runtime.getRuntime().availableProcessors();
		System.out.println("cpus :" + cpus);
		int maxThreads = cpus;
		maxThreads = (maxThreads > 0 ? maxThreads : 1);
		return maxThreads;
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
	
	public synchronized void releaseModel(String key, IModel mdl)
	{
		//System.out.println("release simulation");
		//String key = mdl.getFilePath();
		//availableLoadedModels.get(key).add(mdl);
		//System.out.println("remove " + mdl.getFilePath());
		//lockUnLock(null,key, mdl);
	//	System.out.println("model released ") ;
	}
	
	private synchronized IModel lockUnLock(File fl, String key, IModel mdl) throws IOException
	{
		IModel mm =null;
		if(mdl!=null)
		{
			availableLoadedModels.get(key).add(mdl);
			mm= mdl;
		}
		if(fl != null)
		{
			mm= lockModel(fl);
		}
		return mm;
	}
	
	public synchronized IModel lockModel(File fl) throws IOException
	{
		IModel mdl;
		String key=fl.getAbsolutePath();
		ArrayList<IModel> arr = availableLoadedModels.get(fl.getAbsolutePath());
		System.out.println(fl.getAbsolutePath());
		if(arr ==null)
		{
			arr = new ArrayList<IModel>();
			availableLoadedModels.put(key, arr);
			loadedModels.put(key, new ArrayList<IModel>());
		}
		if(arr.size() == 0)
		{
			mdl =HeadlessSimulationLoader.loadModel(fl);
			loadedModels.get(key).add(mdl);
		}
		else
		{
			mdl = arr.get(0);
			arr.remove(0);
		}
		return mdl;
	}
	
	public synchronized IModel loadModel(File fl) throws IOException
	{
		//return lockUnLock( fl,null, null) ; //lockModel(fl); //
		return HeadlessSimulationLoader.loadModel(fl); //lockModel(fl); //mdl.c;
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
				catch (IOException e) {
					e.printStackTrace();
				}
			si.play(); 
			runtime.closeSimulation(this); 
			runtime.releaseModel(si.getSourcePath(),si.getSimulation().getModel());
		}

	}

	@Override
	public HashMap<String, Double> getSimulationState() {
		
		HashMap<String,Double > res = new HashMap<String,Double>();
		for(ExperimentJob exp :simulations.values())
		{
			res.put(exp.getExperimentID(), new Double(exp.getStep()/exp.getFinalStep()));
		}
		return res;
	}


	
}
