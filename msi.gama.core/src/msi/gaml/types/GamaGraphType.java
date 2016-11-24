/*********************************************************************************************
 *
 * 'GamaGraphType.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gaml.expressions.VariableExpression;
import msi.gaml.operators.Cast;

@type(name = IKeyword.GRAPH, id = IType.GRAPH, wraps = {
		IGraph.class }, kind = ISymbolKind.Variable.CONTAINER, concept = { IConcept.TYPE, IConcept.GRAPH })
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GamaGraphType extends GamaContainerType<IGraph> {

	@Override
	public IGraph cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	public static IGraph staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		// param = true : spatial.

		if (obj == null) {
			return null;
		}
		if (obj instanceof IGraph) {
			return (IGraph) obj;
		}
		final boolean spatial = param != null && Cast.asBool(scope, param);

		if (obj instanceof IList) {
			return from(scope, (IList) obj, spatial);
		}
		// List of agents, geometries...

		if (obj instanceof VariableExpression) { // this may be a variable ?
			// in this case, attempt to decode it !
			return (IGraph) ((VariableExpression) obj).value(scope);
		}

		if (obj instanceof GamaMap) {
			return from(scope, (GamaMap) obj, spatial);
		}
		// TODO Matrix, Pair ?

		return null;
	}

	public static IGraph from(final IScope scope, final GamaMap<?, ?> obj, final boolean spatial) {
		final IGraph result = spatial
				? new GamaSpatialGraph(GamaListFactory.create(Types.NO_TYPE), false, false, null, null, scope,
						obj.getType().getKeyType(), Types.NO_TYPE)
				: new GamaGraph(scope, GamaListFactory.create(Types.NO_TYPE), false, false, null, null,
						obj.getType().getKeyType(), Types.NO_TYPE);
		final GamaPair p = new GamaPair(null, null, Types.NO_TYPE, Types.NO_TYPE);
		for (final Map.Entry<?, ?> k : obj.entrySet()) {
			p.key = k.getKey();
			p.value = k.getValue();
			result.addEdge(p);
		}
		return result;
	}

	public static IGraph from(final IScope scope, final IList obj, final boolean spatial) {
		final IType nodeType = obj.getType().getContentType();
		return spatial ? new GamaSpatialGraph(obj, false, false, null, null, scope, nodeType, Types.NO_TYPE)
				: new GamaGraph(scope, obj, false, false, null, null, nodeType, Types.NO_TYPE);
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

	@Override
	public boolean isDrawable() {
		return true;
	}

}
