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
package msi.gaml.types;

import java.util.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Cast;

@type(name = IType.PATH_STR, id = IType.PATH, wraps = { IPath.class, GamaPath.class }, kind = ISymbolKind.Variable.REGULAR)
public class GamaPathType extends GamaType<IPath> {

	@Override
	public IPath cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	@Override
	public IPath getDefault() {
		return null;
	}

	public static IPath pathBetween(final IScope scope, final ILocation source,
		final ILocation target, final IGraph graph) {
		if ( graph == null ) { return null; }
		Collection<IShape> nodes = graph.vertexSet();
		ITopology m = scope.getTopology();
		IShape s = null;
		IShape t = null;
		double minS = Double.MAX_VALUE;
		double minT = Double.MAX_VALUE;
		boolean okS = false;
		boolean okT = false;
		for ( IShape n : nodes ) {
			IShape g = n;
			if ( !okS ) {
				double dist = m.distanceBetween(g, source.getGeometry());
				if ( dist < minS ) {
					s = n;
					minS = dist;
				}
			}
			if ( !okT ) {
				double dist = m.distanceBetween(g, target.getGeometry());
				if ( dist < minT ) {
					t = n;
					minT = dist;
				}
			}
			if ( g.intersects(source) ) {
				s = n;
				okS = true;
			}
			if ( g.intersects(target) ) {
				t = n;
				okT = true;
			}
			if ( okS && okT ) {
				break;
			}
		}
		return (GamaPath) graph.computeShortestPathBetween(s, t);
	}

	public static IPath pathBetweenPoints(final IScope scope, final ILocation source,
		final ILocation target, final GamaSpatialGraph graph) throws GamaRuntimeException {
		if ( graph == null ) { return null; }
		ITopology m = scope.getTopology();
		Collection<IShape> edges = graph.edgeSet();
		Object s1 = null;
		Object t1 = null;
		// Object s2 = null;
		// Object t2 = null;
		IShape edgeS = null, edgeT = null;
		double dist1 = Double.MAX_VALUE;
		double dist2 = Double.MAX_VALUE;
		// System.out.println("source : " + source);

		for ( Object o : edges ) {
			IShape eg = Cast.asGeometry(scope, o);
			double d1 = m.distanceBetween(eg, source);
			// System.out.println(d1 + " -> " + eg);

			if ( d1 < dist1 ) {
				edgeS = eg;
				s1 = graph.getEdgeSource(o);
				// s2 = graph.getEdgeTarget(o);
				dist1 = d1;
			}
			double d2 = m.distanceBetween(eg, target);
			if ( d2 < dist2 ) {
				edgeT = eg;
				t1 = graph.getEdgeSource(o);
				// t2 = graph.getEdgeTarget(o);
				dist2 = d2;
			}
		}
		if ( s1 == null || t1 == null ) { return null; }
		IPath path = null;
		if ( edgeS == edgeT ) {
			path = new GamaPath(m, source, target, GamaList.with(edgeS));
			return path;
		}
		path = graph.computeShortestPathBetween(s1, t1);
		if ( path == null ) { return null; }

		return path;
	}

	public static IPath staticCast(final IScope scope, final Object obj, final Object param) {
		if ( obj instanceof IPath ) { return (IPath) obj; }
		if ( obj instanceof List ) {
			List<ILocation> list = new GamaList();
			for ( Object p : (List) obj ) {
				list.add(Cast.asPoint(scope, p));
			}
			return new GamaPath(scope.getTopology(), list);
		}
		return null;
	}

}
