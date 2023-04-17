/*******************************************************************************************************
 *
 * ILocation.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.runtime.IScope;

/**
 * The class ILocation.
 *
 * @author drogoul
 * @since 15 d�c. 2011
 *
 */
@SuppressWarnings ("rawtypes")
@Deprecated
public interface ILocation extends IShape, Comparable<Coordinate> {

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	double getX();

	/**
	 * Sets the x.
	 *
	 * @param x the new x
	 */
	void setX(double x);

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	double getY();

	/**
	 * Sets the y.
	 *
	 * @param y the new y
	 */
	void setY(double y);

	/**
	 * Gets the z.
	 *
	 * @return the z
	 */
	// public abstract boolean equals(final Coordinate o);
	double getZ();

	/**
	 * Sets the z.
	 *
	 * @param z the new z
	 */
	void setZ(double z);

	/**
	 * Adds the.
	 *
	 * @param p the p
	 */
	void add(ILocation p);

	/**
	 * Euclidian distance to.
	 *
	 * @param targ the targ
	 * @return the double
	 */
	double euclidianDistanceTo(ILocation targ);

	/**
	 * Copy.
	 *
	 * @param scope the scope
	 * @return the i location
	 */
	@Override
	ILocation copy(IScope scope);

	/**
	 * To gama point.
	 *
	 * @return the gama point
	 */
	GamaPoint toGamaPoint();

	/**
	 * Equals with tolerance.
	 *
	 * @param c the c
	 * @param tolerance the tolerance
	 * @return true, if successful
	 */
	boolean equalsWithTolerance(Coordinate c, double tolerance);

}