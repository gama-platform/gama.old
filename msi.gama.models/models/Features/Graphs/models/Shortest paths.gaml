/**
* Name:  Shortest Path Computation on a Graph
* Author:  Patrick Taillandier
* Description: Model to show how to use the optimizer methods to compute the shortest path for the agents placed on a network with all of them 
*	having the same goal location. It also shows how to save these paths computed into a text file.
* Tags: graph, agent_movement, skill, shortest_path, algorithm
*/

model Network

global {
	file shape_file_in <- file('../includes/road.shp') ;
	graph the_graph; 
	geometry shape <- envelope(shape_file_in);
	bool save_shortest_paths <- false;
	bool load_shortest_paths <- false;
	string shortest_paths_file <- "../includes/shortest_paths.csv";
	bool memorize_shortest_paths <- false; //true by default
	
	/*6 types of optimizer (algorithms) can be used for the shortest path computation:
	 *    - Dijkstra: ensure to find the best shortest path - compute one shortest path at a time: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
	 * 	  - BellmannFord: ensure to find the best shortest path - compute one shortest path at a time: https://en.wikipedia.org/wiki/Bellman-Ford_algorithm
	 * 	  - AStar: ensure to find the best shortest path - compute one shortest path at a time: https://en.wikipedia.org/wiki/A*_search_algorithm
	 *    - NBAStar: the default one - ensure to find the best shortest path - compute one shortest path at a time: http://repub.eur.nl/pub/16100/ei2009-10.pdf
	 *    - NBAStarApprox : does not ensure to find the best shortest path - compute one shortest path at a time: http://repub.eur.nl/pub/16100/ei2009-10.pdf
	 *    - FloydWarshall: ensure to find the best shortest path - compute all the shortest paths at the same time (and keep them in memory): https://en.wikipedia.org/wiki/Floyd-Warshall_algorithm
	 */
	string optimizer_type <- "NBAStar" among: ["NBAStar", "NBAStarApprox", "Dijkstra", "AStar", "BellmannFord", "FloydWarshall"];
	int nb_people <- 100;
	init {    
		create road from: shape_file_in ;
		the_graph <- as_edge_graph(list(road));
		
		//allows to choose the type of algorithm to use compute the shortest paths
		the_graph <- the_graph with_optimizer_type optimizer_type;
		
		//allows to define if the shortest paths computed should be memorized (in a cache) or not
		the_graph <- the_graph use_cache memorize_shortest_paths;
		
		//computes all the shortest paths, puts them in a matrix, then saves the matrix in a file
		if save_shortest_paths {
			matrix ssp <- all_pairs_shortest_path(the_graph);
			save ssp type:"text" to:shortest_paths_file;
			
		//loads the file of the shortest paths as a matrix and uses it to initialize all the shortest paths of the graph
		} else if load_shortest_paths {
			the_graph <- the_graph load_shortest_paths matrix(file(shortest_paths_file));
		}
		
		create goal number: 1 {
			location <- any_location_in (one_of(road));
		}
		create people number: nb_people {
			target <- one_of (goal) ;
			location <- any_location_in (one_of(road));
		} 
	}
}

species road  {
	float speed_coef ;
	aspect default {
		draw shape color: #black ;
	}
} 
	
species goal {
	aspect default {
		draw circle(10) color: #red;
	}
}
	
species people skills: [moving] {
	goal target;
	path my_path; 
	
	aspect default {
		draw circle(10) color: #green;
	}
	reflex movement {
		do goto on:the_graph target:target speed:1.0;
	}
}


experiment goto_network type: gui {
	parameter "Type of optimizer" var: optimizer_type ;
	parameter "Number of people" var: nb_people min: 1 max: 1000000;
	parameter "Computed all the shortest paths and save the results" var: save_shortest_paths;
	parameter "Load the shortest paths from the file" var: load_shortest_paths;
	
	output {
		display objects_display {
			species road aspect: default ;
			species people aspect: default ;
			species goal aspect: default ;
		}
	}
}
