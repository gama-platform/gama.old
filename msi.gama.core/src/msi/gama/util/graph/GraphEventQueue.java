package msi.gama.util.graph;

import java.util.ArrayDeque;
import java.util.Deque;

import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Receives graph events and stores them.
 * Notably enables to update the graph only after the 
 * step of the simulation, not during the simulation.
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphEventQueue implements IGraphEventListener {

	/**
	 * The queue that stores events. Note that it is NOT synchronized.
	 * For asynchronous access please use the accessors instead.  
	 */
	public final Deque<GraphEvent> queue = new ArrayDeque<GraphEvent>(1000);
	
	public static final int MAX_EVENTS_MEMORY = 1000000;
	
	@Override
	public void receiveEvent(GraphEvent event) {
		if (queue.size() >= MAX_EVENTS_MEMORY)
			throw new GamaRuntimeException("Too much graph events in memory ("+MAX_EVENTS_MEMORY+")");
		synchronized (queue) {
			System.err.println(event);
			queue.addLast(event);	
		}
	}
	
	/**
	 * Provides the next graph event (synchronous) or null if no more event.
	 * @return
	 */
	public GraphEvent popNext() {
		synchronized (queue) {
			return queue.pollFirst();	
		}
	}	

	public boolean isEmpty() {
		synchronized (queue) {
			return queue.isEmpty();
		}
	}
}
