/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Rotation3D.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
 * <p>
 * Rotations are guaranteed to be immutable objects.
 * </p>
 *
 * @see GamaPoint
 * @adapted from Apache maths3 library by A. Drogoul (2017), esp. to allow direct access to the fields and make the
 *          instances mutable in place (reducing the number of garbage during long rotation operations)
 */

public class Rotation3D implements Serializable, Transformation3D {

	public static class CenteredOn extends Rotation3D {

		final GamaPoint center;

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

	public static final GamaPoint PLUS_I = new GamaPoint(1, 0, 0);
	public static final GamaPoint MINUS_I = new GamaPoint(-1, 0, 0);
	public static final GamaPoint PLUS_J = new GamaPoint(0, 1, 0);
	// public static final GamaPoint MINUS_J = new GamaPoint(0, -1, 0);
	public static final GamaPoint PLUS_K = new GamaPoint(0, 0, 1);
	// public static final GamaPoint MINUS_K = new GamaPoint(0, 0, -1);

	/** Identity rotation. */
	// public static final Rotation3D IDENTITY = new Rotation3D(1.0, 0.0, 0.0, 0.0, false);

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
		if (axis == null) {
			axis = PLUS_K;
		}
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
	 * By default around the Z axis (PLUS_K)
	 *
	 * @param angle
	 *            rotation angle in radians
	 */
	// public Rotation3D(final double angle) {
	// final double halfAngle = -0.5 * angle;
	// q0 = Math.cos(halfAngle);
	// q1 = 0;
	// q2 = 0;
	// q3 = Math.sin(halfAngle);
	// }

	/**
	 * Build the rotation that transforms a pair of vectors into another pair.
	 *
	 * <p>
	 * Except for possible scale factors, if the instance were applied to the pair (u<sub>1</sub>, u<sub>2</sub>) it
	 * will produce the pair (v<sub>1</sub>, v<sub>2</sub>).
	 * </p>
	 *
	 * <p>
	 * If the angular separation between u<sub>1</sub> and u<sub>2</sub> is not the same as the angular separation
	 * between v<sub>1</sub> and v<sub>2</sub>, then a corrected v'<sub>2</sub> will be used rather than v<sub>2</sub>,
	 * the corrected vector will be in the (&pm;v<sub>1</sub>, +v<sub>2</sub>) half-plane.
	 * </p>
	 *
	 * @param u1
	 *            first vector of the origin pair
	 * @param u2
	 *            second vector of the origin pair
	 * @param v1
	 *            desired image of u1 by the rotation
	 * @param v2
	 *            desired image of u2 by the rotation
	 */
	// public Rotation3D(final GamaPoint originVector1, final GamaPoint originVector2, final GamaPoint desiredVector1,
	// final GamaPoint desiredVector2) {
	// GamaPoint u1 = originVector1;
	// GamaPoint u2 = originVector2;
	// GamaPoint v1 = desiredVector1;
	// GamaPoint v2 = desiredVector2;
	//
	// // build orthonormalized base from u1, u2
	// // this fails when vectors are null or collinear, which is forbidden to define a rotation
	// final GamaPoint u3 = u1.crossProduct(u2).normalized();
	// u2 = u3.crossProduct(u1).normalized();
	// u1 = u1.normalized();
	//
	// // build an orthonormalized base from v1, v2
	// // this fails when vectors are null or collinear, which is forbidden to define a rotation
	// final GamaPoint v3 = v1.crossProduct(v2).normalized();
	// v2 = v3.crossProduct(v1).normalized();
	// v1 = v1.normalized();
	//
	// // buid a matrix transforming the first base into the second one
	// final double[][] m = new double[][] {
	// { MathArrays.linearCombination(u1.x, v1.x, u2.x, v2.x, u3.x, v3.x),
	// MathArrays.linearCombination(u1.y, v1.x, u2.y, v2.x, u3.y, v3.x),
	// MathArrays.linearCombination(u1.z, v1.x, u2.z, v2.x, u3.z, v3.x) },
	// { MathArrays.linearCombination(u1.x, v1.y, u2.x, v2.y, u3.x, v3.y),
	// MathArrays.linearCombination(u1.y, v1.y, u2.y, v2.y, u3.y, v3.y),
	// MathArrays.linearCombination(u1.z, v1.y, u2.z, v2.y, u3.z, v3.y) },
	// { MathArrays.linearCombination(u1.x, v1.z, u2.x, v2.z, u3.x, v3.z),
	// MathArrays.linearCombination(u1.y, v1.z, u2.y, v2.z, u3.y, v3.z),
	// MathArrays.linearCombination(u1.z, v1.z, u2.z, v2.z, u3.z, v3.z) } };
	//
	// final double[] quat = mat2quat(m);
	// q0 = quat[0];
	// q1 = quat[1];
	// q2 = quat[2];
	// q3 = quat[3];
	//
	// }

