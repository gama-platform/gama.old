/*******************************************************************************************************
 *
 * VectorUtils.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.common;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * A class required because all the physics engines out there use different classes for their vectors :( A good
 * opportunity to do some pooling, though, but it is not yet implemented. This class is complemented by interfaces
 * extending IPhysicalEntity<VectorType>, that provide to their instance a direct access to the correct methods to use
 * for converting vectors
 *
 * @author Alexis Drogoul
 *
 */
public class VectorUtils {

	/**
	 * New bullet vector.
	 *
	 * @return the javax.vecmath. vector 3 f
	 */
	static javax.vecmath.Vector3f newBulletVector() {
		return new javax.vecmath.Vector3f();
	}

	/**
	 * New native bullet vector.
	 *
	 * @return the com.jme 3 .math. vector 3 f
	 */
	static com.jme3.math.Vector3f newNativeBulletVector() {
		return new com.jme3.math.Vector3f();
	}

	/**
	 * New box 2 D vector.
	 *
	 * @return the org.jbox 2 d.common. vec 2
	 */
	static org.jbox2d.common.Vec2 newBox2DVector() {
		return new org.jbox2d.common.Vec2();
	}

	/**
	 * To bullet vector.
	 *
	 * @param v the v
	 * @return the javax.vecmath. vector 3 f
	 */
	public static javax.vecmath.Vector3f toBulletVector(final GamaPoint v) {
		return toBulletVector(v, newBulletVector());
	}

	/**
	 * To bullet vector.
	 *
	 * @param from the from
	 * @param to the to
	 * @return the javax.vecmath. vector 3 f
	 */
	public static javax.vecmath.Vector3f toBulletVector(final GamaPoint from, final javax.vecmath.Vector3f to) {
		javax.vecmath.Vector3f result = to == null ? newBulletVector() : to;
		if (from != null) {
			result.x = (float) from.x;
			result.y = (float) from.y;
			result.z = (float) from.z;
		}
		return result;
	}

	/**
	 * To box 2 D vector.
	 *
	 * @param v the v
	 * @return the org.jbox 2 d.common. vec 2
	 */
	public static org.jbox2d.common.Vec2 toBox2DVector(final GamaPoint v) {
		return toBox2DVector(v, new org.jbox2d.common.Vec2());
	}

	/**
	 * To box 2 D vector.
	 *
	 * @param from the from
	 * @param to the to
	 * @return the org.jbox 2 d.common. vec 2
	 */
	public static org.jbox2d.common.Vec2 toBox2DVector(final GamaPoint from, final org.jbox2d.common.Vec2 to) {
		org.jbox2d.common.Vec2 result = to == null ? newBox2DVector() : to;
		if (from != null) {
			result.x = (float) from.x;
			result.y = (float) from.y;
		}
		return result;
	}

	/**
	 * To native bullet vector.
	 *
	 * @param v the v
	 * @return the com.jme 3 .math. vector 3 f
	 */
	public static com.jme3.math.Vector3f toNativeBulletVector(final GamaPoint v) {
		return toNativeBulletVector(v, new com.jme3.math.Vector3f());
	}

	/**
	 * To native bullet vector.
	 *
	 * @param from the from
	 * @param to the to
	 * @return the com.jme 3 .math. vector 3 f
	 */
	public static com.jme3.math.Vector3f toNativeBulletVector(final GamaPoint from, final com.jme3.math.Vector3f to) {
		com.jme3.math.Vector3f result = to == null ? newNativeBulletVector() : to;
		if (from != null) {
			result.x = (float) from.x;
			result.y = (float) from.y;
			result.z = (float) from.z;
		}
		return result;
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @return the gama point
	 */
	public static GamaPoint toGamaPoint(final com.jme3.math.Vector3f v) {
		return toGamaPoint(v, new GamaPoint());
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @param to the to
	 * @return the gama point
	 */
	public static GamaPoint toGamaPoint(final com.jme3.math.Vector3f v, final GamaPoint to) {
		GamaPoint result = to == null ? new GamaPoint() : to;
		if (v != null) {
			result.x = v.x;
			result.y = v.y;
			result.z = v.z;
		}
		return result;
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @return the gama point
	 */
	public static GamaPoint toGamaPoint(final javax.vecmath.Vector3f v) {
		return toGamaPoint(v, new GamaPoint());
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @param to the to
	 * @return the gama point
	 */
	public static GamaPoint toGamaPoint(final javax.vecmath.Vector3f v, final GamaPoint to) {
		GamaPoint result = to == null ? new GamaPoint() : to;

		if (v != null) {
			result.x = v.x;
			result.y = v.y;
			result.z = v.z;
		}
		return result;
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @return the gama point
	 */
	public static GamaPoint toGamaPoint(final org.jbox2d.common.Vec2 v) {
		return toGamaPoint(v, new GamaPoint());
	}

	/**
	 * To gama point.
	 *
	 * @param v the v
	 * @param to the to
	 * @return the gama point
	 */
	public static GamaPoint toGamaPoint(final org.jbox2d.common.Vec2 v, final GamaPoint to) {
		GamaPoint result = to == null ? new GamaPoint() : to;

		if (v != null) {
			result.x = v.x;
			result.y = v.y;
		}
		return result;
	}

}
