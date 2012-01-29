model Scenario2

global {
	var simulated_population_rate type: float init: 0.02 const: true;
	
	// GIS data
	var shape_file_road type: string init: '/gis/roadlines.shp';
	var shape_file_bounds type: string init: '/gis/bounds.shp';
	var shape_file_panel type: string init: '/gis/panel.shp';
	var shape_file_ward type: string init: '/gis/wards.shp';

	var insideRoadCoeff type: float init: 0.1 min: 0.01 max: 0.4 parameter: "Size of the external parts of the roads:";

	var pedestrian_speed type: float init: 1;
	var pedestrian_color type: rgb init: rgb('green') const: true;
	
	var macro_patch_length_coeff type: int init: 25 parameter: "Macro-patch length coefficient";
	var capture_pedestrian type: bool init: true parameter: "Capture pedestrian?";

	var ward_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;
	var zone_colors type: list of: rgb init: [rgb('magenta'), rgb('blue'), rgb('yellow')] const: true;

	var shapeSign type: string init: '/icons/CaliforniaEvacuationRoute.jpg' const: true;

	var terminal_panel_ids type: list of: int init: [8];

	var road_graph type: graph;

	init {
		create species: road from: shape_file_road;
		create species: ward from: shape_file_ward with: [id :: read('ID'), wardname :: read('Name'), population :: read('Population')] {
			do action: init_overlapping_roads;
		}
		create species: panel from: shape_file_panel with: [next_panel_id :: read('TARGET'), id :: read('ID')];
		set road_graph value: as_edge_graph (list(road) collect (each.shape));

		create species: road_initializer;
		let ri type: road_initializer value: first (road_initializer as list);
		loop rd over: (road as list) {
			ask target: (ri) {
				do action: initialize {
					arg the_road value: rd;
				}
			}
		}

		loop w over: list(ward) {
			create species: pedestrian number: int ( (w.population * simulated_population_rate) ) {
				set location value: any_location_in (one_of (w.roads));
			}
		}
	}	 

	reflex stop_simulation when: (time = 5400) {
		do action: write {
			arg message value: 'Simulation stops at time: ' + (string(time)) + ' with total duration: ' + total_duration + '\\n ;average duration: ' + average_duration
				+ '\\n ; pedestrians reach shelter: ' + (string(length( (list(pedestrian)) where (each.reach_shelter) )))
				+ '\\n ; pedestrians NOT reach shelter: ' + (string ( (length( (list(pedestrian)) where (each.reach_shelter) )) + ( sum (list(road) collect (length (each.members))) ) ) );
		}
		
		do action: halt;
	}
}

