/**
 *  WAGAMA3
 *  Author: patricktaillandier
 *  Description: new species definition (water); action definition (water_input and accept_water); button definition
 */

model WAGAMA3
 
global {
	
	file nodes_file <- file('../includes/nodes_simple.shp'); 
	file env_file <- file('../includes/environment.shp');
	list nodes of: node function: {list(node)};
	
	init {
		create node from: nodes_file with: [id::read("ID"), id_next::read("ID_NEXT"), source::read("SOURCE")];
		ask nodes {
			set next_node <- nodes first_with (each.id = id_next);
		}
	}
	
	action water_input {
		ask nodes where (each.source = "Yes" ) {
			create water returns: water_created;
			do accept_water water_input: first(water_created);
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
		list waters of: water;
		
		aspect circle {
			draw shape: circle size: radius color: color;
		}
		aspect network {
			if (next_node != nil) {
				draw geometry: line([location, next_node.location]) color: rgb('blue');
			}
			draw shape: circle size: radius color: color;
		}
		
		action accept_water {
			arg water_input type: water;
			add water_input to: waters;
			set water_input.location <- self.location;
		}
	}
	
	species water {
		aspect default{
			draw shape: circle size: 5 color: rgb('blue');
		}	
	}
}

experiment with_interface type: gui {
	parameter 'GIS file of the nodes' var: nodes_file category: 'GIS';
	parameter 'GIS file of the environment' var: env_file category: 'GIS';
	user_command "Add water" action: water_input; 
	output {
		display dynamic {
			species node aspect: network;
			species water aspect: default;
		}
	}
}