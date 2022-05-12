/*******************************************************************************************************
 *
 * SaveStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ITyped;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class SaveHelper {// extends AbstractStatementSequence implements IStatement.WithArgs {

	public static IProjection defineProjection2(final IScope scope,final String gis_code) {
		String code = gis_code==null||"".equals(gis_code)?"EPSG:4326":gis_code;
//		if (crsCode != null) {
//			final IType type = crsCode.getGamlType();
//			if (type.id() == IType.INT || type.id() == IType.FLOAT) {
//				code = "EPSG:" + Cast.asInt(scope, crsCode.value(scope));
//			} else if (type.id() == IType.STRING) { code = (String) crsCode.value(scope); }
//		}
		IProjection gis;
		try {
			gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
		} catch (final FactoryException e1) {
			throw GamaRuntimeException.error(
					"The code " + code + " does not correspond to a known EPSG code. GAMA is unable to save ", scope);
		}

		return gis;
	}

	/**
	 * To clean string.
	 *
	 * @param o the o
	 * @return the string
	 */
	public String toCleanString(final Object o) {
		String val = Cast.toGaml(o).replace(';', ',');
		if (val.startsWith("'") && val.endsWith("'") || val.startsWith("\"") && val.endsWith("\"")) {
			val = val.substring(1, val.length() - 1);
		}

		if (o instanceof String) {
			val = val.replace("\\'", "'");
			val = val.replace("\\\"", "\"");

		}
		return val;
	}

	public static String type2(final ITyped var) {
		switch (var.getGamlType().id()) {
		case IType.BOOL:
			return "Boolean";
		case IType.INT:
			return "Integer";
		case IType.FLOAT:
			return "Double";
		default:
			return "String";
		}
	}

	/**
	 * Gets the geometry type.
	 *
	 * @param agents the agents
	 * @return the geometry type
	 */
	public static String getGeometryType(final List<? extends IShape> agents) {
		String geomType = "";
		boolean isLine = false;
		for (final IShape be : agents) {
			final IShape geom = be.getGeometry();
			if (geom != null && geom.getInnerGeometry() != null) {
				if (geom.getInnerGeometry().getNumGeometries() > 1) {
					Geometry g2 = geometryCollectionToSimpleManagement(geom.getInnerGeometry());
					if (!isLine && g2.getGeometryN(0).getClass() == Point.class) {
						geomType = Point.class.getSimpleName();
					} else if (g2.getGeometryN(0).getClass() == LineString.class) {
						geomType = LineString.class.getSimpleName();
					} else if (g2.getGeometryN(0).getClass() == Polygon.class)
						return Polygon.class.getSimpleName();

				} else {
					String geomType_tmp = geom.getInnerGeometry().getClass().getSimpleName();
					if (geom.getInnerGeometry() instanceof Polygon)
						return geomType_tmp;
					if (!isLine) {
						if (geom.getInnerGeometry() instanceof LineString) {
							isLine = true;
						}
						geomType = geomType_tmp;

					}

				}
			}
		}
		if ("DynamicLineString".equals(geomType)) {
			geomType = LineString.class.getSimpleName();
		}
		return geomType;
	}

	/**
	 * Fixes polygon CWS.
	 *
	 * @param g the g
	 * @return the geometry
	 */
	private static Geometry fixesPolygonCWS(final Geometry g) {
		if (g instanceof Polygon) {
			final Polygon p = (Polygon) g;
			final boolean clockwise = Orientation.isCCW(p.getExteriorRing().getCoordinates());
			if (p.getNumInteriorRing() == 0)
				return g;
			boolean change = false;
			final LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
			final GeometryFactory geomFact = new GeometryFactory();
			for (int i = 0; i < p.getNumInteriorRing(); i++) {
				final LinearRing hole = p.getInteriorRingN(i);
				if (!clockwise && !Orientation.isCCW(hole.getCoordinates())
						|| clockwise && Orientation.isCCW(hole.getCoordinates())) {
					change = true;
					final Coordinate[] coords = hole.getCoordinates();
					ArrayUtils.reverse(coords);
					final CoordinateSequence points = CoordinateArraySequenceFactory.instance().create(coords);
					holes[i] = new LinearRing(points, geomFact);
				} else {
					holes[i] = hole;
				}
			}
			if (change)
				return geomFact.createPolygon(p.getExteriorRing(), holes);
		} else if (g instanceof GeometryCollection) {
			final GeometryCollection gc = (GeometryCollection) g;
			boolean change = false;
			final GeometryFactory geomFact = new GeometryFactory();
			final Geometry[] geometries = new Geometry[gc.getNumGeometries()];
			for (int i = 0; i < gc.getNumGeometries(); i++) {
				final Geometry gg = gc.getGeometryN(i);
				if (gg instanceof Polygon) {
					geometries[i] = fixesPolygonCWS(gg);
					change = true;
				} else {
					geometries[i] = gg;
				}
			}
			if (change)
				return geomFact.createGeometryCollection(geometries);
		}
		return g;
	}

	/**
	 * Builds the feature.
	 *
	 * @param scope           the scope
	 * @param ff              the ff
	 * @param ag              the ag
	 * @param gis             the gis
	 * @param attributeValues the attribute values
	 * @return true, if successful
	 */
	public static boolean buildFeature(final IScope scope, final SimpleFeature ff, final IShape ag,
			final IProjection gis, final Collection<IExpression> attributeValues) {
		final List<Object> values = new ArrayList<>();
		// geometry is by convention (in specs) at position 0
		if (ag.getInnerGeometry() == null)
			return false;
		Geometry g = gis == null ? ag.getInnerGeometry() : gis.inverseTransform(ag.getInnerGeometry());

		g = fixesPolygonCWS(g);
		g = geometryCollectionManagement(g);

		values.add(g);
		if (ag instanceof IAgent) {
			for (final IExpression variable : attributeValues) {
				Object val = scope.evaluate(variable, (IAgent) ag).getValue();
				if (variable.getGamlType().equals(Types.STRING)) {
					if (val == null) {
						val = "";
					} else {
						final String val2 = val.toString();
						if (val2.startsWith("'") && val2.endsWith("'")
								|| val2.startsWith("\"") && val2.endsWith("\"")) {
							val = val2.substring(1, val2.length() - 1);
						}
					}
				}
				values.add(val);
			}
		} else {
			// see #2982. Assume it is an attribute of the shape
			for (final IExpression variable : attributeValues) {
				final Object val = variable.value(scope);
				if (val instanceof String) {
					values.add(ag.getAttribute((String) val));
				} else {
					values.add("");
				}
			}
		}
		// AD Assumes that the type is ok.
		// AD TODO replace this list of variable names by expressions
		// (to be
		// evaluated by agents), so that dynamic values can be passed
		// AD WARNING Would require some sort of iterator operator that
		// would collect the values beforehand
		ff.setAttributes(values);
		return true;
	}

	private static final Set<String> NON_SAVEABLE_ATTRIBUTE_NAMES = new HashSet<>(Arrays.asList(IKeyword.PEERS,
			IKeyword.LOCATION, IKeyword.HOST, IKeyword.AGENTS, IKeyword.MEMBERS, IKeyword.SHAPE));

	public static String buildGeoJSon(final IScope scope, final IList<? extends IShape> agents,
			final IList<String> filterAttr, final String gis_code) throws IOException, SchemaException, GamaRuntimeException {

		final StringBuilder specs = new StringBuilder(agents.size() * 20);
		final String geomType = getGeometryType(agents);
		specs.append("geometry:" + geomType);
		try {
			final SpeciesDescription species = agents instanceof IPopulation
					? ((IPopulation) agents).getSpecies().getDescription()
					: agents.getGamlType().getContentType().getSpecies();
			final Map<String, IExpression> attributes = GamaMapFactory.create();
			// if (species != null) {
//			if (withFacet != null) {
//				computeInitsFromWithFacet(scope, withFacet, attributes, species);
//			} else if (attributesFacet != null) { computeInitsFromAttributesFacet(scope, attributes, species); }

			for (final String var : species.getAttributeNames()) {
//				System.out.println(var);
//				if(var.equals("state")){ attributes.put(var, species.getVarExpr(var, false)); }
				if (!NON_SAVEABLE_ATTRIBUTE_NAMES.contains(var) && filterAttr.contains(var)) {
					attributes.put(var, species.getVarExpr(var, false));
				}
			}
			for (final String e : attributes.keySet()) {
				if (e == null) {
					continue;
				}
				final IExpression var = attributes.get(e);
				String name = e.replace("\"", "");
				name = name.replace("'", "");
				name = name.replace(":", "_");
				final String type = type2(var);
				specs.append(',').append(name).append(':').append(type);
			}
			// }
			final IProjection proj = defineProjection2(scope,gis_code);

			// AD 11/02/15 Added to allow saving to new directories
			if (agents == null || agents.isEmpty())
				return "";

			// The name of the type and the name of the feature source shoud now be
			// the same.
			final SimpleFeatureType type = DataUtilities.createType("geojson", specs.toString());
			final SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
			final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

			// AD Builds once the list of agent attributes to evaluate
			final Collection<IExpression> attributeValues = attributes == null ? Collections.EMPTY_LIST
					: attributes.values();
			int i = 0;
			for (final IShape ag : agents) {
				final SimpleFeature ff = builder.buildFeature(i + "");
				i++;
				final boolean ok = buildFeature(scope, ff, ag, proj, attributeValues);
				if (!ok) {
					continue;
				}
				featureCollection.add(ff);
			}

			final FeatureJSON io = new FeatureJSON(new GeometryJSON(20));
			return io.toString(featureCollection);

		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Geometry collection to simple management.
	 *
	 * @param gg the gg
	 * @return the geometry
	 */
	private static Geometry geometryCollectionToSimpleManagement(final Geometry gg) {
		if (gg instanceof GeometryCollection) {
			final int nb = ((GeometryCollection) gg).getNumGeometries();
			List<Polygon> polys = new ArrayList<>();
			List<LineString> lines = new ArrayList<>();
			List<Point> points = new ArrayList<>();

			for (int i = 0; i < nb; i++) {
				final Geometry g = ((GeometryCollection) gg).getGeometryN(i);
				if (g instanceof Polygon) {
					polys.add((Polygon) g);
				} else if (g instanceof LineString) {
					lines.add((LineString) g);
				} else if (g instanceof Point) {
					points.add((Point) g);
				}
			}
			if (!polys.isEmpty()) {
				if (polys.size() == 1)
					return polys.get(0);
				Polygon[] ps = new Polygon[polys.size()];
				for (int i = 0; i < ps.length; i++) {
					ps[i] = polys.get(i);
				}

				return GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(ps);
			}
			if (!lines.isEmpty()) {

				if (lines.size() == 1)
					return lines.get(0);
				LineString[] ps = new LineString[lines.size()];
				for (int i = 0; i < ps.length; i++) {
					ps[i] = lines.get(i);
				}
				return GeometryUtils.GEOMETRY_FACTORY.createMultiLineString(ps);
			}
			if (!points.isEmpty()) {
				if (points.size() == 1)
					return points.get(0);

				Point[] ps = new Point[points.size()];
				for (int i = 0; i < ps.length; i++) {
					ps[i] = points.get(i);
				}
				return GeometryUtils.GEOMETRY_FACTORY.createMultiPoint(ps);
			}
		}
		return gg;
	}

	/**
	 * Geometry collection management.
	 *
	 * @param gg the gg
	 * @return the geometry
	 */
	private static Geometry geometryCollectionManagement(final Geometry gg) {
		if (gg instanceof GeometryCollection) {
			boolean isMultiPolygon = true;
			boolean isMultiPoint = true;
			boolean isMultiLine = true;
			final int nb = ((GeometryCollection) gg).getNumGeometries();

			for (int i = 0; i < nb; i++) {
				final Geometry g = ((GeometryCollection) gg).getGeometryN(i);
				if (!(g instanceof Polygon)) {
					isMultiPolygon = false;
				}
				if (!(g instanceof LineString)) {
					isMultiLine = false;
				}
				if (!(g instanceof Point)) {
					isMultiPoint = false;
				}
			}

			if (isMultiPolygon) {
				final Polygon[] polygons = new Polygon[nb];
				for (int i = 0; i < nb; i++) {
					polygons[i] = (Polygon) ((GeometryCollection) gg).getGeometryN(i);
				}
				return GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(polygons);
			}
			if (isMultiLine) {
				final LineString[] lines = new LineString[nb];
				for (int i = 0; i < nb; i++) {
					lines[i] = (LineString) ((GeometryCollection) gg).getGeometryN(i);
				}
				return GeometryUtils.GEOMETRY_FACTORY.createMultiLineString(lines);
			}
			if (isMultiPoint) {
				final Point[] points = new Point[nb];
				for (int i = 0; i < nb; i++) {
					points[i] = (Point) ((GeometryCollection) gg).getGeometryN(i);
				}
				return GeometryUtils.GEOMETRY_FACTORY.createMultiPoint(points);
			}
		}
		return gg;
	}

}
