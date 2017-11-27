/**
 *  OpOpGraphs_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpGraphs_related__operatorsTest.
 */

model OpGraphs_related__operatorsTest

global {
	init {
		create testOpGraphs_related__operatorsTest number: 1;
		ask testOpGraphs_related__operatorsTest {do _step_;}
	}
}

	species testOpGraphs_related__operatorsTest {

	
		test add_edgeOp {
			//graph <- graph add_edge (source::target);

		}
	
		test add_nodeOp {
			//graph var0 <- graph add_node node(0) ; 	// var0 equals the graph with node(0)

		}
	
		test adjacencyOp {

		}
	
		test agent_from_geometryOp {
			//geometry line <- one_of(path_followed.segments);
			//road ag <- road(path_followed agent_from_geometry line);

		}
	
		test all_pairs_shortest_pathOp {
			//matrix<int> var0 <- all_pairs_shortest_paths(my_graph); 	// var0 equals shortest_paths_matrix will contain all pairs of shortest paths

		}
	
		test alpha_indexOp {
			graph graphEpidemio <- graph([]);
			float var1 <- alpha_index(graphEpidemio); 	// var1 equals the alpha index of the graph

		}
	
		test as_distance_graphOp {
			//list(ant) as_distance_graph 3.0

		}
	
		test as_edge_graphOp {
			graph var0 <- as_edge_graph([line([{1,5},{12,45}]),line([{13,45},{34,56}])],1);; 	// var0 equals a graph with two edges and three vertices
			graph var1 <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]); 	// var1 equals a graph with these three vertices and two edges
			graph var2 <- as_edge_graph([line([{1,5},{12,45}]),line([{12,45},{34,56}])]); 	// var2 equals a graph with two edges and three vertices

		}
	
		test as_intersection_graphOp {
			//list(ant) as_intersection_graph 0.5

		}
	
		test as_pathOp {
			//path var0 <- [road1,road2,road3] as_path my_graph; 	// var0 equals a path road1->road2->road3 of my_graph

		}
	
		test beta_indexOp {
			graph graphEpidemio <- graph([]);
			float var1 <- beta_index(graphEpidemio); 	// var1 equals the beta index of the graph

		}
	
		test betweenness_centralityOp {
			graph graphEpidemio <- graph([]);
			map var1 <- betweenness_centrality(graphEpidemio); 	// var1 equals the betweenness centrality index of the graph

		}
	
		test biggest_cliques_ofOp {
			graph my_graph <- graph([]);
			list<list> var1 <- biggest_cliques_of (my_graph); 	// var1 equals the list of the biggest cliques as list

		}
	
		test connected_components_ofOp {
			graph my_graph <- graph([]);
			list<list> var1 <- connected_components_of (my_graph); 	// var1 equals the list of all the components as list
			graph my_graph <- graph([]);
			list<list> var3 <- connected_components_of (my_graph, true); 	// var3 equals the list of all the components as list

		}
	
		test connectivity_indexOp {
			graph graphEpidemio <- graph([]);
			float var1 <- connectivity_index(graphEpidemio); 	// var1 equals the connectivity index of the graph

		}
	
		test contains_edgeOp {
			//bool var0 <- graphEpidemio contains_edge (node(0)::node(3)); 	// var0 equals true
			graph graphFromMap <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			bool var2 <- graphFromMap contains_edge link({1,5}::{12,45}); 	// var2 equals true
			assert var2 equals: true; 

		}
	
		test contains_vertexOp {
			graph graphFromMap<-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			bool var1 <- graphFromMap contains_vertex {1,5}; 	// var1 equals true
			assert var1 equals: true; 

		}
	
		test degree_ofOp {
			graph graphFromMap <- graph([]);
			int var1 <- graphFromMap degree_of (node(3)); 	// var1 equals 3

		}
	
		test directedOp {

		}
	
		test edgeOp {

		}
	
		test edge_betweenOp {
			//unknown var0 <- graphFromMap edge_between node1::node2; 	// var0 equals edge1

		}
	
		test edge_betweennessOp {
			graph graphEpidemio <- graph([]);
			map var1 <- edge_betweenness(graphEpidemio); 	// var1 equals the edge betweenness index of the graph

		}
	
		test edgesOp {

		}
	
		test gamma_indexOp {
			graph graphEpidemio <- graph([]);
			float var1 <- gamma_index(graphEpidemio); 	// var1 equals the gamma index of the graph

		}
	
		test generate_barabasi_albertOp {
			//graph<yourNodeSpecy,yourEdgeSpecy> graphEpidemio <- generate_barabasi_albert(
			//		yourNodeSpecy,
			//		yourEdgeSpecy,
			//		3,
			//		5,
			//		true);
			//graph<yourNodeSpecy,yourEdgeSpecy> graphEpidemio <- generate_barabasi_albert(
			//		yourListOfNodes,
			//		yourEdgeSpecy,
			//		3,
			//		5,
			//		true);

		}
	
		test generate_complete_graphOp {
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_complete_graph(
			//			myVertexSpecy,
			//			myEdgeSpecy,
			//			10, 25,
			//		true);
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_complete_graph(
			//			myListOfNodes,
			//			myEdgeSpecy,
			//		true);
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_complete_graph(
			//			myListOfNodes,
			//			myEdgeSpecy,
			//			25,
			//		true);
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_complete_graph(
			//			myVertexSpecy,
			//			myEdgeSpecy,
			//			10,
			//		true);

		}
	
		test generate_watts_strogatzOp {
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_watts_strogatz(
			//			myVertexSpecy,
			//			myEdgeSpecy,
			//			2,
			//			0.3,
			//			2,
			//		true);
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- generate_watts_strogatz(
			//			myListOfNodes,
			//			myEdgeSpecy,
			//			0.3,
			//			2,
			//		true);

		}
	
		test grid_cells_to_graphOp {
			//my_cell_graph<-grid_cells_to_graph(cells_list)

		}
	
		test in_degree_ofOp {
			graph graphFromMap <- graph([]);
			int var1 <- graphFromMap in_degree_of (node(3)); 	// var1 equals 2

		}
	
		test in_edges_ofOp {
			graph graphFromMap <- graph([]);
			container var1 <- graphFromMap in_edges_of node({12,45}); 	// var1 equals [LineString]

		}
	
		test layoutOp {

		}
	
		test load_graph_from_fileOp {
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"example_of_Pajek_file");
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"example_of_Pajek_file");
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"example_of_Pajek_file");
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"example_of_Pajek_file",
			//			myVertexSpecy,
			//			myEdgeSpecy );
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"./example_of_Pajek_file",
			//			myVertexSpecy,
			//			myEdgeSpecy );
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"./example_of_Pajek_file",
			//			myVertexSpecy,
			//			myEdgeSpecy);
			//graph<myVertexSpecy,myEdgeSpecy> myGraph <- load_graph_from_file(
			//			"pajek",
			//			"./example_of_Pajek_file",
			//			myVertexSpecy,
			//			myEdgeSpecy , true);

		}
	
		test load_shortest_pathsOp {
			//graph var0 <- load_shortest_paths(shortest_paths_matrix); 	// var0 equals return my_graph with all the shortest paths computed

		}
	
		test main_connected_componentOp {
			graph var0 <- main_connected_components (my_graph); 	// var0 equals the sub-graph corresponding to the main connected components of the graph

		}
	
		test maximal_cliques_ofOp {
			graph my_graph <- graph([]);
			list<list> var1 <- maximal_cliques_of (my_graph); 	// var1 equals the list of all the maximal cliques as list

		}
	
		test nb_cyclesOp {
			graph graphEpidemio <- graph([]);
			int var1 <- nb_cycles(graphEpidemio); 	// var1 equals the number of cycles in the graph

		}
	
		test neighbors_ofOp {
			container var3 <- neighbors_of (topology(self), self,10); 	// var3 equals all the agents located at a distance lower or equal to 10 to the agent applying the operator considering its topology.
			//container var0 <- graphEpidemio neighbors_of (node(3)); 	// var0 equals [node0,node2]
			//container var1 <- graphFromMap neighbors_of node({12,45}); 	// var1 equals [{1.0,5.0},{34.0,56.0}]
			container var2 <- topology(self) neighbors_of self; 	// var2 equals returns all the agents located at a distance lower or equal to 1 to the agent applying the operator considering its topology.

		}
	
		test nodeOp {

		}
	
		test nodesOp {

		}
	
		test out_degree_ofOp {
			graph graphFromMap <- graph([]);
			int var1 <- graphFromMap out_degree_of (node(3)); 	// var1 equals 4

		}
	
		test out_edges_ofOp {
			graph graphFromMap <- graph([]);
			container var1 <- graphFromMap out_edges_of (node(3)); 	// var1 equals 3

		}
	
		test path_betweenOp {
			//path var0 <- path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), [ag1, ag2, ag3]); 	// var0 equals A path between ag1 and ag2 and ag3 passing through the given cell_grid agents with minimal cost
			//path var1 <- my_topology path_between [ag1, ag2]; 	// var1 equals A path between ag1 and ag2
			//path var2 <- path_between (cell_grid where each.is_free, ag1, ag2); 	// var2 equals A path between ag1 and ag2 passing through the given cell_grid agents
			//path var3 <- my_topology path_between (ag1, ag2); 	// var3 equals A path between ag1 and ag2
			//path var4 <- path_between (my_graph, ag1, ag2); 	// var4 equals A path between ag1 and ag2
			//path var5 <- path_between (cell_grid as_map (each::each.is_obstacle ? 9999.0 : 1.0), ag1, ag2); 	// var5 equals A path between ag1 and ag2 passing through the given cell_grid agents with a minimal cost
			//path var6 <- path_between (cell_grid where each.is_free, [ag1, ag2, ag3]); 	// var6 equals A path between ag1 and ag2 and ag3 passing through the given cell_grid agents

		}
	
		test paths_betweenOp {
			//msi.gama.util.IList<msi.gama.util.path.GamaSpatialPath> var0 <- paths_between(my_graph, ag1:: ag2, 2); 	// var0 equals the 2 shortest paths (ordered by length) between ag1 and ag2

		}
	
		test predecessors_ofOp {
			graph graphEpidemio <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			container var1 <- graphEpidemio predecessors_of ({1,5}); 	// var1 equals []
			container var2 <- graphEpidemio predecessors_of node({34,56}); 	// var2 equals [{12;45}]

		}
	
		test remove_node_fromOp {
			//graph var0 <- node(0) remove_node_from graphEpidemio; 	// var0 equals the graph without node(0)

		}
	
		test rewire_nOp {
			graph graphEpidemio <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			graph var1 <- graphEpidemio rewire_n 10; 	// var1 equals the graph with 3 edges rewired

		}
	
		test source_ofOp {
			//graph graphEpidemio <- generate_barabasi_albert( ["edges_species"::edge,"vertices_specy"::node,"size"::3,"m"::5] );
			//unknown var1 <- graphEpidemio source_of(edge(3)); 	// var1 equals node1
			graph graphFromMap <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			point var3 <- graphFromMap source_of(link({1,5}::{12,45})); 	// var3 equals {1,5}
			assert var3 equals: {1,5}; 

		}
	
		test spatial_graphOp {

		}
	
		test strahlerOp {

		}
	
		test successors_ofOp {
			graph graphEpidemio <- as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			container var1 <- graphEpidemio successors_of ({1,5}); 	// var1 equals [{12,45}]
			assert var1 equals: [{12,45}]; 
			container var2 <- graphEpidemio successors_of node({34,56}); 	// var2 equals []
			assert var2 equals: []; 

		}
	
		test sumOp {
			int var0 <- sum ([12,10,3]); 	// var0 equals 25
			assert var0 equals: 25; 
			unknown var1 <- sum([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}]); 	// var1 equals {20.0,17.0}
			assert var1 equals: {20.0,17.0}; 

		}
	
		test target_ofOp {
			//graph graphEpidemio <- generate_barabasi_albert( ["edges_species"::edge,"vertices_specy"::node,"size"::3,"m"::5] );
			//unknown var1 <- graphEpidemio source_of(edge(3)); 	// var1 equals node1
			graph graphFromMap <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			unknown var3 <- graphFromMap target_of(link({1,5}::{12,45})); 	// var3 equals {12,45}
			assert var3 equals: {12,45}; 

		}
	
		test undirectedOp {

		}
	
		test use_cacheOp {

		}
	
		test weight_ofOp {
			graph graphFromMap <-  as_edge_graph([{1,5}::{12,45},{12,45}::{34,56}]);
			float var1 <- graphFromMap weight_of(link({1,5}::{12,45})); 	// var1 equals 1.0
			assert var1 equals: 1.0; 

		}
	
		test with_optimizer_typeOp {
			//graphEpidemio <- graphEpidemio with_optimizer_type "static";

		}
	
		test with_weightsOp {
			//graph_from_edges (list(ant) as_map each::one_of (list(ant))) with_weights (list(ant) as_map each::each.food)

		}
	
	}


experiment testOpGraphs_related__operatorsTestExp type: gui {}	
	