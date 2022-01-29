/*******************************************************************************************************
 *
 * RayCastCallback.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 4:33:10 AM Jul 15, 2010
 */
package org.jbox2d.callbacks;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

/**
 * Callback class for ray casts.
 * See {@link World#raycast(RayCastCallback, Vec2, Vec2)}
 * @author Daniel Murphy
 */
public interface RayCastCallback {

	/**
	 * Called for each fixture found in the query. You control how the ray cast
	 * proceeds by returning a float:
	 * return -1: ignore this fixture and continue
	 * return 0: terminate the ray cast
	 * return fraction: clip the ray to this point
	 * return 1: don't clip the ray and continue
	 * @param fixture the fixture hit by the ray
	 * @param point the point of initial intersection
	 * @param normal the normal vector at the point of intersection
	 * @return -1 to filter, 0 to terminate, fraction to clip the ray for
	 * closest hit, 1 to continue
	 * @param fraction
	 */
	public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction);
}
