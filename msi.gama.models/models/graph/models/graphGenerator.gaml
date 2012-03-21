/**
 *  testGraph
 *  Author: 
 *  Description: 
 */

model testGraph
 
global { 
	graph_manager graph_util;
		init { 
			create graph_manager returns: graph_util;
			ask graph_util {
				//do generate_barabasi_graph  nb_links: 3 nb_nodes: 100;
				do load_graph_from_pajek edge_species: edgeSpecy vertex_species: nodeSpecy file: "/home/sammy/workspaceMod/msi.gama.models/models/graph/includes/ProteinSimple.dgs";
			}
		}
	/** Insert the global definitions, parameters and actions here */
}

environment {
	/** Insert the grid or gis environment(s) in which the agents will be located */
}

species graph_manager skills: ["graph_user"] {}
entities {
	/** Insert here the definition of the species of agents */
	species nodeSpecy  skills: ["moving"]{
		rgb color <- rgb('black') ;
		aspect base {
			draw shape: circle size:3 color: color ;
		}
		
		reflex {
			do wander;
		}
		
	}
	species edgeSpecy  {
		rgb color <- rgb('blue') ;
		
		aspect base {
			draw color: color ;
		}
	}
}

output {
	display test_display refresh_every: 1 {
		species nodeSpecy aspect: base ;
		species edgeSpecy aspect: base ;
	}
}