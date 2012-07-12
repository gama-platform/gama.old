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
package msi.gaml.operators;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.stream.file.FileSourceDGS1And2;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.stream.file.FileSourceEdge;
import org.graphstream.stream.file.FileSourceGEXF;
import org.graphstream.stream.file.FileSourceGraphML;
import org.graphstream.stream.file.FileSourceLGL;
import org.graphstream.stream.file.FileSourceNCol;
import org.graphstream.stream.file.FileSourcePajek;
import org.graphstream.stream.file.FileSourceTLP;
import org.graphstream.stream.file.dgs.OldFileSourceDGS;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph.VertexRelationship;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 13 avr. 2011
 * 
 * @todo Description
 * 
 */
public class Graphs {

	private static class IntersectionRelation implements VertexRelationship {

		double tolerance;

		IntersectionRelation(final double t) {
			tolerance = t;
		}

		@Override
		public boolean related(final IShape p1, final IShape p2) {
			return Spatial.Properties.opIntersects(
				Spatial.Transformations.opBuffer(p1.getGeometry(), tolerance),
				Spatial.Transformations.opBuffer(p2.getGeometry(), tolerance));
		}

		@Override
		public boolean equivalent(final IShape p1, final IShape p2) {
			return p1 == null ? p2 == null : p1.getGeometry().equals(p2.getGeometry());
		}
	};
	
	private static class IntersectionRelationLine implements VertexRelationship {

		IntersectionRelationLine() {
		}

		@Override
		public boolean related(final IShape p1, final IShape p2) {
			return p1.getInnerGeometry().relate(p2.getInnerGeometry(), "****1****");
		}

		@Override
		public boolean equivalent(final IShape p1, final IShape p2) {
			return p1 == null ? p2 == null : p1.getGeometry().equals(p2.getGeometry());
		}

	};

	private static class DistanceRelation implements VertexRelationship {

		double distance;

		DistanceRelation(final double d) {
			distance = d;
		}

		/**
		 * @throws GamaRuntimeException
		 * @see msi.gama.util.graph.GamaSpatialGraph.VertexRelationship#related(msi.gama.interfaces.IScope,
		 *      msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
		 */
		@Override
		public boolean related(final IShape g1, final IShape g2) {
			return Spatial.Relations.opDistanceTo(GAMA.getDefaultScope(), g1.getGeometry(),
				g2.getGeometry()) <= distance;
		}

		/**
		 * @throws GamaRuntimeException
		 * @see msi.gama.util.graph.GamaSpatialGraph.VertexRelationship#equivalent(msi.gama.interfaces.IGeometry,
		 *      msi.gama.interfaces.IGeometry)
		 */
		@Override
		public boolean equivalent(final IShape p1, final IShape p2) {
			return p1 == null ? p2 == null : p1.getGeometry().equals(p2.getGeometry());
		}

	}

	@operator(value = "agent_from_geometry")
	@doc(
		value = "returns the agent corresponding to given geometry (right-hand operand) in the given path (left-hand operand).",
		special_cases = "if the left-hand operand is nil, returns nil",
		examples = {
			"let line type: geometry <- one_of(path_followed.segments);",	
			"let ag type: road <- road(path_followed agent_from_geometry line);"})
	public static IAgent getAgentFromGeom(final IPath path, final IShape geom) {
		if ( path == null ) { return null; }
		return (IAgent) path.getRealObject(geom);
	}

	/*
	 * TO DO : CHECK THE VALIDITY OF THESE OPERATORS FOR ALL KINDS OF PATH
	 * 
	 * @operator(value = "vertices")
	 * public static GamaList nodesOfPath(final GamaPath path) {
	 * if ( path == null ) { return new GamaList(); }
	 * return path.getVertexList();
	 * }
	 * 
	 * @operator(value = "edges")
	 * public static GamaList edgesOfPath(final GamaPath path) {
	 * if ( path == null ) { return new GamaList(); }
	 * return path.getEdgeList();
	 * }
	 */

