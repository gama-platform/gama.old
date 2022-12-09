/*******************************************************************************************************
 *
 * Rotation3D.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.common.geometry;

import java.io.Serializable;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * This class implements rotations in a three-dimensional space. A reimplementation of class Rotation of the Apache
 * Library
 *
 *
 *
 * @see GamaPoint
 * @adapted from Apache maths3 library by A. Drogoul (2017), esp. to allow direct access to the fields and make the
 *          instances mutable in place (reducing the number of garbage during long rotation operations)
 */

public class Rotation3D implements Serializable, Transformation3D {

	/**
	 * The Class CenteredOn.
	 */
	public static class CenteredOn extends Rotation3D {

		/** The center. */
		final GamaPoint center;

		/**
		 * Instantiates a new centered on.
		 *
		 * @param description
		 *            the description
		 * @param center
		 *            the center
		 */
		public CenteredOn(final AxisAngle description, final GamaPoint center) {
			super(description);
			this.center = center;
		}

		/**
		 * Applies this rotation to a Geometry (to be used as a CoordinateFilter for JTS geometries)
		 */
		@Override
		public void filter(final Coordinate c) {
			final GamaPoint p = (GamaPoint) c;
			p.subtract(center);
			applyTo(p);
			p.add(center);
		}

	}

	/** The Constant PLUS_I. */
	public static final GamaPoint PLUS_I = new GamaPoint(1, 0, 0);

	/** The Constant MINUS_I. */
	public static final GamaPoint MINUS_I = new GamaPoint(-1, 0, 0);

	/** The Constant PLUS_J. */
	public static final GamaPoint PLUS_J = new GamaPoint(0, 1, 0);

	/** The Constant PLUS_K. */
	public static final GamaPoint PLUS_K = new GamaPoint(0, 0, 1);

	/** Scalar coordinate of the quaternion. */
	private double q0;

	/** First coordinate of the vectorial part of the quaternion. */
	private double q1;

	/** Second coordinate of the vectorial part of the quaternion. */
	private double q2;

	/** Third coordinate of the vectorial part of the quaternion. */
	private double q3;

	/**
	 * Build a rotation from the quaternion coordinates.
	 * <p>
	 */
	private Rotation3D(final double quat0, final double quat1, final double quat2, final double quat3,
			final boolean needsNormalization) {
		q0 = quat0;
		q1 = quat1;
		q2 = quat2;
		q3 = quat3;
		if (needsNormalization) {
			// normalization preprocessing
			final double inv = 1.0 / Math.sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
			q0 *= inv;
			q1 *= inv;
			q2 *= inv;
			q3 *= inv;
		}
	}

	/**
	 * Sets the to identity.
	 */
	public void setToIdentity() {
		// 1.0, 0.0, 0.0, 0.0
		q0 = 1;
		q1 = 0;
		q2 = 0;
		q3 = 0;
	}

	/**
	 * Sets this rotation to a rotation between the given vector u and the PLUS_I vector
	 *
	 * @param u
	 *            an arbitrary 3D vector
	 */
	public void alignToHorizontal(final GamaPoint u) {
		final double normProduct = u.norm();
		if (-u.x < (2.0e-15 - 1.0) * normProduct) {
			// special case u = -v: we select a PI angle rotation around
			// an arbitrary vector orthogonal to u
			final GamaPoint w = u.orthogonal();
			q0 = 0.0;
			q1 = -w.x;
			q2 = -w.y;
			q3 = -w.z;
		} else {
			// general case: (u, v) defines a plane, we select
			// the shortest possible rotation: axis orthogonal to this plane
			q0 = Math.sqrt(0.5 * (1.0 - u.x / normProduct));
			final double coeff = 1.0 / (2.0 * q0 * normProduct);
			q1 = 0;
			q2 = coeff * u.z;
			q3 = coeff * -u.y;
		}
	}

	/**
	 * Build a rotation from an axis and an angle.
	 *
	 * @param axis
	 *            axis around which to rotate
	 * @param angle
	 *            rotation angle in radians
	 */
	public Rotation3D(final GamaPoint rotationAxis, final double angle) {
		GamaPoint axis = rotationAxis;
		if (axis == null) { axis = PLUS_K; }
		final double norm = axis.norm();

		final double halfAngle = -0.5 * angle;
		final double coeff = Math.sin(halfAngle) / norm;

		q0 = Math.cos(halfAngle);
		q1 = coeff * axis.x;
		q2 = coeff * axis.y;
		q3 = coeff * axis.z;

	}

	/**
	 *
	 * @param description
	 */
	public Rotation3D(final AxisAngle description) {
		this(description.axis, Math.toRadians(description.angle));
	}

