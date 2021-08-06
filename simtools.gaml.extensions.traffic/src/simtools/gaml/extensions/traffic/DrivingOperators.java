/*********************************************************************************************
 * 
 * 
 * 'DrivingOperators.java', in plugin 'simtools.gaml.extensions.traffic', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.OrderedBidiMap;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
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
import simtools.gaml.extensions.traffic.carfollowing.CustomDualTreeBidiMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DrivingOperators {

	@operator(value = "as_driving_graph", content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2, index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1, concept = {
			IConcept.GRAPH, IConcept.TRANSPORT })
	@doc(value = "creates a graph from the list/map of edges given as operand and connect the node to the edge", examples = {
			@example(value = "as_driving_graph(road,node)  --:  build a graph while using the road agents as edges and the node agents as nodes", isExecutable = false) }, see = {
					"as_intersection_graph", "as_distance_graph", "as_edge_graph" })
	@no_test
	public static IGraph spatialDrivingFromEdges(final IScope scope, final IContainer edges, final IContainer nodes) {
		final IGraph graph = new GamaSpatialGraph(edges, nodes, scope);
		for (final Object edge : edges.iterable(scope)) {
			if (edge instanceof IShape) {
				IAgent agent = ((IShape) edge).getAgent();
				int numLanes = RoadSkill.getNumLanes(agent);
				List<OrderedBidiMap<IAgent, Double>> res = new LinkedList<>();
				//TODO: this should be tied to the setter of numLanes
				for (int i = 0; i < numLanes; i += 1) {
					res.add(
						new CustomDualTreeBidiMap<IAgent, Double>(new Comparator<IAgent>() {
							@Override
							public int compare(IAgent a, IAgent b) {
								int r = a.getSpeciesName().compareTo(b.getSpeciesName());
								if (r != 0) {
									return r;
								} else {
									return Integer.compare(a.getIndex(), b.getIndex());
								}
							}
						}, 
						Collections.reverseOrder())
					);
				}
				RoadSkill.setVehicleOrdering(agent, res);
			}
		}
		return graph;
	}
}
