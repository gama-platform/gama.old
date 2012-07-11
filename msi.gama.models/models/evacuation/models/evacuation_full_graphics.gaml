model evacuation_full_graphics

global {
	float simulated_population_rate <- 0.1 const: true;
	
	// GIS data
	file shape_file_road <- shapefile('../gis/roads.shp');
	string shape_file_roadlines <- '../gis/roadlines.shp';
	string shape_file_rivers <- '../gis/rivers.shp';
	string shape_file_beach <- '../gis/beach.shp';
	string shape_file_bounds <- '../gis/bounds.shp';
	string shape_file_shelters <- '../gis/edu_gov.shp';
	string shape_file_ward <- '../gis/wards.shp';

	float insideRoadCoeff <- 0.1 min: 0.01 max: 0.4 parameter: 'Size of the external parts of the roads:';
	
	rgb pedestrian_color <- rgb('green') const: true;
	
	float pedestrian_max_speed <- 5.4 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_window_length <- 2.0 min: 1.0 max: 5.0 parameter: 'Density window length' category: 'Pedestrian';
	float max_density <- 5.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Pedestrian';

	int macro_patch_length_coeff <- 5 parameter: 'Macro-patch length coefficient';
	bool is_hybrid_simulation <- false parameter: 'Is hybrid simulation?';

	list ward_colors of: rgb <- [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;

	topology road_graph_topology;
	
	list nearest_roads_to_shelters <- [] of: road;
	
	rgb shelter_color <- rgb('blue') const: true;
	rgb road_color <- rgb('black') const: true;
	rgb road_width_color <- rgb('yellow') const: true;
	rgb beach_color <- rgb('blue') const: true;
	rgb river_color <- rgb('gray') const: true; 
	
	float pedestrians_average_speed -> { self compute_average_speed [] };
	int pedestrians_reach_target -> { self compute_pedestrians_reach_target [] };
	int pedestrians_reach_shelter -> { length(list(pedestrian) where each.reach_shelter) };
	
	list not_reach_schelters of: pedestrian <- list(pedestrian) update: list(pedestrian) where !((pedestrian(each)).reach_shelter);
	
	int captured_pedestrians <- sum(list(road) collect ( length((each.members))) );
	int not_captured_pedestrians <- length(list(pedestrian));
	
	action build_simulation_output type: string {
		let retVal type: string <- 'At time: ' + (string(time)) + ' : ';
		set retVal <- retVal + '\n\t pedestrians_reach_shelter: ' + (string(pedestrians_reach_shelter)) + '; pedestrians_average_speed: ' + (string(pedestrians_average_speed)) + '; average_duration: ' + (string(average_duration));
		
		if (is_hybrid_simulation) {
			set retVal <- retVal + '; captured_pedestrians: ' + string(sum(list(road) collect length(each.members))) ;
		}
		
		return retVal;
	}
	
	action compute_average_speed type: float {
		let not_reach_target_pedestrians type: list of: pedestrian <- list(pedestrian) where !(each.reach_shelter);
		
		if (empty(not_reach_target_pedestrians)) { return 0; }
		
		return ( sum(not_reach_target_pedestrians collect each.speed) / length(not_reach_target_pedestrians) );
	}
	
	action compute_pedestrians_reach_target type: int {
		return length( list(pedestrian) where each.reach_shelter );
	}
	
	init {
		loop road_geom over: (shape_file_road.contents) {
			create road with: [ shape :: geometry(road_geom), width :: (geometry(road_geom) get ('WIDTH')) ];
		}
		
		create road_width from: shape_file_roadlines; 
		create shelter from: shape_file_shelters;
		create beach from: shape_file_beach;
		create river from: shape_file_rivers;
		
		loop s over: list(shelter) {
			add item: ( road closest_to (s) ) to: nearest_roads_to_shelters;  	
		}
		
		set nearest_roads_to_shelters <- (remove_duplicates(nearest_roads_to_shelters));
		
		create ward from: shape_file_ward with: [id :: read('ID'), wardname :: read('Name'), population :: read('Population')] {
			do init_overlapping_roads;
		}
		
		set road_graph_topology <- topology(as_edge_graph (list(road)));

		if (is_hybrid_simulation) {
			create road_initializer;
			let ri type: road_initializer <- first (road_initializer as list);
			loop rd over: (road as list) {
				ask target: (ri) {
					do initialize the_road:rd;
				}
			}
		}
		
		loop w over: list(ward) {
			if condition: !(empty(w.roads)) {
				create pedestrian number: int ( (w.population * simulated_population_rate) ) {
					set location <- any_location_in (one_of (w.roads));
				}
			}
		}
	}	 
}

environment bounds: shape_file_bounds;

entities {
	species ward {
	  	int id;
	  	int population min: 0;
	  	string wardname;
	  	rgb color <- one_of(ward_colors);
	  	list roads of: road;
	  	
	  	action init_overlapping_roads {
	  		set roads <- road overlapping shape;
	  	}
	  	
	  	aspect base {
	  		draw shape: geometry color: color;
	  	}
	}
	
	species shelter {
		aspect base {
			draw shape: geometry color: shelter_color;
		}
	}
	
	species road_width {
		aspect base {
			draw shape: geometry color: road_width_color;
		}
	}
	
	species beach {
		aspect base {
			draw shape: geometry color: beach_color;
		}
	}
	
	species river {
		aspect base {
			draw shape: geometry color: river_color;
		}
	}

	species road {
		float width;
		
		geometry extremity1;
		geometry extremity2; 
		
		geometry macro_patch;
		geometry macro_patch_buffer;
		
		
		species captured_pedestrian parent: pedestrian schedules: [] {
			int released_time;
			point released_location;
			
			aspect default {
				
			}
		}

		reflex capture_pedestrian when: ( (is_hybrid_simulation) and (macro_patch != nil) ) {
			
			let to_be_captured_pedestrian type: list of: pedestrian <- (pedestrian overlapping (macro_patch_buffer)) where !(each.reach_shelter);
			if condition: ! (empty(to_be_captured_pedestrian)) {
				set to_be_captured_pedestrian <- to_be_captured_pedestrian where (
					(each.current_road = self)
					and (each.last_macro_patch != self)
					and (each.previous_location != nil)
					and (each.speed > 0));
			}
			
			if !(empty (to_be_captured_pedestrian)) {
				capture to_be_captured_pedestrian as: captured_pedestrian returns: c_pedestrian;
				
				loop cp over: c_pedestrian {
					let road_source_to_previous_location type: geometry <- ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry <- ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float <- 0;
					
					if (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance <- geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location <- last (macro_patch.points);
					} else { // agent moves towards extremity1
						set skip_distance  <- geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location <- first (macro_patch.points);
					}

					set cp.released_time <- time + (skip_distance / cp.speed);
				}
			}
		}
		
		reflex release_captured_pedestrian when: (macro_patch != nil) {
			
			let to_be_released_pedestrian type: list of: captured_pedestrian <- (members) where ( (captured_pedestrian(each).released_time) <= time );
			
			if !(empty (to_be_released_pedestrian)) {
				loop rp over: to_be_released_pedestrian {
					let r_position type: point <- rp.released_location;
					release rp in: world as: pedestrian returns: r_pedestrian;
					set pedestrian(first (list (r_pedestrian))).last_macro_patch <- self;
					set pedestrian(first (list (r_pedestrian))).location <- r_position;
				}
			}
		}
		
	 	aspect base {
	 		draw shape: geometry color: road_color;
	 	}
	}
	
	species macro_patch_viewer {
		aspect base {
			draw shape: geometry color: rgb('red');
		}
	}	
	
	species pedestrian skills: [moving] {

		bool reach_shelter <- false;
   		float local_density <- 0.0;
   		float speed <- pedestrian_max_speed;
   	
   		road last_macro_patch;
   		road current_road <- ( (list(road)) closest_to self ) update: ( (list(road)) closest_to self ) depends_on: [location];
   		point previous_location;

		geometry window_viewer;
		float local_density_window_area;
		shelter goal update: (list(shelter) closest_to self) depends_on: [ shape, location ];
		
		action compute_speed type: float {
			
			set local_density_window_area <- local_density_window_length * (current_road.width);

			/*
			 * 30%: 10961ms 4.12m/s
			 * 
			 * 100%: 60415ms 2.44m/s 192pedestrians 
			 */
			let obstacle_pedestrians type: int <- 1 + length ( ( pedestrian at_distance local_density_window_length ) where ( ( pedestrian(each).current_road = current_road) and !( pedestrian(each).reach_shelter ) ) ) ;


			/*
			 * 100% 67309ms 2.3m/s 191pedestrians
			 */

			if (obstacle_pedestrians = 0) { 
				set local_density <- 0;
				return pedestrian_max_speed;
			}
			
			set local_density <- ( float (obstacle_pedestrians) ) / local_density_window_area;
			
			if (local_density >= max_density) {  return 0; }
			
			else {return pedestrian_max_speed * ( 1 - (local_density / max_density) );}
		}
		
		reflex move when: !(reach_shelter) {
			set previous_location <- location;
			
			set speed <- self compute_speed [];
			
			do action: goto target: goal on: road_graph_topology speed: speed; 
			
			if (location = goal.location) {
				set reach_shelter <- true;
				set speed <- 0;
			}
		}
		
 		aspect base {
 			draw shape: circle size: 20 color: pedestrian_color;
 		}
	}
	
	species road_initializer skills: [moving] {
		action initialize {
			arg the_road type: road;
			
			let should_build_macro_patch type: bool <- empty( list(shelter) overlapping (the_road) );
			set should_build_macro_patch <- should_build_macro_patch and !(nearest_roads_to_shelters contains the_road);
			
			if (should_build_macro_patch) {
				
				let inside_road_geom type: geometry <- the_road.shape;
				set speed <- (the_road.shape).perimeter * insideRoadCoeff;
				let point1 type: point <- first(inside_road_geom.points);
				let point2 type: point <- last(inside_road_geom.points);
				set location <- point1;
				
				do action: goto target: point2 on: road_graph_topology; 
	
				let lines1 type: list of: geometry <- (inside_road_geom split_at location);
				set the_road.extremity1 <- lines1  first_with (geometry(each).points contains point1);
				set inside_road_geom <- lines1 first_with (!(geometry(each).points contains point1));
				set location <- point2;
				do action: goto target: point1 on: road_graph_topology; 
				let lines2 type: list of: geometry <- (inside_road_geom split_at location);
				
				set the_road.extremity2 <-  lines2 first_with (geometry(each).points contains point2);
				set inside_road_geom <- lines2 first_with (!(geometry(each).points contains point2));
				
				if (inside_road_geom.perimeter > (macro_patch_length_coeff * pedestrian_max_speed) ) {
					set the_road.macro_patch <- inside_road_geom;
					set the_road.macro_patch_buffer <- inside_road_geom + 0.01;
				}
			}
		}
	}	
}

experiment 'ABM (10% population)' type: gui {
	parameter 'Hybrid simulation' var: is_hybrid_simulation <- false;
	  
	output {
		display ward_road_network refresh_every: 3 {
			species ward aspect: base transparency: 0.5;
			species road aspect: base transparency: 0.5;
			species road_width aspect: base transparency: 0.5;
			species beach aspect: base transparency: 0.5;
			species river aspect: base transparency: 0.5;
 			species pedestrian aspect: base transparency: 0.1;
 			species shelter aspect: base transparency: 0.1;
		}
		
		monitor total_pedestrian value: length(list(pedestrian)); 
		monitor average_speed value: pedestrians_average_speed;
		monitor pedestrians_reach_target value: pedestrians_reach_target;
		monitor stuck_people value: length( list(pedestrian) where ( (each.speed = 0)  and !(each.reach_shelter) ) );
		monitor average_local_density value: (sum (list(pedestrian) collect (each.local_density))) / (length(pedestrian));
		monitor average_step_duration value: average_duration;

		monitor all_pedestrians value: length(world.members of_generic_species pedestrian);

		display pedestrians_average_speed {
			chart pa_speed_diagram type: series {
				data pedestrians_average_speed value: pedestrians_average_speed color: rgb('green');
			}
		}
		
		display pedestrians_reach_target {
			chart pr_target_diagram type: series {
				data pedestrians_reach_target value: pedestrians_reach_target color: rgb('blue');
			}
		}

	}
}

experiment 'Hybrid (10% population)' type: gui {
	parameter 'Hybrid simulation' var: is_hybrid_simulation <- true;  

	output {
		display ward_road_network refresh_every: 6 {
			species ward aspect: base transparency: 0.5;
			species road aspect: base transparency: 0.5;
			species road_width aspect: base transparency: 0.5;
			species beach aspect: base transparency: 0.5;
			species river aspect: base transparency: 0.5;
 			species pedestrian aspect: base transparency: 0.1;
 			species shelter aspect: base transparency: 0.1;
		}
		
		monitor captured_pedestrians value: captured_pedestrians;
		monitor not_captured_pedestrians value: not_captured_pedestrians;
		
		monitor average_speed value: pedestrians_average_speed;
		monitor pedestrians_reach_target value: pedestrians_reach_target;
		monitor stuck_people value: length( list(pedestrian) where ( (each.speed = 0)  and !(each.reach_shelter) ) );
		
		monitor average_local_density value: !(empty(list(pedestrian))) ? (sum (list(pedestrian) collect (each.local_density))) / (length(pedestrian)) : 0;
		monitor average_step_duration value: average_duration;
		
		monitor roads_WITH_macro_patch value: length( (list(road) where (each.macro_patch != nil)) );
		monitor roads_WITHOUT_macro_patch value: length( (list(road) where (each.macro_patch = nil)) );
		monitor nearest_roads value: length(nearest_roads_to_shelters);
		
		monitor all_pedestrians value: length(world.agents of_generic_species pedestrian);
		
		display pedestrians_average_speed refresh_every: 3 {
			chart pa_speed_diagram type: series {
				data pedestrians_average_speed value: pedestrians_average_speed color: rgb('green');
			}
		}
		
		display pedestrians_reach_target {
			chart pr_target_diagram type: series {
				data pedestrians_reach_target value: pedestrians_reach_target color: rgb('blue');
			}
		}

		display captured_vs_not_captured_pedestrians {
			chart pr_target_diagram type: series {
				data captured value: captured_pedestrians color: rgb('blue');
				data not_captured value: not_captured_pedestrians color: rgb('green');
			}
		}
	}
}