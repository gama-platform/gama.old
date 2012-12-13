/**
 *  EastSideScenario2a
 *  Author: lvminh
 *  Description: 
 */

model EastSideScenario2a

global {
	file shape_file_roadlines  <- file('../includes/danang.eastside.roadlines.shp');
	file shape_file_places  <- file('../includes/danang.eastside.highgrounds.shp');
	file shape_file_busstations <- file('../includes/danang.eastside.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.eastside.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.eastside.busstops.shp');
	file shape_file_buildings <- file('../includes/danang.eastside.buildings.shp');
	string output_folder <- "../outputs/EastSideScenario1/";
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float pedestrian_max_speed <- 1.5 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_range <- 5.0 min: 2.0 max: 10.0 parameter: 'Density for considering range' category: 'Pedestrian';
	float max_density <- 5.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Pedestrian';
	float bus_length <- 12.0;
	float bus_width <- 2.5;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	int eastern_beach_population <- 300;
	int northern_beach_population <- 1;
	int number_of_bus_at_eastern_station <- 1;
	int number_of_bus_at_northern_station <- 1;
	int last_arrival_time <- 0;
	int number_of_survival <- 0;
	geometry creating_area_geometry;
	list obstacle_specy_list <- [species(wheelvehicle), species(pedestrian), species(sign_pole)];
	list background_sepcies_for_pedestrian <- [species(roadwidth), species(busstation), species(building)];
	list background_species_for_vehicle <- [species(roadwidth), species(busstation)];
	string log_to_save;
	list dead_agents <- [];
	replayer myreplayer;
	float sign_pole_height <- 3.0;
	float sign_panel_height <- 0.75;
	init{
		create replayer number:1;
		set myreplayer <- first(replayer);
		create beach from: shape_file_beaches;
		set creating_area_geometry <- (list(beach) at 0);
		
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		let creating_rows type: geometry <- nil;
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
				set width <- 8;
				create roadwidth {
					set shape <- myself.shape buffer 8;
					set creating_rows <- shape + creating_rows;
				}
			}
			if ((road_geom intersects creating_area_geometry) and (not (road_geom intersects (list(beach) at 1))) and (not (road_geom intersects (list(beach) at 2)))) {
				let one_end type: point <- first(road_geom.points);
				let another_end type: point <- last(road_geom.points);
				if (one_end covered_by creating_area_geometry){
					if (not (another_end covered_by creating_area_geometry)){
						create sign_pole number: 1{
							set location <- one_end;
						}
						create sign_panel number: 1{
							set location <- one_end;
						}
						create sign_agent number: 1{
							set the_pole <- last(sign_pole);
							set the_panel <- last(sign_panel);
							set next_point <- another_end;
						}
						ask last(list(sign_agent)){
							do set_location new_location: one_end;
							do set_heading_to target_point: another_end;
						}
					}
				}
				else{
					if (another_end covered_by creating_area_geometry){
						create sign_pole number: 1{
							set location <- another_end;
						}
						create sign_panel number: 1{
							set location <- another_end;
						}
						create sign_agent number: 1{
							set the_pole <- last(sign_pole);
							set the_panel <- last(sign_panel);
							set next_point <- one_end;
						}
						ask last(list(sign_agent)){
							do set_location new_location: another_end;
							do set_heading_to target_point: one_end;
						}
					}
				}
			}
		}
		write "here";
		set the_graph <- as_edge_graph (list(roadline));
		create highground from: shape_file_places;
		create busstation from: shape_file_busstations;
		create building from: shape_file_buildings{
			set capacity <- 100;
			set availability <- capacity;
			set shape <- shape buffer 3;
		}
		create busstop from: shape_file_busstops with: [zone_name:: read('zoneName'), priority:: read('priority')];
		set creating_area_geometry <- (list(beach) at 0);
		set creating_area_geometry <- creating_area_geometry inter creating_rows;
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
		let continue_creating type: bool <- true;
		loop while: (continue_creating and (agent_id < eastern_beach_population)){
			create pedestrian number: 1{
				set shape <- circle(0.6);
				set occupation_space <- 1;
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
		/* save static agents */
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
	reflex stop_simulation when: ((length(pedestrian) = 0) or (time = 7200)){
		do halt;
    }
    /* pause for changing point of view *
    reflex pause_simulation when: (time mod 600 = 0){
    	do pause;
    }
    /* */
    reflex save_dynamic_agents{
		let live_agents type: list <- [];
    	//time: agentid, position, heading
    	loop item over: list(pedestrian){
    		let current_pedestrian type: pedestrian <- pedestrian(item);
    		let current_agent_infor type: list <- [];
    		add current_pedestrian.name to: current_agent_infor;
    		add current_pedestrian.location to: current_agent_infor;
    		add current_pedestrian.heading to: current_agent_infor;
    		add current_agent_infor to: live_agents;
    	}
    	let infor_2_save type: list <- [];
    	let time_step type: int <- int(time);
    	add time_step to: infor_2_save;
    	add dead_agents to: infor_2_save;
    	add live_agents to: infor_2_save;
    	let filename type: string <- "pedestrian" + time_step + ".rep";
    	ask myreplayer{
    		do save_replay file_name: filename agent_information: infor_2_save;
    	}
    	set dead_agents <- [];
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
	species highground{
		string location_name;
		rgb color <- rgb('blue');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species busstation{
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species busstop{
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
	species building{
		float height <- 50.0 + rnd(50);
		int capacity <- 0;
		int availability <- 0;
		rgb color <- rgb('orange');
		aspect base{
			draw geometry: shape color: color z: height;
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
		point target <- nil ;
		/* for driving skill */
		reflex move_with_vehicleGoto when: (target != nil){
			do vehicle_goto target: target on: the_graph speed: speed;
		}
		/* */
		aspect base{
			draw shape: geometry color: color z: 3;
		}
	}
	species pedestrian parent: occupationagent skills: [driving2d]{
		building ultimate_target <- nil;
		point temp_target <- nil;
		float size <- 1.0;
		rgb color <- rgb('yellow');
		float local_density <- 0.0;
		list shelters of: building <- (building as list);
		reflex select_target when: ((ultimate_target = nil) or ((ultimate_target != nil) and (ultimate_target.availability = 0))){
			let available_shelters type: list of: building <- ((list(shelters) where (each.availability > 0)) sort_by (self distance_to each));
			if ((available_shelters != nil) and (length(available_shelters)>0)){
				set ultimate_target <- first(available_shelters);	
				//do write message: "shelter location" + ultimate_target;	
			}
			else{
				do write message: "can not find shelter!";
			}
		}
		/* for driving skill */
		reflex goto_shelter when: ((temp_target = nil) and (ultimate_target != nil)){
			let nearby_signs type: list of: sign_agent <- (self neighbours_at 30) of_species species((sign_agent));
			if ((nearby_signs != nil) and (length(nearby_signs) > 0)){
				let perceived_sign type: sign_agent <- nearby_signs with_min_of (self distance_to each); 
				set temp_target <- perceived_sign.next_point;
			}
			else{
				let moving_status type: int <- -1;
				do pedestrian_goto target: ultimate_target.location on: the_graph speed: speed target_type: false returns: moving_status;
				if (int(moving_status) > 0){
					do write message: "arrived ultimate_target";
					ask ultimate_target {
						set availability <- (availability - 1);
						//do write message: "new availability: " + availability;
					}
					set last_arrival_time <- time;
					set number_of_survival <- (number_of_survival + 1);
					do write message: "at: " + last_arrival_time + ", " + number_of_survival + " arrive shelters";
					add self.name to: dead_agents;
					do die;
				}	
			}
		}
		reflex goto_temp_target when: (temp_target != nil){
			let moving_status type: int <- -1;
				do pedestrian_goto target: temp_target on: the_graph speed: speed target_type: false returns: moving_status;
				if (int(moving_status) > 0){
					do write message: "arrived temp_target";
					set temp_target <- nil;
				}
		}
		/* */
		aspect base{
			draw shape: geometry color: color z: 1.5;
		}
	}
	
	species sign_pole{
		rgb color <- rgb('red');
		float height <- sign_pole_height;
		geometry shape <- square(0.2);
		aspect base{
			draw geometry: shape color: color z: height;
		}
	}
	species sign_panel{
		rgb color <- rgb('blue');
		float height <- sign_panel_height;
		//geometry shape <- polygon([{-0.5, -0.2}, {0.5, 0.2}, {1.5, 0}]);
		geometry shape <- polygon([{-0.5, -0.2}, {0.1, 0}, {-0.5, 0.2}, {1.5, 0}]);
		aspect base{
			draw geometry: shape color: color z: height;
		}
	}
	species sign_agent{
		sign_pole the_pole <- nil;
		sign_panel the_panel <- nil;
		point next_point <- nil;
		action set_location{
			arg new_location type: point;
			if ( (the_pole != nil) and (not dead(the_pole))){
				ask the_pole{
					set location <- new_location;
				}
				set location <- the_pole.location;
			}
			if ( (the_panel != nil) and (not dead(the_panel))){
				ask the_panel{
					set location <- new_location;
				}
			}
		}
		action set_heading{
			arg heading type: int;
			if ( (the_pole != nil) and (not dead(the_pole))){
				ask the_pole{
					set shape <- shape rotated_by heading;
				}
			}
			if ( (the_panel != nil) and (not dead(the_panel))){
				ask the_panel{
					set shape <- shape rotated_by heading;
				}
			}
		}
		action set_heading_to{
			arg target_point type: point;
			let d type: point <- {target_point.x - location.x, target_point.y - location.y};
			let angle type: int <- atan2(d.y, d.x);
			//do write message: "" + angle;
			if ( (the_pole != nil) and (not dead(the_pole))){
				ask the_pole{
					set shape <- shape rotated_by angle;
				}
			}
			if ( (the_panel != nil) and (not dead(the_panel))){
				ask the_panel{
					set shape <- shape rotated_by angle;
				}
			}
		}
	}
}

experiment EastSideScenario2a type: gui {
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
			species busstop aspect: base transparency: 0.02;
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
			species building aspect: base;
			species sign_pole aspect: base;
			species sign_panel aspect: base z: 0.00009;
		}
	}
}
