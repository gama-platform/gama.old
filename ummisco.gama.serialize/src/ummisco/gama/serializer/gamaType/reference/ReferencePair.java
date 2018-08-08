package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaPair;
import msi.gama.util.IReference;
import msi.gaml.types.Types;
import ummisco.gama.serializer.gamaType.reduced.GamaPairReducer;

public class ReferencePair extends GamaPair implements IReference {

	ArrayList<AgentAttribute> agtAttr;
	
	GamaPairReducer pairReducer;

	public ReferencePair(GamaPairReducer p) {
		super(null, null, Types.NO_TYPE, Types.NO_TYPE);
		agtAttr = new ArrayList<AgentAttribute>();
		pairReducer = p;
	}	
	
	public GamaPairReducer getPairReducer() {return pairReducer;}
		
	@Override
	public Object constructReferencedObject(SimulationAgent sim) {
		pairReducer.unreferenceReducer(sim);
		return pairReducer.constructObject();
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
