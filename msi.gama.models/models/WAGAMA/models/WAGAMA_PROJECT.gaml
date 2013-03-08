/**
 *  WAGAMA16
 *  Author: patricktaillandier
 *  Description: new actions for the owner agents
 */

model WAGAMA16


global {
	
	file nodes_file <- file('../includes/nodes.shp'); 
	file env_file <- file('../includes/environment.shp');
	file activities_file <- file('../includes/activities.shp');
	file owners_data <- file('../includes/owners_data.csv');
	file activity_type_data <- file('../includes/activity_type_data.csv');
	string result_file <- 'results.csv';
	
	list nodes of: node function: {node as list};
	list activities of: activity  function: {activity as list};
	list owners of: owner  function: {owner as list};
	list activity_types of: activity_type  function: {activity_type as list};
	administrator the_administrator;
	
	activity current_activity <- nil;
	activity_type current_activity_type <- nil;
	
	
	int total_water_quantity function: {length(water_unit as list)};
	int clean_water_quantity function: {(water_unit as list) count (!each.polluted)};
	int polluted_water_quantity function: {(water_unit as list) count (each.polluted)};
	float mean_money function: {mean(owners collect (float(each.money)))};
	int max_money function: {max(owners collect (each.money))};
	int min_money function: {min(owners collect (each.money))};
	
	int input_water_quantity_north <- 20;
	int input_water_quantity_south <- 20;
	int output_water_quantity_real;
	int output_clean_water_quantity_real;
	int output_polluted_water_quantity_real;
	int output_water_quantity_wished <- 5;
	
	float cleanness_rate_objective <- 0.7;
	int output_water_quantity_objective <- 5;
	
	init {
		create administrator returns: administrator_created;
		set the_administrator <- first(administrator_created);
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
		do water_input;
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
				let input_water_quantity type: int <- input_water_quantity_south ;
				if (myself.id = '1') {
					set input_water_quantity <- input_water_quantity_north;
				}
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

	action save_outputs {
		save [input_water_quantity_north, input_water_quantity_south, output_water_quantity_real, output_clean_water_quantity_real, output_polluted_water_quantity_real,
			mean_money,max_money,min_money] type: "csv" to: result_file;
	}
	
	action repair_activity{
		ask current_activity 
		{ 
			do repair;
		}
	}
	
	action change_activity_type{
		ask current_activity 
		{ 
			do change_type new_type: current_activity_type;
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
				set output_water_quantity_real <- 0;
				set output_clean_water_quantity_real <- 0;
				set output_polluted_water_quantity_real <- 0;
				ask waters {
					set output_water_quantity_real <- output_water_quantity_real + quantity;
					set output_clean_water_quantity_real <- output_clean_water_quantity_real + quantity_clean ;
					set output_polluted_water_quantity_real <- output_polluted_water_quantity_real + quantity_polluted;
					ask water_units {
						do die;
					}
					do die;
				}
				set the_administrator.decision_time <- true;
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
		
		action repair{
			if (dysfunction) and (my_owner.money >= type.money_cost) {
					set my_owner.money <- my_owner.money - type.money_cost;
					set dysfunction <- false;
			}
		}
		
		action change_type {
			arg new_type type: activity_type;
			let change_cost type: int <- type.money_cost + new_type.money_cost;
			if (my_owner.money  >= change_cost) {
				set my_owner.money  <- my_owner.money  - change_cost;
				set type <- new_type;
			}
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
		bool decision_time <- false;
		
		reflex take_measures when: decision_time {
			/* TO COMPLETE  */
			set decision_time <- false;
		}
	}	
	
	species administrator {
		int pollution_tax <- 2;
		int water_tax <- 2;
		int ecological_bonus <- 2;
		bool decision_time <- false; 
		
		action tax_polluters {
			ask (activities where (each.type.excessive_pollution)) {
				let to_pay type: int <- min([myself.pollution_tax, my_owner.money]);
				set my_owner.money <- my_owner.money - to_pay;
			}
		}
		action tax_water_users {
			ask (activities where (each.type.excessive_water)) {
				let to_pay type: int <- min([myself.water_tax, my_owner.money]);
				set my_owner.money <- my_owner.money - to_pay;
			}
		}
		action favor_green_activities {
			ask (activities where (each.type.green_activity)) {
				set my_owner.money <- my_owner.money + myself.ecological_bonus;
			}
		}
		
		reflex take_measures when: decision_time {
			let cleanness_rate type: float 
				<- output_water_quantity_real > 0 ? output_clean_water_quantity_real  / output_water_quantity_real : 1;
			let message_to_write type: string 
				<-"**********************\nTotal quantity of water : " + output_water_quantity_real + "\nCleanness rate : " + cleanness_rate;
			if (output_water_quantity_real  < output_water_quantity_objective)
			{
				do tax_water_users;
				set message_to_write <- message_to_write + "\nMeasure taken : tax the activities that consumn too much water";
			}
			if (cleanness_rate < cleanness_rate_objective)
			{
				if flip(0.5) {
					do tax_polluters;	
					set message_to_write <- message_to_write + "\nMeasure taken : tax the activities that pollute too much";
				} else {
					do favor_green_activities;	
					set message_to_write <- message_to_write + "\nMesure taken : give a bonus to green activities";
				}
			}
			do write message: message_to_write;
			ask world {
				do save_outputs;	
			}
			ask owners {
				set decision_time <- true;
			}
			set decision_time <- false;
		}
	} 
}

experiment with_interface type: gui {
	parameter 'GIS file of the nodes' var: nodes_file category: 'GIS';
	parameter 'GIS file of the environment' var: env_file category: 'GIS';
	parameter 'GIS file of the activities' var: activities_file category: 'GIS';
	parameter 'Quantity of input water North' var: input_water_quantity_north category: 'Water';
	parameter 'Quantity of input water South' var: input_water_quantity_south category: 'Water';
	parameter 'Owners data' var: owners_data category: 'Owners';
	parameter 'Activity type data' var: activity_type_data category: 'Activities';
	parameter 'Objective concerning the output water quantity' var: output_water_quantity_objective category: 'Water';
	parameter 'Objective concerning the output water quality (cleanness rate)' var: cleanness_rate_objective category: 'Water';
	parameter 'Current activity' var: current_activity category: 'Activities';
	parameter 'Current type activity' var: current_activity_type category: 'Activities';
	parameter 'Results file' var: result_file category: 'Result';
	user_command "Add water" action: water_input; 
	user_command "Repair activity" action: repair_activity; 
	user_command "Change activity type" action: change_activity_type; 

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
		display charts { 
			chart name: 'Water quantity' type: series background: rgb('white') size: {1,0.5} position: {0, 0} {
				data total_water_quantity value: total_water_quantity color: rgb('blue') ;
				data clean_water_quantity value: clean_water_quantity color: rgb('green') ;
				data polluted_water_quantity value: polluted_water_quantity color: rgb('red') ;
			}
			chart name: 'money of the owners' type: series background: rgb('white') size: {1,0.5} position: {0, 0.5} {
				data mean_money value: mean_money color: rgb('black') ;
				data min_money value: min_money  color: rgb('red') ;
				data max_money value: max_money  color: rgb('green') ;
			}	
		}
	}
}
experiment calibration type: batch repeat: 10 keep_seed: true until: ( time > 15 ) {
	parameter 'Quantity of input water North' var: input_water_quantity_north min: 10 max: 30 step: 1;
	parameter 'Quantity of input water South' var: input_water_quantity_south min: 10 max: 30 step: 1 ;
	
	method tabu minimize: abs(output_water_quantity_real - output_water_quantity_wished) iter_max: 50 tabu_list_size: 5;
}