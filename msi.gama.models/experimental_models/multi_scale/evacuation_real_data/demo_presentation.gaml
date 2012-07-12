model demo_presentation

global {
	float simulated_population_rate <- 0.001 const: true;
	
	// GIS data
//	file shape_file_road <- shapefile('demo_gis/MainRoads.shp');
	file shape_file_road <- shapefile('demo_gis/roads.shp');
	file shape_file_beach <- shapefile('gis/beach.shp');
	file shape_file_roadwidth <- shapefile('gis/roads.shp');
	string shape_file_bounds <- 'demo_gis/bounds.shp';
	string shape_file_shelters <- 'demo_gis/edu_gov.shp';
	string shape_file_ward <- 'demo_gis/wards.shp';

	var people_number type: int init: 500 parameter: "Number of agents";

	float insideRoadCoeff <- 0.1 min: 0.01 max: 0.4 parameter: 'Size of the external parts of the roads:';
	
	rgb pedestrian_color <- rgb('green') const: true;
	
	float pedestrian_max_speed <- 5.4 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_window_length <- 2 min: 1 max: 5 parameter: 'Density window length' category: 'Pedestrian';
	float max_density <- 5 min: 1 max: 10 parameter: 'Max density (people per m2)' category: 'Pedestrian';

	int macro_patch_length_coeff <- 5 parameter: 'Macro-patch length coefficient';
	bool is_hybrid_simulation <- false parameter: 'Is hybrid simulation?';

	list ward_colors of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;

	topology road_graph_topology;
	density_window_builder window_builder;
	
	list nearest_roads_to_shelters <- [] type: list of: road;
	
	rgb shelter_color <- rgb('blue') const: true;
	rgb road_color <- rgb('black') const: true;
	
	float pedestrians_average_speed -> { self compute_average_speed [] };
	int pedestrians_reach_target -> { self compute_pedestrians_reach_target [] };
	string simulation_output -> { self build_simulation_output [] } ;
	int pedestrians_reach_shelter -> { length(list(pedestrian) where each.reach_shelter) };
	
	action build_simulation_output type: string {
		let retVal type: string value: 'At time: ' + (string(time)) + ' : ';
		set retVal value: retVal + '\n\t pedestrians_reach_shelter: ' + (string(pedestrians_reach_shelter)) + '; pedestrians_average_speed: ' + (string(pedestrians_average_speed)) + '; average_duration: ' + (string(average_duration));
		
		if (is_hybrid_simulation) {
			set retVal value: retVal + '; captured_pedestrians: ' + string(sum(list(road) collect length(each.members))) ;
		}
		
		return retVal;
	}
	
	action compute_average_speed type: float {
		let not_reach_target_pedestrians type: list of: pedestrian value: list(pedestrian) where !(each.reach_shelter);
		
		if (empty(not_reach_target_pedestrians)) { return 0; }
		
		return ( sum(not_reach_target_pedestrians collect each.speed) / length(not_reach_target_pedestrians) );
	}
	
	action compute_pedestrians_reach_target type: int {
		return length( list(pedestrian) where each.reach_shelter );
	}
	
	init {
//		create beach from: shape_file_beach;
		create roadwidth from: shape_file_roadwidth;
		
		loop road_geom over: (shape_file_road.contents) {
			create road with: [ shape :: geometry(road_geom), width :: (geometry(road_geom) get ('WIDTH')) ];
		}

		create shelter from: shape_file_shelters;
		
		loop s over: list(shelter) {
			add item: ( road closest_to (s) ) to: nearest_roads_to_shelters;  	
		}
		
		set nearest_roads_to_shelters value: (remove_duplicates(nearest_roads_to_shelters));
		
		create ward from: shape_file_ward with: [id :: read('ID'), wardname :: read('Name'), population :: read('Population')] {
			do init_overlapping_roads;
		}
		
		set road_graph_topology value: topology(as_edge_graph (list(road)));

		if (is_hybrid_simulation) {
			create road_initializer;
			let ri type: road_initializer value: first (road_initializer as list);
			loop rd over: (road as list) {
				ask target: (ri) {
					do initialize {
						arg the_road value: rd;
					}
				}
			}
		}
		
		create pedestrian number: people_number {
			set location value: any_location_in (one_of (road_graph_topology.places));
		}
		
		/*
		loop w over: list(ward) {
			if condition: !(empty(w.roads)) {
				create pedestrian number: int ( (w.population * simulated_population_rate) ) {
					set location value: any_location_in (one_of (w.roads));
				}
			}
		}
		 */
		 
		create density_window_builder returns: builders;
		set window_builder value: builders at 0;
	}	 
}

