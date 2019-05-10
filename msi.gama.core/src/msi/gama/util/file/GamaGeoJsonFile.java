/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGeoJsonFile.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@file (
		name = "geojson",
		extensions = { "json", "geojson", "geo.json" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.GIS, IConcept.FILE },
		doc = @doc ("Represents geospatial files written using the GeoJSON format. The internal representation is a list of geometries"))
    public class GamaGeoJsonFile extends GamaGisFile {
	@doc (value= "This file constructor allows to read a geojson file (https://geojson.org/)",
			examples = {
				@example(value = "file f <- geojson_file(\"file.json\");", isExecutable = false)
			})
	public GamaGeoJsonFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}
	@doc (value= "This file constructor allows to read a geojson file and specifying the coordinates system code, as an int",
			examples = {
				@example(value = "file f <- geojson_file(\"file.json\", 32648);", isExecutable = false)
			})
	public GamaGeoJsonFile(final IScope scope, final String pathName, final Integer code) {
		super(scope, pathName, code);
		// TODO Auto-generated constructor stub
	}
	@doc (value= "This file constructor allows to read a geojson file and specifying the coordinates system code (epg,...,), as a string",
			examples = {
				@example(value = "file f <- geojson_file(\"file.json\", \"EPSG:32648\");", isExecutable = false)
			})	
	public GamaGeoJsonFile(final IScope scope, final String pathName, final String code) {
		super(scope, pathName, code);
		// TODO Auto-generated constructor stub
	}
	@doc (value= "This file constructor allows to read a geojson file and take a potential z value (not taken in account by default)",
			examples = {
				@example(value = "file f <- geojson_file(\"file.json\", true);", isExecutable = false)
			})
	public GamaGeoJsonFile(final IScope scope, final String pathName, final boolean withZ) {
		super(scope, pathName, (Integer) null, withZ);
		// TODO Auto-generated constructor stub
	}
	@doc (value= "This file constructor allows to read a geojson file, specifying the coordinates system code, as an int and take a potential z value (not taken in account by default)",
			examples = {
				@example(value = "file f <- geojson_file(\"file.json\",32648, true);", isExecutable = false)
			})
	public GamaGeoJsonFile(final IScope scope, final String pathName, final Integer code, final boolean withZ) {
		super(scope, pathName, code, withZ);
		// TODO Auto-generated constructor stub
	}
	@doc (value= "This file constructor allows to read a geojson file, specifying the coordinates system code (epg,...,), as a string and take a potential z value (not taken in account by default",
			examples = {
				@example(value = "file f <- geojson_file(\"file.json\", \"EPSG:32648\",true);", isExecutable = false)
			})
	public GamaGeoJsonFile(final IScope scope, final String pathName, final String code, final boolean withZ) {
		super(scope, pathName, code, withZ);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readShapes(scope);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		final Map<String, String> attributes = new LinkedHashMap<>();
		final SimpleFeatureCollection store = getFeatureCollection(scope);
		final java.util.List<AttributeDescriptor> att_list = store.getSchema().getAttributeDescriptors();
		for (final AttributeDescriptor desc : att_list) {
			String type;
			if (desc.getType() instanceof GeometryType) {
				type = "geometry";
			} else {
				type = Types.get(desc.getType().getBinding()).toString();
			}
			attributes.put(desc.getName().getLocalPart(), type);
		}

		return GamaListFactory.createWithoutCasting(Types.STRING, attributes.keySet());
	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS(final IScope scope) {
		final SimpleFeatureCollection store = getFeatureCollection(scope);
		return store.getSchema().getCoordinateReferenceSystem();
	}

	protected SimpleFeatureCollection getFeatureCollection(final IScope scope) {
		try (FileReader fileReader = new FileReader(getFile(scope))) {
			final JSONParser parser = new JSONParser();
			final Object obj = parser.parse(fileReader);
			final FeatureJSON fJSON = new FeatureJSON();
			return (SimpleFeatureCollection) fJSON.readFeatureCollection(obj.toString());
		} catch (final IOException | ParseException e) {
			GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
		}
		return null;
	}

	public void readShapes(final IScope scope) {
		final IList<IShape> list = getBuffer();
		int size = 0;
		final SimpleFeatureCollection fc = getFeatureCollection(scope);
		if (fc == null)
			return;
		final Envelope3D env = Envelope3D.of(fc.getBounds());
		size = fc.size();
		int index = 0;
		computeProjection(scope, env);
		try (SimpleFeatureIterator reader = fc.features()) {
			while (reader.hasNext()) {
				index++;
				if (index % 20 == 0)
					scope.getGui().getStatus(scope).setSubStatusCompletion(index / size);
				final SimpleFeature feature = reader.next();
				Geometry g = (Geometry) feature.getDefaultGeometry();
				if (g != null && !g.isEmpty() /* Fix for Issue 725 && 677 */ ) {
					g = gis.transform(g);
					if (!with3D) {
						g.apply(ZERO_Z);
						g.geometryChanged();
					}
					list.add(new GamaGisGeometry(g, feature));
				} else if (g == null) {
					// See Issue 725
					GAMA.reportError(scope,
							GamaRuntimeException
									.warning("GamaGeoJsonFile.fillBuffer; geometry could not be added  as it is "
											+ "nil: " + feature.getIdentifier(), scope),
							false);
				}
			}
		}
		if (size > list.size()) {
			GAMA.reportError(scope, GamaRuntimeException.warning("Problem with file " + getFile(scope) + ": only "
					+ list.size() + " of the " + size + " geometries could be added", scope), false);
		}
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		if (gis == null) {
			final SimpleFeatureCollection store = getFeatureCollection(scope);
			if (store == null)
				return new Envelope3D();
			final Envelope3D env = Envelope3D.of(store.getBounds());
			computeProjection(scope, env);
		}
		return gis.getProjectedEnvelope();

	}

}
