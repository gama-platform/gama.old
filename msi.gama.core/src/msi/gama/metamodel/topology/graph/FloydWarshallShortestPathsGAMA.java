/*********************************************************************************************
 *
 * 'FloydWarshallShortestPathsGAMA.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.graph;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import java.util.*;
import msi.gama.util.graph.GamaGraph;
import org.jgrapht.*;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.VertexPair;

// Copy of the jgrapht algorithm: just make it usable with GAMA undirected graph
public class FloydWarshallShortestPathsGAMA<V, E> {

	// ~ Instance fields --------------------------------------------------------

	private final Graph<V, E> graph;
	private final List<V> vertices;
	private int nShortestPaths = 0;
	private double diameter = 0.0;
	private double[][] d = null;
	private int[][] backtrace = null;
	private THashMap<VertexPair<V>, GraphPath<V, E>> paths = null;

	// ~ Constructors -----------------------------------------------------------

	public FloydWarshallShortestPathsGAMA(final Graph<V, E> graph) {
		this.graph = graph;
		this.vertices = new ArrayList<V>(((GamaGraph<V, E>)graph).getVertexMap().keySet());
		
	}

	// ~ Methods ----------------------------------------------------------------

	/**
	 * @return the graph on which this algorithm operates
	 */
	public Graph<V, E> getGraph() {
		return graph;
	}

	/**
	 * @return total number of shortest paths
	 */
	public int getShortestPathsCount() {
		lazyCalculatePaths();
		return nShortestPaths;
	}
	
	

	public double[][] getD() {
		return d;
	}

	public int[][] getBacktrace() {
		return backtrace;
	}

	/**
	 * Calculates the matrix of all shortest paths, along with the diameter, but
	 * does not populate the paths map.
	 */
	public void lazyCalculateMatrix() {
		if ( d != null ) {
			// already done
			return;
		}

		int n = vertices.size();

		// init the backtrace matrix
		backtrace = new int[n][n];
		for ( int i = 0; i < n; i++ ) {
			Arrays.fill(backtrace[i], -1);
		}

		// initialize matrix, 0
		d = new double[n][n];
		for ( int i = 0; i < n; i++ ) {
			Arrays.fill(d[i], Double.POSITIVE_INFINITY);
		}

		// initialize matrix, 1
		for ( int i = 0; i < n; i++ ) {
			d[i][i] = 0.0;
		}

		// initialize matrix, 2
		Set<E> edges = graph.edgeSet();
		for ( E edge : edges ) {
			V v1 = graph.getEdgeSource(edge);
			V v2 = graph.getEdgeTarget(edge);

			int v_1 = vertices.indexOf(v1);
			int v_2 = vertices.indexOf(v2);

			d[v_1][v_2] = graph.getEdgeWeight(edge);
			if ( !((GamaGraph<V, E>) graph).isDirected() ) {
				d[v_2][v_1] = graph.getEdgeWeight(edge);
			}
		}

		// run fw alg
		for ( int k = 0; k < n; k++ ) {
			for ( int i = 0; i < n; i++ ) {
				for ( int j = 0; j < n; j++ ) {
					double ik_kj = d[i][k] + d[k][j];
					if ( ik_kj < d[i][j] ) {
						d[i][j] = ik_kj;
						backtrace[i][j] = k;
						diameter = diameter > d[i][j] ? diameter : d[i][j];
					}
				}
			}
		}
	}

	/**
	 * Get the length of a shortest path.
	 * 
	 * @param a first vertex
	 * @param b second vertex
	 * 
	 * @return shortest distance between a and b
	 */
	public double shortestDistance(final V a, final V b) {
		lazyCalculateMatrix();

		return d[vertices.indexOf(a)][vertices.indexOf(b)];
	}

	/**
	 * @return the diameter (longest of all the shortest paths) computed for the
	 *         graph
	 */
	public double getDiameter() {
		lazyCalculateMatrix();
		return diameter;
	}

	public void shortestPathRecur(final List<E> edges, final int v_a, final int v_b) {
		int k = backtrace[v_a][v_b];
		
		if ( k == -1 ) {
			E edge = graph.getEdge(vertices.get(v_a), vertices.get(v_b));
			if ( edge != null ) {
				edges.add(edge);
			}
		} else {
			shortestPathRecur(edges, v_a, k);
			shortestPathRecur(edges, k, v_b);
		}
	}
	
	public int succRecur(final int v_a, final int v_b) {
		int k = backtrace[v_a][v_b];
		if ( k == -1 ) {
			if ( ! graph.containsEdge(vertices.get(v_a), vertices.get(v_b)) ) {
				return -1;
			}
			return v_b;
		} else {
			return succRecur(v_a, k);
		}
	}
	
	
	/**
	 * Get the shortest path between two vertices. Note: The paths are
	 * calculated using a recursive algorithm. It *will* give problems on paths
	 * longer than the stack allows.
	 * 
	 * @param a From vertice
	 * @param b To vertice
	 * 
	 * @return the path, or null if none found
	 */
	public GraphPath<V, E> getShortestPath(final V a, final V b) {
		lazyCalculatePaths();
		return getShortestPathImpl(a, b);
	}

	private GraphPath<V, E> getShortestPathImpl(final V a, final V b) {
		int v_a = vertices.indexOf(a);
		int v_b = vertices.indexOf(b);

		List<E> edges = new ArrayList<E>();
		shortestPathRecur(edges, v_a, v_b);

		// no path, return null
		if ( edges.size() < 1 ) { return null; }

		GraphPathImpl<V, E> path = new GraphPathImpl<V, E>(graph, a, b, edges, edges.size());

		return path;
	}

	/**
	 * Calculate the shortest paths (not done per default)
	 */
	private void lazyCalculatePaths() {
		// already we have calculated it once.
		if ( paths != null ) { return; }

		lazyCalculateMatrix();

		THashMap<VertexPair<V>, GraphPath<V, E>> sps = new THashMap<VertexPair<V>, GraphPath<V, E>>();
		int n = vertices.size();

		nShortestPaths = 0;
		for ( int i = 0; i < n; i++ ) {
			for ( int j = 0; j < n; j++ ) {
				// don't count this.
				if ( i == j ) {
					continue;
				}

				V v_i = vertices.get(i);
				V v_j = vertices.get(j);

				GraphPath<V, E> path = getShortestPathImpl(v_i, v_j);

				// we got a path
				if ( path != null ) {
					sps.put(new VertexPair<V>(v_i, v_j), path);
					nShortestPaths++;
				}
			}
		}

		this.paths = sps;
	}

	/**
	 * Get shortest paths from a vertex to all other vertices in the graph.
	 * 
	 * @param v the originating vertex
	 * 
	 * @return List of paths
	 */
	public List<GraphPath<V, E>> getShortestPaths(final V v) {
		lazyCalculatePaths();
		final List<GraphPath<V, E>> found = new ArrayList<GraphPath<V, E>>();
		paths.forEachEntry(new TObjectObjectProcedure<VertexPair<V>, GraphPath<V, E>>() {

			@Override
			public boolean execute(final VertexPair<V> pair, final GraphPath<V, E> path) {
				if ( pair.hasVertex(v) ) {
					found.add(path);
				}
				return true;
			}
		});

		return found;
	}
}

// End FloydWarshallShortestPaths.java