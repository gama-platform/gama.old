/*********************************************************************************************
 *
 *
 * 'IGraph.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.graph;

import java.util.*;
import org.jgrapht.*;
import msi.gama.metamodel.topology.graph.FloydWarshallShortestPathsGAMA;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.path.IPath;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractContainerStatement.GraphObjectToAdd;
import msi.gaml.types.IType;

/**
 * Written by drogoul
 * Modified on 24 nov. 2011
 *
 * An interface for the different kinds of graphs encountered in GAML. Vertices are the keys (actually, pairs of nodes), while edges are the values
 *
 */
@vars({
	@var(name = "spanning_tree",
		type = IType.LIST,
		of = ITypeProvider.FIRST_CONTENT_TYPE,
		doc = { @doc("Returns the list of edges that compose the minimal spanning tree of this graph") }),
	@var(name = "circuit",
		type = IType.PATH,
		doc = {
			@doc("Returns a polynomial approximation of the Hamiltonian cycle (the optimal tour passing through each vertex) of this graph") }),
	@var(name = "connected", type = IType.BOOL, doc = { @doc("Returns whether this graph is connected or not") }),
	@var(name = "edges",
		type = IType.LIST,
		of = ITypeProvider.FIRST_CONTENT_TYPE,
		doc = { @doc("Returns the list of edges of the receiver graph") }),
	@var(name = "vertices",
		type = IType.LIST,
		of = ITypeProvider.FIRST_KEY_TYPE,
		doc = { @doc("Returns the list of vertices of the receiver graph") }) })
public interface IGraph<Node, Edge> extends IModifiableContainer<Node, Edge, GamaPair<Node, Node>, GraphObjectToAdd>, IAddressableContainer<Node, Edge, GamaPair<Node, Node>, List<Edge>>, WeightedGraph<Node, Edge>, DirectedGraph<Node, Edge>, UndirectedGraph<Node, Edge>, IGraphEventProvider {

	public abstract double getVertexWeight(final Object v);

	public abstract Double getWeightOf(final Object v);

	public abstract void setVertexWeight(final Object v, final double weight);

	void setWeights(Map<?, Double> weights);

	public Collection _internalEdgeSet();

	public Collection _internalNodesSet();

	public Map<Edge, _Edge<Node, Edge>> _internalEdgeMap();

	public Map<Node, _Vertex<Node, Edge>> _internalVertexMap();

	@getter("edges")
	public abstract IList<Edge> getEdges();

	@getter("vertices")
	public abstract IList<Node> getVertices();

	@getter("spanning_tree")
	public abstract IList<Edge> getSpanningTree(IScope scope);

	@getter("circuit")
	public abstract IPath<Node, Edge, IGraph<Node, Edge>> getCircuit(IScope scope);

	@getter("connected")
	public abstract Boolean getConnected();

	public abstract boolean isDirected();

	public abstract void setDirected(final boolean b);

	public abstract Object addEdge(Object p);

	public abstract void setOptimizerType(String optiType);

	public FloydWarshallShortestPathsGAMA<Node, Edge> getOptimizer();

	public void setOptimizer(FloydWarshallShortestPathsGAMA<Node, Edge> optimizer);

	public int getVersion();

	public void setVersion(int version);

	public void incVersion();

	// FIXME Patrick: To check
	// public abstract IPath<V,E> computeShortestPathBetween(final Object source, final Object target);
	// public abstract IList<IShape> computeBestRouteBetween(final Object source, final Object target);

	public abstract IPath<Node, Edge, IGraph<Node, Edge>> computeShortestPathBetween(IScope scope, final Node source,
		final Node target);

	public abstract IList<Edge> computeBestRouteBetween(IScope scope, final Node source, final Node target);

	public double computeWeight(final IPath<Node, Edge, ? extends IGraph<Node, Edge>> gamaPath);

	public double computeTotalWeight();

	public boolean isSaveComputedShortestPaths();

	public void setSaveComputedShortestPaths(boolean saveComputedShortestPaths);

	public abstract List<IPath<Node, Edge, IGraph<Node, Edge>>> computeKShortestPathsBetween(IScope scope, Node source,
		Node target, int k);

	public abstract IList<IList<Edge>> computeKBestRoutesBetween(IScope scope, final Node source, final Node target,
		int k);

	public GraphObjectToAdd buildValue(IScope scope, Object object);

	public IContainer buildValues(IScope scope, IContainer objects);

	public GamaPair<Node, Node> buildIndex(IScope scope, Object object);

	public IContainer<?, GamaPair<Node, Node>> buildIndexes(IScope scope, IContainer value);

	public ISpecies getVertexSpecies();

	public ISpecies getEdgeSpecies();

}