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

import java.io.*;
import java.net.URL;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.*;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.*;
import org.geotools.feature.*;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.*;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul
 * Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
@file(name = "shape",
	extensions = { "shp" },
	buffer_type = IType.LIST,
	buffer_content = IType.GEOMETRY,
	buffer_index = IType.INT)
public class GamaShapeFile extends GamaGisFile {

	public static class ShapeInfo extends GamaFileMetaData {

		final int itemNumber;
		final CoordinateReferenceSystem crs;
		final double width;
		final double height;

		public ShapeInfo(final URL url, final long modificationStamp) {
			super(modificationStamp);
			ShapefileDataStore store = null;
			ReferencedEnvelope env = new ReferencedEnvelope();
			CoordinateReferenceSystem crs = null;
			int number = 0;
			try {
				store = new ShapefileDataStore(url);
				SimpleFeatureSource source = store.getFeatureSource();
				SimpleFeatureCollection features = source.getFeatures();
				try {
					crs = source.getInfo().getCRS();
				} catch (Exception e) {}
				env = source.getBounds();
				if ( crs != null ) {
					try {
						env = env.transform(new ProjectionFactory().getTargetCRS(), true);
					} catch (Exception e) {}
				}
				number = features.size();
			} catch (Exception e) {
				System.out.println("Error in reading metadata of " + url);

			} finally {
				width = env.getWidth();
				height = env.getHeight();
				itemNumber = number;
				this.crs = crs;
				if ( store != null ) {
					store.dispose();
				}
			}

		}

		public ShapeInfo(final String propertiesString) throws NoSuchAuthorityCodeException, FactoryException {
			super(propertiesString);
			String[] segments = split(propertiesString);
			itemNumber = Integer.valueOf(segments[1]);
			String crsString = segments[2];
			if ( "null".equals(crsString) ) {
				crs = null;
			} else {
				crs = CRS.parseWKT(crsString);
			}
			width = Double.valueOf(segments[3]);
			height = Double.valueOf(segments[4]);
		}

		/**
		 * Method getSuffix()
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			String CRS = crs == null ? "No CRS" : crs.getName().getCode();
			return " (" + itemNumber + " objects | " + CRS + " | " + Math.round(width) + "m x " + Math.round(height) +
				"m)";
		}

		@Override
		public String toPropertyString() {
			Object[] toSave =
				new Object[] { super.toPropertyString(), itemNumber, crs == null ? "null" : crs.toWKT(), width, height };
			return StringUtils.join(toSave, DELIMITER);
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
		if ( getBuffer() != null ) { return; }
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readShapes(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Regarder ce qu'il y a dans la commande "save" pour sauvegarder les fichiers.
		// Merger progressivement save et le syst�me de fichiers afin de ne plus d�pendre de �a.

	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS() {
		ShapefileDataStore store = null;
		try {
			store = new ShapefileDataStore(getFile().toURI().toURL());
			return store.getFeatureSource().getInfo().getCRS();
		} catch (IOException e) {
			return null;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
	}

	protected void readShapes(final IScope scope) {
		GuiUtils.beginSubStatus("Reading file" + getName());
		ShapefileDataStore store = null;
		FeatureReader reader = null;
		File file = getFile();
		IList list = getBuffer();
		try {
			store = new ShapefileDataStore(file.toURI().toURL());
			Envelope env = store.getFeatureSource().getBounds();
			int size = store.getFeatureSource().getCount(Query.ALL);
			int index = 0;
			computeProjection(scope, env);
			reader = store.getFeatureReader();
			while (reader.hasNext()) {
				GuiUtils.updateSubStatusCompletion(index++ / size);
				Feature feature = reader.next();
				Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
				if ( g != null && !g.isEmpty() /* Fix for Issue 725 && 677 */) {
					g = gis.transform(g);
					list.add(new GamaGisGeometry(g, feature));
				} else {
					// See Issue 725
					GAMA.reportError(
						scope,
						GamaRuntimeException.warning("GamaShapeFile.fillBuffer; geometry could not be added : " +
							feature.getIdentifier(), scope), false);
				}
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if ( store != null ) {
				store.dispose();
			}
			GuiUtils.endSubStatus("Reading file " + getName());
		}
	}

	public void getFeatureIterator(final IScope scope, final boolean returnIt) {
		File file = getFile();
		ShapefileDataStore store = null;
		FeatureIterator<SimpleFeature> it = null;
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
		try {
			GuiUtils.beginSubStatus((returnIt ? "Reading file" : "Measuring file ") + getName());
			store = new ShapefileDataStore(file.toURI().toURL());
			features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
			Envelope env = store.getFeatureSource().getBounds();
			// ShapefileFileResourceInfo info = new ShapefileFileResourceInfo(store);
			// Envelope env = info.getBounds();
			computeProjection(scope, env);
			if ( features != null && returnIt ) {
				double size = features.size();
				it = features.features();
				// return returnIt ? features.features() : null;
				int i = 0;
				while (it.hasNext()) {
					GuiUtils.updateSubStatusCompletion(i++ / size);
					final SimpleFeature feature = it.next();
					Geometry g = (Geometry) feature.getDefaultGeometry();
					if ( g != null && !g.isEmpty() /* Fix for Issue 725 */) {
						// Fix for Issue 677
						g = gis.transform(g);
						((IList) getBuffer()).add(new GamaGisGeometry(g, feature));
					} else {
						// See Issue 725
						GAMA.reportError(
							scope,
							GamaRuntimeException.warning("GamaShapeFile.fillBuffer; geometry could not be added : " +
								feature.getID(), scope), false);
					}
				}
			} else {
				// return null;
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if ( it != null ) {
				it.close();
			}
			if ( store != null ) {
				store.dispose();
			}
			GuiUtils.endSubStatus("Opening file " + getName());
		}
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		if ( gis == null ) {
			ShapefileDataStore store = null;
			try {
				store = new ShapefileDataStore(getFile().toURI().toURL());
				Envelope env = store.getFeatureSource().getBounds();
				computeProjection(scope, env);
			} catch (IOException e) {
				return new Envelope();
			} finally {
				if ( store != null ) {
					store.dispose();
				}
			}
		}
		return gis.getProjectedEnvelope();

	}
}
