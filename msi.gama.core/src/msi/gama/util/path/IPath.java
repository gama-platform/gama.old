/*******************************************************************************************************
 *
 * IPath.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.path;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.graph.IGraph;
import msi.gaml.types.IType;

/**
 * The class IPath.
 *
 * @author drogoul
 * @since 14 d�c. 2011
 *
 */
@vars ({ @variable (
		name = IKeyword.TARGET,
		type = IType.NONE,
		doc = @doc ("The target (i.e. last element) of this path")),
		@variable (
				name = IKeyword.SOURCE,
				type = IType.NONE,
				doc = @doc ("The source (i.e. first element) of this path")),
		@variable (
				name = IKeyword.GRAPH,
				type = IType.GRAPH,
				doc = @doc ("The graph this path refers to")),
		@variable (
				name = IKeyword.SHAPE,
				type = IType.GEOMETRY,
				doc = @doc ("The shape obtained by all the points of this path")),
		@variable (
				name = IKeyword.SEGMENTS,
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of segments that compose this path") }),
		@variable (
				name = "distance",
				type = IType.FLOAT,
				doc = { @doc ("Returns the total lenght of all the segments that compose this path") }),
		@variable (
				name = "weight",
				type = IType.FLOAT,
				doc = @doc ("The addition of all the weights of the vertices that compose this path, with respect to the graph they belong to")),
		@variable (
				name = "edges",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = @doc ("The list of edges of the underlying graph that compose this path")),
		@variable (
				name = "vertices",
				type = IType.LIST,
				doc = @doc ("The list of vertices of the underlying graph that compose this path"))
		// @var(name = IKeyword.AGENTS, type = IType.LIST, of = IType.AGENT),
		// Could be replaced by "geometries"
		/*
		 * Normally not necessary as it is inherited from GamaGeometry @var(name = GamaPath.POINTS, type = IType.LIST,
		 * of = IType.POINT)
		 */
})
public interface IPath<V, E, G extends IGraph<V, E>> extends IValue {// extends IShape {

	/**
 * Gets the start vertex.
 *
 * @return the start vertex
 */
@getter (IKeyword.SOURCE)
	V getStartVertex();

	/**
	 * Gets the end vertex.
	 *
	 * @return the end vertex
	 */
	@getter (IKeyword.TARGET)
	V getEndVertex();

	/**
	 * Gets the graph.
	 *
	 * @return the graph
	 */
	@getter (IKeyword.GRAPH)
	G getGraph();

	/**
	 * Gets the edge geometry.
	 *
	 * @return the edge geometry
	 */
	@getter (IKeyword.SEGMENTS)
	IList<IShape> getEdgeGeometry();

	/**
	 * Gets the vertex list.
	 *
	 * @return the vertex list
	 */
	@getter ("vertices")
	IList<V> getVertexList();

	/**
	 * Gets the edge list.
	 *
	 * @return the edge list
	 */
	@getter ("edges")
	IList<E> getEdgeList();

	/**
	 * Gets the geometry.
	 *
	 * @return the geometry
	 */
	@getter ("shape")
	IShape getGeometry();

	// @getter(IKeyword.AGENTS)
	// public abstract List<IShape> getAgentList();

	/**
	 * Gets the weight.
	 *
	 * @return the weight
	 */
	@getter ("weight")
	double getWeight();

	/**
	 * Gets the weight.
	 *
	 * @param line the line
	 * @return the weight
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	double getWeight(final IShape line) throws GamaRuntimeException;

	/**
	 * Accept visitor.
	 *
	 * @param agent the agent
	 */
	void acceptVisitor(final IAgent agent);

	/**
	 * Forget visitor.
	 *
	 * @param agent the agent
	 */
	void forgetVisitor(final IAgent agent);

	/**
	 * Index of.
	 *
	 * @param a the a
	 * @return the int
	 */
	int indexOf(final IAgent a);

	/**
	 * Index segment of.
	 *
	 * @param a the a
	 * @return the int
	 */
	int indexSegmentOf(final IAgent a);

	/**
	 * Checks if is visitor.
	 *
	 * @param a the a
	 * @return true, if is visitor
	 */
	boolean isVisitor(final IAgent a);

	/**
	 * Sets the index of.
	 *
	 * @param a the a
	 * @param index the index
	 */
	void setIndexOf(final IAgent a, final int index);

	/**
	 * Sets the index segement of.
	 *
	 * @param a the a
	 * @param indexSegement the index segement
	 */
	void setIndexSegementOf(final IAgent a, final int indexSegement);

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	int getLength();

	/**
	 * Gets the distance.
	 *
	 * @param scope the scope
	 * @return the distance
	 */
	@getter ("distance")
	double getDistance(IScope scope);

	/**
	 * Gets the topology.
	 *
	 * @param scope the scope
	 * @return the topology
	 */
	ITopology getTopology(IScope scope);

	/**
	 * Sets the real objects.
	 *
	 * @param realObjects the real objects
	 */
	void setRealObjects(final IMap<IShape, IShape> realObjects);

	/**
	 * Gets the real object.
	 *
	 * @param obj the obj
	 * @return the real object
	 */
	IShape getRealObject(final Object obj);

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	void setSource(V source);

	/**
	 * Sets the target.
	 *
	 * @param target the new target
	 */
	void setTarget(V target);

	/**
	 * Gets the graph version.
	 *
	 * @return the graph version
	 */
	int getGraphVersion();

	/**
	 * Sets the graph.
	 *
	 * @param graph the new graph
	 */
	void setGraph(G graph);

}