	@operator(value = "contains_vertex")
	@doc(
		value = "returns true if the graph(left-hand operand) contains the given vertex (righ-hand operand), false otherwise",
		special_cases = "if the left-hand operand is nil, returns false",
		examples = {"let graphFromMap type: graph <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);",
					"graphFromMap contains_vertex {1,5}  --: true"},
		see = {"contains_edge"})
	public static Boolean containsVertex(final GamaGraph graph, final Object vertex) {
		if (graph == null) throw new GamaRuntimeException("In the contains_vertex operator, the graph should not be null!");				
		return graph.containsVertex(vertex);
	}

	@operator(value = "contains_edge")
	@doc(
		value = "returns true if the graph(left-hand operand) contains the given edge (righ-hand operand), false otherwise",
		special_cases = "if the left-hand operand is nil, returns false",
		examples = {"let graphFromMap type: graph <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);",
					"graphFromMap contains_edge link({1,5}::{12,45})  --: true"},
		see = {"contains_vertex"})	
	public static Boolean containsEdge(final IGraph graph, final Object edge) {
		if (graph == null) throw new GamaRuntimeException("In the contains_edge operator, the graph should not be null!");				
		return graph.containsEdge(edge);
	}

	@operator(value = "contains_edge")
	@doc( 
		special_cases = "if the right-hand operand is a pair, returns true if it exists an edge between the two elements of the pair in the graph",
		examples = {
			"let graphEpidemio type: graph <- generate_barabasi_albert( [\"edges_specy\"::edge,\"vertices_specy\"::node,\"size\"::3,\"m\"::5] );",
			"graphEpidemio contains_edge (node(0)::node(3));   --:   true"})
	public static Boolean containsEdge(final IGraph graph, final GamaPair edge) {
		if (graph == null) throw new GamaRuntimeException("In the contains_edge operator, the graph should not be null!");		
		return graph.containsEdge(edge.first(), edge.last());
	}

	@operator(value = "source_of", type = ITypeProvider.LEFT_CONTENT_TYPE)
	@doc(
		value = "returns the source of the edge (right-hand operand) contained in the graph given in left-hand operand.",
		special_cases = "if the lef-hand operand (the graph) is nil, throws an Exception",
		examples = {
			"let graphEpidemio type: graph <- generate_barabasi_albert( [\"edges_specy\"::edge,\"vertices_specy\"::node,\"size\"::3,\"m\"::5] );",
			"graphEpidemio source_of(edge(3)) 				--:  node1",
			"let graphFromMap type: graph <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);",		   	
			"graphFromMap source_of(link({1,5}::{12,45}))  	--: {1.0;5.0}"},
		see = "target_of")
	public static Object sourceOf(final IGraph graph, final Object edge) {
		if (graph == null) throw new GamaRuntimeException("In the source_of operator, the graph should not be null!");				
		if ( graph.containsEdge(edge) ) { return graph.getEdgeSource(edge); }
		return null;
	}

	@operator(value = "target_of", type = ITypeProvider.LEFT_CONTENT_TYPE)
	@doc(
		value = "returns the target of the edge (right-hand operand) contained in the graph given in left-hand operand.",
		special_cases = "if the lef-hand operand (the graph) is nil, returns nil",
		examples = {
			"let graphEpidemio type: graph <- generate_barabasi_albert( [\"edges_specy\"::edge,\"vertices_specy\"::node,\"size\"::3,\"m\"::5] );",
			"graphEpidemio source_of(edge(3)) 				--:  node1",
			"let graphFromMap type: graph <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);",		   	
			"graphFromMap source_of(link({1,5}::{12,45}))  	--: {1.0;5.0}"},
		see = "source_of")	
	public static Object targetOf(final IGraph graph, final Object edge) {
		if (graph == null) throw new GamaRuntimeException("In the target_of operator, the graph should not be null!");		
		if ( graph.containsEdge(edge) ) { return graph.getEdgeTarget(edge); }
		return null;
	}

