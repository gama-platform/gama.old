package msi.gama.util.graph;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

public class TestUtilsGraphs {

	

	/**
	 * Compares two edges for basic values: 
	 * @param original
	 * @param tested
	 * @param acceptedIgnoredEdges: if double edges are known to be in the original network but will not be added in the result
	 */
	public static void compareGamaGraphs(String msg, GamaGraph original, GamaGraph tested, int acceptedIgnoredEdges) {
	
		assertEquals(
				msg+": wrong number of vertices",
				original.getVertices().size(), 
				tested.getVertices().size()
				);
		assertEquals(
				msg+": wrong number of edges",
				original.getVertices().size()-acceptedIgnoredEdges,
				tested.getVertices().size()
				);
		
		for (Object v : original.getVertices()) {
			assertTrue(msg+": node not found "+v, tested.containsVertex(v));
		}
		
		for (Object e: original.getEdges()) {
			Object source = original.getEdgeSource(e);
			Object target = original.getEdgeTarget(e);
			assertTrue(
					msg+": edge "+source+"->"+target+" not found" ,
					tested.containsEdge(source, target)
			);
		}
		
		assertEquals(
				msg+": directionality not restored",
				original.isDirected(), 
				tested.isDirected()
				);
		
		
		
	}
	
	protected static Collection<GamaGraph> getGamaGraphsForTest() {
		LinkedList<GamaGraph> graphs = new LinkedList<GamaGraph>();
		
		GamaGraph g = null;
		
		// an empty graph (default)
		g = new GamaGraph();
		graphs.add(g);
		
		// an empty graph, but not directed
		g = new GamaGraph(false);
		graphs.add(g);
		
		// an empty graph, but directed
		g = new GamaGraph(true);
		graphs.add(g);
	
		
		// a graph with only one node
		g = new GamaGraph(true);
		g.addVertex("1");
		graphs.add(g);
	
		// a graph with only one node
		g = new GamaGraph(false);
		g.addVertex("1");
		graphs.add(g);
		
		// a graph with two nodes
		g = new GamaGraph(true);
		g.addVertex("1");
		g.addVertex("2");
		graphs.add(g);
		
		// a graph with three nodes
		g = new GamaGraph(true);
		g.addVertex("1");
		g.addVertex("2");
		g.addVertex("3");
		graphs.add(g);
				
		// a graph with three nodes
		g = new GamaGraph(false);
		g.addVertex("1");
		g.addVertex("2");
		g.addVertex("3");
		graphs.add(g);
		
		// a small graph (undirected)
		g = new GamaGraph(false);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		graphs.add(g);
		
		// a small graph (directed)
		g = new GamaGraph(true);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		graphs.add(g);
		
		// a small graph with redondant edges (undirected)
		g = new GamaGraph(true);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		g.addEdge(1, 2);
		graphs.add(g);
		
		// a small graph with non redondant edges because it is directed
		g = new GamaGraph(true);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		g.addEdge(2, 1);
		graphs.add(g);
		
		// a complete graph
		g = new GamaGraph(false);
		for (int i=0;i<100;i++) {
			g.addVertex(i);
		}
		for (int i=0;i<100;i++) {
			for (int j=i;j<100;j++) {
				if (i==j)
					continue; // no self loops
				g.addEdge(i, j);
			}
		}
		graphs.add(g);
		
		// disjoint graph
		g = new GamaGraph(false);
		for (int i=0;i<100;i++) {
			g.addVertex(i);
		}
		for (int i=0;i<50;i++) {
			for (int j=i;j<50;j++) {
				if (i==j)
					continue; // no self loops
				g.addEdge(i, j);
			}
		}
		for (int i=52;i<100;i++) {
			for (int j=i;j<100;j++) {
				if (i==j)
					continue; // no self loops
				g.addEdge(i, j);
			}
		}
		graphs.add(g);
		
		// TODO other graphs
		
		return graphs;
	}
}
