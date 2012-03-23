/**
 *  testGraph
 *  Author: 
 *  Description: 
 */

model testGraph 
 
global {  
	var mongraphe type:graph;
	graph_manager graph_util;
		init { 
			create graph_manager returns: graph_util;
			ask graph_util {
				//do generate_barabasi_graph  nb_links: 3 nb_nodes: 100;
				//do load_graph_from_dgs_old edge_species: edgeSpecy vertex_species: nodeSpecy file: "../includes/BarabasiGenerated.dgs";
				
				set mongraphe value:load_graph_from_dgs_old(self, [edge_species::edgeSpecy, vertex_species::nodeSpecy,file::"../includes/BarabasiGenerated.dgs"]);
				//print mongraphe;
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
		
		/*reflex when: flip(0.1) {
			
			create nodeSpecy number: 1 {
			}
			
			do die;
		}*/
		
	}
	species edgeSpecy  {
		rgb color <- rgb('blue') ; 
		
		aspect base {
			draw color: color ;
		}
		
		/* Uncomment to test how the node evolves when an edge agent dies 
		*/
		reflex when: flip(0.1) {
			
			do die;
		}
		/* 
		*/
		
	}
}

output {
	display test_display refresh_every: 1 {
		species nodeSpecy aspect: base ; 
		species edgeSpecy aspect: base ;
	}
	graphdisplay monNom2 graph: mongraphe lowquality:true {
		 
	}
	
}