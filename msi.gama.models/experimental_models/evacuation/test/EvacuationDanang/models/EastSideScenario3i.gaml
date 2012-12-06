/**
 *  EastSideScenario3i
 *  Author: lvminh
 *  Description: 
 */

model EastSideScenario3i

global {
	file shape_file_roadlines  <- file('../includes/danang.eastside.roadlines.shp');
	file shape_file_places  <- file('../includes/danang.eastside.highgrounds.shp');
	file shape_file_busstations <- file('../includes/danang.eastside.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.eastside.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.eastside.busstops.shp');
	file shape_file_buildings <- file('../includes/danang.eastside.buildings.shp');
	string output_folder <- "../outputs/EastSideScenario3/";
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float pedestrian_max_speed <- 1.5 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_range <- 5.0 min: 2.0 max: 10.0 parameter: 'Density for considering range' category: 'Pedestrian';
	float max_density <- 5.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Pedestrian';
	int bus_max_capacity <- 50 min: 20 parameter: 'Max capacity of bus (number of people)' category: 'Bus';
	float bus_max_speed <- 11.1 min: 0.1 parameter: 'Max speed (m per s)' category: 'Bus';
	int building_max_capacity <- 112 min: 20 parameter: 'Max capacity of buildind (number of people)' category: 'Building';
	float bus_length <- 5.0;
	float bus_width <- 2.0;
	float moto_length <- 3.0; 
	float moto_width <- 1.0;
	graph the_graph;
	int eastern_beach_population <- 100;
	int northern_beach_population <- 1;
	int number_of_bus_at_eastern_station <- 10;
	int number_of_bus_at_northern_station <- 1;
	int last_arrival_time <- 0;
	int number_of_survival <- 0;
	geometry creating_area_geometry;
	list obstacle_specy_list <- [species(wheelvehicle), species(pedestrian)];
	list background_sepcies_for_pedestrian <- [species(roadwidth)];
	list background_species_for_vehicle <- [species(roadwidth), species(busstation)];
	string log_to_save;
	list dead_pedestrians <- [];
	list live_buses <- [];
	replayer myreplayer;
	int pedestrian_population <- 0;
	init{
		create replayer number:1;
		set myreplayer <- first(replayer);
		let bus_initial_locations type: list <- myreplayer read_replay[file_name::"../includes/BusInitialLocations.dat"];
		do write message: ""+bus_initial_locations;
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		let creating_rows type: geometry <- nil;
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
				set width <- 5;
				create roadwidth {
					set shape <- myself.shape buffer 5;
					set creating_rows <- shape + creating_rows;
				}
			}
		}
		set the_graph <- as_edge_graph (list(roadline));
		create highground from: shape_file_places;
		create busstation from: shape_file_busstations;
		create beach from: shape_file_beaches;
		create building from: shape_file_buildings{
			set availability <- building_max_capacity;
			set shape <- shape buffer 3;
		}
		create busstop from: shape_file_busstops with: [zone_name:: read('zoneName'), priority:: read('priority')];
		set creating_area_geometry <- (list(beach) at 0);
		set creating_area_geometry <- creating_area_geometry inter creating_rows;
		let point_list type: list of: point <- creating_area_geometry.points; 
		//do write message: "points: " + string(point_list);
		let first_point type: point <- first(point_list);
		let x_min type: float <- first_point.x;
		let x_max type: float <- first_point.x;
		let y_min type: float <- first_point.y;
		let y_max type: float <- first_point.y;
		loop current_point over: point_list{
			if (x_min > current_point.x){
				set x_min <- current_point.x;
			}
			if (x_max < current_point.x){
				set x_max <- current_point.x;
			}
			if (y_min > current_point.y){
				set y_min <- current_point.y;
			}
			if (y_max < current_point.y){
				set y_max <- current_point.y;
			}
		}
		let geometry_min_point type: point <- {x_min, y_min};
		let geometry_max_point type: point <- {x_max, y_max};
		let creating_point type: point <- geometry_min_point;
		let agent_id type: int <- 0;
		let continue_creating type: bool <- true;
		loop while: (continue_creating and (agent_id < eastern_beach_population)){
			create pedestrian number: 1{
				set shape <- circle(0.6);
				set occupation_space <- 1;
				set target <- nil;
				set speed <- pedestrian_max_speed;
				set obstacle_species <- obstacle_specy_list;
				set background_species <- background_sepcies_for_pedestrian;
				set considering_range <- 30;
				set color <- rgb('green');
				set location <- creating_point;
				loop while: not(self covered_by creating_area_geometry){
					set creating_point <- self new_point[current_point::creating_point, min_point::geometry_min_point, max_point::geometry_max_point, gap::3];
					if (creating_point != nil){
						set location <- creating_point;
					}
					else{
						set continue_creating <- false;
					}
				}
				set agent_id <- agent_id + 1;
				if (agent_id mod 100 = 0){
					do write message: "Created agent " + agent_id + " at " + string(creating_point);				
				}
				set creating_point <- self new_point[current_point::creating_point, min_point::geometry_min_point, max_point::geometry_max_point, gap::3];
				set heading <- 90;
			}	
		}
		do write message: "pop: " + agent_id;
		set pedestrian_population <- agent_id;
		
		create wheelvehicle number: number_of_bus_at_eastern_station{
			let bus_location type: point <- one_of(bus_initial_locations);
			set location <- bus_location;
			set bus_initial_locations <- bus_initial_locations - bus_location; 
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set occupation_space <- 20;
			set target <- last(list(busstop));
			set speed <- bus_max_speed;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_vehicle;
			set considering_range <- 30;
			set color <- rgb('yellow');
			let current_agent_infor type: list <- [];
    		add name to: current_agent_infor;
    		add location to: current_agent_infor;
    		add heading to: current_agent_infor;
    		add current_agent_infor to: live_buses;
		}
		/* save static agents *
    	let filename type: string <- "";
    	set filename <- output_folder + "danangeastside_roadwidth_static.shp";
    	save roadwidth to: (filename) type: "shp";
    	set filename <- output_folder + "danangeastside_building_static.shp";
    	save building to: (filename) type: "shp";
    	set filename <- output_folder + "danangeastside_beach_static.shp";
    	save beach to: (filename) type: "shp";
    	set filename <- output_folder + "danangeastside_roadline_static.shp";
    	save roadline to: (filename) type: "shp";
    	set filename <- output_folder + "danangeastside_pedestrian_static.shp";
    	save pedestrian to: (filename) type: "shp";
    	write message: "write shape file ok";
	    /* */
	}
	reflex stop_simulation when: ((number_of_survival >= pedestrian_population) or (time = 7200)){
		do halt;
    }
    /* pause for changing point of view *
    reflex pause_simulation when: (time mod 600 = 0){
    	do pause;
    }
    /* */
    reflex save_dynamic_agents{
		let live_pedestrians type: list <- [];
    	//time: agentid, position, heading
    	loop item over: list(pedestrian){
    		let current_pedestrian type: pedestrian <- pedestrian(item);
    		let current_agent_infor type: list <- [];
    		add current_pedestrian.name to: current_agent_infor;
    		add current_pedestrian.location to: current_agent_infor;
    		add current_agent_infor to: live_pedestrians;
    	}
    	let infor_2_save type: list <- [];
    	let time_step type: int <- int(time);
    	add time_step to: infor_2_save;
    	add dead_pedestrians to: infor_2_save;
    	add live_pedestrians to: infor_2_save;
    	let filename type: string <- "pedestrian" + time_step + ".rep";
    	ask myreplayer{
    		do save_replay file_name: filename agent_information: infor_2_save;
    	}
    	set filename <- "bus" + time_step + ".rep";
    	ask myreplayer{
    		do save_replay file_name: filename agent_information: live_buses;
    	}
    	set dead_pedestrians <- [];
    	set live_buses <- [];
    }
    /* */
}

