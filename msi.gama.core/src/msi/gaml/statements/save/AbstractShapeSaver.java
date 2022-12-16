package msi.gaml.statements.save;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.feature.SchemaException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.FactoryException;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.preferences.GamaPreferences;
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
import msi.gaml.types.Types;

public abstract class AbstractShapeSaver {

	/** The with facet. */

	final Map<String, IExpression> attributes = GamaMapFactory.create();

	/** The Constant DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE. */
	private static final String DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE =
			" does not correspond to a known EPSG code. GAMA is unable to save ";

	/** The Constant THE_CODE. */
	private static final String THE_CODE = "The code ";

	/** The Constant EPSG_LABEL. */
	private static final String EPSG_LABEL = "EPSG:";

	public void save(final IScope scope, final IExpression item, final File f, final String epsgCode,
			final Arguments withFacet, final IExpression attributesFacet) throws GamaRuntimeException {
		save(scope, item, (Object) f, epsgCode, withFacet, attributesFacet);
	}

	public void save(final IScope scope, final IExpression item, final OutputStream os, final String epsgCode,
			final Arguments withFacet, final IExpression attributesFacet) throws GamaRuntimeException {
		save(scope, item, (Object) os, epsgCode, withFacet, attributesFacet);
	}

	private void save(final IScope scope, final IExpression item, final Object object, final String epsgCode,
			final Arguments withFacet, final IExpression attributesFacet) throws GamaRuntimeException {
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
			throw GamaRuntimeException.error(item.serialize(true) + " is not a list of agents or geometries", scope);
		final StringBuilder specs = new StringBuilder(agents.size() * 20);
		final String geomType = GeometryUtils.getGeometryStringType(agents);
		specs.append("geometry:" + geomType);
		try {
			final SpeciesDescription species = agents instanceof IPopulation pop ? pop.getSpecies().getDescription()
					: agents.getGamlType().getContentType().getSpecies();

			// if (species != null) {
			if (withFacet != null) {
				computeInitsFromWithFacet(withFacet, attributes, species);
			} else if (attributesFacet != null) {
				computeInitsFromAttributesFacet(scope, attributesFacet, attributes, species);
			}
			for (final Entry<String, IExpression> enty : attributes.entrySet()) {
				String e = enty.getKey();
				if (e == null) { continue; }
				final IExpression theVar = enty.getValue();
				String theName = e.replace("\"", "").replace("'", "").replace(":", "_");
				final String type = SaveStatement.type(theVar);
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

	public void internalSave(final IScope scope, final File f, final List<? extends IShape> agents, final String specs,
			final String geomType, final Map<String, IExpression> attributes, final IProjection gis,
			final String epsgCode) throws GamaRuntimeException, IOException, SchemaException {
		// by default
		try (OutputStream bf = new BufferedOutputStream(new FileOutputStream(f))) {
			this.internalSave(scope, bf, agents, specs, geomType, attributes, gis, epsgCode);
		}
	}

	protected abstract void internalSave(IScope scope, OutputStream os, List<? extends IShape> agents, String string,
			String geomType, Map<String, IExpression> attributes, IProjection proj, final String epsgCode)
			throws IOException, SchemaException, GamaRuntimeException;

	protected void computeInitsFromWithFacet(final Arguments withFacet, final Map<String, IExpression> values,
			final SpeciesDescription species) throws GamaRuntimeException {
		if (species == null) return;
		if (withFacet.isEmpty()) {
			for (final String theVar : species.getAttributeNames()) {
				if (!SaveStatement.NON_SAVEABLE_ATTRIBUTE_NAMES.contains(theVar)) {
					values.put(theVar, species.getVarExpr(theVar, false));
				}
			}
		} else {
			withFacet.forEachFacet((key, value) -> {
				values.put(value.getExpression().literalValue(), species.getVarExpr(key, false));
				return true;
			});
		}
	}

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

	protected void computeInitsFromAttributesFacet(final IScope scope, final IExpression attributesFacet,
			final Map<String, IExpression> values, final SpeciesDescription species) throws GamaRuntimeException {
		if (attributesFacet instanceof MapExpression me) {
			final Map<IExpression, IExpression> map = me.getElements();
			map.forEach((key, value) -> {
				final String theName = Cast.asString(scope, key.value(scope));
				values.put(theName, value);
			});
		} else {
			@SuppressWarnings ("unchecked") final List<String> names =
					GamaListFactory.create(scope, Types.STRING, Cast.asList(scope, attributesFacet.value(scope)));
			if (species != null) {
				names.forEach(n -> values.put(n,
						species.hasAttribute(n) ? species.getVarExpr(n, false) : IExpressionFactory.NIL_EXPR));
			} else {
				// see #2982
				names.forEach(n -> values.put(n, new ConstantExpression(n)));
			}
		}
	}

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

}
