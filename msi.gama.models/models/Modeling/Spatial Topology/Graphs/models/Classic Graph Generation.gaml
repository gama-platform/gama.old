/**
* Name: Graph Generation
* Author: Patrick Taillandier
* Description: Model which shows how to create three kind of graphs : a scale-free graph, a small-world graph, a complete graph and a complete graph with a radius.
* Tags: graph
*/

model classicgraphgeneration

global {
	graph the_graph ;
	string graph_type <- "small-world";
	int nb_nodes <- 30;
	float p <- 0.2;
	int k <- 4;
	int m <- 4;
	int radius <- 20;
	
	init {
		switch graph_type {
			match "scale-free" {
				the_graph <- generate_barabasi_albert(node_agent, edge_agent, nb_nodes,m, true);	
			}
			match "small-world" {
				the_graph <- generate_watts_strogatz(node_agent, edge_agent, nb_nodes, p, k, true);	
			}
			match "complete" {
				the_graph <- generate_complete_graph(node_agent, edge_agent, nb_nodes,true);	
			}
			match "complete-with-radius" {
				the_graph <- generate_complete_graph(node_agent, edge_agent, nb_nodes, radius,true);	
			}		
		}
		write the_graph;
		write "Edges : "+length(the_graph.edges);
		write "Nodes : "+length(the_graph.vertices);
	}
	
}

species edge_agent {
	aspect default {	
		draw shape color: #black;
	}
}

species node_agent {
	aspect default {	
		draw circle(1) color: #red;
	}
}

experiment loadgraph type: gui {
	parameter "Graph type" var: graph_type among: [ "scale-free", "small-world", "complete"];
	parameter "Number of nodes" var: nb_nodes min: 5 ;
	parameter "Probability to rewire an edge (beta)" var: p min: 0.0 max: 1.0 category: "small-world";
	parameter "Base degree of each node. k must be even" var: k min: 2 max: 10 category: "small-world";
	parameter "Number of edges added per novel node" var: m min: 1 max: 10 category: "scale-free";
	
	output {
		display map type: opengl{
			species edge_agent ;
			species node_agent ;
		}
	}
}
