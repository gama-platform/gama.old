package msi.gama.util.graph;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;

/**
 * Contains various handmade algorithms.
 * Algorithms mainly based on external dependances
 * should take place elsewhere for shake of lisibility.
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphAlgorithmsHandmade {

	/**
	 * Picks up a random node in the graph and returns it. 
	 * @param graph
	 * @return
	 */
	public static Object getOneRandomNode(IGraph graph) {
		return graph.getVertices().get(GAMA.getRandom().between(0, graph.getVertices().size()-1));
	}
	
	/**
	 * Picks up a random node that is not the one passed in parameter
	 * @param graph
	 * @param excludedNode
	 * @return
	 */
	public static Object getAnotherRandomNode(IGraph graph, Object excludedNode) {
		
		if (graph.getVertices().size() < 2)
			throw new GamaRuntimeException("unable to find another node in this very small network");
		
		Object proposedNode = null;
		do {
			proposedNode = getOneRandomNode(graph);
		} while (proposedNode == excludedNode);
		
		return proposedNode;
	}
	
	/**
	 * TODO does not works now
	 * Rewires a graph (in the Watts-Strogatz meaning)
	 * @param graph
	 * @param probability
	 * @return
	 */
	public static IGraph rewireGraph(IGraph graph, Double probability) {
		
		IList edges = graph.getEdges();
		for (int i=0; i<edges.size(); i++) {
			
			Object currentEdge = edges.get(i);
			if (GAMA.getRandom().between(0, 1.0) <= probability) {
				
				// rewire this edge
				Object from = graph.getEdgeSource(currentEdge);
				
				System.err.println("removing "+from);
				
				Object toNode = getAnotherRandomNode(graph, from);
				System.err.println("rewiring "+graph.getEdgeTarget(currentEdge)+" to "+toNode);

				graph.removeEdge(currentEdge);
				
				graph.addEdge(
						from, 
						toNode, 
						currentEdge
						);
				
			}
				
		}
		
		return graph;
		
	}
	
}
