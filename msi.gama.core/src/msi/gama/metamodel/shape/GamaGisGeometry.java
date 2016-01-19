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

import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.operators.Spatial;

import org.opengis.feature.*;
import org.opengis.feature.type.GeometryType;
import com.vividsolutions.jts.geom.Geometry;

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

	/**
	 * In case this geometry is loaded and then later attributed to an agent.
	 * @see msi.gama.metamodel.shape.GamaShape#setAgent(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void setAgent(final IAgent a) {
		super.setAgent(a);
		// a.setExtraAttributes(attributes);
		// attributes = null;
	}

}