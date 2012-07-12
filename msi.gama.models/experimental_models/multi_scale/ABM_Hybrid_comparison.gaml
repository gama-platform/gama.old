model ABM_Hybrid_comparison

global {
	var insideRoadCoeff type: float init: 0.05 min: 0.05 max: 0.4 parameter: 'Size of the external parts of the roads:';
	var the_graph type: graph;
	
	var capture_pedestrian type: bool init: true parameter: 'Capture pedestrian?';
  
	var people_speed type: float init: float(2) min: float(1) max: float(100) parameter: 'People speed';
	var people_number type: int min: 1 init: 100 parameter: "People number";
	
	var environment_size type: int init: 500;
	
	var road_data type: list of: geometry init:[polyline ( [ {5, 5}, {20, 20}, {50, 5}, {75, 80} ] ), polyline ( [ {75, 80}, {120, 130}, {200, 170}, {490, 250} ] ),
		polyline ( [ {75, 80}, {100, 60}, {200, 20}, {250, 5} ] ), polyline ( [ {250, 5}, {300, 40}, {400, 100} ] ), polyline ( [ {400, 100}, {450, 20}, {480, 5} ] ),
		polyline ( [ {75, 80}, {50, 160}, {30, 200}, {5, 300} ] ), polyline ( [ {5, 300}, {50, 200}, {120, 300} ] ), polyline ( [ {120, 300}, {10, 400} ] ),polyline ( [ {120, 300}, {200, 250}, {300, 350} ]),
polyline ( [ {10, 400}, {450, 400} ] )
	];
	
	var road_number type: int min: 1 max: 10 init: 10; 
	
	init { 
		let i type: int value: 0;  
		loop times: road_number { 
			create species: road {
				set shape value: road_data at i;
			}  
			
			set i value: i + 1;
		}
		set the_graph value: as_edge_graph (list(road));
		
		create species: initializerRoad;
		let theRoadInitializer type: initializerRoad value: first (initializerRoad as list);
		loop rd over: (road as list) {
			ask target: (theRoadInitializer) {
				do action: InitializeRoad {
					arg the_road value: rd; 
				}
			} 
		}
		
		create species: people number: people_number {
			let a_road type: road value: one_of(road as list);
			set current_road value: a_road;
			set location value: any_location_in (a_road.shape);
			set goal value: ( (rnd (5)) > 2 ) ? ( first ( (current_road.shape).points ) ) : ( last ( (current_road.shape).points ) );
		}
	}
}

environment width: environment_size height: environment_size {
}

