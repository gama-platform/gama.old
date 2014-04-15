/*********************************************************************************************
 * 
 *
 * 'GamaDynamicLink.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
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
		// IShape previousGeometry;

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
		// previousGeometry = geom;
	}

	private IShape buildGeometry() {
		Geometry p1 = source.getInnerGeometry();
		Geometry p2 = target.getInnerGeometry();
		if ( p1 == null || p2 == null ) { return null; }
		// delete because it is not very consistent with path building
		// Geometry inter = p1.intersection(p2);
		// if ( inter.getLength() > 0 ) { return new GamaShape(inter); }
		return GamaGeometryType.buildLine(s, t);
	}

	@Override
	public Geometry getInnerGeometry() {
		refresh();
		return super.getInnerGeometry();
	}

	@Override
	public GamaPoint getLocation() {
		refresh();
		return super.getLocation();
	}

	@Override
	public String toString() {
		return "link between " + source.toString() + " and " + target.toString();
	}

	@Override
	public GamaDynamicLink copy(final IScope scope) {
		return new GamaDynamicLink(source, target);
	}
}
