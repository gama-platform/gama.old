/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.graph;

import java.util.*;
import java.util.Map.Entry;

import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GraphEvent.GraphEventType;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;
import com.vividsolutions.jts.geom.Coordinate;

public class GamaGraph<V, E> implements IGraph<V, E> {

	protected final Map<E, _Vertex<E>> vertexMap;
	protected final Map<V, _Edge<V>> edgeMap;
	protected boolean directed;
	protected boolean edgeBased;
	protected boolean agentEdge;
	protected IScope scope;

	protected VertexRelationship vertexRelation;
	// protected IScope scope;

	public static int FloydWarshall = 1;
	public static int BellmannFord = 2;
	public static int Djikstra = 3;

	protected ISpecies edgeSpecies;
	protected int optimizerType = Djikstra;
	private FloydWarshallShortestPaths<V, E> optimizer;

	private final LinkedList<IGraphEventListener> listeners = new LinkedList<IGraphEventListener>();

	private final GamaMap verticesBuilt; // only used for optimization purpose of spatial graph
											// building.

	private final Set<IAgent> generatedEdges = new HashSet();
	private int version;

	public GamaGraph(final boolean directed) {
		this.directed = directed;
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		edgeBased = false; 
		vertexRelation = null;
		verticesBuilt = new GamaMap();
		version = 1;
		agentEdge = false;
	}

