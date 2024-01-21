/*******************************************************************************************************
 *
 * AbstractShapeSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.feature.SchemaException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.ITyped;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.SimpleScalingProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.data.MapExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.SaveStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class AbstractShapeSaver.
 */
public abstract class AbstractShapeSaver extends AbstractSaver {

	/** The with facet. */

	/** The Constant DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE. */
	private static final String DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE =
			" does not correspond to a known EPSG code. GAMA is unable to save ";

	/** The Constant THE_CODE. */
	private static final String THE_CODE = "The code ";

	/** The Constant EPSG_LABEL. */
	private static final String EPSG_LABEL = "EPSG:";

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param f
	 *            the f
	 * @param epsgCode
	 *            the epsg code
	 * @param withFacet
	 *            the with facet
	 * @param attributesFacet
	 *            the attributes facet
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave) throws GamaRuntimeException {
		save(scope, item, file, code, attributesToSave);
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param os
	 *            the os
	 * @param epsgCode
	 *            the epsg code
	 * @param withFacet
	 *            the with facet
	 * @param attributesFacet
	 *            the attributes facet
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public void save(final IScope scope, final IExpression item, final OutputStream os, final String epsgCode,
			final Object attributesToSave) throws GamaRuntimeException {
		save(scope, item, (Object) os, epsgCode, attributesToSave);
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param object
	 *            the object
	 * @param epsgCode
	 *            the epsg code
	 * @param withFacet
	 *            the with facet
	 * @param attributesFacet
	 *            the attributes facet
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("unchecked")
	private void save(final IScope scope, final IExpression item, final Object object, final String epsgCode,
			final Object attributesToSave) throws GamaRuntimeException {
		Object value = item.value(scope);
		IList<? extends IShape> agents;
		if (value instanceof ISpecies is) {
			agents = scope.getAgent().getPopulationFor(is);
		} else if (value instanceof IShape is) {
			// see Issue #2857
			agents = GamaListFactory.wrap(Types.GEOMETRY, is);
		} else if (value instanceof IList) {
			agents = (IList<? extends IShape>) value;
		} else
			throw GamaRuntimeException.error(item.serializeToGaml(true) + " is not a list of agents or geometries",
					scope);
		final StringBuilder specs = new StringBuilder(agents.size() * 20);
		final String geomType = GeometryUtils.getGeometryStringType(agents);
		specs.append("geometry:" + geomType);
		try {
			final SpeciesDescription species = agents instanceof IPopulation pop ? pop.getSpecies().getDescription()
					: agents.getGamlType().getContentType().getSpecies(scope.getModel().getDescription());

			final Map<String, IExpression> attributes = computeInits(scope, species, attributesToSave);
			for (final Entry<String, IExpression> enty : attributes.entrySet()) {
				String e = enty.getKey();
				if (e == null) { continue; }
				final IExpression theVar = enty.getValue();
				String theName = e.replace("\"", "").replace("'", "").replace(":", "_");
				final String type = type(theVar);
				specs.append(',').append(theName).append(':').append(type);
			}
			// }
			final IProjection proj = defineProjection(scope, epsgCode);
			if (object instanceof File f) {
				internalSave(scope, f, agents, specs.toString(), geomType, attributes, proj, epsgCode);
			} else if (object instanceof OutputStream os) {
				internalSave(scope, os, agents, specs.toString(), geomType, attributes, proj, epsgCode);
			}
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}

	}

	/**
	 * Type.
	 *
	 * @param theVar
	 *            the var
	 * @return the string
	 */
	public static String type(final ITyped theVar) {
		return switch (theVar.getGamlType().id()) {
			case IType.BOOL -> "Boolean";
			case IType.INT -> "Integer";
			case IType.FLOAT -> "Double";
			default -> "String";
		};
	}

	/**
	 * Internal save.
	 *
	 * @param scope
	 *            the scope
	 * @param f
	 *            the f
	 * @param agents
	 *            the agents
	 * @param specs
	 *            the specs
	 * @param geomType
	 *            the geom type
	 * @param attributes
	 *            the attributes
	 * @param gis
	 *            the gis
	 * @param epsgCode
	 *            the epsg code
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SchemaException
	 *             the schema exception
	 */
	public void internalSave(final IScope scope, final File f, final List<? extends IShape> agents, final String specs,
			final String geomType, final Map<String, IExpression> attributes, final IProjection gis,
			final String epsgCode) throws GamaRuntimeException, IOException, SchemaException {
		// by default
		try (OutputStream bf = new BufferedOutputStream(new FileOutputStream(f))) {
			this.internalSave(scope, bf, agents, specs, geomType, attributes, gis, epsgCode);
		}
	}

	/**
	 * Internal save.
	 *
	 * @param scope
	 *            the scope
	 * @param os
	 *            the os
	 * @param agents
	 *            the agents
	 * @param string
	 *            the string
	 * @param geomType
	 *            the geom type
	 * @param attributes
	 *            the attributes
	 * @param proj
	 *            the proj
	 * @param epsgCode
	 *            the epsg code
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SchemaException
	 *             the schema exception
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected abstract void internalSave(IScope scope, OutputStream os, List<? extends IShape> agents, String string,
			String geomType, Map<String, IExpression> attributes, IProjection proj, final String epsgCode)
			throws IOException, SchemaException, GamaRuntimeException;

	/**
	 * Compute inits from with facet.
	 *
	 * @param attributes
	 *            the with facet
	 * @param result
	 *            the values
	 * @param species
	 *            the species
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected final Map<String, IExpression> computeInits(final IScope scope, final SpeciesDescription species,
			final Object attributes) throws GamaRuntimeException {
		if (attributes == null) return Collections.EMPTY_MAP;
		final Map<String, IExpression> result = GamaMapFactory.create();
		if (attributes instanceof Arguments args && species != null) {
			if (args.isEmpty()) {
				for (final String theVar : species.getAttributeNames()) {
					if (!SaveStatement.NON_SAVEABLE_ATTRIBUTE_NAMES.contains(theVar)) {
						result.put(theVar, species.getVarExpr(theVar, false));
					}
				}
			} else {
				args.forEachFacet((key, value) -> {
					result.put(value.getExpression().literalValue(), species.getVarExpr(key, false));
					return true;
				});
			}
		} else if (attributes instanceof MapExpression me) {
			final Map<IExpression, IExpression> map = me.getElements();
			map.forEach((key, value) -> {
				final String theName = Cast.asString(scope, key.value(scope));
				result.put(theName, value);
			});
		} else if (attributes instanceof IExpression exp) {
			@SuppressWarnings ("unchecked") final List<String> names =
					GamaListFactory.create(scope, Types.STRING, Cast.asList(scope, exp.value(scope)));
			if (species != null) {
				names.forEach(n -> result.put(n,
						species.hasAttribute(n) ? species.getVarExpr(n, false) : IExpressionFactory.NIL_EXPR));
			} else {
				// see #2982
				names.forEach(n -> result.put(n, new ConstantExpression(n)));
			}
		}
		return result;
	}

	/**
	 * Define projection.
	 *
	 * @param scope
	 *            the scope
	 * @param epsgCode
	 *            the epsg code
	 * @return the i projection
	 */
	protected IProjection defineProjection(final IScope scope, final String epsgCode) {
		String code = epsgCode;
		IProjection gis;
		if (code == null) {
			final boolean useNoSpecific = GamaPreferences.External.LIB_USE_DEFAULT.getValue();
			if (!useNoSpecific) {
				code = EPSG_LABEL + GamaPreferences.External.LIB_OUTPUT_CRS.getValue();
				try {
					gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
				} catch (final FactoryException e1) {
					throw GamaRuntimeException.error(THE_CODE + code + DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE, scope);
				}
			} else {
				gis = scope.getSimulation().getProjectionFactory().getWorld();
				if (gis == null || gis.getInitialCRS(scope) == null) {
					final boolean alreadyprojected = GamaPreferences.External.LIB_PROJECTED.getValue();
					if (alreadyprojected) {
						code = EPSG_LABEL + GamaPreferences.External.LIB_TARGET_CRS.getValue();
					} else {
						code = EPSG_LABEL + GamaPreferences.External.LIB_INITIAL_CRS.getValue();
					}
					try {
						gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
					} catch (final FactoryException e1) {
						throw GamaRuntimeException.error(THE_CODE + code + DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE,
								scope);
					}
				}
			}

		} else {
			if (code.startsWith("GAMA")) {
				if ("GAMA".equals(code)) return null;
				final String[] cs = code.split("::");
				if (cs.length == 2) {
					final Double val = Double.parseDouble(cs[1]);
					return new SimpleScalingProjection(val);
				}
				return null;
			}

			try {
				gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
			} catch (final FactoryException e1) {
				throw GamaRuntimeException.error(THE_CODE + code + DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE, scope);
			}
		}

		return gis;
	}

	/**
	 * Geometry collection to simple management.
	 *
	 * @param gg
	 *            the gg
	 * @return the geometry
	 */
	protected Geometry geometryCollectionToSimpleManagement(final Geometry gg) {
		if (gg instanceof GeometryCollection gc) {
			final int nb = gc.getNumGeometries();
			List<Polygon> polys = new ArrayList<>();
			List<LineString> lines = new ArrayList<>();
			List<Point> points = new ArrayList<>();

			for (int i = 0; i < nb; i++) {
				final Geometry g = gc.getGeometryN(i);
				if (g instanceof Polygon p) {
					polys.add(p);
				} else if (g instanceof LineString ls) {
					lines.add(ls);
				} else if (g instanceof Point p) { points.add(p); }
			}
			if (!polys.isEmpty()) {
				if (polys.size() == 1) return polys.get(0);
				Polygon[] ps = new Polygon[polys.size()];
				for (int i = 0; i < ps.length; i++) { ps[i] = polys.get(i); }

				return GeometryUtils.GEOMETRY_FACTORY.createMultiPolygon(ps);
			}
			if (!lines.isEmpty()) {

				if (lines.size() == 1) return lines.get(0);
				LineString[] ps = new LineString[lines.size()];
				for (int i = 0; i < ps.length; i++) { ps[i] = lines.get(i); }
				return GeometryUtils.GEOMETRY_FACTORY.createMultiLineString(ps);
			}
			if (!points.isEmpty()) {
				if (points.size() == 1) return points.get(0);

				Point[] ps = new Point[points.size()];
				for (int i = 0; i < ps.length; i++) { ps[i] = points.get(i); }
				return GeometryUtils.GEOMETRY_FACTORY.createMultiPoint(ps);
			}
		}
		return gg;
	}

	/**
	 * Builds the feature.
	 *
	 * @param scope
	 *            the scope
	 * @param ff
	 *            the ff
	 * @param ag
	 *            the ag
	 * @param gis
	 *            the gis
	 * @param attributeValues
	 *            the attribute values
	 * @return true, if successful
	 */
	public static boolean buildFeature(final IScope scope, final SimpleFeature ff, final IShape ag,
			final IProjection gis, final Collection<IExpression> attributeValues) {
		final List<Object> values = new ArrayList<>();
		// geometry is by convention (in specs) at position 0
		Geometry g = ag.getInnerGeometry();
		if (g == null) return false;
		if (gis != null) { g = gis.inverseTransform(g); }
		g = GeometryUtils.cleanGeometryCollection(GeometryUtils.fixesPolygonCWS(g));
		values.add(g);
		if (ag instanceof IAgent ia) {
			for (final IExpression variable : attributeValues) {
				Object val = ia.getScope().evaluate(variable, ia).getValue();
				if (variable.getGamlType().equals(Types.STRING)) {
					val = val == null ? "" : StringUtils.toJavaString(val.toString());
				}
				values.add(val);
			}
		} else {
			// see #2982. Assume it is an attribute of the shape
			for (final IExpression variable : attributeValues) {
				final Object val = variable.value(scope);
				if (val instanceof String s) {
					values.add(ag.getAttribute(s));
				} else {
					values.add("");
				}
			}
		}
		// AD Assumes that the type is ok.
		// AD WARNING Would require some sort of iterator operator that
		// would collect the values beforehand
		ff.setAttributes(values);
		return true;
	}

}
