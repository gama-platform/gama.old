/*******************************************************************************************************
 *
 * msi.gama.common.geometry.AxisAngle.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package msi.gama.common.geometry;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * A four-element axis angle represented by double-precision floating point x,y,z,angle components. An axis angle is a
 * rotation of angle (radians) about the vector (x,y,z).
 *
 * @adapted from Vecmath by A. Drogoul (2017)
 *
 */
public class AxisAngle implements java.io.Serializable, Cloneable {

	/**
	 * The Axis around which the rotation is done
	 */
	public final GamaPoint axis = new GamaPoint(Rotation3D.PLUS_K);

	/**
	 * The angle of rotation in degrees.
	 */
	public double angle;

	/**
	 * Constructs and initializes an AxisAngle4 from the specified axis and angle. If the axis is null, the default
	 * PLUS_K is chosen
	 *
	 * @param axis
	 *            the axis
	 * @param angle
	 *            the angle of rotation in radian
	 *
	 * @since vecmath 1.2
	 */
	public AxisAngle(final GamaPoint axis, final double angle) {
		if (axis != null) {
			this.axis.setLocation(axis);
		} else {
			this.axis.setLocation(Rotation3D.PLUS_K);
		}
		this.angle = angle;
	}

	public AxisAngle(final Double angle) {
		this.angle = angle == null ? 0 : angle.doubleValue();
	}

	/**
	 * Constructs and initializes an AxisAngle4d to (0,0,1,0).
	 */
	public AxisAngle() {
		axis.setLocation(0, 0, 0);
		this.angle = 0.0;
	}

	/**
	 * Returns a string that contains the values of this AxisAngle4d. The form is (x,y,z,angle).
	 *
	 * @return the String representation
	 */
	@Override
	public String toString() {
		return "(" + axis.x + ", " + axis.y + ", " + axis.z + ", " + this.angle + ")";
	}

	/**
	 * Returns true if all of the data members of AxisAngle4d a1 are equal to the corresponding data members in this
	 * AxisAngle4d.
	 *
	 * @param a1
	 *            the axis-angle with which the comparison is made
	 * @return true or false
	 */
	public boolean equals(final AxisAngle a1) {
		if (a1 == null) { return false; }
		return axis.equals(a1.axis) && this.angle == a1.angle;

	}

	/**
	 * Returns true if the Object o1 is of type AxisAngle4 and all of the data members of o1 are equal to the
	 * corresponding data members in this AxisAngle4.
	 *
	 * @param o1
	 *            the object with which the comparison is made
	 * @return true or false
	 */
	@Override
	public boolean equals(final Object o1) {
		if (o1 instanceof AxisAngle) { return equals((AxisAngle) o1); }
		return false;
	}

	/**
	 * Returns a hash code value based on the data values in this object. Two different AxisAngle4 objects with
	 * identical data values (i.e., AxisAngle4.equals returns true) will return the same hash code value. Two objects
	 * with different data members may return the same hash value, although this is not likely.
	 *
	 * @return the integer hash code value
	 */
	@Override
	public int hashCode() {
		long bits = 1L;
		bits = 31L * bits + Double.doubleToLongBits(axis.x);
		bits = 31L * bits + Double.doubleToLongBits(axis.y);
		bits = 31L * bits + Double.doubleToLongBits(axis.z);
		bits = 31L * bits + Double.doubleToLongBits(angle);
		return (int) (bits ^ bits >> 32);
	}

	/**
	 * Creates a new object of the same class as this object.
	 *
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since vecmath 1.3
	 */
	@Override
	public Object clone() {
		// Since there are no arrays we can just use Object.clone()
		try {
			return super.clone();
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	/**
	 * Get the axis angle, in degrees.<br>
	 * An axis angle is a rotation angle about the vector (x,y,z).
	 *
	 * @return the angle, in degrees.
	 */
	public final double getAngle() {
		return angle;
	}

	/**
	 * Set the axis angle, in degrees.<br>
	 * An axis angle is a rotation angle about the vector (x,y,z).
	 *
	 * @param angle
	 *            The angle to set, in degrees.
	 *
	 */
	public final void setAngle(final double angle) {
		this.angle = angle;
	}

	/**
	 * Get value of <i>x</i> coordinate.
	 *
	 * @return the <i>x</i> coordinate.
	 *
	 */
	public double getX() {
		return axis.x;
	}

	/**
	 * Set a new value for <i>x</i> coordinate.
	 *
	 * @param x
	 *            the <i>x</i> coordinate.
	 *
	 */
	public final void setX(final double x) {
		axis.x = x;
	}

	/**
	 * Get value of <i>y</i> coordinate.
	 *
	 * @return the <i>y</i> coordinate.
	 *
	 */
	public final double getY() {
		return axis.y;
	}

	/**
	 * Set a new value for <i>y</i> coordinate.
	 *
	 * @param y
	 *            the <i>y</i> coordinate.
	 *
	 */
	public final void setY(final double y) {
		axis.y = y;
	}

	/**
	 * Get value of <i>z</i> coordinate.
	 *
	 * @return the <i>z</i> coordinate.
	 *
	 */
	public double getZ() {
		return axis.z;
	}

	/**
	 * Set a new value for <i>z</i> coordinate.
	 *
	 * @param z
	 *            the <i>z</i> coordinate.
	 *
	 */
	public final void setZ(final double z) {
		axis.z = z;
	}

	public GamaPoint getAxis() {
		return axis;
	}

}
