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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * This class provides a basic implementation of {@code Node} interface, to minimize the effort required to implement
 * this interface.
 * </p>
 *
 * <p>
 * This class implements all the methods of {@link org.graphstream.graph.implementations#AbstractElement} and most of
 * the methods of {@link org.graphstream.graph#Node} (there are "only" ten abstract methods). In addition to these,
 * subclasses must provide implementations for {@link #addEdgeCallback(AbstractEdge)} and
 * {@link #removeEdgeCallback(AbstractEdge)} which are called by the parent graph when an edge incident to this node is
 * added to or removed from the graph. This class has a low memory overhead (one reference as field).
 * </p>
 */
public abstract class AbstractNode extends AbstractElement implements Node {

	// *** Fields ***

	/**
	 * The graph to which this node belongs
	 */
	protected AbstractGraph graph;

	// *** Constructors

	/**
	 * Constructs a new node. This constructor copies the parameters into the corresponding fields
	 *
	 * @param graph
	 *            The graph to which this node belongs.
	 * @param id
	 *            Unique identifier of this node.
	 */
	protected AbstractNode(final AbstractGraph graph, final String id) {
		super(id);
		this.graph = graph;
	}

	// *** Inherited from abstract element ***

	@Override
	protected void attributeChanged(final AttributeChangeEvent event, final String attribute, final Object oldValue,
			final Object newValue) {
		graph.listeners.sendAttributeChangedEvent(id, SourceBase.ElementType.NODE, attribute, event, oldValue,
				newValue);
	}

	/**
	 * @return The id of the parent graph
	 * @see org.graphstream.graph.implementations.AbstractElement#myGraphId()
	 */
	// protected String myGraphId() {
	// return graph.getId();
	// }

	/**
	 * This implementation calls the corresponding method of the parent graph
	 *
	 * @see msi.gama.ext.graphstream.AbstractElement#newEvent()
	 */
	// protected long newEvent() {
	// return graph.newEvent();
	// }

	@Override
	/**
	 * This implementation calls the corresponding method of the parent graph
	 *
	 * @see org.graphstream.graph.implementations.AbstractElement#nullAttributesAreErrors()
	 */
	protected boolean nullAttributesAreErrors() {
		return graph.nullAttributesAreErrors();
	}

	// *** Inherited from Node ***

	/**
	 * This implementation returns {@link #graph}.
	 *
	 * @see msi.gama.ext.graphstream.Node#getGraph()
	 */
	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public abstract int getDegree();

	@Override
	public abstract int getInDegree();

	@Override
	public abstract int getOutDegree();

	// [has|get]Edge[Toward|From|Between](Node|int|String) -> 2 * 3 * 3 = 18
	// methods

	/**
	 * This implementation returns {@code true} if {@link #getEdgeToward(Node)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeToward(msi.gama.ext.graphstream.Node)
	 */
	@Override
	public boolean hasEdgeTowardNode(final Node node) {
		return getEdgeTowardNode(node) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeToward(int)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeToward(int)
	 */
	@Override
	public boolean hasEdgeToward(final int index) {
		return getEdgeToward(index) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeToward(Node)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeToward(java.lang.String)
	 */
	@Override
	public boolean hasEdgeToward(final String id) {
		return getEdgeToward(id) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeFrom(Node)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeFrom(msi.gama.ext.graphstream.Node)
	 */
	@Override
	public boolean hasEdgeFromNode(final Node node) {
		return getEdgeFromNode(node) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeFrom(int)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeFrom(int)
	 */
	@Override
	public boolean hasEdgeFrom(final int index) {
		return getEdgeFrom(index) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeFrom(Node)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeFrom(java.lang.String)
	 */
	@Override
	public boolean hasEdgeFrom(final String id) {
		return getEdgeFrom(id) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeBetween(Node)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeBetween(msi.gama.ext.graphstream.Node)
	 */
	@Override
	public boolean hasEdgeBetween(final Node node) {
		return getEdgeBetweenNode(node) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeBetween(int)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeBetween(int)
	 */
	@Override
	public boolean hasEdgeBetween(final int index) {
		return getEdgeBetween(index) != null;
	}

	/**
	 * This implementation returns {@code true} if {@link #getEdgeBetween(Node)} is not {@code null}.
	 *
	 * @see msi.gama.ext.graphstream.Node#hasEdgeBetween(java.lang.String)
	 */
	@Override
	public boolean hasEdgeBetween(final String id) {
		return getEdgeBetween(id) != null;
	}

	@Override
	public abstract <T extends Edge> T getEdgeTowardNode(Node node);

	/**
	 * This implementation uses {@link #getEdgeToward(Node)}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeToward(int)
	 */
	@Override
	public <T extends Edge> T getEdgeToward(final int index) {
		return getEdgeToward(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeToward(Node)}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeToward(java.lang.String)
	 */
	@Override
	public <T extends Edge> T getEdgeToward(final String id) {
		return getEdgeToward(graph.getNode(id));
	}

	@Override
	public abstract <T extends Edge> T getEdgeFromNode(Node node);

	/**
	 * This implementation uses {@link #getEdgeFrom(Node)}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeFrom(int)
	 */
	@Override
	public <T extends Edge> T getEdgeFrom(final int index) {
		return getEdgeFromNode(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeFrom(Node)}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeFrom(java.lang.String)
	 */
	@Override
	public <T extends Edge> T getEdgeFrom(final String id) {
		return getEdgeFromNode(graph.getNode(id));
	}

	@Override
	public abstract <T extends Edge> T getEdgeBetweenNode(Node node);

	/**
	 * This implementation uses {@link #getEdgeBetween(Node)}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeBetween(int)
	 */
	@Override
	@SuppressWarnings ("cast")
	public <T extends Edge> T getEdgeBetween(final int index) {
		return getEdgeBetweenNode(graph.getNode(index));
	}

	/**
	 * This implementation uses {@link #getEdgeBetween(Node)}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeBetween(java.lang.String)
	 */
	@Override
	@SuppressWarnings ("cast")
	public <T extends Edge> T getEdgeBetween(final String id) {
		return getEdgeBetweenNode(graph.getNode(id));
	}

	// get[_|Entering|Leaving]EdgeIterator

	@Override
	public abstract <T extends Edge> Iterator<T> getEdgeIterator();

	@Override
	public abstract <T extends Edge> Iterator<T> getEnteringEdgeIterator();

	@Override
	public abstract <T extends Edge> Iterator<T> getLeavingEdgeIterator();

	// getEach[_Entering|Leaving]Edge

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEachEdge()
	 */
	@Override
	public <T extends Edge> Iterable<T> getEachEdge() {
		return () -> getEdgeIterator();
	}

	/**
	 * This implementation uses {@link #getEnteringEdgeIterator()}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEachEnteringEdge()
	 */
	@Override
	public <T extends Edge> Iterable<T> getEachEnteringEdge() {
		return () -> getEnteringEdgeIterator();
	}

	/**
	 * This implementation uses {@link #getLeavingEdgeIterator()}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEachLeavingEdge()
	 */
	@Override
	public <T extends Edge> Iterable<T> getEachLeavingEdge() {
		return () -> getLeavingEdgeIterator();
	}

	// get[_|Entering|Leaving]EdgeSet

	/**
	 * This implementation uses {@link #getEdgeIterator()} and {@link #getDegree()}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEdgeSet()
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
				return getDegree();
			}
		};
	}

	/**
	 * This implementation uses {@link #getEnteringEdgeIterator()} and {@link #geIntDegree()}
	 *
	 * @see msi.gama.ext.graphstream.Node#getEnteringEdgeSet()
	 */
	@Override
	public <T extends Edge> Collection<T> getEnteringEdgeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				return getEnteringEdgeIterator();
			}

			@Override
			public int size() {
				return getInDegree();
			}
		};
	}

	/**
	 * This implementation uses {@link #getLeavingIterator()} and {@link #geOuttDegree()}
	 *
	 * @see msi.gama.ext.graphstream.Node#getLeavingEdgeSet()
	 */
	@Override
	public <T extends Edge> Collection<T> getLeavingEdgeSet() {
		return new AbstractCollection<T>() {
			@Override
			public Iterator<T> iterator() {
				return getLeavingEdgeIterator();
			}

			@Override
			public int size() {
				return getOutDegree();
			}
		};
	}

	/**
	 * This implementation uses {@link #getEdgeIterator()}
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Edge> iterator() {
		return getEdgeIterator();
	}

	@Override
	public abstract <T extends Edge> T getEdge(int i);

	@Override
	public abstract <T extends Edge> T getEnteringEdge(int i);

	@Override
	public abstract <T extends Edge> T getLeavingEdge(int i);

	/**
	 * This implementation uses {@link #getEdgeIterator()} and stores the visited nodes in a set. In this way it ensures
	 * that each neighbor will be visited exactly once, even in multi-graph.
	 *
	 * @see msi.gama.ext.graphstream.Node#getNeighborNodeIterator()
	 */
	@Override
	public <T extends Node> Iterator<T> getNeighborNodeIterator() {
		return new Iterator<T>() {
			Iterator<Edge> edgeIt = getEdgeIterator();
			HashSet<T> visited = new HashSet<>(getDegree());
			T next;
			{
				gotoNext();
			}

			private void gotoNext() {
				while (edgeIt.hasNext()) {
					next = edgeIt.next().getOpposite(AbstractNode.this);
					if (!visited.contains(next)) {
						visited.add(next);
						return;
					}
				}
				next = null;
			}

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public T next() {
				if (next == null) { throw new NoSuchElementException(); }
				final T current = next;
				gotoNext();
				return current;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("This iterator does not support remove");

			}

			// Iterator<Edge> edgeIterator = getEdgeIterator();
			//
			// public boolean hasNext() {
			// return edgeIterator.hasNext();
			// }
			//
			// public T next() {
			// return edgeIterator.next().getOpposite(AbstractNode.this);
			// }
			//
			// public void remove() {
			// throw new UnsupportedOperationException(
			// "This iterator does not support remove");
			// }
		};
	}

	// breadth- and depth-first iterator

	// *** Other methods ***

	/**
	 * This method is called automatically when an edge incident to this node is created. Subclasses use it to add the
	 * edge to their data structure.
	 *
	 * @param edge
	 *            a new edge incident to this node
	 */
	protected abstract boolean addEdgeCallback(AbstractEdge edge);

	/**
	 * This method is called automatically before removing an edge incident to this node. Subclasses use it to remove
	 * the edge from their data structure.
	 *
	 * @param edge
	 *            an edge incident to this node that will be removed
	 */
	protected abstract void removeEdgeCallback(AbstractEdge edge);

	/**
	 * This method is called for each node when the graph is cleared. Subclasses may use it to clear their data
	 * structures in order to facilitate the garbage collection.
	 */
	protected abstract void clearCallback();

	/**
	 * Checks if an edge enters this node. Utility method that can be useful in subclasses.
	 *
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is entering edge for this node.
	 */
	public boolean isEnteringEdge(final Edge e) {
		return e.getTargetNode() == this || !e.isDirected() && e.getSourceNode() == this;
	}

	/**
	 * Checks if an edge leaves this node. Utility method that can be useful in subclasses.
	 *
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is leaving edge for this node.
	 */
	public boolean isLeavingEdge(final Edge e) {
		return e.getSourceNode() == this || !e.isDirected() && e.getTargetNode() == this;
	}

	/**
	 * Checks if an edge is incident to this node. Utility method that can be useful in subclasses.
	 *
	 * @param e
	 *            an edge
	 * @return {@code true} if {@code e} is incident edge for this node.
	 */
	public boolean isIncidentEdge(final Edge e) {
		return e.getSourceNode() == this || e.getTargetNode() == this;
	}
}
