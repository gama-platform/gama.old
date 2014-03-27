package msi.gama.headless.core;

import java.util.Calendar;
import java.util.Vector;

import msi.gama.headless.common.ISimulator;
import msi.gama.headless.runtime.GamaSimulator;
import msi.gama.headless.xml.Writer;

public class Simulation {
	/**
	 * Variable listeners
	 */
	private String[] listenedVariable;
	private Object [] results;
	private Integer [] listenedVariableFrameRate;
	private Vector<Parameter> parameters;
	private Vector<Output> outputs;
	private Writer outputFile;
	private final String sourcePath;
	//private String driver;
	private final String experimentName;

	/**
	 * simulator to be loaded
	 */
	public ISimulator simulator;

	/**
	 * current step
	 */
	private int step;
	
	/**
	 * id of current experiment
	 */
	private int experimentID;
	public int maxStep;
	
	public void setBufferedWriter(Writer w)
	{
		this.outputFile=w;
	}
	
	public void addParameter(Parameter p)
	{
		this.parameters.add(p);
	}

	public void addOutput(Output p)
	{
		this.outputs.add(p);
	}

	public Simulation(final Simulation clone) {
		this.experimentID = clone.experimentID;
		this.sourcePath = clone.sourcePath;
		this.maxStep = clone.maxStep;
		this.experimentName = clone.experimentName;
		this.parameters = clone.parameters;
		this.listenedVariable = clone.listenedVariable;
		this.listenedVariableFrameRate = clone.listenedVariableFrameRate;
		this.setStep(clone.getStep());
		this.outputs = clone.outputs;
	}

	public Simulation(int expId, String sourcePath, String exp, int max) {
		this.experimentID = expId;
		this.sourcePath = sourcePath;
		this.maxStep = max;
		this.experimentName = exp;
		initialize();
	}
	
	public void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
		
		this.load();
		this.listenedVariable=new String[outputs.size()];
		this.listenedVariableFrameRate=new Integer[outputs.size()];
		this.results=new Object[outputs.size()];
	
		
		for(int i=0; i<parameters.size();i++)
		{
			Parameter temp=parameters.get(i);
			this.simulator.setParameterWithName(temp.getName(), temp.getValue());
			System.out.println("parameter setup : "+ temp.getName()+" "+temp.getValue());
		}
		for(int i=0; i<outputs.size();i++)
		{
			Output temp=outputs.get(i);
			this.listenedVariable[i]=temp.getName();
			this.listenedVariableFrameRate[i]=temp.getFrameRate();
		}
		this.setup();
		simulator.initialize();
	}

	public void load() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		System.setProperty("user.dir",this.sourcePath);
		this.simulator=new GamaSimulator(); 
		this.simulator.load(this.sourcePath, this.experimentID, this.experimentName);
	}
	
	public void setup()
	{
		this.step=0;
	}

	public void play() {
		if (this.outputFile != null)
			this.outputFile.writeSimulationHeader(this);
		System.out.println("Simulation is running...");
		long startdate = Calendar.getInstance().getTimeInMillis();
		int affDelay = maxStep < 100 ? 1 : maxStep /100;
		for(;step<maxStep;step++)
		{
			if(step%affDelay == 0)
				System.out.print(".");
			nextStepDone();
		}
		long endDate= Calendar.getInstance().getTimeInMillis();
		this.simulator.free();
		if(this.outputFile!=null)
			this.outputFile.close();
		System.out.println("\nSimulation duration: "+ (endDate - startdate)+"ms");
	}
	
	public void nextStepDone()
	{
		simulator.nextStep(this.step);
		this.exportData();
	}
	
	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}
	private void exportVariable()
	{
		
		int size=this.listenedVariable.length;
		
		for(int i=0;i<size;i++)
		{
			int frameRate=this.listenedVariableFrameRate[i].intValue();
			if((this.step%frameRate)==0)
			{
				String vars=this.listenedVariable[i];
				Object obj=this.simulator.getVariableWithName(vars);
				this.results[i]=obj;
			} 
		}
		if(this.outputFile!=null)
			this.outputFile.writeResultStep(this.step,this.listenedVariable,this.results);
		
	}
		
	/**
	 * Export simulation data to database...
	 */
	private void exportData()
	{
		exportVariable();
	}
	
	public void initialize()
	{
		parameters=new Vector<Parameter>();
		outputs=new Vector<Output>();
				
		if(simulator!=null)
		{
			simulator.free();
			simulator=null;
		}

	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

}
