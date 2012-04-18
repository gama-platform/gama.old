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

	@operator(value = IType.PATH_STR)
	public static IPath toPath(final IScope scope, final Object object) {
		return GamaPathType.staticCast(scope, object, null);
	}

	@operator(value = "agent_from_geometry")
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
	public static Boolean containsVertex(final GamaGraph graph, final Object vertex) {
		return graph.containsVertex(vertex);
	}

	@operator(value = "contains_edge")
	public static Boolean containsEdge(final IGraph graph, final Object edge) {
		return graph.containsEdge(edge);
	}

	@operator(value = "contains_edge")
	public static Boolean containsEdge(final IGraph graph, final GamaPair edge) {
		return graph.containsEdge(edge.first(), edge.last());
	}

	@operator(value = "source_of", type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static Object sourceOf(final IGraph graph, final Object edge) {
		if ( graph.containsEdge(edge) ) { return graph.getEdgeSource(edge); }
		return null;
	}

	@operator(value = "target_of", type = ITypeProvider.LEFT_CONTENT_TYPE)
	public static Object targetOf(final IGraph graph, final Object edge) {
		if ( graph.containsEdge(edge) ) { return graph.getEdgeTarget(edge); }
		return null;
	}

	@operator(value = "weight_of")
	public static Double weightOf(final IGraph graph, final Object edge) {
		if ( graph.containsEdge(edge) ) {
			return graph.getEdgeWeight(edge);
		} else if ( graph.containsVertex(edge) ) { return graph.getVertexWeight(edge); }
		return 1d;
	}

	@operator(value = "in_edges_of")
	public static IList inEdgesOf(final IGraph graph, final Object vertex) {
		if ( graph.containsVertex(vertex) ) { return new GamaList(graph.incomingEdgesOf(vertex)); }
		return new GamaList();
	}

	@operator(value = "out_edges_of")
	public static IList outEdgesOf(final IGraph graph, final Object vertex) {
		if ( graph.containsVertex(vertex) ) { return new GamaList(graph.outgoingEdgesOf(vertex)); }
		return new GamaList();
	}

	@operator(value = "neighbours_of")
	public static IList neighboursOf(final IGraph graph, final Object vertex) {
		if ( graph.containsVertex(vertex) ) { return new GamaList(
			org.jgrapht.Graphs.neighborListOf(graph, vertex)); }
		return new GamaList();
	}

	@operator(value = "predecessors_of")
	public static IList predecessorsOf(final IGraph graph, final Object vertex) {
		if ( graph.containsVertex(vertex) ) { return new GamaList(
			org.jgrapht.Graphs.predecessorListOf(graph, vertex)); }
		return new GamaList();
	}

	@operator(value = "successors_of")
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
	public static IGraph spatialFromEdges(final IScope scope, final IContainer edges) {
		return new GamaSpatialGraph(edges, true, false, null);
	}

	// @operator(value = "graph_from_edges")
	// public static IGraph fromEdges(final IScope scope, final GamaMap edges) {
	// Edges are represented by pairs of vertex::vertex
	// return GamaGraphType.from(edges, false);
	// }

	@operator(value = "as_edge_graph")
	public static IGraph spatialFromEdges(final IScope scope, final GamaMap edges) {
		// Edges are represented by pairs of vertex::vertex
		return GamaGraphType.from(edges, true);
	}

	// @operator(value = "graph_from_vertices")
	// public static IGraph fromVertices(final IScope scope, final GamaList vertices) {
	// return new GamaGraph(vertices, false, false);
	// }

	@operator(value = "as_intersection_graph")
	public static IGraph spatialFromVertices(final IScope scope, final IContainer vertices,
		final Double tolerance) {
		return new GamaSpatialGraph(vertices, false, false, new IntersectionRelation(tolerance));
	}
	
	public static IGraph spatialLineIntersection(final IScope scope, final IContainer vertices) {
		return new GamaSpatialGraph(vertices, false, false, new IntersectionRelationLine());
	}

	@operator(value = "as_distance_graph")
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
	public static IGraph asDirectedGraph(final IGraph g) {
		return GamaGraphType.asDirectedGraph(g);
	}

	@operator(value = "undirected")
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
