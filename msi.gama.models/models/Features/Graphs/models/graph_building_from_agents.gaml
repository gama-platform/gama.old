/**
 *  graphbuilding
 *  Author: Patrick Taillandier
 *  Description: Show how to create a spatial graph from agents
 */

model graphbuilding

global {
	graph the_graph ;
	string graph_builing_type <- "distance";
	init {
		create dummy_agent number: 30; 
		switch graph_builing_type {
			match "distance" {
				the_graph <- as_distance_graph(dummy_agent, 20);	
			}
			match "inetersection" {
				the_graph <- as_intersection_graph(dummy_agent, 0.01);	
			}	
		}
	}
	
}

species dummy_agent {
	geometry shape <- square (5);
	aspect default {	
		draw shape color: rgb("red");
	}
}

experiment loadgraph type: gui {
	parameter "Method to build the graph" var: graph_builing_type among: [ "distance", "inetersection"];
	
	output {
		display map {
			species dummy_agent ;
			graphics "the graph" {
				loop edge over: the_graph.edges {
					draw edge color: rgb("blue");
				}
			}
		}
	}
}
