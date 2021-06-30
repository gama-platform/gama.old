/*******************************************************************************************************
 *
 * msi.gama.util.graph.IGraph.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.Collection;
import java.util.List;
import java.util.Map;


import msi.gama.metamodel.topology.graph.FloydWarshallShortestPathsGAMA;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaPair;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.path.IPath;
import msi.gaml.operators.Graphs;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

import org.jgrapht.Graph;
/**
 * Written by drogoul Modified on 24 nov. 2011
 *
 * An interface for the different kinds of graphs encountered in GAML. Vertices are the keys (actually, pairs of nodes),
 * while edges are the values
 *
 */
@vars ({ @variable (
		name = "spanning_tree",
		type = IType.LIST,
		of = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
		doc = { @doc ("Returns the list of edges that compose the minimal spanning tree of this graph") }),
		@variable (
				name = "circuit",
				type = IType.PATH,
				doc = { @doc ("Returns a polynomial approximation of the Hamiltonian cycle (the optimal tour passing through each vertex) of this graph") }),
		@variable (
				name = "connected",
				type = IType.BOOL,
				doc = { @doc ("Returns whether this graph is connected or not") }),
		@variable (
				name = "edges",
				type = IType.LIST,
				of = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				doc = { @doc ("Returns the list of edges of the receiver graph") }),
		@variable (
				name = "vertices",
				type = IType.LIST,
				of = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
				doc = { @doc ("Returns the list of vertices of the receiver graph") }) })
@SuppressWarnings ({ "rawtypes" })
public interface IGraph<Node, Edge>
		extends IModifiableContainer<Node, Edge, GamaPair<Node, Node>, Graphs.GraphObjectToAdd>,
		IAddressableContainer<Node, Edge, GamaPair<Node, Node>, List<Edge>>, Graph<Node, Edge>, IGraphEventProvider {

	double getVertexWeight(final Object v);

	Double getWeightOf(final Object v);

	void setVertexWeight(final Object v, final double weight);

	void setWeights(Map<?, Double> weights);

	Collection _internalEdgeSet();

	Collection _internalNodesSet();

	Map<Edge, _Edge<Node, Edge>> _internalEdgeMap();

	Map<Node, _Vertex<Node, Edge>> _internalVertexMap();

	@getter ("edges")
	IList<Edge> getEdges();

	@getter ("vertices")
	IList<Node> getVertices();

	@getter ("spanning_tree")
	IList<Edge> getSpanningTree(IScope scope);

	@getter ("circuit")
	IPath<Node, Edge, IGraph<Node, Edge>> getCircuit(IScope scope);

	@getter ("connected")
	Boolean getConnected();

	@getter ("has_cycle")
	Boolean hasCycle();

	boolean isDirected();

	void setDirected(final boolean b);

	Object addEdge(Object p);

	void setShortestPathAlgorithm(String optiType);
	void setKShortestPathAlgorithm(String optiType);
	
	

	FloydWarshallShortestPathsGAMA<Node, Edge> getFloydWarshallShortestPaths();

	void setFloydWarshallShortestPaths(FloydWarshallShortestPathsGAMA<Node, Edge> optimizer);

	int getVersion();

	void setVersion(int version);

	void incVersion();

	// FIXME Patrick: To check
	// public abstract IPath<V,E> computeShortestPathBetween(final Object
	// source, final Object target);
	// public abstract IList<IShape> computeBestRouteBetween(final Object
	// source, final Object target);

	IPath<Node, Edge, IGraph<Node, Edge>> computeShortestPathBetween(IScope scope, final Node source,
			final Node target);

	IList<Edge> computeBestRouteBetween(IScope scope, final Node source, final Node target);

	double computeWeight(final IPath<Node, Edge, ? extends IGraph<Node, Edge>> gamaPath);

	double computeTotalWeight();

	boolean isSaveComputedShortestPaths();

	void setSaveComputedShortestPaths(boolean saveComputedShortestPaths);

	IList<IPath<Node, Edge, IGraph<Node, Edge>>> computeKShortestPathsBetween(IScope scope, Node source, Node target,
			int k);

	IList<IList<Edge>> computeKBestRoutesBetween(IScope scope, final Node source, final Node target, int k);

	Graphs.GraphObjectToAdd buildValue(IScope scope, Object object);

	IContainer buildValues(IScope scope, IContainer objects);

	GamaPair<Node, Node> buildIndex(IScope scope, Object object);

	IContainer<?, GamaPair<Node, Node>> buildIndexes(IScope scope, IContainer value);

	ISpecies getVertexSpecies();

	ISpecies getEdgeSpecies();

	@Override
	default boolean contains(final IScope scope, final Object o) {
		if (o instanceof GamaPair) { return Graphs.containsEdge(scope, this, (GamaPair) o); }
		return Graphs.containsEdge(scope, this, o);
	}

	@Override
	default boolean containsKey(final IScope scope, final Object o) {
		return Graphs.containsVertex(scope, this, o);
	}

}