package msi.gama.metamodel.topology.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph._Edge;
import msi.gama.util.graph._Vertex;

/**
 * This pathfinding algorithm is due to Wim Pijls and Henk Post in "Yet another
 * bidirectional algorithm for shortest paths." 15 June 2009.
 * <p>
 * <b>This class is not thread-safe.</b> If you need it in different threads,
 * make sure each thread has its own object of this class.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Oct 13, 2016)
 */
public final class NBAStarPathfinder<V, E> {

	private final PriorityQueue<HeapEntry<V>> OPENA = new PriorityQueue<>();
	private final PriorityQueue<HeapEntry<V>> OPENB = new PriorityQueue<>();
	private final Map<V, V> PARENTSA = new HashMap<>();
	private final Map<V, V> PARENTSB = new HashMap<>();
	private final Map<V, Double> DISTANCEA = new HashMap<>();
	private final Map<V, Double> DISTANCEB = new HashMap<>();
	private final Set<V> CLOSED = new HashSet<>();
	private final Map<V, _Vertex<V, E>> vertices = new IdentityHashMap<>();


	private double fA;
	private double fB;
	private double bestPathLength;
	private V touchNode;
	private V sourceNode;
	private V targetNode;

	protected GamaGraph<V, E> graph;
	protected boolean isSpatialGraph;

	public NBAStarPathfinder(GamaGraph<V, E> graph) {
		this.graph = graph;
		isSpatialGraph = graph instanceof GamaSpatialGraph;
	}

	public IList<E> search(V sourceNode, V targetNode) {
		if (sourceNode.equals(targetNode)) {
			return GamaListFactory.create();
		}

		init(sourceNode, targetNode);

		while (!OPENA.isEmpty() && !OPENB.isEmpty()) {
			if (OPENA.size() < OPENB.size()) {
				expandInForwardDirection();
			} else {
				expandInBackwardDirection();
			}
		}

		if (touchNode == null) {
			return GamaListFactory.create();
		}

		return tracebackPath();
	}

	private void expandInForwardDirection() {
		V currentNode = OPENA.remove().getNode();
		if (CLOSED.contains(currentNode)) {
			return;
		}
		_Vertex<V, E> cv = graph.getVertex(currentNode);
		vertices.put(currentNode, cv);
		CLOSED.add(currentNode);

		if (DISTANCEA.get(currentNode)
				+ estimateDistanceBetween(currentNode, targetNode) >= bestPathLength
				|| DISTANCEA.get(currentNode) + fB
						- estimateDistanceBetween(currentNode, sourceNode) >= bestPathLength) {
			// Reject the 'currentNode'.
		} else {
			// Stabilize the 'currentNode'.
			Set<Object> edges = graph.isDirected() ? cv.getOutEdges() : cv.getEdges();
			for (Object edge :edges) {
				final _Edge<V, E> eg = graph.getEdge(edge);
				final V childNode =  (V)(graph.isDirected() ? eg.getTarget() : ((eg.getTarget().equals(currentNode) ? eg.getSource() : eg.getTarget())));
				if (CLOSED.contains(childNode)) {
					continue;
				}
				double tentativeDistance = DISTANCEA.get(currentNode) +eg.getWeight();
				if (!DISTANCEA.containsKey(childNode) || DISTANCEA.get(childNode) > tentativeDistance) {
					
					DISTANCEA.put(childNode, tentativeDistance);
					PARENTSA.put(childNode, currentNode);
					HeapEntry e = new HeapEntry(childNode,
							tentativeDistance + estimateDistanceBetween(childNode, targetNode));
					OPENA.add(e);

					if (DISTANCEB.containsKey(childNode)) {
						double pathLength = tentativeDistance + DISTANCEB.get(childNode);
						if (bestPathLength > pathLength) {
							bestPathLength = pathLength;
							touchNode = childNode;
						}
					}
				}
			}
		}

		if (!OPENA.isEmpty()) {
			fA = OPENA.peek().getDistance();
		}
	}
	
