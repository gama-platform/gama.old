/*******************************************************************************************************
 *
 * Operators.java, in simtools.gaml.extensions.traffic, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving;

import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gama.util.graph.IGraph;

/**
 * The Class Operators.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Operators {
	
	/**
	 * Spatial driving from edges.
	 *
	 * @param scope the scope
	 * @param edges the edges
	 * @param nodes the nodes
	 * @return the i graph
	 */
	@operator(
		value = "as_driving_graph",
		content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
		index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
		concept = { IConcept.GRAPH, IConcept.TRANSPORT }
	)
	@doc(
		value = "creates a graph from the list/map of edges given as operand and connect the node to the edge", 
		examples = {
			@example(
				value = "as_driving_graph(road, node)  --:  build a graph while using the road agents as edges and the node agents as nodes",
				isExecutable = false
			)
		},
		see = { "as_intersection_graph", "as_distance_graph", "as_edge_graph" }
	)
	@no_test
	public static IGraph spatialDrivingFromEdges(final IScope scope, final IContainer edges, final IContainer nodes) {
		return new DrivingGraph(edges, nodes, scope);
	}
}
