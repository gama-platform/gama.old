/**
 *  WAGAMA4
 *  Author: patricktaillandier
 *  Description: add water diffusion dynamic (diffusion reflex  + flow action).
 */

model WAGAMA4

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
	
	action water_input {
		ask nodes where (each.source = "Yes" ) {
			create water returns: water_created;
			do accept_water input_water: first(water_created);
		}
	}
	reflex diffusion {
		ask nodes where (!(empty(each.waters))) {
			do flow;
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
			draw circle(radius) color: color;
		}
		aspect network {
			if (next_node != nil) {
				draw geometry: line([location, next_node.location]) color: rgb('blue');
			}
			draw circle(radius) color: color;
		}
		action accept_water {
			arg input_water type: water;
			add input_water to: waters;
			set input_water.location <- self.location;
		}
		action flow {
			if (next_node = nil) {
				ask waters {
					do die;
				}
			} else {
				loop wAg over: waters {
					ask next_node {
						do accept_water input_water: wAg;
					}	
				}
			}
			set waters <- [];		
		} 
	}
	
	species water {
		aspect default{
			draw circle(5) color: rgb('blue');
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