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

import msi.gama.common.interfaces.IValue;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.GraphEvent.GraphEventType;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;

public class GamaGraph<K, V> implements IGraph<K, V> {

	protected final Map<V, _Vertex<V>> vertexMap;
	protected final Map<K, _Edge<K>> edgeMap;
	protected boolean directed;
	protected boolean edgeBased;

	protected VertexRelationship vertexRelation;
	// protected IScope scope;

	public static int FloydWarshall = 1;
	public static int BellmannFord = 2;
	public static int Djikstra = 3;
	public static int ASTar = 4;

	protected int optimizerType = 1;
	private FloydWarshallShortestPaths optimizer;
	protected boolean verbose;
	
	private LinkedList<IGraphEventListener> listeners = new LinkedList<IGraphEventListener>();
	
	public GamaGraph(final boolean directed) {
		this.directed = directed;
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		edgeBased = false; // TODO  ? (Sam)
		vertexRelation = null;
		
	}
	
	public GamaGraph(final IContainer vertices, final boolean byEdge, final boolean directed) {
		this.directed = directed;
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		edgeBased = byEdge;
		vertexRelation = null;
		if ( byEdge ) {
			buildByEdge(vertices);
		} else {
			buildByVertices(vertices);
		}
	}
	
	public GamaGraph(final IContainer vertices, final boolean byEdge, final boolean directed, final VertexRelationship rel) {
		this.directed = directed;
		vertexMap = new GamaMap();
		edgeMap = new GamaMap();
		edgeBased = byEdge;
		vertexRelation = rel;
		if ( byEdge ) {
			buildByEdge(vertices);
		} else {
			buildByVertices(vertices);
		}
	}

