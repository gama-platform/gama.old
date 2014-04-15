/*********************************************************************************************
 * 
 * 
 * 'GamaGraphType.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.types;

import java.util.Map;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import msi.gaml.expressions.VariableExpression;
import msi.gaml.operators.Cast;

@type(name = IKeyword.GRAPH, id = IType.GRAPH, wraps = { IGraph.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaGraphType extends GamaContainerType<IGraph> {

	@Override
	public IGraph cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentsType) throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	public static IGraph staticCast(final IScope scope, final Object obj, final Object param) {
		// param = true : spatial.

		if ( obj == null ) { return null; }
		if ( obj instanceof IGraph ) { return (IGraph) obj; }
		boolean spatial = param != null && Cast.asBool(scope, param);

		if ( obj instanceof IList ) { return from(scope, (IList) obj, spatial); }
		// List of agents, geometries...

		if ( obj instanceof VariableExpression ) { // this may be a variable ?
			// in this case, attempt to decode it !
			return (IGraph) ((VariableExpression) obj).value(scope);
		}

		if ( obj instanceof Map ) { return from(scope, (Map) obj, spatial); }
		// TODO Matrix, Pair ?

		return null;
	}

	public static IGraph from(final IScope scope, final Map<?, ?> obj, final boolean spatial) {
		IGraph result =
			spatial ? new GamaSpatialGraph(new GamaList(), false, false, null, null, scope) : new GamaGraph(
				new GamaList(), false, false, null, null, scope);
		GamaPair p = new GamaPair(null, null);
		for ( Map.Entry<?, ?> k : obj.entrySet() ) {
			p.key = k.getKey();
			p.value = k.getValue();
			result.addEdge(p);
		}
		return result;
	}

	public static IGraph from(final IScope scope, final IList obj, final boolean spatial) {
		return spatial ? new GamaSpatialGraph(obj, false, false, null, null, scope) : new GamaGraph(obj, false, false,
			null, null, scope);
	}

	public static IGraph useChacheForShortestPath(final IGraph source, final boolean useCache) {
		source.setSaveComputedShortestPaths(useCache);
		return source; // TODO Clone ?
	}

	public static IGraph asDirectedGraph(final IGraph source) {
		source.setDirected(true);
		return source; // TODO Clone ?
	}

	public static IGraph asUndirectedGraph(final IGraph source) {
		source.setDirected(false);
		return source; // TODO Clone ?
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

}
