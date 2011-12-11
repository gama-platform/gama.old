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
package msi.gama.util;

import msi.gama.interfaces.IGeometry;
import com.vividsolutions.jts.geom.Geometry;

public class GamaDynamicLink extends GamaGeometry {

	// Represents a dynamic link between two geometries. The geometry of this link
	// is the intersection of the two geometries when they intersect, and a line between
	// their centroids when they do not.

	final GamaGeometry source, target; // final ? Maybe can be dynamic as well.
	GamaPoint s, t; // cache

	public GamaDynamicLink(final IGeometry source, final IGeometry target) {
		this.source = source.getGeometry();
		this.target = target.getGeometry();
		refresh();
	}

	public GamaGeometry getSource() {
		return source;
	}

	public GamaGeometry getTarget() {
		return target;
	}

	public void refresh() {
		GamaPoint ss = source.getLocation();
		GamaPoint tt = target.getLocation();
		if ( s == null || t == null || !s.equals(ss) || !t.equals(tt) ) {
			s = ss;
			t = tt;
			setGeometry(buildGeometry());
		}
	}

	private GamaGeometry buildGeometry() {
		Geometry p1 = source.getInnerGeometry();
		Geometry p2 = target.getInnerGeometry();
		if ( p1 == null || p2 == null ) { return null; }
		Geometry inter = p1.intersection(p2);
		if ( inter.getLength() > 0 ) { return new GamaGeometry(inter); }
		return buildLine(s, t);
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
	public GamaDynamicLink copy() {
		return new GamaDynamicLink(source, target);
	}
}