entities {
	species road {
		var extremity1 type: geometry;
		var extremity2 type: geometry;
		
		var macro_patch type: geometry;
		var macro_patch_buffer type: geometry;
		
		var source type: point;
		var target type: point; 
		
		var source_in_out_edges type: list of: road;
		var target_in_out_edges type: list of: road;
	
		species captured_people parent: people schedules: [] {
			var released_time type: int;
			var released_location type: point;
			
			aspect default {
				
			}
		}
		
		init {
			
		}
		
		reflex capture_people when: capture_pedestrian {
			let to_be_captured_people type: list of: people value: (people overlapping macro_patch_buffer) where (  
				(each.previous_location != nil) 
				and (each.last_macro != self)
			);
			
			
			if condition: !(empty (to_be_captured_people)) {
				capture target: to_be_captured_people as: captured_people returns: c_people;
				
				loop cp over: c_people {
					let road_source_to_previous_location type: geometry value: ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry value: ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float value: 0;
					
					if condition: (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance value: geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: last (macro_patch.points);
						 

					}
					else { // agent moves towards extremity2 
							set skip_distance  value: geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
							set cp.released_location value: first (macro_patch.points);
						}

/*
					do action: write {
						arg name: message value: 'skip_distance: ' + (string (skip_distance)) + '; released_time : ' + string(time + (skip_distance / people_speed));
					}
	 */
	 				
					set cp.last_macro value: self;
					set cp.released_time value: time + (skip_distance / people_speed);
				}
			}
		}
		
		reflex release_captured_people {
			let to_be_released_people type: list of: captured_people value: (members) where ( (captured_people(each).released_time) <= time );
			
			
			if condition: !(empty (to_be_released_people)) {
				loop rp over: to_be_released_people {
					let r_position type: point value: rp.released_location;
					release target: rp returns: r_people;
					set people(first (list (r_people))).location value: r_position;
				}
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb('black');
		}
	}
	
	species insideRoad {
		aspect default {
			draw shape: geometry color: rgb('red');
		}
	}
	
	species people skills: [moving] {
		var goal type: point ;
		
		var previous_location type: point;
		var last_macro type: road;
		var current_road type: road;
		
		aspect default {
			draw shape: geometry color: rgb('green') ;
		}
		
		action choose_next_road type: road { 
			arg road_vertex type: point;
			
			if condition: ( (the_graph source_of current_road) = road_vertex ) {
				return one_of (current_road.source_in_out_edges);
								

			}
		else {
					return one_of (current_road.target_in_out_edges);
				}
		}
		
		reflex move when: (goal != nil) {
			let followedPath type: path value: self goto [on::the_graph, target::goal, speed::people_speed];
			set previous_location value: followedPath.source;
			
			if condition: (goal = location) {
				set last_macro value: nil;
				set previous_location value: nil;

				set current_road value: self choose_next_road [ road_vertex :: goal ];
				
				if condition: (location = (the_graph source_of current_road) ) {
					set goal value: the_graph target_of current_road;
					

				}
				else {
						set goal value: the_graph source_of current_road;
					}
			}
		}
	}
	
	species dest {
		var color type: rgb init: rgb('red');
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	species initializerRoad skills: [moving] {
		action InitializeRoad {
			arg the_road type: road;
			let inside_road_geom type: geometry value: the_road.shape;
			set speed value: (the_road.shape).perimeter * insideRoadCoeff;
			let point1 type: point value: first(inside_road_geom.points);
			let point2 type: point value: last(inside_road_geom.points);
			set location value: point1;
			do action: goto {
				arg target value: point2;
				arg on value:the_graph; 
			}
			let lines1 type: list of: geometry value: (inside_road_geom split_at location);
			set the_road.extremity1 value: lines1  first_with (geometry(each).points contains point1);
			set inside_road_geom value: lines1 first_with (!(geometry(each).points contains point1));
			set location value: point2;
			do action: goto {
				arg target value: point1;
				arg on value: the_graph; 
			}
			let lines2 type: list of: geometry value: (inside_road_geom split_at location);
			
			set the_road.extremity2 value:  lines2 first_with (geometry(each).points contains point2);
			set inside_road_geom value: lines2 first_with (!(geometry(each).points contains point2));
			set the_road.macro_patch value: inside_road_geom;
			set the_road.macro_patch_buffer value: inside_road_geom + 0.01;
			
			set the_road.source value: first ((the_road.shape).points);
			set the_road.target value: last ((the_road.shape).points);
			set the_road.source_in_out_edges value: (the_graph in_edges_of the_road.source) + (the_graph out_edges_of the_road.source);
			set the_road.target_in_out_edges value: (the_graph in_edges_of the_road.target) + (the_graph out_edges_of the_road.target);


			create species: insideRoad {
				set shape value: inside_road_geom;
			}
		}
	}
}

experiment 10_roads_100_people type: gui {
	// Hybrid 1ms-2ms/step
	// ABM 1ms-2ms/step 
	
	parameter name: 'People number' var: people_number init: 100;
	
	output {
		display default_display {
			species road aspect: default ;
			species insideRoad aspect: default;
			species destination aspect: default ;
			species people aspect: default;
		}
		
		monitor people_number value: length (people);
		monitor captured_people_number value: sum (list(road) collect (length (each.members)));
		monitor step_length_monitor value: duration;
		
		display Execution_Time refresh_every: 5 {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_length_in_mili_second value: float( duration) color: (rgb ('green'));
			}
		}
	}
}

experiment 10_roads_1000_people_expr type: gui {
	// ABM 20ms-30ms/step
	// Hybrid 6ms-7ms/step
	// Hybris is 3.5-4 times faster
	
	parameter name: 'People number' var: people_number init: 1000;

	output {
		display default_display {
			species road aspect: default ;
			species insideRoad aspect: default;
			species dest aspect: default ;
			species people aspect: default; 
		}
		
		monitor people_number value: length (people);
		monitor captured_people_number value: sum (list(road) collect (length (each.members)));
		monitor step_length_monitor value: duration;
		
		display Execution_Time {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_length_in_mili_second value: float( duration) color: (rgb ('green'));
			}
		}
	}
}

experiment 10_roads_10000_people type: gui {
	// Hybrid: 50ms-100ms/step
	// ABM: 250ms-350ms/step
	// Hybrid is 5 times faster
	
	parameter name: 'People number' var: people_number init: 10000;

	output {
		display default_display {
			species road aspect: default ;
			species insideRoad aspect: default;
			species destination aspect: default ;
			species people aspect: default;
		} 
		
		monitor people_number value: length (people);
		monitor captured_people_number value: sum (list(road) collect (length (each.members)));
		monitor step_length_monitor value: duration;
		
		display Execution_Time {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_length_in_mili_second value: float( duration) color: (rgb ('green'));
			}
		}
	}
}

experiment 10_roads_100000_people type: gui {
	// ABM 3500ms-4000ms/step
	// Hybrid 850ms-950ms/step
	// Hybrid is 4 times faster
	
	parameter name: 'People speed' var: people_speed init: 2.0;
	parameter name: 'People number' var: people_number init: 100000;

	output {
		display default_display {
			species road aspect: default ;
			species insideRoad aspect: default;
			species destination aspect: default ;
			species people aspect: default;
		}
		
		monitor people_number value: length (people);
		monitor captured_people_number value: sum (list(road) collect (length (each.members)));
		monitor step_length_monitor value: duration;
		
		display Execution_Time  {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_length_in_mili_second value: float( duration) color: (rgb ('green'));
			}
		}
	}
}