	/**
	 * Sets this rotation to a rotation between the reference frame u1,u2 and the horizontal reference frame, specifying
	 * whether the rotation should be treated in a clockwise way (PLUS_I, MINUS_J) or counter-clockwise way (MINUS_I,
	 * MINUS_J). This shorthand version eliminates a lot of intermediate computations and reduces the garbage produced
	 * to the minimum. If the first vector is already a normal to the horizontal plane, it defers to alignToHorizontal()
	 * directly
	 *
	 * @param u1
	 *            a non-null vector
	 * @param u2
	 *            a second non-null vector
	 * @param clockwise
	 */
	public Rotation3D rotateToHorizontal(final GamaPoint u1, final GamaPoint u2, final boolean clockwise) {
		if (Math.abs(u1.z) == 1d) {
			alignToHorizontal(u2);
			return this;
		}
		double u1x = u1.x, u1y = u1.y, u1z = u1.z;
		double u3x, u3y, u3z;
		u3x = u1y * u2.z - u1z * u2.y;
		u3y = u2.x * u1z - u2.z * u1x;
		u3z = u1x * u2.y - u1y * u2.x;
		final double norm = Math.sqrt(u3x * u3x + u3y * u3y + u3z * u3z);
		u3x /= norm;
		u3y /= norm;
		u3z /= norm;
		u2.setLocation(-u3y * u1z + u3z * u1y, -u1x * u3z + u1z * u3x, -u3x * u1y + u3y * u1x).normalize();
		if (clockwise) {
			u1x *= -1;
			u1y *= -1;
			u1z *= -1;
		} else {
			u3x *= -1;
			u3y *= -1;
			u3z *= -1;

		}
		double s = u2.x + u3y + u1z;
		if (s > -0.19) {
			q0 = 0.5 * Math.sqrt(s + 1.0);
			final double inv = 0.25 / q0;
			q1 = inv * (u3z - u1y);
			q2 = inv * (u1x - u2.z);
			q3 = inv * (u2.y - u3x);
		} else {
			s = u2.x - u3y - u1z;
			if (s > -0.19) {
				q1 = 0.5 * Math.sqrt(s + 1.0);
				final double inv = 0.25 / q1;
				q0 = inv * (u3z - u1y);
				q2 = inv * (u2.y + u3x);
				q3 = inv * (u2.z + u1x);
			} else {
				s = u3y - u2.x - u1z;
				if (s > -0.19) {
					q2 = 0.5 * Math.sqrt(s + 1.0);
					final double inv = 0.25 / q2;
					q0 = inv * (u1x - u2.z);
					q1 = inv * (u2.y + u3x);
					q3 = inv * (u1y + u3z);
				} else {
					s = u1z - u2.x - u3y;
					q3 = 0.5 * Math.sqrt(s + 1.0);
					final double inv = 0.25 / q3;
					q0 = inv * (u2.y - u3x);
					q1 = inv * (u2.z + u1x);
					q2 = inv * (u1y + u3z);
				}
			}
		}
		return this;
	}

	/**
	 * Revert a rotation. Sets this rotation to the reverse of its effect. This means that if r(u) = v, then r.revert(v)
	 * = u. The instance is modified
	 *
	 * @return this, which reverses the effect of the instance
	 */

	public Rotation3D revertInPlace() {
		q0 = -q0;
		return this;
	}

	/**
	 * Get the normalized axis of the rotation.
	 *
	 * @return normalized axis of the rotation
	 */
	public GamaPoint getAxis() {
		final double squaredSine = q1 * q1 + q2 * q2 + q3 * q3;
		if (squaredSine == 0) return PLUS_I;
		final double sgn = +1;
		if (q0 < 0) {
			final double inverse = sgn / Math.sqrt(squaredSine);
			return new GamaPoint(q1 * inverse, q2 * inverse, q3 * inverse);
		}
		final double inverse = -sgn / Math.sqrt(squaredSine);
		return new GamaPoint(q1 * inverse, q2 * inverse, q3 * inverse);
	}

	/**
	 * Get the angle of the rotation in radians
	 *
	 * @return angle of the rotation (between 0 and &pi;)
	 * @see #Rotation(GamaPoint, double)
	 */
	public double getAngle() {
		if (q0 < -0.1 || q0 > 0.1) return 2 * Math.asin(Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3));
		if (q0 < 0) return 2 * Math.acos(-q0);
		return 2 * Math.acos(q0);
	}

	/**
	 * Apply the rotation to a vector.
	 *
	 * @param u
	 *            vector to apply the rotation to. The vector will be directly modified
	 */
	@Override
	public void applyTo(final GamaPoint u) {

		final double x = u.x;
		final double y = u.y;
		final double z = u.z;

		final double s = q1 * x + q2 * y + q3 * z;

		u.x = 2 * (q0 * (x * q0 - (q2 * z - q3 * y)) + s * q1) - x;
		u.y = 2 * (q0 * (y * q0 - (q3 * x - q1 * z)) + s * q2) - y;
		u.z = 2 * (q0 * (z * q0 - (q1 * y - q2 * x)) + s * q3) - z;

	}

	/**
	 * Apply to.
	 *
	 * @param u
	 *            the u
	 */
	public void applyTo(final Coordinate u) {

		final double x = u.x;
		final double y = u.y;
		final double z = u.z;

		final double s = q1 * x + q2 * y + q3 * z;

		u.x = 2 * (q0 * (x * q0 - (q2 * z - q3 * y)) + s * q1) - x;
		u.y = 2 * (q0 * (y * q0 - (q3 * x - q1 * z)) + s * q2) - y;
		u.z = 2 * (q0 * (z * q0 - (q1 * y - q2 * x)) + s * q3) - z;

	}

	/**
	 * Apply the instance to another rotation.
	 * <p>
	 * </p>
	 *
	 * @param r
	 *            rotation to apply the rotation to
	 * @return a new rotation which is the composition of r by the instance
	 */
	public Rotation3D applyTo(final Rotation3D r) {
		return new Rotation3D(r.q0 * q0 - (r.q1 * q1 + r.q2 * q2 + r.q3 * q3),
				r.q1 * q0 + r.q0 * q1 + (r.q2 * q3 - r.q3 * q2), r.q2 * q0 + r.q0 * q2 + (r.q3 * q1 - r.q1 * q3),
				r.q3 * q0 + r.q0 * q3 + (r.q1 * q2 - r.q2 * q1), false);
	}

	/**
	 * Identity.
	 *
	 * @return the rotation 3 D
	 */
	public static Rotation3D identity() {
		return new Rotation3D(1, 0, 0, 0, false);
	}

	/**
	 * Applies this rotation to a Geometry (to be used as a CoordinateFilter for JTS geometries)
	 */
	@Override
	public void filter(final Coordinate c) {
		applyTo(c);
	}

}
