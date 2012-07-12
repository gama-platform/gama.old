model Scenario4

global {
	int guiders_per_road <- 1 parameter: 'Number of guider per road' min: 0 max: 10;
	float simulated_population_rate <- 0.01 const: true;
	
	// GIS data
	string shape_file_road <- '/gis/roadlines.shp';
	string shape_file_bounds <- '/gis/bounds.shp';
	string shape_file_panel <- '/gis/panel.shp';
	string shape_file_ward <- '/gis/wards.shp';

	float insideRoadCoeff <- 0.1 min: 0.01 max: 0.4 parameter: 'Size of the external parts of the roads:';

	float pedestrian_speed <- 1; // TODO remove this???
	rgb pedestrian_color <- rgb('green') const: true;
	float pedestrian_perception_range <- 50; // 50 meters
	 
	float pedestrian_max_speed <- 20 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_window_length <- 2 min: 1 max: 5 parameter: 'Density window length' category: 'Pedestrian';
	float max_density <- 5 min: 1 max: 10 parameter: 'Max density (people per m2)' category: 'Pedestrian';

	int road_width <- 15 min: 4 max: 40 parameter: 'Width (m)' category: 'Road';
	float local_density_window_area <- road_width * local_density_window_length depends_on: [ road_width, local_density_window_length ];

	float guider_speed <- 1;
	rgb guider_color <- rgb('blue');   
	
	int macro_patch_length_coeff <- 25 parameter: 'Macro-patch length coefficient'; 
	bool capture_pedestrian <- false parameter: 'Capture pedestrian?';

	list ward_colors of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;
	list zone_colors of: rgb init: [rgb('magenta'), rgb('blue'), rgb('yellow')] const: true;

	string shapeSign <- '/icons/CaliforniaEvacuationRoute.jpg' const: true;  

	list terminal_panel_ids of: int init: [8];

	int toto;
	int titit;
	int tata;

	topology road_graph_topology;
	density_window_builder window_builder;

	init {
		create species: road from: shape_file_road;
		create species: ward from: shape_file_ward with: [id :: read('ID'), wardname :: read('Name'), population :: read('Population')] {
			do action: init_overlapping_roads;
		}
		create species: panel from: shape_file_panel with: [next_panel_id :: read('TARGET'), id :: read('ID')];
		set road_graph_topology value: topology(as_edge_graph (list(road)));

		create species: road_initializer; 
		let ri type: road_initializer value: first (road_initializer as list);
		loop rd over: (road as list) {
			ask target: (ri) {
				do action: initialize {
					arg the_road value: rd;
				}     
			}
		}
		
//		create pedestrian with: [ location :: any_location_in( (one_of(list(road))).shape )];

		loop w over: list(ward) {
			if condition: !(empty(w.roads)) {
				create species: pedestrian number: int ( (w.population * simulated_population_rate) ) {
					set location value: any_location_in (one_of (w.roads));
				}
			}
		}
		
		loop r over: list(road) {
			create species: guider number: guiders_per_road with: [ managed_road :: r ];
		}
 
		create density_window_builder returns: builders;
		set window_builder value: builders at 0;
	}	 

	reflex stop_simulation when: ( (time = 5400) or (  ( (length(list(pedestrian))) = (length(list(pedestrian) where each.reach_shelter)) ) and ( ( sum (list(road) collect (length (each.members))) ) = 0 ) ) ) {
		do action: write {
			arg message value: 'Simulation stops at time: ' + (string(time)) + ' with total duration: ' + total_duration + '\\n ;average duration: ' + average_duration
				+ '\\n ; pedestrians reach shelter: ' + (string(length( (list(pedestrian)) where (each.reach_shelter) )))
				+ '\\n ; pedestrians NOT reach shelter: ' + (string ( (length( (list(pedestrian)) where !(each.reach_shelter) )) + ( sum (list(road) collect (length (each.members))) ) ) );
		}
		
		do action: halt;
	}
}

