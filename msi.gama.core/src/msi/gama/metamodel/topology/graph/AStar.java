/*********************************************************************************************
 * 
 *
 * 'AStar.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.graph;

import java.util.*;
import msi.gama.metamodel.shape.*;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import msi.gaml.operators.Maths;

/**
 * An implementation of the A* algorithm: use of GraphStream implementation adapted to GAMA (thanks to the Graphstream
 * team !)
 * 
 * <p>
 * A* computes the shortest path from a node to another in a graph. The heuristic used to select a node is the euclidian
 * distance between two nodes. Note it only works for spatial graph. It will fail if the two nodes are in two distinct
 * connected components.
 * </p>
 * 
 * <p>
 * If there are several equivalent shortest paths between the two nodes, the returned one is arbitrary. Therefore this
 * AStar algorithm works with multi-graphs but if two edges between two nodes have the same properties, the one that
 * will be chosen will be arbitrary.
 * </p>
 * 
 * 
 */
public class AStar<V, E> {

	/**
	 * The graph.
	 */
	protected GamaGraph<V, E> graph;

	/**
	 * The source node.
	 */
	protected V source;

	/**
	 * The target node.
	 */
	protected V target;

	/**
	 * The open set.
	 */
	protected GamaMap<V, AStarNode> open = new GamaMap<V, AStarNode>();

	/**
	 * The closed set.
	 */
	protected GamaMap<V, AStarNode> closed = new GamaMap<V, AStarNode>();

	/**
	 * If found the shortest path is stored here.
	 */
	protected List<E> result;

	protected boolean isSpatialGraph;

	/**
	 * Set to false if the algorithm ran, but did not found any path from the
	 * source to the target, or if the algorithm did not run yet.
	 */
	protected boolean pathFound = false;

	/**
	 * New A* algorithm.
	 */
	public AStar() {}

	/**
	 * New A* algorithm on a given graph.
	 * 
	 * @param graph
	 *            The graph where the algorithm will compute paths.
	 */
	public AStar(final GamaGraph<V, E> graph) {
		init(graph);
	}

	/**
	 * New A* algorithm on the given graph.
	 * 
	 * @param graph
	 *            The graph where the algorithm will compute paths.
	 * @param src
	 *            The start node.
	 * @param trg
	 *            The destination node.
	 */
	public AStar(final GamaGraph<V, E> graph, final V src, final V trg) {
		this(graph);
		setSource(src);
		setTarget(trg);
	}

	/**
	 * Change the source node. This clears the already computed path, but
	 * preserves the target node name.
	 * 
	 * @param nodeName
	 *            Identifier of the source node.
	 */
	public void setSource(final V node) {
		clearAll();
		source = node;
	}

	/**
	 * Change the target node. This clears the already computed path, but
	 * preserves the source node name.
	 * 
	 * @param nodeName
	 *            Identifier of the target node.
	 */
	public void setTarget(final V node) {
		clearAll();
		target = node;
	}

	/*
	 * @see
	 * org.graphstream.algorithm.Algorithm#init(org.graphstream.graph.Graph)
	 */
	public void init(final GamaGraph<V, E> graph) {
		clearAll();
		this.graph = graph;
		isSpatialGraph = graph instanceof GamaSpatialGraph;
	}

	/*
	 * @see org.graphstream.algorithm.Algorithm#compute()
	 */
	public void compute() {
		if ( source != null && target != null ) {
			aStar(source, target);
		}
	}

	/**
	 * The computed path, or null if nor result was found.
	 * 
	 * @return The computed path, or null if no path was found.
	 */
	public List<E> getShortestPath() {
		return result;
	}

	/**
	 * After having called {@link #compute()} or {@link #compute(String, String)}, if the {@link #getShortestPath()}
	 * returns null, or this method return true, there is no path from the given
	 * source node to the given target node. In other words, the graph has
	 * several connected components. It also return true if the algorithm did
	 * not run.
	 * 
	 * @return True if there is no possible path from the source to the
	 *         destination or if the algorithm did not run.
	 */
	public boolean noPathFound() {
		return !pathFound;
	}

	/**
	 * Build the shortest path from the target/destination node, following the
	 * parent links.
	 * 
	 * @param target
	 *            The destination node.
	 * @return The path.
	 */
	public List<E> buildPath(final AStarNode target) {
		List<E> path = new GamaList<E>();

		GamaList<AStarNode> thePath = new GamaList<AStarNode>();
		AStarNode node = target;

		while (node != null) {
			thePath.add(node);
			node = node.parent;
		}

		int n = thePath.size();

		if ( n > 1 ) {
			// AStarNode current = thePath.get(n - 1);
			AStarNode follow = thePath.get(n - 2);

			path.add(follow.edge);

			// current = follow;

			for ( int i = n - 3; i >= 0; i-- ) {
				follow = thePath.get(i);
				path.add(follow.edge);
				// current = follow;
			}
		}

		return path;
	}

