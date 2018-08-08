package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.IReference;
import msi.gama.util.path.GamaPath;
import ummisco.gama.serializer.gamaType.reduced.GamaPathReducer;

public class ReferencePath extends GamaPath implements IReference {

	ArrayList<AgentAttribute> agtAttr;
	
	GamaPathReducer pathReducer;

	public ReferencePath(GamaPathReducer p) {
		super();
		agtAttr = new ArrayList<AgentAttribute>();		
		pathReducer = p;
	}	

	@Override
	public Object constructReferencedObject(SimulationAgent sim) {
		pathReducer.unreferenceReducer(sim);
		return pathReducer.constructObject(sim.getScope());
	}	
	
	@Override
	public ArrayList<AgentAttribute> getAgentAttributes() {
		return agtAttr;
	}	

    public boolean equals(Object o) {
        if (o == this)
            return true;
        else
        	return false;
    }
}
