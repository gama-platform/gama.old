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
				g <- graphml_file("../includes/simple.graphml").contents;
			}
			match "gml" {
				g <- graphgml_file("../includes/simple.gml").contents;
			}
			match "dot" {
				g <- graphdot_file("../includes/simple.dot").contents;
			}
			match "dimacs" {
				g <- graphdimacs_file("../includes/simple.dimacs").contents;
			}	
			match "gexf" {
				g <- graphgexf_file("../includes/simple.gexf").contents;
			}
			match "tsplib" {
				g <- graphtsplib_file("../includes/simple.tsplib").contents;
			}
			match "graph6" {
				g <- graph6_file("../includes/simple.g6").contents;
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
		display graph_display type: opengl draw_env: false{
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
