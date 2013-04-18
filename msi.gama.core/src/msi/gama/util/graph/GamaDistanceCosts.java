package msi.gama.util.graph;

import org.graphstream.algorithm.AStar.Costs;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class GamaDistanceCosts implements Costs{

	GamaGraph graph;
	@Override
	public double heuristic(Node node, Node target) {
		/*Object n = graph.getNodesGS().get(node.getId());
		if (n instanceof IShape) {
			IShape t = (IShape) graph.getNodesGS().get(target.getId());
			return t.euclidianDistanceTo((IShape) n);
		}*/
		return 0;
	}

	@Override
	public double cost(Node parent, Edge from, Node next) {
		return from.getAttribute("length");
	}

	public GamaGraph getGraph() {
		return graph;
	}

	public void setGraph(GamaGraph graph) {
		this.graph = graph;
	}

	public GamaDistanceCosts(GamaGraph graph) {
		super();
		this.graph = graph;
	}
	

}