	public GamaGraph(final IContainer edgesOrVertices, final boolean byEdge, final boolean directed,
		final VertexRelationship rel, final ISpecies edgesSpecies, final IScope scope) {
		this.directed = directed;
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		edgeBased = byEdge;
		vertexRelation = rel;
		edgeSpecies = edgesSpecies;
		verticesBuilt = new GamaMap();
		this.scope = scope;
		agentEdge = edgesSpecies != null || byEdge && edgesOrVertices != null && edgesOrVertices.first(scope) instanceof IAgent;
		if ( byEdge ) {
			buildByEdge(edgesOrVertices);
		} else {
			buildByVertices(edgesOrVertices);
		}
		version = 1;
	}

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		
		// display the list of verticies
		sb.append("graph { \nvertices (").append(vertexSet().size()).append("): ").append("[");
		for ( Object v : vertexSet() ) {
			sb.append(v.toString()).append(",");
		}
		sb.append("]\n");
		sb.append("edges (").append(edgeSet().size()).append("): [\n");
		// display each edge
		for(Entry<V,_Edge<V>> entry : edgeMap.entrySet()) {
		    V e = entry.getKey();
		    _Edge<V> v = entry.getValue();
		    sb.append(e.toString()).append("\t(").append(v.toString()).append("),\n");
		}
		sb.append("]\n}");
		/* old aspect, kept if someone prefers this one.
		List<String> renderedVertices = new ArrayList<String>();
		List<String> renderedEdges = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for ( Object e : edgeSet() ) {
			sb.append(e.toString()).append("=(").append(getEdgeSource(e)).append(",")
				.append(getEdgeTarget(e)).append(")");
			renderedEdges.add(sb.toString());
			sb.setLength(0);
		}
		for ( Object v : vertexSet() ) {
			sb.append(v.toString()).append(": in").append(incomingEdgesOf(v)).append(" + out")
				.append(outgoingEdgesOf(v));
			renderedVertices.add(sb.toString());
			sb.setLength(0);
		}*/
		return sb.toString();
		//return "(" + renderedVertices + ", " + renderedEdges + ")";
	}

	protected void buildByVertices(final IContainer<?, E> vertices) {
		for ( E p : vertices ) {
			addVertex(p);
		}
	}

	protected void buildByEdge(final IContainer vertices) {
		for ( Object p : vertices ) {
			addEdge(p);
		}
	}

	protected _Edge<V> getEdge(final Object e) {
		return edgeMap.get(e);
	}

	protected _Vertex<E> getVertex(final Object v) {
		return vertexMap.get(v);
	}

	@Override
	public Object addEdge(final Object e) {
		if ( e instanceof GamaPair ) {
			GamaPair p = (GamaPair) e;
			return addEdge(p.first(), p.last());
		}
		return addEdge(null, null, e) ? e : null;

	}

	@Override
	public Object addEdge(final Object v1, final Object v2) {
		if ( v1 instanceof GamaPair ) {
			GamaPair p = (GamaPair) v1;
			if ( addEdge(p.first(), p.last(), v2) ) { return v2; }
			return null;
		}
		Object p = createNewEdgeObjectFromVertices(v1, v2);

		if ( addEdge(v1, v2, p) ) { return p; }
		return null;
	}

	protected Object createNewEdgeObjectFromVertices(final Object v1, final Object v2) {
		if ( edgeSpecies == null ) { return generateEdgeObject(v1, v2); }
		Map<String, Object> map = new GamaMap();
		IList initVal = new GamaList();
		map.put(IKeyword.SOURCE, v1);
		map.put(IKeyword.TARGET, v2);
		initVal.add(map);
		return generateEdgeAgent(initVal);
	}

	protected Object generateEdgeObject(final Object v1, final Object v2) {
		return new GamaPair(v1, v2);
	}

	protected IAgent generateEdgeAgent(final List<Map> attributes) {
		IAgent agent =
			scope.getAgentScope().getPopulationFor(edgeSpecies)
				.createAgents(scope, 1, attributes, false).first(scope);
		if ( agent != null ) {
			generatedEdges.add(agent);
		}
		return agent;
	}

	@Override
	public boolean addEdge(final Object v1, final Object v2, final Object e) {
		if ( containsEdge(e) ) { return false; }
		addVertex(v1);
		addVertex(v2);
		_Edge<V> edge;
		try {
			edge = newEdge(e, v1, v2);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create edge from " + StringUtils.toGaml(e) + " in graph " +
				this);
			GAMA.reportError(e1);
			return false;
		}
		if ( edge == null ) { return false; }
		edgeMap.put((V) e, edge);
		dispatchEvent(new GraphEvent(this, this, e, null, GraphEventType.EDGE_ADDED));
		return true;

	}

	protected _Edge<V> newEdge(final Object e, final Object v1, final Object v2)
		throws GamaRuntimeException {
		return new _Edge(this, e, v1, v2);
	}

	protected _Vertex<E> newVertex(final Object v) throws GamaRuntimeException {
		return new _Vertex<E>(this);
	}

	@Override
	public boolean addVertex(final Object v) {
		if ( v == null || containsVertex(v) ) { return false; }
		_Vertex<E> vertex;
		try {
			vertex = newVertex(v);
		} catch (GamaRuntimeException e) {
			e.addContext("Impossible to create vertex from " + StringUtils.toGaml(v) +
				" in graph " + this);
			GAMA.reportError(e);
			return false;
		}
		if ( vertex == null ) { return false; }
		vertexMap.put((E) v, vertex);
		dispatchEvent(new GraphEvent(this, this, null, v, GraphEventType.VERTEX_ADDED));
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
	public Set edgesOf(final Object vertex) {
		_Vertex<E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : v.getEdges();
	}

	@Override
	public Set getAllEdges(final Object v1, final Object v2) {
		Set s = new HashSet();
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
		Object o = getVertex(v1).edgeTo(v2);
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
		return getEdge(e).getWeight(e);
	}

	@Override
	public double getVertexWeight(final Object v) {
		if ( !containsVertex(v) ) { return WeightedGraph.DEFAULT_EDGE_WEIGHT; }
		return getVertex(v).getWeight(v);
	}

	@Override
	public Double getWeightOf(final Object v) {
		if ( containsVertex(v) ) { return getVertexWeight(v); }
		if ( containsEdge(v) ) { return getEdgeWeight(v); }
		return null;
	}

	@Override
	public Set incomingEdgesOf(final Object vertex) {
		_Vertex<E> v = getVertex(vertex);
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
		_Vertex<E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : v.outEdges;
	}

	@Override
	public boolean removeAllEdges(final Collection edges) {
		boolean result = false;
		for ( Object e : edges ) {
			result = result || removeEdge(e);
		}
		return result;
	}

	@Override
	public Set removeAllEdges(final Object v1, final Object v2) {
		Set result = new HashSet();
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
		for ( Object o : vertices.toArray() ) {
			result = result || removeVertex(o);
		}
		return result;
	}

	@Override
	public boolean removeEdge(final Object e) {
		if ( e == null ) { return false; }
		_Edge<V> edge = getEdge(e);
		if ( edge == null && e instanceof GamaPair ) { return removeEdge(((GamaPair) e).first(),
			((GamaPair) e).last()) != null; }

		if ( edge == null ) { return false; }
		edge.removeFromVerticesAs(e);
		edgeMap.remove(e);
		if ( generatedEdges.contains(e) ) {
			((IAgent) e).die();
		}
		dispatchEvent(new GraphEvent(this, this, e, null, GraphEventType.EDGE_REMOVED));
		return true;
	}

	@Override
	public Object removeEdge(final Object v1, final Object v2) {
		Object edge = getEdge(v1, v2);
		if ( removeEdge(edge) ) { return edge; }
		return null;

	}

	@Override
	public boolean removeVertex(final Object v) {
		if ( !containsVertex(v) ) { return false; }
		Set edges = edgesOf(v);
		for ( Object e : edges ) {
			removeEdge(e);
		}

		vertexMap.remove(v);
		dispatchEvent(new GraphEvent(this, this, null, v, GraphEventType.VERTEX_REMOVED));
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
		if ( "Djikstra".equals(s) ) {
			optimizerType = 3;
		} else if ( "Bellmann".equals(s) ) {
			optimizerType = 2;
		} else {
			optimizerType = 1;
		}
	}
	
	
	protected IPath pathFromEdges(final Object source, final Object target, final IList edges) {
		return null;//new GamaPath(this, source, target, edges);
	}
	
	@Override
	public IPath computeShortestPathBetween(final Object source, final Object target) {
		return pathFromEdges(source, target, computeBestRouteBetween(source, target));
	}

	@Override
	public IList<IShape> computeBestRouteBetween(final Object source, final Object target) {
		switch (optimizerType) {
			case 1:
				if ( optimizer == null ) {
					optimizer = new FloydWarshallShortestPaths(this);
				}
				return new GamaList<IShape>(optimizer.getShortestPath((V)source, (V)target).getEdgeList());
			case 2:
				BellmanFordShortestPath p1 = new BellmanFordShortestPath(getProxyGraph(), source);
				return new GamaList<IShape>(p1.getPathEdgeList(target));
			case 3:
				DijkstraShortestPath<GamaShape, GamaShape> p2 =
					new DijkstraShortestPath(getProxyGraph(), source, target);
				return new GamaList<IShape>(p2.getPathEdgeList());
		}
		return new GamaList<IShape>();

	}
	protected Graph getProxyGraph() {
		return directed ? this : new AsUndirectedGraph(this);
	}

	
	@Override
	public IList<E> listValue(final IScope scope) {
		// TODO V�rifier ceci.
		GamaList list = edgeBased ? new GamaList(edgeSet()) : new GamaList(vertexSet());
		return list;
	}

	@Override
	public String stringValue(IScope scope) {
		return toString();
	}

	@Override
	public IMatrix matrixValue(final IScope scope) {
		// TODO Representation of the graph as a matrix ?
		return null;
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) {
		// TODO Representation of the graph as a matrix ?
		return null;
	}

	@Override
	public String toGaml() {
		return mapValue(null).toGaml() + " as graph";
	}

	@Override
	public GamaMap mapValue(final IScope scope) {
		GamaMap m = new GamaMap();
		for ( Object edge : edgeSet() ) {
			m.add(scope, new GamaPair(getEdgeSource(edge), getEdgeTarget(edge)), edge, null, false,
				false);
		}
		return m;
	}

	@Override
	public Iterator<E> iterator() {
		return listValue(null).iterator();
	}

	@Override
	public void add(IScope scope, final Object index, final Object value, final Object param,
		boolean all, boolean add) throws GamaRuntimeException {
		double weight = param == null ? DEFAULT_EDGE_WEIGHT : Cast.asFloat(scope, param);
		if ( index == null ) {
			if ( all ) {
				if ( value instanceof GamaGraph ) {
					for ( Object o : ((GamaGraph) value).edgeSet() ) {
						addEdge(o);
					}
				} else if ( value instanceof IContainer ) {
					for ( Object o : (IContainer) value ) {
						this.add(scope, null, o, param, false, true);
					}
				} else { // value != container
					// TODO Runtime exception
				}
			} else if ( value instanceof GamaPair ) {
				Object v = addEdge(((GamaPair) value).getKey(), ((GamaPair) value).getValue());
				setEdgeWeight(v, weight);
			} else {
				addVertex(value);
				setVertexWeight(value, weight);
			}
		} else { // index != null
			if ( index instanceof GamaPair ) {
				addEdge(((GamaPair) index).getKey(), ((GamaPair) index).getValue(), value);
				setEdgeWeight(value, weight);
			}
		}
	}

	@Override
	public Object get(final IScope scope, final Object index) {
		if ( index instanceof GamaPair ) { return getEdge(((GamaPair) index).first(),
			((GamaPair) index).last()); }
		if ( containsVertex(index) ) { return new GamaList(edgesOf(index)); }
		if ( containsEdge(index) ) { return new GamaPair(getEdgeSource(index), getEdgeTarget(index)); }
		return null;
	}

	@Override
	public Object getFromIndicesList(final IScope scope, final IList indices)
		throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty(scope) ) { return null; }
		return get(scope, indices.first(scope));
		// Maybe we should consider the case where two indices that represent vertices are passed
		// (instead of a pair).
	}

	@Override
	public boolean contains(final IScope scope, final Object o) {
		return containsVertex(o) || containsEdge(o);
	}

	@Override
	public E first(final IScope scope) {
		Iterator it = this.iterator();
		if ( it.hasNext() ) { return (E) it.next(); }
		return null;
	}

	@Override
	public E last(final IScope scope) {
		// Solution d�bile. On devrait conserver le dernier entr�.
		return new GamaList<E>(vertexSet()).last(scope); // Attention a l'ordre
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
	public void remove(IScope scope, final Object index, final Object value, final boolean all) {
		if ( index == null ) {
			if ( all ) {
				if ( value instanceof IContainer ) {
					for ( Object obj : (IContainer) value ) {
						remove(scope, null, obj, true);
					}
				} else if ( value != null ) {
					remove(scope, null, value, false);
				} else {
					vertexSet().clear();
				}
			} else if ( !removeVertex(value) ) {
				removeEdge(value);
			}
		} else {
			// TODO if value != null ?
			// EX: remove edge1 at: v1::v2 in case of several edges between vertices
			removeEdge(index);
		}
	}

	@Override
	public GamaGraph reverse(final IScope scope) {
		GamaGraph g =
			new GamaGraph(new GamaList(), false, directed, vertexRelation, edgeSpecies, scope);
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
	public IList getSpanningTree() {
		KruskalMinimumSpanningTree tree = new KruskalMinimumSpanningTree(this);
		return new GamaList(tree.getEdgeSet());
	}

	@Override
	public IPath getCircuit() {
		SimpleWeightedGraph g = new SimpleWeightedGraph(getEdgeFactory());
		Graphs.addAllEdges(g, this, edgeSet());
		List vertices = HamiltonianCycle.getApproximateOptimalForCompleteGraph(g);
		int size = vertices.size();
		IList edges = new GamaList();
		for ( int i = 0; i < size - 1; i++ ) {
			edges.add(this.getEdge(vertices.get(i), vertices.get(i + 1)));
		}
		return pathFromEdges(null, null, edges);
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
	public IGraph copy(IScope scope) {
		GamaGraph g =
			new GamaGraph(GamaList.EMPTY_LIST, true, directed, vertexRelation, edgeSpecies, scope);
		Graphs.addAllEdges(g, this, this.edgeSet());
		return g;
	}

	@Override
	public boolean checkBounds(final Object index, final boolean forAdding) {
		return true;
	}

	@Override
	public void setWeights(final Map w) {
		Map<Object, Double> weights = w;
		for ( Map.Entry<Object, Double> entry : weights.entrySet() ) {
			Object target = entry.getKey();
			if ( target instanceof GamaPair ) {
				target = getEdge(((GamaPair) target).first(), ((GamaPair) target).last());
				setEdgeWeight(target, entry.getValue());
			} else {
				if ( containsEdge(target) ) {
					setEdgeWeight(target, entry.getValue());
				} else {
					setVertexWeight(target, entry.getValue());
				}
			}
		}

	}

	/**
	 * @see msi.gama.interfaces.IGamaContainer#any()
	 */
	@Override
	public E any(final IScope scope) {
		if ( vertexMap.isEmpty() ) { return null; }
		E[] array = (E[]) vertexMap.keySet().toArray();
		int i = GAMA.getRandom().between(0, array.length - 1);
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
			for ( IGraphEventListener l : listeners ) {
				l.receiveEvent(event);
			}
		}
	}

	public void addBuiltVertex(final IShape vertex) {
		verticesBuilt.put(vertex.getLocation().hashCode(), vertex);
	}

	public boolean containsBuiltVertex(final IShape vertex) {
		return verticesBuilt.contains(scope, vertex.getLocation().hashCode());
	}

	public IShape getBuiltVertex(final Coordinate vertex) {
		return (IShape) verticesBuilt.get(vertex.hashCode());
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public void incVersion() {
		version++;
	}

	@Override
	public Iterable<E> iterable(final IScope scope) {
		return listValue(scope);
	}
	
	public double computeWeight(final IPath gamaPath) {
		double result = 0;
		List l = gamaPath.getEdgeList();
		for ( Object o : l ) {
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
}
