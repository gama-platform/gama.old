/*******************************************************************************************************
 *
 * GamaCoordinateSequenceFactory.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;

import msi.gama.metamodel.shape.GamaPoint;

/**
 * A factory for creating GamaCoordinateSequence objects.
 */
public class GamaCoordinateSequenceFactory implements CoordinateSequenceFactory {

	/**
	 * Method create()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(org.locationtech.jts.geom.Coordinate[])
	 */
	@Override
	public ICoordinates create(final Coordinate[] coordinates) {
		if (coordinates.length == 1) return new UniqueCoordinateSequence(3, coordinates[0]);
		return new GamaCoordinateSequence(3, coordinates);
	}

	/**
	 * Creates the.
	 *
	 * @param coordinates the coordinates
	 * @param copy the copy
	 * @return the i coordinates
	 */
	public ICoordinates create(final GamaPoint[] coordinates, final boolean copy) {
		if (coordinates.length == 1) return new UniqueCoordinateSequence(3, coordinates[0]);
		return new GamaCoordinateSequence(3, copy, coordinates);
	}

	/**
	 * Method create()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(org.locationtech.jts.geom.CoordinateSequence)
	 */
	@Override
	public ICoordinates create(final CoordinateSequence cs) {
		if (cs.size() == 1) return new UniqueCoordinateSequence(cs.getDimension(), cs.getCoordinate(0));
		if (cs instanceof GamaCoordinateSequence) return ((GamaCoordinateSequence) cs).clone();
		return new GamaCoordinateSequence(cs.getDimension(), cs.toCoordinateArray());
	}

	/**
	 * Method create()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(int, int)
	 */
	@Override
	public ICoordinates create(final int size, final int dimension) {
		if (size == 1) return new UniqueCoordinateSequence(dimension, new GamaPoint());
		return new GamaCoordinateSequence(dimension, size);
	}

}