/*******************************************************************************************************
 *
 * GImpactMassUtil.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package com.bulletphysics.extras.gimpact;

import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
class GImpactMassUtil {

	/**
	 * Gets the point inertia.
	 *
	 * @param point the point
	 * @param mass the mass
	 * @param out the out
	 * @return the point inertia
	 */
	public static Vector3f get_point_inertia(Vector3f point, float mass, Vector3f out) {
		float x2 = point.x * point.x;
		float y2 = point.y * point.y;
		float z2 = point.z * point.z;
		out.set(mass * (y2 + z2), mass * (x2 + z2), mass * (x2 + y2));
		return out;
	}
	
}
