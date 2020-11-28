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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

/**
 * The class ILocation.
 *
 * @author drogoul
 * @since 15 d�c. 2011
 *
 */
@SuppressWarnings ("rawtypes")
@vars ({ @variable (
		name = IKeyword.X,
		type = IType.FLOAT,
		doc = { @doc ("Returns the x ordinate of this point") }),
		@variable (
				name = IKeyword.Y,
				type = IType.FLOAT,
				doc = { @doc ("Returns the y ordinate of this point") }),
		@variable (
				name = IKeyword.Z,
				type = IType.FLOAT,
				doc = { @doc ("Returns the z ordinate of this point") }) })
public interface ILocation extends IShape, Comparable<Coordinate> {

	@getter (IKeyword.X)
	double getX();

	void setX(double x);

	@getter (IKeyword.Y)
	double getY();

	void setY(double y);

	// public abstract boolean equals(final Coordinate o);
	@getter (IKeyword.Z)
	double getZ();

	void setZ(double z);

	void add(ILocation p);

	@Override
	double euclidianDistanceTo(ILocation targ);

	@Override
	ILocation copy(IScope scope);

	GamaPoint toGamaPoint();

	ILocation yNegated();

	boolean equalsWithTolerance(Coordinate c, double tolerance);

	ILocation withPrecision(int i);

	default int compareTo(ILocation location) {
		return compareTo((Coordinate)toGamaPoint());
	}

}