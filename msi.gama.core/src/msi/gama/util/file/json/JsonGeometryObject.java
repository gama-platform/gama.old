/*******************************************************************************************************
 *
 * JsonGeometryObject.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;

import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.opengis.referencing.FactoryException;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.geometry.UniqueCoordinateSequence;
import msi.gama.metamodel.shape.GamaShapeFactory;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;

/**
 * The Class JsonGeometryObject. Takes care of encoding geometries using the GeoJson format, adding the SRID of the
 * current CRS.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 5 nov. 2023
 */
public class JsonGeometryObject extends JsonGamlObject {

	/**
	 * The prefix for EPSG codes in the <code>crs</code> property.
	 */
	public static final String EPSG_PREFIX = "EPSG:";

	/**
	 * Instantiates a new json shape object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param json
	 *            the json
	 * @date 4 nov. 2023
	 */
	public JsonGeometryObject(final Geometry geometry, final Json json) {
		this(toGeoJsonObject(geometry, json), json);
		try {
			int srid = CRS.lookupEpsgCode(ProjectionFactory.getTargetCRSOrDefault(GAMA.getRuntimeScope()), true);
			add(NAME_CRS,
					json.object(NAME_TYPE, NAME_NAME, NAME_PROPERTIES, json.object(NAME_NAME, EPSG_PREFIX + srid)));
		} catch (FactoryException e) {}
	}

	/**
	 * Instantiates a new json geometry object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @param json
	 *            the json
	 * @date 5 nov. 2023
	 */
	public JsonGeometryObject(final JsonAbstractObject object, final Json json) {
		super(Types.GEOMETRY.getName(), object, json);
	}

