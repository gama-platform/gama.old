package msi.gama.common.util;

import com.vividsolutions.jts.geom.CoordinateSequence;

import msi.gama.common.util.GamaCoordinateSequence.IndexedVisitor;
import msi.gama.common.util.GamaCoordinateSequence.PairVisitor;
import msi.gama.metamodel.shape.GamaPoint;

public interface ICoordinates extends CoordinateSequence, Iterable<GamaPoint> {

	ICoordinates EMPTY = new GamaCoordinateSequence(new GamaPoint[] {});

	GamaPoint getCenter();

	void addCenterTo(final GamaPoint other);

	@Override
	GamaPoint getCoordinate(int i);

	GamaPoint at(int i);

	ICoordinates yNegated();

	@Override
	GamaPoint[] toCoordinateArray();

	/**
	 * Visits the coordinates, passing the x, y, z ordinates of each coordinate and its index to the visitor, optionally
	 * visiting again the first coordinate
	 * 
	 * @param v
	 *            the visitor (cannot be null)
	 * @param max
	 *            the maximum number of vertices to visit (-1 for all)
	 * @param circular
	 *            whether the first vertex will be visited again or not
	 * @param negateY
	 *            whether to negate the y ordinate
	 */
	void visit(IndexedVisitor v, int max, boolean clockwise);

	/**
	 * Visits the coordinates by pairs (n, n+1), optionally extending to the last pair (nmax, 0)
	 * 
	 * @param v
	 *            the visitor (cannot be null)
	 * @param circular
	 *            whether the pair with the last and first vertices will be visited again or not
	 * @param negateY
	 *            whether to negate the y ordinate of the visited coordinates
	 */
	void visit(PairVisitor v);

	/**
	 * With a direction of 1 (when vertices are defined clockwise) or -1
	 * 
	 * @return
	 */

	GamaPoint getNormal(boolean clockwise);

	void getNormal(boolean clockwise, double factor, GamaPoint normal);

	void applyTranslation(int i, double dx, double dy, double dz);

	boolean isConvex();

	public boolean isClockwise();

	public double averageZ();

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
	public void replaceWith(GamaPoint... points);

	/**
	 * To prevent excessive garbage to be created, points can be replaced directly with this method. Note that the size
	 * of the points will not change (only the first 'size' points will be replaced if the length of the parameter / 3
	 * is greater than the size of the sequence). Allows to maintain 'working sequences' without having to create new
	 * ones (be aware that if the same working sequence is used in different methods, it might create unexpected side
	 * effects)
	 * 
	 * @param points
	 *            an Array of double x, y, z
	 * @return this
	 */
	public void replaceWith(double... ordinates);

}