/*******************************************************************************************************
 *
 * DXFEllipse.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import msi.gama.ext.kabeja.dxf.helpers.Point;
import msi.gama.ext.kabeja.dxf.helpers.Vector;
import msi.gama.ext.kabeja.math.MathUtils;
import msi.gama.ext.kabeja.math.ParametricPlane;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFEllipse extends DXFEntity {

	/** The Constant DEFAULT_END_PARAMETER. */
	public static final double DEFAULT_END_PARAMETER = Math.PI * 2;

	/** The Constant DEFAULT_START_PARAMETER. */
	public static final double DEFAULT_START_PARAMETER = 0.0;

	/** The Constant INTEGRATION_STEPS. */
	public static final int INTEGRATION_STEPS = 15;

	/** The ratio. */
	private double ratio = 1.0;

	/** The start parameter. */
	private double startParameter = DEFAULT_START_PARAMETER;

	/** The end parameter. */
	private double endParameter = DEFAULT_END_PARAMETER;

	/** The center. */
	private Point center = new Point();

	/** The major axis direction. */
	private Vector majorAxisDirection = new Vector();

	/** The counterclockwise. */
	private boolean counterclockwise;

	/**
	 *
	 */
	public DXFEllipse() {
		center = new Point();
	}

	@Override
	public Bounds getBounds() {
		double alpha = this.getRotationAngle();
		Bounds bounds = new Bounds();

		ParametricPlane plane = new ParametricPlane(this.center, this.getExtrusion().getDirectionX(),
				this.getExtrusion().getDirectionY(), this.getExtrusion().getNormal());

		if (this.startParameter == DEFAULT_START_PARAMETER && this.endParameter == DEFAULT_END_PARAMETER
				&& alpha == 0.0) {
			double length = this.getHalfMajorAxisLength();

			bounds.addToBounds(plane.getPoint(length, length));
			bounds.addToBounds(plane.getPoint(-length, -length));
		} else {
			int n = 40;

			// we walking over the the ellipse or elliptical arc
			double h = (this.endParameter - this.startParameter) / n;

			double start = this.startParameter;
			// double major = this.getHalfMajorAxisLength();
			// double minor = major * this.ratio;

			Vector minorAxis = MathUtils.crossProduct(this.getExtrusion().getNormal(), this.getMajorAxisDirection());
			minorAxis = MathUtils.scaleVector(minorAxis, this.ratio);

			for (int i = 0; i <= n; i++) {
				Vector v1 = MathUtils.scaleVector(this.getMajorAxisDirection(), Math.cos(start));
				Vector v2 = MathUtils.scaleVector(minorAxis, Math.sin(start));

				// double x = major * Math.cos(start);
				// double y = minor * Math.sin(start);
				double x = v1.getX() + v2.getX();
				double y = v1.getY() + v2.getY();

				// if (alpha != 0.0) {
				// double lx = x;
				// x = lx * Math.cos(alpha) - y * Math.sin(alpha);
				// y = lx * Math.sin(alpha) + y * Math.cos(alpha);
				// }
				Point p = plane.getPoint(x, y);

				bounds.addToBounds(p);

				start += h;
			}
		}

		return bounds;
	}

	/**
	 * Gets the center point.
	 *
	 * @return the center point
	 */
	public Point getCenterPoint() { return center; }

	/**
	 * Sets the center point.
	 *
	 * @param center
	 *            the new center point
	 */
	public void setCenterPoint(final Point center) { this.center = center; }

	/**
	 * Gets the major axis direction.
	 *
	 * @return the major axis direction
	 */
	public Vector getMajorAxisDirection() { return majorAxisDirection; }

	/**
	 * Sets the major axis direction.
	 *
	 * @param d
	 *            the new major axis direction
	 */
	public void setMajorAxisDirection(final Vector d) { this.majorAxisDirection = d; }

	/**
	 * Gets the end parameter.
	 *
	 * @return the end parameter
	 */
	public double getEndParameter() { return endParameter; }

	/**
	 * Sets the end parameter.
	 *
	 * @param endParameter
	 *            the new end parameter
	 */
	public void setEndParameter(final double endParameter) {
		if (endParameter < 0) {
			this.endParameter = Math.PI * 2 + endParameter;
		} else {
			this.endParameter = endParameter;
		}
	}

	/**
	 * Gets the ratio.
	 *
	 * @return the ratio
	 */
	public double getRatio() { return ratio; }

	/**
	 * Sets the ratio.
	 *
	 * @param ratio
	 *            the new ratio
	 */
	public void setRatio(final double ratio) { this.ratio = ratio; }

	/**
	 * Gets the start parameter.
	 *
	 * @return the start parameter
	 */
	public double getStartParameter() { return startParameter; }

	/**
	 * Sets the start parameter.
	 *
	 * @param startParameter
	 *            the new start parameter
	 */
	public void setStartParameter(final double startParameter) {
		if (startParameter < 0) {
			this.startParameter = Math.PI * 2 + startParameter;
		} else {
			this.startParameter = startParameter;
		}
	}

	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_ELLIPSE; }

	/**
	 * Gets the half major axis length.
	 *
	 * @return the half major axis length
	 */
	public double getHalfMajorAxisLength() { return this.majorAxisDirection.getLength(); }

	/**
	 * Gets the local point at.
	 *
	 * @param para
	 *            the para
	 * @return the local point at
	 */
	public Point getLocalPointAt(final double para) {
		Point p = new Point();
		double major = getHalfMajorAxisLength();
		double minor = major * this.ratio;
		double x = major * Math.cos(para);
		double y = minor * Math.sin(para);
		double alpha = this.getRotationAngle();

		if (alpha != 0.0) {
			double lx = x;
			x = lx * Math.cos(alpha) - y * Math.sin(alpha);
			y = lx * Math.sin(alpha) + y * Math.cos(alpha);
		}

		p.setX(x);
		p.setY(y);
		p.setZ(0.0);

		return p;
	}

	/**
	 * Calculate a Point in world coordinates for the given parameter on the ellipse.
	 *
	 * @param para
	 *            in double (between 0.0 and 2*PI)
	 * @return the point of the ellipse in world coordinates
	 */
	public Point getPointAt(final double para) {
		ParametricPlane plane = new ParametricPlane(this.center, this.getExtrusion().getDirectionX(),
				this.getExtrusion().getDirectionY(), this.getExtrusion().getNormal());
		Vector minorAxis = MathUtils.crossProduct(this.getExtrusion().getNormal(), this.getMajorAxisDirection());
		minorAxis = MathUtils.scaleVector(minorAxis, this.ratio);

		Vector v1 = MathUtils.scaleVector(this.getMajorAxisDirection(), Math.cos(para));
		Vector v2 = MathUtils.scaleVector(minorAxis, Math.sin(para));
		double x = v1.getX() + v2.getX();
		double y = v1.getY() + v2.getY();
		return plane.getPoint(x, y);
	}

	/**
	 * Gets the local start point.
	 *
	 * @return the local start point
	 */
	public Point getLocalStartPoint() { return this.getLocalPointAt(this.startParameter); }

	/**
	 * Gets the local end point.
	 *
	 * @return the local end point
	 */
	public Point getLocalEndPoint() { return this.getLocalPointAt(this.endParameter); }

	/**
	 * Gets the rotation angle.
	 *
	 * @return the rotation angle
	 */
	public double getRotationAngle() {
		return MathUtils.getAngle(DXFConstants.DEFAULT_X_AXIS_VECTOR, majorAxisDirection);
	}

	@Override
	public double getLength() {
		int n = INTEGRATION_STEPS;
		double h = (this.endParameter - this.startParameter) / n;

		double a = this.getHalfMajorAxisLength();
		double b = a * this.ratio;
		double start = this.startParameter;

		double end = 0.0;
		double length = 0.0;

		// length = integral (sqrt((major*sin(t))^2+(minor*cos(t))^2))
		for (int i = 0; i < n; i++) {
			double center = h / 2 + start;
			end = start + h;

			double w1 = Math.sqrt(Math.pow(a * Math.sin(start), 2) + Math.pow(b * Math.cos(start), 2));
			double w2 = Math.sqrt(Math.pow(a * Math.sin(center), 2) + Math.pow(b * Math.cos(center), 2));
			double w3 = Math.sqrt(Math.pow(a * Math.sin(end), 2) + Math.pow(b * Math.cos(end), 2));
			// SIMPSON where (h/2)/3 is h/6 here
			length += (w1 + 4 * w2 + w3) * (h / 6);
			start = end;
		}

		return length;
	}

	/**
	 * Checks if is counter clockwise.
	 *
	 * @return true, if is counter clockwise
	 */
	public boolean isCounterClockwise() { return counterclockwise; }

	/**
	 * Sets the counter clockwise.
	 *
	 * @param counterclockwise
	 *            the new counter clockwise
	 */
	public void setCounterClockwise(final boolean counterclockwise) { this.counterclockwise = counterclockwise; }
}
