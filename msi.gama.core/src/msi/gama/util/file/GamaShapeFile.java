/*******************************************************************************************************
 *
 * GamaShapeFile.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import static msi.gama.runtime.GAMA.reportError;
import static msi.gama.runtime.exceptions.GamaRuntimeException.create;
import static msi.gama.runtime.exceptions.GamaRuntimeException.warning;
import static org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import msi.gama.common.geometry.GamaGeometryFactory;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file (
		name = "shape",
		extensions = { "shp", "SHP" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.SHAPEFILE, IConcept.FILE },
		doc = @doc ("Represents a shape file as defined by the ESRI standard. See https://en.wikipedia.org/wiki/Shapefile for more information."))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaShapeFile extends GamaGisFile {

	static {
		DEBUG.ON();
	}

	// FileDataStore store;

	/**
	 * The Class ShapeInfo.
	 */
	public static class ShapeInfo extends GamaFileMetaData {

		/** The item number. */
		final int itemNumber;

		/** The crs. */
		final CoordinateReferenceSystem crs;

		/** The width. */
		final double width;

		/** The height. */
		final double height;

		/** The attributes. */
		final Map<String, String> attributes = new LinkedHashMap();

		/**
		 * Instantiates a new shape info.
		 *
		 * @param scope
		 *            the scope
		 * @param url
		 *            the url
		 * @param modificationStamp
		 *            the modification stamp
		 */
		public ShapeInfo(final IScope scope, final URL url, final long modificationStamp) {
			super(modificationStamp);
			FileDataStore store = null;
			ReferencedEnvelope env = new ReferencedEnvelope();
			CoordinateReferenceSystem crs1 = null;
			int number = 0;
			try {
				store = getDataStore(url);

				final SimpleFeatureSource source = store.getFeatureSource();
				final SimpleFeatureCollection features = source.getFeatures();
				try {
					crs1 = source.getInfo().getCRS();

				} catch (final Exception e) {
					DEBUG.ERR("Ignored exception in ShapeInfo getCRS:" + e.getMessage());
				}
				env = source.getBounds();
				if (crs1 == null) {
					crs1 = ProjectionFactory.manageGoogleCRS(url);
					if (crs1 != null) { env = new ReferencedEnvelope(env, crs1); }
				}

				if (crs1 != null) {
					try {
						env = env.transform(new ProjectionFactory().getTargetCRS(scope), true);
					} catch (final Exception e) {
						store.dispose();
						throw e;
					}
				}
				try {
					number = features.size();
				} catch (final Exception e) {

					store.dispose();
					DEBUG.ERR("Error in loading shapefile: " + e.getMessage());
				}
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
			} catch (final Exception e) {
				DEBUG.ERR("Error in reading metadata of " + url);
				e.printStackTrace();

			} finally {
				width = env.getWidth();
				height = env.getHeight();
				itemNumber = number;
				this.crs = crs1;
				if (store != null) { store.dispose(); }
			}

		}

		/**
		 * Gets the crs.
		 *
		 * @return the crs
		 */
		public CoordinateReferenceSystem getCRS() { return crs; }

		/**
		 * Instantiates a new shape info.
		 *
		 * @param propertiesString
		 *            the properties string
		 */
		public ShapeInfo(final String propertiesString) {
			super(propertiesString);
			final String[] segments = split(propertiesString);
			itemNumber = Integer.parseInt(segments[1]);
			final String crsString = segments[2];
			CoordinateReferenceSystem theCRS;
			if ("null".equals(crsString) || crsString.startsWith("Unknown")) {
				theCRS = null;
			} else {
				try {
					theCRS = CRS.parseWKT(crsString);
				} catch (final Exception e) {
					theCRS = null;
				}
			}
			crs = theCRS;
			width = Double.parseDouble(segments[3]);
			height = Double.parseDouble(segments[4]);
			if (segments.length > 5) {
				final String[] names = splitByWholeSeparatorPreserveAllTokens(segments[5], SUB_DELIMITER);
				final String[] types = splitByWholeSeparatorPreserveAllTokens(segments[6], SUB_DELIMITER);
				for (int i = 0; i < names.length; i++) { attributes.put(names[i], types[i]); }
			}
		}

		/**
		 * Method getSuffix()
		 *
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			final StringBuilder sb = new StringBuilder();
			appendSuffix(sb);
			return sb.toString();
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			sb.append(itemNumber).append(" object");
			if (itemNumber > 1) { sb.append("s"); }
			sb.append(SUFFIX_DEL);
			sb.append(crs == null ? "Unknown CRS" : crs.getName().getCode());
			sb.append(SUFFIX_DEL);
			sb.append(Math.round(width)).append("m x ");
			sb.append(Math.round(height)).append("m");
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Shapefile").append(Strings.LN);
			sb.append(itemNumber).append(" objects").append(Strings.LN);
			sb.append("Dimensions: ").append(Math.round(width) + "m x " + Math.round(height) + "m").append(Strings.LN);
			sb.append("Coordinate Reference System: ").append(crs == null ? "Unknown CRS" : crs.getName().getCode())
					.append(Strings.LN);
			if (!attributes.isEmpty()) {
				sb.append("Attributes: ").append(Strings.LN);
				attributes.forEach((k, v) -> sb.append("<li>").append(k).append(" (" + v + ")").append("</li>"));
			}
			return sb.toString();
		}

		/**
		 * Gets the attributes.
		 *
		 * @return the attributes
		 */
		public Map<String, String> getAttributes() { return attributes; }

		@Override
		public String toPropertyString() {
			// See Issue #1603: .toWKT() && pa can sometimes cause problem with
			// certain projections.
			String system = crs == null ? "Unknown projection" : crs.toWKT();
			try {
				CRS.parseWKT(system);
			} catch (final Exception e) {
				// The toWKT()/parseWKT() pair has a problem
				String srs = CRS.toSRS(crs);
				if (srs == null && crs != null) { srs = crs.getName().getCode(); }
				system = "Unknown projection " + srs;

			}
			final String attributeNames = String.join(SUB_DELIMITER, attributes.keySet());
			final String types = String.join(SUB_DELIMITER, attributes.values());
			final String[] toSave = { super.toPropertyString(), String.valueOf(itemNumber), system,
					String.valueOf(width), String.valueOf(height), attributeNames, types };
			return String.join(DELIMITER, toSave);
		}
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	@doc (
			value = "This file constructor allows to read a shapefile (.shp) file",
			examples = { @example (
					value = "file f <- shape_file(\"file.shp\");",
					isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	/**
	 * Instantiates a new gama shape file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param code
	 *            the code
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code, as an int (epsg code)",
			examples = { @example (
					value = "file f <- shape_file(\"file.shp\", \"32648\");",
					isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	/**
	 * Instantiates a new gama shape file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param code
	 *            the code
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code (epg,...,), as a string",
			examples = { @example (
					value = "file f <- shape_file(\"file.shp\", \"EPSG:32648\");",
					isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	/**
	 * Instantiates a new gama shape file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param with3D
	 *            the with 3 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a shapefile (.shp) file and take a potential z value (not taken in account by default)",
			examples = { @example (
					value = "file f <- shape_file(\"file.shp\", true);",
					isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final boolean with3D) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null, with3D);
	}

	/**
	 * Instantiates a new gama shape file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param code
	 *            the code
	 * @param with3D
	 *            the with 3 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code, as an int (epsg code) and take a potential z value (not taken in account by default)",
			examples = { @example (
					value = "file f <- shape_file(\"file.shp\", \"32648\", true);",
					isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final Integer code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	/**
	 * Instantiates a new gama shape file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param code
	 *            the code
	 * @param with3D
	 *            the with 3 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a shapefile (.shp) file and specifying the coordinates system code (epg,...,), as a string and take a potential z value (not taken in account by default)",
			examples = { @example (
					value = "file f <- shape_file(\"file.shp\", \"EPSG:32648\",true);",
					isExecutable = false) })
	public GamaShapeFile(final IScope scope, final String pathName, final String code, final boolean with3D)
			throws GamaRuntimeException {
		super(scope, pathName, code, with3D);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		ShapeInfo s;
		final IFileMetaDataProvider p = scope.getGui().getMetaDataProvider();
		if (p != null) {
			s = (ShapeInfo) p.getMetaData(getFile(scope), false, true);
		} else {
			try {
				s = new ShapeInfo(scope, getFile(scope).toURI().toURL(), 0);
			} catch (final MalformedURLException e) {
				return GamaListFactory.EMPTY_LIST;
			}
		}
		return GamaListFactory.wrap(Types.STRING, s.attributes.keySet());
	}

	/**
	 * Gets the data store.
	 *
	 * @param url
	 *            the url
	 * @return the data store
	 */
	static FileDataStore getDataStore(final URL url) {
		FileDataStore fds;
		try {
			fds = FileDataStoreFinder.getDataStore(url);
		} catch (IOException e) {
			return null;
		}
		if (fds instanceof ShapefileDataStore store) {
			store.setGeometryFactory(GeometryUtils.GEOMETRY_FACTORY);
			store.setMemoryMapped(GamaPreferences.External.SHAPEFILES_IN_MEMORY.getValue());
			store.setCharset(Charset.forName("UTF8"));
		}
		return fds;

	}

	@Override
	protected final void readShapes(final IScope scope) {
		ProgressCounter counter = new ProgressCounter(scope, "Reading " + getName(scope));
		SimpleFeatureCollection collection = getFeatureCollection(scope);
		computeEnvelope(scope);
		int[] indexOfGeometry = { 0 };
		try {
			collection.accepts(feature -> {
				Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
				// DEBUG.OUT(
				// "Geometry #" + indexOfGeometry[0] + " / " + feature.getIdentifier() + " is "
				// + (g == null ? "nil" : g.isEmpty() ? "empty" : !g.isValid() ? "invalid" : "valid"),
				// true);

				if (g != null && !g.isEmpty() /* Fix for Issue 725 && 677 */ ) {
					if (!with3D && g.getNumPoints() > 2) {
						try {
							if (!g.isValid()) { g = GeometryUtils.cleanGeometry(g); }
						} catch (Exception e) {
							g = GeometryUtils.cleanGeometry(g);
						}
					}
					g = gis.transform(g);
					if (!with3D) {
						g.apply(ZERO_Z);
						g.geometryChanged();
					}
					g = multiPolygonManagement(g);
					GamaShape gt = new GamaGisGeometry(g, feature);
					if (gt.getInnerGeometry() != null) { getBuffer().add(gt); }
				} else if (g == null) {
					// See Issue 725
					reportError(scope, warning("geometry #" + indexOfGeometry[0]
							+ " could not be added as it is nil (identifier: " + feature.getIdentifier() + ")", scope),
							false);
				}
				indexOfGeometry[0]++;
			}, counter);
		} catch (final Exception ex) {
			try {
				getBuffer().clear();
				indexOfGeometry[0] = 0;
				ShpFiles shp = new ShpFiles(getFile(scope).toURI().toURL());
				try (ShapefileReader reader = new ShapefileReader(shp, false, false, GeometryUtils.GEOMETRY_FACTORY)) {
					reader.setFlatGeometry(true);
					// System.out.println("count:" + reader.getCount(0));
					while (reader.hasNext()) {
						Record record = reader.nextRecord();
						Geometry g = GeometryUtils.cleanGeometry((Geometry) record.shape());

						if (g != null && !g.isEmpty() /* Fix for Issue 725 && 677 */ ) {

							if (!with3D && g.getNumPoints() > 2) {
								try {
									if (!g.isValid()) { g = GeometryUtils.cleanGeometry(g); }
								} catch (Exception e) {
									g = GeometryUtils.cleanGeometry(g);
								}
							}
							g = gis.transform(g);
							if (!with3D) {
								g.apply(ZERO_Z);
								g.geometryChanged();
							}
							g = multiPolygonManagement(g);

							for (int i = 0; i < g.getNumGeometries(); i++) {
								GamaShape gt = new GamaGisGeometry(g.getGeometryN(i), null);
								if (gt.getInnerGeometry() != null) { getBuffer().add(gt); }
							}

						} else if (g == null) {
							// See Issue 725
							reportError(scope,
									warning("geometry #" + indexOfGeometry[0] + " could not be added as it is nil",
											scope),
									false);
						}
						indexOfGeometry[0]++;
					}
				}
			} catch (final IOException e2) {
				throw create(e2, scope);
			}
		}

	}

	@Override
	protected SimpleFeatureCollection getFeatureCollection(final IScope scope) {
		try {

			// if (store == null) { store = getDataStoreOld(getFile(scope).toURI().toURL()); }
			final SimpleFeatureSource source = getDataStore(getFile(scope).toURI().toURL()).getFeatureSource();
			// AD See Issue #3094. This constitutes a workaround
			Query query = new Query();
			// if (!with3D) { query.setHints(new Hints(Hints.FEATURE_2D, true)); }
			query.getHints().put(Hints.JTS_COORDINATE_SEQUENCE_FACTORY, GamaGeometryFactory.COORDINATES_FACTORY);
			query.getHints().put(Hints.JTS_GEOMETRY_FACTORY, GeometryUtils.GEOMETRY_FACTORY);
			// AD
			SimpleFeatureCollection collection = source.getFeatures(query);
			if (source.getDataStore() != null) { source.getDataStore().dispose(); }
			return collection;

		} catch (IOException e) {
			throw create(e, scope);
		}
	}

	@Override
	public int length(final IScope scope) {
		// This line deactivated because of issue #3525
		// if (getBuffer() == null) return getFeatureCollection(scope).size();
		return super.length(scope);
	}

}
