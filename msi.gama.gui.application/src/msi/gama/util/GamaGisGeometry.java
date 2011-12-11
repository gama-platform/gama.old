/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import msi.gama.environment.GisUtil;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * The class GamaGisGeometry. A subclass of GamaGeometry able to store the value of the
 * attributes.
 * 
 * @author drogoul
 * @since 30 nov. 2011
 * 
 */
public class GamaGisGeometry extends GamaGeometry {

	public GamaGisGeometry(final SimpleFeature feature) {
		Geometry geom = (Geometry) feature.getDefaultGeometry();
		if ( GisUtil.transformCRS != null ) {
			try {
				geom = JTS.transform(geom, GisUtil.transformCRS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setInnerGeometry(GisUtil.fromGISToAbsolute(geom));
		for ( Property p : feature.getProperties() ) {
			setAttribute(p.getName().getLocalPart(), p.getValue());
		}
	}

	GamaMap gisAttributes = new GamaMap();

	public void setAttribute(final String s, final Object o) {
		gisAttributes.put(s, o);
	}

	public Object getAttribute(final String s) {
		return gisAttributes.get(s);
	}
}