	private void expandInBackwardDirection() {
		V currentNode = OPENB.remove().getNode();
		
		if (CLOSED.contains(currentNode)) {
			return;
		}
		_Vertex<V, E> cv = graph.getVertex(currentNode);
		vertices.put(currentNode, cv);
		
		CLOSED.add(currentNode);

		if (DISTANCEB.get(currentNode)
				+ estimateDistanceBetween(currentNode, sourceNode) >= bestPathLength
				|| DISTANCEB.get(currentNode) + fA
						- estimateDistanceBetween(currentNode, targetNode) >= bestPathLength) {
			// Reject the node 'currentNode'.
		} else {
			Set<Object> edges = graph.isDirected() ? cv.getInEdges() : cv.getEdges();
			for (Object edge :edges) {
				final _Edge<V, E> eg = graph.getEdge(edge);
				final V parentNode = (V)(graph.isDirected() ? eg.getSource(): ( (eg.getSource().equals(currentNode) ? eg.getTarget() : eg.getSource())));
				
				if (CLOSED.contains(parentNode)) {
					continue;
				}

				double tentativeDistance = DISTANCEB.get(currentNode) + eg.getWeight();
				if (!DISTANCEB.containsKey(parentNode) || DISTANCEB.get(parentNode) > tentativeDistance) {
					DISTANCEB.put(parentNode, tentativeDistance);
					PARENTSB.put(parentNode, currentNode);
					HeapEntry e = new HeapEntry(parentNode,
							tentativeDistance + estimateDistanceBetween(parentNode, sourceNode));
					OPENB.add(e);

					if (DISTANCEA.containsKey(parentNode)) {
						double pathLength = tentativeDistance + DISTANCEA.get(parentNode);

						if (bestPathLength > pathLength) {
							bestPathLength = pathLength;
							touchNode = parentNode;
						}
					}
				}
				
			}
		}

		if (!OPENB.isEmpty()) {
			fB = OPENB.peek().getDistance();
		}
	}
	
	

	private void init(V sourceNode, V targetNode) {
		OPENA.clear();
		OPENB.clear();
		PARENTSA.clear();
		PARENTSB.clear();
		DISTANCEA.clear();
		DISTANCEB.clear();
		CLOSED.clear();

		double totalDistance = estimateDistanceBetween(sourceNode, targetNode);

		fA = totalDistance;
		fB = totalDistance;
		bestPathLength = Double.MAX_VALUE;
		touchNode = null;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;

		OPENA.add(new HeapEntry(sourceNode, fA));
		OPENB.add(new HeapEntry(targetNode, fB));
		PARENTSA.put(sourceNode, null);
		PARENTSB.put(targetNode, null);
		DISTANCEA.put(sourceNode, 0.0);
		DISTANCEB.put(targetNode, 0.0);
	}

	/**
	 * Reconstructs a shortest path from the data structures maintained by a
	 * <b>bidirectional</b> pathfinding algorithm.
	 * @return the shortest path object.
	 */
	protected IList<E> tracebackPath() {
		List<V> path = new ArrayList<>();
		V currentNodeId = touchNode;

		while (currentNodeId != null) {
			path.add(currentNodeId);
			currentNodeId = PARENTSA.get(currentNodeId);
		}

		Collections.<V> reverse(path);

		if (PARENTSB != null) {
			currentNodeId = PARENTSB.get(touchNode);

			while (currentNodeId != null) {
				path.add(currentNodeId);
				currentNodeId = PARENTSB.get(currentNodeId);
			}
		}
		final IList<E> edgePath = GamaListFactory.create();
		V cn = path.get(0);
		for (int i = 1; i < path.size(); i ++) {
			V tn = path.get(i);
			List<E> edges = new ArrayList<>(vertices.get(cn).edgesTo(tn)) ;
			if (!graph.isDirected()) edges.addAll(vertices.get(tn).edgesTo(cn));
			if (edges.size() == 1 ) {
				edgePath.add(edges.get(0));
			} else if (edges.size() > 1 ){
				double minV = Double.MAX_VALUE;
				E minE = null;
				for (E e : edges) {
					double w = graph.getEdgeWeight(e);
					if (w < minV) {
						minV = w;
						minE = e;
					}
				}
				edgePath.add(minE);
			}
			
			cn = tn;
		}
		return edgePath;
	}

	/**
	 * This class implements an entry for {@link java.util.PriorityQueue}.
	 *
	 * @author Rodion "rodde" Efremov
	 * @version 1.6 (Oct 13, 2016)
	 */
	final class HeapEntry<V> implements Comparable<HeapEntry> {

		private final V nodeId;
		private final double distance; // The priority key.

		public HeapEntry(V nodeId, double distance) {
			this.nodeId = nodeId;
			this.distance = distance;
		}

		public V getNode() {
			return nodeId;
		}

		public double getDistance() {
			return distance;
		}

		@Override
		public int compareTo(HeapEntry o) {
			return Double.compare(distance, o.distance);
		}
	}

	public double estimateDistanceBetween(V node1, V node2) {
		if (isSpatialGraph) {
			final GamaPoint pt1 = (GamaPoint) ((IShape) node1).getLocation();
			final GamaPoint pt2 = (GamaPoint) ((IShape) node2).getLocation();
			return pt1.distance(pt2);

		}
		return 0;
	}
}
