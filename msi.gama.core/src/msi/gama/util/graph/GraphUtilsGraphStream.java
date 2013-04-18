package msi.gama.util.graph;

import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

public class GraphUtilsGraphStream {

	
	public static Graph getGraphstreamGraphFromGamaGraph(IGraph gamaGraph) {
		
		Graph g = new DefaultGraph("tmpGraph");
		
		Map<Object,Node> gamaNode2graphStreamNode = new HashMap<Object, Node>(gamaGraph._internalNodesSet().size());
		
		// add nodes		
		for (Object v : gamaGraph._internalVertexMap().keySet() ) {
			_Vertex vertex = (_Vertex)gamaGraph._internalVertexMap().get(v);
				
			Node n = g.addNode(v.toString());
			gamaNode2graphStreamNode.put(
					v,
					n
					);
			
			if (v instanceof IAgent) {
				IAgent a = (IAgent)v;
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					n.setAttribute(key.toString(), value.toString());
				}
			}

			if (v instanceof IShape) {
				IShape sh = (IShape)v;
		
				n.setAttribute("x", sh.getLocation().getX());
				n.setAttribute("y", sh.getLocation().getY());
				n.setAttribute("z", sh.getLocation().getZ());
			
			}
		
		}
		
		// add edges
		for (Object edgeObj : gamaGraph._internalEdgeMap().keySet() ) {
			_Edge edge = (_Edge)gamaGraph._internalEdgeMap().get(edgeObj);
			
			try {
			Edge e = g.addEdge(
					edgeObj.toString(), 
					gamaNode2graphStreamNode.get(edge.getSource()),
					gamaNode2graphStreamNode.get(edge.getTarget())
					);
			if (edgeObj instanceof IAgent) {
				IAgent a = (IAgent)edgeObj;
				for (Object key : a.getAttributes().keySet()) {
					Object value = a.getAttributes().get(key);
					e.setAttribute(key.toString(), value.toString());
				}
			}
			} catch (EdgeRejectedException e) {
				GAMA.reportError(new GamaRuntimeException("an edge was rejected during the transformation, probably because it was a double one", true));
			}
			

			
		}

		// some basic tests for integrity
		if (gamaGraph.getVertices().size() != g.getNodeCount())
			GAMA.reportError(
					new GamaRuntimeException(
							"The exportation ran without error, but an integrity test failed: " +
							"the number of vertices is not correct("+g.getNodeCount()+" instead of "+gamaGraph.getVertices().size()+")", 
							true)
					);
		if (gamaGraph.getEdges().size() != g.getEdgeCount())
			GAMA.reportError(
					new GamaRuntimeException(
							"The exportation ran without error, but an integrity test failed: " +
							"the number of edges is not correct("+g.getEdgeCount()+" instead of "+gamaGraph.getEdges().size()+")", 
							true)
					);

								
		return g;
	}
}
