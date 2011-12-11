/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.environment;

import java.util.*;
import msi.gama.interfaces.IGeometry;

/**
 * Written by drogoul Modified on 8 mars 2011
 * 
 * @todo Description
 * 
 */
public class GridMooreNeighbourhood extends GridNeighbourhood {

	public GridMooreNeighbourhood(final IGeometry[] agents, final int xSize, final int ySize/*
																							 * ,
																							 * final
																							 * boolean
																							 * isTorus
																							 */) {
		super(agents, xSize, ySize/* , isTorus */);
	}

	@Override
	protected List<Integer> getNeighboursAtRadius(final int placeIndex, final int radius) {
		int y = placeIndex / xSize;
		int x = placeIndex - y * xSize;
		final List<Integer> v = new ArrayList<Integer>(radius + 1 * radius + 1);
		int p;
		for ( int i = 1 - radius; i < radius; i++ ) {
			p = getPlaceIndexAt(x + i, y - radius);
			if ( p != -1 ) {
				v.add(p);
			}
			p = getPlaceIndexAt(x - i, y + radius);
			if ( p != -1 ) {
				v.add(p);
			}
		}
		for ( int i = -radius; i < radius + 1; i++ ) {
			p = getPlaceIndexAt(x - radius, y - i);
			if ( p != -1 ) {
				v.add(p);
			}
			p = getPlaceIndexAt(x + radius, y + i);
			if ( p != -1 ) {
				v.add(p);
			}
		}
		return v;
	}

	@Override
	public boolean isVN() {
		return false;
	}
}