environment bounds: shape_file_bounds;

entities {
	species ward {
	  	int id;
	  	int population min: 0;
	  	string wardname;
	  	rgb color init: one_of(ward_colors);
	  	list roads of: road;
	  	
	  	action init_overlapping_roads {
	  		set roads value: road overlapping shape;
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

	species roadwidth {
		aspect default {
			draw shape: geometry color: rgb('yellow');
		}
	}

	species road {
		int width;
		
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
			
			let to_be_captured_pedestrian type: list of: pedestrian value: (pedestrian overlapping (macro_patch_buffer)) where !(each.reach_shelter);
			if condition: ! (empty(to_be_captured_pedestrian)) {
				set to_be_captured_pedestrian value: to_be_captured_pedestrian where (
					(each.last_road != self)
					and (each.previous_location != nil) 
					and (each.location != ((each.goal).location)));
			}
			
			if !(empty (to_be_captured_pedestrian)) {
				capture target: to_be_captured_pedestrian as: captured_pedestrian returns: c_pedestrian;
				
				loop cp over: c_pedestrian {
					let road_source_to_previous_location type: geometry value: ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry value: ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float value: 0;
					
					if (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance value: geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: last (macro_patch.points);
					} else { // agent moves towards extremity1
						set skip_distance  value: geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: first (macro_patch.points);
					}

					// TODO recalculate the released_time
					set cp.released_time value: time + (skip_distance / cp.speed);
				}
			}
		}
		
		reflex release_captured_pedestrian when: (macro_patch != nil) {
			let to_be_released_pedestrian type: list of: captured_pedestrian value: (members) where ( (captured_pedestrian(each).released_time) <= time );
			
			if condition: !(empty (to_be_released_pedestrian)) {
				loop rp over: to_be_released_pedestrian {
					let r_position type: point value: rp.released_location;
					release target: rp returns: r_pedestrian;
					set pedestrian(first (list (r_pedestrian))).last_road value: self;
					set pedestrian(first (list (r_pedestrian))).location value: r_position;
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
	
	species pedestrian skills: moving {
		point previous_location;
		road last_road;

		bool reach_shelter <- false;
   		float local_density <- 0;
   		float speed;

		geometry window_viewer;
		float local_density_window_area;
		shelter goal <- (list(shelter) closest_to self) depends_on: [ shape, location ];
		
		init {
			set speed value: self compute_speed [];
		}
	
		action compute_speed type: float {
			ask window_builder {
				do build with: [ current_people :: myself ];
			}
			 
			if (window_viewer = nil) {
				do write {
					arg message value: name + ' with window_viewer = NULL!';
				}
				
				return pedestrian_max_speed;
			}
			
//			let obstacle_pedestrians type: list of: people value: (people overlapping window_viewer.shape);
			let obstacle_pedestrians type: list of: pedestrian value: ( (list(pedestrian) where !((pedestrian(each)).reach_shelter) ) overlapping (window_viewer + 0.1));

			if (length(obstacle_pedestrians) = 0) { 
				set local_density value: 0;
				return pedestrian_max_speed;
			}
			
			set local_density value: ( float( length (obstacle_pedestrians) ) ) / local_density_window_area;
			
			if (local_density >= max_density) {  return value: 0; }
			
			return pedestrian_max_speed * ( 1 - (local_density / max_density) );
		}

		reflex move when: !(reach_shelter) {
			set previous_location value: location;
			
			set speed value: self compute_speed [];
			do action: goto {
				arg target value: goal;
				arg on value: road_graph_topology;
				arg speed value: speed;
			}
			
			if (location = goal.location) {
				set reach_shelter value: true;
				set speed value: 0;
			}
		}
		
 		aspect base {
// 			draw shape: geometry color: pedestrian_color;
 			draw shape: circle size: 30 color: pedestrian_color;
 		}
	}
	
	species road_initializer skills: [moving] {
		action initialize {
			arg the_road type: road;
			
			let should_build_macro_patch type: bool value: empty( list(shelter) overlapping (the_road) );
			set should_build_macro_patch value: should_build_macro_patch and !(nearest_roads_to_shelters contains the_road);
			
			if (should_build_macro_patch) {
				
				let inside_road_geom type: geometry value: the_road.shape;
				set speed value: (the_road.shape).perimeter * insideRoadCoeff;
				let point1 type: point value: first(inside_road_geom.points);
				let point2 type: point value: last(inside_road_geom.points);
				set location value: point1;
				
				do action: goto {
					arg target value: point2;
					arg on value: road_graph_topology; 
				}
	
				let lines1 type: list of: geometry value: (inside_road_geom split_at location);
				set the_road.extremity1 value: lines1  first_with (geometry(each).points contains point1);
				set inside_road_geom value: lines1 first_with (!(geometry(each).points contains point1));
				set location value: point2;
				do action: goto {
					arg target value: point1;
					arg on value: road_graph_topology; 
				}
				let lines2 type: list of: geometry value: (inside_road_geom split_at location);
				
				set the_road.extremity2 value:  lines2 first_with (geometry(each).points contains point2);
				set inside_road_geom value: lines2 first_with (!(geometry(each).points contains point2));
				
				if (inside_road_geom.perimeter > (macro_patch_length_coeff * pedestrian_max_speed) ) {
					set the_road.macro_patch value: inside_road_geom;
					set the_road.macro_patch_buffer value: inside_road_geom + 0.01;
				}
			}
		}
	}	
	
	species density_window_builder skills: [moving] {
		action build {
			arg current_people type: pedestrian;
			
			set location value: current_people.location;
			let previous_road type: road value: list(road) closest_to location;
			let previous_location type: point value: location;
			
			do goto with: [ on :: road_graph_topology, speed :: local_density_window_length, target :: (current_people.goal).location ];
			let current_location type: point value: location;
			let current_road type: road value: list(road) closest_to location;
			
			if (previous_road = current_road) {
				let current_road_first_point type: point value: first ((current_road.shape).points);
				let last_location_2_first_point type: float value: road_graph_topology distance_between [previous_location, current_road_first_point];
				let current_location_2_first_point type: float value: road_graph_topology distance_between [current_location, current_road_first_point];
				
				if (last_location_2_first_point > current_location_2_first_point) { // agent moves towards first point
				 
					let part2 type: geometry value: first (previous_road.shape split_at previous_location);
					set current_people.window_viewer value: last ( (geometry(part2)) split_at current_location); 
					
				} else {
					// agent moves towards last point
					let part2 type: geometry value: last (previous_road.shape split_at previous_location);
					set current_people.window_viewer value: first ( (geometry(part2)) split_at current_location);
				}
				
				set current_people.local_density_window_area value: local_density_window_length * (current_road.width);
			} else {
				// test if the agent moves toward first point or last point of the PREVIOUS road
				
				let previous_location_2_goal type: float value: road_graph_topology distance_between [previous_location, (current_people.goal).location];
				let current_location_2_goal type: float value: road_graph_topology distance_between [current_location, (current_people.goal).location];

				let previous_road_first_point type: point value: first (((previous_road).shape).points);
				let previous_road_first_location_2_goal type: float value: road_graph_topology distance_between [previous_road_first_point, (current_people.goal).location];
				
				let previous_road_last_point type: point value: last ((previous_road.shape).points);
				let previous_road_last_location_2_goal type: float value: road_graph_topology distance_between [previous_road_last_point, (current_people.goal).location];
				
				let current_road_first_point type: point value: first (((current_road).shape).points);

				switch (previous_road_first_location_2_goal) {
					
					// agent has passed first point of PREVIOUS road
					match_between [current_location_2_goal, previous_location_2_goal] {
						let part_on_previous_road type: geometry value: first ((previous_road.shape) split_at previous_location );
						
						if (previous_road_first_point = current_road_first_point) { // agent begins current road with its first point
							let part_on_current_road type: geometry value: first (current_road.shape split_at current_location);
							
							set current_people.window_viewer value: geometry(part_on_previous_road) + geometry(part_on_current_road);
						} else { // agent begins current road with its last point
						
							let part_on_current_road type: geometry value: last (current_road.shape split_at current_location);
							
							set current_people.window_viewer value: geometry(part_on_previous_road) + geometry(part_on_current_road);
						}
					}
				}
				
				switch (previous_road_last_location_2_goal) {

					// agent has passed last point of PREVIOUS road
					match_between [current_location_2_goal, previous_location_2_goal] {
						let part_on_previous_road type: geometry value: last ((previous_road.shape) split_at previous_location );
						
						if (previous_road_last_point = current_road_first_point) { // agent begins current road with its first point
							let part_on_current_road type: geometry value: first (current_road.shape split_at location);
							
							set current_people.window_viewer value: geometry(part_on_previous_road) + geometry(part_on_current_road);
//							set (current_people.window_viewer).shape value: polyline (list ((part_on_last_road).points + (part_on_current_road).points) );

						} else { // agent begins current road with its PREVIOUS point
							let part_on_current_road type: geometry value: last (current_road.shape split_at location);
							
							set current_people.window_viewer value: geometry(part_on_previous_road) + geometry(part_on_current_road);
						}
					}
				}

				// approximation
				set current_people.local_density_window_area value: local_density_window_length * (  ( ( (current_road.width) + (previous_road.width)) ) / 2 );
			}
		}
	}

	species beach  {
		aspect default {
			draw shape: geometry color: rgb('green');
		}
	}
}

experiment pure_ABM_experiment type: gui {
	parameter 'Hybrid simulation' var: is_hybrid_simulation init: false;
	  
	output {
		display ward_road_network {
//			species ward aspect: base transparency: 0.5;
			species beach transparency: 0.5;
			species roadwidth aspect: default transparency: 0.5;
			species road aspect: base transparency: 0.5;
 			species pedestrian aspect: base transparency: 0.1;
 			species shelter aspect: base transparency: 0.1;
		}
		
		monitor pedestrians_reach_shelter value: length(list(pedestrian) where each.reach_shelter);
		monitor total_pedestrian value: length(list(pedestrian)); 
		monitor average_speed value: pedestrians_average_speed;
		monitor pedestrians_reach_target value: pedestrians_reach_target;
		monitor stuck_people value: length( list(pedestrian) where ( (each.speed = 0)  and !(each.reach_shelter) ) );
		monitor average_local_density value: (sum (list(pedestrian) collect (each.local_density))) / (length(pedestrian));
		monitor average_step_duration value: average_duration;

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

		file ABM_experiment_output data: simulation_output refresh_every: 2;
	}
}

experiment Hybrid_experiment type: gui {
	parameter 'Hybrid simulation' var: is_hybrid_simulation init: true;  

	output {
		display ward_road_network {
//			species ward aspect: base transparency: 0.5;
			species beach transparency: 0.5;
			species road aspect: base transparency: 0.5;
 			species pedestrian aspect: base transparency: 0.1;
 			species shelter aspect: base transparency: 0.1;
		}
		
		monitor captured_pedestrians value: sum(list(road) collect ( length((each.members))) ); // WHY always empty???
		monitor not_captured_pedestrians value: length(list(pedestrian));
		
		monitor average_speed value: pedestrians_average_speed;
		monitor pedestrians_reach_target value: pedestrians_reach_target;
		monitor stuck_people value: length( list(pedestrian) where ( (each.speed = 0)  and !(each.reach_shelter) ) );
		
		monitor average_local_density value: (sum (list(pedestrian) collect (each.local_density))) / (length(pedestrian));
		monitor average_step_duration value: average_duration;
		
		monitor roads_WITH_macro_patch value: length( (list(road) where (each.macro_patch != nil)) );
		monitor roads_WITHOUT_macro_patch value: length( (list(road) where (each.macro_patch = nil)) );
		monitor nearest_roads value: length(nearest_roads_to_shelters);
		
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

		
		file HYBRID_experiment_output data: simulation_output refresh_every: 2;
	}
}