	@operator(value = "weight_of")
	@doc(
		value = "returns the weight of the given edge (right-hand operand) contained in the graph given in right-hand operand.",
		comment = "In a localized graph, an edge has a weight by default (the distance between both vertices).",
		special_cases = {
			"if the left-operand (the graph) is nil, returns nil",
			"if the right-hand operand is not an edge of the given graph, weight_of checks whether it is a node of the graph and tries to return its weight",
			"if the right-hand operand is neither a node, nor an edge, returns 1."},
		examples = {
			"let graphFromMap type: graph <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);",
			"graphFromMap source_of(link({1,5}::{12,45}))  --: 41.48493702538308"})
	public static Double weightOf(final IGraph graph, final Object edge) {
		if (graph == null) throw new GamaRuntimeException("In the weight_of operator, the graph should not be null!");
		if ( graph.containsEdge(edge) ) {
			return graph.getEdgeWeight(edge);
		} else if ( graph.containsVertex(edge) ) { return graph.getVertexWeight(edge); }
		return 1d;
	}

	@operator(value = "in_edges_of")
	@doc(
		value = "returns the list of the in-edges of a vertex (right-hand operand) in the graph given as left-hand operand.",
		examples = {"graphEpidemio in_edges_of (node(3))   --:  [edge2,edge3]",
					"graphFromMap in_edges_of node({12,45})  --:  [LineString]"},
		see = "out_edges_of")
	public static IList inEdgesOf(final IGraph graph, final Object vertex) {
		if (graph == null) throw new GamaRuntimeException("In the in_edges_of operator, the graph should not be null!");
		if ( graph.containsVertex(vertex) ) { return new GamaList(graph.incomingEdgesOf(vertex)); }
		return new GamaList();
	}

	@operator(value = "out_edges_of")
	@doc(
		value = "returns the list of the out-edges of a vertex (right-hand operand) in the graph given as left-hand operand.",
		examples = {"graphEpidemio out_edges_of (node(3))   --:   []",
					"graphFromMap out_edges_of {12,45}      --: [LineString]"	},
		see = "in_edges_of")
	public static IList outEdgesOf(final IGraph graph, final Object vertex) {
		if (graph == null) throw new GamaRuntimeException("In the out_edges_of operator, the graph should not be null!");		
		if ( graph.containsVertex(vertex) ) { return new GamaList(graph.outgoingEdgesOf(vertex)); }
		return new GamaList();
	}

	@operator(value = "neighbours_of")
	@doc(
		value = "returns the list of neighbours of the given vertex (right-hand operand) in the given graph (left-hand operand)",
		examples = {
			"graphEpidemio neighbours_of (node(3)) 		--:	[node0,node2]",
			"graphFromMap neighbours_of node({12,45}) 	--: [{1.0;5.0},{34.0;56.0}]"},
		see = {"predecessors_of", "successors_of"})
	public static IList neighboursOf(final IGraph graph, final Object vertex) {
		if (graph == null) throw new GamaRuntimeException("In the neighbours_of operator, the graph should not be null!");				
		if ( graph.containsVertex(vertex) ) { return new GamaList(
			org.jgrapht.Graphs.neighborListOf(graph, vertex)); }
		return new GamaList();
	}

	@operator(value = "predecessors_of")
	@doc(
		value = "returns the list of predecessors (i.e. sources of in edges) of the given vertex (right-hand operand) in the given graph (left-hand operand)",
		examples = {
			"graphEpidemio predecessors_of (node(3)) 		--: [node0,node2]",
			"graphFromMap predecessors_of node({12,45}) 	--:	[{1.0;5.0}]"},
		see = {"neighbours_of", "successors_of"})	
	public static IList predecessorsOf(final IGraph graph, final Object vertex) {
		if ( graph.containsVertex(vertex) ) { return new GamaList(
			org.jgrapht.Graphs.predecessorListOf(graph, vertex)); }
		return new GamaList();
	}

	@operator(value = "successors_of")
	@doc(
		value = "returns the list of successors (i.e. targets of out edges) of the given vertex (right-hand operand) in the given graph (left-hand operand)",
		examples = {
			"graphEpidemio successors_of (node(3)) 		--: []",
			"graphFromMap successors_of node({12,45}) 	--: [{34.0;56.0}]"},
		see = {"predecessors_of", "neighbours_of"})		
	public static IList successorsOf(final IGraph graph, final Object vertex) {
		if ( graph.containsVertex(vertex) ) { return new GamaList(
			org.jgrapht.Graphs.successorListOf(graph, vertex)); }
		return new GamaList();
	}

