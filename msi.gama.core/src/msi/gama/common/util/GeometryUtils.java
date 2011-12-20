/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.util;

import java.util.*;
import msi.gama.metamodel.shape.ILocation;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

/**
 * The class GamaGeometryUtils.
 * 
 * @author drogoul
 * @since 14 déc. 2011
 * 
 */
public class GeometryUtils {

	public static GeometryFactory factory = new GeometryFactory();

	public static GeometryFactory getFactory() {
		return factory;
	}

	// types of geometry
	private final static int NULL = -1;
	private final static int POINT = 0;
	private final static int MULTIPOINT = 1;
	private final static int LINE = 2;
	private final static int MULTILINE = 3;
	private final static int POLYGON = 4;
	private final static int MULTIPOLYGON = 5;

	public static Coordinate pointInGeom(final Geometry geom, final RandomUtils rand) {
		if ( geom instanceof Point || geom instanceof MultiPoint ) {
			Geometry pt;
			if ( geom instanceof Point ) {
				pt = geom;
			} else {
				pt = geom.getGeometryN(rand.between(0, geom.getNumGeometries() - 1));
			}
			return pt.getCoordinate();
		} else if ( geom instanceof LineString || geom instanceof MultiLineString ) {
			Geometry line;
			if ( geom instanceof LineString ) {
				line = geom;
			} else {
				line = geom.getGeometryN(rand.between(0, geom.getNumGeometries() - 1));
			}
			int i = rand.between(0, line.getCoordinates().length - 2);
			Coordinate source = line.getCoordinates()[i];
			Coordinate target = line.getCoordinates()[i + 1];
			if ( source.x != target.x ) {
				double a = (source.y - target.y) / (source.x - target.x);
				double b = source.y - a * source.x;
				double x = rand.between(source.x, target.x);
				double y = a * x + b;
				return new Coordinate(x, y);
			}
			double x = source.x;
			double y = rand.between(source.y, target.y);
			return new Coordinate(x, y);

		} else if ( geom instanceof Polygon || geom instanceof MultiPolygon ) {
			Geometry poly;
			if ( geom instanceof Polygon ) {
				poly = geom;
			} else {
				poly = geom.getGeometryN(rand.between(0, geom.getNumGeometries() - 1));
			}
			Envelope env = poly.getEnvelopeInternal();
			double xMin = env.getMinX();
			double xMax = env.getMaxX();
			double yMin = env.getMinY();
			double yMax = env.getMaxY();
			double x = rand.between(xMin, xMax);
			Coordinate coord1 = new Coordinate(x, yMin);
			Coordinate coord2 = new Coordinate(x, yMax);
			Coordinate[] coords = { coord1, coord2 };
			Geometry line = getFactory().createLineString(coords);
			line = line.intersection(poly);

			return pointInGeom(line, rand);
		}
		return null;

	}

	/**
	 * determine the farthest point of a geometry to another given point
	 * 
	 * @param pt a point
	 * @param geom a GamaGeometry
	 */

	public static Coordinate[] minimiseLength(final Coordinate[] coords) {
		GeometryFactory geomFact = getFactory();
		double dist1 = geomFact.createLineString(coords).getLength();
		Coordinate[] coordstest1 = new Coordinate[3];
		coordstest1[0] = coords[0];
		coordstest1[1] = coords[2];
		coordstest1[2] = coords[1];
		double dist2 = geomFact.createLineString(coordstest1).getLength();

		Coordinate[] coordstest2 = new Coordinate[3];
		coordstest2[0] = coords[1];
		coordstest2[1] = coords[0];
		coordstest2[2] = coords[2];
		double dist3 = geomFact.createLineString(coordstest2).getLength();

		if ( dist1 <= dist2 && dist1 <= dist3 ) { return coords; }
		if ( dist2 <= dist1 && dist2 <= dist3 ) { return coordstest1; }
		if ( dist3 <= dist1 && dist3 <= dist2 ) { return coordstest2; }
		return coords;
	}

