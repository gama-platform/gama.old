package msi.gama.hpc.common;

import java.util.Observable;
import java.util.Vector;

import msi.gama.headless.core.Output;
import msi.gama.headless.core.Parameter;

public class HPCExperiment extends Observable {
	private Vector<Parameter> parameters;
	private Vector<Output> outputs;
	private int state;
	
	public HPCExperiment()
	{
		this.parameters = new Vector<Parameter>();
		this.outputs = new Vector<Output>();
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

}
