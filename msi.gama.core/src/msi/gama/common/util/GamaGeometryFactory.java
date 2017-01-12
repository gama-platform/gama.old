package msi.gama.common.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;

public class GamaGeometryFactory extends GeometryFactory {

	public static final GamaCoordinateSequenceFactory COORDINATES_FACTORY = new GamaCoordinateSequenceFactory();
	public static final CoordinateSequenceFactory JTS_COORDINATES_FACTORY = CoordinateArraySequenceFactory.instance();

	public GamaGeometryFactory() {
		super(COORDINATES_FACTORY);
	}

	/**
	 * Linear rings are created using a simple coordinate array, without enforcing any clockwiseness condition.
	 */
	@Override
	public LinearRing createLinearRing(final Coordinate[] coordinates) {
		return createLinearRing(JTS_COORDINATES_FACTORY.create(coordinates));
	}

	/**
	 * Polygons are created after ensuring that the coordinate sequence in them has been turned clockwise
	 */

	@Override
	public Polygon createPolygon(final LinearRing shell, final LinearRing[] holes) {
		final LinearRing shellClockwise = turnClockwise(shell);
		if (holes != null)
			for (int i = 0; i < holes.length; i++) {
				holes[i] = turnClockwise(holes[i]);
			}
		return super.createPolygon(shellClockwise, holes);
	}

	private LinearRing turnClockwise(final LinearRing ring) {
		return createLinearRing(COORDINATES_FACTORY.create(ring.getCoordinateSequence()));
	}

	@Override
	public GamaCoordinateSequenceFactory getCoordinateSequenceFactory() {
		return COORDINATES_FACTORY;
	}

}