	public static Coordinate[] extractPoints(final Polygon poly, final Geometry geom,
		final int degree) {
		Coordinate[] coords = poly.getCoordinates();
		Coordinate[] c1 = { coords[0], coords[1] };
		Coordinate[] c2 = { coords[1], coords[2] };
		Coordinate[] c3 = { coords[2], coords[3] };
		LineString l1 = getFactory().createLineString(c1);
		LineString l2 = getFactory().createLineString(c2);
		LineString l3 = getFactory().createLineString(c3);
		Coordinate[] pts = new Coordinate[degree];
		if ( degree == 3 ) {
			pts[0] = l1.getCentroid().getCoordinate();
			pts[1] = l2.getCentroid().getCoordinate();
			pts[2] = l3.getCentroid().getCoordinate();
			return minimiseLength(pts);
		} else if ( degree == 2 ) {
			Geometry bounds = geom.getBoundary().buffer(1);
			double val1 = bounds.intersection(l1).getLength() / l1.getLength();
			double val2 = bounds.intersection(l2).getLength() / l2.getLength();
			double val3 = bounds.intersection(l3).getLength() / l3.getLength();
			if ( val1 > val2 ) {
				if ( val1 > val3 ) {
					pts[0] = l2.getCentroid().getCoordinate();
					pts[1] = l3.getCentroid().getCoordinate();
				} else {
					pts[0] = l1.getCentroid().getCoordinate();
					pts[1] = l2.getCentroid().getCoordinate();
				}
			} else {
				if ( val2 > val3 ) {
					pts[0] = l1.getCentroid().getCoordinate();
					pts[1] = l3.getCentroid().getCoordinate();
				} else {
					pts[0] = l1.getCentroid().getCoordinate();
					pts[1] = l2.getCentroid().getCoordinate();
				}
			}
		} else {
			return null;
		}
		return pts;
	}

