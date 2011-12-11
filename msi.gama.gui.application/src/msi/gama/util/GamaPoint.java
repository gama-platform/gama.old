/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import java.util.Iterator;
import msi.gama.interfaces.*;
import msi.gama.internal.types.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.matrix.GamaObjectMatrix;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.index.quadtree.IntervalSize;

/**
 * AgentLocation.
 * 
 * @author drogoul 11 oct. 07
 */
@vars({ @var(name = GamaPoint.X, type = IType.FLOAT_STR),
	@var(name = GamaPoint.Y, type = IType.FLOAT_STR) })
public class GamaPoint extends Coordinate implements IGeometry, IGamaContainer<Integer, Double> {

	public static final String X = "x";

	public static final String Y = "y";

	public GamaPoint(final double xx, final double yy) {
		x = xx;
		y = yy;
	}

	public GamaPoint(final Coordinate coord) {
		x = coord.x;
		y = coord.y;
	}

	public GamaPoint(final GamaPoint point) {
		x = point.x;
		y = point.y;
	}

	@Override
	public void setLocation(final GamaPoint al) {
		x = al.x;
		y = al.y;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

	@Override
	public boolean equals(final Object o) {
		if ( o instanceof Coordinate ) { return IntervalSize.isZeroWidth(x, ((Coordinate) o).x) &&
			IntervalSize.isZeroWidth(y, ((Coordinate) o).y); }
		return false;
	}

	public boolean equals(final Coordinate o) {
		return o != null && IntervalSize.isZeroWidth(x, o.x) && IntervalSize.isZeroWidth(y, o.y);
	}

	@Override
	public int hashCode() {
		return (int) (x * y + x);
	}

	public void setLocation(final double xx, final double yy) {
		x = xx;
		y = yy;
	}

	@Override
	public String toString() {
		return "location[" + x + ";" + y + "]";
	}

	@Override
	public String toGaml() {
		return "{" + x + "," + y + "}";
	}

	@Override
	public String toJava() {
		return Cast.class.getCanonicalName() + "toPoint(" + Cast.toJava(x) + "," + Cast.toJava(y) +
			")";
	}

	@Override
	public GamaPoint getLocation() {
		return this;
	}

	@Override
	// @operator(value = "list", can_be_const = true, content_type = IType.FLOAT)
	public GamaList listValue(final IScope scope) {
		return GamaList.with(x, y/* , z */);
	}

	@Override
	// @operator(value = "matrix", can_be_const = true)
	public IMatrix matrixValue(final IScope scope) {
		return new GamaObjectMatrix((int) x, (int) y);
	}

	@Override
	// @operator(value = "as_matrix", can_be_const = true, content_type = ITypeProvider.LEFT_TYPE,
	// priority = IPriority.CAST)
	public IMatrix matrixValue(final IScope scope, final GamaPoint preferredSize)
		throws GamaRuntimeException {
		if ( preferredSize == null ) { return GamaMatrixType.with(null, this); }
		return GamaMatrixType.with(this, preferredSize);
	}

	@Override
	public String stringValue() {
		return "{" + x + ";" + y + "}";
	}

	@Override
	public IType type() {
		return Types.get(IType.POINT);
	}

	public void add(final double delta, final double delta2) {
		x = x + delta;
		y = y + delta2;
	}

	@Override
	public GamaMap mapValue(final IScope scope) {
		final GamaMap result = new GamaMap();
		result.put("x", x);
		result.put("y", y);
		return result;
	}

	@Override
	public void add(final Double value, final Object param) {}

	@Override
	public void add(final Integer index, final Double value, final Object param) {}

	@Override
	public boolean removeAll(final IGamaContainer<?, Double> value) {
		return false;
	}

	@Override
	public boolean removeFirst(final Double value) {
		return false;
	}

	@Override
	public Double removeAt(final Integer index) {
		return null;
	}

	@Override
	public void putAll(final Double value, final Object param) throws GamaRuntimeException {
		x = value;
		y = value;
	}

	@Override
	public void put(final Integer ii, final Double value, final Object param)
		throws GamaRuntimeException {
		if ( ii == 0 ) {
			x = value;
		} else if ( ii == 1 ) {
			y = value;
		}
	}

	public Coordinate toCoordinate() {
		return new Coordinate(x, y);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	@getter(var = "x")
	public Double first() {
		return x;
	}

	@Override
	@getter(var = "y")
	public Double last() {
		return y;
	}

	@Override
	public Double get(final Integer index) {

		if ( index == 0 ) { return x; }
		if ( index == 1 ) { return y; }
		// exception
		return 0d;
	}

	@Override
	public Double sum() {
		return x + y;
	}

	@Override
	public Double product() {
		return x * y;
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public Double max() {
		return x > y ? x : y;
	}

	@Override
	public Double min() {
		return x > y ? y : x;
	}

	@Override
	public boolean contains(final Object o) {
		return o.equals(Double.valueOf(x)) || o.equals(Double.valueOf(y));
	}

	@Override
	public IGamaContainer<Integer, Double> reverse() {
		return new GamaPoint(y, x);
	}

	@Override
	public void clear() {
		x = 0;
		y = 0;
	}

	@Override
	public GamaPoint copy() {
		return new GamaPoint(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return index instanceof Integer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return value instanceof Number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		return index == 0 || index == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return true;
	}

	@Override
	public Iterator<Double> iterator() {
		return listValue(null).iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IGamaContainer value, final Object param) throws GamaRuntimeException {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final Integer index, final IGamaContainer value, final Object param)
		throws GamaRuntimeException {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGeometry#getGeometry()
	 */
	@Override
	public GamaGeometry getGeometry() {
		return GamaGeometry.createPoint(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGeometry#setGeometry(msi.gama.util.GamaGeometry)
	 */
	@Override
	public void setGeometry(final GamaGeometry g) {
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
	public boolean covers(final IGeometry g) {
		if ( g.isPoint() ) { return g.getLocation().equals(this); }
		return false;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IGeometry g) {
		if ( g.isPoint() ) {
			GamaPoint p = g.getLocation();
			return MathUtils.hypot(x, p.x, y, p.y);
		}
		return g.euclidianDistanceTo(this);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IGeometry g) {
		if ( g.isPoint() ) { return g.getLocation().equals(this); }
		return g.intersects(this);
	}

	@Override
	public Double any() {
		int i = GAMA.getRandom().between(0, 1);
		return i == 0 ? x : y;
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

}
