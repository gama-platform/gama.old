/**
* Name: Graph Operators
* Author: Patrick Taillandier
* Description: Model to show how to use the different existing operators for the graph species
* Tags: Graph
*/

model graphoperators

global {
	graph<geometry,geometry> the_graph;
	list<list> cliques;
	map<geometry,int> ec;
	init {
		create people number: 50;
		
		//creation of the graph: all vertices that are at distance <= 20 are connected
		the_graph <- as_distance_graph(people, 20);
		
		//compute the betweenness_centrality of each vertice
		map<people,int> bc <- map<people, int>(betweenness_centrality(the_graph));
		int max_centrality <- max(bc.values);
		int min_centrality <- min(bc.values);
		ask people {
			centrality <- (bc[self] - min_centrality) / (max_centrality - min_centrality);
			centrality_color <- rgb(255, int(255 * (1 - centrality)), int(255 * (1 - centrality)));
		}
		
		//compute the edge_betweenness of each edge
		ec <- map<geometry, int>(edge_betweenness(the_graph));
		
		write "mean vertice degree: " + mean(the_graph.vertices collect (the_graph degree_of each));
		write "nb_cycles: " + nb_cycles(the_graph);
		write "alpha_index: " + alpha_index(the_graph);
		write "beta_index: " + beta_index(the_graph);
		write "gamma_index: " + gamma_index(the_graph);
		write "connectivity_index: " + connectivity_index(the_graph);
		write "connected_components_of: " + length(connected_components_of(the_graph));		
		write "maximal_cliques_of:" + (maximal_cliques_of(the_graph) collect (length(each)));
		write "biggest_cliques_of:" + (biggest_cliques_of(the_graph) collect (length(each)));
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
		display map background:#lightgray{
			graphics "edges" {
				loop edge over: the_graph.edges {
					draw edge + (0.1+ec[edge]/500) color: #yellow border: #black;
				}
 			}
 			species people aspect: centrality;
		}
	}
}
