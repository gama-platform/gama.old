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
				do generate_barabasi_graph  nb_links: 3 nb_nodes: 100;
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
}

output {
	/** Insert here the definition of the different outputs shown during the simulations */
}