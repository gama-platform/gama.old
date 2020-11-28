/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.GamaGisGeometry.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.GeometryType;

import org.locationtech.jts.geom.Geometry;

/**
 *
 * The class GamaGisGeometry. A subclass of GamaGeometry that maintains a link with the underlying GIS feature
 * attributes
 *
 * @author drogoul
 * @since 30 nov. 2011
 *
 */
public class GamaGisGeometry extends GamaShape {

	public GamaGisGeometry(final Geometry g, final Feature feature) {
		super(g);
		if (feature != null) {
			// We filter out the geometries (already loaded before)
			for (final Property p : feature.getProperties()) {
				if (!(p.getType() instanceof GeometryType)) {
					final String type = p.getDescriptor().getType().getBinding().getSimpleName();
					if ("String".equals(type)) {
						String val = (String) p.getValue();
						if (val != null && ((val.startsWith("'") && val.endsWith("'")) || (val.startsWith("\"") && val.endsWith("\""))))
							val = val.substring(1, val.length() - 1);
						setAttribute(p.getName().getLocalPart(), val);

					} else
						setAttribute(p.getName().getLocalPart(), p.getValue());
				}
			}
		}
	}

}