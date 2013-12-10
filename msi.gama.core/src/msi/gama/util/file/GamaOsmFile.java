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

import java.io.File;
import java.util.*;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Files;
import msi.gaml.types.GamaFileType;
import org.geotools.feature.FeatureIterator;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul
 * Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
public class GamaOsmFile extends GamaFile<Integer, GamaGisGeometry> {

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	public GamaOsmFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isOsm(getFile().getName()) ) { throw GamaRuntimeException.error("The extension " +
			this.getExtension() + " is not recognized for Open Street Map Files"); }
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
		return Files.OSM;
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		// TO DO
	}

	public FeatureIterator<SimpleFeature> getFeatureIterator(final IScope scope) {
		// TO DO
		return null;
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
	public Envelope computeEnvelope(final IScope scope) {
		final File osmFile = getFile();
		Envelope env = null;
		try {
			SAXBuilder sxb = new SAXBuilder();
			try {
				Document document = sxb.build(osmFile);
				List<Element> listBounds = document.getRootElement().getChildren("bounds");
				Iterator i = listBounds.iterator();
				while (i.hasNext()) {
					Element courant = (Element) i.next();
					double minlat = Double.valueOf(courant.getAttributeValue("minlat"));
					double minlon = Double.valueOf(courant.getAttributeValue("minlon"));
					double maxlat = Double.valueOf(courant.getAttributeValue("maxlat"));
					double maxlon = Double.valueOf(courant.getAttributeValue("maxlon"));
					env = new Envelope(minlon, maxlon, minlat, maxlat);
					break;
				}

			} catch (Exception e) {}

			if ( env != null ) {
				final double latitude = env.centre().y;
				final double longitude = env.centre().x;
				final GisUtils gis = scope.getTopology().getGisUtils();
				gis.setInitialCRS(longitude, latitude);
				env = gis.transform(env);
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e);
		}
		return env;

	}

}
