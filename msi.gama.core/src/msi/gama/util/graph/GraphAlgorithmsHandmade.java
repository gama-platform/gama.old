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
	public static Object getOneRandomNode(final IGraph graph) {
		return graph.getVertices().get(GAMA.getRandom().between(0, graph.getVertices().size() - 1));
	}

	/**
	 * Picks up a random node that is not the one passed in parameter
	 * @param graph
	 * @param excludedNode
	 * @return
	 */
	public static Object getAnotherRandomNode(final IGraph graph, final Object excludedNode) {

		if ( graph.getVertices().size() < 2 ) { throw new GamaRuntimeException(
			"unable to find another node in this very small network"); }

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
	public static IGraph rewireGraphProbability(final IGraph graph, final Double probability) {

		IList edges = graph.getEdges();
		for ( int i = 0; i < edges.size(); i++ ) {

			Object currentEdge = edges.get(i);
			if ( GAMA.getRandom().between(0, 1.0) <= probability ) {

				// rewire this edge
				Object from = graph.getEdgeSource(currentEdge);

				System.err.println("removing " + from);

				Object toNode = getAnotherRandomNode(graph, from);
				System.err
					.println("rewiring " + graph.getEdgeTarget(currentEdge) + " to " + toNode);

				graph.removeEdge(currentEdge);

				graph.addEdge(from, toNode, currentEdge);

			}

		}

		return graph;

	}

	/**
	 * Rewires the given count of edges. If there are too many edges,
	 * all the edges will be rewired.
	 * @param graph
	 * @param count
	 * @return
	 */
	public static IGraph rewireGraphCount(final IGraph graph, final Integer count) {

		IList edges = graph.getEdges();
		for ( int i = 0; i < count; i++ ) {

			Object currentEdge =
				edges.get(GAMA.getRandom().between(0, graph.getEdges().length(null) - 1 // VERIFY
																						// NULL
																						// SCOPE
					));

			// rewire this edge
			Object from = graph.getEdgeSource(currentEdge);

			System.err.println("removing " + from);

			Object toNode = getAnotherRandomNode(graph, from);
			System.err.println("rewiring " + graph.getEdgeTarget(currentEdge) + " to " + toNode);

			graph.removeEdge(currentEdge);

			graph.addEdge(from, toNode, currentEdge);

		}

		return graph;

	}

}
