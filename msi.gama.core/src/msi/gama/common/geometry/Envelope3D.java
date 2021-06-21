/*******************************************************************************************************
 *
 * msi.gama.common.geometry.Envelope3D.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.util.PoolUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gaml.operators.Comparison;
import msi.gaml.types.GamaGeometryType;

// import org.opengis.geometry.MismatchedDimensionException;

/**
 * A 3D envelope that extends the 2D JTS Envelope.
 *
 *
 * @author Niels Charlier
 * @adapted for GAMA by A. Drogoul
 *
 */
public class Envelope3D extends Envelope implements IDisposable {

	private final static PoolUtils.ObjectPool<Envelope3D> POOL =
			PoolUtils.create("Envelope 3D", true, () -> new Envelope3D(), (from, to) -> to.set(from), null);

	public static final Envelope3D EMPTY = create();

	public static Envelope3D create() {
		return POOL.get();
	}

	public static Envelope3D of(final Geometry g) {
		if (g instanceof GeometryCollection) return of((GeometryCollection) g);
		final ICoordinates sq = GeometryUtils.getContourCoordinates(g);
		return sq.getEnvelope();
	}

	public static Envelope3D of(final GeometryCollection g) {
		final int i = g.getNumGeometries();
		if (i == 0) return EMPTY;
		final Envelope3D result = of(g.getGeometryN(0));
		for (int j = 1; j < i; j++) {
			result.expandToInclude(of(g.getGeometryN(j)));
		}
		return result;
	}

	public static Envelope3D of(final IShape s) {
		return of(s.getInnerGeometry());
	}

	public static Envelope3D of(final ILocation s) {
		return of((Coordinate) s);
	}

	public static Envelope3D of(final Envelope e) {
		final Envelope3D env = create();
		env.init(e);
		return env;
	}

	public static Envelope3D withYNegated(final Envelope e) {
		final Envelope3D env = create();
		env.init(e);
		env.init(env.getMinX(), env.getMaxX(), -env.getMinY(), -env.getMaxY(), env.minz, env.maxz);
		return env;
	}

	public static Envelope3D of(final Coordinate p) {
		final Envelope3D env = create();
		env.init(p);
		return env;
	}

	public static Envelope3D of(final double x1, final double x2, final double y1, final double y2, final double z1,
			final double z2) {
		final Envelope3D env = create();
		env.init(x1, x2, y1, y2, z1, z2);
		return env;
	}

	@Override
	public void dispose() {
		setToNull();
		POOL.release(this);
	}

	/**
	 * Serial number for compatibility with different versions.
	 */
	private static final long serialVersionUID = -3188702602373537163L;

	/**
	 * the minimum z-coordinate
	 */
	private double minz;

	/**
	 * the maximum z-coordinate
	 */
	private double maxz;

	/**
	 * Initialize an <code>Envelope</code> for a region defined by maximum and minimum values.
	 *
	 * @param x1
	 *            the first x-value
	 * @param x2
	 *            the second x-value
	 * @param y1
	 *            the first y-value
	 * @param y2
	 *            the second y-value
	 * @param z1
	 *            the first z-value
	 * @param z2
	 *            the second z-value
	 */
	public void init(final double x1, final double x2, final double y1, final double y2, final double z1,
			final double z2) {
		init(x1, x2, y1, y2);
		if (z1 < z2) {
			minz = z1;
			maxz = z2;
		} else {
			minz = z2;
			maxz = z1;
		}
	}

	/**
	 * Initialize an <code>Envelope</code> to a region defined by two Coordinates.
	 *
	 * @param p1
	 *            the first Coordinate
	 * @param p2
	 *            the second Coordinate
	 */
	@Override
	public void init(final Coordinate p1, final Coordinate p2) {
		init(p1.x, p2.x, p1.y, p2.y, p1.z, p2.z);
	}

	/**
	 * Initialize an <code>Envelope</code> to a region defined by a single Coordinate.
	 *
	 * @param p
	 *            the coordinate
	 */
	@Override
	public void init(final Coordinate p) {
		init(p.x, p.x, p.y, p.y, p.z, p.z);
	}

	@Override
	public void init(final Envelope env) {
		super.init(env);
		if (env instanceof Envelope3D) {
			this.minz = ((Envelope3D) env).getMinZ();
			this.maxz = ((Envelope3D) env).getMaxZ();
		}
	}

	/**
	 * Initialize an <code>Envelope</code> from an existing 3D Envelope.
	 *
	 * @param env
	 *            the 3D Envelope to initialize from
	 */
	public void init(final Envelope3D env) {
		super.init(env);
		this.minz = env.minz;
		this.maxz = env.maxz;
	}

