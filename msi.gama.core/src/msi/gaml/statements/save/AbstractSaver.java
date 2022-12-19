package msi.gaml.statements.save;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import msi.gama.common.geometry.GeometryUtils;

public class AbstractSaver {

	/**
	 * Geometry collection management.
	 *
	 * @param gg
	 *            the gg
	 * @return the geometry
	 */
	protected Geometry cleanGeometryCollection(final Geometry gg) {
		if (gg instanceof GeometryCollection gc) {
			boolean isMultiPolygon = true;
			boolean isMultiPoint = true;
			boolean isMultiLine = true;
			final int nb = gc.getNumGeometries();

			for (int i = 0; i < nb; i++) {
				final Geometry g = gc.getGeometryN(i);
				if (!(g instanceof Polygon)) { isMultiPolygon = false; }
				if (!(g instanceof LineString)) { isMultiLine = false; }
				if (!(g instanceof Point)) { isMultiPoint = false; }
			}

			if (isMultiPolygon) {
				final Polygon[] polygons = new Polygon[nb];
				for (int i = 0; i < nb; i++) { polygons[i] = (Polygon) gc.getGeometryN(i); }
				return GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(polygons);
			}
			if (isMultiLine) {
				final LineString[] lines = new LineString[nb];
				for (int i = 0; i < nb; i++) { lines[i] = (LineString) gc.getGeometryN(i); }
				return GeometryUtils.GEOMETRY_FACTORY.createMultiLineString(lines);
			}
			if (isMultiPoint) {
				final Point[] points = new Point[nb];
				for (int i = 0; i < nb; i++) { points[i] = (Point) gc.getGeometryN(i); }
				return GeometryUtils.GEOMETRY_FACTORY.createMultiPoint(points);
			}
		}
		return gg;
	}

}
