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

import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.geometry.GeometryUtils;
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
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
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

	/**
	 * Checks if there is enough space for the vehicle to enter the specified lane on the new road
	 *
	 * @param scope
	 * @param road The new road
	 * @param lane The lane index to check
	 * @return true if there is enough space, false otherwise
	 */
	@operator(value = "enough_space_to_enter_road")
	public static boolean enoughSpaceToEnterRoad(IScope scope, IAgent road, int lane, int numLanesOccupied, double requiredLength) {
		List<List<List<IAgent>>> driversOnNextRoad = (List<List<List<IAgent>>>)
				road.getAttribute(RoadSkill.AGENTS_ON);
		// check if chosen lanes on next road are totally clear
		boolean allClear = true;
		for (int i = 0; i < numLanesOccupied; i += 1) {
			// TODO: fix potential bug
			List<List<IAgent>> laneDrivers = driversOnNextRoad.get(lane);
			// check first segment only
			if (!laneDrivers.get(0).isEmpty()) {
				allClear = false;
				break;
			}
		}
		if (allClear) return true;

		// check if any vehicle in these lanes is too close to the source node of the road
		for (int i = 0; i < numLanesOccupied; i += 1) {
			List<List<IAgent>> laneDrivers = driversOnNextRoad.get(lane);
			for (List<IAgent> segmentDrivers : laneDrivers)
			for (IAgent otherDriver : segmentDrivers) {
				if (otherDriver == null || otherDriver.dead()) continue;
				if (GeometryUtils.getFirstPointOf(road).euclidianDistanceTo(otherDriver) <
						requiredLength + (double) otherDriver.getAttribute(DrivingSkill.VEHICLE_LENGTH) / 2) {
					return false;
				}
			}
		}
		return true;
	}
}
