/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.shape;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * The class ILocation.
 * 
 * @author drogoul
 * @since 15 déc. 2011
 * 
 */
@vars({ @var(name = IKeyword.X, type = IType.FLOAT_STR),
	@var(name = IKeyword.Y, type = IType.FLOAT_STR),
	@var(name = IKeyword.Z, type = IType.FLOAT_STR)
})
public interface ILocation extends IShape, Comparable {

	@getter( IKeyword.X)
	public abstract double getX();

	@getter( IKeyword.Y)
	public abstract double getY();
	

	// public abstract boolean equals(final Coordinate o);
	@getter(IKeyword.Z)
	public abstract double getZ(); 


	public abstract void setLocation(final double xx, final double yy);

	public abstract void setLocation(final double xx, final double yy, final double zz);

	public abstract void add(ILocation p);

	public abstract Coordinate toCoordinate();

	@Override
	public abstract double euclidianDistanceTo(ILocation targ);

	@Override
	public ILocation copy();
	
	public boolean hasZ();

}