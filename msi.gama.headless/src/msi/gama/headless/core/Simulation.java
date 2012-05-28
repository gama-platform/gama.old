package msi.gama.headless.core;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import msi.gama.headless.common.ISimulator;
import msi.gama.headless.xml.Writer;


public class Simulation  {
	/**
	 * Variable listeners
	 */
	private String[] listenedVariable;
	private Object [] results;
	private Integer [] listenedVariableFrameRate;
	private Vector<Parameter> parameters;
	private Vector<Output> outputs;
	private Writer outputFile;
	private String sourcePath;
	private String driver;

	/**
	 * simulator to be loaded
	 */
	private ISimulator model;
	
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

	public Simulation(int expId, String sourcePath, String driver, int max)
	{
		this.experimentID=expId;
		this.sourcePath=sourcePath;
		this.driver=driver;
		this.maxStep=max;
		initialize();
	}
	
	public void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
		this.load();
		this.listenedVariable=new String[outputs.size()];
		this.listenedVariableFrameRate=new Integer[outputs.size()];
		this.results=new Object[outputs.size()];
		System.out.println("out "+outputs.size());
		System.out.println("input "+parameters.size());
		
		for(int i=0; i<parameters.size();i++)
		{
			Parameter temp=parameters.get(i);
			this.model.setParameterWithName(temp.getName(), temp.getValue());
			System.out.println("initialisation variable : "+ temp.getName()+" "+temp.getValue());
		}
		for(int i=0; i<outputs.size();i++)
		{
			Output temp=outputs.get(i);
			this.listenedVariable[i]=temp.getName();
			this.listenedVariableFrameRate[i]=temp.getFrameRate();
//			this.addProbe(temp.getName(), temp.getFrameRate());
		}
	//	this.outputFile.writeSimulationHeader(this);
		this.setup();
	}

	public void load() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		this.model=(ISimulator)(Class.forName(this.driver)).newInstance();
		this.model.load(this.sourcePath, this.experimentID);

	}
	
	public void setup()
	{
		this.step=0;
		//this.model.setup();
	}
	
	public void play()
	{
		this.model.initialize();
		if(this.outputFile!=null)
			this.outputFile.writeSimulationHeader(this);
		long startdate = Calendar.getInstance().getTimeInMillis();
		for(;step<maxStep;step++)
			nextStepDone();
		long endDate= Calendar.getInstance().getTimeInMillis();
		this.model.free();
		if(this.outputFile!=null)
			this.outputFile.close();
		System.out.println("Simulation duration: "+ (endDate - startdate)+"ms");
	}
	
	public void nextStepDone()
	{
		model.nextStep(this.step);
		this.exportData();
	}
	
	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}

	/**
	 * add a new probe to the simulation instance
	 * 
	 */
		
		private void exportVariable()
		{
			
			int size=this.listenedVariable.length;
			
			for(int i=0;i<size;i++)
			{
				int frameRate=this.listenedVariableFrameRate[i].intValue();
				if((this.step%frameRate)==0)
				{
					String vars=this.listenedVariable[i];
					Object obj=this.model.getVariableWithName(vars);
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
					
			if(model!=null)
			{
				model.free();
				model=null;
			}

		}


}
