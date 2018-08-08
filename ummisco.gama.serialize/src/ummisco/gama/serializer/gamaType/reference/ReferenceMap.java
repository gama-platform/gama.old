package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaMap;
import msi.gama.util.IReference;
import ummisco.gama.serializer.gamaType.reduced.GamaMapReducer;

public class ReferenceMap extends GamaMap implements IReference {

	ArrayList<AgentAttribute> agtAttr;
	
	GamaMapReducer mapReducer;

	public ReferenceMap(GamaMapReducer m) {
		super(m.getValues().size(), m.getKeysType(), m.getDataType());
		agtAttr = new ArrayList<AgentAttribute>();		
		mapReducer = m;
	}	

	@Override
	public Object constructReferencedObject(SimulationAgent sim) {	
		mapReducer.unreferenceReducer(sim);	
		return mapReducer.constructObject(sim.getScope());
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