	private Envelope3D set(final Envelope3D env) {
		init(env);
		return this;
	}

	/**
	 * Returns the maximal dimension of the envelope
	 */

	public double getLargestDimension() {
		double result = Math.max(getWidth(), getHeight());
		return Math.max(result, getDepth());
	}

	/**
	 * Makes this <code>Envelope</code> a "null" envelope, that is, the envelope of the empty geometry.
	 */
	@Override
	public void setToNull() {
		super.setToNull();
		minz = 0;
		maxz = -1;
	}

	/**
	 * Returns the difference between the maximum and minimum z values.
	 *
	 * @return max z - min z, or 0 if this is a null <code>Envelope</code>
	 */
	public double getDepth() {
		if (isNull()) return 0;
		return maxz - minz;
	}

	/**
	 * Returns the <code>Envelope</code>s minimum z-value. min z > max z indicates that this is a null
	 * <code>Envelope</code>.
	 *
	 * @return the minimum z-coordinate
	 */
	public double getMinZ() {
		return minz;
	}

	/**
	 * Returns the <code>Envelope</code>s maximum z-value. min z > max z indicates that this is a null
	 * <code>Envelope</code>.
	 *
	 * @return the maximum z-coordinate
	 */
	public double getMaxZ() {
		return maxz;
	}

	/**
	 * Gets the volume of this envelope.
	 *
	 * @return the volume of the envelope
	 * @return 0.0 if the envelope is null
	 */
	public double getVolume() {
		if (isNull()) return 0.0;
		return getWidth() * getHeight() * getDepth();
	}

	/**
	 * Gets the minimum extent of this envelope across all three dimensions.
	 *
	 * @return the minimum extent of this envelope
	 */
	@Override
	public double minExtent() {
		if (isNull()) return 0.0;
		return Math.min(getWidth(), Math.min(getHeight(), getDepth()));
	}

	/**
	 * Gets the maximum extent of this envelope across all three dimensions.
	 *
	 * @return the maximum extent of this envelope
	 */
	@Override
	public double maxExtent() {
		if (isNull()) return 0.0;
		return Math.max(getWidth(), Math.max(getHeight(), getDepth()));
	}

	/**
	 * Enlarges this <code>Envelope</code> so that it contains the given {@link Coordinate}. Has no effect if the point
	 * is already on or within the envelope.
	 *
	 * @param p
	 *            the Coordinate to expand to include
	 */
	@Override
	public void expandToInclude(final Coordinate p) {
		expandToInclude(p.x, p.y, p.z);
	}

	/**
	 * Expands this envelope by a given distance in all directions. Both positive and negative distances are supported.
	 *
	 * @param distance
	 *            the distance to expand the envelope
	 */
	@Override
	public void expandBy(final double distance) {
		expandBy(distance, distance, distance);
	}

	/**
	 * Expands this envelope by a given distance in all directions. Both positive and negative distances are supported.
	 *
	 * @param deltaX
	 *            the distance to expand the envelope along the the X axis
	 * @param deltaY
	 *            the distance to expand the envelope along the the Y axis
	 */
	public void expandBy(final double deltaX, final double deltaY, final double deltaZ) {
		if (isNull()) return;
		minz -= deltaZ;
		maxz += deltaZ;
		expandBy(deltaX, deltaY);

		// check for envelope disappearing
		if (minz > maxz) { setToNull(); }
	}

	/**
	 * Enlarges this <code>Envelope</code> so that it contains the given point. Has no effect if the point is already on
	 * or within the envelope.
	 *
	 * @param x
	 *            the value to lower the minimum x to or to raise the maximum x to
	 * @param y
	 *            the value to lower the minimum y to or to raise the maximum y to
	 * @param z
	 *            the value to lower the minimum z to or to raise the maximum z to
	 */
	public void expandToInclude(final double x, final double y, final double z) {
		if (isNull()) {
			expandToInclude(x, y);
			minz = z;
			maxz = z;
		} else {
			expandToInclude(x, y);
			if (z < minz) { minz = z; }
			if (z > maxz) { maxz = z; }
		}
	}

	/**
	 * Translates this envelope by given amounts in the X and Y direction. Returns the envelope
	 *
	 * @param transX
	 *            the amount to translate along the X axis
	 * @param transY
	 *            the amount to translate along the Y axis
	 * @param transZ
	 *            the amount to translate along the Z axis
	 */
	public Envelope3D translate(final double transX, final double transY, final double transZ) {
		if (isNull()) return this;
		init(getMinX() + transX, getMaxX() + transX, getMinY() + transY, getMaxY() + transY, getMinZ() + transZ,
				getMaxZ() + transZ);
		return this;
	}

