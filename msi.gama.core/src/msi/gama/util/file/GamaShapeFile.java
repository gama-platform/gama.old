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
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Files;
import msi.gaml.types.GamaFileType;
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
public class GamaShapeFile extends GamaFile<Integer, GamaGisGeometry> {

	Integer initialCRSCode = null;
	Envelope env = null;

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaShapeFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaShapeFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName);
		initialCRSCode = code;
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isShape(getFile().getName()) ) { throw GamaRuntimeException.error("The extension " +
			this.getExtension() + " is not recognized for ESRI shapefiles"); }
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_copy()
	 */
	@Override
	protected IGamaFile _copy(final IScope scope) {
		// TODO ? Will require to do a copy of the file. But how to get the new name ? Or maybe just
		// as something usable like
		// let f type: file value: write(copy(f2))
		return null;
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_isFixedLength()
	 */
	// @Override
	// protected boolean _isFixedLength() {
	// return false;
	// }

	/**
	 * @see msi.gama.util.GamaFile#_toGaml()
	 */
	@Override
	public String getKeyword() {
		return Files.SHAPE;
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();
		getFeatureIterator(scope, true);
		// if ( features == null ) { return; }
		// while (features.hasNext()) {
		// final SimpleFeature feature = features.next();
		// Geometry g = (Geometry) feature.getDefaultGeometry();
		// if ( g != null && !g.isEmpty() /* Fix for Issue 725 */) {
		// // Fix for Issue 677
		// ((IList) buffer).add(new GamaGisGeometry(scope, g, feature));
		// } else {
		// // See Issue 725
		// GAMA.reportError(
		// GamaRuntimeException.warning("GamaShapeFile.fillBuffer; geometry could not be added : " +
		// feature.getID()), false);
		// }
		// }
		// features.close();
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

	public void getFeatureIterator(final IScope scope, final boolean returnIt) {
		File file = getFile();
		ShapefileDataStore store = null;
		FeatureIterator<SimpleFeature> it = null;
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
		try {
			// store = new ShapefileDataStore(file.toURI().toURL());
			// final String name = store.getTypeNames()[0];
			// final FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
			// final FeatureCollection<SimpleFeatureType, SimpleFeature> featureShp = source.getFeatures();
			// env = featureShp.getBounds();
			store = new ShapefileDataStore(file.toURI().toURL());
			features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
			ShapefileFileResourceInfo info = new ShapefileFileResourceInfo(store);
			CoordinateReferenceSystem prj = info.getCRS();
			env = info.getBounds();
			final double latitude = env.centre().y;
			final double longitude = env.centre().x;
			GisUtils gis = scope.getTopology().getGisUtils();
			// If we have a forced EPSG code for the initial CRS, we use it (even if the .prj file is present).
			if ( initialCRSCode != null ) {
				gis.setInitialCRS(initialCRSCode, true, longitude, latitude);
			} else if ( prj != null ) {
				// Otherwise, if a .prj file is present, we use it
				// ShpFiles shpFiles = new ShpFiles(file);
				// try {
				gis.setInitialCRS(prj, longitude, latitude);
				// } finally {
				// shpFiles.dispose();
				// }
			} else {
				// If the user does not consider the data to be projected, he has entered a default value in the
				// preferences
				if ( !GamaPreferences.LIB_PROJECTED.getValue() ) {
					gis.setInitialCRS(GamaPreferences.LIB_INITIAL_CRS.getValue(), true, longitude, latitude);
				} else {
					// gis.setInitialCRS(longitude, latitude);
				}
			}
			env = gis.transform(env);

			if ( features != null && returnIt ) {
				it = features.features();
				// return returnIt ? features.features() : null;
				while (it.hasNext()) {
					final SimpleFeature feature = it.next();
					Geometry g = (Geometry) feature.getDefaultGeometry();
					if ( g != null && !g.isEmpty() /* Fix for Issue 725 */) {
						// Fix for Issue 677
						((IList) buffer).add(new GamaGisGeometry(scope, g, feature));
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
		if ( env == null ) {

			getFeatureIterator(scope, false);
		}
		return env;

	}
}
