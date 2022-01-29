/*******************************************************************************************************
 *
 * DXFSplineConverter.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.dxf.helpers;

import java.util.ArrayList;
import java.util.Iterator;

import msi.gama.ext.kabeja.dxf.DXFPolyline;
import msi.gama.ext.kabeja.dxf.DXFSpline;
import msi.gama.ext.kabeja.dxf.DXFVertex;
import msi.gama.ext.kabeja.math.NURBS;
import msi.gama.ext.kabeja.math.NURBSFixedNTELSPointIterator;

/**
 * The Class DXFSplineConverter.
 */
public class DXFSplineConverter {
	
	/**
	 * To DXF polyline.
	 *
	 * @param spline the spline
	 * @return the DXF polyline
	 */
	public static DXFPolyline toDXFPolyline(final DXFSpline spline) {
		DXFPolyline p = new DXFPolyline();
		p.setDXFDocument(spline.getDXFDocument());

		if (spline.getDegree() > 0 && spline.getKnots().length > 0) {
			Iterator<?> pi = new NURBSFixedNTELSPointIterator(toNurbs(spline), 30);

			while (pi.hasNext()) { p.addVertex(new DXFVertex((Point) pi.next())); }
		} else {
			// the curve is the controlpoint polygon
			Iterator<?> i = spline.getSplinePointIterator();

			while (i.hasNext()) {
				SplinePoint sp = (SplinePoint) i.next();

				if (sp.isControlPoint()) { p.addVertex(new DXFVertex(sp)); }
			}
		}

		if (spline.isClosed()) { p.setFlags(1); }

		return p;
	}

	/**
	 * To nurbs.
	 *
	 * @param spline the spline
	 * @return the nurbs
	 */
	public static NURBS toNurbs(final DXFSpline spline) {
		Iterator<?> i = spline.getSplinePointIterator();
		ArrayList<SplinePoint> list = new ArrayList<>();

		while (i.hasNext()) {
			SplinePoint sp = (SplinePoint) i.next();

			if (sp.isControlPoint()) { list.add(sp); }
		}

		NURBS n = new NURBS(list.toArray(new Point[list.size()]), spline.getKnots(), spline.getWeights(),
				spline.getDegree());
		n.setClosed(spline.isClosed());

		return n;
	}
}
