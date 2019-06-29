/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaPoint.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import static java.lang.Math.sqrt;
import static msi.gaml.operators.Maths.round;

import java.awt.Point;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.util.NumberUtil;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.IEnvelope;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A mutable point in 3D, deriving from JTS Coordinate, that serves muliple purposes (location of agents, of geometries
 * -- through GamaCoordinateSequence --, vectors -- see Rotation3D and AxisAngle, etc.)
 *
 * @author drogoul 11 oct. 07
 */
@SuppressWarnings ({ "unchecked", "rawtypes", "deprecation" })
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
public class GamaPoint extends Coordinate implements IShape, IEnvelope {

	// FACTORY METHODS

	public static GamaPoint createEmpty() {
		return new GamaPoint();
	}

	public static GamaPoint create(final double x, final double y) {
		return new GamaPoint(x, y);
	}

	public static GamaPoint create(final double x, final double y, final double z) {
		return new GamaPoint(x, y, z);
	}

	public static GamaPoint create(final Point point) {
		if (point == null) { return createEmpty(); }
		return new GamaPoint(point.x, point.y);
	}

	public static GamaPoint create(final Coordinate c) {
		if (c == null) { return createEmpty(); }
		return new GamaPoint(c.x, c.y, c.z);
	}

	public static GamaPoint create(final GamaPoint c) {
		if (c == null) { return createEmpty(); }
		return new GamaPoint(c.x, c.y, c.z);
	}

	// Unmodifiable null point

	public static final GamaPoint NULL_POINT = new GamaPoint() {

		@Override
		public GamaPoint add(final double ax, final double ay, final double az) {
			return this;
		}

		@Override
		public GamaPoint add(final GamaPoint loc) {
			return this;
		}

		@Override
		public Object clone() {
			return this;
		}

		@Override
		public GamaPoint divideBy(final double value) {
			return this;
		}

		@Override
		public GamaPoint multiplyBy(final double value) {
			return this;
		}

		@Override
		public void negate() {}

		@Override
		public GamaPoint normalize() {
			return this;
		}

		@Override
		public void setAgent(final IAgent agent) {}

		@Override
		public void setAttribute(final String key, final Object value) {}

		@Override
		public void setCoordinate(final Coordinate c) {}

		@Override
		public void setDepth(final double depth) {}

		@Override
		public void setInnerGeometry(final Geometry point) {}

		@Override
		public GamaPoint subtract(final GamaPoint loc) {
			return this;
		}

		@Override
		public void setGeometry(final IShape g) {}

		@Override
		public GamaPoint setLocation(final double[] coords) {
			return this;
		}

		@Override
		public GamaPoint setLocation(final double x, final double y, final double z) {
			return this;
		}

		@Override
		public void setLocation(final GamaPoint al) {}

		@Override
		public void setOrdinate(final int i, final double v) {}

		@Override
		public void setX(final double xx) {}

		@Override
		public void setY(final double yy) {}

		@Override
		public void setZ(final double zz) {}

		@Override
		public void copyShapeAttributesFrom(final IShape other) {}
	};

	private GamaPoint() {
		this(0d, 0d, 0d);
	}

	//
	private GamaPoint(final double x, final double y) {
		this(x, y, 0d);
	}

	//
	private GamaPoint(final double x, final double y, final double z) {
		setLocation(x, y, z);
	}

	@Override
	public void setLocation(final GamaPoint al) {
		if (al == null) { return; }
		setLocation(al.getX(), al.getY(), al.getZ());
	}

	public GamaPoint setLocation(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		setZ(z);
		return this;
	}

