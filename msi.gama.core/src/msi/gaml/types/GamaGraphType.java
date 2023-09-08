/*******************************************************************************************************
 *
 * GamaGraphType.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gaml.expressions.variables.VariableExpression;
import msi.gaml.operators.Cast;

/**
 * The Class GamaGraphType.
 */
@type (
		name = IKeyword.GRAPH,
		id = IType.GRAPH,
		wraps = { IGraph.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.GRAPH },
		doc = @doc ("Special type of container composed of edges and vertices"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraphType extends GamaContainerType<IGraph> {

	@Override
	public IGraph cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	@Override
	public int getNumberOfParameters() {
		return 2;
	}

	/**
	 * Static cast.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param param the param
	 * @param copy the copy
	 * @return the i graph
	 */
	public static IGraph staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		// param = true : spatial.

		if (obj == null) { return null; }
		if (obj instanceof IGraph) { return (IGraph) obj; }
		final boolean spatial = param != null && Cast.asBool(scope, param);

		if (obj instanceof IList) { return from(scope, (IList) obj, spatial); }
		// List of agents, geometries...

		if (obj instanceof VariableExpression) { // this may be a variable ?
			// in this case, attempt to decode it !
			return (IGraph) ((VariableExpression) obj).value(scope);
		}

		if (obj instanceof IMap) { return from(scope, (IMap) obj, spatial); }
		// TODO Matrix, Pair ?

		return null;
	}

	/**
	 * From.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param spatial the spatial
	 * @return the i graph
	 */
	public static IGraph from(final IScope scope, final IMap<?, ?> obj, final boolean spatial) {
		final IGraph result = spatial
				? new GamaSpatialGraph(GamaListFactory.create(Types.NO_TYPE), false, false, false,null, null, scope,
						obj.getGamlType().getKeyType(), Types.NO_TYPE)
				: new GamaGraph(scope, GamaListFactory.create(Types.NO_TYPE), false, false,false, null, null,
						obj.getGamlType().getKeyType(), Types.NO_TYPE);
		final GamaPair p = new GamaPair(null, null, Types.NO_TYPE, Types.NO_TYPE);
		for (final Map.Entry<?, ?> k : obj.entrySet()) {
			p.key = k.getKey();
			p.value = k.getValue();
			result.addEdge(p);
		}
		return result;
	}

	/**
	 * From.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param spatial the spatial
	 * @return the i graph
	 */
	public static IGraph from(final IScope scope, final IList obj, final boolean spatial) {
		final IType nodeType = obj.getGamlType().getContentType();
		return spatial ? new GamaSpatialGraph(obj, false, false,false, null, null, scope, nodeType, Types.NO_TYPE)
				: new GamaGraph(scope, obj, false, false, false,null, null, nodeType, Types.NO_TYPE);
	}

	// GamaSpatialGraph(final IContainer edgesOrVertices, final boolean byEdge, final boolean directed,
	// final VertexRelationship rel, final ISpecies edgesSpecies, final IScope scope, final IType nodeType,
	// final IType edgeType) {

	// public GamaGraph(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge, final boolean
	// directed,
	// final VertexRelationship rel, final ISpecies edgesSpecies, final IType nodeType, final IType edgeType) {

	/**
	 * From.
	 *
	 * @param scope the scope
	 * @param edgesOrVertices the edges or vertices
	 * @param byEdge the by edge
	 * @param directed the directed
	 * @param spatial the spatial
	 * @param nodeType the node type
	 * @param edgeType the edge type
	 * @return the i graph
	 */
	public static IGraph from(final IScope scope, final IList edgesOrVertices, final boolean byEdge,
			final boolean directed, final boolean spatial, final IType nodeType, final IType edgeType) {
		return spatial ? new GamaSpatialGraph(edgesOrVertices, byEdge, directed, false,null, null, scope, nodeType, edgeType)
				: new GamaGraph(scope, edgesOrVertices, byEdge, directed, false,null, null, nodeType, edgeType);
	}

	/**
	 * Use chache for shortest path.
	 *
	 * @param source the source
	 * @param useCache the use cache
	 * @return the i graph
	 */
	public static IGraph useChacheForShortestPath(final IGraph source, final boolean useCache) {
		source.setSaveComputedShortestPaths(useCache);
		return source; // TODO Clone ?
	}

	/**
	 * As directed graph.
	 *
	 * @param source the source
	 * @return the i graph
	 */
	public static IGraph asDirectedGraph(final IGraph source) {
		source.setDirected(true);
		return source; // TODO Clone ?
	}

	/**
	 * As undirected graph.
	 *
	 * @param source the source
	 * @return the i graph
	 */
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
