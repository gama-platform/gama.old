/**
* Name: Save graphs
* Author: Patrick Taillandier
* Description: This is a model that shows how to save a graph with diverse formats.
* Tags: save_file, graph
*/

model Savetoshapefile

global {
	init {
		graph the_graph <- generate_random_graph(20, 20, true,node_agent, edge_agent);
		
		//save the graph with different format.
		save the_graph to:"../results/my_graph.gml" format:"gml";
		save the_graph to:"../results/my_graph.dimacs" format:"dimacs";
		save the_graph to:"../results/my_graph.dot" format:"dot";
		save the_graph to:"../results/my_graph.graphml" format:"graphml";
		save the_graph to:"../results/my_graph.g6" format: "graph6" ;
		save the_graph to:"../results/my_graph.gexf" format:"gexf" ;
		
		
	}
} 
  
//species that represent the nodes of the graph
species node_agent {
	aspect default {
		draw circle(1.0) color:#red border: #black;
	}
}

//species that represent the edges of the graph
species edge_agent {
	aspect default {
		draw shape color:#black end_arrow: 1.0;
	}
}
experiment main type: gui {
	output {
		display map {
			species node_agent;
			species edge_agent;
		}
	}
}
