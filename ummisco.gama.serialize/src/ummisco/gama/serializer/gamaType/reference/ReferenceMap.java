/*******************************************************************************************************
 *
 * ReferenceMap.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaMap;
import msi.gama.util.IReference;
import ummisco.gama.serializer.gamaType.reduced.GamaMapReducer;

/**
 * The Class ReferenceMap.
 */
public class ReferenceMap extends GamaMap implements IReference {

	/** The agt attr. */
	ArrayList<AgentAttribute> agtAttr;

	/** The map reducer. */
	GamaMapReducer mapReducer;

	/**
	 * Instantiates a new reference map.
	 *
	 * @param m the m
	 */
	public ReferenceMap(final GamaMapReducer m) {
		super(m.getValues().size(), m.getKeysType(), m.getDataType());
		agtAttr = new ArrayList<>();
		mapReducer = m;
	}

	@Override
	public Object constructReferencedObject(final SimulationAgent sim) {
		mapReducer.unreferenceReducer(sim);
		return mapReducer.constructObject(sim.getScope());
	}

	@Override
	public ArrayList<AgentAttribute> getAgentAttributes() {
		return agtAttr;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else {
			return false;
		}
	}
}
