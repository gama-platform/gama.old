/*******************************************************************************************************
 *
 * OutputCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.IOException;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.java_websocket.WebSocket;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.SimpleScalingProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.SaveStatement;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class OutputCommand.
 */
public class OutputCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object socket_id = map.get("socket_id");
		final Object species = map.get("species");
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("output");
		DEBUG.OUT(exp_id);
		DEBUG.OUT(socket_id);

		if (exp_id == "" || socket_id == null || species == null)
			return new CommandResponse(GamaServerMessageType.MalformedRequest,
					"For 'output', mandatory parameters are: 'exp_id', 'socket_id' and 'species' ", map, false);

		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id.toString(), exp_id);
		if (gama_exp == null || gama_exp.getSimulation() == null)
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		final boolean wasPaused = gama_exp.controller.isPaused();
		gama_exp.controller.directPause();
		IList<? extends IShape> agents = gama_exp.getSimulation().getSimulation().getPopulationFor(species.toString());

		@SuppressWarnings ("unchecked") final IList<String> ll =
				map.get("attributes") != null ? (IList<String>) map.get("attributes") : GamaListFactory.EMPTY_LIST;
		final String crs = map.get("crs") != null ? map.get("crs").toString() : "";
		String res = "";
		GamaServerMessageType status = GamaServerMessageType.CommandExecutedSuccessfully;
		try {
			res = buildGeoJSon(gama_exp.getSimulation().getExperimentPlan().getAgent().getScope(), agents, ll, crs);
		} catch (Exception ex) {
			res = ex.getMessage();
			status = GamaServerMessageType.RuntimeError;
		}

		if (!wasPaused) { gama_exp.controller.userStart(); }
		return new CommandResponse(status, res, map, true);
	}

	/**
	 * Builds the geo J son.
	 *
	 * @param scope
	 *            the scope
	 * @param agents
	 *            the agents
	 * @param filterAttr
	 *            the filter attr
	 * @param gis_code
	 *            the gis code
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SchemaException
	 *             the schema exception
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("deprecation")
	public String buildGeoJSon(final IScope scope, final IList<? extends IShape> agents, final IList<String> filterAttr,
			final String gis_code) throws IOException, SchemaException, GamaRuntimeException {

		final StringBuilder specs = new StringBuilder(agents.size() * 20);
		final String geomType = SaveStatement.getGeometryType(agents);
		specs.append("geometry:" + geomType);
		try {
			final SpeciesDescription species =
					agents instanceof IPopulation ? ((IPopulation) agents).getSpecies().getDescription()
							: agents.getGamlType().getContentType().getSpecies();
			final Map<String, IExpression> attributes = GamaMapFactory.create();
			// if (species != null) {
			// if (withFacet != null) {
			// computeInitsFromWithFacet(scope, withFacet, attributes, species);
			// } else if (attributesFacet != null) { computeInitsFromAttributesFacet(scope, attributes, species); }

			for (final String var : species.getAttributeNames()) {
				// System.out.println(var);
				// if(var.equals("state")){ attributes.put(var, species.getVarExpr(var, false)); }
				if (!SaveStatement.NON_SAVEABLE_ATTRIBUTE_NAMES.contains(var) && filterAttr.contains(var)) {
					attributes.put(var, species.getVarExpr(var, false));
				}
			}
			for (final String e : attributes.keySet()) {
				if (e == null) { continue; }
				final IExpression var = attributes.get(e);
				String name = e.replace("\"", "");
				name = name.replace("'", "");
				name = name.replace(":", "_");
				final String type = SaveStatement.type(var);
				specs.append(',').append(name).append(':').append(type);
			}
			// }
			final IProjection proj = defineProjection2(scope, gis_code);

			// AD 11/02/15 Added to allow saving to new directories
			if (agents.isEmpty()) return "";

			// The name of the type and the name of the feature source shoud now be
			// the same.
			final SimpleFeatureType type = DataUtilities.createType("geojson", specs.toString());
			final SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
			final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

			// AD Builds once the list of agent attributes to evaluate
			int i = 0;
			for (final IShape ag : agents) {
				final SimpleFeature ff = builder.buildFeature(i + "");
				i++;
				final boolean ok = SaveStatement.buildFeature(scope, ff, ag, proj, attributes.values());
				if (!ok) { continue; }
				featureCollection.add(ff);
			}
			// System.out.println(Jsoner.serialize(agents));
			final FeatureJSON io = new FeatureJSON(new GeometryJSON(20));
			return io.toString(featureCollection);
			// return Jsoner.serialize(agents);

		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Define projection 2.
	 *
	 * @param scope
	 *            the scope
	 * @param gis_code
	 *            the gis code
	 * @return the i projection
	 */
	public static IProjection defineProjection2(final IScope scope, final String gis_code) {
		// String code = gis_code==null||"".equals(gis_code)?"EPSG:4326":gis_code;
		// if (crsCode != null) {
		// final IType type = crsCode.getGamlType();
		// if (type.id() == IType.INT || type.id() == IType.FLOAT) {
		// code = "EPSG:" + Cast.asInt(scope, crsCode.value(scope));
		// } else if (type.id() == IType.STRING) { code = (String) crsCode.value(scope); }
		// }
		// IProjection gis;
		// try {
		// gis = scope.getSimulation().getProjectionFactory().getWorld();
		// gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
		// } catch (final FactoryException e1) {
		// throw GamaRuntimeException.error(
		// "The code " + code + " does not correspond to a known EPSG code. GAMA is unable to save ", scope);
		// }

		String code = null;
		if (gis_code != null) { code = gis_code; }
		IProjection gis;
		if (code == null) {
			final boolean useNoSpecific = GamaPreferences.External.LIB_USE_DEFAULT.getValue();
			if (!useNoSpecific) {
				code = "EPSG:" + GamaPreferences.External.LIB_OUTPUT_CRS.getValue();
				try {
					gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
				} catch (final FactoryException e1) {
					throw GamaRuntimeException.error(
							"The code " + code + " does not correspond to a known EPSG code. GAMA is unable to save ",
							scope);
				}
			} else {
				gis = scope.getSimulation().getProjectionFactory().getWorld();
				if (gis == null || gis.getInitialCRS(scope) == null) {
					final boolean alreadyprojected = GamaPreferences.External.LIB_PROJECTED.getValue();
					if (alreadyprojected) {
						code = "EPSG:" + GamaPreferences.External.LIB_TARGET_CRS.getValue();
					} else {
						code = "EPSG:" + GamaPreferences.External.LIB_INITIAL_CRS.getValue();
					}
					try {
						gis = scope.getSimulation().getProjectionFactory().forSavingWith(scope, code);
					} catch (final FactoryException e1) {
						throw GamaRuntimeException.error("The code " + code
								+ " does not correspond to a known EPSG code. GAMA is unable to save ", scope);
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
				throw GamaRuntimeException.error(
						"The code " + code + " does not correspond to a known EPSG code. GAMA is unable to save ",
						scope);
			}
		}

		return gis;
	}

}
