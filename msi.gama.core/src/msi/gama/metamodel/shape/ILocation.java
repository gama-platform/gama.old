/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.ILocation.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	double getX();

	void setX(double x);

	double getY();

	void setY(double y);

	// public abstract boolean equals(final Coordinate o);
	double getZ();

	void setZ(double z);

	void add(ILocation p);

	double euclidianDistanceTo(ILocation targ);

	@Override
	ILocation copy(IScope scope);

	GamaPoint toGamaPoint();

	boolean equalsWithTolerance(Coordinate c, double tolerance);

}