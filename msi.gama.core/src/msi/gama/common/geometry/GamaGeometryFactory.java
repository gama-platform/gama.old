/*******************************************************************************************************
 *
 * msi.gama.common.geometry.GamaGeometryFactory.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.geometry;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequenceFactory;

import msi.gama.metamodel.shape.GamaPoint;

public class GamaGeometryFactory extends GeometryFactory {

	public static final GamaCoordinateSequenceFactory COORDINATES_FACTORY = new GamaCoordinateSequenceFactory();
	public static final CoordinateSequenceFactory JTS_COORDINATES_FACTORY = CoordinateArraySequenceFactory.instance();

	public GamaGeometryFactory() {
		super(COORDINATES_FACTORY);
	}

	public static boolean isRing(final Coordinate[] pts) {
		if (pts.length < 4)
			return false;
		if (!pts[0].equals(pts[pts.length - 1]))
			return false;
		return true;
	}

	public static boolean isRing(final List<GamaPoint> pts) {
		final int size = pts.size();
		if (size < 4)
			return false;
		if (!pts.get(0).equals(pts.get(size - 1)))
			return false;
		return true;
	}

	/**
	 * Linear rings are created using a simple coordinate array, without enforcing any clockwiseness condition.
	 */
	@Override
	public LinearRing createLinearRing(final Coordinate[] coordinates) {
		Coordinate[] coords = coordinates;
		if (!isRing(coords)) {
			coords = (Coordinate[]) ArrayUtils.add(coords, coords[0]);
		}
		return createLinearRing(JTS_COORDINATES_FACTORY.create(coords));
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
		if (ring == null || ring.isEmpty())
			return ring;
		return createLinearRing(COORDINATES_FACTORY.create(ring.getCoordinateSequence()));
	}

	@Override
	public GamaCoordinateSequenceFactory getCoordinateSequenceFactory() {
		return COORDINATES_FACTORY;
	}

}
