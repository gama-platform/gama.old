/**
 *  testGraph
 *  Author: patricktaillandier
 *  Description: 
 */

model testGraph

global { 
	graph_manager graph_util;
		init { 
			create graph_manager returns: graph_util;
			ask graph_util {
				do load_graph_from edge_species: species(self) vertex_species: species(self) file: nil;
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
