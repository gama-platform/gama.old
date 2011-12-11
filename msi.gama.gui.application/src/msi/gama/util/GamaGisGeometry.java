/**
 * Created by drogoul, 30 nov. 2011
 * 
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