	/**
	 * Creates the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometry
	 *            the geometry
	 * @param encodeCRS
	 *            the encode CRS
	 * @return the map
	 * @date 4 nov. 2023
	 */
	private static JsonAbstractObject toGeoJsonObject(final Geometry geometry, final Json json) {
		JsonAbstractObject result = new JsonObject(json);
		result.add(NAME_TYPE, geometry.getGeometryType());
		JsonArray components = json.array();
		String key = NAME_COORDINATES;
		if (geometry instanceof Point || geometry instanceof LineString) {
			components = (JsonArray) json.valueOf(GeometryUtils.getContourCoordinates(geometry));
		} else if (geometry instanceof Polygon polygon) {
			components = toJsonArray(polygon, json);
		} else if (geometry instanceof MultiPoint multiPoint) {
			components = toJsonArray(multiPoint, json);
		} else if (geometry instanceof MultiLineString multiLineString) {
			components = toJsonArray(multiLineString, json);
		} else if (geometry instanceof MultiPolygon multiPolygon) {
			components = toJsonArray(multiPolygon, json);
		} else if (geometry instanceof GeometryCollection geometryCollection) {
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				components.add(toGeoJsonObject(geometryCollection.getGeometryN(i), json));
			}
			key = NAME_GEOMETRIES;
		} else
			throw new IllegalArgumentException("Unable to encode geometry " + geometry.getGeometryType());
		result.add(key, components);
		return result;
	}

	/**
	 * Make json aware.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param poly
	 *            the poly
	 * @return the list
	 * @date 4 nov. 2023
	 */
	private static JsonArray toJsonArray(final Polygon poly, final Json json) {
		JsonArray result = json.array();
		result.add(json.valueOf(GeometryUtils.getContourCoordinates(poly)));
		for (int i = 0; i < poly.getNumInteriorRing(); i++) {
			result.add(json.valueOf(GeometryUtils.getContourCoordinates(poly.getInteriorRingN(i))));
		}
		return result;
	}

	/**
	 * Make json aware.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryCollection
	 *            the geometry collection
	 * @return the list
	 * @date 4 nov. 2023
	 */
	private static JsonArray toJsonArray(final GeometryCollection geometryCollection, final Json json) {
		JsonArray list = json.array();
		for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
			Geometry geometry = geometryCollection.getGeometryN(i);
			if (geometry instanceof Polygon polygon) {
				list.add(toJsonArray(polygon, json));
			} else if (geometry instanceof LineString || geometry instanceof Point) {
				list.add(json.valueOf(GeometryUtils.getContourCoordinates(geometry)));
			}
		}
		return list;
	}

	@Override
	public IShape toGamlValue(final IScope scope) {
		Geometry g = fromGeoJsonObject(this);
		// int srid = readSRID(this);
		// TODO what to do with SRID ?
		// g = GeometryUtils.cleanGeometry(g);
		// g = scope.getSimulation().getProjectionFactory().getWorld().transform(g);

		IShape shape = GamaShapeFactory.createFrom(g);
		shape.setGeometricalType(Type.valueOf(get("inner_type").asString()));
		if (contains("depth")) { shape.setDepth(get("depth").asDouble()); }
		return shape;
	}

	/**
	 * Creates the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the geometry map
	 * @param factory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry fromGeoJsonObject(final JsonGeometryObject object) {
		return switch (object.get(NAME_TYPE).asString()) {
			case NAME_POINT -> buildPoint(object);
			case NAME_LINESTRING -> buildLineString(object);
			case NAME_POLYGON -> buildPolygon(object);
			case NAME_MULTIPOINT -> buildMultiPoint(object);
			case NAME_MULTILINESTRING -> buildMultiLineString(object);
			case NAME_MULTIPOLYGON -> buildMultiPolygon(object);
			case NAME_GEOMETRYCOLLECTION -> buildGeometryCollection(object);
			case NAME_FEATURE -> manageFeature(object);
			case NAME_FEATURECOLLECTION -> manageFeatureCollection(object);
			default -> throw new RuntimeException("Unexpected value for type");
		};
	}

	/**
	 * Creates a feature collection.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry manageFeatureCollection(final JsonGeometryObject geometryMap) {
		return GEOMETRY_FACTORY.createGeometryCollection(StreamEx.of(geometryMap.get(NAME_FEATURES).asArray())
				.select(JsonGeometryObject.class).map(JsonGeometryObject::manageFeature).toArray(Geometry.class));
	}

	/**
	 * Creates a feature.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry manageFeature(final JsonGeometryObject geometryMap) {
		return fromGeoJsonObject((JsonGeometryObject) geometryMap.get(NAME_GEOMETRY));
	}

	/**
	 * Creates the geometry collection.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildGeometryCollection(final JsonGeometryObject geometryMap) {
		return GEOMETRY_FACTORY.createGeometryCollection(StreamEx.of(geometryMap.get(NAME_GEOMETRIES).asArray())
				.select(JsonGeometryObject.class).map(JsonGeometryObject::fromGeoJsonObject).toArray(Geometry.class));
	}

	/**
	 * Creates a multi polygon.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildMultiPolygon(final JsonGeometryObject geometryMap) {
		Geometry result = null;
		JsonArray polygonsList = geometryMap.get(NAME_COORDINATES).asArray();
		Polygon[] polygons = new Polygon[polygonsList.size()];
		int p = 0;
		for (JsonValue ringsList : polygonsList) {
			List<CoordinateSequence> rings = new ArrayList<>();
			for (JsonValue coordinates : ringsList.asArray()) {
				rings.add(createCoordinateSequence(coordinates.asArray()));
			}
			if (rings.isEmpty()) { continue; }
			LinearRing outer = GEOMETRY_FACTORY.createLinearRing(rings.get(0));
			LinearRing[] inner = null;
			if (rings.size() > 1) {
				inner = new LinearRing[rings.size() - 1];
				for (int i = 1; i < rings.size(); i++) {
					inner[i - 1] = GEOMETRY_FACTORY.createLinearRing(rings.get(i));
				}
			}
			polygons[p] = GEOMETRY_FACTORY.createPolygon(outer, inner);
			++p;
		}
		result = GEOMETRY_FACTORY.createMultiPolygon(polygons);
		return result;
	}

	/**
	 * Creates a multi linestring.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildMultiLineString(final JsonGeometryObject geometryMap) {
		return GEOMETRY_FACTORY.createMultiLineString(StreamEx.of(geometryMap.get(NAME_COORDINATES).asArray())
				.map(c -> GEOMETRY_FACTORY.createLineString(createCoordinateSequence(c.asArray())))
				.toArray(LineString.class));
	}

	/**
	 * Creates the multi point.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildMultiPoint(final JsonGeometryObject geometryMap) {
		return GEOMETRY_FACTORY.createMultiPoint(createCoordinateSequence(geometryMap.get(NAME_COORDINATES).asArray()));
	}

	/**
	 * Creates the polygon.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildPolygon(final JsonGeometryObject geometryMap) {
		Geometry result = null;
		JsonArray ringsList = geometryMap.get(NAME_COORDINATES).asArray();
		if (ringsList.isEmpty()) return GEOMETRY_FACTORY.createPolygon();
		List<CoordinateSequence> rings = new ArrayList<>();
		for (JsonValue coordinates : ringsList) { rings.add(createCoordinateSequence(coordinates.asArray())); }
		LinearRing outer = GEOMETRY_FACTORY.createLinearRing(rings.get(0));
		LinearRing[] inner = null;
		if (rings.size() > 1) {
			inner = new LinearRing[rings.size() - 1];
			for (int i = 1; i < rings.size(); i++) { inner[i - 1] = GEOMETRY_FACTORY.createLinearRing(rings.get(i)); }
		}
		result = GEOMETRY_FACTORY.createPolygon(outer, inner);
		return result;
	}

	/**
	 * Creates the line string.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geometryMap
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildLineString(final JsonGeometryObject geometryMap) {
		return GEOMETRY_FACTORY.createLineString(createCoordinateSequence(geometryMap.get(NAME_COORDINATES).asArray()));
	}

	/**
	 * Creates the point.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the geometry map
	 * @param geometryFactory
	 *            the geometry factory
	 * @return the geometry
	 * @throws ParseException
	 *             the parse exception
	 * @date 5 nov. 2023
	 */
	private static Geometry buildPoint(final JsonGeometryObject object) {
		JsonArray c = object.get(NAME_COORDINATES).asArray();
		if (c.isEmpty()) return null;
		c = c.get(0).asArray();
		CoordinateSequence coordinate =
				new UniqueCoordinateSequence(c.get(0).asDouble(), c.get(1).asDouble(), c.get(2).asDouble());
		return GEOMETRY_FACTORY.createPoint(coordinate);
	}

	/**
	 * Creates the coordinate sequence.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param coordinates
	 *            the coordinates
	 * @return the coordinate sequence
	 * @date 5 nov. 2023
	 */
	private static CoordinateSequence createCoordinateSequence(final JsonArray coordinates) {
		ICoordinates result = ICoordinates.ofLength(coordinates.size());
		int i = 0;
		for (JsonValue ordinates : coordinates) {
			JsonArray c = ordinates.asArray();
			if (c.size() > 0) { result.setOrdinate(i, 0, c.get(0).asDouble()); }
			if (c.size() > 1) { result.setOrdinate(i, 1, c.get(1).asDouble()); }
			if (c.size() > 2) { result.setOrdinate(i++, 2, c.get(2).asDouble()); }
		}
		return result;
	}

	/**
	 * Read SRID.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return the int
	 * @date 5 nov. 2023
	 */
	private static int readSRID(final JsonGeometryObject object) {
		JsonObject crs = object.get(NAME_CRS).asObject();
		if (crs != null) {
			JsonObject properties = crs.get(NAME_PROPERTIES).asObject();
			String name = properties.get(NAME_NAME).asString();
			String[] split = name.split(":");
			String epsg = split[1];
			return Integer.parseInt(epsg);
		}
		return 4326;
		// The default CRS is a geographic coordinate reference
		// system, using the WGS84 datum, and with longitude and
		// latitude units of decimal degrees. SRID 4326
	}

}
