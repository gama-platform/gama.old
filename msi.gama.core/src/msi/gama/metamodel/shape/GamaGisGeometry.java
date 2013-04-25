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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.shape;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
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

	public GamaGisGeometry(IScope scope, Geometry g, final SimpleFeature feature) {
		super(scope.getSimulationScope().getGisUtils().transform(g));
		if ( feature != null ) {
			// We filter out the geometries (already loaded before)
			for ( Property p : feature.getProperties() ) {
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
	public void setAgent(IAgent a) {
		super.setAgent(a);
		a.setExtraAttributes(attributes);
	}

}