environment bounds: shape_file_roadlines;

entities {
	species roadline{
		float width;
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species roadwidth{
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species mylocation parent: occupationagent{
		int location_type <- 0;
		/* 1: building
		 * 2: busstop
		 * 3: highground
		 */
	}
	species highground parent: mylocation{
		string location_name;
		rgb color <- rgb('blue');
		init{
			set location_type <- 3;
		}
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species busstation {
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species busstop parent: mylocation{
		init{
			set location_type <- 2;
		}
		list waiting_passengers <- [];
		rgb color <- rgb('red');
		string zone_name <- "";
		int priority <- 0;
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species beach{
		rgb color <- rgb([120, 120, 255]);
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species building parent: mylocation{
		init{
			set location_type <- 1;
		}
		int availability <- building_max_capacity;
		rgb color <- rgb('orange');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species occupationagent{
		float occupation_space <- 1.0;
		action compute_speed type: float { //coded by Vo Ducan, modified by Van-Minh LE
			arg max_density type: float;
			arg max_speed type: float;
			arg local_density_range type: float;
			
			let local_density_window_area type: float <- local_density_range*local_density_range*3.14;			
			let obstacle_agents type: list <- (occupationagent at_distance local_density_range) collect (each.occupation_space);
			let obstacle_space type: float <- 0;
			if ((obstacle_agents != nil) and (length(obstacle_agents) > 0)){
				set obstacle_space <- sum(obstacle_agents);
			}
			if (obstacle_space = 0) { 
				return max_speed;
			}		
			let local_density <- obstacle_space / local_density_window_area;
			if (local_density >= max_density) {  return 0; }	
			else {
				let return_speed type: float <- max_speed * ( 1 - (local_density / max_density) );
				return return_speed;
			}
		}
		action new_point type: point{
			arg current_point type: point;
			arg min_point type: point;
			arg max_point type: point;
			arg gap type: float;
			
			let return_point type: point <- {current_point.x + gap, current_point.y};
			if (return_point.x > max_point.x){
				set return_point <- {min_point.x, current_point.y + gap};
			}
			if (return_point.y > max_point.y){
				return nil;
			}
			return return_point;
		}
	}
	species replayer skills: [driving2d]{
		
	}
	species wheelvehicle parent: occupationagent skills: [driving2d]{
		float length <- bus_length;
		float width <- bus_width;
		rgb color <- rgb('yellow');
		mylocation target <- nil;
		int behavior_id <- 1;
		int vehicle_availability <- bus_max_capacity;
		int target_busstop_index <- 0;
		bool is_last_busstop <- false;
		reflex bus_behavior{
			let pre_heading type: int <- self.heading;
			switch behavior_id{
				match 1{ // initially, select the busstop
					//do write message: "behavior id: " + behavior_id;
					if (length(pedestrian) > 0){
						set target <- (list(pedestrian) with_min_of (self distance_to each));
						set behavior_id <- 2;
					}
					else{
						set behavior_id <- 3;
						//do write message: "status 1: no pedestrian";
					}
				}
				match 2{ //goto the agent
					//do write message: "behavior id: " + behavior_id;
					if ((target != nil) and (not dead(target))){
						//set speed <- self compute_speed [max_density::bus_max_density, max_speed::bus_max_speed, local_density_range::max_local_density_range];
						let moving_status type: int <- -1;
						do vehicle_goto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
						if (int(moving_status) > 0){
							set behavior_id <- 3;
						}
					}
					else{
						set behavior_id <- 1;
						//do write message: "status 2: target pedestrian arrived shelter";
					}
				}
				match 3{ // waiting for picking up people
					//do write message: "behavior id: " + behavior_id;
					if ((target != nil)  and (not dead(target))){
						let pedestrian_target type: pedestrian <- pedestrian(target);
						if ((vehicle_availability > 0)){
							set vehicle_availability <- vehicle_availability -1;
							ask pedestrian_target{
								add name to: dead_pedestrians;
								do die;
							}
							//do write message: "bus available : " + vehicle_availability;
						}
						if (vehicle_availability = 0){
							set behavior_id <- 4;
						}
						else{
							set behavior_id <- 1;
						}	
					}
					else{
						set behavior_id <- 1;
						//do write message: "status 3: stuck";
					}
				}
				match 4{ // select shelter
					//do write message: "behavior id: " + behavior_id;
					let available_building type: list of: building <- (list(building) where (each.availability > 0));
					if ((available_building != nil) and (length(available_building) > 0)){
						set target <- (available_building with_min_of (each distance_to self));
					}
					else{
						set target <- (list(highground) with_min_of (each distance_to self));
					}
					if (target != nil){
						set behavior_id <- 5;
					}
					else{
						//do write message: "status 4: can not find any shelter";
					}
				}
				match 5{
					//do write message: "behavior id: " + behavior_id;
					if (target != nil){
						//set speed <- self compute_speed [max_density::bus_max_density, max_speed::bus_max_speed, local_density_range::max_local_density_range];
						let moving_status type: int <- -1;
						do vehicle_goto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
						if (int(moving_status) > 0){
							set behavior_id <- 6;
						}
						if (target.location_type = 1){
							let building_target type: building <- building(target);
							if (building_target.availability = 0){
								set behavior_id <- 4;
							}
						}
					}
					else{
						//do write message: "status 5: stuck";
					}
				}
				match 6{
					//do write message: "behavior id: " + behavior_id;
					if (target != nil){
						if (target.location_type = 1){ //building
							let building_target type: building <- building(target);
							if ((vehicle_availability < bus_max_capacity) and (building_target.availability > 0)){
								set vehicle_availability <- vehicle_availability + 1;
								ask building_target{
									set availability <- availability - 1;
								}
							}
							else{
								if (vehicle_availability < bus_max_capacity){
									set behavior_id <- 4;
								}
								else{
									set target_busstop_index <- 0;
									set behavior_id <- 1;
								}
							}
						}
						else{ //highground
							if (vehicle_availability < bus_max_capacity){
								set vehicle_availability <- vehicle_availability + 1;
							}
							else{
								set target_busstop_index <- 0;
								set behavior_id <- 1;
							}
						}
						set last_arrival_time <- time;
						set number_of_survival <- (number_of_survival + 1);
						//do write message: "at: " + last_arrival_time + ", " + number_of_survival + "th person arrive shelters by bus";
					}
					else{
						//do write message: "status 6: stuck";
					}
				}
			}
			let post_heading type: int <- self.heading;
			let rotate_angle type: int <- post_heading - pre_heading;
			let current_agent_infor type: list <- [];
    		add self.name to: current_agent_infor;
    		add self.location to: current_agent_infor;
    		add rotate_angle to: current_agent_infor;
    		add current_agent_infor to: live_buses;
		}
		aspect base{
			draw shape: geometry color: color;
		}
	}
	species pedestrian parent: mylocation skills: [driving2d]{
		building target <- nil;
		float size <- 1.0;
		rgb color <- rgb('yellow');
		float local_density <- 0.0;
		list shelters of: building <- (building as list);
		reflex select_target when: ((target = nil) or ((target != nil) and (target.availability = 0))){
			let available_shelters type: list of: building <- ((list(shelters) where (each.availability > 0)) sort_by (self distance_to each));
			if ((available_shelters != nil) and (length(available_shelters)>0)){
				set target <- first(available_shelters);	
				//do write message: "shelter location" + target;	
			}
			else{
				//do write message: "can not find shelter!";
			}
		}
		/* for driving skill */
		reflex move_with_pedestrian_goto when: (target != nil){
			
			let moving_status type: int <- -1;
			do pedestrian_goto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
			if (int(moving_status) > 0){
				//do write message: "arrived target";
				ask target {
					set availability <- (availability - 1);
					//do write message: "new availability: " + availability;
				}
				set last_arrival_time <- time;
				set number_of_survival <- (number_of_survival + 1);
				//do write message: "at: " + last_arrival_time + ", " + number_of_survival + " arrive shelters";
				add self.name to: dead_pedestrians;
				do die;
			}
		}
		/* */
		aspect base{
			draw shape: geometry color: color z: 1.5;
		}
	}
}

experiment EastSideScenario3i type: gui {
	output{
		/* *
		display city_display refresh_every: 1{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species highground aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.02;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			
			species building aspect: base;
		}
		/* */
		display city_display_with_opengl refresh_every: 1 type: opengl{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species highground aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.02;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			//species busstop aspect: base transparency: 0.02;
			species building aspect: base;
		}
		/* */
	}
}
