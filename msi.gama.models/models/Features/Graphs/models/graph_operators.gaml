/**
 *  graphoperators
 *  Author: Patrick Taillandier
 *  Description: 
 */

model graphoperators

global {
	graph the_graph;
	init {
		create people number: 50;
		
		//creation of the graph: all vertices that are at distance <= 20 are connected
		the_graph <- as_distance_graph(people, 20);
		
		//compute the betweenness_centrality of each vertice
		map<people,float> bc <- map<people, float>(betweenness_centrality(the_graph));
		float max_centrality <- max(bc.values);
		float min_centrality <- min(bc.values);
		ask people {
			centrality <- (bc[self] - min_centrality) / (max_centrality - min_centrality);
			centrality_color <- rgb(255, int(255 * (1 - centrality)), int(255 * (1 - centrality)));
		}
		write "mean vertice degree: " + mean(the_graph.vertices collect (the_graph degree_of each));
		write "nb_cycles: " + nb_cycles(the_graph);
		write "alpha_index: " + alpha_index(the_graph);
		write "beta_index: " + beta_index(the_graph);
		write "gamma_index: " + gamma_index(the_graph);
		write "connectivity_index: " + connectivity_index(the_graph);
		write "connected_components_of: " + connected_components_of(the_graph);
		
	}
}

species people {
	float centrality;
	rgb centrality_color;
	aspect centrality{
		draw circle(1) color: centrality_color;
		
	}
}

experiment graphoperators type: gui {
	
	output {
		display map {
			graphics "edges" {
				loop edge over: the_graph.edges {
					draw edge color: rgb("black");
				}
 			}
 			species people aspect: centrality;
			
		}
	}
}