	public GamaPoint setLocation(final double[] coords) {
		final int n = coords.length;
		switch (n) {
			case 0:
				return this;
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
		return this;
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

	public GamaPoint add(final GamaPoint loc) {
		x += loc.x;
		y += loc.y;
		setZ(z + loc.z);
		return this;
	}

	public GamaPoint add(final double ax, final double ay, final double az) {
		x += ax;
		y += ay;
		setZ(z + az);
		return this;
	}

	public GamaPoint subtract(final GamaPoint loc) {
		x -= loc.x;
		y -= loc.y;
		setZ(z - loc.z);
		return this;
	}

	public GamaPoint multiplyBy(final double value) {
		x *= value;
		y *= value;
		setZ(z * value);
		return this;
	}

	public GamaPoint divideBy(final double value) {
		x /= value;
		y /= value;
		setZ(z / value);
		return this;
	}

	@Override
	public GamaPoint copy(final IScope scope) {
		return create(x, y, z);
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
		if (o instanceof GamaPoint) {
			final double tolerance = GamaPreferences.External.TOLERANCE_POINTS.getValue();
			if (tolerance > 0.0) { return equalsWithTolerance((GamaPoint) o, tolerance); }
			return equals3D((GamaPoint) o);
		}
		return super.equals(o);
	}

	public boolean equalsWithTolerance(final Coordinate c, final double tolerance) {
		if (tolerance == 0.0) { return equals3D(c); }
		if (!NumberUtil.equalsWithTolerance(this.x, c.x, tolerance)) { return false; }
		if (!NumberUtil.equalsWithTolerance(this.y, c.y, tolerance)) { return false; }
		if (!Double.isNaN(z) && !Double.isNaN(c.z) && !NumberUtil.equalsWithTolerance(this.z, c.z, tolerance)) {
			return false;
		}

		return true;
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
	public double euclidianDistanceTo(final GamaPoint p) {
		final double dx = p.getX() - x;
		final double dy = p.getY() - y;
		final double dz = p.getZ() - z;
		return sqrt(dx * dx + dy * dy + dz * dz);
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

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(org.locationtech.jts.geom.Geometry)
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
	public Object getAttribute(final String key) {
		return null;
	}

	@Override
	public void setAttribute(final String key, final Object value) {}

	@Override
	public boolean hasAttribute(final String key) {
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
		return create(x * d, y * d, z * d);
	}

	public GamaPoint dividedBy(final double d) {
		return create(x / d, y / d, z / d);
	}

	public GamaPoint minus(final GamaPoint other) {
		return create(x - other.x, y - other.y, z - other.z);
	}

	public GamaPoint minus(final double ax, final double ay, final double az) {
		return create(x - ax, y - ay, z - az);
	}

	public GamaPoint plus(final GamaPoint other) {
		return create(x + other.x, y + other.y, z + other.z);
	}

	public GamaPoint plus(final double ax, final double ay, final double az) {
		return create(x + ax, y + ay, z + az);
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
		if (r == 0d) { return create(0, 0, 0); }
		return create(this.x / r, this.y / r, this.z / r);
	}

	public GamaPoint normalize() {
		final double r = this.norm();
		if (r == 0d) { return this; }
		x = x / r;
		y = y / r;
		z = z / r;
		return this;
	}

	public GamaPoint negated() {
		return create(-x, -y, -z);
	}

	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}

	public final static double dotProduct(final GamaPoint v1, final GamaPoint v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	public final static GamaPoint cross(final GamaPoint v1, final GamaPoint v2) {
		return create(v1.y * v2.z - v1.z * v2.y, v2.x * v1.z - v2.z * v1.x, v1.x * v2.y - v1.y * v2.x);
	}

	/**
	 * Method getPoints()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPoints()
	 */
	@Override
	public IList<? extends GamaPoint> getPoints() {
		final IList result = GamaListFactory.create(Types.POINT);
		result.add(this);
		return result;
	}

	/**
	 * @return the point with y negated (for OpenGL, for example), without side effect on the point.
	 */
	public GamaPoint yNegated() {
		return create(x, -y, z);
	}

	@Override
	public void setDepth(final double depth) {
		// TODO Auto-generated method stub
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType getGamlType() {
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

	public GamaPoint orthogonal() {

		final double threshold = 0.6 * norm();
		if (threshold == 0) { return this; }

		if (Math.abs(x) <= threshold) {
			final double inverse = 1 / sqrt(y * y + z * z);
			return create(0, inverse * z, -inverse * y);
		} else if (Math.abs(y) <= threshold) {
			final double inverse = 1 / sqrt(x * x + z * z);
			return create(-inverse * z, 0, inverse * x);
		}
		final double inverse = 1 / sqrt(x * x + y * y);
		return create(inverse * y, -inverse * x, 0);

	}

	public GamaPoint withPrecision(final int i) {
		return create(round(x, i), round(y, i), round(z, i));
	}

	@Override
	public boolean isNull() {
		return this.equals(NULL_POINT);
	}

	@Override
	public boolean intersects(final IEnvelope bounds) {
		if (bounds.isPoint()) { return this.equals(bounds.getLocation()); }
		return bounds.intersects(this);
	}

	@Override
	public double getMaxX() {
		return getX();
	}

	@Override
	public double getMaxY() {
		return getY();
	}

	@Override
	public double getMinX() {
		return getX();
	}

	@Override
	public double getMinY() {
		return getY();
	}

	@Override
	public boolean covers(final IEnvelope bounds) {
		if (bounds.isPoint()) { return this.equals(bounds.getLocation()); }
		return false;
	}

	@Override
	public int compareTo(final Coordinate other) {
		final int result = super.compareTo(other);
		if (result != 0 || Double.isNaN(z)) { return result; }
		if (z < other.z) { return -1; }
		if (z > other.z) { return 1; }
		return 0;
	}

}
