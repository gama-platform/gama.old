/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
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
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Graphs;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

/**
 * The class GamaGeometryUtils.
 * 
 * @author drogoul
 * @since 14 déc. 2011
 * 
 */
public class GeometryUtils {

	// TODO static CoordinateSequenceFactory csf = new PackedCoordinateSequenceFactory(
	// PackedCoordinateSequenceFactory.DOUBLE, 2);

	public static GeometryFactory factory = new GeometryFactory();
	public static PreparedGeometryFactory pgfactory = new PreparedGeometryFactory();

	// TODO : see the possibility to use new LiteCoordinateSequenceFactory()

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

	public static GamaPoint pointInGeom(final Geometry geom, final RandomUtils rand) {
		if ( geom instanceof Point ) { return new GamaPoint(geom.getCoordinate()); }
		if ( geom instanceof LineString ) {
			int i = rand.between(0, geom.getCoordinates().length - 2);
			Coordinate source = geom.getCoordinates()[i];
			Coordinate target = geom.getCoordinates()[i + 1];
			if ( source.x != target.x ) {
				double a = (source.y - target.y) / (source.x - target.x);
				double b = source.y - a * source.x;
				double x = rand.between(source.x, target.x);
				double y = a * x + b;
				return new GamaPoint(x, y);
			}
			double x = source.x;
			double y = rand.between(source.y, target.y);
			return new GamaPoint(x, y);
		}
		if ( geom instanceof Polygon ) {
			Envelope env = geom.getEnvelopeInternal();
			double xMin = env.getMinX();
			double xMax = env.getMaxX();
			double yMin = env.getMinY();
			double yMax = env.getMaxY();
			double x = rand.between(xMin, xMax);
			Coordinate coord1 = new Coordinate(x, yMin);
			Coordinate coord2 = new Coordinate(x, yMax);
			Coordinate[] coords = { coord1, coord2 };
			Geometry line = getFactory().createLineString(coords);
			line = line.intersection(geom);
			return pointInGeom(line, rand);
		}
		if ( geom instanceof GeometryCollection ) { return pointInGeom(
			geom.getGeometryN(rand.between(0, geom.getNumGeometries() - 1)), rand); }

		return null;

	}

	private static Coordinate[] minimiseLength(final Coordinate[] coords) {
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

	public static Coordinate[] extractPoints(final IShape triangle, final Geometry geom,
		final int degree) {
		Coordinate[] coords = triangle.getInnerGeometry().getCoordinates();
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

	public static GamaList<IShape> triangulation(final Geometry geom, boolean optimized) {
		GamaList<IShape> geoms = new GamaList<IShape>();
		if ( geom instanceof GeometryCollection ) {
			GeometryCollection gc = (GeometryCollection) geom;
			for ( int i = 0; i < gc.getNumGeometries(); i++ ) {
				geoms.addAll(triangulation(gc.getGeometryN(i), optimized));
			}
		} else if ( geom instanceof Polygon ) {
			Polygon polygon = (Polygon) geom;
			if (optimized) {
				double sizeTol = Math.sqrt(polygon.getArea()) / 100.0;
				polygon = (Polygon) DouglasPeuckerSimplifier.simplify(geom, sizeTol);
				ConformingDelaunayTriangulationBuilder dtb =
					new ConformingDelaunayTriangulationBuilder();
				dtb.setSites(polygon);
				// dtb.setClipEnvelope(polygon.getEnvelopeInternal());
				dtb.setConstraints(polygon);
				dtb.setTolerance(sizeTol);
				GeometryCollection tri = (GeometryCollection) dtb.getTriangles(getFactory());
				// GeometryCollection tri = (GeometryCollection)
				// dtb.getDiagram(BasicTransfomartions.geomFactory);
				PreparedGeometry pg = pgfactory.create(polygon.buffer(sizeTol, 5, 0));
				int nb = tri.getNumGeometries();
				for ( int i = 0; i < nb; i++ ) {
					Geometry gg = tri.getGeometryN(i);
					// try {
					if ( (pg.covers(gg))) {/*gg.intersection(geom).getArea() > 0.001 */ //gg.relate(geom, "****1****")) {
						geoms.add(new GamaShape(gg));
						// }
						// } catch (AssertionFailedException e) {
						// if ( gg.isValid() && gg.intersection(geom.buffer(0.0001)).getArea() > 1.0 ) {
						// geoms.add((Polygon) gg);
						// }
					}
				}
			} else {
				ConformingDelaunayTriangulationBuilder dtb =
					new ConformingDelaunayTriangulationBuilder();
				dtb.setSites(polygon);
				dtb.setConstraints(polygon);
				dtb.setTolerance(0.01);
				GeometryCollection tri = (GeometryCollection) dtb.getTriangles(getFactory());
				int nb = tri.getNumGeometries();
				for ( int i = 0; i < nb; i++ ) {
					Geometry gg = tri.getGeometryN(i);
					if ( gg.intersection(geom).getArea() > 0.001) {
						geoms.add(new GamaShape(gg));
					}
				}
			}
		}
		return geoms;
	}

	public static List<LineString> squeletisation(final IScope scope, final Geometry geom) {
		IList<LineString> network = new GamaList<LineString>();
		IList polys = new GamaList(GeometryUtils.triangulation(geom, true));
		IGraph graph = Graphs.spatialLineIntersection(scope, polys);

		Collection<GamaShape> nodes = graph.vertexSet();
		GeometryFactory geomFact = GeometryUtils.getFactory();
		for ( GamaShape node : nodes ) {
			Coordinate[] coordsArr =
				GeometryUtils.extractPoints(node, geom, graph.degreeOf(node) / 2);
			if ( coordsArr != null ) {
				network.add(geomFact.createLineString(coordsArr));
			}
		}

		return network;
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

}
