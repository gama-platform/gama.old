/**
 *  graphbuilding
 *  Author: Patrick Taillandier
 *  Description: Show how to create a spatial graph from agents
 */

model graphbuilding

global {
	graph the_graph ;
	string graph_builing_type <- "distance";
	float distance <- 20.0;
	float tolerance <- 0.0;
	init {
		create dummy_agent number: 30; 
		switch graph_builing_type {
			match "distance" {
				the_graph <- as_distance_graph(dummy_agent, distance);	
			}
			match "intersection" {
				the_graph <- as_intersection_graph(dummy_agent, tolerance);	
			}	
		}
		write "nb vertices: " + length(the_graph.vertices);
		write "nb edges: " + length(the_graph.edges);
	}
	
}

species dummy_agent {
	geometry shape <- square (5);
	aspect default {	
		draw shape color: rgb("red");
	}
}

experiment loadgraph type: gui {
	parameter "Method to build the graph" var: graph_builing_type among: [ "distance", "intersection"];
	parameter "Tolerance" var: tolerance min: 0.0 max: 2.0 category: "Intersection";
	parameter "Distance" var: distance min: 1.0 max: 50.0 category: "Distance";
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
