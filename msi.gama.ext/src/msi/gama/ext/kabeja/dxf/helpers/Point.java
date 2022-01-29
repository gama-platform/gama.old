/*******************************************************************************************************
 *
 * Point.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.helpers;

import java.util.Objects;

import msi.gama.ext.kabeja.dxf.DXFConstants;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 *
 *
 */
public class Point {
	
	/** The x. */
	protected double x = 0.0;
	
	/** The y. */
	protected double y = 0.0;
	
	/** The z. */
	protected double z = 0.0;

	/**
	 * Instantiates a new point.
	 */
	public Point() {}

	/**
	 * Instantiates a new point.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public Point(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return Returns the x.
	 */
	public double getX() { return x; }

	/**
	 * @param x
	 *            The x to set.
	 */
	public void setX(final double x) { this.x = x; }

	/**
	 * @return Returns the y.
	 */
	public double getY() { return y; }

	/**
	 * @param y
	 *            The y to set.
	 */
	public void setY(final double y) { this.y = y; }

	/**
	 * @return Returns the z.
	 */
	public double getZ() { return z; }

	/**
	 * @param z
	 *            The z to set.
	 */
	public void setZ(final double z) { this.z = z; }

	@Override
	public String toString() {
		return super.toString() + "[" + this.x + "," + this.y + "," + this.z + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Point) {
			Point p = (Point) obj;
			double d = DXFConstants.POINT_CONNECTION_RADIUS;

			if (Math.abs(x - p.getX()) <= d && Math.abs(y - p.getY()) <= d) return Math.abs(z - p.getZ()) <= d;
		}

		return false;
	}
}
