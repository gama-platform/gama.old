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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.util;

import java.util.*;
import msi.gama.database.sql.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.projection.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.*;
import msi.gaml.types.GamaGeometryType;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.prep.*;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import com.vividsolutions.jts.triangulate.*;
import com.vividsolutions.jts.triangulate.quadedge.LocateFailureException;

/**
 * The class GamaGeometryUtils.
 * 
 * @author drogoul
 * @since 14 d�c. 2011
 * 
 */
public class GeometryUtils {

	public static GeometryFactory factory = new GeometryFactory();
	public static PreparedGeometryFactory pgfactory = new PreparedGeometryFactory();
	public static CoordinateSequenceFactory coordFactory = factory.getCoordinateSequenceFactory();

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
			final int i = rand.between(0, geom.getCoordinates().length - 2);
			final Coordinate source = geom.getCoordinates()[i];
			final Coordinate target = geom.getCoordinates()[i + 1];
			if ( source.x != target.x ) {
				final double a = (source.y - target.y) / (source.x - target.x);
				final double b = source.y - a * source.x;
				final double x = rand.between(source.x, target.x);
				final double y = a * x + b;
				return new GamaPoint(x, y, 0);
			}
			final double x = source.x;
			final double y = rand.between(source.y, target.y);
			return new GamaPoint(x, y, 0);
		}
		if ( geom instanceof Polygon ) {
			final Envelope env = geom.getEnvelopeInternal();
			final double xMin = env.getMinX();
			final double xMax = env.getMaxX();
			final double yMin = env.getMinY();
			final double yMax = env.getMaxY();
			final double x = rand.between(xMin, xMax);
			final Coordinate coord1 = new Coordinate(x, yMin);
			final Coordinate coord2 = new Coordinate(x, yMax);
			final Coordinate[] coords = { coord1, coord2 };
			Geometry line = factory.createLineString(coords);
			try {
				line = line.intersection(geom);
			} catch (final Exception e) {
				final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
				line =
					GeometryPrecisionReducer.reducePointwise(line, pm).intersection(
						GeometryPrecisionReducer.reducePointwise(geom, pm));

			}
			return pointInGeom(line, rand);
		}
		if ( geom instanceof GeometryCollection ) { return pointInGeom(
			geom.getGeometryN(rand.between(0, geom.getNumGeometries() - 1)), rand); }

		return null;

	}

	private static Coordinate[] minimiseLength(final Coordinate[] coords) {
		final GeometryFactory geomFact = factory;
		final double dist1 = geomFact.createLineString(coords).getLength();
		final Coordinate[] coordstest1 = new Coordinate[3];
		coordstest1[0] = coords[0];
		coordstest1[1] = coords[2];
		coordstest1[2] = coords[1];
		final double dist2 = geomFact.createLineString(coordstest1).getLength();

		final Coordinate[] coordstest2 = new Coordinate[3];
		coordstest2[0] = coords[1];
		coordstest2[1] = coords[0];
		coordstest2[2] = coords[2];
		final double dist3 = geomFact.createLineString(coordstest2).getLength();

		if ( dist1 <= dist2 && dist1 <= dist3 ) { return coords; }
		if ( dist2 <= dist1 && dist2 <= dist3 ) { return coordstest1; }
		if ( dist3 <= dist1 && dist3 <= dist2 ) { return coordstest2; }
		return coords;
	}

	public static Coordinate[] extractPoints(final IShape triangle, final Geometry geom, final int degree) {
		final Coordinate[] coords = triangle.getInnerGeometry().getCoordinates();
		final Coordinate[] c1 = { coords[0], coords[1] };
		final Coordinate[] c2 = { coords[1], coords[2] };
		final Coordinate[] c3 = { coords[2], coords[3] };
		final LineString l1 = factory.createLineString(c1);
		final LineString l2 = factory.createLineString(c2);
		final LineString l3 = factory.createLineString(c3);
		final Coordinate[] pts = new Coordinate[degree];
		if ( degree == 3 ) {
			pts[0] = l1.getCentroid().getCoordinate();
			pts[1] = l2.getCentroid().getCoordinate();
			pts[2] = l3.getCentroid().getCoordinate();
			return minimiseLength(pts);
		} else if ( degree == 2 ) {
			final Geometry bounds = geom.getBoundary().buffer(1);
			final double val1 = bounds.intersection(l1).getLength() / l1.getLength();
			final double val2 = bounds.intersection(l2).getLength() / l2.getLength();
			final double val3 = bounds.intersection(l3).getLength() / l3.getLength();
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

	public static GamaList<IShape> hexagonalGridFromGeom(final IShape geom, final int nbRows, final int nbColumns) {
		final double widthEnv = geom.getEnvelope().getWidth();
		final double heightEnv = geom.getEnvelope().getHeight();
		double xmin = geom.getEnvelope().getMinX();
		double ymin = geom.getEnvelope().getMinY();
		final double widthHex = widthEnv / (nbColumns * 0.75 + 0.25);
		final double heightHex = heightEnv / nbRows;
		final GamaList<IShape> geoms = new GamaList<IShape>();
		xmin += widthHex / 2.0;
		ymin += heightHex / 2.0;
		for ( int l = 0; l < nbRows; l++ ) {
			for ( int c = 0; c < nbColumns; c = c + 2 ) {
				final GamaShape poly =
					(GamaShape) GamaGeometryType.buildHexagon(widthHex, heightHex, new GamaPoint(xmin + c * widthHex *
						0.75, ymin + l * heightHex, 0));
				// GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(size, xmin + (c * 1.5)
				// * size, ymin + 2* size*val * l);
				if ( geom.covers(poly) ) {
					geoms.add(poly);
				}
			}
		}
		for ( int l = 0; l < nbRows; l++ ) {
			for ( int c = 1; c < nbColumns; c = c + 2 ) {
				final GamaShape poly =
					(GamaShape) GamaGeometryType.buildHexagon(widthHex, heightHex, new GamaPoint(xmin + c * widthHex *
						0.75, ymin + (l + 0.5) * heightHex, 0));
				// GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(size, xmin + (c * 1.5)
				// * size, ymin + 2* size*val * l);
				if ( geom.covers(poly) ) {
					geoms.add(poly);
				}
			}
		}
		/*
		 * for(int l=0;l<nbColumns;l++){
		 * for(int c=0;c<nbRows;c = c+2){
		 * GamaShape poly = (GamaShape) GamaGeometryType.buildHexagon(size, xmin + ((c +1) * 1.5) *
		 * size, ymin + 2* size*val * (l+0.5));
		 * if (geom.covers(poly))
		 * geoms.add(poly);
		 * }
		 * }
		 */
		return geoms;
	}

	public static List<Geometry> discretisation(final Geometry geom, final double size, final boolean complex) {
		final List<Geometry> geoms = new ArrayList<Geometry>();
		if ( geom instanceof GeometryCollection ) {
			final GeometryCollection gc = (GeometryCollection) geom;
			for ( int i = 0; i < gc.getNumGeometries(); i++ ) {
				geoms.addAll(discretisation(gc.getGeometryN(i), size, complex));
			}
		} else {
			final Envelope env = geom.getEnvelopeInternal();
			final double xMax = env.getMaxX();
			final double yMax = env.getMaxY();
			double x = env.getMinX();
			double y = env.getMinY();
			final GeometryFactory geomFact = factory;
			while (x < xMax) {
				y = env.getMinY();
				while (y < yMax) {
					final Coordinate c1 = new Coordinate(x, y);
					final Coordinate c2 = new Coordinate(x + size, y);
					final Coordinate c3 = new Coordinate(x + size, y + size);
					final Coordinate c4 = new Coordinate(x, y + size);
					final Coordinate[] cc = { c1, c2, c3, c4, c1 };
					final Geometry square = geomFact.createPolygon(geomFact.createLinearRing(cc), null);
					y += size;
					try {
						Geometry g = null;
						try {
							g = square.intersection(geom);
						} catch (final Exception e) {
							final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
							g =
								GeometryPrecisionReducer.reducePointwise(geom, pm).intersection(
									GeometryPrecisionReducer.reducePointwise(square, pm));
						}
						// geoms.add(g);
						if ( complex ) {
							geoms.add(g);
						} else {
							if ( g instanceof Polygon ) {
								geoms.add(g);
							} else if ( g instanceof MultiPolygon ) {
								final MultiPolygon mp = (MultiPolygon) g;
								for ( int i = 0; i < mp.getNumGeometries(); i++ ) {
									if ( mp.getGeometryN(i) instanceof Polygon ) {
										geoms.add(mp.getGeometryN(i));
									}
								}
							}
						}
					} catch (final TopologyException e) {}
				}
				x += size;
			}
		}
		return geoms;
	}

	public static GamaList<IShape> triangulation(final IScope scope, final IList<IShape> lines) {
		final GamaList<IShape> geoms = new GamaList<IShape>();
		final ConformingDelaunayTriangulationBuilder dtb = new ConformingDelaunayTriangulationBuilder();

		final Geometry points = GamaGeometryType.geometriesToGeometry(scope, lines).getInnerGeometry();
		final double sizeTol = Math.sqrt(points.getEnvelope().getArea()) / 100.0;

		dtb.setSites(points);
		dtb.setConstraints(points);
		dtb.setTolerance(sizeTol);
		final GeometryCollection tri = (GeometryCollection) dtb.getTriangles(factory);
		final int nb = tri.getNumGeometries();
		for ( int i = 0; i < nb; i++ ) {
			final Geometry gg = tri.getGeometryN(i);
			geoms.add(new GamaShape(gg));
		}
		return geoms;
	}

	public static GamaList<IShape> triangulation(final IScope scope, final Geometry geom) {
		final GamaList<IShape> geoms = new GamaList<IShape>();
		if ( geom instanceof GeometryCollection ) {
			final GeometryCollection gc = (GeometryCollection) geom;
			for ( int i = 0; i < gc.getNumGeometries(); i++ ) {
				geoms.addAll(triangulation(scope, gc.getGeometryN(i)));
			}
		} else if ( geom instanceof Polygon ) {
			final Polygon polygon = (Polygon) geom;
			final double sizeTol = Math.sqrt(polygon.getArea()) / 100.0;
			final ConformingDelaunayTriangulationBuilder dtb = new ConformingDelaunayTriangulationBuilder();
			GeometryCollection tri = null;
			try {
				dtb.setSites(polygon);
				dtb.setConstraints(polygon);
				dtb.setTolerance(sizeTol);
				tri = (GeometryCollection) dtb.getTriangles(factory);
			} catch (final LocateFailureException e) {
				throw GamaRuntimeException.error("Impossible to draw Geometry");
			}
			final PreparedGeometry pg = pgfactory.create(polygon.buffer(sizeTol, 5, 0));
			final PreparedGeometry env = pgfactory.create(pg.getGeometry().getEnvelope());
			final int nb = tri.getNumGeometries();
			for ( int i = 0; i < nb; i++ ) {

				final Geometry gg = tri.getGeometryN(i);

				if ( env.covers(gg) && pg.covers(gg) ) {
					geoms.add(new GamaShape(gg));
				}
			}
		}
		return geoms;
	}

	public class contraintVertexFactory3D implements ConstraintVertexFactory {

		@Override
		public ConstraintVertex createVertex(final Coordinate p, final Segment constraintSeg) {
			final Coordinate c = new Coordinate(p);
			c.z = p.z;
			return new ConstraintVertex(c);
		}

	}

	public static List<LineString> squeletisation(final IScope scope, final Geometry geom) {
		final List<LineString> network = new GamaList<LineString>();
		final IList polys = new GamaList(GeometryUtils.triangulation(scope, geom));
		final IGraph graph = Graphs.spatialLineIntersection(scope, polys);

		final Collection<GamaShape> nodes = graph.vertexSet();
		final GeometryFactory geomFact = GeometryUtils.factory;
		for ( final GamaShape node : nodes ) {
			final Coordinate[] coordsArr = GeometryUtils.extractPoints(node, geom, graph.degreeOf(node) / 2);
			if ( coordsArr != null ) {
				network.add(geomFact.createLineString(coordsArr));
			}
		}

		return network;
	}

	public static Geometry buildGeometryJTS(final List<List<List<ILocation>>> listPoints) {
		final int geometryType = geometryType(listPoints);
		switch (geometryType) {
			case NULL:
				return null;
			case POINT:
				return buildPoint(listPoints.get(0));
			case LINE:
				return buildLine(listPoints.get(0));
			case POLYGON:
				return buildPolygon(listPoints.get(0));
			case MULTIPOINT:
				final int nb = listPoints.size();
				final Point[] geoms = new Point[nb];
				for ( int i = 0; i < nb; i++ ) {
					geoms[i] = (Point) buildPoint(listPoints.get(i));
				}
				return factory.createMultiPoint(geoms);
			case MULTILINE:
				final int n = listPoints.size();
				final LineString[] lines = new LineString[n];
				for ( int i = 0; i < n; i++ ) {
					lines[i] = (LineString) buildLine(listPoints.get(i));
				}
				return factory.createMultiLineString(lines);
			case MULTIPOLYGON:
				final int n3 = listPoints.size();
				final Polygon[] polys = new Polygon[n3];
				for ( int i = 0; i < n3; i++ ) {
					polys[i] = (Polygon) buildPolygon(listPoints.get(i));
				}
				return factory.createMultiPolygon(polys);
			default:
				return null;
		}
	}

	private static Geometry buildPoint(final List<List<ILocation>> listPoints) {
		return factory.createPoint((Coordinate) listPoints.get(0).get(0));
	}

	private static Geometry buildLine(final List<List<ILocation>> listPoints) {
		final List<ILocation> coords = listPoints.get(0);
		final int nb = coords.size();
		final Coordinate[] coordinates = new Coordinate[nb];
		for ( int i = 0; i < nb; i++ ) {
			coordinates[i] = (Coordinate) coords.get(i);
		}
		return factory.createLineString(coordinates);
	}

	private static Geometry buildPolygon(final List<List<ILocation>> listPoints) {
		final List<ILocation> coords = listPoints.get(0);
		final int nb = coords.size();
		final Coordinate[] coordinates = new Coordinate[nb];
		for ( int i = 0; i < nb; i++ ) {
			coordinates[i] = (Coordinate) coords.get(i);
		}
		final int nbHoles = listPoints.size() - 1;
		LinearRing[] holes = null;
		if ( nbHoles > 0 ) {
			holes = new LinearRing[nbHoles];
			for ( int i = 0; i < nbHoles; i++ ) {
				final List<ILocation> coordsH = listPoints.get(i + 1);
				final int nbp = coordsH.size();
				final Coordinate[] coordinatesH = new Coordinate[nbp];
				for ( int j = 0; j < nbp; j++ ) {
					coordinatesH[j] = (Coordinate) coordsH.get(j);
				}
				holes[i] = factory.createLinearRing(coordinatesH);
			}
		}
		final Polygon poly = factory.createPolygon(factory.createLinearRing(coordinates), holes);
		return poly;
	}

	private static int geometryType(final List<List<List<ILocation>>> listPoints) {
		final int size = listPoints.size();
		if ( size == 0 ) { return NULL; }
		if ( size == 1 ) { return geometryTypeSimp(listPoints.get(0)); }
		final int type = geometryTypeSimp(listPoints.get(0));
		switch (type) {
			case POINT:
				return MULTIPOINT;
			case LINE:
				return MULTILINE;
			case POLYGON:
				return POLYGON;
			default:
				return NULL;
		}
	}

	private static int geometryTypeSimp(final List<List<ILocation>> listPoints) {
		if ( listPoints.isEmpty() || listPoints.get(0).isEmpty() ) { return NULL; }
		final List<ILocation> list0 = listPoints.get(0);
		final int size0 = list0.size();
		if ( size0 == 1 || size0 == 2 && list0.get(0).equals(list0.get(listPoints.size() - 1)) ) { return POINT; }
		if ( !list0.get(0).equals(list0.get(listPoints.size() - 1)) || size0 < 3 ) { return LINE; }
		return POLYGON;
	}

	public static GamaList<GamaPoint> locExteriorRing(final Geometry geom, final Double distance) {
		final GamaList<GamaPoint> locs = new GamaList<GamaPoint>();
		if ( geom instanceof Point ) {
			locs.add(new GamaPoint(geom.getCoordinate()));
		} else if ( geom instanceof LineString ) {
			double dist_cur = 0;
			final int nbSp = geom.getNumPoints();
			final Coordinate[] coordsSimp = geom.getCoordinates();
			boolean same = false;
			double x_t = 0, y_t = 0, x_s = 0, y_s = 0;
			for ( int i = 0; i < nbSp - 1; i++ ) {
				if ( !same ) {
					final Coordinate s = coordsSimp[i];
					final Coordinate t = coordsSimp[i + 1];
					x_t = t.x;
					y_t = t.y;
					x_s = s.x;
					y_s = s.y;
				} else {
					i = i - 1;
				}
				final double dist = Math.sqrt(Math.pow(x_s - x_t, 2) + Math.pow(y_s - y_t, 2));
				if ( dist_cur < dist ) {
					final double ratio = dist_cur / dist;
					x_s = x_s + ratio * (x_t - x_s);
					y_s = y_s + ratio * (y_t - y_s);
					locs.add(new GamaPoint(x_s, y_s));
					dist_cur = distance;
					same = true;
				} else if ( dist_cur > dist ) {
					dist_cur = dist_cur - dist;
					same = false;
				} else {
					locs.add(new GamaPoint(x_t, y_t));
					dist_cur = distance;
					same = false;
				}
			}
		} else if ( geom instanceof Polygon ) {
			final Polygon poly = (Polygon) geom;
			locs.addAll(locExteriorRing(poly.getExteriorRing(), distance));
			for ( int i = 0; i < poly.getNumInteriorRing(); i++ ) {
				locs.addAll(locExteriorRing(poly.getInteriorRingN(i), distance));
			}
		}
		return locs;
	}

	// ---------------------------------------------------------------------------------------------
	// Thai.truongminh@gmail.com
	// Created date:24-Feb-2013: Process for SQL - MAP type
	// Modified: 29-Apr-2013

	public static Envelope computeEnvelopeFromSQLData(final IScope scope, final Map<String, Object> params) {
		final String crs = (String) params.get("crs");
		final String srid = (String) params.get("srid");
		final Boolean longitudeFirst =
			params.get("longitudeFirst") == null ? true : (Boolean) params.get("longitudeFirst");
		SqlConnection sqlConn;
		Envelope env = null;
		// create connection
		sqlConn = SqlUtils.createConnectionObject(scope, params);
		// get data
		final GamaList gamaList = sqlConn.selectDB((String) params.get("select"));
		env = SqlConnection.getBounds(gamaList);
		IProjection gis = ProjectionFactory.fromParams(params, env);
		env = gis.getProjectedEnvelope();
		return env;
		// ----------------------------------------------------------------------------------------------------
	}

	public static Envelope computeEnvelopeFrom(final IScope scope, final Object obj) {
		Envelope result = null;
		if ( obj instanceof Number ) {
			final double size = ((Number) obj).doubleValue();
			result = new Envelope(0, size, 0, size);
		} else if ( obj instanceof ILocation ) {
			final ILocation size = (ILocation) obj;
			result = new Envelope(0, size.getX(), 0, size.getY());
		} else if ( obj instanceof IShape ) {
			result = ((IShape) obj).getEnvelope();
		} else if ( obj instanceof Envelope ) {
			result = (Envelope) obj;
		} else if ( obj instanceof String ) {
			result = computeEnvelopeFrom(scope, Files.from(scope, (String) obj));
		} else if ( obj instanceof Map ) {
			result = computeEnvelopeFromSQLData(scope, (Map) obj);
		} else if ( obj instanceof IGamaFile ) {
			result = ((IGamaFile) obj).computeEnvelope(scope);
		} else if ( obj instanceof IList ) {
			Envelope boundsEnv = null;
			for ( final Object bounds : (IList) obj ) {
				final Envelope env = computeEnvelopeFrom(scope, bounds);
				if ( boundsEnv == null ) {
					boundsEnv = env;
				} else {
					boundsEnv.expandToInclude(env);
				}
			}
			result = boundsEnv;
		}
		return result;
	}

	public static GamaList<IShape> split_at(final IShape geom, final ILocation pt) {
		final GamaList<IShape> lines = new GamaList<IShape>();
		GamaList<Geometry> geoms = null;
		if ( geom.getInnerGeometry() instanceof LineString ) {
			final Coordinate[] coords = ((LineString) geom.getInnerGeometry()).getCoordinates();
			final Point pt1 = GeometryUtils.factory.createPoint(new GamaPoint(pt.getLocation()));
			final int nb = coords.length;
			int indexTarget = -1;
			double distanceT = Double.MAX_VALUE;
			for ( int i = 0; i < nb - 1; i++ ) {
				final Coordinate s = coords[i];
				final Coordinate t = coords[i + 1];
				final Coordinate[] seg = { s, t };
				final Geometry segment = GeometryUtils.factory.createLineString(seg);
				final double distT = segment.distance(pt1);
				if ( distT < distanceT ) {
					distanceT = distT;
					indexTarget = i;
				}
			}
			int nbSp = indexTarget + 2;
			final Coordinate[] coords1 = new Coordinate[nbSp];
			for ( int i = 0; i <= indexTarget; i++ ) {
				coords1[i] = coords[i];
			}
			coords1[indexTarget + 1] = new GamaPoint(pt.getLocation());

			nbSp = coords.length - indexTarget;
			final Coordinate[] coords2 = new Coordinate[nbSp];
			coords2[0] = new GamaPoint(pt.getLocation());
			int k = 1;
			for ( int i = indexTarget + 1; i < coords.length; i++ ) {
				coords2[k] = coords[i];
				k++;
			}
			final GamaList<Geometry> geoms1 = new GamaList<Geometry>();
			geoms1.add(GeometryUtils.factory.createLineString(coords1));
			geoms1.add(GeometryUtils.factory.createLineString(coords2));
			geoms = geoms1;
		} else if ( geom.getInnerGeometry() instanceof MultiLineString ) {
			final Point point = GeometryUtils.factory.createPoint((Coordinate) pt);
			Geometry geom2 = null;
			double distMin = Double.MAX_VALUE;
			final MultiLineString ml = (MultiLineString) geom.getInnerGeometry();
			for ( int i = 0; i < ml.getNumGeometries(); i++ ) {
				final double dist = ml.getGeometryN(i).distance(point);
				if ( dist <= distMin ) {
					geom2 = ml.getGeometryN(i);
					distMin = dist;
				}
			}
			final Coordinate[] coords = ((LineString) geom2).getCoordinates();
			final Point pt1 = GeometryUtils.factory.createPoint(new GamaPoint(pt.getLocation()));
			final int nb = coords.length;
			int indexTarget = -1;
			double distanceT = Double.MAX_VALUE;
			for ( int i = 0; i < nb - 1; i++ ) {
				final Coordinate s = coords[i];
				final Coordinate t = coords[i + 1];
				final Coordinate[] seg = { s, t };
				final Geometry segment = GeometryUtils.factory.createLineString(seg);
				final double distT = segment.distance(pt1);
				if ( distT < distanceT ) {
					distanceT = distT;
					indexTarget = i;
				}
			}
			int nbSp = indexTarget + 2;
			final Coordinate[] coords1 = new Coordinate[nbSp];
			for ( int i = 0; i <= indexTarget; i++ ) {
				coords1[i] = coords[i];
			}
			coords1[indexTarget + 1] = new GamaPoint(pt.getLocation());

			nbSp = coords.length - indexTarget;
			final Coordinate[] coords2 = new Coordinate[nbSp];
			coords2[0] = new GamaPoint(pt.getLocation());
			int k = 1;
			for ( int i = indexTarget + 1; i < coords.length; i++ ) {
				coords2[k] = coords[i];
				k++;
			}
			final GamaList<Geometry> geoms1 = new GamaList<Geometry>();
			geoms1.add(GeometryUtils.factory.createLineString(coords1));
			geoms1.add(GeometryUtils.factory.createLineString(coords2));
			geoms = geoms1;
		}
		if ( geoms != null ) {
			for ( final Geometry g : geoms ) {
				lines.add(new GamaShape(g));
			}
		}
		return lines;
	}
}