	// public Rotation3D(final GamaPoint u1, final GamaPoint u2, final boolean clockwise) {
	// rotateToHorizontal(u1, u2, clockwise);
	// }

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
	 * Build one of the rotations that transform one vector into another one.
	 *
	 * <p>
	 * Except for a possible scale factor, if the instance were applied to the vector u it will produce the vector v.
	 * There is an infinite number of such rotations, this constructor choose the one with the smallest associated angle
	 * (i.e. the one whose axis is orthogonal to the (u, v) plane). If u and v are collinear, an arbitrary rotation axis
	 * is chosen.
	 * </p>
	 *
	 * @param u
	 *            origin vector
	 * @param v
	 *            desired image of u by the rotation
	 */
	// public Rotation3D(final GamaPoint u, final GamaPoint v) {
	// setToRotationBetween(u, v);
	// }

	/**
	 * Convert an orthogonal rotation matrix to a quaternion.
	 *
	 * @param ort
	 *            orthogonal rotation matrix
	 * @return quaternion corresponding to the matrix
	 */
	// private static double[] mat2quat(final double[][] ort) {
	//
	// final double[] quat = new double[4];
	// double s = ort[0][0] + ort[1][1] + ort[2][2];
	// if (s > -0.19) {
	// // compute q0 and deduce q1, q2 and q3
	// quat[0] = 0.5 * Math.sqrt(s + 1.0);
	// final double inv = 0.25 / quat[0];
	// quat[1] = inv * (ort[1][2] - ort[2][1]);
	// quat[2] = inv * (ort[2][0] - ort[0][2]);
	// quat[3] = inv * (ort[0][1] - ort[1][0]);
	// } else {
	// s = ort[0][0] - ort[1][1] - ort[2][2];
	// if (s > -0.19) {
	// // compute q1 and deduce q0, q2 and q3
	// quat[1] = 0.5 * Math.sqrt(s + 1.0);
	// final double inv = 0.25 / quat[1];
	// quat[0] = inv * (ort[1][2] - ort[2][1]);
	// quat[2] = inv * (ort[0][1] + ort[1][0]);
	// quat[3] = inv * (ort[0][2] + ort[2][0]);
	// } else {
	// s = ort[1][1] - ort[0][0] - ort[2][2];
	// if (s > -0.19) {
	// // compute q2 and deduce q0, q1 and q3
	// quat[2] = 0.5 * Math.sqrt(s + 1.0);
	// final double inv = 0.25 / quat[2];
	// quat[0] = inv * (ort[2][0] - ort[0][2]);
	// quat[1] = inv * (ort[0][1] + ort[1][0]);
	// quat[3] = inv * (ort[2][1] + ort[1][2]);
	// } else {
	// // compute q3 and deduce q0, q1 and q2
	// s = ort[2][2] - ort[0][0] - ort[1][1];
	// quat[3] = 0.5 * Math.sqrt(s + 1.0);
	// final double inv = 0.25 / quat[3];
	// quat[0] = inv * (ort[0][1] - ort[1][0]);
	// quat[1] = inv * (ort[0][2] + ort[2][0]);
	// quat[2] = inv * (ort[2][1] + ort[1][2]);
	// }
	// }
	// }
	// return quat;
	//
	// }

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
		if (squaredSine == 0) {
			return PLUS_I;
		} else {
			final double sgn = +1;
			if (q0 < 0) {
				final double inverse = sgn / Math.sqrt(squaredSine);
				return new GamaPoint(q1 * inverse, q2 * inverse, q3 * inverse);
			}
			final double inverse = -sgn / Math.sqrt(squaredSine);
			return new GamaPoint(q1 * inverse, q2 * inverse, q3 * inverse);
		}
	}

