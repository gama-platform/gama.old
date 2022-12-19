/*******************************************************************************************************
 *
 * GamaPathReducer.java, in ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.IReference;
import msi.gama.util.graph.IGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.PathFactory;
import ummisco.gama.serializer.gamaType.reference.ReferencePath;

/**
 * The Class GamaPathReducer.
 */
public class GamaPathReducer {

	/** The g. */
	IGraph<Object, Object> g;
	
	/** The start. */
	Object start;
	
	/** The target. */
	Object target;
	
	/** The edges. */
	IList<Object> edges;

	/**
	 * Instantiates a new gama path reducer.
	 *
	 * @param p the p
	 */
	public GamaPathReducer(final GamaPath p) {
		g = p.getGraph();
		start = p.getStartVertex();
		target = p.getEndVertex();
		edges = p.getEdgeList();
	}

	/**
	 * Unreference reducer.
	 *
	 * @param sim the sim
	 */
	@SuppressWarnings ("unchecked")
	public void unreferenceReducer(final SimulationAgent sim) {
		g = (IGraph) IReference.getObjectWithoutReference(g, sim);
		start = IReference.getObjectWithoutReference(start, sim);
		target = IReference.getObjectWithoutReference(target, sim);
		edges = (IList) IReference.getObjectWithoutReference(edges, sim);
	}

	/**
	 * Construct object.
	 *
	 * @param scope the scope
	 * @return the gama path
	 */
	public GamaPath constructObject(final IScope scope) {

		GamaPath path = null;
		if (IReference.isReference(g) || IReference.isReference(start) || IReference.isReference(target)
				|| IReference.isReference(edges)) {
			path = new ReferencePath(this);
		} else {
			path = PathFactory.newInstance(g, start, target, edges);
		}
		return path;
	}
}
