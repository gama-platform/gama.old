/*********************************************************************************************
 * 
 * 
 * 'GamaGraph.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.graph;

import java.util.*;
import java.util.Map.Entry;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GraphEvent.GraphEventType;
import msi.gama.util.matrix.*;
import msi.gama.util.path.*;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractContainerStatement.EdgeToAdd;
import msi.gaml.statements.AbstractContainerStatement.GraphObjectToAdd;
import msi.gaml.statements.AbstractContainerStatement.NodeToAdd;
import msi.gaml.statements.AbstractContainerStatement.NodesToAdd;
import msi.gaml.types.*;
import org.jgrapht.*;
import org.jgrapht.Graphs;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.VertexPair;

public class GamaGraph<V, E> implements IGraph<V, E> {

	protected final GamaMap<V, _Vertex<V, E>> vertexMap;
	protected final GamaMap<E, _Edge<V, E>> edgeMap;
	protected boolean directed;
	protected boolean edgeBased;
	protected boolean agentEdge;
	protected final IScope scope;
	protected Map<VertexPair<V>, GamaList<GamaList<E>>> shortestPathComputed = null;
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

	public GamaGraph(final IScope scope, final boolean directed) {
		this.directed = directed;
		vertexMap = new GamaMap<V, _Vertex<V, E>>();
		edgeMap = new GamaMap<E, _Edge<V, E>>();
		edgeBased = false;
		vertexRelation = null;
		version = 1;
		agentEdge = false;
		this.scope = scope;
		shortestPathComputed = new GamaMap<VertexPair<V>, GamaList<GamaList<E>>>();
	}

	public GamaGraph(final IContainer edgesOrVertices, final boolean byEdge, final boolean directed,
		final VertexRelationship rel, final ISpecies edgesSpecies, final IScope scope) {
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		shortestPathComputed = new GamaMap<VertexPair<V>, GamaList<GamaList<E>>>();
		this.scope = scope;
		init(scope, edgesOrVertices, byEdge, directed, rel, edgesSpecies);
	}

	public GamaGraph(final IScope scope) {
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		shortestPathComputed = new GamaMap<VertexPair<V>, GamaList<GamaList<E>>>();
		this.scope = scope;
	}

	protected void init(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge,
		final boolean directed, final VertexRelationship rel, final ISpecies edgesSpecies) {
		this.directed = directed;
		edgeBased = byEdge;
		vertexRelation = rel;
		edgeSpecies = edgesSpecies;
		agentEdge =
			edgesSpecies != null || byEdge && edgesOrVertices != null &&
				edgesOrVertices.firstValue(scope) instanceof IAgent;
		if ( byEdge ) {
			buildByEdge(scope, edgesOrVertices);
		} else {
			buildByVertices(scope, edgesOrVertices);
		}
		version = 1;
	}

	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();

		// display the list of verticies
		sb.append("graph { \nvertices (").append(vertexSet().size()).append("): ").append("[");
		for ( final Object v : vertexSet() ) {
			sb.append(v.toString()).append(",");
		}
		sb.append("]").append(Strings.LN);
		sb.append("edges (").append(edgeSet().size()).append("): [").append(Strings.LN);
		// display each edge
		for ( final Entry<E, _Edge<V, E>> entry : edgeMap.entrySet() ) {
			final E e = entry.getKey();
			final _Edge<V, E> v = entry.getValue();
			sb.append(e.toString()).append(Strings.TAB).append("(").append(v.toString()).append("),")
				.append(Strings.LN);
		}
		sb.append("]\n}");
		/*
		 * old aspect, kept if someone prefers this one.
		 * List<String> renderedVertices = new ArrayList<String>();
		 * List<String> renderedEdges = new ArrayList<String>();
		 * StringBuffer sb = new StringBuffer();
		 * for ( Object e : edgeSet() ) {
		 * sb.append(e.toString()).append("=(").append(getEdgeSource(e)).append(",")
		 * .append(getEdgeTarget(e)).append(")");
		 * renderedEdges.add(sb.toString());
		 * sb.setLength(0);
		 * }
		 * for ( Object v : vertexSet() ) {
		 * sb.append(v.toString()).append(": in").append(incomingEdgesOf(v)).append(" + out")
		 * .append(outgoingEdgesOf(v));
		 * renderedVertices.add(sb.toString());
		 * sb.setLength(0);
		 * }
		 */
		return sb.toString();
		// return "(" + renderedVertices + ", " + renderedEdges + ")";
	}

	protected void buildByVertices(final IScope scope, final IContainer<?, E> vertices) {
		for ( final E p : vertices.iterable(scope) ) {
			addVertex(p);
		}
	}

	protected void buildByEdge(final IScope scope, final IContainer vertices) {
		for ( final Object p : vertices.iterable(scope) ) {
			addEdge(p);
			Object p2 = p instanceof GraphObjectToAdd ? ((GraphObjectToAdd) p).getObject() : p;
			if ( p2 instanceof IShape ) {
				_Edge ed = getEdge(p2);
				if ( ed != null ) {
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
		if ( e instanceof GamaPair ) {
			final GamaPair p = (GamaPair) e;
			return addEdge(p.first(), p.last());
		} else if ( e instanceof GraphObjectToAdd ) {
			addValue(scope, (GraphObjectToAdd) e);
			return ((GraphObjectToAdd) e).getObject();
		}
		return addEdge(null, null, e) ? e : null;

	}

	@Override
	public void addValue(final IScope scope, final GraphObjectToAdd value) {
		if ( value instanceof EdgeToAdd ) {
			EdgeToAdd edge = (EdgeToAdd) value;
			if ( edge.object == null ) {
				edge.object = addEdge(edge.source, edge.target);
			}
			addEdge(edge.source, edge.target, edge.object);
			if ( edge.weight != null ) {
				setEdgeWeight(edge.object, edge.weight);
			}
		} else {
			NodeToAdd node = (NodeToAdd) value;
			this.addVertex(node.object);
			if ( node.weight != null ) {
				this.setVertexWeight(node.object, node.weight);
			}
		}

	}

	@Override
	public void addValueAtIndex(final IScope scope, final GamaPair<V, V> index, final GraphObjectToAdd value) {

		EdgeToAdd edge = new EdgeToAdd(((GamaPair) index).key, ((GamaPair) index).value, null, null);
		if ( value instanceof EdgeToAdd ) {
			edge.object = ((EdgeToAdd) value).object;
			edge.weight = ((EdgeToAdd) value).weight;
		} else {
			edge.object = value;
		}
		addValue(scope, edge);

		// else, shoud have been taken in consideration by the validator
	}

	@Override
	public void setValueAtIndex(final IScope scope, final GamaPair<V, V> index, final GraphObjectToAdd value) {
		addValueAtIndex(scope, index, value);
	}

	@Override
	public void addVallues(final IScope scope, final IContainer<?, GraphObjectToAdd> values) {
		if ( values instanceof GamaGraph ) {
			for ( final Object o : ((GamaGraph) values).edgeSet() ) {
				addEdge(o);
			}
			return;
		}
		for ( final GraphObjectToAdd o : values.iterable(scope) ) {
			addValue(scope, o);
		}

	}

	@Override
	public void setAllValues(final IScope scope, final GraphObjectToAdd value) {
		// Not allowed for graphs ?
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		if ( value instanceof EdgeToAdd ) {
			EdgeToAdd edge = (EdgeToAdd) value;
			if ( edge.object != null ) {
				removeEdge(edge.object);
			} else if ( edge.source != null && edge.target != null ) {
				removeAllEdges(edge.source, edge.target);
			}
		} else if ( value instanceof NodeToAdd ) {
			removeVertex(((NodeToAdd) value).object);
		} else if ( !removeVertex(value) ) {
			removeEdge(value);
		}
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		if ( index instanceof GamaPair ) {
			GamaPair p = (GamaPair) index;
			removeAllEdges(p.key, p.value);
		}
	}

	/**
	 * Method removeIndexes()
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, Object> index) {
		for ( Object pair : index.iterable(scope) ) {
			removeIndex(scope, pair);
		}
	}

	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {
		if ( values instanceof IGraph ) {
			removeAllEdges(((IGraph) values).edgeSet());
		} else {
			for ( Object o : values.iterable(scope) ) {
				removeValue(scope, o);
			}

		}
	}

	@Override
	public void removeAllOccurencesOfValue(final IScope scope, final Object value) {
		removeValue(scope, value);
	}

	@Override
	public Object addEdge(final Object v1, final Object v2) {
		if ( v1 instanceof GamaPair ) {
			final GamaPair p = (GamaPair) v1;
			if ( addEdge(p.first(), p.last(), v2) ) { return v2; }
			return null;
		}
		final Object p = createNewEdgeObjectFromVertices(v1, v2);

		if ( addEdge(v1, v2, p) ) { return p; }
		return null;
	}

	protected Object createNewEdgeObjectFromVertices(final Object v1, final Object v2) {
		if ( edgeSpecies == null ) { return generateEdgeObject(v1, v2); }
		final Map<String, Object> map = new GamaMap();
		final IList initVal = new GamaList();
		map.put(IKeyword.SOURCE, v1);
		map.put(IKeyword.TARGET, v2);
		map.put(IKeyword.SHAPE, Creation.link(scope, new GamaPair(v1, v2)));
		initVal.add(map);
		return generateEdgeAgent(initVal);
	}

	protected Object generateEdgeObject(final Object v1, final Object v2) {
		return new GamaPair(v1, v2);
	}

	protected IAgent generateEdgeAgent(final List<Map> attributes) {
		final IAgent agent =
			scope.getAgentScope().getPopulationFor(edgeSpecies).createAgents(scope, 1, attributes, false)
				.firstValue(scope);
		if ( agent != null ) {
			generatedEdges.add(agent);
		}
		return agent;
	}

	@Override
	public boolean addEdge(final Object v1, final Object v2, final Object e) {
		if ( e == null ) { return addEdge(v1, v2) != null; }
		if ( containsEdge(e) ) { return false; }
		addVertex(v1);
		addVertex(v2);
		_Edge<V, E> edge;
		try {
			edge = newEdge(e, v1, v2);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create edge from " + StringUtils.toGaml(e) + " in graph " + this);
			throw e1;
		}
		// if ( edge == null ) { return false; }
		edgeMap.put((E) e, edge);
		dispatchEvent(new GraphEvent(scope, this, this, e, null, GraphEventType.EDGE_ADDED));
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
		if ( v instanceof GraphObjectToAdd ) {
			addValue(scope, (GraphObjectToAdd) v);
			return ((GraphObjectToAdd) v).getObject() != null;
		}
		if ( v == null || containsVertex(v) ) { return false; }
		_Vertex<V, E> vertex;
		try {
			vertex = newVertex(v);
		} catch (final GamaRuntimeException e) {
			e.addContext("Impossible to create vertex from " + StringUtils.toGaml(v) + " in graph " + this);
			throw e;
		}
		// if ( vertex == null ) { return false; }
		vertexMap.put((V) v, vertex);
		dispatchEvent(new GraphEvent(scope, this, this, null, v, GraphEventType.VERTEX_ADDED));
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
		if ( !containsVertex(v1) || !containsVertex(v2) ) { return s; }
		s.addAll(getVertex(v1).edgesTo(v2));
		if ( !directed ) {
			s.addAll(getVertex(v2).edgesTo(v1));
		}
		return s;
	}

	@Override
	public Object getEdge(final Object v1, final Object v2) {
		if ( !containsVertex(v1) || !containsVertex(v2) ) { return null; }
		final Object o = getVertex(v1).edgeTo(v2);
		return o == null && !directed ? getVertex(v2).edgeTo(v1) : o;
	}

	@Override
	public EdgeFactory getEdgeFactory() {
		return null; // NOT USED
	}

	@Override
	public Object getEdgeSource(final Object e) {
		if ( !containsEdge(e) ) { return null; }
		return getEdge(e).getSource();
	}

	@Override
	public Object getEdgeTarget(final Object e) {
		if ( !containsEdge(e) ) { return null; }
		return getEdge(e).getTarget();
	}

	@Override
	public double getEdgeWeight(final Object e) {
		if ( !containsEdge(e) ) { return WeightedGraph.DEFAULT_EDGE_WEIGHT; }
		return getEdge(e).getWeight();
	}

	@Override
	public double getVertexWeight(final Object v) {
		if ( !containsVertex(v) ) { return WeightedGraph.DEFAULT_EDGE_WEIGHT; }
		return getVertex(v).getWeight();
	}

	@Override
	public Double getWeightOf(final Object v) {
		if ( containsVertex(v) ) { return getVertexWeight(v); }
		if ( containsEdge(v) ) { return getEdgeWeight(v); }
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
		for ( final Object e : edges ) {
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
		if ( !directed ) {
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
		for ( final Object o : vertices.toArray() ) {
			result = result || removeVertex(o);
		}
		return result;
	}

	@Override
	public boolean removeEdge(final Object e) {
		if ( e == null ) { return false; }
		final _Edge<V, E> edge = getEdge(e);
		if ( edge == null && e instanceof GamaPair ) { return removeEdge(((GamaPair) e).first(), ((GamaPair) e).last()) != null; }

		if ( edge == null ) { return false; }
		edge.removeFromVerticesAs(e);
		edgeMap.remove(e);
		if ( generatedEdges.contains(e) ) {
			((IAgent) e).dispose();
		}
		dispatchEvent(new GraphEvent(scope, this, this, e, null, GraphEventType.EDGE_REMOVED));
		return true;
	}

	@Override
	public Object removeEdge(final Object v1, final Object v2) {
		final Object edge = getEdge(v1, v2);
		if ( removeEdge(edge) ) { return edge; }
		return null;

	}

	@Override
	public boolean removeVertex(final Object v) {
		if ( !containsVertex(v) ) { return false; }
		final Set edges = edgesOf(v);
		for ( final Object e : edges ) {
			removeEdge(e);
		}

		vertexMap.remove(v);
		dispatchEvent(new GraphEvent(scope, this, this, null, v, GraphEventType.VERTEX_REMOVED));
		return true;
	}

	@Override
	public void setEdgeWeight(final Object e, final double weight) {
		if ( !containsEdge(e) ) { return; }
		getEdge(e).setWeight(weight);
	}

	@Override
	public void setVertexWeight(final Object v, final double weight) {
		if ( !containsVertex(v) ) { return; }
		getVertex(v).setWeight(weight);
	}

	@Override
	public Set vertexSet() {
		return vertexMap.keySet();
	}

	@Override
	public void setOptimizerType(final String s) {
		if ( "AStar".equals(s) ) {
			optimizerType = 4;
		} else if ( "Dijkstra".equals(s) ) {
			optimizerType = 3;
		} else if ( "Bellmann".equals(s) ) {
			optimizerType = 2;
		} else {
			optimizerType = 1;
		}
	}

	// protected IPath<V,E> pathFromEdges(final Object source, final Object target, final IList<E> edges) {
	protected IPath<V, E, IGraph<V, E>> pathFromEdges(final IScope scope, final V source, final V target,
		final IList<E> edges) {
		// return new GamaPath(this, source, target, edges);
		return PathFactory.newInstance(this, source, target, edges);
	}

	@Override
	// public IPath<V,E> computeShortestPathBetween(final Object source, final Object target) {
		public
		IPath<V, E, IGraph<V, E>> computeShortestPathBetween(final IScope scope, final V source, final V target) {
		return pathFromEdges(scope, source, target, computeBestRouteBetween(scope, source, target));
	}

	@Override
	public IList<E> computeBestRouteBetween(final IScope scope, final V source, final V target) {

		switch (optimizerType) {
			case 1:
				if ( optimizer == null ) {
					optimizer = new FloydWarshallShortestPathsGAMA<V, E>(this);
				}
				GraphPath<V, E> path = optimizer.getShortestPath(source, target);
				if ( path == null ) { return new GamaList<E>(); }
				return GamaList.from(path.getEdgeList());
			case 2:
				VertexPair<V> nodes1 = new VertexPair<V>(source, target);
				GamaList<GamaList<E>> sp1 = shortestPathComputed.get(nodes1);
				GamaList<E> spl1 = null;
				if ( sp1 == null || sp1.isEmpty() ) {
					spl1 = new GamaList<E>();
					final BellmanFordShortestPath<V, E> p1 = new BellmanFordShortestPath<V, E>(getProxyGraph(), source);
					List<E> re = p1.getPathEdgeList(target);
					if ( re == null ) {
						spl1 = new GamaList<E>();
					} else {
						spl1 = new GamaList<E>(re);
					}
					if ( saveComputedShortestPaths ) {
						saveShortestPaths(spl1, source, target);
					}
				} else {
					spl1 = new GamaList<E>(sp1.get(0));
				}
				return spl1;
			case 3:
				// long t1 = java.lang.System.currentTimeMillis();
				VertexPair<V> nodes2 = new VertexPair<V>(source, target);
				// System.out.println("nodes2 : " + nodes2);
				GamaList<GamaList<E>> sp2 = shortestPathComputed.get(nodes2);
				GamaList<E> spl2 = null;

				if ( sp2 == null || sp2.isEmpty() ) {
					spl2 = new GamaList<E>();

					try {
						final DijkstraShortestPath<GamaShape, GamaShape> p2 =
							new DijkstraShortestPath(getProxyGraph(), source, target);
						List re = p2.getPathEdgeList();
						if ( re == null ) {
							spl2 = new GamaList<E>();
						} else {
							spl2 = new GamaList<E>(re);
						}

					} catch (IllegalArgumentException e) {
						spl2 = new GamaList<E>();
					}
					if ( saveComputedShortestPaths ) {
						saveShortestPaths(spl2, source, target);
					}
				} else {
					spl2 = new GamaList<E>(sp2.get(0));
				}
				// java.lang.System.out.println("DijkstraShortestPath : " + (java.lang.System.currentTimeMillis() - t1
				// ));
				return spl2;
			case 4:
				// t1 = java.lang.System.currentTimeMillis();

				VertexPair<V> nodes3 = new VertexPair<V>(source, target);
				GamaList<GamaList<E>> sp3 = shortestPathComputed.get(nodes3);
				GamaList<E> spl3 = null;
				if ( sp3 == null || sp3.isEmpty() ) {
					spl3 = new GamaList<E>();
					msi.gama.metamodel.topology.graph.AStar astarAlgo =
						new msi.gama.metamodel.topology.graph.AStar(this, source, target);
					astarAlgo.compute();
					spl3 = new GamaList<E>(astarAlgo.getShortestPath());
					if ( saveComputedShortestPaths ) {
						saveShortestPaths(spl3, source, target);
					}

				} else {
					spl3 = new GamaList<E>(sp3.get(0));
				}

				// java.lang.System.out.println("ASTAR : " + (java.lang.System.currentTimeMillis() - t1 ));
				return spl3;

		}
		return new GamaList<E>();

	}

	private void saveShortestPaths(final List<E> edges, final V source, final V target) {
		V s = source;
		GamaList<GamaList<E>> spl = new GamaList<GamaList<E>>();
		spl.add(new GamaList<E>(edges));
		shortestPathComputed.put(new VertexPair<V>(source, target), spl);
		List<E> edges2 = new GamaList<E>(edges);
		for ( E edge : edges ) {
			edges2.remove(0);
			// System.out.println("s : " + s + " j : " + j + " i: " + i);
			V nwS = (V) this.getEdgeTarget(edge);
			if ( !directed && nwS == s ) {
				nwS = (V) this.getEdgeSource(edge);
			}
			VertexPair<V> pp = new VertexPair<V>(nwS, target);
			if ( !shortestPathComputed.containsKey(pp) ) {
				GamaList<GamaList<E>> spl2 = new GamaList<GamaList<E>>();
				spl2.add(new GamaList<E>(edges2));
				shortestPathComputed.put(pp, spl2);
			}
			s = nwS;
			if ( edges2.isEmpty() ) {
				break;
			}
		}
	}

	@Override
	public IList<IPath<V, E, IGraph<V, E>>> computeKShortestPathsBetween(final IScope scope, final V source,
		final V target, final int k) {
		final IList<IList<E>> pathLists = computeKBestRoutesBetween(scope, source, target, k);
		IList<IPath<V, E, IGraph<V, E>>> paths = new GamaList<IPath<V, E, IGraph<V, E>>>();

		for ( IList<E> p : pathLists ) {
			paths.add(pathFromEdges(scope, source, target, p));
		}
		return paths;
	}

	@Override
	public IList<IList<E>> computeKBestRoutesBetween(final IScope scope, final V source, final V target, final int k) {
		VertexPair<V> pp = new VertexPair<V>(source, target);
		IList<IList<E>> paths = new GamaList<IList<E>>();
		GamaList<GamaList<E>> sps = shortestPathComputed.get(pp);
		if ( sps != null && sps.size() >= k ) {
			for ( GamaList<E> sp : sps ) {
				paths.add(new GamaList<E>(sp));
			}
		} else {
			final KShortestPaths<V, E> kp = new KShortestPaths<V, E>(getProxyGraph(), source, k);
			List<GraphPath<V, E>> pathsJGT = kp.getPaths(target);
			GamaList<GamaList<E>> el = new GamaList<GamaList<E>>();
			for ( GraphPath<V, E> p : pathsJGT ) {
				paths.add(new GamaList(p.getEdgeList()));
				if ( saveComputedShortestPaths ) {
					el.add(new GamaList<E>(p.getEdgeList()));
				}
			}
			if ( saveComputedShortestPaths ) {
				shortestPathComputed.put(pp, el);
			}
		}
		return paths;
	}

	protected Graph<V, E> getProxyGraph() {
		return directed ? this : new AsUndirectedGraph<V, E>(this);
	}

	@Override
	public IList<E> listValue(final IScope scope, final IType contentsType) {
		// TODO V�rifier ceci.

		return new GamaList(edgeSet()).listValue(scope, contentsType);

		// final GamaList list = edgeBased ? new GamaList(edgeSet()) : new GamaList(vertexSet());
		// return list.listValue(scope, contentsType);
	}

	@Override
	public String stringValue(final IScope scope) {
		return toString();
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType) {
		// TODO Representation of the graph as a matrix ?
		// TODO Possibility to build an adjacency matrix from this method ?
		return null;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final ILocation preferredSize) {
		// TODO Representation of the graph as a matrix ?
		return null;
	}

	@Override
	public String toGaml() {
		return mapValue(null, Types.NO_TYPE, Types.NO_TYPE).toGaml() + " as graph";
	}

	@Override
	public GamaMap mapValue(final IScope scope, final IType keyType, final IType contentsType) {
		final GamaMap m = new GamaMap();
		// WARNING Does not respect the contract regarding keyType and contentsType
		for ( final Object edge : edgeSet() ) {
			m.put(new GamaPair(getEdgeSource(edge), getEdgeTarget(edge)), edge);
		}
		return m;
	}

	// @Override
	// public Iterator<E> iterator() {
	// return listValue(null).iterator();
	// }

	@Override
	public List<E> get(final IScope scope, final GamaPair<V, V> index) {
		return new GamaList(getAllEdges(index.key, index.value));
		// if ( containsVertex(index) ) { return new GamaList(edgesOf(index)); }
		// if ( containsEdge(index) ) { return new GamaPair(getEdgeSource(index), getEdgeTarget(index)); }
		// return null;
	}

	@Override
	public List<E> getFromIndicesList(final IScope scope, final IList<GamaPair<V, V>> indices)
		throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty(scope) ) { return null; }
		return get(scope, indices.firstValue(scope));
		// Maybe we should consider the case where two indices that represent vertices are passed
		// (instead of a pair).
	}

	@Override
	public boolean contains(final IScope scope, final Object o) {
		// AD: see Issue 918
		return /* containsVertex(o) || */containsEdge(o);
	}

	@Override
	public E firstValue(final IScope scope) {
		return listValue(scope, Types.NO_TYPE).firstValue(scope);
	}

	@Override
	public E lastValue(final IScope scope) {
		// Solution d�bile. On devrait conserver le dernier entr�.
		return listValue(scope, Types.NO_TYPE).lastValue(scope);// Attention a l'ordre
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
	public GamaGraph reverse(final IScope scope) {
		final GamaGraph g = new GamaGraph(new GamaList(), false, directed, vertexRelation, edgeSpecies, scope);
		Graphs.addGraphReversed(g, this);
		return g;
	}

	@Override
	public IList getEdges() {
		return new GamaList(edgeSet());
	}

	@Override
	public IList getVertices() {
		return new GamaList(vertexSet());
	}

	@Override
	public IList getSpanningTree(final IScope scope) {
		final KruskalMinimumSpanningTree tree = new KruskalMinimumSpanningTree(this);
		return new GamaList(tree.getEdgeSet());
	}

	@Override
	public IPath getCircuit(final IScope scope) {
		final SimpleWeightedGraph g = new SimpleWeightedGraph(getEdgeFactory());
		Graphs.addAllEdges(g, this, edgeSet());
		final List vertices = HamiltonianCycle.getApproximateOptimalForCompleteGraph(g);
		final int size = vertices.size();
		final IList edges = new GamaList();
		for ( int i = 0; i < size - 1; i++ ) {
			edges.add(this.getEdge(vertices.get(i), vertices.get(i + 1)));
		}
		return pathFromEdges(scope, null, null, edges);
	}

	@Override
	public Boolean getConnected() {
		ConnectivityInspector c;
		if ( directed ) {
			c = new ConnectivityInspector((DirectedGraph) this);
		} else {
			c = new ConnectivityInspector((UndirectedGraph) this);
		}
		return c.isGraphConnected();
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
		final GamaGraph g = new GamaGraph(GamaList.EMPTY_LIST, true, directed, vertexRelation, edgeSpecies, scope);
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
		for ( final Map.Entry<Object, Double> entry : weights.entrySet() ) {
			Object target = entry.getKey();
			if ( target instanceof GamaPair ) {
				target = getEdge(((GamaPair) target).first(), ((GamaPair) target).last());
				setEdgeWeight(target, Cast.asFloat(scope, entry.getValue()));
			} else {
				if ( containsEdge(target) ) {
					setEdgeWeight(target, Cast.asFloat(scope, entry.getValue()));
				} else {
					setVertexWeight(target,Cast.asFloat(scope,  entry.getValue()));
				}
			}
		}

	}

	/**
	 * @see msi.gama.interfaces.IGamaContainer#any()
	 */
	@Override
	public E anyValue(final IScope scope) {
		if ( vertexMap.isEmpty() ) { return null; }
		final E[] array = (E[]) vertexMap.keySet().toArray();
		final int i = scope.getRandom().between(0, array.length - 1);
		return array[i];
	}

	@Override
	public void addListener(final IGraphEventListener listener) {
		synchronized (listeners) {
			if ( !listeners.contains(listener) ) {
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
	public void dispatchEvent(final GraphEvent event) {
		synchronized (listeners) {
			if ( listeners.isEmpty() ) { return; }
			for ( final IGraphEventListener l : listeners ) {
				l.receiveEvent(event);
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
		return listValue(scope, Types.NO_TYPE);
	}

	@Override
	public double computeWeight(final IPath gamaPath) {
		double result = 0;
		final List l = gamaPath.getEdgeList();
		for ( final Object o : l ) {
			result += getEdgeWeight(o);
		}
		return result;
	}

	@Override
	public double computeTotalWeight() {
		double result = 0;
		for ( final Object o : edgeSet() ) {
			result += getEdgeWeight(o);
		}
		for ( final Object o : vertexSet() ) {
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
		GamaList<V> vertices = (GamaList<V>) getVertices();
		int nbvertices = matrix.numCols;
		shortestPathComputed = new GamaMap<VertexPair<V>, GamaList<GamaList<E>>>();
		GamaIntMatrix mat = GamaIntMatrix.from(scope, matrix);

		Map<Integer, E> edgesVertices = new GamaMap<Integer, E>();
		for ( int i = 0; i < nbvertices; i++ ) {
			V v1 = vertices.get(i);
			for ( int j = 0; j < nbvertices; j++ ) {
				V v2 = vertices.get(j);
				VertexPair<V> vv = new VertexPair<V>(v1, v2);
				GamaList<E> edges = new GamaList<E>();
				if ( v1 == v2 ) {
					GamaList<GamaList<E>> spl = new GamaList<GamaList<E>>();
					spl.add(edges);
					shortestPathComputed.put(vv, spl);
					continue;
				}
				V vs = v1;
				int previous = i;
				Integer next = mat.get(scope, j, i);
				if ( i == next ) {
					GamaList<GamaList<E>> spl = new GamaList<GamaList<E>>();
					spl.add(edges);
					shortestPathComputed.put(vv, spl);
					continue;
				}
				do {
					V vn = vertices.get(next);
					Integer id = previous * nbvertices + next;
					E edge = edgesVertices.get(id);
					if ( edge == null ) {
						Set<E> eds = this.getAllEdges(vs, vn);
						for ( E ed : eds ) {
							if ( edge == null || getEdgeWeight(ed) < getEdgeWeight(edge) ) {
								edge = ed;
							}
						}
						edgesVertices.put(id, edge);
					}
					if ( edge == null ) {
						break;
					}
					edges.add(edge);
					previous = next;
					next = mat.get(scope, j, next);

					vs = vn;
				} while (previous != j);
				GamaList<GamaList<E>> spl = new GamaList<GamaList<E>>();
				spl.add(edges);
				shortestPathComputed.put(vv, spl);
			}
		}
	}

	public IList getPath(final int M[], final GamaList vertices, final int nbvertices, final Object v1,
		final Object vt, final int i, final int j) {
		// VertexPair vv = new VertexPair(v1, vt);
		GamaList<E> edges = new GamaList<E>();
		if ( v1 == vt ) { return new GamaList<E>(); }
		Object vc = vt;
		int previous = j;
		int next = M[j];
		if ( j == next || next == -1 ) { return new GamaList<E>(); }
		do {
			Object vn = vertices.get(next);

			Set<E> eds = this.getAllEdges(vn, vc);

			E edge = null;
			for ( E ed : eds ) {
				if ( edge == null || getEdgeWeight(ed) < getEdgeWeight(edge) ) {
					edge = ed;
				}
			}
			if ( edge == null ) {
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
		IList edgesVertices = new GamaList();
		for ( int j = 0; j < nbvertices; j++ ) {
			GamaList<E> edges = new GamaList<E>();
			V vt = (V) vertices.get(j);
			if ( v1 == vt ) {
				continue;
			}
			Object vc = vt;
			int previous = j;
			int next = M[j];
			if ( j == next || next == -1 ) {
				continue;
			}
			do {
				Object vn = vertices.get(next);

				Set<E> eds = this.getAllEdges(vn, vc);

				E edge = null;
				for ( E ed : eds ) {
					if ( edge == null || getEdgeWeight(ed) < getEdgeWeight(edge) ) {
						edge = ed;
					}
				}
				if ( edge == null ) {
					break;
				}
				edges.add(0, edge);
				previous = next;
				next = M[next];
				vc = vn;
			} while (previous != i);
			VertexPair vv = new VertexPair(v1, vt);
			if ( !shortestPathComputed.containsKey(vv) ) {
				GamaList<GamaList<E>> ssp = new GamaList<GamaList<E>>();
				ssp.add(edges);
				shortestPathComputed.put(vv, ssp);
			}
			if ( j == t ) {
				edgesVertices = edges;
			}
		}
		return edgesVertices;
	}

	public GamaIntMatrix saveShortestPaths(final IScope scope) {
		GamaMap<V, Integer> indexVertices = new GamaMap<V, Integer>();
		GamaList<V> vertices = (GamaList<V>) getVertices();

		for ( int i = 0; i < vertexMap.size(); i++ ) {
			indexVertices.put(vertices.get(i), i);
		}
		GamaIntMatrix matrix = new GamaIntMatrix(vertices.size(), vertices.size());
		for ( int i = 0; i < vertices.size(); i++ ) {
			for ( int j = 0; j < vertices.size(); j++ ) {
				matrix.set(scope, j, i, i);
			}
		}
		if ( optimizer != null ) {
			for ( int i = 0; i < vertices.size(); i++ ) {
				V v1 = vertices.get(i);
				for ( int j = 0; j < vertices.size(); j++ ) {
					V v2 = vertices.get(j);
					GraphPath<V, E> path = optimizer.getShortestPath(v1, v2);
					if ( path == null || path.getEdgeList() == null || path.getEdgeList().isEmpty() ) {
						continue;
					}
					matrix.set(scope, j, i, nextVertice(scope, path.getEdgeList().get(0), v1, indexVertices, directed));
				}
			}
		} else {
			if ( optimizerType == 1 ) {
				optimizer = new FloydWarshallShortestPathsGAMA(getProxyGraph());
				optimizer.lazyCalculateMatrix();
				for ( int i = 0; i < vertexMap.size(); i++ ) {
					for ( int j = 0; j < vertexMap.size(); j++ ) {
						if ( i == j ) {
							continue;
						}
						matrix.set(scope,j, i, optimizer.succRecur(i,j));
					}
				}
			} else {
				for ( int i = 0; i < vertexMap.size(); i++ ) {
					V v1 = vertices.get(i);
					for ( int j = 0; j < vertexMap.size(); j++ ) {
						if ( i == j ) {
							continue;
						}
						if ( matrix.get(scope, j, i) != i ) {
							continue;
						}
						V v2 = vertices.get(j);
						List edges = computeBestRouteBetween(scope, v1, v2);
						// System.out.println("edges : " + edges);
						if ( edges == null ) {
							continue;
						}
						V source = v1;
						int s = i;
						for ( Object edge : edges ) {
							// System.out.println("s : " + s + " j : " + j + " i: " + i);
							if ( s != i && matrix.get(scope, j, s) != s ) {
								break;
							}

							V target = (V) this.getEdgeTarget(edge);
							if ( !directed && target == source ) {
								target = (V) this.getEdgeSource(edge);
							}
							Integer k = indexVertices.get(scope, target);
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
		if ( isDirected ) { return indexVertices.get(scope, (V) this.getEdgeTarget(edge)); }

		V target = (V) this.getEdgeTarget(edge);
		if ( target != source ) {
			// source = target;
			return indexVertices.get(scope, target);
		}
		source = (V) this.getEdgeSource(edge);
		return indexVertices.get(scope, source);
	}

	public Map<VertexPair<V>, GamaList<GamaList<E>>> getShortestPathComputed() {
		return shortestPathComputed;
	}

	public GamaList<E> getShortestPath(final V s, final V t) {
		VertexPair<V> vp = new VertexPair<V>(s, t);
		GamaList<GamaList<E>> ppc = shortestPathComputed.get(vp);
		if ( ppc == null || ppc.isEmpty() ) { return null; }
		return ppc.get(0);
	}

	public Map<V, _Vertex<V, E>> getVertexMap() {
		return vertexMap;
	}

	/**
	 * Method buildValue()
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object, msi.gaml.types.IContainerType)
	 */
	@Override
	public GraphObjectToAdd buildValue(final IScope scope, final Object object, final IContainerType containerType) {
		if ( object instanceof NodeToAdd ) { return new NodeToAdd(containerType.getKeyType().cast(scope,
			((NodeToAdd) object).object, null), ((NodeToAdd) object).weight); }
		if ( object instanceof EdgeToAdd ) { return new EdgeToAdd(containerType.getKeyType().cast(scope,
			((EdgeToAdd) object).source, null), containerType.getKeyType().cast(scope, ((EdgeToAdd) object).target,
			null), containerType.getContentType().cast(scope, ((EdgeToAdd) object).object, null),
			((EdgeToAdd) object).weight); }
		return new EdgeToAdd(null, null, containerType.getContentType().cast(scope, object, null), 0.0);
	}

	/**
	 * Method buildValues()
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer, msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, GraphObjectToAdd> buildValues(final IScope scope, final IContainer objects,
		final IContainerType containerType) {
		IList list = new GamaList();
		if ( !(objects instanceof NodesToAdd) ) {
			for ( Object o : objects.iterable(scope) ) {
				list.add(buildValue(scope, o, containerType));
			}
		} else {
			for ( Object o : objects.iterable(scope) ) {
				list.add(buildValue(scope, new NodeToAdd(o), containerType));
			}
		}
		return list;
	}

	/**
	 * Method buildIndex()
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object, msi.gaml.types.IContainerType)
	 */
	@Override
	public GamaPair<V, V> buildIndex(final IScope scope, final Object object, final IContainerType containerType) {
		return GamaPairType.staticCast(scope, object, containerType.getKeyType(), containerType.getContentType());
	}

	@Override
	public IContainer<?, GamaPair<V, V>> buildIndexes(final IScope scope, final IContainer value,
		final IContainerType containerType) {
		IList<GamaPair<V, V>> result = new GamaList();
		for ( Object o : value.iterable(scope) ) {
			result.add(buildIndex(scope, o, containerType));
		}
		return result;
	}

	public Object getLinkedGraph() {
		return linkedGraph;
	}

	public void setLinkedGraph(final Object linkedGraph) {
		this.linkedGraph = linkedGraph;
	}

}
