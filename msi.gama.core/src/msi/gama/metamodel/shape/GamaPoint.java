/*********************************************************************************************
 *
 * 'GamaPoint.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.shape;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Maths;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * AgentLocation.
 *
 * @author drogoul 11 oct. 07
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })

public class GamaPoint extends Coordinate implements ILocation {

	private static final double[] EMPTY = new double[] {};
	public static final GamaPoint NULL_POINT = new GamaPoint(0d, 0d, 0d);

	{
		x = 0.0d;
		y = 0.0d;
		z = 0.0d;
	}

	public GamaPoint(final double... coords) {
		setLocation(coords);
	}

	public GamaPoint(final Coordinate coord) {
		this(coord.x, coord.y, coord.z);
	}

	public GamaPoint(final GamaPoint point) {
		this((Coordinate) point);
	}

	public GamaPoint(final ILocation point) {
		this(point == null ? EMPTY : new double[] { point.getX(), point.getY(), point.getZ() });
	}

	@Override
	public void setLocation(final ILocation al) {
		setLocation(al.getX(), al.getY(), al.getZ());
	}

	public void setLocation(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		setZ(z);
	}

	@Override
	public void setLocation(final double... coords) {
		final int n = coords.length;
		switch (n) {
			case 0:
				return;
			case 1:
				setX(coords[0]);
				setY(coords[0]);
				setZ(coords[0]);
				break;
			case 2:
				setX(coords[0]);
				setY(coords[1]);
				break;
			default:
				setX(coords[0]);
				setY(coords[1]);
				setZ(coords[2]);
		}
	}

	@Override
	public void setCoordinate(final Coordinate c) {
		setLocation(c.x, c.y, c.z);
	}

	@Override
	public void setOrdinate(final int i, final double v) {
		switch (i) {
			case X:
				setX(v);
				break;
			case Y:
				setY(v);
				break;
			case Z:
				setZ(v);
				break;
		}
	}

	@Override
	public void setX(final double xx) {
		x = xx;
	}

	@Override
	public void setY(final double yy) {
		y = yy;
	}

	@Override
	public void setZ(final double zz) {
		z = Double.isNaN(zz) ? 0.0d : zz;
	}

	@Override
	@getter ("x")
	public double getX() {
		return x;
	}

	@Override
	@getter ("y")
	public double getY() {
		return y;
	}

	@Override
	@getter ("z")
	public double getZ() {
		return z;
	}

	@Override
	public boolean isPoint() {
		return true;
	}

	@Override
	public boolean isLine() {
		return false;
	}

	@Override
	public String toString() {
		return "location[" + x + ";" + y + ";" + z + "]";
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "{" + x + "," + y + "," + z + "}";
	}

	@Override
	public GamaPoint getLocation() {
		return this;
	}

	@Override
	public String stringValue(final IScope scope) {
		return "{" + x + "," + y + "," + z + "}";
	}

	@Override
	public void add(final ILocation loc) {
		setX(x + loc.getX());
		setY(y + loc.getY());
		setZ(z + loc.getZ());
	}

	public void add(final GamaPoint loc) {
		setX(x + loc.x);
		setY(y + loc.y);
		setZ(z + loc.z);
	}

	public void multiplyBy(final double value) {
		x *= value;
		y *= value;
		setZ(z * value);
	}

	public void divideBy(final double value) {
		x /= value;
		y /= value;
		setZ(z / value);
	}

	public Coordinate toCoordinate() {
		return new Coordinate(x, y, z);
	}

	@Override
	public GamaPoint copy(final IScope scope) {
		return new GamaPoint(x, y, z);
	}

	@Override
	public GamaShape getGeometry() {
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
		return GeometryUtils.GEOMETRY_FACTORY.createPoint(this);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getEnvelope()
	 */
	@Override
	public Envelope3D getEnvelope() {
		return Envelope3D.of(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof GamaPoint) { return equals3D((GamaPoint) o); }
		return super.equals(o);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		if (g.isPoint()) { return g.getLocation().equals(this); }
		return false;
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		if (g.isPoint()) { return euclidianDistanceTo(g.getLocation()); }
		return g.euclidianDistanceTo(this);
	}

	@Override
	public double euclidianDistanceTo(final ILocation p) {
		return Maths.hypot(x, p.getX(), y, p.getY(), z, p.getZ());
	}

	public double euclidianDistanceTo(final GamaPoint p) {
		return distance3D(p);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		if (g.isPoint()) { return g.getLocation().equals(this); }
		return g.intersects(this);
	}

	@Override
	public boolean crosses(final IShape g) {
		if (g.isPoint()) { return false; }
		return g.crosses(this);
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
	//
	// /**
	// * @see msi.gama.interfaces.IGeometry#getPerimeter()
	// */
	// @Override
	// public double getPerimeter() {
	// return 0d;
	// }

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry point) {
		final Coordinate p = point.getCoordinate();
		setLocation(p.x, p.y, p.z);
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#dispose()
	 */
	@Override
	public void dispose() {}

	@Override
	public GamaMap getAttributes() {
		return null;
	}

	@Override
	public GamaMap getOrCreateAttributes() {
		return GamaMapFactory.create();
	}

	@Override
	public Object getAttribute(final Object key) {
		return null;
	}

	@Override
	public void setAttribute(final Object key, final Object value) {}

	@Override
	public boolean hasAttribute(final Object key) {
		return false;
	}

	/**
	 * Method getGeometricalType()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getGeometricalType()
	 */
	@Override
	public Type getGeometricalType() {
		return Type.POINT;
	}

	public GamaPoint times(final double d) {
		return new GamaPoint(x * d, y * d, z * d);
	}

	public GamaPoint dividedBy(final double d) {
		return new GamaPoint(x / d, y / d, z / d);
	}

	public GamaPoint minus(final GamaPoint other) {
		return new GamaPoint(x - other.x, y - other.y, z - other.z);
	}

	public GamaPoint plus(final GamaPoint other) {
		return new GamaPoint(x + other.x, y + other.y, z + other.z);
	}

	public double norm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + hashCode(x);
		result = 370 * result + hashCode(y);
		result = 3700 * result + hashCode(z);
		return result;
	}

	public GamaPoint normalized() {
		final double r = this.norm();
		if (r == 0d) { return new GamaPoint(0, 0, 0); }
		return new GamaPoint(this.x / r, this.y / r, this.z / r);
	}

	public GamaPoint negated() {
		return new GamaPoint(-x, -y, -z);
	}

	public final static double dotProduct(final GamaPoint v1, final GamaPoint v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public final static GamaPoint cross(final GamaPoint v1, final GamaPoint v2) {
		final double x = v1.y * v2.z - v1.z * v2.y;
		final double y = v2.x * v1.z - v2.z * v1.x;
		final double z = v1.x * v2.y - v1.y * v2.x;
		return new GamaPoint(x, y, z);
	}

	/**
	 * Method getPoints()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getPoints()
	 */
	@Override
	public IList<? extends ILocation> getPoints() {
		final IList result = GamaListFactory.create(Types.POINT);
		result.add(this);
		return result;
	}

	/**
	 * @return the point with y negated (for OpenGL, for example), without side effect on the point.
	 */
	public GamaPoint yNegated() {
		return new GamaPoint(x, -y, z);
	}

	@Override
	public void setDepth(final double depth) {
		// TODO Auto-generated method stub
	}

	/**
	 * Method getType()
	 * 
	 * @see msi.gama.common.interfaces.ITyped#getType()
	 */
	@Override
	public IType getType() {
		return Types.POINT;
	}

	/**
	 * Method getArea()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getArea()
	 */
	@Override
	public Double getArea() {
		return 0d;
	}

	/**
	 * Method getVolume()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getVolume()
	 */
	@Override
	public Double getVolume() {
		return 0d;
	}

	/**
	 * Method getPerimeter()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return 0;
	}

	/**
	 * Method getHoles()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getHoles()
	 */
	@Override
	public IList<GamaShape> getHoles() {
		return GamaListFactory.create();
	}

	/**
	 * Method getCentroid()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getCentroid()
	 */
	@Override
	public GamaPoint getCentroid() {
		return this;
	}

	/**
	 * Method getExteriorRing()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getExteriorRing()
	 */
	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return new GamaShape(this);
	}

	/**
	 * Method getWidth()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getWidth()
	 */
	@Override
	public Double getWidth() {
		return 0d;
	}

	/**
	 * Method getHeight()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getHeight()
	 */
	@Override
	public Double getHeight() {
		return 0d;
	}

	/**
	 * Method getDepth()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getDepth()
	 */
	@Override
	public Double getDepth() {
		return 0d;
	}

	/**
	 * Method getGeometricEnvelope()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getGeometricEnvelope()
	 */
	@Override
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(this);
	}

	/**
	 * Method getGeometries()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#getGeometries()
	 */
	@Override
	public IList<? extends IShape> getGeometries() {
		return GamaListFactory.createWithoutCasting(Types.GEOMETRY, new GamaShape(this));
	}

	/**
	 * Method isMultiple()
	 * 
	 * @see msi.gama.metamodel.shape.IShape#isMultiple()
	 */
	@Override
	public boolean isMultiple() {
		return false;
	}

	@Override
	public double getOrdinate(final int i) {
		switch (i) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
		}
		return 0d;
	}

	@Override
	public void copyShapeAttributesFrom(final IShape other) {}

}
