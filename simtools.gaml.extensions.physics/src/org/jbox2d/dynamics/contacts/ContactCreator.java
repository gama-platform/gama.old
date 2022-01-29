/*******************************************************************************************************
 *
 * ContactCreator.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The Interface ContactCreator.
 */
// updated to rev 100
public interface ContactCreator {

	/**
	 * Contact create fcn.
	 *
	 * @param argPool the arg pool
	 * @param fixtureA the fixture A
	 * @param fixtureB the fixture B
	 * @return the contact
	 */
	public Contact contactCreateFcn(IWorldPool argPool, Fixture fixtureA, Fixture fixtureB);
	
	/**
	 * Contact destroy fcn.
	 *
	 * @param argPool the arg pool
	 * @param contact the contact
	 */
	public void contactDestroyFcn(IWorldPool argPool, Contact contact);
}
