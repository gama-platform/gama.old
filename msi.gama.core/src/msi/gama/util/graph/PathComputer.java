/*******************************************************************************************************
 *
 * PathComputer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.BhandariKDisjointShortestPaths;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.alg.shortestpath.ContractionHierarchyBidirectionalDijkstra;
import org.jgrapht.alg.shortestpath.DeltaSteppingShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.TransitNodeRoutingShortestPath;
import org.jgrapht.alg.shortestpath.YenKShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.SupplierUtil;

import com.google.common.collect.ImmutableList;

import msi.gama.metamodel.topology.graph.AStar;
import msi.gama.metamodel.topology.graph.FloydWarshallShortestPathsGAMA;
import msi.gama.metamodel.topology.graph.NBAStarPathfinder;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.path.IPath;
import msi.gaml.types.Types;

/**
 * The Class PathComputer.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @param <V>
 *            the value type
 * @param <E>
 *            the element type
 * @date 30 oct. 2023
 */
public class PathComputer<V, E> {

	/**
	 *
	 */
	private final GamaGraph<V, E> graph;

	/** The save computed shortest paths. */
	protected boolean saveComputedShortestPaths = true;

	/**
	 * Instantiates a new path computer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param gamaGraph
	 *            TODO
	 * @date 30 oct. 2023
	 */
	PathComputer(final GamaGraph<V, E> gamaGraph) {
		graph = gamaGraph;
	}

	/**
	 * The Enum shortestPathAlgorithm.
	 */
	public enum shortestPathAlgorithm {

		/** The Floyd warshall. */
		FloydWarshall,

		/** The Bellmann ford. */
		BellmannFord,

		/** The Dijkstra. */
		Dijkstra,

		/** The A star. */
		AStar,

		/** The NBA star. */
		NBAStar,

		/** The NBA star approx. */
		NBAStarApprox,

		/** The Delta stepping. */
		DeltaStepping,

		/** The CH bidirectional dijkstra. */
		CHBidirectionalDijkstra,

		/** The Bidirectional dijkstra. */
		BidirectionalDijkstra,

		/** The Transit node routing. */
		TransitNodeRouting;
	}

	/**
	 * The Enum kShortestPathAlgorithm.
	 */
	public enum kShortestPathAlgorithm {

		/** The Yen. */
		Yen,
		/** The Bhandari. */
		Bhandari;
	}

	/** The version. */
	protected int version = 1;

	/** The shortest path computed. */
	protected Map<Pair<V, V>, IList<IList<E>>> shortestPathComputed = new ConcurrentHashMap<>();

	/** The shortest path matrix. */
	protected GamaIntMatrix shortestPathMatrix = null;

	/** The Constant ONLY_FOR_DIRECTED_GRAPH. */
	public static final ImmutableList<PathComputer.kShortestPathAlgorithm> ONLY_FOR_DIRECTED_GRAPH =
			ImmutableList.of(kShortestPathAlgorithm.Bhandari);

	/** The path finding algo. */
	protected PathComputer.shortestPathAlgorithm pathFindingAlgo = shortestPathAlgorithm.BidirectionalDijkstra;

	/** The k path finding algo. */
	protected PathComputer.kShortestPathAlgorithm kPathFindingAlgo = kShortestPathAlgorithm.Yen;

	/** The optimizer. */
	private FloydWarshallShortestPathsGAMA<V, E> optimizer;

	/** The contraction hierarchy BD. */
	protected ContractionHierarchyBidirectionalDijkstra<V, E> contractionHierarchyBD = null;

	/** The transit node routing. */
	protected TransitNodeRoutingShortestPath<V, E> transitNodeRouting = null;

	/** The linked J graph. */
	protected AbstractBaseGraph<String, Object> linkedJGraph;

	/** The from linked gto edges. */
	protected Map<Object, Object> fromLinkedGtoEdges;

	/**
	 * Gets the shortest path computed.
	 *
	 * @return the shortest path computed
	 */
	public Map<Pair<V, V>, IList<IList<E>>> getShortestPathComputed() { return shortestPathComputed; }

