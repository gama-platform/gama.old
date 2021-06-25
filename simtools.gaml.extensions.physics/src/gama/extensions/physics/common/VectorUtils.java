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

	static javax.vecmath.Vector3f newBulletVector() {
		return new javax.vecmath.Vector3f();
	}

	static com.jme3.math.Vector3f newNativeBulletVector() {
		return new com.jme3.math.Vector3f();
	}

	static org.jbox2d.common.Vec2 newBox2DVector() {
		return new org.jbox2d.common.Vec2();
	}

	public static javax.vecmath.Vector3f toBulletVector(final GamaPoint v) {
		return toBulletVector(v, newBulletVector());
	}

	public static javax.vecmath.Vector3f toBulletVector(final GamaPoint from, final javax.vecmath.Vector3f to) {
		javax.vecmath.Vector3f result = to == null ? newBulletVector() : to;
		if (from != null) {
			result.x = (float) from.x;
			result.y = (float) from.y;
			result.z = (float) from.z;
		}
		return result;
	}

	public static org.jbox2d.common.Vec2 toBox2DVector(final GamaPoint v) {
		return toBox2DVector(v, new org.jbox2d.common.Vec2());
	}

	public static org.jbox2d.common.Vec2 toBox2DVector(final GamaPoint from, final org.jbox2d.common.Vec2 to) {
		org.jbox2d.common.Vec2 result = to == null ? newBox2DVector() : to;
		if (from != null) {
			result.x = (float) from.x;
			result.y = (float) from.y;
		}
		return result;
	}

	public static com.jme3.math.Vector3f toNativeBulletVector(final GamaPoint v) {
		return toNativeBulletVector(v, new com.jme3.math.Vector3f());
	}

	public static com.jme3.math.Vector3f toNativeBulletVector(final GamaPoint from, final com.jme3.math.Vector3f to) {
		com.jme3.math.Vector3f result = to == null ? newNativeBulletVector() : to;
		if (from != null) {
			result.x = (float) from.x;
			result.y = (float) from.y;
			result.z = (float) from.z;
		}
		return result;
	}

	public static GamaPoint toGamaPoint(final com.jme3.math.Vector3f v) {
		return toGamaPoint(v, new GamaPoint());
	}

	public static GamaPoint toGamaPoint(final com.jme3.math.Vector3f v, final GamaPoint to) {
		GamaPoint result = to == null ? new GamaPoint() : to;
		if (v != null) {
			result.x = v.x;
			result.y = v.y;
			result.z = v.z;
		}
		return result;
	}

	public static GamaPoint toGamaPoint(final javax.vecmath.Vector3f v) {
		return toGamaPoint(v, new GamaPoint());
	}

	public static GamaPoint toGamaPoint(final javax.vecmath.Vector3f v, final GamaPoint to) {
		GamaPoint result = to == null ? new GamaPoint() : to;

		if (v != null) {
			result.x = v.x;
			result.y = v.y;
			result.z = v.z;
		}
		return result;
	}

	public static GamaPoint toGamaPoint(final org.jbox2d.common.Vec2 v) {
		return toGamaPoint(v, new GamaPoint());
	}

	public static GamaPoint toGamaPoint(final org.jbox2d.common.Vec2 v, final GamaPoint to) {
		GamaPoint result = to == null ? new GamaPoint() : to;

		if (v != null) {
			result.x = v.x;
			result.y = v.y;
		}
		return result;
	}

}
