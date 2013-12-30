/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import java.io.*;
import java.net.MalformedURLException;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
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
@file(name = "shape", extensions = { "shp" })
public class GamaShapeFile extends GamaGisFile {

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaShapeFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, null);
	}

	public GamaShapeFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();
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
			store = new ShapefileDataStore(file.toURI().toURL());
			features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
			ShapefileFileResourceInfo info = new ShapefileFileResourceInfo(store);
			CoordinateReferenceSystem prj = info.getCRS();
			Envelope env = info.getBounds();
			computeProjection(scope, env);
			if ( features != null && returnIt ) {
				it = features.features();
				// return returnIt ? features.features() : null;
				while (it.hasNext()) {
					final SimpleFeature feature = it.next();
					Geometry g = (Geometry) feature.getDefaultGeometry();
					if ( g != null && !g.isEmpty() /* Fix for Issue 725 */) {
						// Fix for Issue 677
						g = gis.transform(g);
						((IList) buffer).add(new GamaGisGeometry(g, feature));
					} else {
						// See Issue 725
						GAMA.reportError(
							GamaRuntimeException.warning("GamaShapeFile.fillBuffer; geometry could not be added : " +
								feature.getID()), false);
					}
				}
			} else {
				// return null;
			}
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e);
		} finally {
			if ( it != null ) {
				it.close();
			}
			if ( store != null ) {
				store.dispose();
			}
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
