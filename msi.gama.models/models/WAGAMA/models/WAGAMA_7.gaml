/**
 *  WAGAMA7
 *  Author: patricktaillandier
 *  Description: taking into account of severals sources with a synchronization at the meeting point
 */

model WAGAMA7
 
global {
	
	file nodes_file <- file('../includes/nodes.shp'); 
	file env_file <- file('../includes/environment.shp');
	list nodes of: node function: {node as list};
	int input_water_quantity <- 20;

	init {
		create node from: nodes_file with: [id::string(read("ID")), id_next::string(read("ID_NEXT")), source::string(read("SOURCE"))];
		ask nodes {
			set next_node <- nodes first_with (each.id = id_next);
		}
		ask nodes {
			if (source = "Yes" ) {
				set nb_inputs <- 1;
			} else {
				set nb_inputs <- nodes count (each.next_node = self);
			}
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
		ask nodes where (length(each.waters) >= each.nb_inputs){
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
		int nb_inputs;
		
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
				let waterAg type: water <- self water_merge [];
				ask next_node {
					do accept_water water_input: waterAg;	
				}
			}
			set waters <- [];		
		}
		
		action water_merge {
			let waterAg type: water <- nil;
			if (length(waters) > 1) {
				create water returns: water_created;
				set waterAg <- first(water_created);
				ask waters {
					set waterAg.water_units <- waterAg.water_units union water_units;
					do die;
				}
			} else {
				set waterAg <- first(waters);
			}
			return waterAg;
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
		monitor 'Water quantity' value: length(water_unit as list) ;
		display dynamic {
			species node aspect: network;
			species water aspect: quantity_quality;
		}
	}
}