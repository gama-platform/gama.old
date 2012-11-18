package msi.gama.hpc.runtime;

import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import msi.gama.hpc.common.HPCExperiment;

public class MulticoreScheduler implements Observer {
	private  Vector<MulticoreRuntime> tasks ;
	private  Vector<MulticoreRuntime> running;
	private int availableCore;
	
	private String inputfileDirectory;
	private String outputfileDirectory;
	
	private static long RUNTIME_ID = 0;
	
	public MulticoreScheduler(int coreNumber, String inputfileDirectory , String outputfileDirectory)
	{
		running = new  Vector<MulticoreRuntime>();
		this.inputfileDirectory = inputfileDirectory;
		this.outputfileDirectory =  outputfileDirectory;
		
		this.tasks= new  Vector<MulticoreRuntime>();
		this.availableCore = coreNumber;
	}
	
	public MulticoreRuntime push(HPCExperiment exp)
	{
		long expId = RUNTIME_ID++;
		String localInputFile = inputfileDirectory + "/"+exp+"_"+ expId+ ".xml";
		String localOutputFile = outputfileDirectory + "/"+exp+"_"+ expId;
		
		
		MulticoreRuntime ret = new MulticoreRuntime(exp, inputfileDirectory,outputfileDirectory);
		this.tasks.add(ret);
		
		//// Generate Multicore
		return ret;
	}

	public void update(Observable o, Object arg) {
		for (int i=0 ; i < running.size(); i++)
		{
			if(running.get(i).getState()== MulticoreRuntime.FINISHED)
			{
				System.out.println("TaskFinished " + running.get(i) ) ;
				running.remove(i);
				i--;
			}
		}
	}
	
	private void submitNewtaskToProcessor()
	{
		if(tasks.size()>0 && running.size() < availableCore)
		{
			MulticoreRuntime one = tasks.firstElement();
			MulticoreFactory.submitOneExperiment( one);
			tasks.remove(0);
			running.add(one);
		}
	}
}
