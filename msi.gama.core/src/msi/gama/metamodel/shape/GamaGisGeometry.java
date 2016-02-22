/*********************************************************************************************
 *
 *
 * 'GamaGisGeometry.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.shape;

import org.opengis.feature.*;
import org.opengis.feature.type.GeometryType;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.common.util.GeometryUtils;

/**
 *
 * The class GamaGisGeometry. A subclass of GamaGeometry that maintains a link with the underlying
 * GIS feature attributes
 *
 * @author drogoul
 * @since 30 nov. 2011
 *
 */
public class GamaGisGeometry extends GamaShape {

	public GamaGisGeometry(final Geometry g, final Feature feature) {
		super(GeometryUtils.isClockWise(g) ? g : GeometryUtils.changeClockWise(g));
		if ( feature != null ) {
			// We filter out the geometries (already loaded before)
			for ( final Property p : feature.getProperties() ) {
				if ( !(p.getType() instanceof GeometryType) ) {
					setAttribute(p.getName().getLocalPart(), p.getValue());
				}
			}
		}
	}

}