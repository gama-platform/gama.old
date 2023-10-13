/*******************************************************************************************************
 *
 * ShapeSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.SchemaException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

/**
 * The Class ShapeSaver.
 */
public class ShapeSaver extends AbstractShapeSaver {

	// AD 2/1/16 Replace IAgent by IShape so as to be able to save geometries
	@Override
	public void internalSave(final IScope scope, final File f, final List<? extends IShape> agents, final String specs,
			final String geomType, final Map<String, IExpression> attributes, final IProjection gis,
			final String epsgCode) throws IOException, SchemaException, GamaRuntimeException {
		// AD 11/02/15 Added to allow saving to new directories
		if (agents == null || agents.isEmpty()) return;

		final ShapefileDataStore store = new ShapefileDataStore(f.toURI().toURL());
		store.setCharset(StandardCharsets.UTF_8);
		// The name of the type and the name of the feature source shoud now be
		// the same.
		final SimpleFeatureType type =
				DataUtilities.createType(store.getFeatureSource().getEntry().getTypeName(), specs);
		store.createSchema(type);
		// AD: creation of a FeatureWriter on the store.
		boolean isPolygon =
				geomType.equals(MultiPolygon.class.getSimpleName()) || geomType.equals(Polygon.class.getSimpleName());
		boolean isLine = geomType.equals(MultiLineString.class.getSimpleName())
				|| geomType.equals(LineString.class.getSimpleName());
		boolean isPoint =
				geomType.equals(MultiPoint.class.getSimpleName()) || geomType.equals(Point.class.getSimpleName());
		try (FeatureWriter fw = store.getFeatureWriter(Transaction.AUTO_COMMIT)) {
			// AD Builds once the list of agent attributes to evaluate
			final Collection<IExpression> attributeValues =
					attributes == null ? Collections.emptyList() : attributes.values();

			for (final IShape ag : agents) {
				if (ag.getGeometries().size() > 1) {
					ag.setInnerGeometry(geometryCollectionToSimpleManagement(ag.getInnerGeometry()));
				}
				Geometry internal = ag.getInnerGeometry();
				if (isPolygon && (internal instanceof Polygon || internal instanceof MultiPolygon)
						|| isLine && ag.isLine() || isPoint && ag.isPoint()) {
					final SimpleFeature ff = (SimpleFeature) fw.next();
					final boolean ok = buildFeature(scope, ff, ag, gis, attributeValues);
					if (!ok) { break; }
				}

			}
			// Writes the prj file
			if (gis != null) {
				final CoordinateReferenceSystem crs = gis.getInitialCRS(scope);
				if (crs != null) {
					try (FileWriter fw1 = new FileWriter(f.getAbsolutePath().replace(".shp", ".prj"))) {
						fw1.write(crs.toString());
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (final ClassCastException e) {
			throw GamaRuntimeException.error(
					"Cannot save agents/geometries with different types of geometries (point, line, polygon) in a same shapefile",
					scope);
		} finally {
			store.dispose();

		}
	}

	@Override
	protected void internalSave(final IScope scope, final OutputStream os, final List<? extends IShape> agents,
			final String string, final String geomType, final Map<String, IExpression> attributes,
			final IProjection proj, final String epsgCode) throws IOException, SchemaException, GamaRuntimeException {
		// Do nothing

	}

	@Override
	public Set<String> computeFileTypes() {
		return Set.of("shp", "shape");
	}

	@Override
	public BiMap<String, String> getSynonyms() { return ImmutableBiMap.of("shp", "shape"); }

}
