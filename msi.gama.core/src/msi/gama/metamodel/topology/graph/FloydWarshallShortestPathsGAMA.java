/*******************************************************************************************************
 *
 * FloydWarshallShortestPathsGAMA.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;

import msi.gama.runtime.GAMA;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.matrix.GamaIntMatrix;

/**
 * The Class FloydWarshallShortestPathsGAMA.
 *
 * @param <V>
 *            the value type
 * @param <E>
 *            the element type
 */
// Copy of the jgrapht algorithm: just make it usable with GAMA undirected graph
public class FloydWarshallShortestPathsGAMA<V, E> {

	// TODO Look at the new implementantion of the algorithm in JGraphT 1.0.1 and try to derive from it.

	// ~ Instance fields --------------------------------------------------------

	/** The graph. */
	private final GamaGraph<V, E> graph;

	/** The vertices. */
	private final List<V> vertices;

	/** The n shortest paths. */
	private int nShortestPaths = 0;

	/** The diameter. */
	private double diameter = 0.0;

	/** The d. */
	private double[][] d = null;

	/** The backtrace. */
	private int[][] backtrace = null;

	/** The matrix. */
	private GamaIntMatrix matrix = null;

	/** The paths. */
	private IMap<Pair<V, V>, GraphPath<V, E>> paths = null;

	// ~ Constructors -----------------------------------------------------------

	/**
	 * Instantiates a new floyd warshall shortest paths GAMA.
	 *
	 * @param graph
	 *            the graph
	 */
	public FloydWarshallShortestPathsGAMA(final GamaGraph<V, E> graph) {
		this.graph = graph;
		this.vertices = new ArrayList<>(graph.getVertexMap().keySet());

	}

	// ~ Methods ----------------------------------------------------------------

	/**
	 * Instantiates a new floyd warshall shortest paths GAMA.
	 *
	 * @param graph
	 *            the graph
	 * @param matrix
	 *            the matrix
	 */
	public FloydWarshallShortestPathsGAMA(final GamaGraph<V, E> graph, final GamaIntMatrix matrix) {
		this.graph = graph;
		this.vertices = new ArrayList<>(graph.getVertexMap().keySet());
		this.paths = GamaMapFactory.createUnordered();
		nShortestPaths = 0;
		this.matrix = matrix;
	}

	/**
	 * @return the graph on which this algorithm operates
	 */
	public Graph<V, E> getGraph() { return graph; }

	/**
	 * @return total number of shortest paths
	 */
	public int getShortestPathsCount() {
		lazyCalculatePaths();
		return nShortestPaths;
	}

	/**
	 * Gets the d.
	 *
	 * @return the d
	 */
	public double[][] getD() { return d; }

	/**
	 * Gets the backtrace.
	 *
	 * @return the backtrace
	 */
	public int[][] getBacktrace() { return backtrace; }

