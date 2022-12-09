/*******************************************************************************************************
 *
 * IGraphEventListener.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import msi.gama.runtime.IScope;

/**
 * The listener interface for receiving IGraphEvent events.
 * The class that is interested in processing a IGraphEvent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addIGraphEventListener<code> method. When
 * the IGraphEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see IGraphEventEvent
 */
public interface IGraphEventListener {

	/**
	 * Receive event.
	 *
	 * @param scope the scope
	 * @param event the event
	 */
	public void receiveEvent(final IScope scope, GraphEvent event);

}
