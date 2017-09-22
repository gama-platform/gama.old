/*********************************************************************************************
 *
 * 'GamaGraph.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.HamiltonianCycle;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.alg.KruskalMinimumSpanningTree;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.VertexPair;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.FloydWarshallShortestPathsGAMA;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.graph.GraphEvent.GraphEventType;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.operators.Strings;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractContainerStatement.EdgeToAdd;
import msi.gaml.statements.AbstractContainerStatement.GraphObjectToAdd;
import msi.gaml.statements.AbstractContainerStatement.NodeToAdd;
import msi.gaml.statements.AbstractContainerStatement.NodesToAdd;
import msi.gaml.types.GamaListType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraph<V, E> implements IGraph<V, E> {

	protected final Map<V, _Vertex<V, E>> vertexMap;
	protected final Map<E, _Edge<V, E>> edgeMap;
	protected boolean directed;
	protected boolean edgeBased;
	protected boolean agentEdge;
	protected final IScope scope;
	protected final IContainerType type;
	protected Map<VertexPair<V>, IList<IList<E>>> shortestPathComputed = null;
	protected VertexRelationship vertexRelation;

	public static int FloydWarshall = 1;
	public static int BellmannFord = 2;
	public static int Djikstra = 3;
	public static int AStar = 4;

	protected boolean saveComputedShortestPaths = true;

	protected ISpecies edgeSpecies;
	protected int optimizerType = Djikstra;
	private FloydWarshallShortestPathsGAMA<V, E> optimizer;

	private Object linkedGraph = null;

	private final LinkedList<IGraphEventListener> listeners = new LinkedList<IGraphEventListener>();

	private final Set<IAgent> generatedEdges = new HashSet<IAgent>();
	protected int version;

	protected ISpecies vertexSpecies;

	public GamaGraph(final IScope scope, final boolean directed, final IType nodeType, final IType vertexType) {
		this.directed = directed;
		vertexMap = new TOrderedHashMap<V, _Vertex<V, E>>();
		edgeMap = new TOrderedHashMap<E, _Edge<V, E>>();
		edgeBased = false;
		vertexRelation = null;
		version = 1;
		agentEdge = false;
		this.scope = scope;
		shortestPathComputed = new ConcurrentHashMap<VertexPair<V>, IList<IList<E>>>();
		type = Types.GRAPH.of(nodeType, vertexType);
	}

	public GamaGraph(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge, final boolean directed,
			final VertexRelationship rel, final ISpecies edgesSpecies, final IType nodeType, final IType edgeType) {
		vertexMap = new TOrderedHashMap();
		edgeMap = new TOrderedHashMap();
		shortestPathComputed = new ConcurrentHashMap<VertexPair<V>, IList<IList<E>>>();
		this.scope = scope;
		// WARNING TODO Verify this
		// IType nodeType = byEdge ? Types.NO_TYPE :
		// edgesOrVertices.getType().getContentType();
		// IType edgeType = byEdge ? edgesOrVertices.getType().getContentType()
		// : Types.NO_TYPE;
		//
		type = Types.GRAPH.of(nodeType, edgeType);
		init(scope, edgesOrVertices, byEdge, directed, rel, edgesSpecies);
	}

	public GamaGraph(final IScope scope, final IType nodeType, final IType vertexType) {
		vertexMap = new TOrderedHashMap();
		edgeMap = new TOrderedHashMap();
		shortestPathComputed = new ConcurrentHashMap<VertexPair<V>, IList<IList<E>>>();
		this.scope = scope;
		type = Types.GRAPH.of(nodeType, vertexType);
	}

	public IScope getScope() {
		return scope;
	}

	protected void init(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge,
			final boolean directed, final VertexRelationship rel, final ISpecies edgesSpecies) {
		this.directed = directed;
		edgeBased = byEdge;
		vertexRelation = rel;
		edgeSpecies = edgesSpecies;
		agentEdge = edgesSpecies != null
				|| byEdge && edgesOrVertices != null && edgesOrVertices.firstValue(scope) instanceof IAgent;
		if (byEdge) {
			buildByEdge(scope, edgesOrVertices);
		} else {
			buildByVertices(scope, edgesOrVertices);
		}
		version = 1;
	}

	protected void init(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge,
			final boolean directed, final VertexRelationship rel, final ISpecies edgesSpecies, final Double tolerance) {
		this.directed = directed;
		edgeBased = byEdge;
		vertexRelation = rel;
		edgeSpecies = edgesSpecies;
		agentEdge = edgesSpecies != null
				|| byEdge && edgesOrVertices != null && edgesOrVertices.firstValue(scope) instanceof IAgent;
		if (byEdge) {
			buildByEdge(scope, edgesOrVertices, tolerance);
		} else {
			buildByVertices(scope, edgesOrVertices);
		}
		version = 1;
	}

	@Override
	public IContainerType getType() {
		return type;
	}

	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();

		// display the list of verticies
		sb.append("graph { \nvertices (").append(vertexSet().size()).append("): ").append("[");
		for (final Object v : vertexSet()) {
			sb.append(v.toString()).append(",");
		}
		sb.append("]").append(Strings.LN);
		sb.append("edges (").append(edgeSet().size()).append("): [").append(Strings.LN);
		// display each edge
		for (final Entry<E, _Edge<V, E>> entry : edgeMap.entrySet()) {
			final E e = entry.getKey();
			final _Edge<V, E> v = entry.getValue();
			sb.append(e.toString()).append(Strings.TAB).append("(").append(v.toString()).append("),")
					.append(Strings.LN);
		}
		sb.append("]\n}");
		/*
		 * old aspect, kept if someone prefers this one. List<String> renderedVertices = new ArrayList<String>();
		 * List<String> renderedEdges = new ArrayList<String>(); StringBuffer sb = new StringBuffer(); for ( Object e :
		 * edgeSet() ) { sb.append(e.toString()).append("=(").append(getEdgeSource(e)).append( ",")
		 * .append(getEdgeTarget(e)).append(")"); renderedEdges.add(sb.toString()); sb.setLength(0); } for ( Object v :
		 * vertexSet() ) { sb.append(v.toString()).append(": in" ).append(incomingEdgesOf(v)).append(" + out")
		 * .append(outgoingEdgesOf(v)); renderedVertices.add(sb.toString()); sb.setLength(0); }
		 */
		return sb.toString();
		// return "(" + renderedVertices + ", " + renderedEdges + ")";
	}

	protected void buildByVertices(final IScope scope, final IContainer<?, E> vertices) {
		for (final E p : vertices.iterable(scope)) {
			addVertex(p);
		}
	}

	protected void buildByEdge(final IScope scope, final IContainer edges) {
		if (edges != null)
			for (final Object p : edges.iterable(scope)) {
				addEdge(p);
				final Object p2 = p instanceof GraphObjectToAdd ? ((GraphObjectToAdd) p).getObject() : p;
				if (p2 instanceof IShape) {
					final _Edge ed = getEdge(p2);
					if (ed != null) {
						ed.setWeight(((IShape) p2).getPerimeter());
					}
				}
			}
	}

	protected void buildByEdge(final IScope scope, final IContainer vertices, final Double tolerance) {
		if (vertices != null)
			for (final Object p : vertices.iterable(scope)) {
				addEdge(p);
				final Object p2 = p instanceof GraphObjectToAdd ? ((GraphObjectToAdd) p).getObject() : p;
				if (p2 instanceof IShape) {
					final _Edge ed = getEdge(p2);
					if (ed != null) {
						ed.setWeight(((IShape) p2).getPerimeter());
					}
				}
			}
	}

	protected void buildByEdge(final IScope scope, final IContainer edges, final IContainer vertices) {}

	public _Edge<V, E> getEdge(final Object e) {
		return edgeMap.get(e);
	}

	public _Vertex<V, E> getVertex(final Object v) {
		return vertexMap.get(v);
	}

	@Override
	public Object addEdge(final Object e) {
		incVersion();

		if (e instanceof GamaPair) {
			final GamaPair p = (GamaPair) e;
			return addEdge(p.first(), p.last());
		} else if (e instanceof GraphObjectToAdd) {
			addValue(scope, (GraphObjectToAdd) e);
			return ((GraphObjectToAdd) e).getObject();
		}
		return addEdge(null, null, e) ? e : null;

	}

	@Override
	public void addValue(final IScope scope, final GraphObjectToAdd value) {
		if (value instanceof EdgeToAdd) {
			final EdgeToAdd edge = (EdgeToAdd) value;
			if (edge.object == null) {
				edge.object = addEdge(edge.source, edge.target);
			}
			addEdge(edge.source, edge.target, edge.object);
			if (edge.weight != null) {
				setEdgeWeight(edge.object, edge.weight);
			}
		} else {
			final NodeToAdd node = (NodeToAdd) value;
			this.addVertex(node.object);
			if (node.weight != null) {
				this.setVertexWeight(node.object, node.weight);
			}
		}

	}

	@Override
	public void addValueAtIndex(final IScope scope, final Object idx, final GraphObjectToAdd value) {
		final GamaPair index = buildIndex(scope, idx);
		final EdgeToAdd edge = new EdgeToAdd(index.key, index.value, null, null);
		if (value instanceof EdgeToAdd) {
			edge.object = ((EdgeToAdd) value).object;
			edge.weight = ((EdgeToAdd) value).weight;
		} else {
			edge.object = value;
		}
		addValue(scope, edge);

		// else, shoud have been taken in consideration by the validator
	}

	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final GraphObjectToAdd value) {
		addValueAtIndex(scope, index, value);
	}

	@Override
	public void addValues(final IScope scope, final IContainer values) {
		if (values instanceof GamaGraph) {
			for (final Object o : ((GamaGraph) values).edgeSet()) {
				addEdge(o);
			}
			return;
		}
		for (final Object o : values.iterable(scope)) {
			if (o instanceof GraphObjectToAdd) {
				addValue(scope, (GraphObjectToAdd) o);
			}
		}

	}

	@Override
	public void setAllValues(final IScope scope, final GraphObjectToAdd value) {
		// Not allowed for graphs ?
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		if (value instanceof EdgeToAdd) {
			final EdgeToAdd edge = (EdgeToAdd) value;
			if (edge.object != null) {
				removeEdge(edge.object);
			} else if (edge.source != null && edge.target != null) {
				removeAllEdges(edge.source, edge.target);
			}
		} else if (value instanceof NodeToAdd) {
			removeVertex(((NodeToAdd) value).object);
		} else if (!removeVertex(value)) {
			removeEdge(value);
		}
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		if (index instanceof GamaPair) {
			final GamaPair p = (GamaPair) index;
			removeAllEdges(p.key, p.value);
		}
	}

	/**
	 * Method removeIndexes()
	 * 
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, ?> index) {
		for (final Object pair : index.iterable(scope)) {
			removeIndex(scope, pair);
		}
	}

	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {
		if (values instanceof IGraph) {
			removeAllEdges(((IGraph) values).edgeSet());
		} else {
			for (final Object o : values.iterable(scope)) {
				removeValue(scope, o);
			}

		}
	}

	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {
		removeValue(scope, value);
	}

	@Override
	public Object addEdge(final Object v1, final Object v2) {
		if (v1 instanceof GamaPair) {
			final GamaPair p = (GamaPair) v1;
			if (addEdge(p.first(), p.last(), v2)) { return v2; }
			return null;
		}
		final Object p = createNewEdgeObjectFromVertices(v1, v2);

		if (addEdge(v1, v2, p)) { return p; }
		return null;
	}

	protected Object createNewEdgeObjectFromVertices(final Object v1, final Object v2) {
		if (edgeSpecies == null) { return generateEdgeObject(v1, v2); }
		final Map<String, Object> map = new TOrderedHashMap();
		final List initVal = new ArrayList<>();
		map.put(IKeyword.SOURCE, v1);
		map.put(IKeyword.TARGET, v2);
		map.put(IKeyword.SHAPE, Creation.link(scope, (IShape) v1, (IShape) v2));
		initVal.add(map);
		return generateEdgeAgent(initVal);
	}

	protected Object generateEdgeObject(final Object v1, final Object v2) {
		return new GamaPair(v1, v2, getType().getKeyType(), getType().getKeyType());
	}

	protected IAgent generateEdgeAgent(final List<Map<String, Object>> attributes) {
		final IAgent agent = scope.getAgent().getPopulationFor(edgeSpecies)
				.createAgents(scope, 1, attributes, false, true).firstValue(scope);
		if (agent != null) {
			generatedEdges.add(agent);
		}
		return agent;
	}

	@Override
	public boolean addEdge(final Object v1, final Object v2, final Object e) {
		if (e == null) { return addEdge(v1, v2) != null; }
		if (containsEdge(e)) { return false; }
		addVertex(v1);
		addVertex(v2);
		_Edge<V, E> edge;
		try {
			edge = newEdge(e, v1, v2);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create edge from " + StringUtils.toGaml(e, false) + " in graph " + this);
			throw e1;
		}
		// if ( edge == null ) { return false; }
		edgeMap.put((E) e, edge);
		dispatchEvent(scope, new GraphEvent(scope, this, this, e, null, GraphEventType.EDGE_ADDED));
		return true;

	}

	protected _Edge<V, E> newEdge(final Object e, final Object v1, final Object v2) throws GamaRuntimeException {
		return new _Edge(this, e, v1, v2);
	}

	protected _Vertex<V, E> newVertex(final Object v) throws GamaRuntimeException {
		return new _Vertex<V, E>(this);
	}

	@Override
	public boolean addVertex(final Object v) {
		if (v instanceof GraphObjectToAdd) {
			if (v instanceof IAgent) {
				if (!this.getVertices().isEmpty() && ((IAgent) v).getSpecies() != vertexSpecies) {
					vertexSpecies = null;
				}
			}
			addValue(scope, (GraphObjectToAdd) v);
			return ((GraphObjectToAdd) v).getObject() != null;
		}
		if (v == null || containsVertex(v)) { return false; }
		_Vertex<V, E> vertex;
		try {
			vertex = newVertex(v);
		} catch (final GamaRuntimeException e) {
			e.addContext("Impossible to create vertex from " + StringUtils.toGaml(v, false) + " in graph " + this);
			throw e;
		}
		// if ( vertex == null ) { return false; }
		vertexMap.put((V) v, vertex);
		dispatchEvent(scope, new GraphEvent(scope, this, this, null, v, GraphEventType.VERTEX_ADDED));
		return true;

	}

	@Override
	public boolean containsEdge(final Object e) {
		return edgeMap.containsKey(e);
	}

	@Override
	public boolean containsEdge(final Object v1, final Object v2) {
		return getEdge(v1, v2) != null || !directed && getEdge(v2, v1) != null;
	}

	@Override
	public boolean containsVertex(final Object v) {
		return vertexMap.containsKey(v);
	}

	@Override
	public Set edgeSet() {
		return edgeMap.keySet();
	}

	@Override
	public Collection _internalEdgeSet() {
		return edgeMap.values();
	}

	@Override
	public Collection _internalNodesSet() {
		return edgeMap.values();
	}

	@Override
	public Map<E, _Edge<V, E>> _internalEdgeMap() {
		return edgeMap;
	}

	@Override
	public Map<V, _Vertex<V, E>> _internalVertexMap() {
		return vertexMap;
	}

	@Override
	public Set edgesOf(final Object vertex) {
		final _Vertex<V, E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : v.getEdges();
	}

	@Override
	public Set getAllEdges(final Object v1, final Object v2) {
		final Set s = new HashSet();
		if (!containsVertex(v1) || !containsVertex(v2)) { return s; }
		s.addAll(getVertex(v1).edgesTo(v2));
		if (!directed) {
			s.addAll(getVertex(v2).edgesTo(v1));
		}
		return s;
	}

	@Override
	public Object getEdge(final Object v1, final Object v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) { return null; }
		final Object o = getVertex(v1).edgeTo(v2);
		return o == null && !directed ? getVertex(v2).edgeTo(v1) : o;
	}

	@Override
	public EdgeFactory getEdgeFactory() {
		return null; // NOT USED
	}

	@Override
	public Object getEdgeSource(final Object e) {
		if (!containsEdge(e)) { return null; }
		return getEdge(e).getSource();
	}

	@Override
	public Object getEdgeTarget(final Object e) {
		if (!containsEdge(e)) { return null; }
		return getEdge(e).getTarget();
	}

	@Override
	public double getEdgeWeight(final Object e) {
		if (!containsEdge(e)) { return WeightedGraph.DEFAULT_EDGE_WEIGHT; }
		return getEdge(e).getWeight();
	}

	@Override
	public double getVertexWeight(final Object v) {
		if (!containsVertex(v)) { return WeightedGraph.DEFAULT_EDGE_WEIGHT; }
		return getVertex(v).getWeight();
	}

	@Override
	public Double getWeightOf(final Object v) {
		if (containsVertex(v)) { return getVertexWeight(v); }
		if (containsEdge(v)) { return getEdgeWeight(v); }
		return null;
	}

	@Override
	public Set incomingEdgesOf(final Object vertex) {
		final _Vertex<V, E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : v.inEdges;
	}

	@Override
	public int inDegreeOf(final Object vertex) {
		return incomingEdgesOf(vertex).size();
	}

	@Override
	public int outDegreeOf(final Object vertex) {
		return outgoingEdgesOf(vertex).size();
	}

	@Override
	public int degreeOf(final Object v) {
		return inDegreeOf(v) + outDegreeOf(v);
	}

	@Override
	public Set outgoingEdgesOf(final Object vertex) {
		final _Vertex<V, E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : v.outEdges;
	}

	@Override
	public boolean removeAllEdges(final Collection edges) {
		boolean result = false;
		for (final Object e : edges) {
			result = result || removeEdge(e);
		}
		return result;
	}

	@Override
	public Set removeAllEdges(final Object v1, final Object v2) {
		final Set result = new HashSet();
		Object edge = removeEdge(v1, v2);
		while (edge != null) {
			result.add(edge);
			edge = removeEdge(v1, v2);
		}
		if (!directed) {
			edge = removeEdge(v2, v1);
			while (edge != null) {
				result.add(edge);
				edge = removeEdge(v2, v1);
			}
		}
		return result;
	}

	@Override
	public boolean removeAllVertices(final Collection vertices) {
		boolean result = false;
		for (final Object o : vertices.toArray()) {
			result = result || removeVertex(o);
		}
		return result;
	}

	@Override
	public boolean removeEdge(final Object e) {
		if (e == null) { return false; }
		final _Edge<V, E> edge = getEdge(e);
		if (edge == null
				&& e instanceof GamaPair) { return removeEdge(((GamaPair) e).first(), ((GamaPair) e).last()) != null; }

		if (edge == null) { return false; }
		incVersion();
		edge.removeFromVerticesAs(e);
		edgeMap.remove(e);
		if (generatedEdges.contains(e)) {
			((IAgent) e).dispose();
		}
		dispatchEvent(scope, new GraphEvent(scope, this, this, e, null, GraphEventType.EDGE_REMOVED));
		return true;
	}

	@Override
	public Object removeEdge(final Object v1, final Object v2) {
		final Object edge = getEdge(v1, v2);
		if (removeEdge(edge)) {
			incVersion();
			return edge;
		}
		return null;

	}

	@Override
	public boolean removeVertex(final Object v) {
		if (!containsVertex(v)) { return false; }
		incVersion();
		final Set edges = edgesOf(v);
		for (final Object e : edges) {
			removeEdge(e);
		}

		vertexMap.remove(v);
		dispatchEvent(scope, new GraphEvent(scope, this, this, null, v, GraphEventType.VERTEX_REMOVED));
		return true;
	}

	@Override
	public void setEdgeWeight(final Object e, final double weight) {
		if (!containsEdge(e)) { return; }
		incVersion();
		getEdge(e).setWeight(weight);
	}

	@Override
	public void setVertexWeight(final Object v, final double weight) {
		if (!containsVertex(v)) { return; }
		incVersion();
		getVertex(v).setWeight(weight);
	}

	@Override
	public Set vertexSet() {
		incVersion();
		return vertexMap.keySet();
	}

	@Override
	public void setOptimizerType(final String s) {
		if ("AStar".equals(s)) {
			optimizerType = 4;
		} else if ("Dijkstra".equals(s)) {
			optimizerType = 3;
		} else if ("Bellmann".equals(s)) {
			optimizerType = 2;
		} else {
			optimizerType = 1;
		}
	}

	// protected IPath<V,E> pathFromEdges(final Object source, final Object
	// target, final IList<E> edges) {
	protected IPath<V, E, IGraph<V, E>> pathFromEdges(final IScope scope, final V source, final V target,
			final IList<E> edges) {
		// return new GamaPath(this, source, target, edges);
		return PathFactory.newInstance(this, source, target, edges);
	}

	@Override
	// public IPath<V,E> computeShortestPathBetween(final Object source, final
	// Object target) {
	public IPath<V, E, IGraph<V, E>> computeShortestPathBetween(final IScope scope, final V source, final V target) {
		return pathFromEdges(scope, source, target, computeBestRouteBetween(scope, source, target));
	}

	@Override
	public IList<E> computeBestRouteBetween(final IScope scope, final V source, final V target) {
		if (source.equals(target))
			return GamaListFactory.create(getType().getContentType());
		switch (optimizerType) {
			case 1:
				if (optimizer == null) {
					optimizer = new FloydWarshallShortestPathsGAMA<V, E>(this);
				}
				final GraphPath<V, E> path = optimizer.getShortestPath(source, target);
				if (path == null) { return GamaListFactory.create(getType().getContentType()); }
				return GamaListFactory.create(scope, getType().getContentType(), path.getEdgeList());
			case 2:
				final VertexPair<V> nodes1 = new VertexPair<V>(source, target);
				final IList<IList<E>> sp1 = shortestPathComputed.get(nodes1);
				IList<E> spl1 = null;
				if (sp1 == null || sp1.isEmpty() || sp1.get(0).isEmpty()) {
					spl1 = GamaListFactory.create(getType().getContentType());
					final BellmanFordShortestPath<V, E> p1 = new BellmanFordShortestPath<V, E>(getProxyGraph(), source);
					final List<E> re = p1.getPathEdgeList(target);
					if (re == null) {
						spl1 = GamaListFactory.create(getType().getContentType());
					} else {
						spl1 = GamaListFactory.create(scope, getType().getContentType(), re);
					}
					if (saveComputedShortestPaths) {
						saveShortestPaths(spl1, source, target);
					}
				} else {
					spl1 = GamaListFactory.create(scope, getType().getContentType(), sp1.get(0));
				}
				return spl1;
			case 3:
				// long t1 = java.lang.System.currentTimeMillis();
				final VertexPair<V> nodes2 = new VertexPair<V>(source, target);
				// System.out.println("nodes2 : " + nodes2);
				final IList<IList<E>> sp2 = shortestPathComputed.get(nodes2);
				IList<E> spl2 = null;

				if (sp2 == null || sp2.isEmpty() || sp2.get(0).isEmpty()) {
					spl2 = GamaListFactory.create(getType().getContentType());

					try {
						final DijkstraShortestPath<GamaShape, GamaShape> p2 =
								new DijkstraShortestPath(getProxyGraph(), source, target);
						final List re = p2.getPathEdgeList();
						if (re == null) {
							spl2 = GamaListFactory.create(getType().getContentType());
						} else {
							spl2 = GamaListFactory.create(scope, getType().getContentType(), re);
						}

					} catch (final IllegalArgumentException e) {
						spl2 = GamaListFactory.create(getType().getContentType());
					}
					if (saveComputedShortestPaths) {
						saveShortestPaths(spl2, source, target);
					}
				} else {
					spl2 = GamaListFactory.create(scope, getType().getContentType(), sp2.get(0));
				}
				// java.lang.System.out.println("DijkstraShortestPath : " +
				// (java.lang.System.currentTimeMillis() - t1
				// ));
				return spl2;
			case 4:
				// t1 = java.lang.System.currentTimeMillis();

				final VertexPair<V> nodes3 = new VertexPair<V>(source, target);
				final IList<IList<E>> sp3 = shortestPathComputed.get(nodes3);
				IList<E> spl3 = null;
				if (sp3 == null || sp3.isEmpty() || sp3.get(0).isEmpty()) {
					spl3 = GamaListFactory.create(getType().getContentType());
					final msi.gama.metamodel.topology.graph.AStar astarAlgo =
							new msi.gama.metamodel.topology.graph.AStar(this, source, target);
					astarAlgo.compute();
					final List re = astarAlgo.getShortestPath();
					if (re == null) {
						spl3 = GamaListFactory.create(getType().getContentType());
					} else {
						spl3 = GamaListFactory.create(scope, getType().getContentType(), re);
					}
					if (saveComputedShortestPaths) {
						saveShortestPaths(spl3, source, target);
					}

				} else {
					spl3 = GamaListFactory.create(scope, getType().getContentType(), sp3.get(0));
				}

				// java.lang.System.out.println("ASTAR : " +
				// (java.lang.System.currentTimeMillis() - t1 ));
				return spl3;

		}
		return GamaListFactory.create(getType().getContentType());

	}

	private void saveShortestPaths(final List<E> edges, final V source, final V target) {
		V s = source;
		final IList<IList<E>> spl = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
		spl.add(GamaListFactory.createWithoutCasting(getType().getContentType(), edges));
		shortestPathComputed.put(new VertexPair<V>(source, target), spl);
		final List<E> edges2 = GamaListFactory.create(scope, getType().getContentType(), edges);
		for (int i = 0; i < edges.size(); i++) {
			final E edge = edges2.remove(0);
			if (edges2.isEmpty()) {
				break;
			}
			// System.out.println("s : " + s + " j : " + j + " i: " + i);
			V nwS = (V) this.getEdgeTarget(edge);
			if (!directed && nwS.equals(s)) {
				nwS = (V) this.getEdgeSource(edge);
			}
			final VertexPair<V> pp = new VertexPair<V>(nwS, target);
			if (!shortestPathComputed.containsKey(pp)) {
				final IList<IList<E>> spl2 = GamaListFactory.create(getType().getContentType());
				spl2.add(GamaListFactory.createWithoutCasting(getType().getContentType(), edges2));
				shortestPathComputed.put(pp, spl2);
			}
			s = nwS;

		}
	}

	@Override
	public IList<IPath<V, E, IGraph<V, E>>> computeKShortestPathsBetween(final IScope scope, final V source,
			final V target, final int k) {
		final IList<IList<E>> pathLists = computeKBestRoutesBetween(scope, source, target, k);
		final IList<IPath<V, E, IGraph<V, E>>> paths = GamaListFactory.create(Types.PATH);

		for (final IList<E> p : pathLists) {
			paths.add(pathFromEdges(scope, source, target, p));
		}
		return paths;
	}

	@Override
	public IList<IList<E>> computeKBestRoutesBetween(final IScope scope, final V source, final V target, final int k) {
		final VertexPair<V> pp = new VertexPair<V>(source, target);
		final IList<IList<E>> paths = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
		final IList<IList<E>> sps = shortestPathComputed.get(pp);
		if (sps != null && sps.size() >= k) {
			for (final IList<E> sp : sps) {
				paths.add(GamaListFactory.create(scope, getType().getContentType(), sp));
			}
		} else {
			final KShortestPaths<V, E> kp = new KShortestPaths<V, E>(getProxyGraph(), source, k);
			final List<GraphPath<V, E>> pathsJGT = kp.getPaths(target);
			final IList<IList<E>> el = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
			for (final GraphPath<V, E> p : pathsJGT) {
				paths.add(GamaListFactory.create(scope, getType().getContentType(), p.getEdgeList()));
				if (saveComputedShortestPaths) {
					el.add(GamaListFactory.create(scope, getType().getContentType(), p.getEdgeList()));
				}
			}
			if (saveComputedShortestPaths) {
				shortestPathComputed.put(pp, el);
			}
		}
		return paths;
	}

	protected Graph<V, E> getProxyGraph() {
		return directed ? this : new AsUndirectedGraph<V, E>(this);
	}

	@Override
	public IList<E> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		// TODO V�rifier ceci.

		return GamaListType.staticCast(scope, edgeSet(), contentsType, false);
		// final GamaList list = edgeBased ? new GamaList(edgeSet()) : new
		// GamaList(vertexSet());
		// return list.listValue(scope, contentsType);
	}

	@Override
	public StreamEx<E> stream(final IScope scope) {
		return StreamEx.<E> of(edgeSet());
	}

	@Override
	public String stringValue(final IScope scope) {
		return toString();
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final boolean copy) {
		// TODO Representation of the graph as a matrix ?
		// TODO Possibility to build an adjacency matrix from this method ?
		return null;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize,
			final boolean copy) {
		// TODO Representation of the graph as a matrix ?
		return null;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return mapValue(null, Types.NO_TYPE, Types.NO_TYPE, false).serialize(includingBuiltIn) + " as graph";
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {
		final GamaMap m = GamaMapFactory.create(Types.PAIR.of(getType().getKeyType(), getType().getKeyType()),
				getType().getContentType());
		// WARNING Does not respect the contract regarding keyType and
		// contentsType
		for (final Object edge : edgeSet()) {
			m.put(new GamaPair(getEdgeSource(edge), getEdgeTarget(edge), getType().getKeyType(),
					getType().getKeyType()), edge);
		}
		return m;
	}

	// @Override
	// public Iterator<E> iterator() {
	// return listValue(null).iterator();
	// }

	@Override
	public List<E> get(final IScope scope, final GamaPair<V, V> index) {
		return GamaListFactory.create(scope, getType().getContentType(), getAllEdges(index.key, index.value));
		// if ( containsVertex(index) ) { return new GamaList(edgesOf(index)); }
		// if ( containsEdge(index) ) { return new
		// GamaPair(getEdgeSource(index), getEdgeTarget(index)); }
		// return null;
	}

	@Override
	public List<E> getFromIndicesList(final IScope scope, final IList<GamaPair<V, V>> indices)
			throws GamaRuntimeException {
		if (indices == null || indices.isEmpty(scope)) { return null; }
		return get(scope, indices.firstValue(scope));
		// Maybe we should consider the case where two indices that represent
		// vertices are passed
		// (instead of a pair).
	}

	@Override
	public boolean contains(final IScope scope, final Object o) {
		// AD: see Issue 918
		return /* containsVertex(o) || */containsEdge(o);
	}

	@Override
	public E firstValue(final IScope scope) {
		return listValue(scope, Types.NO_TYPE, false).firstValue(scope);
	}

	@Override
	public E lastValue(final IScope scope) {
		// Solution d�bile. On devrait conserver le dernier entr�.
		return listValue(scope, Types.NO_TYPE, false).lastValue(scope);// Attention
																		// a
																		// l'ordre
	}

	@Override
	public int length(final IScope scope) {
		return edgeBased ? edgeSet().size() : vertexSet().size(); // ??
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return edgeSet().isEmpty() && vertexSet().isEmpty();
	}

	@Override
	public IContainer reverse(final IScope scope) {
		final GamaGraph g = new GamaGraph(scope, GamaListFactory.create(type.getKeyType()), false, directed,
				vertexRelation, edgeSpecies, type.getKeyType(), type.getContentType());
		Graphs.addGraphReversed(g, this);
		return g;
	}

	@Override
	public IList getEdges() {
		return GamaListFactory.createWithoutCasting(getType().getContentType(), edgeSet());
	}

	@Override
	public IList getVertices() {
		return GamaListFactory.createWithoutCasting(getType().getKeyType(), vertexSet());
	}

	@Override
	public IList getSpanningTree(final IScope scope) {
		final KruskalMinimumSpanningTree tree = new KruskalMinimumSpanningTree(this);
		return GamaListFactory.create(scope, getType().getContentType(), tree.getMinimumSpanningTreeEdgeSet());
	}

	@Override
	public IPath getCircuit(final IScope scope) {
		final SimpleWeightedGraph g = new SimpleWeightedGraph(getEdgeFactory());
		Graphs.addAllEdges(g, this, edgeSet());
		final List vertices = HamiltonianCycle.getApproximateOptimalForCompleteGraph(g);
		final int size = vertices.size();
		final IList edges = GamaListFactory.create(getType().getContentType());
		for (int i = 0; i < size - 1; i++) {
			edges.add(this.getEdge(vertices.get(i), vertices.get(i + 1)));
		}
		return pathFromEdges(scope, null, null, edges);
	}

	@Override
	public Boolean getConnected() {
		ConnectivityInspector c;
		if (directed) {
			c = new ConnectivityInspector((DirectedGraph) this);
		} else {
			c = new ConnectivityInspector((UndirectedGraph) this);
		}
		return c.isGraphConnected();
	}

	@Override
	public Boolean hasCycle() {
		CycleDetector<V, E> c;
		if (directed) {
			c = new CycleDetector(this);
		} else {
			return true;
		}
		return c.detectCycles();
	}

	@Override
	public boolean isDirected() {
		return directed;
	}

	@Override
	public void setDirected(final boolean b) {
		directed = b;
	}

	@Override
	public IGraph copy(final IScope scope) {
		final GamaGraph g = new GamaGraph(scope, GamaListFactory.create(), true, directed, vertexRelation, edgeSpecies,
				type.getKeyType(), type.getContentType());
		Graphs.addAllEdges(g, this, this.edgeSet());
		return g;
	}

	@Override
	public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
		return true;
	}

	@Override
	public void setWeights(final Map w) {
		final Map<Object, Double> weights = w;
		for (final Map.Entry<Object, Double> entry : weights.entrySet()) {
			Object target = entry.getKey();
			if (target instanceof GamaPair) {
				target = getEdge(((GamaPair) target).first(), ((GamaPair) target).last());
				setEdgeWeight(target, Cast.asFloat(scope, entry.getValue()));
			} else {
				if (containsEdge(target)) {
					setEdgeWeight(target, Cast.asFloat(scope, entry.getValue()));
				} else {
					setVertexWeight(target, Cast.asFloat(scope, entry.getValue()));
				}
			}
		}

	}

	/**
	 * @see msi.gama.interfaces.IGamaContainer#any()
	 */
	@Override
	public E anyValue(final IScope scope) {
		if (vertexMap.isEmpty()) { return null; }
		final E[] array = (E[]) vertexMap.keySet().toArray();
		final int i = scope.getRandom().between(0, array.length - 1);
		return array[i];
	}

	@Override
	public void addListener(final IGraphEventListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}

	}

	@Override
	public void removeListener(final IGraphEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void dispatchEvent(final IScope scope, final GraphEvent event) {
		synchronized (listeners) {
			if (listeners.isEmpty()) { return; }
			for (final IGraphEventListener l : listeners) {
				l.receiveEvent(scope, event);
			}
		}
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public void setVersion(final int version) {
		this.version = version;
		shortestPathComputed.clear();
	}

	@Override
	public void incVersion() {
		version++;
		shortestPathComputed.clear();
		linkedGraph = null;
	}

	@Override
	public java.lang.Iterable<E> iterable(final IScope scope) {
		return listValue(scope, Types.NO_TYPE, false);
	}

	@Override
	public double computeWeight(final IPath gamaPath) {
		double result = 0;
		final List l = gamaPath.getEdgeList();
		for (final Object o : l) {
			result += getEdgeWeight(o);
		}
		return result;
	}

	@Override
	public double computeTotalWeight() {
		double result = 0;
		for (final Object o : edgeSet()) {
			result += getEdgeWeight(o);
		}
		for (final Object o : vertexSet()) {
			result += getEdgeWeight(o);
		}
		return result;
	}

	public void reInitPathFinder() {
		optimizer = null;
	}

	public boolean isAgentEdge() {
		return agentEdge;
	}

	@Override
	public boolean isSaveComputedShortestPaths() {
		return saveComputedShortestPaths;
	}

	@Override
	public void setSaveComputedShortestPaths(final boolean saveComputedShortestPaths) {
		this.saveComputedShortestPaths = saveComputedShortestPaths;
	}

	@Override
	public FloydWarshallShortestPathsGAMA<V, E> getOptimizer() {
		return optimizer;
	}

	@Override
	public void setOptimizer(final FloydWarshallShortestPathsGAMA<V, E> optimizer) {
		this.optimizer = optimizer;
	}

	public void loadShortestPaths(final IScope scope, final GamaMatrix matrix) {
		final GamaList<V> vertices = (GamaList<V>) getVertices();
		final int nbvertices = matrix.numCols;
		shortestPathComputed = new ConcurrentHashMap<VertexPair<V>, IList<IList<E>>>();
		final GamaIntMatrix mat = GamaIntMatrix.from(scope, matrix);
		if (optimizerType == 1) {
			optimizer = new FloydWarshallShortestPathsGAMA(this, mat);
			return;
		}

		final Map<Integer, E> edgesVertices = GamaMapFactory.create(Types.INT, getType().getContentType());
		for (int i = 0; i < nbvertices; i++) {
			final V v1 = vertices.get(i);
			for (int j = 0; j < nbvertices; j++) {
				final V v2 = vertices.get(j);
				final VertexPair<V> vv = new VertexPair<V>(v1, v2);
				final IList<E> edges = GamaListFactory.create(getType().getContentType());
				if (v1 == v2) {
					final IList<IList<E>> spl = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
					spl.add(edges);
					shortestPathComputed.put(vv, spl);
					continue;
				}
				V vs = v1;
				int previous = i;
				Integer next = mat.get(scope, j, i);
				if (next == -1) {
					continue;
				}
				if (i == next) {
					final IList<IList<E>> spl = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
					spl.add(edges);
					shortestPathComputed.put(vv, spl);
					continue;
				}
				do {
					final V vn = vertices.get(next);
					final Integer id = previous * nbvertices + next;
					E edge = edgesVertices.get(id);
					if (edge == null) {
						final Set<E> eds = this.getAllEdges(vs, vn);
						for (final E ed : eds) {
							if (edge == null || getEdgeWeight(ed) < getEdgeWeight(edge)) {
								edge = ed;
							}
						}
						edgesVertices.put(id, edge);
					}
					if (edge == null) {
						break;
					}
					edges.add(edge);
					previous = next;
					next = mat.get(scope, j, next);

					vs = vn;
				} while (previous != j);
				final IList<IList<E>> spl = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
				spl.add(edges);
				shortestPathComputed.put(vv, spl);
			}
		}
	}

	public IList getPath(final int M[], final GamaList vertices, final int nbvertices, final Object v1, final Object vt,
			final int i, final int j) {
		// VertexPair vv = new VertexPair(v1, vt);
		final IList<E> edges = GamaListFactory.create(getType().getContentType());
		if (v1 == vt) { return edges; }
		Object vc = vt;
		int previous = j;
		int next = M[j];
		if (j == next || next == -1) { return edges; }
		do {
			final Object vn = vertices.get(next);

			final Set<E> eds = this.getAllEdges(vn, vc);

			E edge = null;
			for (final E ed : eds) {
				if (edge == null || getEdgeWeight(ed) < getEdgeWeight(edge)) {
					edge = ed;
				}
			}
			if (edge == null) {
				break;
			}
			edges.add(0, edge);
			previous = next;
			next = M[next];
			vc = vn;
		} while (previous != i);
		return edges;
	}

	public IList savePaths(final int M[], final GamaList vertices, final int nbvertices, final Object v1, final int i,
			final int t) {
		IList edgesVertices = GamaListFactory.create(getType().getContentType());
		for (int j = 0; j < nbvertices; j++) {
			final IList<E> edges = GamaListFactory.create(getType().getContentType());
			final V vt = (V) vertices.get(j);
			if (v1 == vt) {
				continue;
			}
			Object vc = vt;
			int previous = j;
			int next = M[j];
			if (j == next || next == -1) {
				continue;
			}
			do {
				final Object vn = vertices.get(next);

				final Set<E> eds = this.getAllEdges(vn, vc);

				E edge = null;
				for (final E ed : eds) {
					if (edge == null || getEdgeWeight(ed) < getEdgeWeight(edge)) {
						edge = ed;
					}
				}
				if (edge == null) {
					break;
				}
				edges.add(0, edge);
				previous = next;
				next = M[next];
				vc = vn;
			} while (previous != i);
			final VertexPair vv = new VertexPair(v1, vt);
			if (!shortestPathComputed.containsKey(vv)) {
				final IList<IList<E>> ssp = GamaListFactory.create(Types.LIST.of(getType().getContentType()));
				ssp.add(edges);
				shortestPathComputed.put(vv, ssp);
			}
			if (j == t) {
				edgesVertices = edges;
			}
		}
		return edgesVertices;
	}

	public GamaIntMatrix saveShortestPaths(final IScope scope) {
		final GamaMap<V, Integer> indexVertices = GamaMapFactory.create(getType().getKeyType(), Types.INT);
		final GamaList<V> vertices = (GamaList<V>) getVertices();

		for (int i = 0; i < vertexMap.size(); i++) {
			indexVertices.put(vertices.get(i), i);
		}
		final GamaIntMatrix matrix = new GamaIntMatrix(vertices.size(), vertices.size());
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.size(); j++) {
				matrix.set(scope, j, i, i);
			}
		}
		if (optimizer != null) {
			for (int i = 0; i < vertices.size(); i++) {
				final V v1 = vertices.get(i);
				for (int j = 0; j < vertices.size(); j++) {
					final V v2 = vertices.get(j);
					final GraphPath<V, E> path = optimizer.getShortestPath(v1, v2);
					if (path == null || path.getEdgeList() == null || path.getEdgeList().isEmpty()) {
						continue;
					}
					matrix.set(scope, j, i, nextVertice(scope, path.getEdgeList().get(0), v1, indexVertices, directed));
				}
			}
		} else {
			if (optimizerType == 1) {
				optimizer = new FloydWarshallShortestPathsGAMA(this);
				optimizer.lazyCalculateMatrix();
				for (int i = 0; i < vertexMap.size(); i++) {
					for (int j = 0; j < vertexMap.size(); j++) {
						if (i == j) {
							continue;
						}
						matrix.set(scope, j, i, optimizer.succRecur(i, j));
					}
				}
			} else {
				for (int i = 0; i < vertexMap.size(); i++) {
					final V v1 = vertices.get(i);
					for (int j = 0; j < vertexMap.size(); j++) {
						if (i == j) {
							continue;
						}
						if (matrix.get(scope, j, i) != i) {
							continue;
						}
						final V v2 = vertices.get(j);
						final List edges = computeBestRouteBetween(scope, v1, v2);
						// System.out.println("edges : " + edges);
						if (edges == null) {
							continue;
						}
						V source = v1;
						int s = i;
						for (final Object edge : edges) {
							// System.out.println("s : " + s + " j : " + j + "
							// i: " + i);
							if (s != i && matrix.get(scope, j, s) != s) {
								break;
							}

							V target = (V) this.getEdgeTarget(edge);
							if (!directed && target == source) {
								target = (V) this.getEdgeSource(edge);
							}
							final Integer k = indexVertices.get(scope, target);
							// System.out.println("k : " +k);
							matrix.set(scope, j, s, k);
							s = k;
							source = target;
						}

					}
				}
			}

		}
		return matrix;

	}

	private Integer nextVertice(final IScope scope, final E edge, V source, final GamaMap<V, Integer> indexVertices,
			final boolean isDirected) {
		if (isDirected) { return indexVertices.get(scope, (V) this.getEdgeTarget(edge)); }

		final V target = (V) this.getEdgeTarget(edge);
		if (target != source) {
			// source = target;
			return indexVertices.get(scope, target);
		}
		source = (V) this.getEdgeSource(edge);
		return indexVertices.get(scope, source);
	}

	public Map<VertexPair<V>, IList<IList<E>>> getShortestPathComputed() {
		return shortestPathComputed;
	}

	public IList<E> getShortestPath(final V s, final V t) {
		final VertexPair<V> vp = new VertexPair<V>(s, t);
		final IList<IList<E>> ppc = shortestPathComputed.get(vp);
		if (ppc == null || ppc.isEmpty()) { return null; }
		return ppc.get(0);
	}

	public Map<V, _Vertex<V, E>> getVertexMap() {
		return vertexMap;
	}

	/**
	 * Method buildValue()
	 * 
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public GraphObjectToAdd buildValue(final IScope scope, final Object object) {
		if (object instanceof NodeToAdd) { return new NodeToAdd(
				type.getKeyType().cast(scope, ((NodeToAdd) object).object, null, false), ((NodeToAdd) object).weight); }
		if (object instanceof EdgeToAdd) { return new EdgeToAdd(
				type.getKeyType().cast(scope, ((EdgeToAdd) object).source, null, false),
				type.getKeyType().cast(scope, ((EdgeToAdd) object).target, null, false),
				type.getContentType().cast(scope, ((EdgeToAdd) object).object, null, false),
				((EdgeToAdd) object).weight); }
		return new EdgeToAdd(null, null, type.getContentType().cast(scope, object, null, false), 0.0);
	}

	/**
	 * Method buildValues()
	 * 
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, GraphObjectToAdd> buildValues(final IScope scope, final IContainer objects) {
		final IList list = GamaListFactory.create();
		if (!(objects instanceof NodesToAdd)) {
			for (final Object o : objects.iterable(scope)) {
				list.add(buildValue(scope, o));
			}
		} else {
			for (final Object o : objects.iterable(scope)) {
				list.add(buildValue(scope, new NodeToAdd(o)));
			}
		}
		return list;
	}

	/**
	 * Method buildIndex()
	 * 
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public GamaPair<V, V> buildIndex(final IScope scope, final Object object) {
		return GamaPairType.staticCast(scope, object, type.getKeyType(), type.getContentType(), false);
	}

	@Override
	public IContainer<?, GamaPair<V, V>> buildIndexes(final IScope scope, final IContainer value) {
		final IList<GamaPair<V, V>> result = GamaListFactory.create(Types.PAIR);
		for (final Object o : value.iterable(scope)) {
			result.add(buildIndex(scope, o));
		}
		return result;
	}

	public Object getLinkedGraph() {
		return linkedGraph;
	}

	public void setLinkedGraph(final Object linkedGraph) {
		this.linkedGraph = linkedGraph;
	}

	public GamaFloatMatrix toMatrix(final IScope scope) {
		final int nbVertices = this.getVertices().size();
		if (nbVertices == 0) { return null; }
		final GamaFloatMatrix mat = new GamaFloatMatrix(nbVertices, nbVertices);
		mat.setAllValues(scope, Double.POSITIVE_INFINITY);
		for (int i = 0; i < nbVertices; i++) {
			for (int j = 0; j < nbVertices; j++) {
				if (i == j) {
					mat.set(scope, i, j, 0);
				} else {
					final Object edge = getEdge(getVertices().get(i), getVertices().get(j));
					if (edge != null) {
						mat.set(scope, i, j, getWeightOf(edge));
					}
				}
			}
		}
		return mat;
	}

	@Override
	public ISpecies getVertexSpecies() {
		return vertexSpecies;
	}

	@Override
	public ISpecies getEdgeSpecies() {
		return edgeSpecies;
	}

	public void disposeVertex(final IAgent agent) {
		final Set edgesToModify = edgesOf(agent);
		removeVertex(agent);

		for (final Object obj : edgesToModify) {
			if (obj instanceof IAgent) {
				((IAgent) obj).dispose();
			}
		}
	}

}