	public static List<Geometry> discretisation(final Geometry geom, final double size,
		final boolean complex) {
		List<Geometry> geoms = new ArrayList<Geometry>();
		if ( geom instanceof GeometryCollection ) {
			GeometryCollection gc = (GeometryCollection) geom;
			for ( int i = 0; i < gc.getNumGeometries(); i++ ) {
				geoms.addAll(discretisation(gc.getGeometryN(i), size, complex));
			}
		} else {
			Envelope env = geom.getEnvelopeInternal();
			double xMax = env.getMaxX();
			double yMax = env.getMaxY();
			double x = env.getMinX();
			double y = env.getMinY();
			GeometryFactory geomFact = getFactory();
			while (x < xMax) {
				y = env.getMinY();
				while (y < yMax) {
					Coordinate c1 = new Coordinate(x, y);
					Coordinate c2 = new Coordinate(x + size, y);
					Coordinate c3 = new Coordinate(x + size, y + size);
					Coordinate c4 = new Coordinate(x, y + size);
					Coordinate[] cc = { c1, c2, c3, c4, c1 };
					Geometry square = geomFact.createPolygon(geomFact.createLinearRing(cc), null);
					y += size;
					try {
						Geometry g = null;
						// try {
						g = square.intersection(geom);
						// } catch (AssertionFailedException e) {
						// g = square.intersection(geom.buffer(0.01));
						// }
						// geoms.add(g);
						if ( complex ) {
							geoms.add(g);
						} else {
							if ( g instanceof Polygon ) {
								geoms.add(g);
							} else if ( g instanceof MultiPolygon ) {
								MultiPolygon mp = (MultiPolygon) g;
								for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
									if ( mp.getGeometryN(i) instanceof Polygon ) {
										geoms.add(mp.getGeometryN(i));
									}
								}
							}
						}
					} catch (TopologyException e) {}
				}
				x += size;
			}
		}
		return geoms;
	}

	public static List<Polygon> triangulation(final Geometry geom) {
		List<Polygon> geoms = new ArrayList<Polygon>();
		if ( geom instanceof GeometryCollection ) {
			GeometryCollection gc = (GeometryCollection) geom;
			for ( int i = 0; i < gc.getNumGeometries(); i++ ) {
				geoms.addAll(triangulation(gc.getGeometryN(i)));
			}
		} else if ( geom instanceof Polygon ) {
			Polygon polygon = (Polygon) geom;
			ConformingDelaunayTriangulationBuilder dtb =
				new ConformingDelaunayTriangulationBuilder();
			dtb.setSites(polygon);
			// dtb.setClipEnvelope(polygon.getEnvelopeInternal());
			dtb.setConstraints(polygon);
			dtb.setTolerance(0.01);
			GeometryCollection tri = (GeometryCollection) dtb.getTriangles(getFactory());
			// GeometryCollection tri = (GeometryCollection)
			// dtb.getDiagram(BasicTransfomartions.geomFactory);

			int nb = tri.getNumGeometries();
			for ( int i = 0; i < nb; i++ ) {
				Geometry gg = tri.getGeometryN(i);
				// try {
				if ( gg.isValid() && gg.intersection(geom).getArea() > 0.001 ) {
					geoms.add((Polygon) gg);
					// }
					// } catch (AssertionFailedException e) {
					// if ( gg.isValid() && gg.intersection(geom.buffer(0.0001)).getArea() > 1.0 ) {
					// geoms.add((Polygon) gg);
					// }
				}
			}
		}
		return geoms;
	}

	public static Geometry removeHoles(final Geometry geom) {
		if ( geom instanceof Polygon ) { return getFactory().createPolygon(
			getFactory().createLinearRing(((Polygon) geom).getExteriorRing().getCoordinates()),
			null); }
		if ( geom instanceof MultiPolygon ) {
			MultiPolygon mp = (MultiPolygon) geom;
			Polygon[] polys = new Polygon[mp.getNumGeometries()];
			for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
				polys[i] = (Polygon) removeHoles(mp.getGeometryN(i));
			}
			return getFactory().createMultiPolygon(polys);
		}
		return geom;
	}

	public static Geometry buildGeometryJTS(final List<List<List<ILocation>>> listPoints) {
		int geometryType = geometryType(listPoints);
		if ( geometryType == NULL ) {
			return null;
		} else if ( geometryType == POINT ) {
			return buildPoint(listPoints.get(0));
		} else if ( geometryType == LINE ) {
			return buildLine(listPoints.get(0));
		} else if ( geometryType == POLYGON ) {

			return buildPolygon(listPoints.get(0));
		} else if ( geometryType == MULTIPOINT ) {
			int nb = listPoints.size();
			Point[] geoms = new Point[nb];
			for ( int i = 0; i < nb; i++ ) {
				geoms[i] = (Point) buildPoint(listPoints.get(i));
			}
			return getFactory().createMultiPoint(geoms);
		} else if ( geometryType == MULTILINE ) {
			int nb = listPoints.size();
			LineString[] geoms = new LineString[nb];
			for ( int i = 0; i < nb; i++ ) {
				geoms[i] = (LineString) buildLine(listPoints.get(i));
			}
			return getFactory().createMultiLineString(geoms);
		} else if ( geometryType == MULTIPOLYGON ) {
			int nb = listPoints.size();
			Polygon[] geoms = new Polygon[nb];
			for ( int i = 0; i < nb; i++ ) {
				geoms[i] = (Polygon) buildPolygon(listPoints.get(i));
			}
			// System.out.println("buildGeometryJTS : Multipolygon : " +
			// GamaGeometryType.getFactory().createMultiPolygon(geoms).getArea());

			return getFactory().createMultiPolygon(geoms);
		}
		return null;
	}

	private static Geometry buildPoint(final List<List<ILocation>> listPoints) {
		return getFactory().createPoint(listPoints.get(0).get(0).toCoordinate());
	}

	private static Geometry buildLine(final List<List<ILocation>> listPoints) {
		List<ILocation> coords = listPoints.get(0);
		int nb = coords.size();
		Coordinate[] coordinates = new Coordinate[nb];
		for ( int i = 0; i < nb; i++ ) {
			coordinates[i] = coords.get(i).toCoordinate();
		}
		return getFactory().createLineString(coordinates);
	}

	private static Geometry buildPolygon(final List<List<ILocation>> listPoints) {
		List<ILocation> coords = listPoints.get(0);
		int nb = coords.size();
		Coordinate[] coordinates = new Coordinate[nb];
		for ( int i = 0; i < nb; i++ ) {
			coordinates[i] = coords.get(i).toCoordinate();
		}
		int nbHoles = listPoints.size() - 1;
		LinearRing[] holes = null;
		if ( nbHoles > 0 ) {
			holes = new LinearRing[nbHoles];
			for ( int i = 0; i < nbHoles; i++ ) {
				List<ILocation> coordsH = listPoints.get(i + 1);
				int nbp = coordsH.size();
				Coordinate[] coordinatesH = new Coordinate[nbp];
				for ( int j = 0; j < nbp; j++ ) {
					coordinatesH[j] = coordsH.get(j).toCoordinate();
				}
				holes[i] = getFactory().createLinearRing(coordinatesH);
			}
		}
		Polygon poly =
			getFactory().createPolygon(getFactory().createLinearRing(coordinates), holes);
		return poly;
	}

	private static int geometryType(final List<List<List<ILocation>>> listPoints) {
		if ( listPoints.size() == 0 ) { return NULL; }
		if ( listPoints.size() == 1 ) { return geometryTypeSimp(listPoints.get(0)); }
		int type = geometryTypeSimp(listPoints.get(0));
		if ( type == POINT ) { return MULTIPOINT; }
		if ( type == LINE ) { return MULTILINE; }
		if ( type == POLYGON ) { return MULTIPOLYGON; }
		return NULL;
	}

	private static int geometryTypeSimp(final List<List<ILocation>> listPoints) {
		if ( listPoints.isEmpty() || listPoints.get(0).isEmpty() ) { return NULL; }
		if ( listPoints.get(0).size() == 1 || listPoints.get(0).size() == 2 &&
			listPoints.get(0).get(0).equals(listPoints.get(0).get(listPoints.size() - 1)) ) { return POINT; }

		if ( !listPoints.get(0).get(0).equals(listPoints.get(0).get(listPoints.size() - 1)) ||
			listPoints.get(0).size() < 3 ) { return LINE; }

		return POLYGON;
	}

	public static Geometry rotation(final Geometry geom, final double angle) {
		if ( geom.getClass().equals(Polygon.class) ) {
			return GeometryUtils.rotation((Polygon) geom, angle);
		} else if ( geom.getClass().equals(MultiPolygon.class) ) {
			int num = ((MultiPolygon) geom).getNumGeometries();
			Polygon[] polys = new Polygon[num];
			for ( int i = 0; i < num; i++ ) {
				polys[i] =
					GeometryUtils.rotation((Polygon) ((MultiPolygon) geom).getGeometryN(i), angle);
			}
			return getFactory().createMultiPolygon(polys);
		} else if ( geom.getClass().equals(LineString.class) ) {
			return GeometryUtils.rotation((LineString) geom, angle);
		} else if ( geom.getClass().equals(MultiLineString.class) ) {
			int num = ((MultiLineString) geom).getNumGeometries();
			LineString[] lines = new LineString[num];
			for ( int i = 0; i < num; i++ ) {
				lines[i] =
					GeometryUtils.rotation((LineString) ((MultiLineString) geom).getGeometryN(i),
						angle);
			}
			return getFactory().createMultiLineString(lines);
		}
		return geom;

	}

	// angle: angle de la direction de l'affinite, a partir de l'axe des x
	private static LineString affinite(final LineString ls, final Coordinate c, final double angle,
		final double coef) {
		// rotation
		LineString rot = rotation(ls, c, -1.0 * angle);

		Coordinate[] coord = rot.getCoordinates();
		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			coord_[i] = new Coordinate(c.x + coef * (coord[i].x - c.x), coord[i].y);
		}

		return rotation(getFactory().createLineString(coord_), c, angle);
	}

	// angle: angle de la direction de l'affinite, a partir de l'axe des x
	public static Polygon affinite(final Polygon geom, final Coordinate c, final double angle,
		final double coef) {
		// pivote le polygone
		Polygon rot = rotation(geom, c, -1.0 * angle);

		// le contour externe
		Coordinate[] coord = rot.getExteriorRing().getCoordinates();
		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			coord_[i] = new Coordinate(c.x + coef * (coord[i].x - c.x), coord[i].y);
		}

		LinearRing lr = getFactory().createLinearRing(coord_);

		// les trous
		LinearRing[] trous = new LinearRing[rot.getNumInteriorRing()];
		for ( int j = 0; j < rot.getNumInteriorRing(); j++ ) {
			Coordinate[] hole_coord = rot.getInteriorRingN(j).getCoordinates();
			Coordinate[] hole_coord_ = new Coordinate[hole_coord.length];
			for ( int i = 0; i < hole_coord.length; i++ ) {
				hole_coord_[i] =
					new Coordinate(c.x + coef * (hole_coord[i].x - c.x), hole_coord[i].y);
			}
			trous[j] = getFactory().createLinearRing(hole_coord_);
		}
		return rotation(new Polygon(lr, trous, getFactory()), c, angle);
	}

	public static LineString affinite(final LineString geom, final double angle, final double scale) {
		return affinite(geom, geom.getCentroid().getCoordinate(), angle, scale);
	}

	static Polygon affinite(final Polygon geom, final double angle, final double scale) {
		return affinite(geom, geom.getCentroid().getCoordinate(), angle, scale);
	}

	public static Geometry affinite(final Geometry geom, final double angle, final double scale) {
		if ( geom.getClass().equals(Polygon.class) ) {
			return GeometryUtils.affinite((Polygon) geom, angle, scale);
		} else if ( geom.getClass().equals(MultiPolygon.class) ) {
			int num = ((MultiPolygon) geom).getNumGeometries();
			Polygon[] polys = new Polygon[num];
			for ( int i = 0; i < num; i++ ) {
				polys[i] =
					GeometryUtils.affinite((Polygon) ((MultiPolygon) geom).getGeometryN(i), angle,
						scale);
			}
			return getFactory().createMultiPolygon(polys);
		} else if ( geom.getClass().equals(LineString.class) ) {
			return GeometryUtils.affinite((LineString) geom, angle, scale);
		} else if ( geom.getClass().equals(MultiLineString.class) ) {
			int num = ((MultiLineString) geom).getNumGeometries();
			LineString[] lines = new LineString[num];
			for ( int i = 0; i < num; i++ ) {
				lines[i] =
					GeometryUtils.affinite((LineString) ((MultiLineString) geom).getGeometryN(i),
						angle, scale);
			}
			return getFactory().createMultiLineString(lines);
		} else {
			return geom;
		}
	}

	private static Polygon homothetie(final Polygon geom, final double x0, final double y0,
		final double scale) {

		// le contour externe
		Coordinate[] coord = geom.getExteriorRing().getCoordinates();
		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			coord_[i] =
				new Coordinate(x0 + scale * (coord[i].x - x0), y0 + scale * (coord[i].y - y0));
		}
		LinearRing lr = getFactory().createLinearRing(coord_);

		// les trous
		LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
		for ( int j = 0; j < geom.getNumInteriorRing(); j++ ) {
			Coordinate[] hole_coord = geom.getInteriorRingN(j).getCoordinates();
			Coordinate[] hole_coord_ = new Coordinate[hole_coord.length];
			for ( int i = 0; i < hole_coord.length; i++ ) {
				hole_coord_[i] =
					new Coordinate(x0 + scale * (hole_coord[i].x - x0), y0 + scale *
						(hole_coord[i].y - y0));
			}
			trous[j] = getFactory().createLinearRing(hole_coord_);
		}
		return getFactory().createPolygon(lr, trous);
	}

	public static LineString homothetie(final LineString geom, final double x0, final double y0,
		final double scale) {
		Coordinate[] coord = geom.getCoordinates();
		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			coord_[i] =
				new Coordinate(x0 + scale * (coord[i].x - x0), y0 + scale * (coord[i].y - y0));
		}
		return getFactory().createLineString(coord_);
	}

	private static LineString homothetie(final LineString geom, final double scale) {
		return homothetie(geom, geom.getCentroid().getX(), geom.getCentroid().getY(), scale);
	}

	public static Polygon homothetie(final Polygon geom, final double scale) {
		return homothetie(geom, geom.getCentroid().getX(), geom.getCentroid().getY(), scale);
	}

	public static Geometry homothetie(final Geometry geom, final double scale) {
		if ( geom.getClass().equals(Polygon.class) ) {
			return GeometryUtils.homothetie((Polygon) geom, scale);
		} else if ( geom.getClass().equals(MultiPolygon.class) ) {
			int num = ((MultiPolygon) geom).getNumGeometries();
			Polygon[] polys = new Polygon[num];
			for ( int i = 0; i < num; i++ ) {
				polys[i] =
					GeometryUtils
						.homothetie((Polygon) ((MultiPolygon) geom).getGeometryN(i), scale);
			}
			return getFactory().createMultiPolygon(polys);
		} else if ( geom.getClass().equals(LineString.class) ) {
			return GeometryUtils.homothetie((LineString) geom, scale);
		} else if ( geom.getClass().equals(MultiLineString.class) ) {
			int num = ((MultiLineString) geom).getNumGeometries();
			LineString[] lines = new LineString[num];
			for ( int i = 0; i < num; i++ ) {
				lines[i] =
					GeometryUtils.homothetie((LineString) ((MultiLineString) geom).getGeometryN(i),
						scale);
			}
			return getFactory().createMultiLineString(lines);
		}
		return geom;

	}

	public static Polygon translation(final Polygon geom, final double dx, final double dy) {

		// le contour externe
		LinearRing lr = translation((LinearRing) geom.getExteriorRing(), dx, dy);

		// les trous
		int n = geom.getNumInteriorRing();
		LinearRing[] trous = new LinearRing[n];
		for ( int j = 0; j < n; j++ ) {
			trous[j] = translation((LinearRing) geom.getInteriorRingN(j), dx, dy);
		}

		return getFactory().createPolygon(lr, trous);
	}

	private static LinearRing translation(final LinearRing lr, final double dx, final double dy) {
		return getFactory().createLinearRing(translation(lr.getCoordinates(), dx, dy));
	}

	static LineString translation(final LineString ls, final double dx, final double dy) {
		return getFactory().createLineString(translation(ls.getCoordinates(), dx, dy));
	}

	private static Coordinate[] translation(final Coordinate[] coord, final double dx,
		final double dy) {

		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			coord_[i] = new Coordinate(coord[i].x + dx, coord[i].y + dy);
		}
		return coord_;
	}

	public static Geometry translation(final Geometry geom, final double dx, final double dy) {
		if ( geom instanceof Point ) {
			return getFactory().createPoint(
				new Coordinate(dx + geom.getCoordinate().x, dy + geom.getCoordinate().y));
		} else if ( geom instanceof MultiPoint ) {
			int num = ((MultiPoint) geom).getNumGeometries();
			Point[] polys = new Point[num];
			for ( int i = 0; i < num; i++ ) {
				polys[i] =
					(Point) GeometryUtils.translation(((MultiPoint) geom).getGeometryN(i), dx, dy);
			}
			return getFactory().createMultiPoint(polys);
		} else if ( geom instanceof Polygon ) {
			return GeometryUtils.translation((Polygon) geom, dx, dy);
		} else if ( geom instanceof MultiPolygon ) {
			int num = ((MultiPolygon) geom).getNumGeometries();
			Polygon[] polys = new Polygon[num];
			for ( int i = 0; i < num; i++ ) {
				polys[i] =
					GeometryUtils.translation((Polygon) ((MultiPolygon) geom).getGeometryN(i), dx,
						dy);
			}
			return getFactory().createMultiPolygon(polys);
		} else if ( geom.getClass().equals(LineString.class) ) {
			return GeometryUtils.translation((LineString) geom, dx, dy);
		} else if ( geom.getClass().equals(MultiLineString.class) ) {
			int num = ((MultiLineString) geom).getNumGeometries();
			LineString[] lines = new LineString[num];
			for ( int i = 0; i < num; i++ ) {
				lines[i] =
					GeometryUtils.translation(
						(LineString) ((MultiLineString) geom).getGeometryN(i), dx, dy);
			}
			return getFactory().createMultiLineString(lines);
		}
		return geom;
	}

	public static LineString rotation(final LineString ls, final Coordinate c, final double angle) {
		double cos = Math.cos(angle), sin = Math.sin(angle);

		Coordinate[] coord = ls.getCoordinates();
		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			double x = coord[i].x, y = coord[i].y;
			coord_[i] =
				new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y), c.y + sin * (x - c.x) +
					cos * (y - c.y));
		}
		return getFactory().createLineString(coord_);
	}

	// angle en radian, rotation dans le sens direct de centre c
	public static Polygon rotation(final Polygon geom, final Coordinate c, final double angle) {
		double cos = Math.cos(angle), sin = Math.sin(angle);

		// rotation de l'enveloppe
		Coordinate[] coord = geom.getExteriorRing().getCoordinates();
		Coordinate[] coord_ = new Coordinate[coord.length];
		for ( int i = 0; i < coord.length; i++ ) {
			double x = coord[i].x, y = coord[i].y;
			coord_[i] =
				new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y), c.y + sin * (x - c.x) +
					cos * (y - c.y));
		}
		LinearRing lr = getFactory().createLinearRing(coord_);

		// rotation des trous
		LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
		for ( int j = 0; j < geom.getNumInteriorRing(); j++ ) {
			Coordinate[] coord2 = geom.getInteriorRingN(j).getCoordinates();
			Coordinate[] coord2_ = new Coordinate[coord2.length];
			for ( int i = 0; i < coord2.length; i++ ) {
				double x = coord2[i].x, y = coord2[i].y;
				coord2_[i] =
					new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y), c.y + sin * (x - c.x) +
						cos * (y - c.y));
			}
			trous[j] = getFactory().createLinearRing(coord2_);
		}
		return getFactory().createPolygon(lr, trous);
	}

	// angle en radian, rotation dans le sens direct
	static LineString rotation(final LineString geom, final double angle) {
		return rotation(geom, geom.getCentroid().getCoordinate(), angle);
	}

	// angle en radian, rotation dans le sens direct
	static Polygon rotation(final Polygon geom, final double angle) {
		return rotation(geom, geom.getCentroid().getCoordinate(), angle);
	}

	/*
	 * public static int[][] discretisationGrid(Geometry geom, double size, double xMax, double
	 * yMax) { Chrono c = new Chrono(); c.start(); int[][] matrix = new int[1+ (int)(xMax/size)][1+
	 * (int)(yMax/size)]; int x = 0; int i = 0;
	 * 
	 * GeometryFactory geomFact = BasicTransfomartions.geomFactory; while (x < xMax) { int y = 0;
	 * int j = 0; while (y < yMax){ Coordinate c1 = new Coordinate(x,y); Coordinate c2 = new
	 * Coordinate(x+size,y); Coordinate c3 = new Coordinate(x+size,y+size); Coordinate c4 = new
	 * Coordinate(x,y+size); Coordinate[] cc = {c1,c2,c3,c4,c1}; Geometry square =
	 * geomFact.createPolygon(geomFact.createLinearRing(cc), null); y += size; try{ //Geometry g =
	 * square.intersection(geom); //if (square.covers(square)) { if (square.coveredBy(geom)) { //if
	 * (!square.disjoint(geom)){ //if (true) { matrix[i][j]= Integer.MAX_VALUE; } else matrix[i][j]
	 * = -1; } catch (TopologyException e){ matrix[i][j] = -1; } j++; } x += size; i++; } c.stop();
	 * System.out.println("TEMPS CONSTRUCTION MATRICE : " + c.getMilliSec()); return matrix; }
	 */

	// TODO UCdetector: Remove unused code:
	// public static List<Polygon> triangulationDC(final Geometry geom, final boolean allTriangle,
	// final double size) {
	// GamaList<Polygon> geoms = new GamaList<Polygon>();
	// geoms.addAll(new GamaList<Polygon>(discretisationConvexe(geom, size * size)));
	// GamaList<Polygon> geoms2 = new GamaList<Polygon>();
	// for ( Polygon poly : geoms ) {
	// if ( !allTriangle && isConvex(poly) ) {
	// geoms2.add(poly);
	// } else if ( poly.getArea() > 0.1 ) {
	// geoms2.addAll(triangulation(poly));
	// }
	// }
	// return geoms2;
	// }

	// TODO UCdetector: Remove unused code:
	// public static FeatureCollection buildBasicFeatureCollection(final List<Geometry> geoms)
	// throws SchemaException, IOException {
	// if ( geoms.isEmpty() ) { return null; }
	// String specs = "geom:" + geoms.get(0).getClass().getSimpleName();
	// SimpleFeatureType type = DataUtilities.createType("GEOMETRY", specs);
	// Transaction t = new DefaultTransaction();
	// FeatureCollection collection = FeatureCollections.newCollection();
	//
	// int i = 1;
	//
	// for ( Geometry g : geoms ) {
	// List<Object> liste = new GamaList<Object>();
	// liste.add(g);
	//
	// SimpleFeature simpleFeature =
	// SimpleFeatureBuilder.build(type, liste.toArray(), String.valueOf(i++));
	// collection.add(simpleFeature);
	// }
	// t.commit();
	// t.close();
	// return collection;
	// }

}
