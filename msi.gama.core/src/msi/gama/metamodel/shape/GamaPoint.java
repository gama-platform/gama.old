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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.shape;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gaml.operators.Maths;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.*;

/**
 * AgentLocation.
 * 
 * @author drogoul 11 oct. 07
 */

public class GamaPoint extends Coordinate implements ILocation {

	public boolean hasZ;
	
	public GamaPoint(final double xx, final double yy) {
		x = xx;
		y = yy;
		hasZ = false;
	}
	
	public GamaPoint(final double xx, final double yy,final double zz) {
		x = xx;
		y = yy;
		z = zz;
		hasZ = !(z + "") .equals("NaN");
	}

	public GamaPoint(final Coordinate coord) {
		x = coord.x;
		y = coord.y;
		z = coord.z;
		hasZ = !(z + "") .equals("NaN");
	}

	public GamaPoint(final ILocation point) {
		x = point.getX();
		y = point.getY();
		z = point.getZ();
		hasZ = !(z + "") .equals("NaN");
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}
	
	@Override
	public double getZ() {
		hasZ = !(z + "") .equals("NaN");
		if (hasZ)
			return super.z;
		return 0.0;
	}

	@Override
	public void setLocation(final ILocation al) {
		x = al.getX();
		y = al.getY();
		z = al.getZ();
		hasZ = !(z + "") .equals("NaN");
	}

	@Override
	public boolean isPoint() {
		return true;
	}

	// @Override
	// public boolean equals(final Object o) {
	// if ( o instanceof Coordinate ) { return IntervalSize.isZeroWidth(x, ((Coordinate) o).x) &&
	// IntervalSize.isZeroWidth(y, ((Coordinate) o).y); }
	// return false;
	// }

	// @Override
	// public boolean equals(final Coordinate o) {
	// return o != null && IntervalSize.isZeroWidth(x, o.x) && IntervalSize.isZeroWidth(y, o.y);
	// }

	/*@Override
	public int hashCode() {
		return (int) (x * y + x);
	}*/

	@Override
	public void setLocation(final double xx, final double yy) {
		x = xx;
		y = yy;
		hasZ = false;
	}
	
	public void setLocation(final double xx, final double yy, final double zz) {
		x = xx;
		y = yy;
		z = zz;
		hasZ = !(z + "") .equals("NaN");
	}

	@Override
	public String toString() {
		if (hasZ)
			return "location[" + x + ";" + y +  ";" + z + "]";
		return "location[" + x + ";" + y + "]";
	}

	@Override
	public String toGaml() {
		String zStr = hasZ ? "," + z : "";
		return "{" + x + "," + y+ zStr + "}";
	}

	//
	// @Override
	// public String toJava() {
	// return Cast.class.getCanonicalName() + "toPoint(" + Cast.toJava(x) + "," + Cast.toJava(y) +
	// ")";
	// }

	@Override
	public GamaPoint getLocation() {
		return this;
	}

	//
	// @Override
	// // @operator(value = "list", can_be_const = true, content_type = IType.FLOAT)
	// public GamaList listValue(final IScope scope) {
	// return GamaList.with(x, y/* , z */);
	// }
	//
	// @Override
	// // @operator(value = "matrix", can_be_const = true)
	// public IMatrix matrixValue(final IScope scope) {
	// return new GamaObjectMatrix((int) x, (int) y);
	// }
	//
	// @Override
	// // @operator(value = "as_matrix", can_be_const = true, content_type =
	// ITypeProvider.LEFT_TYPE,
	// // priority = IPriority.CAST)
	// public IMatrix matrixValue(final IScope scope, final GamaPoint preferredSize)
	// throws GamaRuntimeException {
	// if ( preferredSize == null ) { return GamaMatrixType.with(null, this); }
	// return GamaMatrixType.with(this, preferredSize);
	// }

	@Override
	public String stringValue() {
		String zStr = hasZ ? "," + z : "";
		return "{" + x + "," + y+ zStr + "}";
	}

	@Override
	public IType type() {
		return Types.get(IType.POINT);
	}

	@Override
	public void add(final ILocation loc) {
		x = x + loc.getX();
		y = y + loc.getY();
		if (hasZ && !(loc.getZ() + "") .equals("NaN"))
			z = z + loc.getZ();
	}

	@Override
	public Coordinate toCoordinate() {
		return new Coordinate(x, y, z);
	}

	@getter( "x")
	public Double first() {
		return x;
	}

	@getter( "y")
	public Double last() {
		return y;
	}

	@Override
	public GamaPoint copy() {
		return new GamaPoint(x, y, z);
	}

	@Override
	public IShape getGeometry() {
		return GamaGeometryType.createPoint(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGeometry#setGeometry(msi.gama.util.GamaGeometry)
	 */
	@Override
	public void setGeometry(final IShape g) {
		setLocation(g.getLocation());
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		return getGeometry().getInnerGeometry();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getEnvelope()
	 */
	@Override
	public Envelope getEnvelope() {
		return new Envelope(this);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		if ( g.isPoint() ) { return g.getLocation().equals(this); }
		return false;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		if ( g.isPoint() ) { return euclidianDistanceTo(g.getLocation()); }
		return g.euclidianDistanceTo(this);
	}

	@Override
	public double euclidianDistanceTo(final ILocation p) {
		if (hasZ && ((GamaPoint)p).hasZ)
			return Maths.hypot(x, p.getX(), y, p.getY(),z, p.getZ());
		return Maths.hypot(x, p.getX(), y, p.getY());
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		if ( g.isPoint() ) { return g.getLocation().equals(this); }
		return g.intersects(this);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return null;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#setAgent(msi.gama.interfaces.IAgent)
	 */
	@Override
	public void setAgent(final IAgent agent) {}

	/**
	 * @see msi.gama.interfaces.IGeometry#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return 0d;
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry point) {
		Coordinate p = point.getCoordinate();
		setLocation(p.x, p.y, p.z);
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#dispose()
	 */
	@Override
	public void dispose() {}

	@Override
	public boolean hasZ() {
		return hasZ;
	}

	

}
