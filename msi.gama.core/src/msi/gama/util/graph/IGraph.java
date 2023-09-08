/*******************************************************************************************************
 *
 * IGraph.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;

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

	/**
	 * Gets the vertex weight.
	 *
	 * @param v
	 *            the v
	 * @return the vertex weight
	 */
	double getVertexWeight(final Object v);

	/**
	 * Gets the weight of.
	 *
	 * @param v
	 *            the v
	 * @return the weight of
	 */
	Double getWeightOf(final Object v);

	/**
	 * Sets the vertex weight.
	 *
	 * @param v
	 *            the v
	 * @param weight
	 *            the weight
	 */
	void setVertexWeight(final Object v, final double weight);

	/**
	 * Sets the weights.
	 *
	 * @param weights
	 *            the weights
	 */
	void setWeights(Map<?, Double> weights);

	// /**
	// * Internal edge set.
	// *
	// * @return the collection
	// */
	// Collection _internalEdgeSet();

	// Collection _internalNodesSet();

	/**
	 * Internal edge map.
	 *
	 * @return the map
	 */
	Map<Edge, _Edge<Node, Edge>> _internalEdgeMap();

	/**
	 * Internal vertex map.
	 *
	 * @return the map
	 */
	Map<Node, _Vertex<Node, Edge>> _internalVertexMap();

	/**
	 * Gets the edges.
	 *
	 * @return the edges
	 */
	@getter ("edges")
	IList<Edge> getEdges();

	/**
	 * Gets the vertices.
	 *
	 * @return the vertices
	 */
	@getter ("vertices")
	IList<Node> getVertices();

	/**
	 * Gets the spanning tree.
	 *
	 * @param scope
	 *            the scope
	 * @return the spanning tree
	 */
	@getter ("spanning_tree")
	IList<Edge> getSpanningTree(IScope scope);

	/**
	 * Gets the circuit.
	 *
	 * @param scope
	 *            the scope
	 * @return the circuit
	 */
	@getter ("circuit")
	IPath<Node, Edge, IGraph<Node, Edge>> getCircuit(IScope scope);

	/**
	 * Gets the connected.
	 *
	 * @return the connected
	 */
	@getter ("connected")
	Boolean getConnected();

	/**
	 * Checks for cycle.
	 *
	 * @return the boolean
	 */
	@getter ("has_cycle")
	Boolean hasCycle();

	/**
	 * Checks if is directed.
	 *
	 * @return true, if is directed
	 */
	boolean isDirected();

	/**
	 * Sets the directed.
	 *
	 * @param b
	 *            the new directed
	 */
	void setDirected(final boolean b);

	/**
	 * Adds the edge.
	 *
	 * @param p
	 *            the p
	 * @return the object
	 */
	Object addEdge(Object p);

	/**
	 * Sets the shortest path algorithm.
	 *
	 * @param optiType
	 *            the new shortest path algorithm
	 */
	void setShortestPathAlgorithm(String optiType);

	/**
	 * Sets the k shortest path algorithm.
	 *
	 * @param optiType
	 *            the new k shortest path algorithm
	 */
	void setKShortestPathAlgorithm(String optiType);

	/**
	 * Gets the floyd warshall shortest paths.
	 *
	 * @return the floyd warshall shortest paths
	 */
	FloydWarshallShortestPathsGAMA<Node, Edge> getFloydWarshallShortestPaths();

	/**
	 * Sets the floyd warshall shortest paths.
	 *
	 * @param optimizer
	 *            the optimizer
	 */
	void setFloydWarshallShortestPaths(FloydWarshallShortestPathsGAMA<Node, Edge> optimizer);

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	int getVersion();

	/**
	 * Sets the version.
	 *
	 * @param version
	 *            the new version
	 */
	void setVersion(int version);

	/**
	 * Inc version.
	 */
	void incVersion();

	// FIXME Patrick: To check
	// public abstract IPath<V,E> computeShortestPathBetween(final Object
	// source, final Object target);
	// public abstract IList<IShape> computeBestRouteBetween(final Object
	// source, final Object target);

	/**
	 * Compute shortest path between.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the i path
	 */
	IPath<Node, Edge, IGraph<Node, Edge>> computeShortestPathBetween(IScope scope, final Node source,
			final Node target);

	/**
	 * Compute best route between.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the i list
	 */
	IList<Edge> computeBestRouteBetween(IScope scope, final Node source, final Node target);

	/**
	 * Compute weight.
	 *
	 * @param gamaPath
	 *            the gama path
	 * @return the double
	 */
	double computeWeight(final IPath<Node, Edge, ? extends IGraph<Node, Edge>> gamaPath);

	/**
	 * Compute total weight.
	 *
	 * @return the double
	 */
	double computeTotalWeight();

	/**
	 * Checks if is save computed shortest paths.
	 *
	 * @return true, if is save computed shortest paths
	 */
	boolean isSaveComputedShortestPaths();

	/**
	 * Sets the save computed shortest paths.
	 *
	 * @param saveComputedShortestPaths
	 *            the new save computed shortest paths
	 */
	void setSaveComputedShortestPaths(boolean saveComputedShortestPaths);

	/**
	 * Compute K shortest paths between.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param k
	 *            the k
	 * @return the i list
	 */
	IList<IPath<Node, Edge, IGraph<Node, Edge>>> computeKShortestPathsBetween(IScope scope, Node source, Node target,
			int k);

	/**
	 * Compute K best routes between.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param k
	 *            the k
	 * @return the i list
	 */
	IList<IList<Edge>> computeKBestRoutesBetween(IScope scope, final Node source, final Node target, int k);

	/**
	 * Builds the value.
	 *
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @return the graphs. graph object to add
	 */
	Graphs.GraphObjectToAdd buildValue(IScope scope, Object object);

	/**
	 * Builds the values.
	 *
	 * @param scope
	 *            the scope
	 * @param objects
	 *            the objects
	 * @return the i container
	 */
	IContainer buildValues(IScope scope, IContainer objects);

	/**
	 * Builds the index.
	 *
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @return the gama pair
	 */
	GamaPair<Node, Node> buildIndex(IScope scope, Object object);

	/**
	 * Builds the indexes.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 * @return the i container
	 */
	IContainer<?, GamaPair<Node, Node>> buildIndexes(IScope scope, IContainer value);

	/**
	 * Gets the vertex species.
	 *
	 * @return the vertex species
	 */
	ISpecies getVertexSpecies();

	/**
	 * Gets the edge species.
	 *
	 * @return the edge species
	 */
	ISpecies getEdgeSpecies();

	/**
	 * Contains.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	@Override
	default boolean contains(final IScope scope, final Object o) {
		if (o instanceof GamaPair) return Graphs.containsEdge(scope, this, (GamaPair) o);
		return Graphs.containsEdge(scope, this, o);
	}

	/**
	 * Contains key.
	 *
	 * @param scope
	 *            the scope
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	@Override
	default boolean containsKey(final IScope scope, final Object o) {
		return Graphs.containsVertex(scope, this, o);
	}

}