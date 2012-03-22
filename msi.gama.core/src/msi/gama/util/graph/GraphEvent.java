package msi.gama.util.graph;

public final class GraphEvent {

	/**
	 * The corresponding graph
	 */
	public final IGraph graph;
	
	/**
	 * the timestep at which the event was sent
	 */
	public final long timestep;
	
	public final _Edge edge;
	public final _Vertex vertex;
	
	public final GraphEventType eventType;
	
	

	public enum GraphEventType {
		
		/**
		 * The graph was flushed
		 */
		GRAPH_CLEARED,
		
		/**
		 * The properties of a graph changed (for instance, attributes).
		 * Will not be thrown for a edge or node changed, as another 
		 * event will already be thrown. 
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
		EDGE_CHANGED
		;
		
	}
	

	public GraphEvent(IGraph graph, long timestep, _Edge edge, _Vertex vertex,
			GraphEventType eventType) {
		super();
		this.graph = graph;
		this.timestep = timestep;
		this.edge = edge;
		this.vertex = vertex;
		this.eventType = eventType;
	}

	public String toString() {
		return (new StringBuffer()).append("graph event ").append(eventType)
				.append(", edge=").append(edge)
				.append(", vertex=").append(vertex)
				.append(", timestep=").append(timestep)
				.toString();
	}
	
}
