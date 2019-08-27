/***
* Name: graph
* Author: kevinchapuis
* Description: All the operators related to graphs
* Tags: graph, network, path
***/
model graph

global {
	int nb_nodes <- 10 parameter: true min: 1;
	int av_degree <- 4 parameter: true;
	int x_cells <- 10;
	int y_cells <- 10;
	graph g_graph;
	string the_layout parameter: true init: "Circle" among: ["Circle", "Forced", "Grid"];
	string graph_generator parameter: true init: "Complete" among: ["Scall-free", "Small-world", "Complete", "Distance", "Intersection", "Grid"];

	init {
		write "- Using dedicated agent that extends 'graph_node' =>  'species my_node parent: graph_node edge_species: my_edge' with my_edge parent: base_edge";
		/*
		 * Most of the work is done in the 'related_to(node_agent node)' method
		 * 
		 * Use: e.g. if returns always true, will then obtain a complete graph
		 * 
		 */
		create builtin_node number: nb_nodes;
		g_graph <- first(builtin_node).my_graph;
	}

	action clean {
		ask regular_agent_node {
			do die;
		}

		ask regular_agent_edge {
			do die;
		}

		ask builtin_edge {
			do die;
		}

		ask builtin_node {
			do die;
		}

	}

	/*
	 * Generate a graph with scale-free network structural properties:
	 * https://en.wikipedia.org/wiki/Barabási–Albert_model
	 * 
	 */
	action scall_free {
		write "- Scale-free : Barabási–Albert = generate_barabasi_albert(node_species, edge_species, nb_nodes, new_edges, synchronize)";
		do clean;
		int new_edges_addition_per_node_introduction <- 4;
		g_graph <- generate_barabasi_albert(regular_agent_node, // The species of nodes
		regular_agent_edge, // The species of edges
		nb_nodes, // The number of nodes in the graph
		new_edges_addition_per_node_introduction, // the number of edges created when a new node enter the graph
		true);
	}

	/*
	 * Generate a graph with small-world network structural properties
	 * https://en.wikipedia.org/wiki/Small-world_network
	 * 
	 */
	action small_world {
		write "- Small-world : Watts-Strogatz = generate_watts_strogatz(node_species, edge_species, nb_nodes, rewire_proba, start_degree, synchronize)";
		do clean;
		float rewirering_probability <- 0.1;
		int fake_lattice_start_degree <- 4; // Even and more than 2
		g_graph <- generate_watts_strogatz(regular_agent_node, // The species of nodes
		regular_agent_edge, // The species of edges
		nb_nodes, // The number of nodes
		rewirering_probability, // The probability to rewire a node in the generation process
		fake_lattice_start_degree, // The degree of node at start, before the rewirering process
		true);
	}

	/*
	 * Generate a complete graph where each node is connected to all other nodes
	 */
	action complete {
		write "- Complete = generate_complete_graph(node_species, edge_species, nb_node)";
		do clean;
		g_graph <- generate_complete_graph(regular_agent_node, // The species of nodes
		regular_agent_edge, // The species of edges 
		nb_nodes, // The number of nodes in the graph
		true);
	}

	action from_nodes {
		do clean;
		create regular_agent_node number: nb_nodes;
		write "\tas_distance_graph(my_species, distance)";
		float distance <- 20 #m;
		g_graph <- as_distance_graph(regular_agent_node, // A list of agent to connect to one another 
		distance,// The maximal distance between two nodes for them to be connected
		regular_agent_edge 
);
	}

	action from_polygons {
		do clean;
		// Create a set of lines (no need to create agent) to build network with
		create regular_agent_node number: nb_nodes with: [shape::circle(rnd(5,20)) at_location any_location_in(world)];
		write "\tas_intersection_graph(my_lines, tolerance)";
		float tolerance <- 1.0;
		g_graph <- as_intersection_graph(regular_agent_node, tolerance,regular_agent_edge);
	}

	action grid_graph (int k) {
		do clean;
		write "With a grid with 4, 6 or 8 neighbors that correspond to a lattice of 4, 6 and 8 degree";
		write "- Using 'grid_cells_to_graph(my_grid)'";
		switch k {
			match_one [5, 6, 7] {
				g_graph <- grid_cells_to_graph(cell6,regular_agent_edge);
			}

			match_between [8, #infinity] {
				g_graph <- grid_cells_to_graph(cell8,regular_agent_edge);
			}

			default {
				g_graph <- grid_cells_to_graph(cell4,regular_agent_edge);
			}

		}

	}

	action access_and_modify_edge_and_node {
		write "\n==================";
		write "GRAPH MANIPULATION\n";

		/*
		 * build a new age as a pair of point
		 */
		pair an_edge <- one_of(g_graph.vertices)::one_of(g_graph.vertices);
		write "Test weither the graph contains an edge: 'graph contains_edge edge'";
		bool e1_in_graph <- g_graph contains_edge an_edge;
		geometry node1 <- any(g_graph.vertices);
		geometry node2 <- any(g_graph.vertices);
		write "Test weither the graph contains a node: 'graph contains_vertex node'";
		bool n1_in_graph <- g_graph contains_vertex node1;
		bool n2_in_graph <- g_graph contains_vertex node2;
		write "Access to an edge: 'graph edge_between pair(node1::node2)'";
		geometry the_edge <- g_graph edge_between (node1::node2);
		write "Access to a node from an edge: 'graph target_of edge' or 'graph source_of edge'";
		float w <- g_graph weight_of an_edge;
		geometry target <- g_graph target_of an_edge;
		geometry source <- g_graph source_of an_edge;
		write "Add nodes and edges";
		write "use the 'graph add_edge p1::p2' operator";
		g_graph <- g_graph add_edge an_edge;
		write "use the 'graph add_node p1 operator";
		if (type_of(first(g_graph.vertices)) = point) {
			g_graph <- g_graph add_node any_location_in(world);
		
		}
		write "Remove nodes and edges";
		write "use the 'p1 remove_node_from graph' operator";
		g_graph <- geometry(any(g_graph.vertices)) remove_node_from g_graph;
		write "Rewire nodes";
		g_graph <- g_graph rewire_n 10;
		write "Change the weigth of edges";
		g_graph <- g_graph with_weights (g_graph.edges as_map (each::rnd(20)));
		write "Turn graph into directed / undirected ones";
		g_graph <- directed(g_graph);
		g_graph <- undirected(g_graph);
	}

	action connectivity_of_node_and_edge {
		write "\n==================";
		write "NODES CONNECTIVITY\n";
		geometry a_node <- one_of(g_graph.vertices);
		write "Access to the list of successors and predecessors:\n" + "'graph successors_of node' or 'graph predecessors_of node'";
		list successors <- g_graph successors_of a_node;
		list predecessors <- g_graph predecessors_of a_node;
		write "Access to the neighbords of a node: 'graph neighbors_of node'";
		list neighbors <- g_graph neighbors_of a_node;
		write "The degree of a node (number of neighbords): 'degree_of', 'in_degree_of' and 'out_degree_of'";
		int d_n <- g_graph degree_of a_node;
		int in_d <- g_graph in_degree_of a_node;
		int out_d <- g_graph out_degree_of a_node;
		list in_e <- g_graph in_edges_of a_node;
		list out_e <- g_graph out_edges_of a_node;
	}

	action connectivity_of_graph {
		write "\n==================";
		write "GRAPH CONNECTIVITY\n";
		write "Compute the betweenness centrality of each node: correspond to the number of shortest path " + "that pass by the node";
		map<geometry, int> bc <- betweenness_centrality(g_graph);
		write "Number of cycle in the graph = " + nb_cycles(g_graph);
		write "Alpha index of the graph = " + alpha_index(g_graph);
		write "Beta index of the graph = " + beta_index(g_graph);
		write "Gamma index of the graph = " + gamma_index(g_graph);
		write "Connectivity index of the graph = " + connectivity_index(g_graph);
		write "Compute main connected component and all connected components of the graph";
		g_graph <- main_connected_component(g_graph);
		list component <- connected_components_of(g_graph);
		write "Calcul the maximum and biggest cliques: 'maximal_cliques_of' and 'biggest_cliques_of'";
		list cliques_max <- maximal_cliques_of(g_graph);
		list cliques_big <- biggest_cliques_of(g_graph);
	}

	action layout_graph {
		write "\n==================";
		write "GRAPH LAYOUT\n";
		switch the_layout {
			match "Circle" {
				do c_layout;
			}

			match "Grid" {
				do g_layout;
			}

			match "Forced" {
				do f_layout;
			}

		}

	}

	action c_layout {
		write "Circle classical layout : nodes are randomly placed on a circle";
		g_graph <- layout_circle(g_graph, world.shape, // The geometry to spatialize nodes in 
		false // Shuffle or not the nodes
);
	}

	action f_layout {
		write "Forced based layout : connected node pull each other, while unconnected node push each other away";
		g_graph <- layout_force(g_graph, world.shape, // The geometry to spatialize nodes in 
		0.4, // The pull/push force
		0.01, // The cooling rate of the algorithm
		100 // Maximum number of iterations
);
	}

	action g_layout {
		write "Homemade grid based layout : distributes nodes over a grid to minimize edge crossing";
		graph q <- layout_grid(g_graph, world.shape, // The geometry to spatialize nodes in
		1.5 // The ratio of possible grid position over the total number of nodes (should be higher than 1.0 )
		);

	}

	action path_finding_graph {
		write "\n====================";
		write "GRAPH PATH OPERATORS\n";
		write "The matrix of predecessor in all shortest path: ";
		matrix sp <- all_pairs_shortest_path(g_graph);
		write first(10, sp);
		g_graph <- load_shortest_paths(g_graph, sp);
		geometry node1 <- any(g_graph.vertices);
		geometry node2 <- any(g_graph.vertices);
		map<int, geometry> mfb <- map<int, geometry>(g_graph max_flow_between (node1, node2));
		write "Find a path between two nodes: 'graph path_between (node1, node2)'";
		path tp <- g_graph path_between (node1, node2);
	}

}

