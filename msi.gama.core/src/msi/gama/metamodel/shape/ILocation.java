/*********************************************************************************************
 * 
 *
 * 'ILocation.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.shape;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * The class ILocation.
 * 
 * @author drogoul
 * @since 15 d�c. 2011
 * 
 */
@vars({ @var(name = IKeyword.X, type = IType.FLOAT), @var(name = IKeyword.Y, type = IType.FLOAT),
	@var(name = IKeyword.Z, type = IType.FLOAT) })
public interface ILocation extends IShape, Comparable {

	@getter(IKeyword.X)
	public abstract double getX();

	public abstract void setX(double x);

	@getter(IKeyword.Y)
	public abstract double getY();

	public abstract void setY(double y);

	// public abstract boolean equals(final Coordinate o);
	@getter(IKeyword.Z)
	public abstract double getZ();

	public abstract void setZ(double z);

	public abstract void setLocation(final double ... coords);

	public abstract void add(ILocation p);

	public abstract Coordinate toCoordinate();

	@Override
	public abstract double euclidianDistanceTo(ILocation targ);

	@Override
	public ILocation copy(IScope scope);

}