	public void compute(final V source, final V target) {
		setSource(source);
		setTarget(target);
		compute();
	}

	protected void clearAll() {
		open.clear();
		closed.clear();

		result = null;
		pathFound = false;
	}

	protected void aStar(final V sourceNode, final V targetNode) {
		clearAll();
		open.put(sourceNode, new AStarNode(sourceNode, null, null, 0, heuristic(sourceNode, targetNode)));

		pathFound = false;

		while (!open.isEmpty()) {
			AStarNode current = getNextBetterNode();

			assert current != null;

			if ( current.node == targetNode ) {
				// We found it !

				assert current.edge != null;
				pathFound = true;
				result = buildPath(current);

				return;
			} else {
				open.remove(current.node);
				closed.put(current.node, current);
				_Vertex<V, E> node = graph.getVertex(current.node);

				// For each successor of the current node :
				Set<E> edges = node.getOutEdges();
				if ( !graph.isDirected() ) {
					edges.addAll(node.getInEdges());
				}
				for ( E edge : edges ) {
					_Edge<V, E> eg = graph.getEdge(edge);

					V next = (V) eg.getOther(current.node);
					double h = heuristic(next, targetNode);
					double g = current.g + eg.getWeight();
					double f = g + h;
					// If the node is already in open with a better rank, we
					// skip it.

					AStarNode alreadyInOpen = open.get(next);

					if ( alreadyInOpen != null && alreadyInOpen.rank <= f ) {
						continue;
					}

					// If the node is already in closed with a better rank; we
					// skip it.

					AStarNode alreadyInClosed = closed.get(next);

					if ( alreadyInClosed != null && alreadyInClosed.rank <= f ) {
						continue;
					}

					closed.remove(next);
					open.put(next, new AStarNode(next, edge, current, g, h));
				}
			}
		}
	}

	protected double heuristic(final Object node1, final Object node2) {
		if ( isSpatialGraph ) {
			GamaPoint pt1 = (GamaPoint) ((IShape) node1).getLocation();
			GamaPoint pt2 = (GamaPoint) ((IShape) node2).getLocation();
			return Maths.hypot(pt1.x, pt2.x, pt1.y, pt2.y);

		}
		// return ((ILocation) node1).euclidianDistanceTo((ILocation)node2);
		// return ((IShape) node1).euclidianDistanceTo((IShape)node2);
		return 0;
	}

	/**
	 * Find the node with the lowest rank in the open list.
	 * 
	 * @return The node of open that has the lowest rank.
	 */
	protected AStarNode getNextBetterNode() {
		// TODO: consider using a priority queue here ?
		// The problem is that we use open has a hash to ensure
		// a node we will add to to open is not yet in it.

		double min = Float.MAX_VALUE;
		AStarNode theChosenOne = null;

		for ( AStarNode node : open.values() ) {
			if ( node.rank < min ) {
				theChosenOne = node;
				min = node.rank;
			}
		}

		return theChosenOne;
	}

	// Nested classes

	/**
	 * Representation of a node in the A* algorithm.
	 * 
	 * <p>
	 * This representation contains :
	 * <ul>
	 * <li>the node itself;</li>
	 * <li>its parent node (to reconstruct the path);</li>
	 * <li>the g value (cost from the source to this node);</li>
	 * <li>the h value (estimated cost from this node to the target);</li>
	 * <li>the f value or rank, the sum of g and h.</li>
	 * </ul>
	 * </p>
	 */
	protected class AStarNode {

		/**
		 * The node.
		 */
		public V node;

		/**
		 * The node's parent.
		 */
		public AStarNode parent;

		/**
		 * The edge used to go from parent to node.
		 */
		public E edge;

		/**
		 * Cost from the source node to this one.
		 */
		public double g;

		/**
		 * Estimated cost from this node to the destination.
		 */
		public double h;

		/**
		 * Sum of g and h.
		 */
		public double rank;

		/**
		 * New A* node.
		 * 
		 * @param node
		 *            The node.
		 * @param edge
		 *            The edge used to go from parent to node (useful for
		 *            multi-graphs).
		 * @param parent
		 *            It's parent node.
		 * @param g
		 *            The cost from the source to this node.
		 * @param h
		 *            The estimated cost from this node to the target.
		 */
		public AStarNode(final V node, final E edge, final AStarNode parent, final double g, final double h) {
			this.node = node;
			this.edge = edge;
			this.parent = parent;
			this.g = g;
			this.h = h;
			this.rank = g + h;
		}
	}
}