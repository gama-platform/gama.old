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
package msi.gama.metamodel.shape;

import msi.gama.runtime.IScope;
import msi.gaml.types.GamaGeometryType;
import com.vividsolutions.jts.geom.Geometry;

public class GamaDynamicLink extends GamaShape {

	// FIXME: DynamicLink does not work

	// Represents a dynamic link between two geometries. The geometry of this link
	// is the intersection of the two geometries when they intersect, and a line between
	// their centroids when they do not.

	final IShape source, target; // final ? Maybe can be dynamic as well.
	ILocation s, t; // cache
	IShape previousGeometry;

	public GamaDynamicLink(final IShape source, final IShape target) {
		this.source = source;
		this.target = target;
		refresh();
	}

	public IShape getSource() {
		return source;
	}

	public IShape getTarget() {
		return target;
	}

	public void refresh() {
		ILocation ss = source.getLocation();
		ILocation tt = target.getLocation();
		// if ( s == null || t == null || !s.equals(ss) || !t.equals(tt) ) {
		s = ss;
		t = tt;
		setGeometry(buildGeometry());
		// }
	}

	@Override
	public void setGeometry(final IShape geom) {
		if ( geom == null || geom == this ) { return; }
		super.setGeometry(geom);
		// if ( getAgent() != null ) {
		// getAgent().getTopology().updateAgent(previousGeometry, getAgent());
		// }
		previousGeometry = geom;
	}

	private IShape buildGeometry() {
		Geometry p1 = source.getInnerGeometry();
		Geometry p2 = target.getInnerGeometry();
		if ( p1 == null || p2 == null ) { return null; }
		Geometry inter = p1.intersection(p2);
		if ( inter.getLength() > 0 ) { return new GamaShape(inter); }
		return GamaGeometryType.buildLine(s, t);
	}

	@Override
	public Geometry getInnerGeometry() {
		refresh();
		return super.getInnerGeometry();
	}

	@Override
	public ILocation getLocation() {
		refresh();
		return super.getLocation();
	}

	@Override
	public String toString() {
		return "link between " + source.toString() + " and " + target.toString();
	}

	@Override
	public GamaDynamicLink copy(IScope scope) {
		return new GamaDynamicLink(source, target);
	}
}