	/**
	 * Computes the coordinate of the centre of this envelope (as long as it is non-null
	 *
	 * @return the centre coordinate of this envelope <code>null</code> if the envelope is null
	 */
	@Override
	public GamaPoint centre() {
		if (isNull()) return null;
		return new GamaPoint((getMinX() + getMaxX()) / 2.0, (getMinY() + getMaxY()) / 2.0,
				(getMinZ() + getMaxZ()) / 2.0);
	}

	/**
	 * Check if the region defined by <code>other</code> overlaps (intersects) the region of this <code>Envelope</code>.
	 *
	 * @param other
	 *            the <code>Envelope</code> which this <code>Envelope</code> is being checked for overlapping
	 * @return <code>true</code> if the <code>Envelope</code>s overlap
	 */
	@Override
	public boolean intersects(final Envelope other) {
		if (!super.intersects(other)) return false;
		return !(getMinZOf(other) > maxz || getMaxZOf(other) < minz);
	}

	/**
	 * Check if the point <code>p</code> overlaps (lies inside) the region of this <code>Envelope</code>.
	 *
	 * @param p
	 *            the <code>Coordinate</code> to be tested
	 * @return <code>true</code> if the point overlaps this <code>Envelope</code>
	 */
	@Override
	public boolean intersects(final Coordinate p) {
		return intersects(p.x, p.y, p.z);
	}

	/**
	 * Check if the point <code>(x, y)</code> overlaps (lies inside) the region of this <code>Envelope</code>.
	 *
	 * @param x
	 *            the x-ordinate of the point
	 * @param y
	 *            the y-ordinate of the point
	 * @param z
	 *            the z-ordinate of the point
	 * @return <code>true</code> if the point overlaps this <code>Envelope</code>
	 */
	protected boolean intersects(final double x, final double y, final double z) {
		if (isNull()) return false;
		return intersects(x, y) && !(z < minz || z > maxz);
	}

	/**
	 * Tests if the given point lies in or on the envelope.
	 *
	 * @param x
	 *            the x-coordinate of the point which this <code>Envelope</code> is being checked for containing
	 * @param y
	 *            the y-coordinate of the point which this <code>Envelope</code> is being checked for containing
	 * @return <code>true</code> if <code>(x, y)</code> lies in the interior or on the boundary of this
	 *         <code>Envelope</code>.
	 */
	protected boolean covers(final double x, final double y, final double z) {
		if (isNull()) return false;
		return covers(x, y) && z >= minz && z <= maxz;
	}

	/**
	 * Tests if the given point lies in or on the envelope.
	 *
	 * @param p
	 *            the point which this <code>Envelope</code> is being checked for containing
	 * @return <code>true</code> if the point lies in the interior or on the boundary of this <code>Envelope</code>.
	 */
	@Override
	public boolean covers(final Coordinate p) {
		return covers(p.x, p.y, p.z);
	}

	/**
	 * Tests if the <code>Envelope other</code> lies wholely inside this <code>Envelope</code> (inclusive of the
	 * boundary).
	 *
	 * @param other
	 *            the <code>Envelope</code> to check
	 * @return true if this <code>Envelope</code> covers the <code>other</code>
	 */
	@Override
	public boolean covers(final Envelope other) {
		if (isNull() || other.isNull()) return false;
		if (!super.covers(other)) return false;
		return getMinZOf(other) >= minz && getMaxZOf(other) <= maxz;
	}

