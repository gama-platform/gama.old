package msi.gama.metamodel.topology.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.graph.IGraph;

/**
 * An implementation of the A* algorithm: use of GraphStream implementation adapted to GAMA (thanks to the Graphstream team !)
 * 
 * <p>
 * A* computes the shortest path from a node to another in a graph. The heuristic used to select a node 
 * is the euclidian distance between two nodes. Note it only works for spatial graph. It will fail if the two nodes are in
 * two distinct connected components.
 * </p>
 * 
 * <p>
 * If there are several equivalent shortest paths between the two nodes, the returned
 * one is arbitrary. Therefore this AStar algorithm works with multi-graphs but if two
 * edges between two nodes have the same properties, the one that will be chosen will
 * be arbitrary. 
 * </p>
 * 
 * 
 */
public class AStar<V,E> {
	/**
	 * The graph.
	 */
	protected IGraph<V,E> graph;

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
	protected HashMap<V, AStarNode> closed = new HashMap<V, AStarNode>();

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
	public AStar() {
	}

	/**
	 * New A* algorithm on a given graph.
	 * 
	 * @param graph
	 *            The graph where the algorithm will compute paths.
	 */
	public AStar(IGraph<V,E> graph) {
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
	public AStar(IGraph<V,E> graph, V src, V trg) {
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
	public void setSource(V node) {
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
	public void setTarget(V node) {
		clearAll();
		target = node;
	}


	/*
	 * @see
	 * org.graphstream.algorithm.Algorithm#init(org.graphstream.graph.Graph)
	 */
	public void init(IGraph<V,E> graph) {
		clearAll();
		this.graph = graph;
		isSpatialGraph = graph instanceof ISpatialGraph;
	}

	/*
	 * @see org.graphstream.algorithm.Algorithm#compute()
	 */
	public void compute() {
		if (source != null && target != null) {
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
	 * After having called {@link #compute()} or
	 * {@link #compute(String, String)}, if the {@link #getShortestPath()}
	 * returns null, or this method return true, there is no path from the given
	 * source node to the given target node. In other words, the graph has
	 * several connected components. It also return true if the algorithm did
	 * not run.
	 * 
	 * @return True if there is no possible path from the source to the
	 *         destination or if the algorithm did not run.
	 */
	public boolean noPathFound() {
		return (! pathFound);
	}

	/**
	 * Build the shortest path from the target/destination node, following the
	 * parent links.
	 * 
	 * @param target
	 *            The destination node.
	 * @return The path.
	 */
	public List<E> buildPath(AStarNode target) {
		List<E> path = new GamaList<E>();

		ArrayList<AStarNode> thePath = new ArrayList<AStarNode>();
		AStarNode node = target;

		while (node != null) {
			thePath.add(node);
			node = node.parent;
		}

		int n = thePath.size();

		if (n > 1) {
			//AStarNode current = thePath.get(n - 1);
			AStarNode follow = thePath.get(n - 2);

			path.add(follow.edge);

			//current = follow;

			for (int i = n - 3; i >= 0; i--) {
				follow = thePath.get(i);
				path.add(follow.edge);
			//	current = follow;
			}
		}

		return path;
	}

	public void compute(V source, V target) {
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

	protected void aStar(V sourceNode, V targetNode) {
		clearAll();
		open.put(
				sourceNode,
				new AStarNode(sourceNode, null, null, 0, heuristic(
						sourceNode, targetNode)));

		pathFound = false;

		while (!open.isEmpty()) {
			AStarNode current = getNextBetterNode();

			assert (current != null);

			if (current.node == targetNode) {
				// We found it !
				assert current.edge != null;
				pathFound = true;
				result = buildPath(current);
				return;
			} else {
				open.remove(current.node);
				closed.put(current.node, current);

				// For each successor of the current node :
				Set<E> edges = graph.outgoingEdgesOf(current.node);
				if (! graph.isDirected()) edges.addAll(graph.incomingEdgesOf(current.node));
				
				for (E edge : edges){
					V next = getOpposite(current.node,edge);
					double h = heuristic(next, targetNode);
					double g = current.g + graph.getEdgeWeight(edge);
					double f = g + h;

					// If the node is already in open with a better rank, we
					// skip it.

					AStarNode alreadyInOpen = open.get(next);

					if (alreadyInOpen != null && alreadyInOpen.rank <= f)
						continue;

					// If the node is already in closed with a better rank; we
					// skip it.

					AStarNode alreadyInClosed = closed.get(next);

					if (alreadyInClosed != null && alreadyInClosed.rank <= f)
						continue;

					closed.remove(next);
					open.put(next, new AStarNode(next, edge, current, g, h));
				}
			}
		}
	}
	
	protected V getOpposite(V node, E edge) {
		V s = graph.getEdgeSource(edge);
		if (s == node)
			return graph.getEdgeTarget(edge);
		return s;
	}
	protected double heuristic (Object node1, Object node2) {
		if (isSpatialGraph)
			return ((IShape) node1).euclidianDistanceTo((IShape)node2);
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

		for (AStarNode node : open.values()) {
			if (node.rank < min) {
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
		public AStarNode(V node, E edge, AStarNode parent, double g,
				double h) {
			this.node = node;
			this.edge = edge;
			this.parent = parent;
			this.g = g;
			this.h = h;
			this.rank = g + h;
		}
	}
}