	/**
	 * Gets the shortest path.
	 *
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return the shortest path
	 */
	public IList<E> getShortestPath(final V s, final V t) {
		final Pair<V, V> vp = new Pair<>(s, t);
		final IList<IList<E>> ppc = shortestPathComputed.get(vp);
		if (ppc == null || ppc.isEmpty()) return null;
		return ppc.get(0);
	}

	/**
	 * Save shortest paths.
	 *
	 * @param scope
	 *            the scope
	 * @return the gama int matrix
	 */
	public GamaIntMatrix saveShortestPaths(final IScope scope) {
		final IMap<V, Integer> indexVertices = GamaMapFactory.create(graph.getGamlType().getKeyType(), Types.INT);
		final IList<V> vertices = graph.getVertices();

		for (int i = 0; i < graph.vertexMap.size(); i++) { indexVertices.put(vertices.get(i), i); }
		final GamaIntMatrix matrix = new GamaIntMatrix(vertices.size(), vertices.size());
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.size(); j++) { matrix.set(scope, j, i, i); }
		}
		if (optimizer != null) {
			for (int i = 0; i < vertices.size(); i++) {
				final V v1 = vertices.get(i);
				for (int j = 0; j < vertices.size(); j++) {
					final V v2 = vertices.get(j);
					final GraphPath<V, E> path = optimizer.getShortestPath(v1, v2);
					if (path == null || path.getEdgeList() == null || path.getEdgeList().isEmpty()) { continue; }
					matrix.set(scope, j, i,
							nextVertice(scope, path.getEdgeList().get(0), v1, indexVertices, graph.directed));
				}
			}
		} else if (shortestPathAlgorithm.FloydWarshall.equals(pathFindingAlgo)) {
			optimizer = new FloydWarshallShortestPathsGAMA<>(graph);
			optimizer.lazyCalculateMatrix();
			for (int i = 0; i < graph.vertexMap.size(); i++) {
				for (int j = 0; j < graph.vertexMap.size(); j++) {
					if (i == j) { continue; }
					matrix.set(scope, j, i, optimizer.succRecur(i, j));
				}
			}
		} else {
			for (int i = 0; i < graph.vertexMap.size(); i++) {
				final V v1 = vertices.get(i);
				for (int j = 0; j < graph.vertexMap.size(); j++) {
					if (i == j || matrix.get(scope, j, i) != i) { continue; }
					final V v2 = vertices.get(j);
					final List edges = computeBestRouteBetween(scope, v1, v2);
					// DEBUG.LOG("edges : " + edges);
					if (edges == null) { continue; }
					V source = v1;
					int s = i;
					for (final Object edge : edges) {
						// DEBUG.LOG("s : " + s + " j : " + j + "
						// i: " + i);
						if (s != i && matrix.get(scope, j, s) != s) { break; }

						V target = (V) graph.getEdgeTarget(edge);
						if (!graph.directed && target == source) { target = (V) graph.getEdgeSource(edge); }
						final Integer k = indexVertices.get(scope, target);
						// DEBUG.LOG("k : " +k);
						matrix.set(scope, j, s, k);
						s = k;
						source = target;
					}

				}
			}
		}
		return matrix;

	}

	/**
	 * Next vertice.
	 *
	 * @param scope
	 *            the scope
	 * @param edge
	 *            the edge
	 * @param source
	 *            the source
	 * @param indexVertices
	 *            the index vertices
	 * @param isDirected
	 *            the is directed
	 * @return the integer
	 */
	@SuppressWarnings ("unchecked")
	private Integer nextVertice(final IScope scope, final E edge, final V source, final IMap<V, Integer> indexVertices,
			final boolean isDirected) {
		if (isDirected) return indexVertices.get(scope, (V) graph.getEdgeTarget(edge));

		final V target = (V) graph.getEdgeTarget(edge);
		if (target != source) // source = target;
			return indexVertices.get(scope, target);
		return indexVertices.get(scope, (V) graph.getEdgeSource(edge));
	}

	/**
	 * Save paths.
	 *
	 * @param M
	 *            the m
	 * @param vertices
	 *            the vertices
	 * @param nbvertices
	 *            the nbvertices
	 * @param v1
	 *            the v 1
	 * @param i
	 *            the i
	 * @param t
	 *            the t
	 * @return the i list
	 */
	public IList savePaths(final int M[], final IList vertices, final int nbvertices, final Object v1, final int i,
			final int t) {
		IList edgesVertices = GamaListFactory.create(graph.getGamlType().getContentType());
		for (int j = 0; j < nbvertices; j++) {
			final IList<E> edges = GamaListFactory.create(graph.getGamlType().getContentType());
			final V vt = (V) vertices.get(j);
			if (v1 == vt) { continue; }
			Object vc = vt;
			int previous;
			int next = M[j];
			if (j == next || next == -1) { continue; }
			do {
				final Object vn = vertices.get(next);

				final Set<E> eds = graph.getAllEdges(vn, vc);

				E edge = null;
				for (final E ed : eds) {
					if (edge == null || graph.getEdgeWeight(ed) < graph.getEdgeWeight(edge)) { edge = ed; }
				}
				if (edge == null) { break; }
				edges.add(0, edge);
				previous = next;
				next = M[next];
				vc = vn;
			} while (previous != i);
			final Pair vv = new Pair<>(v1, vt);
			if (!shortestPathComputed.containsKey(vv)) {
				final IList<IList<E>> ssp = GamaListFactory.create(Types.LIST.of(graph.getGamlType().getContentType()));
				ssp.add(edges);
				shortestPathComputed.put(vv, ssp);
			}
			if (j == t) { edgesVertices = edges; }
		}
		return edgesVertices;
	}

	/**
	 * Gets the shortest path from matrix.
	 *
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return the shortest path from matrix
	 */
	public IList<E> getShortestPathFromMatrix(final V s, final V t) {
		final IList<V> vertices = graph.getVertices();
		final IList<E> edges = GamaListFactory.create(graph.getGamlType().getContentType());
		V vs = s;
		final int indexS = vertices.indexOf(vs);
		final int indexT = vertices.indexOf(t);
		int previous = indexS;
		Integer next = shortestPathMatrix.get(graph.graphScope, indexT, previous);
		if (previous == next) return edges;
		do {
			if (next == -1) return GamaListFactory.create(graph.getGamlType().getContentType());
			final V vn = vertices.get(next);

			final Set<E> eds = graph.getAllEdges(vs, vn);
			E edge = null;
			for (final E ed : eds) {
				if (edge == null || graph.getEdgeWeight(ed) < graph.getEdgeWeight(edge)) { edge = ed; }
			}
			if (edge == null) return GamaListFactory.create(graph.getGamlType().getContentType());
			edges.add(edge);
			previous = next;
			next = shortestPathMatrix.get(graph.graphScope, indexT, next);
			vs = vn;
		} while (previous != indexT);
		return edges;
	}

	/**
	 * Checks if is save computed shortest paths.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return true, if is save computed shortest paths
	 * @date 30 oct. 2023
	 */
	public boolean isSaveComputedShortestPaths() { return saveComputedShortestPaths; }

	/**
	 * Sets the save computed shortest paths.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param saveComputedShortestPaths
	 *            the new save computed shortest paths
	 * @date 30 oct. 2023
	 */
	public void setSaveComputedShortestPaths(final boolean saveComputedShortestPaths) {
		this.saveComputedShortestPaths = saveComputedShortestPaths;
	}

	/**
	 * Gets the floyd warshall shortest paths.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the floyd warshall shortest paths
	 * @date 30 oct. 2023
	 */
	public FloydWarshallShortestPathsGAMA<V, E> getFloydWarshallShortestPaths() { return optimizer; }

	/**
	 * Sets the floyd warshall shortest paths.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param optimizer
	 *            the optimizer
	 * @date 30 oct. 2023
	 */
	public void setFloydWarshallShortestPaths(final FloydWarshallShortestPathsGAMA<V, E> optimizer) {
		this.optimizer = optimizer;
	}

	/**
	 * Load shortest paths.
	 *
	 * @param scope
	 *            the scope
	 * @param matrix
	 *            the matrix
	 */
	public void loadShortestPaths(final IScope scope, final GamaMatrix matrix) {
		shortestPathMatrix = GamaIntMatrix.from(scope, matrix);

	}

	/**
	 * Compute shortest path between.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the i path
	 * @date 30 oct. 2023
	 */
	public IPath<V, E, IGraph<V, E>> computeShortestPathBetween(final IScope scope, final V source, final V target) {
		return graph.pathFromEdges(scope, source, target, computeBestRouteBetween(scope, source, target));
	}

	/**
	 * Gets the shortest path.
	 *
	 * @param scope
	 *            the scope
	 * @param algo
	 *            the algo
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the shortest path
	 */
	public IList<E> getShortestPath(final IScope scope, final ShortestPathAlgorithm<V, E> algo, final V source,
			final V target) {
		final GraphPath ph = algo.getPath(source, target);
		if (ph == null) return GamaListFactory.create(graph.getGamlType().getKeyType());
		final List re = ph.getEdgeList();
		if (re == null) return GamaListFactory.create(graph.getGamlType().getKeyType());
		return GamaListFactory.create(scope, graph.getGamlType().getContentType(), re);
	}

	/**
	 * Compute best route between.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the i list
	 * @date 30 oct. 2023
	 */
	public IList<E> computeBestRouteBetween(final IScope scope, final V source, final V target) {
		if (source.equals(target)) return GamaListFactory.create(graph.getGamlType().getContentType());
		if (shortestPathMatrix != null) {
			final IList<E> edges = getShortestPathFromMatrix(source, target);
			if (saveComputedShortestPaths) { saveShortestPaths(edges, source, target); }
			return edges;
		}
		if (pathFindingAlgo == shortestPathAlgorithm.FloydWarshall) {
			if (optimizer == null) { optimizer = new FloydWarshallShortestPathsGAMA<>(graph); }
			final GraphPath<V, E> path = optimizer.getShortestPath(source, target);
			if (path == null) return GamaListFactory.create(graph.getGamlType().getContentType());
			return GamaListFactory.create(scope, graph.getGamlType().getContentType(), path.getEdgeList());
		}
		IList<IList<E>> sp = null;
		if (saveComputedShortestPaths) { sp = shortestPathComputed.get(new Pair<>(source, target)); }
		IList<E> spl = null;
		if (sp == null || sp.isEmpty() || sp.get(0).isEmpty()) {
			if (pathFindingAlgo == shortestPathAlgorithm.NBAStar) {
				final NBAStarPathfinder<V, E> p = new NBAStarPathfinder<>(graph, false);
				spl = p.search(source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.NBAStarApprox) {
				final NBAStarPathfinder<V, E> p = new NBAStarPathfinder<>(graph, true);
				spl = p.search(source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.AStar) {
				final AStar<V, E> astarAlgo = new AStar<>(graph, source, target);
				spl = astarAlgo.compute();
			} else if (pathFindingAlgo == shortestPathAlgorithm.Dijkstra) {
				spl = getShortestPath(scope, new DijkstraShortestPath<>(graph), source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.BellmannFord) {
				spl = getShortestPath(scope, new BellmanFordShortestPath<>(graph), source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.DeltaStepping) {
				ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
						.newFixedThreadPool(GamaExecutorService.THREADS_NUMBER.getValue());
				spl = getShortestPath(scope, new DeltaSteppingShortestPath<>(graph, executor), source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.TransitNodeRouting) {
				if (transitNodeRouting == null) {
					ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
							.newFixedThreadPool(GamaExecutorService.THREADS_NUMBER.getValue());
					transitNodeRouting = new TransitNodeRoutingShortestPath<>(graph, executor);
				}
				spl = getShortestPath(scope, transitNodeRouting, source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.CHBidirectionalDijkstra) {
				if (contractionHierarchyBD == null) {
					ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
							.newFixedThreadPool(GamaExecutorService.THREADS_NUMBER.getValue());
					contractionHierarchyBD = new ContractionHierarchyBidirectionalDijkstra<>(graph, executor);
				}
				spl = getShortestPath(scope, contractionHierarchyBD, source, target);
			} else if (pathFindingAlgo == shortestPathAlgorithm.BidirectionalDijkstra) {
				spl = getShortestPath(scope, new BidirectionalDijkstraShortestPath<>(graph), source, target);
			}

			if (saveComputedShortestPaths) { saveShortestPaths(spl, source, target); }

		} else {
			spl = GamaListFactory.create(scope, graph.getGamlType().getContentType(), sp.get(0));
		}
		return spl;
	}

	/**
	 * Save shortest paths.
	 *
	 * @param edges
	 *            the edges
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 */
	private void saveShortestPaths(final List<E> edges, final V source, final V target) {
		V s = source;
		final IList<IList<E>> spl = GamaListFactory.create(Types.LIST.of(graph.getGamlType().getContentType()));
		spl.add(GamaListFactory.createWithoutCasting(graph.getGamlType().getContentType(), edges));
		shortestPathComputed.put(new Pair<>(source, target), spl);
		final List<E> edges2 = GamaListFactory.create(graph.graphScope, graph.getGamlType().getContentType(), edges);
		for (int i = 0; i < edges.size(); i++) {
			final E edge = edges2.remove(0);
			if (edges2.isEmpty()) { break; }
			// DEBUG.LOG("s : " + s + " j : " + j + " i: " + i);
			V nwS = (V) graph.getEdgeTarget(edge);
			if (!graph.directed && nwS.equals(s)) { nwS = (V) graph.getEdgeSource(edge); }
			final Pair<V, V> pp = new Pair<>(nwS, target);
			if (!shortestPathComputed.containsKey(pp)) {
				final IList<IList<E>> spl2 = GamaListFactory.create(graph.getGamlType().getContentType());
				spl2.add(GamaListFactory.createWithoutCasting(graph.getGamlType().getContentType(), edges2));
				shortestPathComputed.put(pp, spl2);
			}
			s = nwS;

		}
	}

	/**
	 * Compute K shortest paths between.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param k
	 *            the k
	 * @return the i list
	 * @date 30 oct. 2023
	 */
	public IList<IPath<V, E, IGraph<V, E>>> computeKShortestPathsBetween(final IScope scope, final V source,
			final V target, final int k) {
		if (!graph.directed && ONLY_FOR_DIRECTED_GRAPH.contains(kPathFindingAlgo)) {
			GamaRuntimeException.error(
					kPathFindingAlgo.name() + " cannot be used for undirected graphs - use the Yen algorithm for that",
					scope);
		}
		final IList<IList<E>> pathLists = computeKBestRoutesBetween(scope, source, target, k);
		final IList<IPath<V, E, IGraph<V, E>>> paths = GamaListFactory.create(Types.PATH);

		for (final IList<E> p : pathLists) {
			if (p == null) { continue; }
			paths.add(graph.pathFromEdges(scope, source, target, p));
		}
		return paths;
	}

	/**
	 * Ge kt shortest path.
	 *
	 * @param scope
	 *            the scope
	 * @param algo
	 *            the algo
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param k
	 *            the k
	 * @param useLinkedGraph
	 *            the use linked graph
	 * @return the i list
	 */
	@SuppressWarnings ("unchecked")
	public IList<IList<E>> geKtShortestPath(final IScope scope, final KShortestPathAlgorithm algo, final V source,
			final V target, final int k, final boolean useLinkedGraph) {
		final List<GraphPath<V, E>> pathsJGT = algo.getPaths(useLinkedGraph ? source.toString() : source,
				useLinkedGraph ? target.toString() : target, k);

		if (pathsJGT == null) return GamaListFactory.create(graph.getGamlType().getContentType());
		final IList<IList<E>> el = GamaListFactory.create(Types.LIST.of(graph.getGamlType().getContentType()));
		final IList<IList<E>> paths = GamaListFactory.create(Types.LIST.of(graph.getGamlType().getContentType()));

		for (final GraphPath p : pathsJGT) {
			IList<E> path = GamaListFactory.create();
			if (useLinkedGraph) {
				for (Object e : p.getEdgeList()) { path.add((E) fromLinkedGtoEdges.get(e)); }
			} else {
				path.addAll(p.getEdgeList());
			}
			paths.add(path);
			if (saveComputedShortestPaths) { el.add(path); }
		}
		if (saveComputedShortestPaths) { shortestPathComputed.put(new Pair<>(source, target), el); }
		return paths;
	}

	/**
	 * Compute K best routes between.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param k
	 *            the k
	 * @return the i list
	 * @date 30 oct. 2023
	 */
	public IList<IList<E>> computeKBestRoutesBetween(final IScope scope, final V source, final V target, final int k) {
		final Pair<V, V> pp = new Pair<>(source, target);
		final IList<IList<E>> sps = shortestPathComputed.get(pp);
		if (sps != null && sps.size() >= k) {
			IList<IList<E>> paths = GamaListFactory.create(Types.LIST.of(graph.getGamlType().getContentType()));
			for (final IList<E> sp : sps) {
				paths.add(GamaListFactory.create(scope, graph.getGamlType().getContentType(), sp));
			}
			return paths;
		}
		IList<IList<E>> paths = GamaListFactory.create(Types.LIST.of(graph.getGamlType().getContentType()));
		if (kPathFindingAlgo == kShortestPathAlgorithm.Yen) {
			paths = geKtShortestPath(scope, new YenKShortestPath<>(graph), source, target, k, false);
		} else if (kPathFindingAlgo == kShortestPathAlgorithm.Bhandari) {
			generateGraph();
			paths = geKtShortestPath(scope, new BhandariKDisjointShortestPaths<>(linkedJGraph), source, target, k,
					true);
		}
		return paths;

	}

	/**
	 * Generate graph.
	 */
	@SuppressWarnings ("unchecked")
	void generateGraph() {
		if (linkedJGraph != null) return;
		fromLinkedGtoEdges = GamaMapFactory.create();
		linkedJGraph = graph.directed
				? new DefaultDirectedGraph(SupplierUtil.createStringSupplier(),
						SupplierUtil.createDefaultWeightedEdgeSupplier(), true)
				: new DefaultUndirectedGraph(SupplierUtil.createStringSupplier(),
						SupplierUtil.createDefaultWeightedEdgeSupplier(), true);
		for (Object v : graph.getVertices()) { linkedJGraph.addVertex(v.toString()); }
		for (Object e : graph.getEdges()) {
			String s = graph.getEdgeSource(e).toString();
			String t = graph.getEdgeTarget(e).toString();
			if (s.equals(t)) { continue; }
			DefaultWeightedEdge de = (DefaultWeightedEdge) linkedJGraph.addEdge(s, t);
			linkedJGraph.setEdgeWeight(s, t, graph.getWeightOf(e));

			fromLinkedGtoEdges.put(de, e);
		}
	}

	/**
	 * Sets the shortest path algorithm.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param s
	 *            the new shortest path algorithm
	 * @date 30 oct. 2023
	 */
	public void setShortestPathAlgorithm(final String s) { pathFindingAlgo = shortestPathAlgorithm.valueOf(s); }

	/**
	 * Sets the k shortest path algorithm.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param s
	 *            the new k shortest path algorithm
	 * @date 30 oct. 2023
	 */
	public void setKShortestPathAlgorithm(final String s) { kPathFindingAlgo = kShortestPathAlgorithm.valueOf(s); }

	/**
	 * Gets the version.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the version
	 * @date 30 oct. 2023
	 */
	public int getVersion() { return version; }

	/**
	 * Sets the version.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param version
	 *            the new version
	 * @date 30 oct. 2023
	 */
	public void setVersion(final int version) {
		this.version = version;
		shortestPathComputed.clear();
	}

	/**
	 * Inc version.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 30 oct. 2023
	 */
	public void incVersion() {
		version++;
		shortestPathComputed.clear();
		contractionHierarchyBD = null;
		transitNodeRouting = null;
		linkedJGraph = null;
		fromLinkedGtoEdges = null;
	}

	/**
	 * Re init path finder.
	 */
	public void reInitPathFinder() {
		optimizer = null;
	}

}