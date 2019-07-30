/*
 * Copyright 2006 - 2016 Stefan Balev <stefan.balev@graphstream-project.org> Julien Baudry
 * <julien.baudry@graphstream-project.org> Antoine Dutot <antoine.dutot@graphstream-project.org> Yoann Pigné
 * <yoann.pigne@graphstream-project.org> Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 *
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic graph, create them from scratch, file or any
 * source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the CeCILL-C license that fits European
 * law, and the GNU Lesser General Public License. You can use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following URL <http://www.cecill.info> or under
 * the terms of the GNU LGPL as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C and LGPL licenses and
 * that you accept their terms.
 */
package msi.gama.ext.graphstream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p>
 * A lightweight graph class intended to allow the construction of big graphs (millions of elements).
 * </p>
 *
 * <p>
 * The main purpose here is to minimize memory consumption even if the management of such a graph implies more CPU
 * consuming. See the <code>complexity</code> tags on each method so as to figure out the impact on the CPU.
 * </p>
 */
public class AdjacencyListGraph extends AbstractGraph {

	public static final double GROW_FACTOR = 1.1;
	public static final int DEFAULT_NODE_CAPACITY = 128;
	public static final int DEFAULT_EDGE_CAPACITY = 1024;

	protected Map<String, AbstractNode> nodeMap;
	protected Map<String, AbstractEdge> edgeMap;

	protected AbstractNode[] nodeArray;
	protected AbstractEdge[] edgeArray;

	protected int nodeCount;
	protected int edgeCount;

	// *** Constructors ***

	/**
	 * Creates an empty graph.
	 *
	 * @param id
	 *            Unique identifier of the graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are automatically created when referenced when creating
	 *            a edge, even if not yet inserted in the graph.
	 * @param initialNodeCapacity
	 *            Initial capacity of the node storage data structures. Use this if you know the approximate maximum
	 *            number of nodes of the graph. The graph can grow beyond this limit, but storage reallocation is
	 *            expensive operation.
	 * @param initialEdgeCapacity
	 *            Initial capacity of the edge storage data structures. Use this if you know the approximate maximum
	 *            number of edges of the graph. The graph can grow beyond this limit, but storage reallocation is
	 *            expensive operation.
	 */
	public AdjacencyListGraph(final String id, final boolean strictChecking, final boolean autoCreate,
			final int nodeCapacity, final int edgeCapacity) {
		super(id, strictChecking, autoCreate);
		int initialNodeCapacity = nodeCapacity;
		int initialEdgeCapacity = edgeCapacity;

		setNodeFactory((id1, graph) -> new AdjacencyListNode((AbstractGraph) graph, id1));
		setEdgeFactory(
				(id1, src, dst, directed) -> new AbstractEdge(id1, (AbstractNode) src, (AbstractNode) dst, directed));

		if (initialNodeCapacity < DEFAULT_NODE_CAPACITY) {
			initialNodeCapacity = DEFAULT_NODE_CAPACITY;
		}
		if (initialEdgeCapacity < DEFAULT_EDGE_CAPACITY) {
			initialEdgeCapacity = DEFAULT_EDGE_CAPACITY;
		}

		nodeMap = new HashMap<>();
		edgeMap = new HashMap<>();
		nodeArray = new AbstractNode[initialNodeCapacity];
		edgeArray = new AbstractEdge[initialEdgeCapacity];
		nodeCount = edgeCount = 0;
	}

	/**
	 * Creates an empty graph with default edge and node capacity.
	 *
	 * @param id
	 *            Unique identifier of the graph.
	 * @param strictChecking
	 *            If true any non-fatal error throws an exception.
	 * @param autoCreate
	 *            If true (and strict checking is false), nodes are automatically created when referenced when creating
	 *            a edge, even if not yet inserted in the graph.
	 */
	public AdjacencyListGraph(final String id, final boolean strictChecking, final boolean autoCreate) {
		this(id, strictChecking, autoCreate, DEFAULT_NODE_CAPACITY, DEFAULT_EDGE_CAPACITY);
	}

	/**
	 * Creates an empty graph with strict checking and without auto-creation.
	 *
	 * @param id
	 *            Unique identifier of the graph.
	 */
	public AdjacencyListGraph(final String id) {
		this(id, true, false);
	}

	// *** Callbacks ***

	@Override
	protected void addEdgeCallback(final AbstractEdge edge) {
		edgeMap.put(edge.getId(), edge);
		if (edgeCount == edgeArray.length) {
			final AbstractEdge[] tmp = new AbstractEdge[(int) (edgeArray.length * GROW_FACTOR) + 1];
			System.arraycopy(edgeArray, 0, tmp, 0, edgeArray.length);
			Arrays.fill(edgeArray, null);
			edgeArray = tmp;
		}
		edgeArray[edgeCount] = edge;
		edge.setIndex(edgeCount++);
	}

