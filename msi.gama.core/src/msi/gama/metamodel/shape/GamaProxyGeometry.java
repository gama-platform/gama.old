/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaProxyGeometry.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import static msi.gama.common.geometry.GeometryUtils.translate;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class GamaProxyGeometry. A geometry that represents a wrapper to a reference geometry and a translation. All the
 * operations are transmitted to the reference geometry, taking this translation into account. The inner geometry of
 * each instance is computed dynamically every time.
 *
 * This class does not allow any other transformation to its geometry than translation (no scaling, no rotation, etc.).
 * TODO This might come later when rotatedBy() and scaledBy() are redefined outside GamaShape.
 *
 * Abstract methods to override: getReferenceGeometry()
 *
 * Caching of the resulting innner geometry can be achieved by redefining getInnerGeometry() and implementing the policy
 * there. However, the purpose of this class is principally to save memory (see. GamaSpatialMatrix).
 *
 *
 * AD: Changed in 2016 to create attributes due to the abandon of attributes in agents. These geometries have attributes
 * now.
 *
 * The geometries dont have individual attributes. Instead, they read from / write to the attributes of the reference
 * geometry. This can be a simple way to implement properties common to a set of geometries. Subclasses that wish to
 * implement individual attributes can do so by overriding the corresponding methods.
 *
 *
 * @author drogoul
 * @since 18 mai 2013
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public abstract class GamaProxyGeometry implements IShape, Cloneable {

	ILocation absoluteLocation;
	// Property map to add all kinds of information (e.g to specify if the
	// geometry is a sphere, a
	// cube, etc...). Can be reused by subclasses (for example to store GIS
	// information)
	protected IMap<String, Object> attributes;

	public GamaProxyGeometry(final ILocation loc) {
		setLocation(loc);
	}

	@Override
	public IType getGamlType() {
		return Types.GEOMETRY;
	}

	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {
		if (attributes == null) { return; }
		attributes.forEachPair(visitor);
	}

	/**
	 * Method setLocation()
	 *
	 * @see msi.gama.common.interfaces.ILocated#setLocation(msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public void setLocation(final ILocation loc) {
		absoluteLocation = loc;
	}

	/**
	 * Method getLocation()
	 *
	 * @see msi.gama.common.interfaces.ILocated#getLocation()
	 */
	@Override
	public ILocation getLocation() {
		return absoluteLocation;
	}

	/**
	 * Method stringValue()
	 *
	 * @see msi.gama.common.interfaces.IValue#stringValue(msi.gama.runtime.IScope)
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return SHAPE_WRITER.write(getInnerGeometry());
	}

	/**
	 * @return The geometry wrapped by this proxy. This geometry can be static or dynamic (all translations are computed
	 *         dynamically). No caching being made in the basic implementation, it can also change during the lifetime
	 *         of the proxy.
	 */
	protected abstract IShape getReferenceGeometry();

	/**
	 * Method copy()
	 *
	 * @see msi.gama.common.interfaces.IValue#copy(msi.gama.runtime.IScope)
	 */
	@Override
	public IShape copy(final IScope scope) throws GamaRuntimeException {
		return new GamaShape(this);
	}

	/**
	 * Method toGaml()
	 *
	 * @see msi.gama.common.interfaces.IGamlable#toGaml()
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getReferenceGeometry().serialize(includingBuiltIn) + " at_location "
				+ absoluteLocation.serialize(includingBuiltIn);
	}

	/**
	 * Method getAttributes(). The attributes are shared by all the translated geometries. Another option would be to
	 * maintain a map of attributes in each translated shape, but it is costly.
	 *
	 * @see msi.gama.common.interfaces.IAttributed#getAttributes()
	 */
	// @Override
	// public GamaMap getAttributes() {
	// return attributes;
	// // return getReferenceGeometry().getAttributes();
	// }

	/**
	 * Method getOrCreateAttributes()
	 *
	 * @see msi.gama.common.interfaces.IAttributed#getOrCreateAttributes()
	 */
	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		if (attributes == null) {
			attributes = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		}
		return attributes;
		// return getReferenceGeometry().getOrCreateAttributes();
	}

	/**
	 * Method getAttribute()
	 *
	 * @see msi.gama.common.interfaces.IAttributed#getAttribute(java.lang.Object)
	 */
	@Override
	public Object getAttribute(final String key) {
		if (attributes == null) { return null; }
		return attributes.get(key);
		// return getReferenceGeometry().getAttribute(key);
	}

	/**
	 * Method setAttribute()
	 *
	 * @see msi.gama.common.interfaces.IAttributed#setAttribute(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setAttribute(final String key, final Object value) {
		getOrCreateAttributes().put(key, value);
		// getReferenceGeometry().setAttribute(key, value);
	}

	/**
	 * Method hasAttribute()
	 *
	 * @see msi.gama.common.interfaces.IAttributed#hasAttribute(java.lang.Object)
	 */
	@Override
	public boolean hasAttribute(final String key) {
		return attributes != null && attributes.containsKey(key);
		// return getReferenceGeometry().hasAttribute(key);
	}

	/**
	 * Method getAgent()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		// This method is intended to be subclassed if necessary
		return null;
	}

	/**
	 * Method setAgent()
	 *
	 * @see msi.gama.metamodel.shape.IShape#setAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setAgent(final IAgent agent) {
		// This method is intended to be subclassed if necessary
	}

	/**
	 * Method getGeometry()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometry()
	 */
	@Override
	public IShape getGeometry() {
		return this; // TODO or the translated geometry ??
	}

	/**
	 * Method setGeometry()
	 *
	 * @see msi.gama.metamodel.shape.IShape#setGeometry(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public void setGeometry(final IShape g) {
		// Not allowed. The reference geometry is final
	}

	/**
	 * Method isPoint()
	 *
	 * @see msi.gama.metamodel.shape.IShape#isPoint()
	 */
	@Override
	public boolean isPoint() {
		return getReferenceGeometry().isPoint();
	}

	@Override
	public boolean isLine() {
		return getReferenceGeometry().isLine();
	}

	/**
	 * Method getInnerGeometry()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		final Geometry copy = (Geometry) getReferenceGeometry().getInnerGeometry().clone();
		translate(copy, getReferenceGeometry().getLocation().toGamaPoint(), getLocation().toGamaPoint());
		return copy;
	}

	/**
	 * Method getEnvelope(). Computed dynamically. A subclass may choose to cache this (often used) information by
	 * redefining this method
	 *
	 * @see msi.gama.metamodel.shape.IShape#getEnvelope()
	 */
	@Override
	public Envelope3D getEnvelope() {
		final Envelope3D copy = getReferenceGeometry().getEnvelope();
		final ILocation loc = getLocation();
		final ILocation loc2 = getReferenceGeometry().getLocation();
		final double dx = loc.getX() - loc2.getX();
		final double dy = loc.getY() - loc2.getY();
		final double dz = loc.getZ() - loc2.getZ();
		copy.translate(dx, dy, dz);
		return copy;
	}

	/**
	 * Method covers()
	 *
	 * @see msi.gama.metamodel.shape.IShape#covers(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean covers(final IShape g) {
		// TODO Use prepared geometries like in GamaShape ?
		return getInnerGeometry().covers(g.getInnerGeometry());
	}

	/**
	 * Method crosses()
	 *
	 * @see msi.gama.metamodel.shape.IShape#crosses(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean crosses(final IShape g) {
		return getInnerGeometry().crosses(g.getInnerGeometry());
	}

	/**
	 * Method euclidianDistanceTo()
	 *
	 * @see msi.gama.metamodel.shape.IShape#euclidianDistanceTo(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		if (isPoint() && g.isPoint()) { return g.getLocation().euclidianDistanceTo(getLocation()); }
		return getInnerGeometry().distance(g.getInnerGeometry());
	}

	/**
	 * Method euclidianDistanceTo()
	 *
	 * @see msi.gama.metamodel.shape.IShape#euclidianDistanceTo(msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public double euclidianDistanceTo(final ILocation g) {
		if (isPoint()) { return g.euclidianDistanceTo(getLocation()); }
		return getInnerGeometry().distance(g.getInnerGeometry());
		// GamaShape.ppd.initialize();
		// DistanceToPoint.computeDistance(getInnerGeometry(), (Coordinate) g,
		// GamaShape.ppd);
		// return GamaShape.ppd.getDistance();
	}

	/**
	 * Method intersects()
	 *
	 * @see msi.gama.metamodel.shape.IShape#intersects(msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return getInnerGeometry().intersects(g.getInnerGeometry());
	}

	/**
	 * Method getPerimeter()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return getReferenceGeometry().getPerimeter();
	}

	/**
	 * Method setInnerGeometry()
	 *
	 * @see msi.gama.metamodel.shape.IShape#setInnerGeometry(org.locationtech.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry intersection) {}

	/**
	 * Method dispose()
	 *
	 * @see msi.gama.metamodel.shape.IShape#dispose()
	 */
	@Override
	public void dispose() {
		if (attributes != null) {
			attributes.clear();
		}
		attributes = null;
	}

	@Override
	public Type getGeometricalType() {
		return getReferenceGeometry().getGeometricalType();
	}

	/**
	 * Method getPoints()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPoints()
	 */
	@Override
	public IList<? extends ILocation> getPoints() {
		final IList<GamaPoint> result = GamaListFactory.create(Types.POINT);
		final Coordinate[] points = getInnerGeometry().getCoordinates();
		for (final Coordinate c : points) {
			result.add(new GamaPoint(c));
		}
		return result;
	}

	/**
	 * Method setDepth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#setDepth(double)
	 */
	@Override
	public void setDepth(final double depth) {
		// this.setAttribute(IShape.DEPTH_ATTRIBUTE, depth);
	}

	/**
	 * Method getArea()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getArea()
	 */
	@Override
	public Double getArea() {
		return getReferenceGeometry().getArea();
	}

	/**
	 * Method getVolume()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getVolume()
	 */
	@Override
	public Double getVolume() {
		return getReferenceGeometry().getVolume();
	}

	/**
	 * Method getHoles()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getHoles()
	 */
	@Override
	public IList<GamaShape> getHoles() {
		final IList<GamaShape> holes = GamaListFactory.create(Types.GEOMETRY);
		final Geometry g = getInnerGeometry();
		if (g instanceof Polygon) {
			final Polygon p = (Polygon) g;
			final int n = p.getNumInteriorRing();
			for (int i = 0; i < n; i++) {
				holes.add(new GamaShape(
						GeometryUtils.GEOMETRY_FACTORY.createPolygon(p.getInteriorRingN(i).getCoordinates())));
			}
		}
		return holes;
	}

	/**
	 * Method getCentroid()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getCentroid()
	 */
	@Override
	public GamaPoint getCentroid() {
		return absoluteLocation.toGamaPoint();
	}

	/**
	 * Method getExteriorRing()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getExteriorRing()
	 */
	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return getReferenceGeometry().getExteriorRing(scope).translatedTo(scope, absoluteLocation);
	}

	/**
	 * Method getWidth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getWidth()
	 */
	@Override
	public Double getWidth() {
		return getReferenceGeometry().getWidth();
	}

	/**
	 * Method getHeight()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getHeight()
	 */
	@Override
	public Double getHeight() {
		return getReferenceGeometry().getHeight();
	}

	/**
	 * Method getDepth()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getDepth()
	 */
	@Override
	public Double getDepth() {
		return getReferenceGeometry().getDepth();
	}

	/**
	 * Method getGeometricEnvelope()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getGeometricEnvelope()
	 */
	@Override
	public GamaShape getGeometricEnvelope() {
		return new GamaShape(getEnvelope().toGeometry());
	}

}
