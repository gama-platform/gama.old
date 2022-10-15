/**
* Name: GraphImportation
* Author: P. Taillandier
* Description:  Shows how to load diverse graph files. 
* Tags: Graph, File
*/

model GraphImportation

global {
	
	string type <- "graphml" among: ["graphml", "gml","dot", "dimacs", "gexf", "tsplib", "graph6"] parameter: true; 
	
	map loc_nodes;
	
	graph g;
	init {
		do importation;
	}
	reflex reimport {
		do importation;
	}
	action importation {
		
		switch type {
			match "graphml" {
				g <- graphml_file("../includes/graphs/simple.graphml").contents;
			}
			match "gml" {
				g <- graphgml_file("../includes/graphs/simple.gml").contents;
			}
			match "dot" {
				g <- graphdot_file("../includes/graphs/simple.dot").contents;
			}
			match "dimacs" {
				g <- graphdimacs_file("../includes/graphs/simple.dimacs").contents;
			}	
			match "gexf" {
				g <- graphgexf_file("../includes/graphs/simple.gexf").contents;
			}
			match "tsplib" {
				g <- graphtsplib_file("../includes/graphs/simple.tsplib").contents;
			}
			match "graph6" {
				g <- graph6_file("../includes/graphs/simple.g6").contents;
			}	
		}
		write g;
		
		loop v over: g.vertices {
			loc_nodes[v] <- any_location_in(world);	
		} 
	}
}

experiment import_graph type: gui {
	output {
		display graph_display type: 3d axes: false{
			graphics "graph " {
				
				loop v over: g.vertices {
					draw circle(1) at: point(loc_nodes[v]) color: #red border: #black;
				}
				loop e over: g.edges {
					string s <- g source_of e;
					string t <- g target_of e;
					draw line([point(loc_nodes[s]),  point(loc_nodes[t])]) color: #black end_arrow: 1.0;
				}
			}
		}
	}	
}
