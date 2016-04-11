/**
* Name: Graph Generation using Agents
* Author: Patrick Taillandier
* Description: Model which shows how to create a graph using agents of a species as vertices. Two kinds of generations are presented : one where the distance 
* taken into account to link two vertices by an edge, an other where the intersections of two vertices joins these vertices by an edge. 
* Tags: graph
*/

model graphbuilding

global {
	graph<geometry, geometry> the_graph ;
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
		draw shape color: #red;
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
					draw edge color: #blue;
				}
			}
		}
	}
}
