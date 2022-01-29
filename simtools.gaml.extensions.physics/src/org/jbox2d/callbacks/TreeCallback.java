/*******************************************************************************************************
 *
 * TreeCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.callbacks;

import org.jbox2d.collision.broadphase.DynamicTree;

// update to rev 100
/**
 * callback for {@link DynamicTree}
 * @author Daniel Murphy
 *
 */
public interface TreeCallback {
	
	/**
	 * Callback from a query request.  
	 * @param proxyId the id of the proxy
	 * @return if the query should be continued
	 */
	public boolean treeCallback(int proxyId);
}
