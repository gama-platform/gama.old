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
package msi.gama.util.graph.graphstream_copy;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * This class provides a basic implementation of {@link msi.gama.util.graph.graphstream_copy.Graph} interface, to
 * minimize the effort required to implement this interface. It provides event management implementing all the methods
 * of {@link msi.gama.util.graph.graphstream_copy.Pipe}. It also manages strict checking and auto-creation policies, as
 * well as other services as displaying, reading and writing.
 * </p>
 *
 * <p>
 * Subclasses have to maintain data structures allowing to efficiently access graph elements by their id or index and
 * iterating on them. They also have to maintain coherent indices of the graph elements. When AbstractGraph decides to
 * add or remove elements, it calls one of the "callbacks" {@link #addNodeCallback(AbstractNode)},
 * {@link #addEdgeCallback(AbstractEdge)}, {@link #removeNodeCallback(AbstractNode)},
 * {@link #removeEdgeCallback(AbstractEdge)}, {@link #clearCallback()}. The role of these callbacks is to update the
 * data structures and to re-index elements if necessary.
 * </p>
 */
public abstract class AbstractGraph extends AbstractElement implements Graph {
	// *** Fields ***

	private boolean strictChecking;
	private boolean autoCreate;
	GraphListeners listeners;
	private NodeFactory<? extends AbstractNode> nodeFactory;
	private EdgeFactory<? extends AbstractEdge> edgeFactory;

	private double step = 0;

	private boolean nullAttributesAreErrors;

	private final long replayId = 0;

	// *** Constructors ***

	/**
	 * The same as {@code AbstractGraph(id, true, false)}
	 *
	 * @param id
	 *            Identifier of the graph
	 * @see #AbstractGraph(String, boolean, boolean)
	 */
	public AbstractGraph(final String id) {
		this(id, true, false);
	}

	/**
	 * Creates a new graph. Subclasses must create their node and edge factories and initialize their data structures in
	 * their constructors.
	 *
	 * @param id
	 * @param strictChecking
	 * @param autoCreate
	 */
	public AbstractGraph(final String id, final boolean strictChecking, final boolean autoCreate) {
		super(id);

		this.strictChecking = strictChecking;
		this.autoCreate = autoCreate;
		this.listeners = new GraphListeners(this);
	}

	// *** Inherited from abstract element

	@Override
	protected void attributeChanged(final AttributeChangeEvent event, final String attribute, final Object oldValue,
			final Object newValue) {
		listeners.sendAttributeChangedEvent(id, SourceBase.ElementType.GRAPH, attribute, event, oldValue, newValue);
	}

	@Override
	public boolean nullAttributesAreErrors() {
		return nullAttributesAreErrors;
	}

	// *** Inherited from graph ***

	// some helpers

	// get node / edge by its id/index

	@Override
	public abstract <T extends Node> T getNode(String id);

	@Override
	public abstract <T extends Node> T getNode(int index);

	@Override
	public abstract <T extends Edge> T getEdge(String id);

	@Override
	public abstract <T extends Edge> T getEdge(int index);

	// node and edge count, iterators and views

	@Override
	public abstract int getNodeCount();

	@Override
	public abstract int getEdgeCount();

	@Override
	public abstract <T extends Node> Iterator<T> getNodeIterator();

	@Override
	public abstract <T extends Edge> Iterator<T> getEdgeIterator();

	/**
	 * This implementation uses {@link #getNodeIterator()}
	 *
	 * @see msi.gama.util.graph.graphstream_copy.Graph#getEachNode()
	 */
	@Override
	public <T extends Node> Iterable<? extends T> getEachNode() {
		return () -> getNodeIterator();
	}

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 *
	 * @see msi.gama.util.graph.graphstream_copy.Graph#getEachEdge()
	 */
	@Override
	public <T extends Edge> Iterable<? extends T> getEachEdge() {
		return () -> getEdgeIterator();
	}

	/**
	 * This implementation uses {@link #getNodeIterator()} and {@link #getNodeCount()}
	 *
	 * @see msi.gama.util.graph.graphstream_copy.Graph#getNodeSet()
	 */
	@Override
	public <T extends Node> Collection<T> getNodeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				return getNodeIterator();
			}

			@Override
			public int size() {
				return getNodeCount();
			}
		};
	}

	/**
	 * This implementation uses {@link #getEdgeIterator()} and {@link #getEdgeCount()}
	 *
	 * @see msi.gama.util.graph.graphstream_copy.Graph#getNodeSet()
	 */
	@Override
	public <T extends Edge> Collection<T> getEdgeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				return getEdgeIterator();
			}

			@Override
			public int size() {
				return getEdgeCount();
			}
		};
	}

	/**
	 * This implementation returns {@link #getNodeIterator()}
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Node> iterator() {
		return getNodeIterator();
	}

	// Factories

	@Override
	public NodeFactory<? extends Node> nodeFactory() {
		return nodeFactory;
	}

	@Override
	public EdgeFactory<? extends Edge> edgeFactory() {
		return edgeFactory;
	}

	@Override
	@SuppressWarnings ("unchecked")
	public void setNodeFactory(final NodeFactory<? extends Node> nf) {
		nodeFactory = (NodeFactory<? extends AbstractNode>) nf;
	}

	@Override
	@SuppressWarnings ("unchecked")
	public void setEdgeFactory(final EdgeFactory<? extends Edge> ef) {
		edgeFactory = (EdgeFactory<? extends AbstractEdge>) ef;
	}

	// strict checking, autocreation, etc

	@Override
	public boolean isStrict() {
		return strictChecking;
	}

	@Override
	public boolean isAutoCreationEnabled() {
		return autoCreate;
	}

	@Override
	public double getStep() {
		return step;
	}

	@Override
	public void setNullAttributesAreErrors(final boolean on) {
		nullAttributesAreErrors = on;
	}

	@Override
	public void setStrict(final boolean on) {
		strictChecking = on;
	}

	@Override
	public void setAutoCreate(final boolean on) {
		autoCreate = on;
	}

	@Override
	public void stepBegins(final double time) {
		listeners.sendStepBegins(time);
		this.step = time;
	}

	// adding and removing elements

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#clear()
	 */
	@Override
	public void clear() {
		listeners.sendGraphCleared();

		final Iterator<AbstractNode> it = getNodeIterator();

		while (it.hasNext()) {
			it.next().clearCallback();
		}

		clearCallback();
		clearAttributesWithNoEvent();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addNode(java.lang.String)
	 */
	@Override
	@SuppressWarnings ("unchecked")
	public <T extends Node> T addNode(final String id) {
		AbstractNode node = getNode(id);

		if (node != null) {
			if (strictChecking) {
				throw new IdAlreadyInUseException("id \"" + id + "\" already in use. Cannot create a node.");
			}
			return (T) node;
		}

		node = nodeFactory.newInstance(id, this);
		addNodeCallback(node);

		listeners.sendNodeAdded(id);

		return (T) node;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public <T extends Edge> T addEdge(final String id, final String node1, final String node2) {
		return addEdge(id, node1, node2, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public <T extends Edge> T addEdge(final String id, final String from, final String to, final boolean directed) {
		return addEdge(id, (AbstractNode) getNode(from), from, (AbstractNode) getNode(to), to, directed);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, int, int)
	 */
	@Override
	public <T extends Edge> T addEdge(final String id, final int index1, final int index2) {
		return addEdge(id, index1, index2, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, int, int, boolean)
	 */
	@Override
	public <T extends Edge> T addEdge(final String id, final int fromIndex, final int toIndex, final boolean directed) {
		return addEdgeToNodes(id, getNode(fromIndex), getNode(toIndex), directed);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, org.graphstream.graph.Node,
	 * org.graphstream.graph.Node)
	 */
	@Override
	public <T extends Edge> T addEdge(final String id, final Node node1, final Node node2) {
		return addEdgeToNodes(id, node1, node2, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#addEdge(java.lang.String, org.graphstream.graph.Node,
	 * org.graphstream.graph.Node, boolean)
	 */
	@Override
	public <T extends Edge> T addEdgeToNodes(final String id, final Node from, final Node to, final boolean directed) {
		return addEdge(id, (AbstractNode) from, from.getId(), (AbstractNode) to, to.getId(), directed);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeNode(java.lang.String)
	 */
	@Override
	public <T extends Node> T removeNode(final String id) {
		final AbstractNode node = getNode(id);

		if (node == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("Node \"" + id + "\" not found. Cannot remove it.");
			}
			return null;
		}

		return removeNode(node);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeNode(int)
	 */
	@Override
	public <T extends Node> T removeNode(final int index) {
		final Node node = getNode(index);

		if (node == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("Node #" + index + " not found. Cannot remove it.");
			}
			return null;
		}

		return removeNode(node);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeNode(org.graphstream.graph.Node)
	 */
	@Override
	@SuppressWarnings ("unchecked")
	public <T extends Node> T removeNode(final Node node) {
		if (node == null) { return null; }

		removeNode((AbstractNode) node, true);
		return (T) node;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(java.lang.String)
	 */
	@Override
	public <T extends Edge> T removeEdge(final String id) {
		final Edge edge = getEdge(id);

		if (edge == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("Edge \"" + id + "\" not found. Cannot remove it.");
			}
			return null;
		}

		return removeEdgeObject(edge);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(int)
	 */
	@Override
	public <T extends Edge> T removeEdge(final int index) {
		final Edge edge = getEdge(index);

		if (edge == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("Edge #" + index + " not found. Cannot remove it.");
			}
			return null;
		}

		return removeEdgeObject(edge);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Edge)
	 */
	@Override
	@SuppressWarnings ("unchecked")
	public <T extends Edge> T removeEdgeObject(final Edge edge) {
		if (edge == null) { return null; }

		removeEdge((AbstractEdge) edge, true, true, true);
		return (T) edge;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(java.lang.String, java.lang.String)
	 */
	@Override
	public <T extends Edge> T removeEdge(final String from, final String to) {
		final Node fromNode = getNode(from);
		final Node toNode = getNode(to);

		if (fromNode == null || toNode == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("Cannot remove the edge. The node \"%s\" does not exist",
						fromNode == null ? from : to);
			}
			return null;
		}

		return removeEdge(fromNode, toNode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(int, int)
	 */
	@Override
	public <T extends Edge> T removeEdge(final int fromIndex, final int toIndex) {
		final Node fromNode = getNode(fromIndex);
		final Node toNode = getNode(toIndex);

		if (fromNode == null || toNode == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("Cannot remove the edge. The node #%d does not exist",
						fromNode == null ? fromIndex : toIndex);
			}
			return null;
		}

		return removeEdge(fromNode, toNode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.graph.Graph#removeEdge(org.graphstream.graph.Node, org.graphstream.graph.Node)
	 */
	@Override
	public <T extends Edge> T removeEdge(final Node node1, final Node node2) {
		final AbstractEdge edge = node1.getEdgeTowardNode(node2);

		if (edge == null) {
			if (strictChecking) {
				throw new ElementNotFoundException("There is no edge from \"%s\" to \"%s\". Cannot remove it.",
						node1.getId(), node2.getId());
			}
			return null;
		}

		return removeEdgeObject(edge);
	}

	// *** Sinks, sources etc. ***

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#addAttributeSink(org.graphstream.stream .AttributeSink)
	 */
	@Override
	public void addAttributeSink(final AttributeSink sink) {
		listeners.addAttributeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#addElementSink(org.graphstream.stream. ElementSink)
	 */
	@Override
	public void addElementSink(final ElementSink sink) {
		listeners.addElementSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#addSink(org.graphstream.stream.Sink)
	 */
	@Override
	public void addSink(final Sink sink) {
		listeners.addSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#clearAttributeSinks()
	 */
	@Override
	public void clearAttributeSinks() {
		listeners.clearAttributeSinks();
	}

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#clearElementSinks()
	 */
	@Override
	public void clearElementSinks() {
		listeners.clearElementSinks();
	}

	/*
	 * *(non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#clearSinks()
	 */
	@Override
	public void clearSinks() {
		listeners.clearSinks();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#removeAttributeSink(org.graphstream.stream .AttributeSink)
	 */
	@Override
	public void removeAttributeSink(final AttributeSink sink) {
		listeners.removeAttributeSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#removeElementSink(org.graphstream.stream .ElementSink)
	 */
	@Override
	public void removeElementSink(final ElementSink sink) {
		listeners.removeElementSink(sink);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.Source#removeSink(org.graphstream.stream.Sink)
	 */
	@Override
	public void removeSink(final Sink sink) {
		listeners.removeSink(sink);
	}

	@Override
	public void edgeAttributeAdded(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object value) {
		listeners.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	@Override
	public void edgeAttributeChanged(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object oldValue, final Object newValue) {
		listeners.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
	}

	@Override
	public void edgeAttributeRemoved(final String sourceId, final long timeId, final String edgeId,
			final String attribute) {
		listeners.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	@Override
	public void graphAttributeAdded(final String sourceId, final long timeId, final String attribute,
			final Object value) {
		listeners.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	@Override
	public void graphAttributeChanged(final String sourceId, final long timeId, final String attribute,
			final Object oldValue, final Object newValue) {
		listeners.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
	}

	@Override
	public void graphAttributeRemoved(final String sourceId, final long timeId, final String attribute) {
		listeners.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	@Override
	public void nodeAttributeAdded(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object value) {
		listeners.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	@Override
	public void nodeAttributeChanged(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object oldValue, final Object newValue) {
		listeners.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
	}

	@Override
	public void nodeAttributeRemoved(final String sourceId, final long timeId, final String nodeId,
			final String attribute) {
		listeners.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	@Override
	public void edgeAdded(final String sourceId, final long timeId, final String edgeId, final String fromNodeId,
			final String toNodeId, final boolean directed) {
		listeners.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	@Override
	public void edgeRemoved(final String sourceId, final long timeId, final String edgeId) {
		listeners.edgeRemoved(sourceId, timeId, edgeId);
	}

	@Override
	public void graphCleared(final String sourceId, final long timeId) {
		listeners.graphCleared(sourceId, timeId);
	}

	@Override
	public void nodeAdded(final String sourceId, final long timeId, final String nodeId) {
		listeners.nodeAdded(sourceId, timeId, nodeId);
	}

	@Override
	public void nodeRemoved(final String sourceId, final long timeId, final String nodeId) {
		listeners.nodeRemoved(sourceId, timeId, nodeId);
	}

	@Override
	public void stepBegins(final String sourceId, final long timeId, final double step) {
		listeners.stepBegins(sourceId, timeId, step);
	}

	// *** callbacks maintaining user's data structure

	/**
	 * This method is automatically called when a new node is created. Subclasses must add the new node to their data
	 * structure and to set its index correctly.
	 *
	 * @param node
	 *            the node to be added
	 */
	protected abstract void addNodeCallback(AbstractNode node);

	/**
	 * This method is automatically called when a new edge is created. Subclasses must add the new edge to their data
	 * structure and to set its index correctly.
	 *
	 * @param edge
	 *            the edge to be added
	 */
	protected abstract void addEdgeCallback(AbstractEdge edge);

	/**
	 * This method is automatically called when a node is removed. Subclasses must remove the node from their data
	 * structures and to re-index other node(s) so that node indices remain coherent.
	 *
	 * @param node
	 *            the node to be removed
	 */
	protected abstract void removeNodeCallback(AbstractNode node);

	/**
	 * This method is automatically called when an edge is removed. Subclasses must remove the edge from their data
	 * structures and re-index other edge(s) so that edge indices remain coherent.
	 *
	 * @param edge
	 *            the edge to be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * This method is automatically called when the graph is cleared. Subclasses must remove all the nodes and all the
	 * edges from their data structures.
	 */
	protected abstract void clearCallback();

	// *** _ methods ***

	// Why do we pass both the ids and the references of the endpoints here?
	// When the caller knows the references it's stupid to call getNode(id)
	// here. If the node does not exist the reference will be null.
	// And if autoCreate is on, we need also the id. Sad but true!
	@SuppressWarnings ("unchecked")
	protected <T extends Edge> T addEdge(final String edgeId, AbstractNode src, final String srcId, AbstractNode dst,
			final String dstId, final boolean directed) {
		AbstractEdge edge = getEdge(edgeId);
		if (edge != null) {
			if (strictChecking) {
				throw new IdAlreadyInUseException("id \"" + edgeId + "\" already in use. Cannot create an edge.");
			}
			if (edge.getSourceNode() == src && edge.getTargetNode() == dst
					|| !directed && edge.getTargetNode() == src && edge.getSourceNode() == dst) {
				return (T) edge;
			}
			return null;
		}

		if (src == null || dst == null) {
			if (strictChecking) {
				throw new ElementNotFoundException(
						String.format("Cannot create edge %s[%s-%s%s]. Node '%s' does not exist.", edgeId, srcId,
								directed ? ">" : "-", dstId, src == null ? srcId : dstId));
			}
			if (!autoCreate) { return null; }
			if (src == null) {
				src = addNode(srcId);
			}
			if (dst == null) {
				dst = addNode(dstId);
			}
		}
		// at this point edgeId is not in use and both src and dst are not null
		edge = edgeFactory.newInstance(edgeId, src, dst, directed);
		// see if the endpoints accept the edge
		if (!src.addEdgeCallback(edge)) {
			if (strictChecking) { throw new EdgeRejectedException("Edge " + edge + " was rejected by node " + src); }
			return null;
		}
		// note that for loop edges the callback is called only once
		if (src != dst && !dst.addEdgeCallback(edge)) {
			// the edge is accepted by src but rejected by dst
			// so we have to remove it from src
			src.removeEdgeCallback(edge);
			if (strictChecking) { throw new EdgeRejectedException("Edge " + edge + " was rejected by node " + dst); }
			return null;
		}

		// now we can finally add it
		addEdgeCallback(edge);

		listeners.sendEdgeAdded(edgeId, srcId, dstId, directed);

		return (T) edge;
	}

	// helper for removeNode_
	private void removeAllEdges(final AbstractNode node) {
		// first check if the EdgeIterator of node supports remove
		// if this is the case, we will use it, generally it will be much more
		// efficient
		final Iterator<AbstractEdge> edgeIt = node.getEdgeIterator();
		boolean supportsRemove = true;
		if (!edgeIt.hasNext()) { return; }
		try {
			edgeIt.next();
			edgeIt.remove();
		} catch (final UnsupportedOperationException e) {
			supportsRemove = false;
		}
		if (supportsRemove) {
			while (edgeIt.hasNext()) {
				edgeIt.next();
				edgeIt.remove();
			}
		} else {
			while (node.getDegree() > 0) {
				removeEdgeObject(node.getEdge(0));
			}
		}
	}

	// *** Methods for iterators ***

	/**
	 * This method is similar to {@link #removeNode(Node)} but allows to control if
	 * {@link #removeNodeCallback(AbstractNode)} is called or not. It is useful for iterators supporting
	 * {@link java.util.Iterator#remove()} who want to update the data structures by their owns.
	 *
	 * @param node
	 *            the node to be removed
	 * @param graphCallback
	 *            if {@code false}, {@code removeNodeCallback(node)} is not called
	 */
	protected void removeNode(final AbstractNode node, final boolean graphCallback) {
		if (node == null) { return; }

		removeAllEdges(node);
		listeners.sendNodeRemoved(node.getId());

		if (graphCallback) {
			removeNodeCallback(node);
		}
	}

	/**
	 * This method is similar to {@link #removeEdgeObject(Edge)} but allows to control if different callbacks are called
	 * or not. It is useful for iterators supporting {@link java.util.Iterator#remove()} who want to update the data
	 * structures by their owns.
	 *
	 * @param edge
	 *            the edge to be removed
	 * @param graphCallback
	 *            if {@code false}, {@link #removeEdgeCallback(AbstractEdge)} of the graph is not called
	 * @param sourceCallback
	 *            if {@code false}, {@link AbstractNode#removeEdgeCallback(AbstractEdge)} is not called for the source
	 *            node of the edge
	 * @param targetCallback
	 *            if {@code false}, {@link AbstractNode#removeEdgeCallback(AbstractEdge)} is not called for the target
	 *            node of the edge
	 */
	protected void removeEdge(final AbstractEdge edge, final boolean graphCallback, final boolean sourceCallback,
			final boolean targetCallback) {
		if (edge == null) { return; }

		final AbstractNode src = edge.getSourceNode();
		final AbstractNode dst = edge.getTargetNode();

		listeners.sendEdgeRemoved(edge.getId());

		if (sourceCallback) {
			src.removeEdgeCallback(edge);
		}

		if (src != dst && targetCallback) {
			dst.removeEdgeCallback(edge);
		}

		if (graphCallback) {
			removeEdgeCallback(edge);
		}
	}

}