	// @operator(value = "graph_from_edges")
	// public static IGraph fromEdges(final IScope scope, final GamaList edges) {
	// return new GamaGraph(edges, true, false);
	// }

	@operator(value = "as_edge_graph") 
	@doc(
		value = "creates a graph from the list/map of edges given as operand",
		special_cases = "if the operand is a list, the graph will be built with elements of the list as vertices",
		examples = {"as_edge_graph([{1,5},{12,45},{34,56}])  --:  build a graph with these three vertices and reflexive links on each vertices"},
		see = {" as_intersection_graph, as_distance_graph"})
	public static IGraph spatialFromEdges(final IScope scope, final IContainer edges) {
		return new GamaSpatialGraph(edges, true, false, null);
	}

	// @operator(value = "graph_from_edges")
	// public static IGraph fromEdges(final IScope scope, final GamaMap edges) {
	// Edges are represented by pairs of vertex::vertex
	// return GamaGraphType.from(edges, false);
	// }

	@operator(value = "as_edge_graph")
	@doc(
		special_cases = "if the operand is a map, the graph will be built by creating edges from pairs of the map",
		examples = "as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}])  --:  build a graph with these three vertices and two edges")	
	public static IGraph spatialFromEdges(final IScope scope, final GamaMap edges) {
		// Edges are represented by pairs of vertex::vertex
		return GamaGraphType.from(edges, true);
	}

	// @operator(value = "graph_from_vertices")
	// public static IGraph fromVertices(final IScope scope, final GamaList vertices) {
	// return new GamaGraph(vertices, false, false);
	// }

	@operator(value = "as_intersection_graph")
	@doc(
		value = "creates a graph from a list of vertices (left-hand operand). An edge is created between each pair of vertices with an intersection (with a given tolerance).",
		comment = "as_intersection_graph is more efficient for a list of geometries (but less accurate) than as_distance_graph.",
		examples = "list(ant) as_intersection_graph 0.5;",
		see = {"as_distance_graph", "as_edge_graph"}
	)
	public static IGraph spatialFromVertices(final IScope scope, final IContainer vertices,
		final Double tolerance) {
		return new GamaSpatialGraph(vertices, false, false, new IntersectionRelation(tolerance));
	}
	
	public static IGraph spatialLineIntersection(final IScope scope, final IContainer vertices) {
		return new GamaSpatialGraph(vertices, false, false, new IntersectionRelationLine());
	}

	@operator(value = "as_distance_graph")
	@doc(
		value = "creates a graph from a list of vertices (left-hand operand). An edge is created between each pair of vertices close enough (less than a distance, right-hand operand).",	
		comment ="as_distance_graph is more efficient for a list of points than as_intersection_graph.",
		examples = "list(ant) as_distance_graph 3.0;",
		see = {"as_intersection_graph", "as_edge_graph"}
	)
	public static IGraph spatialDistanceGraph(final IScope scope, final IContainer vertices,
		final Double distance) {
		return new GamaSpatialGraph(vertices, false, false, new DistanceRelation(distance));
	}

	// @operator(value = "spatialize")
	// public static IGraph asSpatialGraph(final GamaGraph g) {
	// return GamaGraphType.asSpatialGraph(g);
	// }

	// @operator(value = "unspatialize")
	// public static IGraph asRegularGraph(final GamaGraph g) {
	// return GamaGraphType.asRegularGraph(g);
	// }

	@operator(value = "directed")
	@doc(
		value = "the operand graph becomes a directed graph.",
		comment = "the operator alters the operand graph, it does not create a new one.",
		see = {"undirected"})
	public static IGraph asDirectedGraph(final IGraph g) {
		return GamaGraphType.asDirectedGraph(g);
	}

	@operator(value = "undirected")
	@doc(
		value = "the operand graph becomes an undirected graph.",
		comment = "the operator alters the operand graph, it does not create a new one.",
		see = {"directed"})	
	public static IGraph asUndirectedGraph(final IGraph g) {
		return GamaGraphType.asUndirectedGraph(g);
	}

