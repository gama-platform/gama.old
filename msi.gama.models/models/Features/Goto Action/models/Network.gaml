model Network
// Proposed by Patrick Taillandier

global {
	file shape_file_in <- file('../includes/gis/Reseau_TC.shp') ;
	graph the_graph; 
	geometry shape <- envelope(shape_file_in);
	
	/*4 type of optimizer can be used for the shortest path computation:
	 *    - Djikstra: the default one - ensure to find the best shortest path - compute one shortest path at a time (by default, memorise the shortest path found)
	 * 	  - Bellmann: ensure to find the best shortest path - compute one shortest path at a time (by default, memorise the shortest path found)
	 * 	  - AStar: do not ensure to find the best shortest path - compute one shortest path at a time (by default, memorise the shortest path found)
	 *    - Floyd Warshall: ensure to find the best shortest path - compute all the shortest pathes at the same time (and keep them in memory)
	 */
	string optimizer_type <- "Djikstra";
	int nb_people <- 1000;
	init {    
		create road from: shape_file_in ;
		the_graph <- as_edge_graph(list(road));
		the_graph <- the_graph with_optimizer_type optimizer_type;
		create goal number: 1 {
			location <- any_location_in (one_of(road));
		}
		create people number: nb_people {
			target <- one_of (goal) ;
			location <- any_location_in (one_of(road));
		} 
	}
}
entities {
	species road  {
		float speed_coef ;
		aspect default {
			draw shape color: rgb('black') ;
		}
	} 
	species goal {
		aspect default {
			draw circle(100) color: rgb('red');
		}
	}
	species people skills: [moving] {
		goal target;
		path my_path; 
	
		aspect default {
			draw circle(100) color: rgb('green');
		}
		reflex movement {
			do goto on:the_graph target:target speed:1;
		}
	}
}

experiment goto_network type: gui {
	parameter "Type of optimizer" var: optimizer_type among: ["Djikstra", "AStar", "Bellmann", "Floyd Warshall"];
	parameter "Number of people" var: nb_people min: 1 max: 1000000;
	
	output {
		display objects_display {
			species road aspect: default ;
			species people aspect: default ;
			species goal aspect: default ;
		}
	}
}
