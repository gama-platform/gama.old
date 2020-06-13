/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphEventQueue.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.ArrayDeque;
import java.util.Deque;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Receives graph events and stores them. Notably enables to update the graph only after the step of the simulation, not
 * during the simulation.
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphEventQueue implements IGraphEventListener {

	/**
	 * The queue that stores events. Note that it is NOT synchronized. For asynchronous access please use the accessors
	 * instead.
	 */
	public final Deque<GraphEvent> queue = new ArrayDeque<GraphEvent>(1000);

	/**
	 * If the queue grows beyong this size, an error will be thrown
	 */
	public static final int MAX_EVENTS_MEMORY = 1000000;

	@Override
	public void receiveEvent(final IScope scope, final GraphEvent event) {
		if (queue.size() >= MAX_EVENTS_MEMORY)
			throw GamaRuntimeException.error("Too much graph events in memory (" + MAX_EVENTS_MEMORY + ")", scope);
		synchronized (queue) {
			queue.addLast(event);
		}
	}

	/**
	 * Provides the next graph event (synchronous) or null if no more event.
	 * 
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

	public int size() {
		synchronized (queue) {
			return queue.size();
		}
	}
}