	@operator(value = "with_weights")
	public static IGraph withWeights(final IScope scope, final IGraph graph, final GamaMap weights) {
		// a map of vertex/edge::double to provide weights
		// Example : graph_from_edges (list ant as_map each::one_of (list ant)) with_weights (list
		// ant as_map each::each.food)
		graph.setWeights(weights);
		if (graph instanceof GamaSpatialGraph) 
			((GamaSpatialGraph) graph).reInitPathFinder();
		return graph;
	}

	@operator(value = "with_weights")
	public static IGraph withWeights(final IScope scope, final IGraph graph, final IList weights) {
		// Simply a list of double... and, by default, for edges.However, the ordering of edges may
		// change overtime, which can create a problem somewhere...
		IList edges = graph.getEdges();
		int n = edges.size();
		if ( n != weights.size() ) { return graph; }
		for ( int i = 0; i < n; i++ ) {
			graph.setEdgeWeight(edges.get(i), Cast.asFloat(scope, weights.get(i)));
		}
		if (graph instanceof GamaSpatialGraph) 
			((GamaSpatialGraph) graph).reInitPathFinder();
		return graph;
	}
	
	@operator(value = "set_verbose")
	public static IGraph setVerbose(final IScope scope, final IGraph graph, final Boolean verbose) {
		graph.setVerbose(verbose);
		return graph;
	}
	
	@operator(value = "with_optimizer_type")
	public static IGraph setOptimizeType(final IScope scope, final IGraph graph, final String optimizerType) {
		graph.setOptimizerType(optimizerType);
		return graph;
	}

	@operator(value = "remove_node_from")
	public static IGraph removeEdgeFrom(final IShape node, final IGraph g) {
		g.removeVertex(node);
		return g;
	}

	@operator(value = "rewire_p")
	public static IGraph rewireGraph(final IGraph g, final Double probability) {
		GraphAlgorithmsHandmade.rewireGraphProbability(g, probability);
		return g;
	}
	
	@operator(value = "rewire_n")
	public static IGraph rewireGraph(final IGraph g, final Integer count) {
		GraphAlgorithmsHandmade.rewireGraphCount(g, count);
		return g;
	}
	
	
	/*
	public static IGraph addRandomEdges(final IGraph g, final Double probability) {
		GraphAlgorithmsHandmade.rewireGraph(g, probability);
		return g;
	}
	*/
	
	
	// TODO "complete" (pour cr�er un graphe complet)

	// vertices_to_graph [vertices] with_weights (vertices collect: each.val) -> renvoie un graphe
	// construit � partir des vertex (edges g�n�r�s soit sous la forme d'une paire vertex::vertex,
	// soit sous la forme d'un lien g�om�trique)
	// vertices_to_graph [a1, a2, a3] with_weights ([1, 4, 8]) -> m�me chose
	// edges_to_graph [edges] with_weights (edges collect: each.length) -> renvoie un graphe
	// construit � partir des edges (vertex g�n�r�s soit sous la forme d'une paire edge::edge, soit
	// sous la forme d'un point pour les g�om�tries)
	// edges_to_graph [a1::a2, a2::a3] with_weights ([3.0, 1.3]) -> m�me chose
	// add item: v1 to:g weight: 1 -> ajout d'un vertex
	// add item: v1::v2 to:g weight:1 -> ajout d'un edge g�n�r� (et des vertex correspondants si
	// n�cessaire)
	// add item: (v1::v2)::e to: g weight: 1 -> edge (ajout d'un edge explicite et des vertex
	// correspondants si n�cessaire)
	// remove item: v1::v2 from: g -> remove edge
	// remove item: o from: g -> remove edge / vertex
	// put item: e2 at: v1::v2 in: g -> replace/add an edge (on peut aussi faire la m�me chose pour
	// remplacer un vertex)

	// TODO Transformer peu � peu toutes les primitives (GeometricFunctions, GeometricSkill, etc.)
	// en op�rateurs (as_graph, as_network, as_triangle_graph, as_complete_graph -- En cr�ant les
	// liens dynamiques correspondants --, as_weighted_graph ...).

	// TODO Ajouter les op�rateurs d'union, d'intersection, d'�galit�, de diff�rence

	// TODO Ajouter des g�n�rateurs sp�cifiques a partir de GraphGenerator (pb: quelles classes pour
	// les vertices/edges ??
}
