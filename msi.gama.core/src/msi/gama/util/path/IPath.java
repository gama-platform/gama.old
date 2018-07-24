/*********************************************************************************************
 *
 * 'IPath.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.path;

import gnu.trove.map.hash.THashMap;
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

	@getter (IKeyword.SOURCE)
	public abstract V getStartVertex();

	@getter (IKeyword.TARGET)
	public abstract V getEndVertex();

	@getter (IKeyword.GRAPH)
	public abstract G getGraph();

	@getter (IKeyword.SEGMENTS)
	public abstract IList<IShape> getEdgeGeometry();

	@getter ("vertices")
	public abstract IList<V> getVertexList();

	@getter ("edges")
	public abstract IList<E> getEdgeList();

	@getter ("shape")
	public abstract IShape getGeometry();

	// @getter(IKeyword.AGENTS)
	// public abstract List<IShape> getAgentList();

	@getter ("weight")
	public abstract double getWeight();

	public abstract double getWeight(final IShape line) throws GamaRuntimeException;

	public abstract void acceptVisitor(final IAgent agent);

	public abstract void forgetVisitor(final IAgent agent);

	public abstract int indexOf(final IAgent a);

	public abstract int indexSegmentOf(final IAgent a);

	public abstract boolean isVisitor(final IAgent a);

	public abstract void setIndexOf(final IAgent a, final int index);

	public abstract void setIndexSegementOf(final IAgent a, final int indexSegement);

	public abstract int getLength();

	@getter ("distance")
	public abstract double getDistance(IScope scope);

	public abstract ITopology getTopology(IScope scope);

	public abstract void setRealObjects(final THashMap<IShape, IShape> realObjects);

	public abstract IShape getRealObject(final Object obj);

	public void setSource(V source);

	public void setTarget(V target);

	public int getGraphVersion();

	public abstract void setGraph(G graph);

}