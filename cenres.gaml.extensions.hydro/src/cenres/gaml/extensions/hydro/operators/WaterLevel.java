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
 * - Benoit Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package cenres.gaml.extensions.hydro.operators;

import cenres.gaml.extensions.hydro.utils.WaterLevelUtils;

import com.vividsolutions.jts.geom.Coordinate;


import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.util.GamaList;

public class WaterLevel {

	/*
	 * author: Philippe Caillou
	 */
	@operator(value = { "water_level_for" })
	public static Double opWaterLevel(final IShape shape, final Double val) {
		if ( shape == null || val == null) { return null; }
		return WaterLevelUtils.heigth(new GamaList<Coordinate>(shape.getInnerGeometry().getCoordinates()), val);
	}
}