	/**
	 * Computes the distance between this and another <code>Envelope</code>. The distance between overlapping Envelopes
	 * is 0. Otherwise, the distance is the Euclidean distance between the closest points.
	 */
	@Override
	public double distance(final Envelope env) {
		if (intersects(env)) return 0;

		double dx = 0.0;
		if (getMaxX() < env.getMinX()) {
			dx = env.getMinX() - getMaxX();
		} else if (getMinX() > env.getMaxX()) { dx = getMinX() - env.getMaxX(); }

		double dy = 0.0;
		if (getMaxY() < env.getMinY()) {
			dy = env.getMinY() - getMaxY();
		} else if (getMinY() > env.getMaxY()) { dy = getMinY() - env.getMaxY(); }

		double dz = 0.0;
		final double otherMinZ = getMinZOf(env);
		final double otherMaxZ = getMaxZOf(env);
		if (maxz < otherMinZ) {
			dz = otherMinZ - maxz;
		} else if (minz > otherMaxZ) { dz = minz - otherMaxZ; }

		// if either is zero, the envelopes overlap either vertically or
		// horizontally
		if (dx == 0.0 && dz == 0.0) return dy;
		if (dy == 0.0 && dz == 0.0) return dx;
		if (dx == 0.0 && dy == 0.0) return dz;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	// ---------------------------------------------------------------------------------------------------------------

	private Envelope3D() {
		super();
	}

	/**
	 * Computes the intersection of two {@link Envelope}s.
	 *
	 * @param env
	 *            the envelope to intersect with
	 * @return a new Envelope representing the intersection of the envelopes (this will be the null envelope if either
	 *         argument is null, or they do not intersect
	 */
	@Override
	public Envelope3D intersection(final Envelope env) {
		if (isNull() || env.isNull() || !intersects(env)) return EMPTY;
		final Envelope xyInt = super.intersection(env);
		final double otherMinZ = getMinZOf(env);
		final double intMinZ = minz > otherMinZ ? minz : otherMinZ;
		final double otherMaxZ = getMaxZOf(env);
		final double intMaxZ = maxz < otherMaxZ ? maxz : otherMaxZ;
		return of(xyInt.getMinX(), xyInt.getMaxX(), xyInt.getMinY(), xyInt.getMaxY(), intMinZ, intMaxZ);
	}

	/**
	 * Enlarges this <code>Envelope</code> so that it contains the <code>other</code> Envelope. Has no effect if
	 * <code>other</code> is wholly on or within the envelope.
	 *
	 * @param other
	 *            the <code>Envelope</code> to expand to include
	 */
	@Override
	public void expandToInclude(final Envelope other) {
		if (other.isNull()) return;
		final double otherMinZ = getMinZOf(other);
		final double otherMaxZ = getMaxZOf(other);
		if (isNull()) {
			super.expandToInclude(other);
			minz = otherMinZ;
			maxz = otherMaxZ;
		} else {
			super.expandToInclude(other);
			if (otherMinZ < minz) { minz = otherMinZ; }
			if (otherMaxZ > maxz) { maxz = otherMaxZ; }
		}
	}

	/**
	 * @param other
	 * @return
	 */
	private double getMaxZOf(final Envelope other) {
		if (other instanceof Envelope3D) return ((Envelope3D) other).maxz;
		return 0d;
	}

	/**
	 * @param other
	 * @return
	 */
	private double getMinZOf(final Envelope other) {
		if (other instanceof Envelope3D) return ((Envelope3D) other).minz;
		return 0d;
	}

	/**
	 * Returns a hash value for this envelope. This value need not remain consistent between different implementations
	 * of the same class.
	 */
	@Override
	public int hashCode() {
		// Algorithm from Effective Java by Joshua Bloch [Jon Aquino]
		int result = super.hashCode();
		result = 37 * result + Coordinate.hashCode(minz);
		result = 37 * result + Coordinate.hashCode(maxz);
		return result ^ (int) serialVersionUID;
	}

	/**
	 * Compares the specified object with this envelope for equality.
	 */
	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof Envelope3D)) return false;
		final Envelope3D otherEnvelope = (Envelope3D) other;
		if (isNull()) return otherEnvelope.isNull();
		return super.equals(other) && Comparison.equal(minz, otherEnvelope.getMinZ())
				&& Comparison.equal(maxz, otherEnvelope.getMaxZ());
	}

	public boolean isFlat() {
		return minz == maxz;
	}

	public boolean isHorizontal() {
		return minz == maxz;
	}

	public Polygon toGeometry() {
		if (isFlat())
			return (Polygon) GamaGeometryType.buildRectangle(getWidth(), getHeight(), centre()).getInnerGeometry();
		return (Polygon) GamaGeometryType.buildBox(getWidth(), getHeight(), getDepth(), centre()).getInnerGeometry();
	}

	@Override
	public String toString() {
		return "Env[" + getMinX() + " : " + getMaxX() + ", " + getMinY() + " : " + getMaxY() + ",  " + minz + " : "
				+ maxz + "]";
	}

	public Envelope3D yNegated() {
		return of(getMinX(), getMaxX(), -getMaxY(), -getMinY(), minz, maxz);
	}

	public Envelope3D rotate(final AxisAngle rotation) {
		if (isNull()) return this;
		GamaShape se = new GamaShape(this);
		se = new GamaShape(se, null, rotation, se.getLocation());
		init(se.getEnvelope());
		return this;
	}

}