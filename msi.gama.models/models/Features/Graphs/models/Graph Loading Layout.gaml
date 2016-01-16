/**
* Name: Graph from DGS File and Layout Changed
* Author: Patrick Taillandier
* Description: Model which shows how to load a graph from a DGS File, and change is layout. 
* Tag : Graph, Import Files
*/

model graphloadinglayout

global {
	graph the_graph ;
	string barabasi_file <- "../includes/ProteinSimple.dgs";
	geometry shape <- rectangle(500,500);
	string layout_type <- "forcedirected";
	int layout_time <- 1000;
	
	//The operator load_graph_from_file generates the graph from the file, and chose the vertices as agents of node_agent 
	//species, and edges as edge_agent agents
	init {
		the_graph <- load_graph_from_file(barabasi_file, node_agent, edge_agent);
	}
	
	//In case the layout type is forcedirected or random, the reflex will change at each step the layout of the graph
	reflex layout_graph {
		the_graph <- layout(the_graph, layout_type, layout_time);
	}
}

species edge_agent {
	aspect default {	
		draw shape color: #black;
	}
}

species node_agent {
	aspect default {	
		draw circle(2) color: #red;
	}
}

experiment loadgraph type: gui {
	parameter "Layout type" var: layout_type among: [ "forcedirected", "random", "radialtree", "circle"];
	parameter "layout time" var: layout_time min: 1 max: 100000;
	output {
		display map type: opengl{
			species edge_agent ;
			species node_agent ;
		}
	}
}
