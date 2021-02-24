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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Containers;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;

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
				final IAgent ag = ((IShape) edge).getAgent();
				if (ag.hasAttribute(RoadSkill.LANES) && ag.hasAttribute(RoadSkill.AGENTS_ON)) {
					final int lanes = (Integer) ag.getAttribute(RoadSkill.LANES);
					if (lanes > 0) {
						final IList agentsOn = (IList) ag.getAttribute(RoadSkill.AGENTS_ON);
						for (int i = 0; i < lanes; i++) {
							final int nbSeg = ag.getInnerGeometry().getNumPoints() - 1;
							final IList lisSg = GamaListFactory.create(Types.NO_TYPE);
							for (int j = 0; j < nbSeg; j++) {
								lisSg.add(GamaListFactory.create(Types.NO_TYPE));
							}
							agentsOn.add(lisSg);
						}
					}
					ag.setAttribute(RoadSkill.AGENTS, GamaListFactory.create(Types.NO_TYPE));
				}

			}
		}
		return graph;
	}

	@operator(value = "extract_nodes", 
			  content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			  index_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 2,
			  concept = {IConcept.GRAPH, IConcept.TRANSPORT})
	@doc(value = "A list of nodes that correspond to the given edges",
		comment = "The first operand is a list of edges, and the second one is the species representing graph nodes",
		examples = {
			@example(value = "list<node> nodes <- extract_nodes(road, node); // `road` and `node` are species defined by the user",
					isExecutable = false),
		}
	)
	@no_test
	public static IList extractNodes(IScope scope, IContainer edges, ISpecies nodeSpecies) {
		// extract the end points of each edge
		IList uniquePoints = GamaListFactory.create(Types.POINT);
		for (Object edge : edges.iterable(scope)) {
			if (edge instanceof IShape) {
				IAgent agent = ((IShape) edge).getAgent();
				IList points = agent.getGeometry().getPoints();
				uniquePoints.add((ILocation) points.firstValue(scope));
				uniquePoints.add((ILocation) points.lastValue(scope));
			}
		}
		// remove duplicate points
		uniquePoints = Containers.remove_duplicates(scope, uniquePoints);

		// create node agents at these points
		IPopulation population = scope.getAgent().getPopulationFor(nodeSpecies);
		List<Map<String, Object>> attrMaps = new ArrayList<>();
		for (Object point : uniquePoints.iterable(scope)) {
			Map<String, Object> map = new HashMap<>();
			map.put("location", point);
			attrMaps.add(map);
		}
		return population.createAgents(scope, uniquePoints.size(), attrMaps, false, true);
	}
}
