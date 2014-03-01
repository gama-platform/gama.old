package simtools.gaml.extensions.traffic;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IType;

public class DrivingOperators {
	
	@operator(value = "as_driving_graph", content_type = IType.GEOMETRY, index_type = IType.GEOMETRY)
	@doc(value = "creates a graph from the list/map of edges given as operand and connect the node to the edge", examples = { "as_driving_graph(road,node)  --:  build a graph while using the road agents as edges and the node agents as nodes" }, see = {
		"as_intersection_graph", "as_distance_graph","as_edge_graph" })
	public static IGraph spatialDrivingFromEdges(final IScope scope, final IContainer edges, final IContainer nodes) {
		IGraph graph = new GamaSpatialGraph(edges, nodes,scope);
		for (Object edge: edges.iterable(scope)) {
			if (edge instanceof IShape) {
				IAgent ag = ((IShape) edge).getAgent();
				if (ag.hasAttribute(RoadSkill.LANES) && ag.hasAttribute(RoadSkill.AGENTS_ON)) {
					int lanes = (Integer) ag.getAttribute(RoadSkill.LANES);
					if (lanes > 0) {
						IList agentsOn = (IList) ag.getAttribute(RoadSkill.AGENTS_ON) ;
						for (int i = 0; i < lanes; i++) {
							int nbSeg = ag.getInnerGeometry().getNumPoints() - 1;
							GamaList lisSg = new GamaList();
							for (int j = 0; j < nbSeg; j++) {
								lisSg.add(new GamaList());
							}
							agentsOn.add(lisSg);
						}
					}
					ag.setAttribute(RoadSkill.AGENTS, new GamaList());
				}
				
				
			}
		}
		return graph;
	}

}
