/**
 *  EastSideScenario3e
 *  Author: lvminh
 *  Description: 
 */

model EastSideScenario3e

global {
	file shape_file_roadlines  <- file('../includes/danang.eastside.roadlines.shp');
	file shape_file_places  <- file('../includes/danang.eastside.highgrounds.shp');
	file shape_file_busstations <- file('../includes/danang.eastside.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.eastside.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.eastside.busstops.shp');
	file shape_file_buildings <- file('../includes/danang.eastside.buildings.shp');
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float pedestrian_max_speed <- 1.5 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float max_local_density_range <- 11.1 min: 2.0 max: 20.0 parameter: 'Density for considering range' category: 'Pedestrian';
	float pedestrian_max_density <- 6.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Pedestrian';
	float bus_max_speed <- 11.1 min: 0.1 parameter: 'Max speed (m per s)' category: 'Bus';
	int bus_max_capacity <- 50 min: 20 parameter: 'Max capacity of bus (number of people)' category: 'Bus';
	float bus_occupation_space <- 20.0 min: 20 parameter: 'Occupation space of bus (number of people)' category: 'Bus';
	float bus_max_density <- 1.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Bus';
	int building_max_capacity <- 100 min: 20 parameter: 'Max capacity of buildind (number of people)' category: 'Building';
	float bus_length <- 10.0;
	float bus_width <- 2.0;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	int eastern_beach_population <- 10;
	int northern_beach_population <- 1;
	int number_of_bus_at_eastern_station <- 1;
	int number_of_bus_at_northern_station <- 0;
	int last_arrival_time <- 0;
	int number_of_survival <- 0;
	geometry creating_area_geometry;
	list obstacle_specy_list <- [species(wheelvehicle), species(pedestrian)];
	list background_sepcies_for_pedestrian <- [species(roadwidth), species(busstation), species(beach), species(building)];
	list background_species_for_vehicle <- [species(roadwidth), species(busstation)];
	init{
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
				set width <- 10;
				create roadwidth {
					set shape <- myself.shape buffer 15;
				}
			}
		}
		set the_graph <- as_edge_graph (list(roadline));
		create highground from: shape_file_places;
		create busstation from: shape_file_busstations;
		create beach from: shape_file_beaches;
		create building from: shape_file_buildings{
			set availability <- building_max_capacity;
			set shape <- shape buffer 5;
		}
		create busstop from: shape_file_busstops with: [zone_name:: read('zoneName'), priority:: read('priority')];
		
		set creating_area_geometry <- (list(beach) at 0);
		let point_list type: list of: point <- creating_area_geometry.points; 
		do write message: "points: " + string(point_list);
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
		create pedestrian number: eastern_beach_population{
			set shape <- polygon([{0, 0.5}, {0.3, -0.4}, {-0.3, -0.4}]);
			set occupation_space <- 1;
			set target <- nil;
			if (flip(0.5)){
				set known_targets <- list(building);	
			}
			else{
				set known_targets <- list(busstop);
			}
			set speed <- pedestrian_max_speed;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_sepcies_for_pedestrian;
			set considering_range <- 30;
			set color <- rgb('green');
			set location <- creating_point;
			loop while: not(self covered_by creating_area_geometry){
				set creating_point <- self new_point[current_point::creating_point, min_point::geometry_min_point, max_point::geometry_max_point, gap::4];
				if (creating_point != nil){
					set location <- creating_point;
				}
				else{
					do die;
				}
			}
			set agent_id <- agent_id + 1;
			if (agent_id mod 100 = 0){
				do write message: "Created agent " + agent_id + " at " + string(creating_point);				
			}
			set creating_point <- self new_point[current_point::creating_point, min_point::geometry_min_point, max_point::geometry_max_point, gap::4];
			set heading <- 90;
		}
		/* */
		set creating_area_geometry <- first(list(busstation));
		create wheelvehicle number: number_of_bus_at_eastern_station{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set occupation_space <- 20;
			set target <- last(list(busstop));
			set speed <- bus_max_speed;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_vehicle;
			set considering_range <- 30;
			set color <- rgb('green');
			let overlapping_list type: list <- (agents_overlapping(self) of_species (species(self)));
			loop while: (not(self covered_by creating_area_geometry) or ((overlapping_list != nil) and (length(overlapping_list) >0))){
				set location <- any_location_in(creating_area_geometry);
				set overlapping_list <- (agents_overlapping(self) of_species (species(self)));
			}
			set creating_area_geometry <- creating_area_geometry - shape;
		}
		/* */
	}
	reflex stop_simulation when: ((length(pedestrian) = 0) or (time = 7200)){
		do halt;
    }
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
	species mylocation{
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
		int waiting_passengers <- 0;
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
	species wheelvehicle parent: occupationagent skills: [driving]{
		float length <- bus_length;
		float width <- bus_width;
		rgb color <- rgb('yellow');
		mylocation target <- nil;
		int behavior_id <- 1;
		int vehicle_availability <- bus_max_capacity;
		int target_busstop_index <- 0;
		bool is_last_busstop <- false;
		reflex bus_behavior{
			switch behavior_id{
				match 1{ // initially, select the busstop
					do write message: "behavior id: " + behavior_id;
					let number_of_busstop type: int <-length(busstop as list); 
					if (target_busstop_index < number_of_busstop){
						set target <- (list(busstop) at target_busstop_index);
						set is_last_busstop <- (target_busstop_index = (number_of_busstop + 1)); 
						set target_busstop_index <- (target_busstop_index + 1) mod number_of_busstop;
						set behavior_id <- 2; 
					}
					else{
						do write message: "status 1: stuck at finding busstop";
					}
				}
				match 2{ //goto the busstop
					do write message: "behavior id: " + behavior_id;
					if ((target != nil) and (target.location_type = 2) ){
						//set speed <- self compute_speed [max_density::bus_max_density, max_speed::bus_max_speed, local_density_range::max_local_density_range];
						let moving_status type: int <- -1;
						do vehicleGoto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
						if (int(moving_status) > 0){
							set behavior_id <- 3;
						}
					}
					else{
						do write message: "status 2: stuck";
					}
				}
				match 3{ // waiting for picking up people
					do write message: "behavior id: " + behavior_id;
					if ((target != nil) and (target.location_type = 2) ){
						let busstop_target type: busstop <- busstop(target);
						if ((busstop_target.waiting_passengers > 0) and (vehicle_availability > 0)){
							set vehicle_availability <- vehicle_availability -1;
							ask busstop_target{
								set waiting_passengers <- waiting_passengers - 1;
							}
							do write message: "bus available : " + vehicle_availability;
						}
						else{
							if ((is_last_busstop) or (vehicle_availability = 0)){
								set behavior_id <- 4;
							}
							else{
								set behavior_id <- 1;
							}	
						}
					}
					else{
						do write message: "status 3: stuck";
					}
				}
				match 4{ // select shelter
					do write message: "behavior id: " + behavior_id;
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
						do write message: "status 4: can not find any shelter";
					}
				}
				match 5{
					do write message: "behavior id: " + behavior_id;
					if (target != nil){
						//set speed <- self compute_speed [max_density::bus_max_density, max_speed::bus_max_speed, local_density_range::max_local_density_range];
						let moving_status type: int <- -1;
						do vehicleGoto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
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
						do write message: "status 5: stuck";
					}
				}
				match 6{
					do write message: "behavior id: " + behavior_id;
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
						do write message: "at: " + last_arrival_time + ", " + number_of_survival + "th person arrive shelters by bus";
					}
					else{
						do write message: "status 6: stuck";
					}
				}
			}
		}
		aspect base{
			draw shape: geometry color: color;
		}
	}
	species pedestrian parent: occupationagent skills: [driving]{
		mylocation target <- nil;
		float size <- 1.0;
		rgb color <- rgb('yellow');
		float local_density <- 0.0;
		list known_targets of: mylocation <- nil;
		reflex select_target when: ((target = nil)){
			if ((known_targets != nil) and (length(known_targets) > 0)){
				if (first(known_targets).location_type = 1){
					set target <- ((list(known_targets) where (building(each).availability>0)) with_min_of (self distance_to each));
				}
				else{
					set target <- (list(known_targets) with_min_of ((self distance_to each)));	
				}					
			}
		}
		/* for driving skill */
		reflex move_with_vehicleGoto when: ((target != nil)){			
			//set speed <- self compute_speed [max_density::pedestrian_max_density, max_speed::pedestrian_max_speed, local_density_range::max_local_density_range];
			let moving_status type: int <- -1;
			do vehicleGoto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
			if (target.location_type = 1){
				if (building(target).availability  = 0){
					set target <- nil;
				}
			}
			if (int(moving_status) > 0){
				do write message: "arrived target";
				if ((target != nil) and (target.location_type = 1)){
					let building_target type: building <- building(target);
					ask building_target {
						set availability <- (availability - 1);
						//do write message: "new availability: " + availability;
					}
					set last_arrival_time <- time;
					set number_of_survival <- (number_of_survival + 1);
					do write message: "at: " + last_arrival_time + ", " + number_of_survival + "th person arrive shelters on foot";
				}
				else if ((target != nil) and (target.location_type = 2)){
					let bustop_target type: busstop <- busstop(target);
					ask bustop_target{
						set waiting_passengers <- waiting_passengers + 1;
					}
					do write message: string(bustop_target) + ": " + bustop_target.waiting_passengers;
				}
				do die;
			}
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
}

experiment EastSideScenario3e type: gui {
	output{
		
		display city_display_with_opengl refresh_every: 1 type: opengl{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species highground aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.02;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			species busstop aspect: base transparency: 0.02;
			species building aspect: base;
		}
		/*display chart_display refresh_every: 10 {
			chart name: 'Survivors' type: series background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
				data name:'Survivors' value: number_of_survival style: line color: rgb('green') ;

			}
		}*/
	}
}
