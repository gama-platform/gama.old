/*******************************************************************************************************
 *
 * ReferencePath.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.IReference;
import msi.gama.util.path.GamaPath;
import ummisco.gama.serializer.gamaType.reduced.GamaPathReducer;

/**
 * The Class ReferencePath.
 */
public class ReferencePath extends GamaPath implements IReference {

	/** The agt attr. */
	ArrayList<AgentAttribute> agtAttr;
	
	/** The path reducer. */
	GamaPathReducer pathReducer;

	/**
	 * Instantiates a new reference path.
	 *
	 * @param p the p
	 */
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
