/*******************************************************************************************************
 *
 * msi.gama.common.geometry.GamaCoordinateSequenceFactory.java, in plugin msi.gama.core, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.geometry;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;

import msi.gama.metamodel.shape.GamaPoint;

public class GamaCoordinateSequenceFactory implements CoordinateSequenceFactory {

	/**
	 * Method create()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(org.locationtech.jts.geom.Coordinate[])
	 */
	@Override
	public ICoordinates create(final Coordinate[] coordinates) {
		if (coordinates.length == 1) { return new UniqueCoordinateSequence(coordinates[0]); }
		return new GamaCoordinateSequence(coordinates);
	}

	public ICoordinates create(final GamaPoint[] coordinates, final boolean copy) {
		if (coordinates.length == 1) { return new UniqueCoordinateSequence(coordinates[0]); }
		return new GamaCoordinateSequence(copy, coordinates);
	}

	/**
	 * Method create()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(org.locationtech.jts.geom.CoordinateSequence)
	 */
	@Override
	public ICoordinates create(final CoordinateSequence coordSeq) {
		if (coordSeq.size() == 1) { return new UniqueCoordinateSequence(coordSeq.getCoordinate(0)); }
		if (coordSeq instanceof GamaCoordinateSequence) { return ((GamaCoordinateSequence) coordSeq).copy(); }
		return new GamaCoordinateSequence(coordSeq.toCoordinateArray());
	}

	/**
	 * Method create()
	 *
	 * @see org.locationtech.jts.geom.CoordinateSequenceFactory#create(int, int)
	 */
	@Override
	public ICoordinates create(final int size, final int dimension) {
		if (size == 1) { return new UniqueCoordinateSequence(GamaPoint.createEmpty()); }
		return new GamaCoordinateSequence(size);
	}

}