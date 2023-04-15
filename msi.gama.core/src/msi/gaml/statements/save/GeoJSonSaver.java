/*******************************************************************************************************
 *
 * GeoJSonSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

/**
 * The Class GeoJSonSaver.
 */
public class GeoJSonSaver extends AbstractShapeSaver {

	// AD 2/1/16 Replace IAgent by IShape so as to be able to save geometries
	@Override
	protected void internalSave(final IScope scope, final OutputStream os, final List<? extends IShape> agents,
			final String specs, final String geomType, final Map<String, IExpression> attributes, final IProjection gis,
			final String epsgCode) throws IOException, SchemaException, GamaRuntimeException {
		// AD 11/02/15 Added to allow saving to new directories
		if (agents == null || agents.isEmpty()) return;

		// The name of the type and the name of the feature source shoud now be
		// the same.
		final SimpleFeatureType type = DataUtilities.createType("geojson", specs);
		final SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
		final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

		// AD Builds once the list of agent attributes to evaluate
		final Collection<IExpression> attributeValues =
				attributes == null ? Collections.emptyList() : attributes.values();
		int i = 0;
		for (final IShape ag : agents) {
			final SimpleFeature ff = builder.buildFeature(i + "");
			i++;
			final boolean ok = buildFeature(scope, ff, ag, gis, attributeValues);
			if (!ok) { continue; }
			featureCollection.add(ff);
		}

		final FeatureJSON io = new FeatureJSON(new GeometryJSON(20));
		io.writeFeatureCollection(featureCollection, os);

	}

	@Override
	public Set<String> computeFileTypes() {
		return Set.of("geojson", "json");
	}

}
