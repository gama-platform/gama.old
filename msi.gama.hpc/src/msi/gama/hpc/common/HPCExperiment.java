package msi.gama.hpc.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Vector;

public class HPCExperiment {
	private Vector<Parameter> parameters;
	private Vector<Output> outputs;
	private int state;
	private int id;
	private String modelPath;
	private int finalStep;
	
	public HPCExperiment(int id, String mod, int finalStep)
	{
		this.parameters = new Vector<Parameter>();
		this.outputs = new Vector<Output>();
		this.id = id;
		this.modelPath = mod;
		this.finalStep = finalStep;
	}
	
	public int getState()
	{
		return state;
	}

	public void addParameter(Parameter p)
	{
		this.parameters.add(p);
	}

	public void addOutput(Output p)
	{
		this.outputs.add(p);
	}
	
	public static File produceXML(HPCExperiment hp, String fileName)
	{
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Simulation id=\""+hp.id+"\" \n" +
				" driver=\"msi.gama.headless.runtime.GamaSimulator\" \n" +
				" sourcePath=\""+hp.modelPath + "\" " +
				" finalstep=\""+ hp.finalStep +"\"> \n" +
				"<Parameters >\n";
		
		for(int i=0; i<hp.parameters.size();i++)
		{
			Parameter pp = hp.parameters.get(i);
			chaine = chaine + "<Parameter name=\""+ pp.getName()+"\" type=\""+Parameter.castType(pp.getValue())+"\" value=\""+pp.getValue().toString() +"\" />\n";
		}
		
		chaine = chaine + "</Parameters><Outputs>";
		
				
		for(int j=0; j<hp.outputs.size();j++)
		{
			Output o = hp.outputs.get(j);
			chaine = chaine + "<Output id=\""+j+"\" name=\""+ o.getName()+"\"  framerate=\""+o.getFrameRate() +"\" />";
		}
		chaine = chaine + "</Outputs></Simulation>";
		
		try {
			File outf = new File(fileName);
			FileOutputStream out = new FileOutputStream(outf);
			out.write(chaine.getBytes(),0,chaine.length());
			return outf;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}
