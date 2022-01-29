/*******************************************************************************************************
 *
 * DXFSpline.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import msi.gama.ext.kabeja.dxf.helpers.DXFSplineConverter;
import msi.gama.ext.kabeja.dxf.helpers.SplinePoint;

/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class DXFSpline extends DXFEntity {
	
	/** The Constant APPROXIMATION_STEPS. */
	protected static final int APPROXIMATION_STEPS = 10;
	
	/** The degree. */
	protected int degree;
	
	/** The node points size. */
	protected int nodePointsSize;
	
	/** The control point size. */
	protected int controlPointSize;
	
	/** The fit point size. */
	protected int fitPointSize;
	
	/** The knots. */
	protected double[] knots;
	
	/** The weights. */
	protected double[] weights;
	
	/** The points. */
	protected List<SplinePoint> points = new ArrayList<>();
	
	/** The fit tolerance. */
	protected double fitTolerance;
	
	/** The knots tolerance. */
	protected double knotsTolerance;
	
	/** The control point tolerance. */
	protected double controlPointTolerance;
	
	/** The polyline. */
	DXFPolyline polyline;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		// simple the convex hull of the spline
		// Iterator i = points.iterator();
		//
		// while (i.hasNext()) {
		// SplinePoint p = (SplinePoint) i.next();
		// bounds.addToBounds(p);
		// }
		//
		// return bounds;

		// more correct bounds
		if (this.polyline == null) { this.polyline = toDXFPolyline(); }

		return this.polyline.getBounds();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.miethxml.kabeja.dxf.DXFEntity#getType()
	 */
	@Override
	public String getType() { return DXFConstants.ENTITY_TYPE_SPLINE; }

	/**
	 * Adds the spline point.
	 *
	 * @param p the p
	 */
	public void addSplinePoint(final SplinePoint p) {
		this.points.add(p);
		this.polyline = null;
	}

	/**
	 * Gets the spline point iterator.
	 *
	 * @return the spline point iterator
	 */
	public Iterator getSplinePointIterator() { return points.iterator(); }

	/**
	 * Checks if is rational.
	 *
	 * @return true, if is rational
	 */
	public boolean isRational() { return (this.flags & 4) == 4; }

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	public boolean isClosed() { return (this.flags & 1) == 1; }

	/**
	 * Checks if is periodic.
	 *
	 * @return true, if is periodic
	 */
	public boolean isPeriodic() { return (this.flags & 2) == 2; }

	/**
	 * Checks if is planar.
	 *
	 * @return true, if is planar
	 */
	public boolean isPlanar() { return (this.flags & 8) == 8; }

	/**
	 * Checks if is linear.
	 *
	 * @return true, if is linear
	 */
	public boolean isLinear() { return (this.flags & 16) == 16; }

	/**
	 * @return Returns the controlPointSize.
	 */
	public int getControlPointSize() { return controlPointSize; }

	/**
	 * @param controlPointSize
	 *            The controlPointSize to set.
	 */
	public void setControlPointSize(final int controlPointSize) { this.controlPointSize = controlPointSize; }

	/**
	 * @return Returns the degree.
	 */
	public int getDegree() { return degree; }

	/**
	 * @param degree
	 *            The degree to set.
	 */
	public void setDegree(final int degree) { this.degree = degree; }

	/**
	 * @return Returns the fitPointSize.
	 */
	public int getFitPointSize() { return fitPointSize; }

	/**
	 * @param fitPointSize
	 *            The fitPointSize to set.
	 */
	public void setFitPointSize(final int fitPointSize) { this.fitPointSize = fitPointSize; }

	/**
	 * @return Returns the fitTolerance.
	 */
	public double getFitTolerance() { return fitTolerance; }

	/**
	 * @param fitTolerance
	 *            The fitTolerance to set.
	 */
	public void setFitTolerance(final double fitTolerance) { this.fitTolerance = fitTolerance; }

	/**
	 * @return Returns the knots.
	 */
	public double[] getKnots() { return knots; }

	/**
	 * @param knots
	 *            The knots to set.
	 */
	public void setKnots(final double[] knots) {
		this.knots = knots;
		this.polyline = null;
	}

	/**
	 * @return Returns the nodePointsSize.
	 */
	public int getNodePointsSize() { return nodePointsSize; }

	/**
	 * @param nodePointsSize
	 *            The nodePointsSize to set.
	 */
	public void setNodePointsSize(final int nodePointsSize) { this.nodePointsSize = nodePointsSize; }

	/**
	 * @return Returns the weights.
	 */
	public double[] getWeights() { return weights; }

	/**
	 * @param weights
	 *            The weights to set.
	 */
	public void setWeights(final double[] weights) { this.weights = weights; }

	/**
	 * @return Returns the controlPointTolerance.
	 */
	public double getControlPointTolerance() { return controlPointTolerance; }

	/**
	 * @param controlPointTolerance
	 *            The controlPointTolerance to set.
	 */
	public void setControlPointTolerance(final double controlPointTolerance) {
		this.controlPointTolerance = controlPointTolerance;
	}

	/**
	 * @return Returns the knotsTolerance.
	 */
	public double getKnotsTolerance() { return knotsTolerance; }

	/**
	 * @param knotsTolerance
	 *            The knotsTolerance to set.
	 */
	public void setKnotsTolerance(final double knotsTolerance) { this.knotsTolerance = knotsTolerance; }

	@Override
	public double getLength() {
		if (this.polyline == null) { this.polyline = toDXFPolyline(); }

		return this.polyline.getLength();
	}

	/**
	 * To DXF polyline.
	 *
	 * @return the DXF polyline
	 */
	protected DXFPolyline toDXFPolyline() {
		return DXFSplineConverter.toDXFPolyline(this);
	}
}