	@Override
	public String toString() {
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
		}
		return "(" + renderedVertices + ", " + renderedEdges + ")";
	}

	protected void buildByVertices(final IContainer<?, V> vertices) {
		for ( V p : vertices ) {
			addVertex(p);
		}
	}

	protected void buildByEdge(final IContainer vertices) {
		for ( Object p : vertices ) {
			addEdge(p);
		}
	}

	protected _Edge<K> getEdge(final Object e) {
		return edgeMap.get(e);
	}

	protected _Vertex<V> getVertex(final Object v) {
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
		GamaPair p = new GamaPair(v1, v2);
		return p;
	}

	@Override
	public boolean addEdge(final Object v1, final Object v2, final Object e) {
		if ( containsEdge(e) ) { return false; }
		addVertex(v1);
		addVertex(v2);
		_Edge<K> edge;
		try {
			edge = newEdge(e, v1, v2);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create edge from " + StringUtils.toGaml(e) + " in graph " +
				this);
			GAMA.reportError(e1);
			return false;
		}
		if ( edge == null ) { return false; }
		edgeMap.put((K) e, edge);
		dispatchEvent(new GraphEvent(this, this, e, null, GraphEventType.EDGE_ADDED ));
		return true;

	}

	protected _Edge<K> newEdge(final Object e, final Object v1, final Object v2)
		throws GamaRuntimeException {
		return new _Edge(this, e, v1, v2);
	}

	protected _Vertex<V> newVertex(final Object v) throws GamaRuntimeException {
		return new _Vertex<V>(this);
	}

	@Override
	public boolean addVertex(final Object v) {
		if ( v == null || containsVertex(v) ) { return false; }
		_Vertex<V> vertex;
		try {
			vertex = newVertex(v);
		} catch (GamaRuntimeException e) {
			e.addContext("Impossible to create vertex from " + StringUtils.toGaml(v) +
				" in graph " + this);
			GAMA.reportError(e);
			return false;
		}
		if ( vertex == null ) { return false; }
		vertexMap.put((V) v, vertex);
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

	public Collection _internalEdgeSet() {
		return edgeMap.values();
	}
	
	public Collection _internalNodesSet() {
		return edgeMap.values();
	}
	
	@Override
	public Set edgesOf(final Object vertex) {
		_Vertex<V> v = getVertex(vertex);
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
		_Vertex<V> v = getVertex(vertex);
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
		_Vertex<V> v = getVertex(vertex);
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
		for ( Object o : vertices ) {
			result = result || removeVertex(o);
		}
		return result;
	}

	@Override
	public boolean removeEdge(final Object e) {
		if ( e == null ) { return false; }
		_Edge<K> edge = getEdge(e);
		if ( edge == null && e instanceof GamaPair ) { return removeEdge(((GamaPair) e).first(),
			((GamaPair) e).last()) != null; }

		if ( edge == null ) { return false; }
		edge.removeFromVerticesAs(e);
		edgeMap.remove(e);
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
		dispatchEvent(new GraphEvent(this, this, null, v,  GraphEventType.VERTEX_REMOVED));
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
	public Set<? extends V> vertexSet() {
		return vertexMap.keySet();
	}

	@Override
	public void setOptimizerType(final String s) {
		if ( "Djikstra".equals(s) ) {
			optimizerType = 3;
		} else if ( "Bellmann".equals(s) ) {
			optimizerType = 2;
		} else if ( "AStar".equals(s) ){
			optimizerType = 4;
		} else {
			optimizerType = 1;
		}
	}

	@Override
	public IValue computeShortestPathBetween(final Object source, final Object target) {
		switch (optimizerType) {
			case 1:
				return FWShortestPath(source, target);
			case 2:
				return BFShortestPath(source, target);
			case 3:
				return DShortestPath(source, target);
			case 4:
				return AShortestPath(source, target);
		}
		return null;
	}

	protected Graph getProxyGraph() {
		return directed ? this : new AsUndirectedGraph(this);
	}

	private IValue DShortestPath(final Object source, final Object target) {
		DijkstraShortestPath<GamaShape, GamaShape> p =
			new DijkstraShortestPath(getProxyGraph(), source, target);
		return pathFromEdges(source, target, new GamaList(p.getPathEdgeList()));
	}

	private IValue BFShortestPath(final Object source, final Object target) {
		BellmanFordShortestPath p = new BellmanFordShortestPath(getProxyGraph(), source);
		return pathFromEdges(source, target, new GamaList(p.getPathEdgeList(target)));
	}

	private IValue FWShortestPath(final Object source, final Object target) {
		if ( optimizer == null ) {
			optimizer = new FloydWarshallShortestPaths(getProxyGraph());
		}
		GraphPath p = optimizer.getShortestPath(source, target);
		return pathFromEdges(source, target, new GamaList(p.getEdgeList()));
	} 
	
	private IValue AShortestPath(final Object source, final Object target) {
		AStarShortestPath p = new AStarShortestPath(this, (IShape)source, (IShape)target);
		return pathFromEdges(source, target, new GamaList(p.getPathEdgeList()));
	}

	/*
	 * In "regular" (non spatial) graphs, we return a list. And a path in spatial graphs. IValue is
	 * the smallest denominator
	 */
	protected IValue pathFromEdges(final Object source, final Object target, final IList edges) {
		return new GamaList(edges);
	}

	@Override
	public IType type() {
		return Types.get(IType.GRAPH);
	}

	@Override
	public IList<V> listValue(final IScope scope) {
		// TODO V�rifier ceci.
		GamaList list = edgeBased ? new GamaList(edgeSet()) : new GamaList(vertexSet());
		return list;
	}

	@Override
	public String stringValue() {
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

	//
	// @Override
	// public String toJava() {
	// return null;
	// }

	@Override
	public GamaMap mapValue(final IScope scope) {
		GamaMap m = new GamaMap();
		for ( Object edge : edgeSet() ) {
			m.add(new GamaPair(getEdgeSource(edge), getEdgeTarget(edge)), edge, null);
		}
		return m;
	}

	@Override
	public Iterator<V> iterator() {
		return listValue(null).iterator();
	}

	@Override
	public void add(final Object value, final Object param) throws GamaRuntimeException {
		add(null, value, param);
	}

	@Override
	public void add(final Object index, final Object value, final Object param)
		throws GamaRuntimeException {
		double weight =
			param == null ? DEFAULT_EDGE_WEIGHT : Cast.asFloat(GAMA.getDefaultScope(), param);
		if ( index == null ) {
			if ( value instanceof GamaPair ) {
				Object v = addEdge(((GamaPair) value).first(), ((GamaPair) value).last());
				setEdgeWeight(v, weight);
			} else {
				addVertex(value);
				setVertexWeight(value, weight);
			}
		} else { // index != null
			if ( index instanceof GamaPair ) {
				addEdge(((GamaPair) index).first(), ((GamaPair) index).last(), value);
				setEdgeWeight(value, weight);
			}
		}
	}

	@Override
	public void clear() {
		removeAllVertices(vertexSet());
	}

	@Override
	public void putAll(final Object value, final Object param) {
		// Nothing to do ?
	}

	@Override
	public Object get(final Object index) {
		if ( index instanceof GamaPair ) { return getEdge(((GamaPair) index).first(),
			((GamaPair) index).last()); }
		if ( containsVertex(index) ) { return new GamaList(edgesOf(index)); }
		if ( containsEdge(index) ) { return new GamaPair(getEdgeSource(index), getEdgeTarget(index)); }
		return null;
	}

	@Override
	public boolean contains(final Object o) {
		return containsVertex(o) || containsEdge(o);
	}

	@Override
	public V first() {
		Iterator it = this.iterator();
		if ( it.hasNext() ) { return (V) it.next(); }
		return null;
	}

	@Override
	public V last() {
		// Solution d�bile. On devrait conserver le dernier entr�.
		return new GamaList<V>(vertexSet()).last(); // Attention a l'ordre
	}

	@Override
	public int length() {
		return edgeBased ? edgeSet().size() : vertexSet().size(); // ??
	}

	@Override
	public V max(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).max(scope);
		// Poids maximum ?
	}

	@Override
	public V min(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).min(scope);
		// Poids minimum ?
	}

	@Override
	public Object product(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).product(scope);
		// Faire le produit de tous les poids ?
	}

	@Override
	public Object sum(final IScope scope) throws GamaRuntimeException {
		return listValue(scope).sum(scope);
		// Somme de tous les poids ?
	}

	@Override
	public boolean isEmpty() {
		return edgeSet().isEmpty() && vertexSet().isEmpty();
	}

	@Override
	public boolean removeAll(final IContainer list) throws GamaRuntimeException {
		boolean result = true;
		for ( Object value : list ) {
			result = removeFirst(value) & result;
		}
		return result;
	}

	@Override
	public boolean removeFirst(final Object value) {
		return removeVertex(value) || removeEdge(value);
	}

	@Override
	public Object removeAt(final Object index) {
		Object e;
		if ( index instanceof GamaPair ) {
			e = getEdge(((GamaPair) index).first(), ((GamaPair) index).last());
		} else {
			e = getEdge(index);
		}
		if ( e == null ) { return null; }
		removeEdge(index);
		return e;
	}

	@Override
	public GamaGraph reverse() {
		GamaGraph g = new GamaGraph(new GamaList(), false, directed);
		Graphs.addGraphReversed(g, this);
		return g;
	}

	@Override
	public void put(final Object index, final Object value, final Object param)
		throws GamaRuntimeException {
		add(index, value, param);
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
	public IValue getCircuit() {
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
	public IGraph copy() {
		GamaGraph g = new GamaGraph(GamaList.EMPTY_LIST, true, directed);
		Graphs.addAllEdges(g, this, this.edgeSet());
		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkIndex(java.lang.Object)
	 */
	@Override
	public boolean checkIndex(final Object index) {
		return index != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkValue(java.lang.Object)
	 */
	@Override
	public boolean checkValue(final Object value) {
		return value != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final Object index, final boolean forAdding) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#isFixedLength()
	 */
	@Override
	public boolean isFixedLength() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(msi.gama.interfaces.IGamaContainer,
	 * java.lang.Object)
	 */
	@Override
	public void addAll(final IContainer value, final Object param) throws GamaRuntimeException {
		if ( value instanceof GamaGraph ) {
			for ( Object o : ((GamaGraph) value).edgeSet() ) {
				addEdge(o);
			}
		} else {
			for ( Object o : value ) {
				this.add(o, param);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IGamaContainer#addAll(java.lang.Object,
	 * msi.gama.interfaces.IGamaContainer, java.lang.Object)
	 */
	@Override
	public void addAll(final Object index, final IContainer value, final Object param)
		throws GamaRuntimeException {
		addAll(value, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.IGraph#setWeights(java.util.Map)
	 */
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
	public V any() {
		if ( vertexMap.isEmpty() ) { return null; }
		V[] array = (V[]) vertexMap.keySet().toArray();
		int i = GAMA.getRandom().between(0, array.length - 1);
		return array[i];
	}

	@Override
	public Boolean isVerbose() {
		return verbose;
	}

	@Override
	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public void addListener(IGraphEventListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);	
		}
			
	}

	@Override
	public void removeListener(IGraphEventListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);	
		}
	}

	@Override
	public void dispatchEvent(GraphEvent event) {
		synchronized (listeners) {
			if (listeners.isEmpty()) return;
			for (IGraphEventListener l : listeners)
				l.receiveEvent(event);	
		}
	}


}
