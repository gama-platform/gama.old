/*******************************************************************************************************
 *
 * ISpatialGraph.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.graph;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;

/**
 * The class ISpatialGraph.
 *
 * @author drogoul
 * @since 3 f�vr. 2012
 *
 */
public interface ISpatialGraph extends IGraph<IShape, IShape>, IAgentFilter {

	/**
	 * Gets the topology.
	 *
	 * @param scope the scope
	 * @return the topology
	 */
	ITopology getTopology(IScope scope);

	/**
	 * Gets the vertices.
	 *
	 * @return the vertices
	 */
	@Override
	IList<IShape> getVertices();

	/**
	 * Gets the edges.
	 *
	 * @return the edges
	 */
	@Override
	IList<IShape> getEdges();

}
