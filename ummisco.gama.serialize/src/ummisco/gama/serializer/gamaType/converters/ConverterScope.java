package ummisco.gama.serializer.gamaType.converters;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;

public class ConverterScope {
	SimulationAgent simAgt;
	IScope scope;
	
	public ConverterScope(IScope s){
		scope = s;
		simAgt=null;
	}

	public IScope getScope() { return scope; }
	public SimulationAgent getSimulationAgent() { return simAgt; }
	public void setSimulationAgent(SimulationAgent sim){ simAgt = sim;}
}
