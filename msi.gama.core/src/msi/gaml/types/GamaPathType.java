/*********************************************************************************************
 *
 * 'GamaPathType.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Cast;

@type(name = IKeyword.PATH, id = IType.PATH, wraps = { IPath.class,
		GamaPath.class }, kind = ISymbolKind.Variable.REGULAR, concept = { IConcept.TYPE })
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaPathType extends GamaType<IPath> {

	@Override
	public IPath cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	@Override
	public IPath getDefault() {
		return null;
	}

	// public static IPath pathBetween(final IScope scope, final ILocation
	// source, final ILocation target,
	// final IGraph graph) {
	// if ( graph == null ) { return null; }
	// Collection<IShape> nodes = graph.vertexSet();
	// ITopology m = scope.getTopology();
	// IShape s = null;
	// IShape t = null;
	// double minS = Double.MAX_VALUE;
	// double minT = Double.MAX_VALUE;
	// boolean okS = false;
	// boolean okT = false;
	// for ( IShape n : nodes ) {
	// IShape g = n;
	// if ( !okS ) {
	// double dist = m.distanceBetween(scope, g, source.getGeometry());
	// if ( dist < minS ) {
	// s = n;
	// minS = dist;
	// }
	// }
	// if ( !okT ) {
	// double dist = m.distanceBetween(scope, g, target.getGeometry());
	// if ( dist < minT ) {
	// t = n;
	// minT = dist;
	// }
	// }
	// if ( g.intersects(source) ) {
	// s = n;
	// okS = true;
	// }
	// if ( g.intersects(target) ) {
	// t = n;
	// okT = true;
	// }
	// if ( okS && okT ) {
	// break;
	// }
	// }
	// return graph.computeShortestPathBetween(scope, s, t);
	// }
	//
	// public static IPath pathBetweenPoints(final IScope scope, final ILocation
	// source, final ILocation target,
	// final GamaSpatialGraph graph) throws GamaRuntimeException {
	// if ( graph == null ) { return null; }
	// ITopology m = scope.getTopology();
	// Collection<IShape> edges = graph.edgeSet();
	// Object s1 = null;
	// Object t1 = null;
	// // Object s2 = null;
	// // Object t2 = null;
	// IShape edgeS = null, edgeT = null;
	// double dist1 = Double.MAX_VALUE;
	// double dist2 = Double.MAX_VALUE;
	// // System.out.println("source : " + source);
	//
	// for ( Object o : edges ) {
	// IShape eg = Cast.asGeometry(scope, o, false);
	// double d1 = m.distanceBetween(scope, eg, source);
	// // System.out.println(d1 + " -> " + eg);
	//
	// if ( d1 < dist1 ) {
	// edgeS = eg;
	// s1 = graph.getEdgeSource(o);
	// // s2 = graph.getEdgeTarget(o);
	// dist1 = d1;
	// }
	// double d2 = m.distanceBetween(scope, eg, target);
	// if ( d2 < dist2 ) {
	// edgeT = eg;
	// t1 = graph.getEdgeSource(o);
	// // t2 = graph.getEdgeTarget(o);
	// dist2 = d2;
	// }
	// }
	// if ( s1 == null || t1 == null ) { return null; }
	// IPath path = null;
	// if ( edgeS == edgeT ) {
	// // path = new GamaPath(m, source, target, GamaList.with(edgeS));
	// path = PathFactory.newInstance(m, source, target,
	// GamaListFactory.createWithoutCasting(Types.PATH, edgeS));
	// return path;
	// }
	// path = graph.computeShortestPathBetween(scope, (IShape) s1, (IShape) t1);
	// if ( path == null ) { return null; }
	//
	// return path;
	// }
	//
	public static IPath staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof IPath) {
			return (IPath) obj;
		}
		if (obj instanceof List) {
			// List<ILocation> list = new GamaList();
			final List<IShape> list = GamaListFactory.create(Types.GEOMETRY);
			boolean isEdges = true;

			for (final Object p : (List) obj) {
				list.add(Cast.asPoint(scope, p));
				if (isEdges && !(p instanceof IShape && ((IShape) p).isLine())) {
					isEdges = false;
				}
			}
			// return new GamaPath(scope.getTopology(), list);
			return PathFactory.newInstance(scope, isEdges ? (IList<IShape>) obj : (IList<IShape>) list, isEdges);
		}
		return null;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

}