	@Override
	protected void addNodeCallback(final AbstractNode node) {
		nodeMap.put(node.getId(), node);
		if (nodeCount == nodeArray.length) {
			final AbstractNode[] tmp = new AbstractNode[(int) (nodeArray.length * GROW_FACTOR) + 1];
			System.arraycopy(nodeArray, 0, tmp, 0, nodeArray.length);
			Arrays.fill(nodeArray, null);
			nodeArray = tmp;
		}
		nodeArray[nodeCount] = node;
		node.setIndex(nodeCount++);
	}

	@Override
	protected void removeEdgeCallback(final AbstractEdge edge) {
		edgeMap.remove(edge.getId());
		final int i = edge.getIndex();
		edgeArray[i] = edgeArray[--edgeCount];
		edgeArray[i].setIndex(i);
		edgeArray[edgeCount] = null;
	}

	@Override
	protected void removeNodeCallback(final AbstractNode node) {
		nodeMap.remove(node.getId());
		final int i = node.getIndex();
		nodeArray[i] = nodeArray[--nodeCount];
		nodeArray[i].setIndex(i);
		nodeArray[nodeCount] = null;
	}

	@Override
	protected void clearCallback() {
		nodeMap.clear();
		edgeMap.clear();
		Arrays.fill(nodeArray, 0, nodeCount, null);
		Arrays.fill(edgeArray, 0, edgeCount, null);
		nodeCount = edgeCount = 0;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends Edge> T getEdge(final String id) {
		return (T) edgeMap.get(id);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends Edge> T getEdge(final int index) {
		if (index < 0 || index >= edgeCount) {
			throw new IndexOutOfBoundsException("Edge " + index + " does not exist");
		}
		return (T) edgeArray[index];
	}

	@Override
	public int getEdgeCount() {
		return edgeCount;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends Node> T getNode(final String id) {
		return (T) nodeMap.get(id);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends Node> T getNode(final int index) {
		if (index < 0 || index > nodeCount) {
			throw new IndexOutOfBoundsException("Node " + index + " does not exist");
		}
		return (T) nodeArray[index];
	}

	@Override
	public int getNodeCount() {
		return nodeCount;
	}

	// *** Iterators ***

	protected class EdgeIterator<T extends Edge> implements Iterator<T> {
		int iNext = 0;
		int iPrev = -1;

		@Override
		public boolean hasNext() {
			return iNext < edgeCount;
		}

		@Override
		@SuppressWarnings ("unchecked")
		public T next() {
			if (iNext >= edgeCount) { throw new NoSuchElementException(); }
			iPrev = iNext++;
			return (T) edgeArray[iPrev];
		}

		@Override
		public void remove() {
			if (iPrev == -1) { throw new IllegalStateException(); }
			removeEdge(edgeArray[iPrev], true, true, true);
			iNext = iPrev;
			iPrev = -1;
		}
	}

	protected class NodeIterator<T extends Node> implements Iterator<T> {

		public void reset() {
			iNext = 0;
			iPrev = -1;
		}

		int iNext = 0;
		int iPrev = -1;

		@Override
		public boolean hasNext() {
			return iNext < nodeCount;
		}

		@Override
		@SuppressWarnings ("unchecked")
		public T next() {
			if (iNext >= nodeCount) { throw new NoSuchElementException(); }
			iPrev = iNext++;
			return (T) nodeArray[iPrev];
		}

		@Override
		public void remove() {
			if (iPrev == -1) { throw new IllegalStateException(); }
			removeNode(nodeArray[iPrev], true);
			iNext = iPrev;
			iPrev = -1;
		}
	}

	@Override
	public <T extends Edge> Iterator<T> getEdgeIterator() {
		return new EdgeIterator<>();
	}

	NodeIterator nodeIterator;

	@SuppressWarnings ("unchecked")
	@Override
	public <T extends Node> Iterator<T> getNodeIterator() {
		if (nodeIterator == null) {
			nodeIterator = new NodeIterator<>();
		} else {
			nodeIterator.reset();
		}
		return nodeIterator;
	}

	/*
	 * For performance tuning
	 *
	 * @return the number of allocated but unused array elements public int getUnusedArrayElements() { int count = 0;
	 * count += edgeArray.length - edgeCount; count += nodeArray.length - nodeCount; for (ALNode n : this.<ALNode>
	 * getEachNode()) count += n.edges.length - n.degree; return count; }
	 */
}
