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
import java.net.MalformedURLException;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.types.IType;
import org.geotools.data.shapefile.*;
import org.geotools.feature.*;
import org.opengis.feature.simple.*;
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
		setBuffer(new GamaList());
		getFeatureIterator(scope, true);
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
		File file = getFile();
		ShapefileDataStore store = null;
		try {
			store = new ShapefileDataStore(file.toURI().toURL());
			try {
				store.getSchema();
			} catch (IOException e) {
				return null;
			}
			ShapefileFileResourceInfo info = new ShapefileFileResourceInfo(store);
			return info.getCRS();

		} catch (MalformedURLException e) {
			return null;
		} finally {
			if ( store != null ) {
				store.dispose();
			}
		}
		// TODO Should we dispose the store ?
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
			ShapefileFileResourceInfo info = new ShapefileFileResourceInfo(store);
			Envelope env = info.getBounds();
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
			getFeatureIterator(scope, false);
		}
		return gis.getProjectedEnvelope();

	}
}
