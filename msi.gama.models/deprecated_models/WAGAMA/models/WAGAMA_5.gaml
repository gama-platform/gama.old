/**
 *  WAGAMA5
 *  Author: patricktaillandier
 *  Description: New species definition (water_unit); adding of a list of water_unit agent to the water agents
 */

model WAGAMA5

global {
	
	file nodes_file <- file('../includes/nodes_simple.shp'); 
	file env_file <- file('../includes/environment.shp');
	list nodes of: node function: {node as list};
	int input_water_quantity <- 20;


	init {
		create node from: nodes_file with: [id::string(read("ID")), id_next::string(read("ID_NEXT")), source::string(read("SOURCE"))];
		ask nodes {
			set next_node <- nodes first_with (each.id = id_next);
		}
	}
	
	action water_input {
		ask nodes where (each.source = "Yes" ) {
			create water returns: water_created {
				create water_unit number: input_water_quantity returns: new_wu;
				set water_units <- water_units union list(new_wu);
			}
			do accept_water water_input: first(water_created);
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
			arg water_input type: water;
			add water_input to: waters;
			set water_input.location <- self.location;
		}
		action flow {
			if (next_node = nil) {
				ask waters {
					ask water_units {
						do die;
					}
					do die;
				}
			} else {
				loop wAg over: waters {
					ask next_node {
						do accept_water water_input: wAg;
					}	
				}
			}
			set waters <- [];		
		} 
	}
	
	species water {
		list water_units of: water_unit;
		int quantity function: {length(water_units)};
		int quantity_polluted function: {water_units count (each.polluted)};
		int quantity_clean function: {water_units count (!each.polluted)};
		
		aspect default{
			draw circle(5) color: rgb('blue');
		}	
		aspect quantity_quality{
			draw circle(quantity / 2) 
				color: rgb([255 * quantity_polluted / quantity, 0, 255 * quantity_clean / quantity]);
		}
	}
	
	species water_unit {
		bool polluted <- false;
	}
}

experiment with_interface type: gui {
	parameter 'GIS file of the nodes' var: nodes_file category: 'GIS';
	parameter 'GIS file of the environment' var: env_file category: 'GIS';
	parameter 'Quantity of input water' var: input_water_quantity category: 'Water';
	user_command "Add water" action: water_input; 
	output {
		display dynamic {
			species node aspect: network;
			species water aspect: quantity_quality;
		}
	}
}