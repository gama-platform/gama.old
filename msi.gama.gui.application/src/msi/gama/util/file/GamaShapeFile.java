/**
 * 
 */
package msi.gama.util.file;

import msi.gama.environment.GisUtil;
import msi.gama.interfaces.IScope;
import msi.gama.internal.types.GamaFileType;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Files;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

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
		if ( !GamaFileType.isShape(file.getName()) ) { throw new GamaRuntimeException(
			"The extension " + this.getExtension() + " is not recognized for ESRI shapefiles"); }
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_copy()
	 */
	@Override
	protected IGamaFile _copy() {
		// TODO ? Will require to do a copy of the file. But how to get the new name ? Or maybe just
		// as something usable like
		// let f type: file value: write(copy(f2))
		return null;
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_isFixedLength()
	 */
	@Override
	protected boolean _isFixedLength() {
		return false;
	}

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
	protected void fillBuffer() throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();
		FeatureIterator<SimpleFeature> features = GisUtil.getFeatureIterator(file);
		if ( features == null ) { return; }
		while (features.hasNext()) {
			SimpleFeature feature = features.next();
			buffer.add(new GamaGisGeometry(feature), null);
		}
		features.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Regarder ce qu'il y a dans la commande "save" pour sauvegarder les fichiers.

	}

}
