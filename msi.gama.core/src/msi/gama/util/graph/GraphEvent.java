/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphEvent.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import msi.gama.runtime.IScope;

@SuppressWarnings({ "rawtypes" })
public final class GraphEvent {

	/**
	 * The corresponding graph
	 */
	public final IGraph graph;

	/**
	 * the timestep at which the event was sent
	 */
	public final Object sender;
	public final IScope scope;
	public final Object edge;
	public final Object vertex;

	public final GraphEventType eventType;

	public enum GraphEventType {

		/**
		 * The graph was flushed
		 */
		GRAPH_CLEARED,

		/**
		 * The properties of a graph changed (for instance, attributes). Will
		 * not be thrown for a edge or node changed, as another event will
		 * already be thrown.
		 */
		GRAPH_CHANGED,

		/**
		 * a novel vertex was created
		 */
		VERTEX_ADDED,

		/**
		 * A vertex was removed
		 */
		VERTEX_REMOVED,

		/**
		 * A vertex changed (may be its attributes, for instance)
		 */
		VERTEX_CHANGED,

		/**
		 * An edge was added
		 */
		EDGE_ADDED,

		/**
		 * An edge was removed
		 */
		EDGE_REMOVED,

		/**
		 * An edge changed (may be its attributes ?)
		 */
		EDGE_CHANGED;

	}

	public GraphEvent(final IScope scope, final IGraph graph, final Object sender, final Object edge,
			final Object vertex, final GraphEventType eventType) {
		super();
		this.graph = graph;
		this.sender = sender;
		this.edge = edge;
		this.vertex = vertex;
		this.eventType = eventType;
		this.scope = scope;
	}

	@Override
	public String toString() {
		return new StringBuffer().append("graph event ").append(eventType).append(", edge=").append(edge)
				.append(", vertex=").append(vertex).append(", sender=").append(sender).toString();
	}

}
