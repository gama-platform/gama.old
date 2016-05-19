/*********************************************************************************************
 *
 *
 * 'GamaShapeFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.splitByWholeSeparatorPreserveAllTokens;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file(name = "shape", extensions = {
		"shp" }, buffer_type = IType.LIST, buffer_content = IType.GEOMETRY, buffer_index = IType.INT, concept = {
				IConcept.SHAPEFILE, IConcept.FILE })
public class GamaShapeFile extends GamaGisFile {

	public static class ShapeInfo extends GamaFileMetaData {

		final int itemNumber;
		final CoordinateReferenceSystem crs;
		final double width;
		final double height;
		final Map<String, String> attributes = new LinkedHashMap();

		public ShapeInfo(final URL url, final long modificationStamp) {
			super(modificationStamp);
			ShapefileDataStore store = null;
			ReferencedEnvelope env = new ReferencedEnvelope();
			CoordinateReferenceSystem crs1 = null;
			int number = 0;
			try {
				store = new ShapefileDataStore(url);
				final SimpleFeatureSource source = store.getFeatureSource();
				final SimpleFeatureCollection features = source.getFeatures();
				try {
					crs1 = source.getInfo().getCRS();
				} catch (final Exception e) {
					System.out.println("Ignored exception in ShapeInfo getCRS:" + e.getMessage());
				}
				env = source.getBounds();
				if (crs1 != null) {
					try {
						env = env.transform(new ProjectionFactory().getTargetCRS(), true);
					} catch (final Exception e) {
						throw e;
					}
				}
				number = features.size();
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
				System.out.println("Error in reading metadata of " + url);
				e.printStackTrace();

			} finally {
				width = env.getWidth();
				height = env.getHeight();
				itemNumber = number;
				this.crs = crs1;
				if (store != null) {
					store.dispose();
				}
			}

		}

		public CoordinateReferenceSystem getCRS() {
			return crs;
		}

		public ShapeInfo(final String propertiesString) {
			super(propertiesString);
			final String[] segments = split(propertiesString);
			itemNumber = Integer.valueOf(segments[1]);
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
			width = Double.valueOf(segments[3]);
			height = Double.valueOf(segments[4]);
			if (segments.length > 5) {
				final String[] names = splitByWholeSeparatorPreserveAllTokens(segments[5], SUB_DELIMITER);
				final String[] types = splitByWholeSeparatorPreserveAllTokens(segments[6], SUB_DELIMITER);
				for (int i = 0; i < names.length; i++) {
					attributes.put(names[i], types[i]);
				}
			}
		}

		/**
		 * Method getSuffix()
		 * 
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			final String CRS = crs == null ? "Unknown CRS" : crs.getName().getCode();
			return "" + itemNumber + " objects | " + CRS + " | " + FastMath.round(width) + "m x "
					+ FastMath.round(height) + "m";
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Shapefile").append(Strings.LN);
			sb.append(itemNumber).append(" objects").append(Strings.LN);
			sb.append("Dimensions: ").append(FastMath.round(width) + "m x " + FastMath.round(height) + "m")
					.append(Strings.LN);
			sb.append("Coordinate Reference System: ").append(crs == null ? "Unknown CRS" : crs.getName().getCode())
					.append(Strings.LN);
			if (!attributes.isEmpty()) {
				sb.append("Attributes: ").append(Strings.LN);
				for (final Map.Entry<String, String> entry : attributes.entrySet()) {
					sb.append("<li>").append(entry.getKey()).append(" (" + entry.getValue() + ")").append("</li>");
				}
			}
			return sb.toString();
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		@Override
		public String toPropertyString() {
			// See Issue #1603: .toWKT() && pa can sometimes cause problem with
			// certain projections.
			String system = crs == null ? "Unknown projection" : crs.toWKT();
			try {
				final CoordinateReferenceSystem trial = CRS.parseWKT(system);
			} catch (final Exception e) {
				// The toWKT()/parseWKT() pair has a problem
				String srs = CRS.toSRS(crs);
				if (srs == null && crs != null) {
					srs = crs.getName().getCode();
				}
				system = "Unknown projection " + srs;

			}
			final String attributeNames = join(attributes.keySet(), SUB_DELIMITER);
			final String types = join(attributes.values(), SUB_DELIMITER);
			final Object[] toSave = new Object[] { super.toPropertyString(), itemNumber, system, width, height,
					attributeNames, types };
			return join(toSave, DELIMITER);
		}
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaShapeFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	public GamaShapeFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	public GamaShapeFile(final IScope scope, final String pathName, final String code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readShapes(scope);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		ShapeInfo s;
		final IFileMetaDataProvider p = scope.getGui().getMetaDataProvider();
		if (p != null) {
			s = (ShapeInfo) p.getMetaData(getFile(), false, true);
		} else {
			try {
				s = new ShapeInfo(getFile().toURI().toURL(), 0);
			} catch (final MalformedURLException e) {
				return GamaListFactory.create();
			}
		}
		return GamaListFactory.createWithoutCasting(Types.STRING, s.attributes.keySet());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Regarder ce qu'il y a dans la commande "save" pour sauvegarder
		// les fichiers.
		// Merger progressivement save et le syst�me de fichiers afin de ne plus
		// d�pendre de �a.

	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS() {
		ShapefileDataStore store = null;
		try {
			store = new ShapefileDataStore(getFile().toURI().toURL());
			return store.getFeatureSource().getInfo().getCRS();
		} catch (final IOException e) {
			return null;
		} finally {
			if (store != null) {
				store.dispose();
			}
		}
	}

	protected void readShapes(final IScope scope) {
		scope.getGui().beginSubStatus("Reading file" + getName());
		ShapefileDataStore store = null;
		FeatureReader reader = null;
		final File file = getFile();
		final IList list = getBuffer();
		int size = 0;
		try {
			store = new ShapefileDataStore(file.toURI().toURL());
			final Envelope env = store.getFeatureSource().getBounds();
			size = store.getFeatureSource().getCount(Query.ALL);
			int index = 0;
			computeProjection(scope, env);
			reader = store.getFeatureReader();

			while (reader.hasNext()) {
				scope.getGui().setSubStatusCompletion(index++ / size);
				final Feature feature = reader.next();
				Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
				if (g != null && !g.isEmpty() /* Fix for Issue 725 && 677 */ ) {
					g = gis.transform(g);
					list.add(new GamaGisGeometry(g, feature));
				} else {
					// See Issue 725
					GAMA.reportError(scope,
							GamaRuntimeException
									.warning(
											"GamaShapeFile.fillBuffer; geometry could not be added  as it is "
													+ (g == null ? "nil: " : "empty: ") + feature.getIdentifier(),
											scope),
							false);
				}
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			if (store != null) {
				store.dispose();
			}
			scope.getGui().endSubStatus("Reading file " + getName());
		}
		if (size > list.size()) {
			GAMA.reportError(scope, GamaRuntimeException.warning("Problem with file " + getFile() + ": only "
					+ list.size() + " of the " + size + " geometries could be added", scope), false);
		}
	}

	public void getFeatureIterator(final IScope scope, final boolean returnIt) {
		final File file = getFile();
		ShapefileDataStore store = null;
		FeatureIterator<SimpleFeature> it = null;
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
		try {
			scope.getGui().beginSubStatus((returnIt ? "Reading file" : "Measuring file ") + getName());
			store = new ShapefileDataStore(file.toURI().toURL());
			features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
			final Envelope env = store.getFeatureSource().getBounds();
			// ShapefileFileResourceInfo info = new
			// ShapefileFileResourceInfo(store);
			// Envelope env = info.getBounds();
			computeProjection(scope, env);
			if (features != null && returnIt) {
				final double size = features.size();
				it = features.features();
				// return returnIt ? features.features() : null;
				int i = 0;
				while (it.hasNext()) {
					scope.getGui().setSubStatusCompletion(i++ / size);
					final SimpleFeature feature = it.next();
					Geometry g = (Geometry) feature.getDefaultGeometry();
					if (g != null && !g.isEmpty() /* Fix for Issue 725 */ ) {
						// Fix for Issue 677
						g = gis.transform(g);
						((IList) getBuffer()).add(new GamaGisGeometry(g, feature));
					} else {
						// See Issue 725
						GAMA.reportError(scope,
								GamaRuntimeException
										.warning("GamaShapeFile.fillBuffer; geometry could not be added  as it is "
												+ (g == null ? "nil :" : "empty :") + feature.getID(), scope),
								false);
					}
				}
			} else {
				// return null;
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if (it != null) {
				it.close();
			}
			if (store != null) {
				store.dispose();
			}
			scope.getGui().endSubStatus("Opening file " + getName());
		}
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if (gis == null) {
			ShapefileDataStore store = null;
			try {
				store = new ShapefileDataStore(getFile().toURI().toURL());
				final Envelope env = store.getFeatureSource().getBounds();
				computeProjection(scope, env);
			} catch (final IOException e) {
				return new Envelope();
			} finally {
				if (store != null) {
					store.dispose();
				}
			}
		}
		return gis.getProjectedEnvelope();

	}

}
