/*******************************************************************************************************
 *
 * GamaGraph.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;
import org.jgrapht.alg.tour.PalmerHamiltonianCycle;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.SimpleWeightedGraph;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonGamlObject;
import msi.gama.util.file.json.JsonValue;
import msi.gama.util.graph.GraphEvent.GraphEventType;
import msi.gama.util.graph.loader.GamaGraphMLEdgeImporter;
import msi.gama.util.graph.loader.GamaGraphMLNodeImporter;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Graphs.EdgeToAdd;
import msi.gaml.operators.Graphs.GraphObjectToAdd;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Creation;
import msi.gaml.operators.Strings;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaListType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;

/**
 * The Class GamaGraph.
 *
 * @param <V>
 *            the value type
 * @param <E>
 *            the element type
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGraph<V, E> implements IGraph<V, E> {

	@Override
	public JsonValue serializeToJson(final Json json) {
		JsonGamlObject object = json.typedObject(getGamlType(), "directed", directed, "vertices", vertexMap, "edges",
				edgeMap, "agentEdge", agentEdge);
		if (edgeSpecies != null) { object.add("edgeSpecies", edgeSpecies); }
		if (vertexSpecies != null) { object.add("vertexSpecies", vertexSpecies); }
		// Make pathComputer IJsonable ? to add it here ?
		return object;
	}

	/** The path computer. */
	public PathComputer pathComputer;

	/** The vertex map. */
	protected final Map<V, _Vertex<V, E>> vertexMap;

	/** The edge map. */
	protected final Map<E, _Edge<V, E>> edgeMap;

	/** The directed. */
	protected boolean directed;

	/** The agent edge. */
	protected boolean agentEdge;

	/** The graph scope. */
	protected final IScope graphScope;

	/** The type. */
	protected final IContainerType type;

	/** The vertex relation. */
	protected VertexRelationship vertexRelation;

	/** The default node weight. */
	protected static double DEFAULT_NODE_WEIGHT = 0.0;

	/** The edge species. */
	protected ISpecies edgeSpecies;
	/** The listeners. */
	private final LinkedList<IGraphEventListener> listeners = new LinkedList<>();

	/** The generated edges. */
	private final Set<IAgent> generatedEdges = new LinkedHashSet<>();

	/** The vertex species. */
	protected ISpecies vertexSpecies;

	/**
	 * Instantiates a new gama graph.
	 *
	 * @param scope
	 *            the scope
	 * @param directed
	 *            the directed
	 * @param nodeType
	 *            the node type
	 * @param vertexType
	 *            the vertex type
	 */
	public GamaGraph(final IScope scope, final boolean directed, final IType nodeType, final IType vertexType) {
		this.directed = directed;
		vertexMap = GamaMapFactory.create();
		edgeMap = GamaMapFactory.create();
		// edgeBased = false;
		vertexRelation = null;
		agentEdge = false;
		this.graphScope = scope;
		type = Types.GRAPH.of(nodeType, vertexType);
	}

	/**
	 * Instantiates a new gama graph.
	 *
	 * @param scope
	 *            the scope
	 * @param edgesOrVertices
	 *            the edges or vertices
	 * @param byEdge
	 *            the by edge
	 * @param directed
	 *            the directed
	 * @param rel
	 *            the rel
	 * @param edgesSpecies
	 *            the edges species
	 * @param nodeType
	 *            the node type
	 * @param edgeType
	 *            the edge type
	 */
	public GamaGraph(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge, final boolean directed,
			final boolean uniqueEdge, final VertexRelationship rel, final ISpecies edgesSpecies, final IType nodeType,
			final IType edgeType) {
		vertexMap = GamaMapFactory.create();
		edgeMap = GamaMapFactory.create();
		this.graphScope = scope;
		// WARNING TODO Verify this
		// IType nodeType = byEdge ? Types.NO_TYPE :
		// edgesOrVertices.getType().getContentType();
		// IType edgeType = byEdge ? edgesOrVertices.getType().getContentType()
		// : Types.NO_TYPE;
		//
		type = Types.GRAPH.of(nodeType, edgeType);
		init(scope, edgesOrVertices, byEdge, directed, uniqueEdge, rel, edgesSpecies);
	}

	/**
	 * Instantiates a new gama graph.
	 *
	 * @param scope
	 *            the scope
	 * @param nodeType
	 *            the node type
	 * @param edgeType
	 *            the edge type
	 */
	public GamaGraph(final IScope scope, final IType nodeType, final IType edgeType) {
		vertexMap = GamaMapFactory.create();
		edgeMap = GamaMapFactory.create();
		this.graphScope = scope;
		type = Types.GRAPH.of(nodeType, edgeType);
	}

	/**
	 * Instantiates a new gama graph.
	 *
	 * @param scope
	 *            the scope
	 * @param graph
	 *            the graph
	 * @param nodeS
	 *            the node S
	 * @param edgeS
	 *            the edge S
	 */
	public GamaGraph(final IScope scope, final AbstractBaseGraph<?, DefaultEdge> graph, final ISpecies nodeS,
			final ISpecies edgeS) {
		this(scope, graph, nodeS, edgeS, null, null);
	}

	/**
	 * Instantiates a new gama graph.
	 *
	 * @param scope
	 *            the scope
	 * @param graph
	 *            the graph
	 * @param nodes
	 *            the nodes
	 */
	public GamaGraph(final IScope scope, final AbstractBaseGraph<?, DefaultEdge> graph,
			final GamaMap<?, IShape> nodes) {
		this(scope, nodes == null || nodes.isEmpty() ? Types.GEOMETRY
				: nodes.getValues().get(0) instanceof IAgent ? Types.AGENT : Types.GEOMETRY, Types.GEOMETRY);
		if (nodes != null) {
			for (IShape v : nodes.getValues()) { addVertex(v); }
			for (DefaultEdge e : graph.edgeSet()) {
				Object s = graph.getEdgeSource(e);
				Object t = graph.getEdgeTarget(e);
				IShape sg = nodes.get(s);
				IShape tg = nodes.get(t);
				IList<IShape> points = GamaListFactory.create();
				points.add(sg.getLocation());
				points.add(tg.getLocation());
				IShape eg = Creation.line(scope, points);
				setEdgeWeight(eg, graph.getEdgeWeight(e));
				addEdge(sg, tg, eg);
			}
		}
	}

	/**
	 * Instantiates a new gama graph, with a specified node and edge attributes to store attributes read in the graph
	 * file.
	 *
	 * @param scope
	 *            the scope
	 * @param graph
	 *            the graph
	 * @param nodeS
	 *            the species of the nodes in the created GAMA graph
	 * @param edgeS
	 *            the species of the edges in the created GAMA graph
	 * @param nodeAttr
	 *            the name of the attribute in nodeS species, that will contain the attributes read in the graph file
	 * @param edgeAttr
	 *            the name of the attribute in edgeS species, that will contain the attributes read in the graph file
	 */
	public GamaGraph(final IScope scope, final AbstractBaseGraph<?, DefaultEdge> graph, final ISpecies nodeS,
			final ISpecies edgeS, final String nodeAttr, final String edgeAttr) {
		this(scope, nodeS == null ? Types.STRING : Types.AGENT, edgeS == null ? Types.STRING : Types.AGENT);
		Map<String, IAgent> verticesAg = GamaMapFactory.create();
		for (Object v : graph.vertexSet()) {
			if (nodeS == null) {
				addVertex(v.toString());
			} else {
				IList atts = GamaListFactory.create();
				final IList<IAgent> listAgt =
						nodeS.getPopulation(scope).createAgents(scope, 1, atts, false, false, null);
				IAgent ag = listAgt.get(0);
				if (v != null) {
					ag.setName(v.toString());
					if (ag.hasAttribute(nodeAttr) && v instanceof GamaGraphMLNodeImporter) {
						ag.setAttribute(nodeAttr, GamaMapFactory.create(scope, Types.STRING, Types.STRING,
								((GamaGraphMLNodeImporter) v).getAttributes()));
					}
					addVertex(ag);
					verticesAg.put(v.toString(), ag);
				}
			}
		}
		for (DefaultEdge e : graph.edgeSet()) {
			Object s = graph.getEdgeSource(e);
			Object t = graph.getEdgeTarget(e);

			if (edgeS == null) {
				if (nodeS == null) {
					// addEdge(s.toString(), t.toString(), e);
					String the_edge = new Pair(s.toString(), t.toString()).toString();
					addEdge(s.toString(), t.toString(), the_edge); // (null : null)
					setEdgeWeight(the_edge, graph.getEdgeWeight(e));
				} else {
					addEdge(s, t, e);
					setEdgeWeight(e, graph.getEdgeWeight(e));
				}
				// setEdgeWeight(e, graph.getEdgeWeight(e));
			} else {
				IList atts = GamaListFactory.create();
				final IList<IAgent> listAgt =
						edgeS.getPopulation(scope).createAgents(scope, 1, atts, false, true, null);
				IAgent ag = listAgt.get(0);
				if (e != null) {
					ag.setName(ag.getSpeciesName() + ag.getIndex());
					if (ag.hasAttribute(edgeAttr) && e instanceof GamaGraphMLEdgeImporter) {
						ag.setAttribute(edgeAttr, GamaMapFactory.create(scope, Types.STRING, Types.STRING,
								((GamaGraphMLEdgeImporter) e).getAttributes()));
					}
				}

				if (nodeS != null) {
					IAgent n1 = verticesAg.get(s.toString());
					IAgent n2 = verticesAg.get(t.toString());
					addEdge(n1, n2, ag);
					ag.setGeometry(Spatial.Creation.link(scope, n1, n2));
				} else {
					addEdge(s, t, ag);
				}

				setEdgeWeight(ag, graph.getEdgeWeight(e));
			}

		}
	}

	/**
	 * Instantiates a new gama graph.
	 *
	 * @param scope
	 *            the scope
	 * @param graph
	 *            the graph
	 * @param nodes
	 *            the list of nodes
	 * @param edgeS
	 *            the species of the edges
	 */
	public GamaGraph(final IScope scope, final AbstractBaseGraph<String, DefaultEdge> graph, final IList nodes,
			final ISpecies edgeS) {
		this(scope, Types.get(nodes.get(0).getClass()), edgeS == null ? Types.STRING : Types.AGENT);
		Map<String, Object> verticesAg = GamaMapFactory.create();
		for (Object v : graph.vertexSet()) {
			Object d = nodes.get(Integer.parseInt(v.toString()));
			addVertex(d);
			verticesAg.put(v.toString(), d);
		}
		for (DefaultEdge e : graph.edgeSet()) {
			Object s = graph.getEdgeSource(e);
			Object t = graph.getEdgeTarget(e);

			if (edgeS == null) {
				addEdge(s, t, e);
				setEdgeWeight(e, graph.getEdgeWeight(e));
			} else {
				IList atts = GamaListFactory.create();
				final IList<IAgent> listAgt =
						edgeS.getPopulation(scope).createAgents(scope, 1, atts, false, true, null);
				IAgent ag = listAgt.get(0);
				if (e != null) { ag.setName(e.toString()); }

				Object n1 = verticesAg.get(s.toString());
				Object n2 = verticesAg.get(t.toString());
				addEdge(n1, n2, ag);
				if (n1 instanceof IShape) { ag.setGeometry(Spatial.Creation.link(scope, (IShape) n1, (IShape) n2)); }

				setEdgeWeight(ag, graph.getEdgeWeight(e));
			}

		}
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public IScope getScope() { return graphScope; }

	/**
	 * Inits the.
	 *
	 * @param scope
	 *            the scope
	 * @param edgesOrVertices
	 *            the edges or vertices
	 * @param byEdge
	 *            the by edge
	 * @param directed
	 *            the directed
	 * @param rel
	 *            the rel
	 * @param edgesSpecies
	 *            the edges species
	 */
	protected void init(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge,
			final boolean directed, final boolean uniqueEdge, final VertexRelationship rel,
			final ISpecies edgesSpecies) {
		this.directed = directed;
		// edgeBased = byEdge;
		vertexRelation = rel;
		edgeSpecies = edgesSpecies;
		agentEdge = edgesSpecies != null
				|| byEdge && edgesOrVertices != null && edgesOrVertices.firstValue(scope) instanceof IAgent;
		if (byEdge) {
			buildByEdge(scope, edgesOrVertices);
		} else {
			buildByVertices(scope, edgesOrVertices, uniqueEdge);
		}
	}

	/**
	 * Inits the.
	 *
	 * @param scope
	 *            the scope
	 * @param edgesOrVertices
	 *            the edges or vertices
	 * @param byEdge
	 *            the by edge
	 * @param directed
	 *            the directed
	 * @param rel
	 *            the rel
	 * @param edgesSpecies
	 *            the edges species
	 * @param tolerance
	 *            the tolerance
	 */
	protected void init(final IScope scope, final IContainer edgesOrVertices, final boolean byEdge,
			final boolean directed, final boolean uniqueEdge, final VertexRelationship rel, final ISpecies edgesSpecies,
			final Double tolerance) {
		this.directed = directed;
		// edgeBased = byEdge;
		vertexRelation = rel;
		edgeSpecies = edgesSpecies;
		agentEdge = edgesSpecies != null
				|| byEdge && edgesOrVertices != null && edgesOrVertices.firstValue(scope) instanceof IAgent;
		if (byEdge) {
			buildByEdge(scope, edgesOrVertices, tolerance);
		} else {
			buildByVertices(scope, edgesOrVertices, uniqueEdge);
		}
	}

	@Override
	public IContainerType getGamlType() { return type; }

	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();

		// display the list of verticies
		sb.append("graph { \nvertices (").append(vertexSet().size()).append("): ").append("[");
		for (final Object v : vertexSet()) { sb.append(v.toString()).append(","); }
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

	/**
	 * Builds the by vertices.
	 *
	 * @param scope
	 *            the scope
	 * @param vertices
	 *            the vertices
	 */
	protected void buildByVertices(final IScope scope, final IContainer<?, E> vertices, final boolean uniqueEdge) {
		for (final E p : vertices.iterable(scope)) { addVertex(p); }
	}

	/**
	 * Builds the by edge.
	 *
	 * @param scope
	 *            the scope
	 * @param edges
	 *            the edges
	 */
	protected void buildByEdge(final IScope scope, final IContainer edges) {
		if (edges != null) {
			for (final Object p : edges.iterable(scope)) {
				addEdge(p);
				final Object p2 = p instanceof GraphObjectToAdd ? ((GraphObjectToAdd) p).getObject() : p;
				if (p2 instanceof IShape) {
					final _Edge ed = getEdge(p2);
					if (ed != null) { ed.setWeight(((IShape) p2).getPerimeter()); }
				}
			}
		}
	}

	/**
	 * Builds the by edge.
	 *
	 * @param scope
	 *            the scope
	 * @param vertices
	 *            the vertices
	 * @param tolerance
	 *            the tolerance
	 */
	protected void buildByEdge(final IScope scope, final IContainer vertices, final Double tolerance) {
		if (vertices != null) {
			for (final Object p : vertices.iterable(scope)) {
				addEdge(p);
				final Object p2 = p instanceof GraphObjectToAdd ? ((GraphObjectToAdd) p).getObject() : p;
				if (p2 instanceof IShape) {
					final _Edge ed = getEdge(p2);
					if (ed != null) { ed.setWeight(((IShape) p2).getPerimeter()); }
				}
			}
		}
	}

	/**
	 * Builds the by edge.
	 *
	 * @param scope
	 *            the scope
	 * @param edges
	 *            the edges
	 * @param vertices
	 *            the vertices
	 */
	protected void buildByEdge(final IScope scope, final IContainer edges, final IContainer vertices) {}

	/**
	 * Gets the edge.
	 *
	 * @param e
	 *            the e
	 * @return the edge
	 */
	public _Edge<V, E> getEdge(final Object e) {
		return edgeMap.get(e);
	}

	/**
	 * Gets the vertex.
	 *
	 * @param v
	 *            the v
	 * @return the vertex
	 */
	public _Vertex<V, E> getVertex(final Object v) {
		return vertexMap.get(v);
	}

	@Override
	public Object addEdge(final Object e) {
		getPathComputer().incVersion();

		if (e instanceof GamaPair p) return addEdge(p.first(), p.last());
		if (e instanceof GraphObjectToAdd) {
			addValue(graphScope, (GraphObjectToAdd) e);
			return ((GraphObjectToAdd) e).getObject();
		}
		return addEdge(null, null, e) ? e : null;

	}

	@Override
	public void addValue(final IScope scope, final msi.gaml.operators.Graphs.GraphObjectToAdd value) {
		if (value instanceof msi.gaml.operators.Graphs.EdgeToAdd edge) {
			if (edge.object == null) { edge.object = addEdge(edge.source, edge.target); }
			addEdge(edge.source, edge.target, edge.object);
			if (edge.weight != null) { setEdgeWeight(edge.object, edge.weight); }
		} else {
			final msi.gaml.operators.Graphs.NodeToAdd node = (msi.gaml.operators.Graphs.NodeToAdd) value;
			this.addVertex(node.object);
			if (node.weight != null) { this.setVertexWeight(node.object, node.weight); }
		}

	}

	@Override
	public void addValueAtIndex(final IScope scope, final Object idx,
			final msi.gaml.operators.Graphs.GraphObjectToAdd value) {
		final GamaPair index = buildIndex(scope, idx);
		final EdgeToAdd edge = new EdgeToAdd(index.key, index.value, null, (Double) null);
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
	public void setValueAtIndex(final IScope scope, final Object index,
			final msi.gaml.operators.Graphs.GraphObjectToAdd value) {
		addValueAtIndex(scope, index, value);
	}

	@Override
	public void addValues(final IScope scope, final Object index, final IContainer values) {
		// Index is not used here as it does not make sense for graphs (see #2985)
		if (values instanceof GamaGraph) {
			for (final Object o : ((GamaGraph) values).edgeSet()) { addEdge(o); }
		} else {
			for (final Object o : values.iterable(scope)) {
				if (o instanceof msi.gaml.operators.Graphs.GraphObjectToAdd) {
					addValue(scope, (msi.gaml.operators.Graphs.GraphObjectToAdd) o);
				}
			}
		}

	}

	@Override
	public void setAllValues(final IScope scope, final msi.gaml.operators.Graphs.GraphObjectToAdd value) {
		// Not allowed for graphs ?
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		if (value instanceof msi.gaml.operators.Graphs.EdgeToAdd edge) {
			if (edge.object != null) {
				removeEdge(edge.object);
			} else if (edge.source != null && edge.target != null) { removeAllEdges(edge.source, edge.target); }
		} else if (value instanceof msi.gaml.operators.Graphs.NodeToAdd) {
			removeVertex(((msi.gaml.operators.Graphs.NodeToAdd) value).object);
		} else if (!removeVertex(value)) { removeEdge(value); }
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		if (index instanceof GamaPair p) { removeAllEdges(p.key, p.value); }
	}

	/**
	 * Method removeIndexes()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, ?> index) {
		for (final Object pair : index.iterable(scope)) { removeIndex(scope, pair); }
	}

	@Override
	public void removeValues(final IScope scope, final IContainer<?, ?> values) {
		if (values instanceof IGraph) {
			removeAllEdges(((IGraph) values).edgeSet());
		} else {
			for (final Object o : values.iterable(scope)) { removeValue(scope, o); }

		}
	}

	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {
		removeValue(scope, value);
	}

	@Override
	public Object addEdge(final Object v1, final Object v2) {
		if (v1 instanceof GamaPair p) {
			if (addEdge(p.first(), p.last(), v2)) return v2;
			return null;
		}
		final Object p = createNewEdgeObjectFromVertices(v1, v2);

		if (addEdge(v1, v2, p)) return p;
		return null;
	}

	/**
	 * Creates the new edge object from vertices.
	 *
	 * @param v1
	 *            the v 1
	 * @param v2
	 *            the v 2
	 * @return the object
	 */
	protected Object createNewEdgeObjectFromVertices(final Object v1, final Object v2) {
		if (getEdgeSpecies() == null) return generateEdgeObject(v1, v2);
		final IMap<String, Object> map = GamaMapFactory.create();
		final List initVal = new ArrayList<>();
		map.put(IKeyword.SOURCE, v1);
		map.put(IKeyword.TARGET, v2);
		map.put(IKeyword.SHAPE, Creation.link(graphScope, (IShape) v1, (IShape) v2));
		initVal.add(map);
		return generateEdgeAgent(initVal);
	}

	/**
	 * Generate edge object.
	 *
	 * @param v1
	 *            the v 1
	 * @param v2
	 *            the v 2
	 * @return the object
	 */
	protected Object generateEdgeObject(final Object v1, final Object v2) {
		return new GamaPair(v1, v2, getGamlType().getKeyType(), getGamlType().getKeyType());
	}

	/**
	 * Generate edge agent.
	 *
	 * @param attributes
	 *            the attributes
	 * @return the i agent
	 */
	protected IAgent generateEdgeAgent(final List<Map<String, Object>> attributes) {
		final IAgent agent = graphScope.getAgent().getPopulationFor(getEdgeSpecies())
				.createAgents(graphScope, 1, attributes, false, true).firstValue(graphScope);
		if (agent != null) { generatedEdges.add(agent); }
		return agent;
	}

	@Override
	public boolean addEdge(final Object v1, final Object v2, final Object e) {
		if (e == null) return addEdge(v1, v2) != null;
		if (containsEdge(e)) return false;
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
		dispatchEvent(graphScope, new GraphEvent(graphScope, this, this, e, null, GraphEventType.EDGE_ADDED));
		return true;

	}

	/**
	 * New edge.
	 *
	 * @param e
	 *            the e
	 * @param v1
	 *            the v 1
	 * @param v2
	 *            the v 2
	 * @return the edge
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected _Edge<V, E> newEdge(final Object e, final Object v1, final Object v2) throws GamaRuntimeException {
		return new _Edge(this, e, v1, v2);
	}

	/**
	 * New vertex.
	 *
	 * @param v
	 *            the v
	 * @return the vertex
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	protected _Vertex<V, E> newVertex(final Object v) throws GamaRuntimeException {
		return new _Vertex<>(this);
	}

	@Override
	public boolean addVertex(final Object v) {
		if (v instanceof msi.gaml.operators.Graphs.GraphObjectToAdd) {
			if (v instanceof IAgent && !this.getVertices().isEmpty() && ((IAgent) v).getSpecies() != vertexSpecies) {
				vertexSpecies = null;
			}
			addValue(graphScope, (msi.gaml.operators.Graphs.GraphObjectToAdd) v);
			return ((msi.gaml.operators.Graphs.GraphObjectToAdd) v).getObject() != null;
		}
		if (v == null || containsVertex(v)) return false;
		_Vertex<V, E> vertex;
		try {
			vertex = newVertex(v);
		} catch (final GamaRuntimeException e) {
			e.addContext("Impossible to create vertex from " + StringUtils.toGaml(v, false) + " in graph " + this);
			throw e;
		}
		// if ( vertex == null ) { return false; }
		vertexMap.put((V) v, vertex);
		dispatchEvent(graphScope, new GraphEvent(graphScope, this, this, null, v, GraphEventType.VERTEX_ADDED));
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
	//
	// @Override
	// public Collection _internalEdgeSet() {
	// return edgeMap.values();
	// }

	// @Override
	// public Collection _internalNodesSet() {
	// return edgeMap.values();
	// }

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
		final Set s = new LinkedHashSet();
		if (!containsVertex(v1) || !containsVertex(v2)) return s;
		s.addAll(getVertex(v1).edgesTo(v2));
		if (!directed) { s.addAll(getVertex(v2).edgesTo(v1)); }
		return s;
	}

	@Override
	public Object getEdge(final Object v1, final Object v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) return null;
		final Object o = getVertex(v1).edgeTo(v2);
		return o == null && !directed ? getVertex(v2).edgeTo(v1) : o;
	}

	@Override
	public Object getEdgeSource(final Object e) {
		if (!containsEdge(e)) return null;
		return getEdge(e).getSource();
	}

	@Override
	public Object getEdgeTarget(final Object e) {
		if (!containsEdge(e)) return null;
		return getEdge(e).getTarget();
	}

	@Override
	public double getEdgeWeight(final Object e) {
		if (!containsEdge(e)) return Graph.DEFAULT_EDGE_WEIGHT;
		return getEdge(e).getWeight();
	}

	@Override
	public double getVertexWeight(final Object v) {
		if (!containsVertex(v)) return DEFAULT_NODE_WEIGHT;
		return getVertex(v).getWeight();
	}

	@Override
	public Double getWeightOf(final Object v) {
		if (containsVertex(v)) return getVertexWeight(v);
		if (containsEdge(v)) return getEdgeWeight(v);
		return null;
	}

	@Override
	public Set incomingEdgesOf(final Object vertex) {
		final _Vertex<V, E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : isDirected() ? v.inEdges : v.getEdges();
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
		return isDirected() ? inDegreeOf(v) + outDegreeOf(v) : inDegreeOf(v);
	}

	@Override
	public Set outgoingEdgesOf(final Object vertex) {
		final _Vertex<V, E> v = getVertex(vertex);
		return v == null ? Collections.EMPTY_SET : isDirected() ? v.outEdges : v.getEdges();
	}

	@Override
	public boolean removeAllEdges(final Collection edges) {
		boolean result = false;
		for (final Object e : edges) { result = result || removeEdge(e); }
		return result;
	}

	@Override
	public Set removeAllEdges(final Object v1, final Object v2) {
		final Set result = new LinkedHashSet();
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
		for (final Object o : vertices.toArray()) { result = result || removeVertex(o); }
		return result;
	}

	@Override
	public boolean removeEdge(final Object e) {
		if (e == null) return false;
		final _Edge<V, E> edge = getEdge(e);
		if (edge == null && e instanceof GamaPair)
			return removeEdge(((GamaPair) e).first(), ((GamaPair) e).last()) != null;

		if (edge == null) return false;
		getPathComputer().incVersion();
		edge.removeFromVerticesAs(e);
		edgeMap.remove(e);
		if (generatedEdges.contains(e)) { ((IAgent) e).dispose(); }
		dispatchEvent(graphScope, new GraphEvent(graphScope, this, this, e, null, GraphEventType.EDGE_REMOVED));
		return true;
	}

	@Override
	public Object removeEdge(final Object v1, final Object v2) {
		final Object edge = getEdge(v1, v2);
		if (removeEdge(edge)) {
			getPathComputer().incVersion();
			return edge;
		}
		return null;

	}

	@Override
	public boolean removeVertex(final Object v) {
		if (!containsVertex(v)) return false;
		getPathComputer().incVersion();
		final Set edges = edgesOf(v);
		for (final Object e : edges) { removeEdge(e); }

		vertexMap.remove(v);
		dispatchEvent(graphScope, new GraphEvent(graphScope, this, this, null, v, GraphEventType.VERTEX_REMOVED));
		return true;
	}

	@Override
	public void setEdgeWeight(final Object e, final double weight) {
		if (!containsEdge(e)) return;
		getPathComputer().incVersion();
		getEdge(e).setWeight(weight);
	}

	@Override
	public void setVertexWeight(final Object v, final double weight) {
		if (!containsVertex(v)) return;
		getPathComputer().incVersion();
		getVertex(v).setWeight(weight);
	}

	@Override
	public Set vertexSet() {
		getPathComputer().incVersion();
		return vertexMap.keySet();
	}

	/**
	 * Path from edges.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param edges
	 *            the edges
	 * @return the i path
	 */
	protected IPath<V, E, IGraph<V, E>> pathFromEdges(final IScope scope, final V source, final V target,
			final IList<E> edges) {
		return PathFactory.newInstance(this, source, target, edges);
	}

	@Override
	public IList<E> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		return GamaListType.staticCast(scope, edgeSet(), contentsType, false);
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
		return this.toMatrix(scope);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final IType contentsType, final GamaPoint preferredSize,
			final boolean copy) {
		return this.toMatrix(scope);
	}

	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		return mapValue(null, Types.NO_TYPE, Types.NO_TYPE, false).serializeToGaml(includingBuiltIn) + " as graph";
	}

	@Override
	public IMap mapValue(final IScope scope, final IType keyType, final IType contentsType, final boolean copy) {
		final IMap m = GamaMapFactory.create(Types.PAIR.of(getGamlType().getKeyType(), getGamlType().getKeyType()),
				getGamlType().getContentType());
		// WARNING Does not respect the contract regarding keyType and
		// contentsType
		for (final Object edge : edgeSet()) {
			m.put(new GamaPair(getEdgeSource(edge), getEdgeTarget(edge), getGamlType().getKeyType(),
					getGamlType().getKeyType()), edge);
		}
		return m;
	}

	@Override
	public List<E> get(final IScope scope, final GamaPair<V, V> index) {
		return GamaListFactory.create(scope, getGamlType().getContentType(), getAllEdges(index.key, index.value));
	}

	@Override
	public List<E> getFromIndicesList(final IScope scope, final IList<GamaPair<V, V>> indices)
			throws GamaRuntimeException {
		if (indices == null || indices.isEmpty(scope)) return null;
		return get(scope, indices.firstValue(scope));
		// Maybe we should consider the case where two indices that represent
		// vertices are passed
		// (instead of a pair).
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
		return listValue(scope, Types.NO_TYPE, false).length(scope);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return edgeSet().isEmpty() && vertexSet().isEmpty();
	}

	@Override
	public IContainer reverse(final IScope scope) {
		final GamaGraph g = new GamaGraph(scope, GamaListFactory.create(type.getKeyType()), false, directed, false,
				vertexRelation, edgeSpecies, type.getKeyType(), type.getContentType());
		Graphs.addGraphReversed(g, this);
		return g;
	}

	@Override
	public IList getEdges() { return GamaListFactory.wrap(getGamlType().getContentType(), edgeSet()); }

	@Override
	public IList getVertices() { return GamaListFactory.wrap(getGamlType().getKeyType(), vertexSet()); }

	@Override
	public IList getSpanningTree(final IScope scope) {
		final KruskalMinimumSpanningTree tree = new KruskalMinimumSpanningTree(this);
		return GamaListFactory.create(scope, getGamlType().getContentType(), tree.getSpanningTree().getEdges());
	}

	@Override
	public IPath getCircuit(final IScope scope) {
		final SimpleWeightedGraph g = new SimpleWeightedGraph<V, E>(null, null);
		Graphs.addAllEdges(g, this, edgeSet());
		HamiltonianCycleAlgorithmBase hamilton = new PalmerHamiltonianCycle();
		final List vertices = hamilton.getTour(this).getVertexList();
		final int size = vertices.size();
		final IList edges = GamaListFactory.create(getGamlType().getContentType());
		for (int i = 0; i < size - 1; i++) { edges.add(this.getEdge(vertices.get(i), vertices.get(i + 1))); }
		return pathFromEdges(scope, (V) edges.get(0), (V) edges.get(edges.size() - 1), edges);
	}

	@Override
	public Boolean getConnected() {
		ConnectivityInspector<V, E> c = new ConnectivityInspector(this);
		return c.isConnected();
	}

	@Override
	public Boolean hasCycle() {
		CycleDetector<V, E> c;
		if (!directed) return true;
		c = new CycleDetector(this);
		return c.detectCycles();
	}

	@Override
	public boolean isDirected() { return directed; }

	@Override
	public void setDirected(final boolean b) { directed = b; }

	@Override
	public IGraph copy(final IScope scope) {
		final GamaGraph g = new GamaGraph(scope, GamaListFactory.EMPTY_LIST, true, directed, false, vertexRelation,
				edgeSpecies, type.getKeyType(), type.getContentType());

		Graphs.addAllVertices(g, this.getVertices());
		Graphs.addAllEdges(g, this, this.edgeSet());
		for (Object obj : getVertices()) { g.setVertexWeight(obj, getWeightOf(obj)); }
		for (Object obj : getEdges()) { g.setEdgeWeight(obj, getWeightOf(obj)); }
		return g;
	}

	// @Override
	// public boolean checkBounds(final IScope scope, final Object index, final boolean forAdding) {
	// return true;
	// }

	@Override
	public void setWeights(final Map w) {
		final Map<Object, Double> weights = w;
		for (final Map.Entry<Object, Double> entry : weights.entrySet()) {
			Object target = entry.getKey();
			if (target instanceof GamaPair) {
				target = getEdge(((GamaPair) target).first(), ((GamaPair) target).last());
				setEdgeWeight(target, Cast.asFloat(graphScope, entry.getValue()));
			} else if (containsEdge(target)) {
				setEdgeWeight(target, Cast.asFloat(graphScope, entry.getValue()));
			} else {
				setVertexWeight(target, Cast.asFloat(graphScope, entry.getValue()));
			}
		}

	}

	/**
	 * @see msi.gama.interfaces.IGamaContainer#any()
	 */
	@Override
	public E anyValue(final IScope scope) {
		if (vertexMap.isEmpty()) return null;
		final E[] array = (E[]) vertexMap.keySet().toArray();
		final int i = scope.getRandom().between(0, array.length - 1);
		return array[i];
	}

	@Override
	public void addListener(final IGraphEventListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) { listeners.add(listener); }
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
			if (listeners.isEmpty()) return;
			for (final IGraphEventListener l : listeners) { l.receiveEvent(scope, event); }
		}
	}

	@Override
	public java.lang.Iterable<E> iterable(final IScope scope) {
		return listValue(scope, Types.NO_TYPE, false);
	}

	@Override
	public double computeWeight(final IPath gamaPath) {
		double result = 0;
		final List l = gamaPath.getEdgeList();
		for (final Object o : l) { result += getEdgeWeight(o); }
		return result;
	}

	@Override
	public double computeTotalWeight() {
		double result = 0;
		for (final Object o : edgeSet()) { result += getEdgeWeight(o); }
		for (final Object o : vertexSet()) { result += getVertexWeight(o); }
		return result;
	}

	/**
	 * Checks if is agent edge.
	 *
	 * @return true, if is agent edge
	 */
	public boolean isAgentEdge() { return agentEdge; }

	/**
	 * Gets the path.
	 *
	 * @param M
	 *            the m
	 * @param vertices
	 *            the vertices
	 * @param nbvertices
	 *            the nbvertices
	 * @param v1
	 *            the v 1
	 * @param vt
	 *            the vt
	 * @param i
	 *            the i
	 * @param j
	 *            the j
	 * @return the path
	 */
	public IList getPath(final int M[], final IList vertices, final int nbvertices, final Object v1, final Object vt,
			final int i, final int j) {
		// VertexPair vv = new VertexPair(v1, vt);
		final IList<E> edges = GamaListFactory.create(getGamlType().getContentType());
		if (v1 == vt) return edges;
		Object vc = vt;
		int previous;
		int next = M[j];
		if (j == next || next == -1) return edges;
		do {
			final Object vn = vertices.get(next);

			final Set<E> eds = this.getAllEdges(vn, vc);

			E edge = null;
			for (final E ed : eds) { if (edge == null || getEdgeWeight(ed) < getEdgeWeight(edge)) { edge = ed; } }
			if (edge == null) { break; }
			edges.add(0, edge);
			previous = next;
			next = M[next];
			vc = vn;
		} while (previous != i);
		return edges;
	}

	/**
	 * Gets the vertex map.
	 *
	 * @return the vertex map
	 */
	public Map<V, _Vertex<V, E>> getVertexMap() { return vertexMap; }

	/**
	 * Method buildValue()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public msi.gaml.operators.Graphs.GraphObjectToAdd buildValue(final IScope scope, final Object object) {
		if (object instanceof msi.gaml.operators.Graphs.NodeToAdd) return new msi.gaml.operators.Graphs.NodeToAdd(
				type.getKeyType().cast(scope, ((msi.gaml.operators.Graphs.NodeToAdd) object).object, null, false),
				((msi.gaml.operators.Graphs.NodeToAdd) object).weight);
		if (object instanceof msi.gaml.operators.Graphs.EdgeToAdd) return new msi.gaml.operators.Graphs.EdgeToAdd(
				type.getKeyType().cast(scope, ((msi.gaml.operators.Graphs.EdgeToAdd) object).source, null, false),
				type.getKeyType().cast(scope, ((msi.gaml.operators.Graphs.EdgeToAdd) object).target, null, false),
				type.getContentType().cast(scope, ((msi.gaml.operators.Graphs.EdgeToAdd) object).object, null, false),
				((msi.gaml.operators.Graphs.EdgeToAdd) object).weight);
		return new msi.gaml.operators.Graphs.EdgeToAdd(null, null,
				type.getContentType().cast(scope, object, null, false), 0.0);
	}

	/**
	 * Method buildValues()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public IContainer<?, msi.gaml.operators.Graphs.GraphObjectToAdd> buildValues(final IScope scope,
			final IContainer objects) {
		try (final Collector.AsList list = Collector.getList()) {
			if (!(objects instanceof msi.gaml.operators.Graphs.NodesToAdd)) {
				for (final Object o : objects.iterable(scope)) { list.add(buildValue(scope, o)); }
			} else {
				for (final Object o : objects.iterable(scope)) {
					list.add(buildValue(scope, new msi.gaml.operators.Graphs.NodeToAdd(o)));
				}
			}
			return list.items();
		}
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
		for (final Object o : value.iterable(scope)) { result.add(buildIndex(scope, o)); }
		return result;
	}

	/**
	 * To matrix.
	 *
	 * @param scope
	 *            the scope
	 * @return the gama float matrix
	 */
	public GamaFloatMatrix toMatrix(final IScope scope) {
		final int nbVertices = this.getVertices().size();
		if (nbVertices == 0) return null;
		final GamaFloatMatrix mat = new GamaFloatMatrix(nbVertices, nbVertices);
		mat.setAllValues(scope, Double.POSITIVE_INFINITY);
		for (int i = 0; i < nbVertices; i++) {
			for (int j = 0; j < nbVertices; j++) {
				if (i == j) {
					mat.set(scope, i, j, 0);
				} else {
					final Object edge = getEdge(getVertices().get(i), getVertices().get(j));
					if (edge != null) { mat.set(scope, i, j, getWeightOf(edge)); }
				}
			}
		}
		return mat;
	}

	@Override
	public ISpecies getVertexSpecies() { return vertexSpecies; }

	@Override
	public ISpecies getEdgeSpecies() {
		if (edgeSpecies == null) {
			final IType contents = getGamlType().getContentType();
			edgeSpecies = getScope().getModel().getSpecies(contents.getSpeciesName());
		}
		return edgeSpecies;
	}

	/**
	 * Dispose vertex.
	 *
	 * @param agent
	 *            the agent
	 */
	public void disposeVertex(final IAgent agent) {
		final Set edgesToModify = edgesOf(agent);
		removeVertex(agent);

		for (final Object obj : edgesToModify) { if (obj instanceof IAgent) { ((IAgent) obj).dispose(); } }
	}

	@Override
	public V addVertex() {

		return null;
	}

	@Override
	public Supplier<E> getEdgeSupplier() { return null; }

	@Override
	public GraphType getType() {
		if (isDirected()) return DefaultGraphType.simple().asDirected().asWeighted();
		return DefaultGraphType.simple().asUndirected().asWeighted();
	}

	@Override
	public Supplier<V> getVertexSupplier() { return null; }

	/**
	 * Gets the path computer.
	 *
	 * @return the path computer
	 */
	@Override
	public PathComputer getPathComputer() {
		if (pathComputer == null) { pathComputer = new PathComputer(this); }
		return pathComputer;
	}

}
