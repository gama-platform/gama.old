/**
* Name: GraphAttributesImportation
* Author: K. Johnson and B. Gaudou
* Description:  Shows how to load graph files and import attributes. 
* Tags: Graph, File
*/

model GraphAttributesImportation

global {
	
	string type <- "graphml" among: ["graphml"] parameter: true; 
	
	graph<node_graph,edge_graph> g;
	
	init {
		do importation;		
	}
	
	reflex reimport {
		do importation;
	}
	
	action importation {	
		// Up to now, attributes importation are only available for graphml	datafiles.
		switch type {
			match "graphml" {
				// att and attEdges are the name of the attribute off the nodde_graph and edge_graph species
	   			g <- graphml_file("../includes/graphs/agents-attributes.graphml", node_graph, edge_graph,"att","attEdges").contents; 
			}
		}
			
		ask node_graph {
	   		do init_agt;
		}
		ask edge_graph {
			do init_agt;
		}
		
		write g;		
	}
}

species edge_graph {   
	float prob <- 0.0;
	rgb my_color <- #green;
    map<string,string> attEdges;	
    
    action init_agt {
    	name <- attEdges["name"]; 
    	prob <- float(attEdges["prob"]);		
    }
}

species node_graph {
    map<string,string> att;
    
    action init_agt {
    	location <- {att["xpoint"] as float,att["ypoint"] as float}; 		
    }
}

experiment import_graph type: gui {
	output {
		display graph_display type: 3d axes: false{
			graphics "graph " {
				
				loop v over: g.vertices {
					draw circle(1) at: v.location color: #red border: #black;
				}
				loop e over: g.edges {
					node_graph s <- g source_of e;
					node_graph t <- g target_of e;
					draw line([s.location, t.location]) color: #black end_arrow: 1.0;
				}
			}
		}
	}	
}
