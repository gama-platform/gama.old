/**
 *  WAGAMA11
 *  Author: patricktaillandier
 *  Description: definition of different types of activities
 */

model WAGAMA11
 
global {
	
	file nodes_file <- file('../includes/nodes.shp'); 
	file env_file <- file('../includes/environment.shp');
	file activities_file <- file('../includes/activities.shp');
	file owners_data <- file('../includes/owners_data.csv');
	file activity_type_data <- file('../includes/activity_type_data.csv');
	list nodes of: node function: {node as list};
	list activities of: activity  function: {activity as list};
	list owners of: owner  function: {owner as list};
	list activity_types of: activity_type  function: {activity_type as list};
	int input_water_quantity <- 20;
	
	
	init {
		create node from: nodes_file with: [id::read("ID"), id_next::read("ID_NEXT"), source::read("SOURCE")];
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
		do load_activity_type_data;
		create activity from: activities_file with: [id::read("ID"), input_id::read("INPUT"), output_id::read("OUTPUT"), type_name::read("TYPE")];
		ask activities {
			set input_node <- nodes first_with (each.id = input_id);
			set output_node <- nodes first_with (each.id = output_id);
			set type <- activity_types first_with (each.name = type_name) ;
		}
		do load_owners_data;
	}
	
	action load_owners_data {
		let owner_matrix type: matrix <- matrix(owners_data);
		let nb_lines type: int <- length(owner_matrix column_at 0);
		loop i from: 1 to: ( nb_lines - 1 ) {
			let current_line type: list <- ( owner_matrix row_at i );
			let id_activity type: string <- current_line at 0;
			let id_owner type: string  <- current_line at 1;
			let current_activity type: activity <- activities first_with (each.id = id_activity);
			if (current_activity != nil ) {
				let current_owner type: owner <- owners first_with (each.id = id_owner);
				if (current_owner = nil) {
					create owner returns: owner_created{
						set id <- id_owner;
					}
					set current_owner <- first(owner_created);
				}
				set current_activity.my_owner <- current_owner;
				add current_activity to: current_owner.my_activities; 	
			}	
		}	
	}
	
	action load_activity_type_data {
		let activity_type_matrix type: matrix <- matrix(activity_type_data);
		let nb_lines type: int <- length(activity_type_matrix column_at 0);
		loop i from: 1 to: ( nb_lines - 1 ) {
			let current_line type: list <- ( activity_type_matrix row_at i );
			create activity_type returns: activity_types_created;
			let activity_type_created type: activity_type <- first(activity_types_created);
			set activity_type_created.name <- current_line at 0;
			set activity_type_created.clean_water_input <- current_line at 1;
			set activity_type_created.polluted_water_input <- current_line at 2;
			set activity_type_created.money_cost <- current_line at 3;
			set activity_type_created.clean_water_output <- current_line at 4;
			set activity_type_created.polluted_water_output <- current_line at 5;
			set activity_type_created.money_earned <- current_line at 6;
			set activity_type_created.excessive_water<- current_line at 7 = 'TRUE';
			set activity_type_created.excessive_pollution<- current_line at 8 = 'TRUE';
			set activity_type_created.green_activity<- current_line at 9 = 'TRUE';
			set activity_type_created.color<- current_line at 10 ;
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
				ask (activities where (each.input_node = self and !(each.dysfunction))) {
					do take_water water_in: waterAg;
				}
				ask (activities where (each.output_node = self and !(each.dysfunction))) {
					do reject_water water_out: waterAg;
				}
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
			draw shape: circle size: 5 color: rgb('blue');
		}	
		aspect quantity_quality{
			draw shape: circle size: quantity / 2 
				color: rgb([255 * quantity_polluted / quantity, 0, 255 * quantity_clean / quantity]);
		}
	}
	
	species water_unit {
		bool polluted <- false;
	}
	
	species activity {
		string id;
		string input_id;
		string output_id;
		node input_node;
		node output_node;
		rgb color function: {dysfunction ? rgb('red') : rgb('green')};
		bool dysfunction <- false;
		activity_type type;
		owner my_owner;
		string type_name;
		
		action take_water {
			arg water_in type: water;
			let wished_quantity type: int <- type.clean_water_input + type.polluted_water_input;
			let quantity type: int <- min ([water_in.quantity, wished_quantity]);
			let water_unit_taken type: list of: water_unit <- [];
			let money_lost type: int <- min ([my_owner.money, type.money_cost]);
			set my_owner.money <- my_owner.money - money_lost;
			loop times: quantity {
				let wu type: water_unit <- one_of(water_in.water_units);
				remove wu from: water_in.water_units;
				add wu to: water_unit_taken;
			}
			set dysfunction <- (quantity < wished_quantity) 
				or ((water_unit_taken count (each.polluted)) > type.polluted_water_input)
				or (money_lost < type.money_cost);
			if (!dysfunction) {
				set my_owner.money <- my_owner.money + type.money_earned;
			}
			ask water_unit_taken {
				do die;
			}
		}
		
		action reject_water {
			arg water_out type: water;
			create water_unit number: type.clean_water_output returns: wu_clean;
			create water_unit number: type.polluted_water_output returns: wu_polluted {
				set polluted <- true;
			}
			set water_out.water_units <- water_out.water_units union list(wu_clean) union list(wu_polluted);	
		}
		
		aspect default{
			draw geometry: line([location, input_node.location]) color: rgb('green');
			draw geometry: line([location, output_node.location]) color: rgb('red');
			draw geometry: shape color: color;
		}
		
		aspect activity_type{
			draw geometry: line([location, input_node.location]) color: rgb('green');
			draw geometry: line([location, output_node.location]) color: rgb('red');
			draw geometry: shape color: type.color;
			draw text: name + " : " + type.name size: 2 color: rgb('black'); 
		}
		
		aspect owners{
			draw geometry: line([location, input_node.location]) color: rgb('green');
			draw geometry: line([location, output_node.location]) color: rgb('red');
			draw geometry: shape color: my_owner.color;
		}
	}
	
	species activity_type {
		int clean_water_input <- 3;
		int polluted_water_input <- 1;
		int clean_water_output <- 0;
		int polluted_water_output <- 2;
		bool excessive_water <- false;
		bool excessive_pollution <- false;
		bool green_activity <- false;
		rgb color <- rgb('yellow');
		int money_earned <- 3;
		int money_cost <- 2;
	}
	
	species owner {
		string id;
		list my_activities of: activity;
		int money <- 10;
		rgb color <- rgb([rnd(255), rnd(255), rnd(255)]);
	}	
}

experiment with_interface type: gui {
	parameter 'GIS file of the nodes' var: nodes_file category: 'GIS';
	parameter 'GIS file of the environment' var: env_file category: 'GIS';
	parameter 'GIS file of the activities' var: activities_file category: 'GIS';
	parameter 'Quantity of input water' var: input_water_quantity category: 'Water';
	parameter 'Owners data' var: owners_data category: 'Owners';
	parameter 'Activity type data' var: activity_type_data category: 'Activities';
	user_command "Add water" action: water_input; 
	output {
		monitor 'Water quantity' value: length(water_unit as list) ;
		display dynamic {
			species node aspect: network;
			species activity aspect: default;
			species water aspect: quantity_quality;
		}
		display activity_type {
			species node aspect: network;
			species activity aspect: activity_type;
		}
		display owners {
			species node aspect: network;
			species activity aspect: owners;
		}
	}
}