	/**
	 * Get the angle of the rotation in radians
	 *
	 * @return angle of the rotation (between 0 and &pi;)
	 * @see #Rotation(GamaPoint, double)
	 */
	public double getAngle() {
		if (q0 < -0.1 || q0 > 0.1) {
			return 2 * Math.asin(Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3));
		} else if (q0 < 0) { return 2 * Math.acos(-q0); }
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
	 * Apply the rotation to a vector stored in an array.
	 *
	 * @param in
	 *            an array with three items which stores the vector to rotate
	 */
	// public void applyTo(final double[] in) {
	//
	// final double x = in[0];
	// final double y = in[1];
	// final double z = in[2];
	//
	// final double s = q1 * x + q2 * y + q3 * z;
	//
	// in[0] = 2 * (q0 * (x * q0 - (q2 * z - q3 * y)) + s * q1) - x;
	// in[1] = 2 * (q0 * (y * q0 - (q3 * x - q1 * z)) + s * q2) - y;
	// in[2] = 2 * (q0 * (z * q0 - (q1 * y - q2 * x)) + s * q3) - z;
	//
	// }

	/**
	 * Apply the inverse of the rotation to a vector.
	 *
	 * @param u
	 *            vector to apply the inverse of the rotation to
	 */
	// public void applyInverseTo(final GamaPoint u) {
	//
	// final double x = u.x;
	// final double y = u.y;
	// final double z = u.z;
	//
	// final double s = q1 * x + q2 * y + q3 * z;
	// final double m0 = -q0;
	//
	// u.x = 2 * (m0 * (x * m0 - (q2 * z - q3 * y)) + s * q1) - x;
	// u.y = 2 * (m0 * (y * m0 - (q3 * x - q1 * z)) + s * q2) - y;
	// u.z = 2 * (m0 * (z * m0 - (q1 * y - q2 * x)) + s * q3) - z;
	//
	// }

	/**
	 * Apply the inverse of the rotation to a vector stored in an array.
	 *
	 * @param in
	 *            an array with three items which stores vector to rotate
	 * @param out
	 *            an array with three items to put result to (it can be the same array as in)
	 */
	// public void applyInverseTo(final double[] in, final double[] out) {
	//
	// final double x = in[0];
	// final double y = in[1];
	// final double z = in[2];
	//
	// final double s = q1 * x + q2 * y + q3 * z;
	// final double m0 = -q0;
	//
	// out[0] = 2 * (m0 * (x * m0 - (q2 * z - q3 * y)) + s * q1) - x;
	// out[1] = 2 * (m0 * (y * m0 - (q3 * x - q1 * z)) + s * q2) - y;
	// out[2] = 2 * (m0 * (z * m0 - (q1 * y - q2 * x)) + s * q3) - z;
	//
	// }

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
	 * Apply the inverse of the instance to another rotation.
	 * <p>
	 *
	 * </p>
	 *
	 * @param r
	 *            rotation to apply the rotation to
	 * @return a new rotation which is the composition of r by the inverse of the instance
	 */
	// public Rotation3D applyInverseTo(final Rotation3D r) {
	// return new Rotation3D(-r.q0 * q0 - (r.q1 * q1 + r.q2 * q2 + r.q3 * q3),
	// -r.q1 * q0 + r.q0 * q1 + (r.q2 * q3 - r.q3 * q2), -r.q2 * q0 + r.q0 * q2 + (r.q3 * q1 - r.q1 * q3),
	// -r.q3 * q0 + r.q0 * q3 + (r.q1 * q2 - r.q2 * q1), false);
	// }

	public static Rotation3D identity() {
		return new Rotation3D(1, 0, 0, 0, false);
	}

	/**
	 * Applies r to this, transforming the rotation in place
	 *
	 * @param r
	 *            another Rotation3D
	 */

	// public void apply(final Rotation3D r) {
	// equivalent to r.applyTo(this), but in place
	// q0 = -r.q0 * q0 - (r.q1 * q1 + r.q2 * q2 + r.q3 * q3);
	// q1 = -r.q1 * q0 + r.q0 * q1 + (r.q2 * q3 - r.q3 * q2);
	// q2 = -r.q2 * q0 + r.q0 * q2 + (r.q3 * q1 - r.q1 * q3);
	// q3 = -r.q3 * q0 + r.q0 * q3 + (r.q1 * q2 - r.q2 * q1);
	// }

	/**
	 * Applies this rotation to a Geometry (to be used as a CoordinateFilter for JTS geometries)
	 */
	@Override
	public void filter(final Coordinate c) {
		applyTo(c);
	}

}
