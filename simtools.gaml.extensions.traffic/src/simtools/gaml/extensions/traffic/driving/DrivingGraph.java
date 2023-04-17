/*******************************************************************************************************
 *
 * DrivingGraph.java, in simtools.gaml.extensions.traffic, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic.driving;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IMap;
import msi.gama.util.graph.GraphEvent;
import msi.gama.util.graph.GraphEvent.GraphEventType;
import msi.gama.util.graph._Edge;

/**
 * The Class DrivingGraph.
 */
public class DrivingGraph extends GamaSpatialGraph {
	
	/**
	 * Instantiates a new driving graph.
	 *
	 * @param edges the edges
	 * @param vertices the vertices
	 * @param scope the scope
	 */
	public DrivingGraph(final IContainer edges, final IContainer vertices, final IScope scope) {
		super(scope, vertices.getGamlType().getContentType(), edges.getGamlType().getContentType());
		init(scope, edges, vertices);
	}

	@Override
	public boolean addEdgeWithNodes(final IScope scope, final IShape e, final IMap<GamaPoint, IShape> nodes) {
		if (containsEdge(e)) return false;
		final Coordinate[] coord = e.getInnerGeometry().getCoordinates();
		final IShape ptS = new GamaPoint(coord[0]);
		final IShape ptT = new GamaPoint(coord[coord.length - 1]);
		final IShape v1 = nodes.get(ptS);
		if (v1 == null) return false;
		final IShape v2 = nodes.get(ptT);
		if (v2 == null) return false;

		if (e instanceof IAgent && ((IAgent) e).getSpecies().implementsSkill("skill_road")) {
			final IAgent roadAgent = e.getAgent();
			final IAgent source = v1.getAgent();
			final IAgent target = v2.getAgent();
			final List<IAgent> v1ro = RoadNodeSkill.getRoadsOut(source);
			if (!v1ro.contains(roadAgent)) {
				v1ro.add(roadAgent);
			}
			final List<IAgent> v2ri = RoadNodeSkill.getRoadsIn(target);
			if (!v2ri.contains(roadAgent)) {
				v2ri.add(roadAgent);
			}
			RoadSkill.setSourceNode(roadAgent, source);
			RoadSkill.setTargetNode(roadAgent, target);
		}

		addVertex(v1);
		addVertex(v2);
		_Edge<IShape, IShape> edge;
		try {
			edge = newEdge(e, v1, v2);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create edge from " + StringUtils.toGaml(e, false) + " in graph " + this);
			throw e1;
		}
		// if ( edge == null ) { return false; }
		edgeMap.put(e, edge);
		dispatchEvent(scope, new GraphEvent(scope, this, this, e, null, GraphEventType.EDGE_ADDED));
		return true;
	}
}
