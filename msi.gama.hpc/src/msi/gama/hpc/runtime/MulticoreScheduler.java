package msi.gama.hpc.runtime;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import msi.gama.hpc.common.HPCExperiment;
import msi.gama.hpc.common.Output;
import msi.gama.hpc.common.Parameter;

public class MulticoreScheduler implements Observer {
	private  Vector<MulticoreRuntime> tasks ;
	private  Vector<MulticoreRuntime> running;
	public Vector<MulticoreRuntime> getTasks() {
		return tasks;
	}

	public void setTasks(Vector<MulticoreRuntime> tasks) {
		this.tasks = tasks;
	}

	public Vector<MulticoreRuntime> getRunning() {
		return running;
	}

	public void setRunning(Vector<MulticoreRuntime> running) {
		this.running = running;
	}

	private int availableCore;
	
	private String inputfileDirectory;
	private String outputfileDirectory;
	
	private static long RUNTIME_ID = 0;
	
	public MulticoreScheduler(int coreNumber, String inputfileDirectory , String outputfileDirectory)
	{
		MulticoreFactory.configure();
		MulticoreFactory.setPath("/Users/langthang/Desktop/MonGAMA/eclipse");
		running = new  Vector<MulticoreRuntime>();
		this.inputfileDirectory = inputfileDirectory;
		this.outputfileDirectory =  outputfileDirectory;
		
		this.tasks= new  Vector<MulticoreRuntime>();
		this.availableCore = coreNumber;
	}
	
	public MulticoreRuntime push(HPCExperiment exp)
	{
		long expId = RUNTIME_ID ++;
		String localInputFile = inputfileDirectory + "/experiment_in_"+ expId+ ".xml";
		String localOutputFile = outputfileDirectory + "/experiment_out_"+ expId;
		
		HPCExperiment.produceXML(exp,localInputFile);
		System.out.println( "Path "+ localInputFile+ "\n"+localOutputFile);
		MulticoreRuntime ret = new MulticoreRuntime(exp, localInputFile,localOutputFile);
		ret.addObserver(this);
		this.tasks.add(ret);
		submitNewtaskToProcessor();
		//// Generate Multicore
		return ret;
	}

	public void update(Observable o, Object arg) {
		for (int i=0 ; i < running.size(); i++)
		{
			if(running.get(i).getState() == MulticoreRuntime.FINISHED)
			{
				System.out.println("TaskFinished " + running.get(i) ) ;
				running.remove(i);
				i--;
				submitNewtaskToProcessor();
			}
		}
	}
	
	private void submitNewtaskToProcessor()
	{
		MulticoreRuntime one = null;
		if(tasks.size() > 0  && running.size() < availableCore)
		{
			one = tasks.firstElement();
			tasks.remove(0);
		}
		if(one != null )
		{
			MulticoreFactory.submitOneExperiment(one);
			running.add(one);
		}
	}
	
	private void shutdown()
	{
		MulticoreFactory.shutdown();
	}
	
	public static void main(String [] arg)
	{
		MulticoreScheduler mm = new MulticoreScheduler(4,"/Users/langthang/Desktop/MonGAMA/eclipse/tmp",
															"/Users/langthang/Desktop/MonGAMA/eclipse/tmp/output");
		HPCExperiment test = new HPCExperiment(12, "/Users/langthang/Desktop/MonGAMA/eclipse/samples/predatorPrey/predatorPrey.gaml", 100);
		
		test.addParameter(new Parameter("nb_preys_init", new Integer(12)));
//		test.addParameter(new Parameter("coucou1", new Integer(1)));
//		test.addOutput(new Output("toto", 1, "12"));
//		test.addOutput(new Output("titi", 3, "2"));
		test.addOutput(new Output("main_display", 1, "2"));
		test.addOutput(new Output("number_of_preys", 2, "1"));
		test.addOutput(new Output("number_of_predators", 3, "1"));
			
	//	HPCExperiment.produceXML(test,"/tmp/toto.xml");
		for(int i = 0; i < 13; i ++) {
			mm.push(test);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(mm.getTasks().size());
		}
		
		mm.shutdown();
	}
}
