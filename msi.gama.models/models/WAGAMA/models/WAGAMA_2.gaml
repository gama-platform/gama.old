/**
 *  WAGAMA2
 *  Author: patricktaillandier
 *  Description: attribut reading from GIS data; more complex agent aspect ; use of the function facet
 */

model WAGAMA2
 
global {
	
	file nodes_file <- file('../includes/nodes_simple.shp'); 
	file env_file <- file('../includes/environment.shp');
	list nodes of: node function: {node as list};
	
	init {
		create node from: nodes_file with: [id::read("ID"), id_next::read("ID_NEXT"), source::read("SOURCE")];
		ask nodes {
			set next_node <- nodes first_with (each.id = id_next);
		}
	}
}

environment bounds: env_file;

entities {
	species node {
		const radius type: float <- 2.0;
		rgb color <- rgb('white');
		string id;
		string id_next;
		string source;
		node next_node;
		
		aspect circle {
			draw shape: circle size: radius color: color;
		}
		aspect network {
			if (next_node != nil) {
				draw geometry: line([location, next_node.location]) color: rgb('blue');
			}
			draw shape: circle size: radius color: color;
		}
	}
}

experiment with_interface type: gui {
	parameter 'GIS file of the nodes' var: nodes_file category: 'GIS';
	parameter 'GIS file of the environment' var: env_file category: 'GIS';
	output {
		display dynamic {
			species node aspect: network;
		}
	}
}