/*******************************************************************************************************
 *
 * QueryCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 4:30:03 AM Jul 15, 2010
 */
package org.jbox2d.callbacks;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

/**
 * Callback class for AABB queries.
 * See {@link World#queryAABB(QueryCallback, org.jbox2d.collision.AABB)}.
 * @author Daniel Murphy
 */
public interface QueryCallback {

	/**
	 * Called for each fixture found in the query AABB.
	 * @param fixture
	 * @return false to terminate the query.
	 */
	public boolean reportFixture(Fixture fixture);
}
