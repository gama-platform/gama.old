/*******************************************************************************************************
 *
 * msi.gama.common.geometry.ICoordinates.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.CoordinateSequence;

import msi.gama.metamodel.shape.GamaPoint;

public interface ICoordinates extends CoordinateSequence, Iterable<GamaPoint> {

	/**
	 * An interface used to visit pairs of coordinates in a sequence
	 *
	 * @author drogoul
	 *
	 */
	@FunctionalInterface
	public interface PairVisitor {
		void process(GamaPoint p1, GamaPoint p2);
	}

	/**
	 * An interface used to visit points in a sequence, which accepts their ordinates and their index in the sequence
	 *
	 * @author drogoul
	 *
	 */
	@FunctionalInterface
	public interface IndexedVisitor {
		void process(final int i, final double x, final double y, final double z);
	}

	public interface VertexVisitor {
		void process(final double... ordinates);
	}

	/**
	 * The empty coordinate sequence
	 */
	ICoordinates EMPTY = new GamaCoordinateSequence(3, new GamaPoint[] {});

	/**
	 * Returns the geometric center of this sequence of points
	 *
	 * @return a new point containing the ordinates of the center
	 */
	default GamaPoint getCenter() {
		final GamaPoint p = new GamaPoint();
		addCenterTo(p);
		return p;
	}

	default void getCenter(final GamaPoint center) {
		center.setLocation(0, 0, 0);
		addCenterTo(center);
	}

	@Override
	@Deprecated
	default CoordinateSequence clone() {
		return copy();
	}

	/**
	 * Computes the center of this sequence of points and fills the parameter with its ordinates
	 *
	 * @param other
	 *            the result of the computation
	 */
	void addCenterTo(final GamaPoint other);

	@Override
	GamaPoint getCoordinate(int i);

	/**
	 * Returns the point at index i or null if i is greater than the sequence size or smaller than zero
	 *
	 * @param i
	 * @return a point or null
	 */
	default GamaPoint at(final int i) {
		if (i > size() || i < 0) return null;
		return getCoordinate(i);
	}

	/**
	 * Returns a new sequence of points with all their y ordinate negated. The original sequence is left untouched
	 *
	 * @return a new sequence of points with all their y ordinate negated
	 */
	ICoordinates yNegated();

	/**
	 * Returns the array backing this sequence of points. Note that this array is *not* a copy. Any modification will
	 * directly affect the sequence and possibly change its properties (i.e. clockwiseness or ring), which cannot
	 * therefore be verified
	 */
	@Override
	GamaPoint[] toCoordinateArray();

	/**
	 * Visits the coordinates, passing the x, y, z ordinates of each coordinate and its index to the visitor
	 *
	 * @param v
	 *            the visitor (cannot be null)
	 * @param max
	 *            the maximum number of vertices to visit (-1 for all)
	 * @param clockwise
	 *            whether to visit the sequence in the clockwise or counter-clockwise direction
	 */
	void visit(IndexedVisitor v, int max, boolean clockwise);

	/**
	 * Visits all the coordinates, passing the x, y, z ordinates of each coordinate to the visitor. The visit is done in
	 * the clockwise order. In the case of a line string, the last point is not visited.
	 *
	 * @param v
	 *            the visitor (cannot be null)
	 */
	void visitClockwise(VertexVisitor v);
	//
	// /**
	// * Visits all the coordinates, passing the x, y, z ordinates of each coordinate to the visitor. The visit is done
	// in
	// * the counter-clockwise order. In the case of a line string, the first point is not visited.
	// *
	// * @param v
	// * the visitor (cannot be null)
	// */
	// void visitCounterClockwise(VertexVisitor v);

	/**
	 * Visits all the coordinates, passing the x, -y, z ordinates of each coordinate to the visitor. The visit is done
	 * in the counter-clockwise order (same as the clockwise order if y was not negated). In the case of a line string,
	 * the first point is not visited.
	 *
	 * @param v
	 *            the visitor (cannot be null)
	 */
	void visitYNegatedCounterClockwise(VertexVisitor v);

	/**
	 * Visits all the coordinates, passing the x, -y, z ordinates of each coordinate to the visitor. The visit is done
	 * in the clockwise order (same as the counter-clockwise order if y was not negated). In the case of a line string,
	 * the first point is not visited.
	 *
	 * @param v
	 *            the visitor (cannot be null)
	 */
	// void visitYNegatedClockwise(VertexVisitor v);

	/**
	 * Visits all the coordinates by pairs of adjacent coordinates (n, n+1)
	 *
	 * @param v
	 *            the pair visitor (cannot be null)
	 */
	void visit(PairVisitor v);

	/**
	 * Returns the normal to the sequence, with a direction of 1 (when clockwise is asked and the sequence is clockwise)
	 * or -1
	 *
	 * @return the normal to the sequence of points, in clockwise or counter-clockwise direction
	 */

	default GamaPoint getNormal(final boolean clockwise) {
		final GamaPoint normal = new GamaPoint();
		getNormal(clockwise, 1, normal);
		return normal;
	}

	/**
	 * Computes the normal to this sequence, multiplying the resulting unit vector by a given factor, and fills the
	 * third parameter with its ordinates
	 *
	 * @param clockwise
	 *            Whether or not the sequence is expected to be clockwise
	 * @param factor
	 *            a multiplying factor
	 * @param normal
	 *            the result of the computation
	 */
	void getNormal(boolean clockwise, double factor, GamaPoint normal);

	/**
	 * Expands an existing envelope with this sequence of points
	 *
	 * @param envelope
	 */
	Envelope3D getEnvelopeInto(Envelope3D envelope);

	/**
	 * Returns a new envelope that contains all the points in the sequence
	 *
	 * @return a new Envelope3D containing all the points
	 */
	default Envelope3D getEnvelope() {
		return getEnvelopeInto(Envelope3D.create());
	}

	/**
	 * Compute the average z ordinate of this sequence of points
	 *
	 * @return the average z ordinate
	 */
	double averageZ();

	/**
	 * To prevent excessive garbage to be created, points can be replaced directly with this method. Note that the size
	 * of the points will not change (only the first 'size' points will be replaced if the length of the parameter is
	 * greater than the size of the sequence). Allows to maintain 'working sequences' without having to create new ones
	 * (be aware that if the same working sequence is used in different methods, it might create unexpected side
	 * effects)
	 *
	 * @param points
	 *            an Array of points
	 * @return this
	 */
	ICoordinates setTo(GamaPoint... points);

	/**
	 * To prevent excessive garbage to be created, points can be replaced directly with this method. Note that the size
	 * of the points will not change (only the first 'size' points will be replaced if the length of the parameter / 3
	 * is greater than the size of the sequence). Allows to maintain 'working sequences' without having to create new
	 * ones (be aware that if the same working sequence is used in different methods, it might create unexpected side
	 * effects). In the method with an index, the index is expressed in terms of ordinates, not points (an index of 6
	 * means me will be entering point 2 -- or the 3rd one)
	 *
	 * @param points
	 *            an Array of double x, y, z
	 * @return this
	 */
	default ICoordinates setTo(final double... ordinates) {
		return setTo(0, ordinates);
	}

	ICoordinates setTo(int begin, double... ordinates);

	/**
	 * Equivalent to the setOrdinate(i, d) method but sets all the ordinates at once. No measure is taken for ensuring
	 * that the sequence is still valid after this (i.e. clockwise and/or ring)
	 *
	 * @param i
	 *            the index of the point to replace
	 * @param x
	 *            x ordinate
	 * @param y
	 *            y ordinate
	 * @param z
	 *            z ordinate
	 */
	void replaceWith(int i, double x, double y, double z);

	/**
	 * Returns the vector between the point considered as the origin of the sequence (i.e. at index 0) and the following
	 * point in the sequence, normalized
	 *
	 * @return a point containing the vector
	 */
	GamaPoint directionBetweenLastPointAndOrigin();

	/**
	 * Applies a 3D rotation to the sequence of points
	 *
	 * @param rotation
	 */
	void applyRotation(Rotation3D rotation);

	/**
	 * Whether this sequence is horizontal or not (i.e. all the z ordinates are equal)
	 *
	 * @return true if all the z ordinates are equal, otherwise false
	 */
	boolean isHorizontal();

	/**
	 * Return the length of the sequence (i.e. the sum of all the segments)
	 *
	 * @return the length of the sequence
	 */
	double getLength();

	/**
	 * Sets all the z ordinates of the points to the given z ordinate
	 *
	 * @param elevation
	 */
	void setAllZ(double elevation);

	/**
	 * Returns whether or not all the points in this sequence are covered by the envelope in argument
	 *
	 * @param envelope3d
	 *            an Envelope3D (cannot be null)
	 * @return true or false if at least one point lies outside the envelope
	 */
	boolean isCoveredBy(Envelope3D envelope3d);

	/**
	 * Creates a sequence filled with {0,0,0} points of the given length
	 *
	 * @param length
	 *            the length of the sequence
	 * @return a new ICoordinates with the given length
	 */
	static ICoordinates ofLength(final int length) {
		return GamaGeometryFactory.COORDINATES_FACTORY.create(length, 3);
	}

	/**
	 * Returns whether or not the sequence is ordered in the clockwise order
	 *
	 * @return true or false if CCW
	 */
	boolean isClockwise();

	/**
	 * Replaces the last point in the sequence with the first point (whether or not it has already been filled
	 */
	void completeRing();

	/**
	 * Translates the points in the sequence by the ordinates provided as arguments
	 *
	 * @param i,
	 *            j, k the ordinates of the translation
	 */
	void translateBy(double i, double j, double k);

	/**
	 * Makes sure the sequence is ordered in the clockwise orientation. Reverses the array if it is not.
	 */
	void ensureClockwiseness();

}