/*
 * GENERAL PURPOSE GRAPH SPECIEIS
 */
species builtin_edge parent: base_edge {
}

species builtin_node parent: graph_node edge_species: builtin_edge {

/*
	 * This particular methods define the structure of the network
	 */
	bool related_to (builtin_node other) {
		return true;
	}

}

species regular_agent_edge {
}

species regular_agent_node {
}

grid cell4 width: x_cells height: y_cells neighbors: 4 {
}

grid cell6 width: x_cells height: y_cells neighbors: 6 {
}

grid cell8 width: x_cells height: y_cells neighbors: 8 {
}

experiment Graph type: gui {
	user_command "Accessing/modifying graphs" {
		ask world {
			do access_and_modify_edge_and_node;
		}

	}

	user_command "Connectivity of node and edge" {
		ask world {
			do connectivity_of_node_and_edge;
		}

	}

	user_command "Connectivity of graph" {
		ask world {
			do connectivity_of_graph;
		}

	}

	user_command "Layout graph" {
		ask world {
			do layout_graph;
		}

	}

	user_command "Path with graph" {
		ask world {
			do path_finding_graph;
		}

	}

	user_command "Create graphs" {
		switch graph_generator {
			match "Scall-free" {
				ask world {
					do scall_free();
				}

			}

			match "Small-world" {
				ask world {
					do small_world();
				}

			}

			match "Complete" {
				ask world {
					do complete();
				}

			}

			match "Distance" {
				ask world {
					do from_nodes();
				}

			}

			match "Intersection" {
				ask world {
					do from_polygons();
				}

			}

			match "Grid" {
				ask world {
					do grid_graph(av_degree);
				}

			}

		}

	}

	output {
		display graph_layout {
			graphics "graph" {
				loop e over: g_graph.edges {
					draw geometry(e) color: #black;
				}

				loop v over: g_graph.vertices {
					draw circle(0.5) at: geometry(v).location color: #red border: #black;
				}

			}

		}

	}

}
