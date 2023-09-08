/*******************************************************************************************************
 *
 * ReferencePair.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaPair;
import msi.gama.util.IReference;
import msi.gaml.types.Types;
import ummisco.gama.serializer.gamaType.reduced.GamaPairReducer;

/**
 * The Class ReferencePair.
 */
public class ReferencePair extends GamaPair<Object, Object> implements IReference {

	/** The agt attr. */
	ArrayList<AgentAttribute> agtAttr;

	/** The pair reducer. */
	GamaPairReducer pairReducer;

	/**
	 * Instantiates a new reference pair.
	 *
	 * @param p the p
	 */
	public ReferencePair(final GamaPairReducer p) {
		super(null, null, Types.NO_TYPE, Types.NO_TYPE);
		agtAttr = new ArrayList<>();
		pairReducer = p;
	}

	/**
	 * Gets the pair reducer.
	 *
	 * @return the pair reducer
	 */
	public GamaPairReducer getPairReducer() {
		return pairReducer;
	}

	@Override
	public Object constructReferencedObject(final SimulationAgent sim) {
		pairReducer.unreferenceReducer(sim);
		return pairReducer.constructObject();
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
