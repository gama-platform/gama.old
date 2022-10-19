/*******************************************************************************************************
 *
 * ReferenceList.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaList;
import msi.gama.util.IReference;
import ummisco.gama.serializer.gamaType.reduced.GamaListReducer;

/**
 * The Class ReferenceList.
 */
public class ReferenceList extends GamaList<Object> implements IReference {

	/** The agt attr. */
	ArrayList<AgentAttribute> agtAttr;

	/** The list reducer. */
	GamaListReducer listReducer;

	/**
	 * Instantiates a new reference list.
	 *
	 * @param l
	 *            the l
	 */
	public ReferenceList(final GamaListReducer l) {
		super(l.getValuesListReducer().size(), l.getContentTypeListReducer());
		addAll(l.getValuesListReducer());
		agtAttr = new ArrayList<>();
		listReducer = l;
	}

	@Override
	public Object constructReferencedObject(final SimulationAgent sim) {

		listReducer.unreferenceReducer(sim);
		return listReducer.constructObject(sim.getScope());
	}

	@Override
	public ArrayList<AgentAttribute> getAgentAttributes() { return agtAttr; }

	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		return false;
	}
}