entities {
	species road {
		var extremity1 type: geometry;
		var extremity2 type: geometry;
		
		var macro_patch type: geometry;
		var macro_patch_buffer type: geometry;

		species captured_pedestrian parent: pedestrian schedules: [] {
			var released_time type: int;
			var released_location type: point;
			
			aspect default {
				
			}
		}

		reflex capture_pedestrian when: ( (capture_pedestrian) and (macro_patch != nil) ) {
			
			let to_be_captured_people type: list of: pedestrian value: (pedestrian overlapping (macro_patch_buffer)) where !(each.reach_shelter);
			if condition: ! (empty(to_be_captured_people)) {
				set to_be_captured_people value: to_be_captured_people where (
					(each.last_road != self)
					and (each.previous_location != nil) 
					and (each.location != ((each.current_panel).location)));
			}
			
			if condition: !(empty (to_be_captured_people)) {
				capture target: to_be_captured_people as: captured_pedestrian returns: c_people;
				
				loop cp over: c_people {
					let road_source_to_previous_location type: geometry value: ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry value: ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float value: 0;
					
					if condition: (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance value: geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: last (macro_patch.points);
						
						else { // agent moves towards extremity1
							set skip_distance  value: geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
							set cp.released_location value: first (macro_patch.points);
						}
					}

					set cp.last_road value: self;
					set cp.released_time value: time + (skip_distance / pedestrian_speed);
				}
			}
		}
		
		reflex release_captured_people when: (macro_patch != nil) {
			let to_be_released_people type: list of: captured_pedestrian value: (members) where ( (captured_pedestrian(each).released_time) <= time );
			
			
			if condition: !(empty (to_be_released_people)) {
				loop rp over: to_be_released_people {
					let r_position type: point value: rp.released_location;
					release target: rp returns: r_people;
					set pedestrian(first (list (r_people))).location value: r_position;
				}
			}
		}

	 	aspect base {
	 		draw shape: geometry color: rgb('yellow');
	 	}
	}
	
	species ward {
	  	var id type: int;
	  	var population type: int min: 0;
	  	var wardname type: string;
	  	var color type: rgb init: one_of(ward_colors);
	  	var roads type: list of: road;
	  	
	  	action init_overlapping_roads {
	  		set roads value: road overlapping shape;
	  	}
	  	
	  	
	  	aspect base {
	  		draw shape: geometry color: color;
	  	}
	}

	species panel {
		var next_panel_id type: int;
		var id type: int;
		
		var is_terminal type: bool init: false;
		
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

	species pedestrian skills: moving {
		var current_panel type: panel init: nil;
		
		var previous_location type: point;
		var last_road type: road;
		var reach_shelter type: bool init: false;

		init {
			set current_panel value: (list (panel)) closest_to shape;
		}
		
		reflex move when: ( !(reach_shelter) and (current_panel != nil) and (location != (current_panel.location)) ) {
			set previous_location value: location;
			
			do action: goto {
				arg target value: current_panel;
				arg on value: road_graph;
				arg speed value: pedestrian_speed;
			}
		}
		
		reflex switch_panel when: !(reach_shelter) and (location = (current_panel.location)) {
			if condition: !(current_panel.is_terminal) {
				
				set current_panel value: one_of ( (list (panel)) where (each.id =  current_panel.next_panel_id) ) ;
				
				else {
					set reach_shelter value: true;
				}
			}
		}
		
		aspect base {
			draw shape: geometry color: pedestrian_color;
		}
	}

	species road_initializer skills: [moving] {
		action initialize {
			arg the_road type: road;
			
			let inside_road_geom type: geometry value: the_road.shape;
			set speed value: (the_road.shape).perimeter * insideRoadCoeff;
			let point1 type: point value: first(inside_road_geom.points);
			let point2 type: point value: last(inside_road_geom.points);
			set location value: point1;
			
			do action: goto {
				arg target value: point2;
				arg on value: road_graph; 
			}

			let lines1 type: list of: geometry value: (inside_road_geom split_at location);
			set the_road.extremity1 value: lines1  first_with (geometry(each).points contains point1);
			set inside_road_geom value: lines1 first_with (!(geometry(each).points contains point1));
			set location value: point2;
			do action: goto {
				arg target value: point1;
				arg on value: road_graph; 
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

environment bounds: shape_file_bounds;

experiment default_expr type: gui {
	output {
		display pedestrian_road_network {
		 	species road aspect: base transparency: 0.1;
		 	species panel aspect: base transparency: 0.01;
 			species pedestrian aspect: base transparency: 0.1;
		}

		display Execution_Time {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_duration_in_mili_second value: duration color: (rgb ('green'));
			}
		}

		display Pedetrian_vs_Captured_Pedetrian {
			chart name: 'Pedestrian_vs._Captured_Pedestrian' type: series background: rgb ('black') {
				data pedestrians value: length (list (pedestrian)) color: rgb ('blue');
				data captured_pedestrian value: sum (list(road) collect (length (each.members))) color: rgb ('white');  
			}
		}
		
		monitor pedestrians value: length (list(pedestrian));
		monitor captured_pedestrians value: sum (list(road) collect (length (each.members)));

		monitor pedestrians_reach_target value: length(list(pedestrian) where (each.reach_shelter));
		monitor pedestrians_NOT_reach_target value: length(list(pedestrian) where !(each.reach_shelter));
		monitor step_duration value: duration;
		monitor simulation_duration value: total_duration;
		monitor average_step_duration value: average_duration;
	}
}