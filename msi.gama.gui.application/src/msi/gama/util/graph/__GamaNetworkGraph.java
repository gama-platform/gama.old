/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.graph;

import msi.gama.environment.GeometricFunctions;
import msi.gama.util.*;
import org.geotools.graph.build.line.BasicLineGraphGenerator;
import org.geotools.graph.structure.*;
import com.vividsolutions.jts.geom.*;

/**
 * Written by drogoul
 * Modified on 24 nov. 2011
 * 
 * @todo Description
 * 
 */
public abstract class __GamaNetworkGraph extends __GamaGeotoolsGraphWrapper {

	@Override
	protected void add(final GamaGeometry value, final double weight) {
		Geometry g = value.getInnerGeometry();
		// if the inner geometry is a line string, add it directly to the graph.
		if ( g instanceof LineString ) {
			Graphable component = generator.add(value);
			weights.put(component, weight);
			// if it is a multilinestring, recursively add its components with the same weight
		} else if ( g instanceof MultiLineString ) {
			for ( int i = 0, n = g.getNumGeometries(); i < n; i++ ) {
				add(new GamaGeometry(g.getGeometryN(i)), weight);
			}
			// if it is any other geometry, build its skeleton and add the line strings recursively
			// with the same weight.
		} else {
			for ( LineString l : GeometricFunctions.squeletisation(null, g) ) {
				// Double-check the idea of creating skeletons automatically
				add(new GamaGeometry(l), weight);
			}
		}
	}

	@Override
	protected void remove(final GamaGeometry value) {
		Geometry g = value.getInnerGeometry();
		// if the inner geometry is a line string, add it directly to the graph.
		if ( g instanceof LineString ) {
			Graphable component = generator.remove(value);
			weights.remove(component);
			// if it is a multilinestring, recursively add its components with the same weight
		} else if ( g instanceof MultiLineString ) {
			for ( int i = 0, n = g.getNumGeometries(); i < n; i++ ) {
				remove(new GamaGeometry(g.getGeometryN(i)));
			}
			// if it is any other geometry, build its skeleton and add the line strings recursively
			// with the same weight.
		} else {
			for ( LineString l : GeometricFunctions.squeletisation(null, g) ) {
				// Double-check the idea of creating skeletons automatically
				remove(new GamaGeometry(l));
			}
		}

	}

	private class GamaNetworkGraphGenerator extends BasicLineGraphGenerator {

		@Override
		public Graphable add(final Object obj) {
			LineString ls = (LineString) ((GamaGeometry) obj).getInnerGeometry();
			// must be a line string as the class is private
			// parent class expects a line segment
			Edge e =
				(Edge) super.add(new LineSegment(ls.getCoordinateN(0), ls.getCoordinateN(ls
					.getNumPoints() - 1)));
			// over write object to be the GamaGeometry
			e.setObject(obj);
			return e;
		}

		@Override
		public Graphable remove(final Object obj) {
			LineString ls = (LineString) ((GamaGeometry) obj).getInnerGeometry();
			// must be a line string as the class is private
			// parent ecpexts a line segment
			return super.remove(new LineSegment(ls.getCoordinateN(0), ls.getCoordinateN(ls
				.getNumPoints() - 1)));
		}

		@Override
		public Graphable get(final Object obj) {
			if ( obj instanceof GamaPoint ) { return getNode((GamaPoint) obj); }
			if ( obj instanceof GamaGeometry ) {
				Geometry g = ((GamaGeometry) obj).getInnerGeometry();
				if ( g instanceof LineString ) {
					LineString ls = (LineString) g;
					// must be a line string as the class is private
					// parent ecpexts a line segment
					return super.get(new LineSegment(ls.getCoordinateN(0), ls.getCoordinateN(ls
						.getNumPoints() - 1)));
				} else if ( g instanceof Point ) { return getNode(g.getCoordinate()); }
			}
			return null;
			// No other case ?
		}

		@Override
		protected void setObject(final Node n, final Object obj) {
			// set underlying object to be point instead of coordinate
			GamaPoint c = new GamaPoint((Coordinate) obj);
			n.setObject(GamaGeometry.createPoint(c));
		}

	}

}
