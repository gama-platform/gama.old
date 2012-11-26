/**
 *  EastSideScenario3a
 *  Author: lvminh
 *  Description: 
 */

model EastSideScenario3a

global {
	file shape_file_roadlines  <- file('../includes/danang.eastside.roadlines.shp');
	file shape_file_places  <- file('../includes/danang.eastside.highgrounds.shp');
	file shape_file_busstations <- file('../includes/danang.eastside.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.eastside.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.eastside.busstops.shp');
	file shape_file_buildings <- file('../includes/danang.eastside.buildings.shp');
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float pedestrian_max_speed <- 1.5 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float max_local_density_range <- 8.0 min: 2.0 max: 10.0 parameter: 'Density for considering range' category: 'Pedestrian';
	float pedestrian_max_density <- 6.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Pedestrian';
	float bus_max_speed <- 11.1 min: 0.1 parameter: 'Max speed (m per s)' category: 'Bus';
	int bus_max_capacity <- 50 min: 20 parameter: 'Max capacity of bus (number of people)' category: 'Bus';
	float bus_occupation_space <- 20.0 min: 20 parameter: 'Occupation space of bus (number of people)' category: 'Bus';
	float bus_max_density <- 1.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Bus';
	int building_max_capacity <- 100 min: 20 parameter: 'Max capacity of buildind (number of people)' category: 'Building';
	float bus_length <- 12.0;
	float bus_width <- 2.5;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	int eastern_beach_population <- 1000;
	int northern_beach_population <- 1;
	int number_of_bus_at_eastern_station <- 0;
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
					set shape <- myself.shape buffer 10;
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
		create pedestrian number: eastern_beach_population{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 270;
			set shape <- circle(size);
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
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
		/* *
		set creating_area_geometry <- first(list(busstation));
		create wheelvehicle number: number_of_bus_at_eastern_station{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set occupation_space <- 20;
			set target <- last(list(busstop));
			set speed <- 7;
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
				return pedestrian_max_speed;
			}		
			let local_density <- obstacle_space / local_density_window_area;
			if (local_density >= max_density) {  return 0; }	
			else {return max_speed * ( 1 - (local_density / max_density) );}
		}
	}
	species wheelvehicle parent: occupationagent skills: [driving]{
		float length <- bus_length;
		float width <- bus_width;
		rgb color <- rgb('yellow');
		mylocation target <- nil;
		bool is_goto_busstop <- true;
		int candidate_busstop_index <- 0;
		int behavior_id <- 1;
		int availability <- bus_max_capacity;
		reflex bus_behavior{
			switch behavior_id{
				match 1{ // initially, select the busstop
					let busstops_with_passengers type: list <- (list(busstop) where (each.waiting_passengers > 0)); 
					if ((busstops_with_passengers != nil) and (length(busstops_with_passengers) > 0)){
						set target <- (busstops_with_passengers with_min_of (self distance_to each));
						set behavior_id <- 1;	
					}
				}
				match 2{ //goto the busstop
					if (target != nil){
						let moving_status type: int <- -1;
						do vehicleGoto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
						if (int(moving_status) > 0){
							set behavior_id <- 3;
						}
						let busstop_target type: busstop <- busstop(target);
						if (busstop_target.waiting_passengers = 0){
							set behavior_id <- 1;
						}
					}
				}
				match 3{ // waiting for picking up people
					
				}
			}
		}
		/* for driving skill *
		reflex select_busstop when: ((is_goto_busstop) and ((target = nil) or ((target != nil) and (target.location_type = 2) and (busstop(target).waiting_passengers  = 0)))){
			
			set target <- (list(busstop) at candidate_busstop_index);
			set candidate_busstop_index <- (candidate_busstop_index + 1) mod length(busstop);
		}
		reflex goto_busstop when: ((is_goto_busstop) and (target != nil) ){
			
		}
		reflex wait_for_capturing{
			
		}
		reflex move_with_vehicleGoto when: (target != nil){
			let moving_status type: int <- -1;
			do vehicleGoto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
			if (int(moving_status) > 0){
				
			}
		}
		/* */
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
		reflex select_target when: (target = nil){
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
		reflex move_with_vehicleGoto when: (target != nil){			
			set speed <- self compute_speed [max_density::pedestrian_max_density, max_speed::pedestrian_max_speed, local_density_range::max_local_density_range];
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
					do write message: "at: " + last_arrival_time + ", " + number_of_survival + " arrive shelters";
					do die;	
				}
				else if ((target != nil) and (target.location_type = 2)){
					let bustop_target type: busstop <- busstop(target);
					ask bustop_target{
						set waiting_passengers <- waiting_passengers + 1;
					}
					do write message: string(bustop_target) + ": " + bustop_target.waiting_passengers;
				}
			}
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
}

experiment EastSideScenario3a type: gui {
	output{
		display city_display refresh_every: 1{
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
	}
}
