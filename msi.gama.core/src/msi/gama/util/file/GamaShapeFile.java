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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import java.io.*;
import java.util.List;
import msi.gama.common.util.*;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Files;
import msi.gaml.types.GamaFileType;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.*;
import org.geotools.feature.*;
import org.opengis.feature.simple.*;
import org.opengis.feature.type.*;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul
 * Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
public class GamaShapeFile extends GamaFile<Integer, GamaGisGeometry> {

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaShapeFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
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
	protected IGamaFile _copy(IScope scope) {
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
	protected void fillBuffer(IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();
		FeatureIterator<SimpleFeature> features = getFeatureIterator(scope);
		if ( features == null ) { return; }
		while (features.hasNext()) {
			SimpleFeature feature = features.next();
			Geometry g = (Geometry) feature.getDefaultGeometry();
			if ( g != null ) {
				((IList) buffer).add(new GamaGisGeometry(scope, g, feature));
			}
		}
		features.close();
	}

	public FeatureIterator<SimpleFeature> getFeatureIterator(IScope scope) {
		File file = null;
		ShpFiles shpf = null;
		try {
			file = getFile();
			ShapefileDataStore store = new ShapefileDataStore(file.toURI().toURL());
			String name = store.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureShp = source.getFeatures();
			SimpleFeatureType type = store.getSchema();
			List<AttributeDescriptor> descriptors = type.getAttributeDescriptors();
			for ( AttributeDescriptor ad : descriptors ) {
				GuiUtils.debug("Type of attribute " + ad.getLocalName() + ": " +
					ad.getType().getBinding().getSimpleName() + "; is geometry? " +
					(ad.getType() instanceof GeometryType));
			}

			if ( store.getSchema().getCoordinateReferenceSystem() != null ) {
				shpf = new ShpFiles(file);
				double latitude = featureShp.getBounds().centre().x;
				double longitude = featureShp.getBounds().centre().y;
				scope.getTopology().getGisUtils().setTransformCRS(shpf, latitude, longitude);
			}
			return featureShp.features();
		} catch (Exception e) {
			return null;
		} finally {
			// clean ?
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Regarder ce qu'il y a dans la commande "save" pour sauvegarder les fichiers.
		// Merger progressivement save et le systéme de fichiers afin de ne plus dépendre de ça.

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		File shpFile = getFile();
		ShapefileDataStore store = null;
		Envelope env = null;
		try {
			store = new ShapefileDataStore(shpFile.toURI().toURL());
			String name = store.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
			env = source.getBounds();
			if ( store.getSchema().getCoordinateReferenceSystem() != null ) {
				ShpFiles shpf = new ShpFiles(shpFile);
				double latitude = env.centre().x;
				double longitude = env.centre().y;
				GisUtils gis = scope.getTopology().getGisUtils();
				gis.setTransformCRS(shpf, latitude, longitude);
				env = gis.transform(env);
			}
		} catch (IOException e) {
			throw GamaRuntimeException.create(e);
		}
		store.dispose();
		return env;

	}

}