entities {
	species road {
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

		reflex capture_pedestrian when: ( (capture_pedestrian) and (macro_patch != nil) ) {
			
			let to_be_captured_pedestrian type: list of: pedestrian value: (pedestrian overlapping (macro_patch_buffer)) where !(each.reach_shelter);
			if condition: ! (empty(to_be_captured_pedestrian)) {
				set to_be_captured_pedestrian value: to_be_captured_pedestrian where (
					(each.last_road != self)
					and (each.previous_location != nil) 
					and (each.location != ((each.current_panel).location)));
			}
			
			if condition: !(empty (to_be_captured_pedestrian)) {
				capture target: to_be_captured_pedestrian as: captured_pedestrian returns: c_pedestrian;
				
				loop cp over: c_pedestrian {
					let road_source_to_previous_location type: geometry value: ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry value: ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float value: 0;
					
					if condition: (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance value: geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: last (macro_patch.points);
						

					} else { // agent moves towards extremity1
							set skip_distance  value: geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
							set cp.released_location value: first (macro_patch.points);
						}

					// TODO recalculate the released_time
					set cp.released_time value: time + (skip_distance / pedestrian_speed);
				}
			}
		}
		
		reflex release_captured_pedestrian when: (macro_patch != nil) {
			let to_be_released_pedestrian type: list of: captured_pedestrian value: (members) where ( (captured_pedestrian(each).released_time) <= time );
			
			if condition: !(empty (to_be_released_pedestrian)) {
				loop rp over: to_be_released_pedestrian {
					let r_position type: point value: rp.released_location;
					release rp in: world as: pedestrian returns: r_pedestrian;
					set pedestrian(first (list (r_pedestrian))).last_road value: self;
					set pedestrian(first (list (r_pedestrian))).location value: r_position;
				}
			}
		}

	 	aspect base {
	 		draw shape: geometry color: rgb('yellow');
	 	}
	}
	
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

	species panel {
		int next_panel_id;
		int id;
		
		bool is_terminal <- false;
		
		init {
			if condition: (terminal_panel_ids contains id) {
				set is_terminal value: true;
			}
		}
		
		aspect base {
			draw image: shapeSign at: location size: 50;
		}
	}		

	species bounds {
		aspect base {
			draw shape: geometry color: rgb('gray');
		}
	}

	species guider skills: [moving] {
		road managed_road;
		point target1;
		point target2;
		bool reach_target1 <- false;
		bool reach_target2 <- false;
		bool finish_patrolling <- false;
		
		panel current_panel;
		bool reach_shelter;

		init {
			set location value: any_location_in(managed_road.shape);
			set target1 value: first ((managed_road.shape).points);
			set target2 value: last ((managed_road.shape).points);
			
			set current_panel value: (list (panel)) closest_to shape;
		}
		
		reflex patrol when: !(finish_patrolling) {
			if condition: !(reach_target1) {
				
				do action: goto {
					arg target value: target1;
					arg on value: road_graph_topology;
					arg speed value: guider_speed;
				}

				if condition: (location = target1) {
					set reach_target1 value: true;
				}
				
				else {
					if condition: !(reach_target2) {
						
						do action: goto {
							arg target value: target2;
							arg on value: road_graph_topology;
							arg speed value: guider_speed;
						}
						
						if condition: (location = target2) {
							set reach_target2 value: true;
						} else {
							set finish_patrolling value: true;
						}
					}
				}
			}
		}
		
		reflex move_to_shelter when: ( finish_patrolling and !(reach_shelter) and (current_panel != nil) and (location != (current_panel.location)) ) {
			do action: goto {
				arg target value: current_panel;
				arg on value: road_graph_topology;
				arg speed value: guider_speed;
			}
		}
		
		reflex switch_panel when: ( finish_patrolling and !(reach_shelter) and (location = (current_panel.location)) ) {
			if !(current_panel.is_terminal) {
				set current_panel value: one_of ( (list (panel)) where (each.id =  current_panel.next_panel_id) ) ;
			} else {
				set reach_shelter value: true;
			}
		}
		
		aspect base {
			draw shape: geometry color: guider_color;
		}
	}
	
	species pedestrian skills: [moving] {
		point previous_location;
		road last_road;

		bool reach_shelter <- false;
		panel current_panel;
   		float local_density <- 0; 
   		float speed;

		geometry window_viewer;
	
		reflex search_panel when: ( !(reach_shelter) and (current_panel = nil) ) {
			let nearest_panel type: panel value: panel closest_to self;
			if ( (nearest_panel != nil) and ( (nearest_panel distance_to self) <= pedestrian_perception_range ) ) {
				set current_panel value: nearest_panel;
			} else {
				let nearest_guider type: guider value: guider closest_to self;
				if condition: ( (nearest_guider != nil) and ( (nearest_guider distance_to self) <= pedestrian_perception_range ) ) {
					set current_panel value: nearest_guider.current_panel;

				} else {
					let neighbour_with_panel_info type: pedestrian value: one_of ( (pedestrian overlapping (shape + nearest_guider)) where (each.current_panel != nil) );
					if condition: (neighbour_with_panel_info != nil) {
						set current_panel value: neighbour_with_panel_info.current_panel;
					}
				}
			}
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
			let obstacle_pedestrians type: list of: pedestrian value: (pedestrian overlapping (window_viewer + 0.1));

			if (length(obstacle_pedestrians) = 0) { 
				set local_density value: 0;
				return pedestrian_max_speed;
			}
			
			set local_density value: ( float( length (obstacle_pedestrians) ) ) / local_density_window_area;
			
			if (local_density >= max_density) {  return value: 0; }
			
			return pedestrian_max_speed * ( 1 - (local_density / max_density) );
		}

		reflex move when: !(reach_shelter) and (current_panel != nil) {
			set previous_location value: location;
			
			set speed value: self compute_speed [];
			do action: goto {
				arg target value: current_panel;
				arg on value: road_graph_topology;
				arg speed value: speed;
			}
		}
		
		reflex switch_panel when: (current_panel != nil) and !(reach_shelter) and (location = current_panel.location) {
			if  !(current_panel.is_terminal) {
				set current_panel value: one_of ( (list (panel)) where (each.id =  current_panel.next_panel_id) ) ;
			} else {
				set reach_shelter value: true;
			}
		}
		
 		aspect base {
 			draw shape: geometry color: pedestrian_color;
 		}
	}
	
	species road_initializer skills: [moving] {
		action initialize {
			arg the_road type: road;
			
			let intersecting_terminal_panels type: list of: panel value: ( (list(panel)) where (terminal_panel_ids contains each.id) ) overlapping the_road.shape;
			if condition: empty(intersecting_terminal_panels) {
				
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
				set the_road.macro_patch_buffer value: inside_road_geom + 0.01;
				
				if condition: (inside_road_geom.perimeter > (macro_patch_length_coeff * pedestrian_speed) ) {
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
			
			do goto with: [ on :: road_graph_topology, speed :: local_density_window_length, target :: (current_people.current_panel).location ];
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
			} else {
				// test if the agent moves toward first point or last point of the PREVIOUS road
				
				let previous_location_2_goal type: float value: road_graph_topology distance_between [previous_location, (current_people.current_panel).location];
				let current_location_2_goal type: float value: road_graph_topology distance_between [current_location, (current_people.current_panel).location];

				let previous_road_first_point type: point value: first (((previous_road).shape).points);
				let previous_road_first_location_2_goal type: float value: road_graph_topology distance_between [previous_road_first_point, (current_people.current_panel).location];
				
				let previous_road_last_point type: point value: last ((previous_road.shape).points);
				let previous_road_last_location_2_goal type: float value: road_graph_topology distance_between [previous_road_last_point, (current_people.current_panel).location];
				
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
			}
		}
	}
}

environment bounds: shape_file_bounds;

experiment default_expr type: gui {
	output {
		display pedestrian_road_network {
		 	species road aspect: base transparency: 0.1;
		 	species panel aspect: base transparency: 0.01;
 			species pedestrian aspect: base transparency: 0.1;
		}
		
		display ward_road_network {
			species ward aspect: base transparency: 0.5;
		 	species road aspect: base transparency: 0.1;
 			species pedestrian aspect: base transparency: 0.1;
		}
		
		/*
		display guider_road_network {
		 	species road aspect: base transparency: 0.1;
		 	species panel aspect: base transparency: 0.01;
 			species guider aspect: base transparency: 0.1;
		}
 		*/
 
		display Execution_Time {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_duration_in_mili_second value: float(duration) color: (rgb ('green'));
			}
		}

/*
		display Pedetrian_vs_Captured_Pedetrian {
			chart name: 'Pedestrian_vs._Captured_Pedestrian' type: series background: rgb ('black') {
				data pedestrians value: length (list (pedestrian)) color: rgb ('blue');
				data captured_pedestrian value: sum (list(road) collect (length (each.members))) color: rgb ('white');  
			}
		}
	 */
	 	
		monitor pedestrians value: length (list(pedestrian));
		monitor captured_pedestrians value: sum (list(road) collect (length (each.members)));

		monitor pedestrians_reach_shelter value: length(list(pedestrian) where (each.reach_shelter));
		monitor pedestrians_NOT_reach_shelter value: length(list(pedestrian) where !(each.reach_shelter));
		
/*
		monitor pedestrians_WITH_panel_info value: length(list(pedestrian) where (each.current_panel != nil));
		monitor pedestrian_WITHOUT_panel_info value: length(list(pedestrian) where (each.current_panel = nil));

		monitor guiders_reach_shelter value: length(list(guider) where (each.reach_shelter));
		monitor guiders_NOT_reach_shelter value: length(list(guider) where !(each.reach_shelter));
		
		monitor step_duration value: duration;
		monitor simulation_duration value: total_duration;
		monitor average_step_duration value: average_duration;
		
		monitor roads_WITH_macro_patch value: (length (list(road) where (each.macro_patch != nil)));
		monitor roads_WITHOUT_macro_patch value: (length (list(road) where (each.macro_patch = nil)));
 */
 
		monitor total_pedestrian value: length(list(pedestrian)); 
		monitor average_speed value: (sum (list(pedestrian) collect (each.speed))) / (length(pedestrian));
		monitor stuck_people value: length(list(pedestrian) where (each.speed = 0));
		monitor average_local_density value: (sum (list(pedestrian) collect (each.local_density))) / (length(pedestrian));
		monitor average_step_duration value: average_duration;
	}
}