/**
 *  loadgraph
 *  Author: Patrick Taillandier
 *  Description: Show how to load a graph from a file (dgs) and to apply a layout
 */

model graphloadinglayout

global {
	graph the_graph ;
	string barabasi_file <- "../includes/ProteinSimple.dgs";
	geometry shape <- rectangle(500,500);
	string layout_type <- "radialtree";
	int layout_time <- 1000;
	
	init {
		the_graph <- load_graph_from_file(barabasi_file, node_agent, edge_agent);
		
		
	}
	
	reflex layout_graph {
		the_graph <- layout(the_graph, layout_type, layout_time);
	}
}

species edge_agent {
	aspect default {	
		draw shape color: rgb("black");
	}
}

species node_agent {
	aspect default {	
		draw circle(2) color: rgb("red");
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
