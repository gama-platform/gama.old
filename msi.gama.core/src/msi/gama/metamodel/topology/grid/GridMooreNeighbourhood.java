/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.grid;

import java.util.*;
import msi.gama.metamodel.shape.IShape;

/**
 * Written by drogoul Modified on 8 mars 2011
 * 
 * @todo Description
 * 
 */
public class GridMooreNeighbourhood extends GridNeighbourhood {

	public GridMooreNeighbourhood(final IShape[] agents, final int xSize, final int ySize, final boolean isTorus) {
		super(agents, xSize, ySize , isTorus);
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