	/**
	 * Calculates the matrix of all shortest paths, along with the diameter, but does not populate the paths map.
	 */
	@SuppressWarnings ("unchecked")
	public void lazyCalculateMatrix() {
		matrix = null;
		if (d != null) // already done
			return;

		final int n = vertices.size();

		// init the backtrace matrix
		backtrace = new int[n][n];
		for (int i = 0; i < n; i++) { Arrays.fill(backtrace[i], -1); }

		// initialize matrix, 0
		d = new double[n][n];
		for (int i = 0; i < n; i++) { Arrays.fill(d[i], Double.POSITIVE_INFINITY); }

		// initialize matrix, 1
		for (int i = 0; i < n; i++) { d[i][i] = 0.0; }

		// initialize matrix, 2
		final Set<E> edges = graph.edgeSet();

		for (final E edge : edges) {
			final V v1 = (V) graph.getEdgeSource(edge);
			final V v2 = (V) graph.getEdgeTarget(edge);

			final int v_1 = vertices.indexOf(v1);
			final int v_2 = vertices.indexOf(v2);

			d[v_1][v_2] = graph.getEdgeWeight(edge);
			if (!graph.isDirected()) { d[v_2][v_1] = graph.getEdgeWeight(edge); }
		}

		// run fw alg
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					final double ik_kj = d[i][k] + d[k][j];
					if (ik_kj < d[i][j]) {
						d[i][j] = ik_kj;
						backtrace[i][j] = k;
						diameter = diameter > d[i][j] ? diameter : d[i][j];
					}
				}
			}
		}
	}

	/**
	 * @return the diameter (longest of all the shortest paths) computed for the graph
	 */
	public double getDiameter() {
		lazyCalculateMatrix();
		return diameter;
	}

	/**
	 * Shortest path recur.
	 *
	 * @param edges
	 *            the edges
	 * @param v_a
	 *            the v a
	 * @param v_b
	 *            the v b
	 */
	public void shortestPathRecur(final List<E> edges, final int v_a, final int v_b) {
		final int k = backtrace[v_a][v_b];

		if (k == -1) {
			final Set<E> edgs = graph.getAllEdges(vertices.get(v_a), vertices.get(v_b));
			double minW = Double.MAX_VALUE;
			E edge = null;
			for (final E e : edgs) {
				final double ew = graph.getEdgeWeight(e);
				if (ew < minW) {
					minW = ew;
					edge = e;
				}
			}
			if (edge != null) { edges.add(edge); }
		} else {
			shortestPathRecur(edges, v_a, k);
			shortestPathRecur(edges, k, v_b);
		}
	}

	/**
	 * Succ recur.
	 *
	 * @param v_a
	 *            the v a
	 * @param v_b
	 *            the v b
	 * @return the int
	 */
	public int succRecur(final int v_a, final int v_b) {
		final int k = backtrace[v_a][v_b];
		if (k != -1) return succRecur(v_a, k);
		if (!graph.containsEdge(vertices.get(v_a), vertices.get(v_b))) return -1;
		return v_b;
	}

	/**
	 * Get the shortest path between two vertices. Note: The paths are calculated using a recursive algorithm. It *will*
	 * give problems on paths longer than the stack allows.
	 *
	 * @param a
	 *            From vertice
	 * @param b
	 *            To vertice
	 *
	 * @return the path, or null if none found
	 */
	public GraphPath<V, E> getShortestPath(final V a, final V b) {
		lazyCalculatePaths();
		return getShortestPathImpl(a, b);
	}

	/**
	 * Gets the shortest path impl.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the shortest path impl
	 */
	private GraphPath<V, E> getShortestPathImpl(final V a, final V b) {
		int v_a = vertices.indexOf(a);
		final int v_b = vertices.indexOf(b);
		int prev = v_a;
		final List<E> edges = new ArrayList<>();
		if (matrix != null) {
			v_a = matrix.get(GAMA.getRuntimeScope(), v_b, v_a);
			if (v_a != -1) {
				while (prev != v_b) {
					final Set<E> eds = graph.getAllEdges(vertices.get(prev), vertices.get(v_a));
					if (eds.isEmpty()) return null;
					double minW = Double.MAX_VALUE;
					E ed = null;
					for (final E e : eds) {
						final double w = graph.getEdgeWeight(e);
						if (w < minW) {
							minW = w;
							ed = e;
						}
					}
					edges.add(ed);
					if (prev != v_b) {
						prev = v_a;
						v_a = matrix.get(GAMA.getRuntimeScope(), v_b, v_a);
					}
				}
			}
		} else {
			shortestPathRecur(edges, v_a, v_b);
		}

		// no path, return null
		if (edges.size() < 1) return null;
		final GraphWalk<V, E> path = new GraphWalk<>(graph, a, b, edges, edges.size());
		if (graph.getPathComputer().isSaveComputedShortestPaths()) {
			final V v_i = vertices.get(v_a);
			final V v_j = vertices.get(v_b);

			paths.put(new Pair<>(v_i, v_j), path);
			nShortestPaths++;
		}
		return path;
	}

	/**
	 * Calculate the shortest paths (not done per default)
	 */
	private void lazyCalculatePaths() {
		// already we have calculated it once.
		if (paths != null || matrix != null) return;

		lazyCalculateMatrix();

		this.paths = GamaMapFactory.createUnordered();

		nShortestPaths = 0;

	}
}

// End FloydWarshallShortestPaths.java