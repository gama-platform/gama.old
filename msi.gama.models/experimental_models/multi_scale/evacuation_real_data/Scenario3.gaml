model Scenario3

/*
this scenario is not necessary infact

 incase we have under 30 minutes of evacuation, pedestrians just find high level building to enter!!!
 
 Scenario1 is enough!
 */
 
global { 
	var guiders_per_road type: int parameter: 'Number of guider per road' init: 1 min: 0 max: 10;
	var simulated_population_rate type: float init: 0.1 const: true;
	
	// GIS data
	var shape_file_road type: string init: '/gis/roadlines.shp'; 
	var shape_file_rivers type: string init: '/gis/rivers.shp';
	var shape_file_beach type: string init: '/gis/Beacha.shp'; 
	var shape_file_roadwidth type: string init: '/gis/roads.shp';
	var shape_file_building type: string init: '/gis/buildr.shp';
	var shape_file_bounds type: string init: '/gis/bounds.shp';
	var shape_file_ward type: string init: '/gis/wards.shp';
	var shape_file_zone type: string init: '/gis/zone.shp';

	var insideRoadCoeff type: float init: 0.1 min: 0.01 max: 0.4 parameter: "Size of the external parts of the roads:";
 
	var pedestrian_speed type: float init: 1.0; // TODO how to define precisely 1m/s?
	var pedestrian_size type: float init: 1.0 const: true;
	var pedestrian_color type: rgb init: rgb('green');
	var pedestrian_perception_range type: float init: 100.0; // 100 meters
	
	var guider_speed type: float init: 1.0;
	var guider_color type: rgb init: rgb('blue');
	 
	var macro_patch_length_coeff type: int init: 25 parameter: "Macro-patch length coefficient";
	var capture_pedestrian type: bool init: true parameter: "Capture pedestrian?";

	var ward_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;
	var zone_colors type: list of: rgb init: [rgb('magenta'), rgb('blue'), rgb('yellow')] const: true;
	
	var agents_reach_target type: int init: 0;
	var average_reaching_target_time type: float init: 0.0;
	
	var zone1_building_color type: rgb init: rgb('orange'); 
	var zone2_building_color type: rgb init: rgb('gray');
	var zone3_building_color type: rgb init: rgb('yellow');
	
	var road_graph type: graph;
	
	init {
		create species: road from: shape_file_road;
		create species: beach from: shape_file_beach;
		 
		create species: ward from: shape_file_ward with: [id :: read('ID'), wardname :: read('Name'), population :: read('Population')] {
			do action: init_overlapping_roads;
		} 
		 
		create species: zone from: shape_file_zone with: [id :: read('ID')];
		create species: building from: shape_file_building with: [ floor :: read('STAGE'), x :: read('X'), y :: read('Y')];
		create species: roadwidth from: shape_file_roadwidth;
		create species: river from: shape_file_rivers;
		
		loop b over: ( (list(building)) where (each.floor > 3) ) {
			create species: dest with: [shape :: b.shape];
		}
		
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
			if condition: !(empty(w.roads)) {
				create species: pedestrian number: int ( (w.population * simulated_population_rate) ) {
					set location value: any_location_in (one_of (w.roads));
				}
			}
		}

		loop r over: list(road) {
			create species: guider number: guiders_per_road with: [ managed_road :: r ];
		}
	}	 

	reflex stop_simulation when: ( (time = 1800) or (  ( (length(list(pedestrian))) = (length(list(pedestrian) where each.reach_shelter)) ) and ( ( sum (list(road) collect (length (each.members))) ) = 0 ) ) ) {
		do action: write {
			arg message value: 'Simulation stops at time: ' + (string(time)) + ' with total duration: ' + total_duration + '\\n ;average duration: ' + average_duration
				+ '\\n ; pedestrians reach shelter: ' + (string(length( (list(pedestrian)) where (each.reach_shelter) )))
				+ '\\n ; pedestrians NOT reach shelter: ' + (string ( (length( (list(pedestrian)) where !(each.reach_shelter) )) + ( sum (list(road) collect (length (each.members))) ) ) );
		}
		
		do action: halt;
	}
}

environment bounds: shape_file_bounds;

entities {
	species road {
		var extremity1 type: geometry;
		var extremity2 type: geometry;
		
		var macro_patch type: geometry;
		var macro_patch_buffer type: geometry;

		var nearby_destinations type: list of: dest init: [];

		init {
			if condition: (macro_patch != nil) {
				set nearby_destinations value: dest overlapping (shape + 10); // 10m buffer
			}
		}

		species captured_pedestrian parent: pedestrian schedules: [] {
			var released_time type: int;
			var released_location type: point;
			
			aspect default {
				
			}
		}

		reflex capture_pedestrian when: ( (capture_pedestrian) and (macro_patch != nil) ) {
			
			let to_be_captured_pedestrian type: list of: pedestrian value: (pedestrian overlapping (macro_patch_buffer)) where ( 
					(each.shelter != nil) and !(each.reach_shelter) and !(each.shelter in nearby_destinations)
					and (each.last_road != self) and (each.previous_location != nil)
			);
			
			if condition: !(empty (to_be_captured_pedestrian)) {
				
				capture to_be_captured_pedestrian as: captured_pedestrian returns: c_people;
				
				loop cp over: c_people {
					let road_source_to_previous_location type: geometry value: ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry value: ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float value: 0;
					
					if condition: (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance value: geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: last (macro_patch.points);
						

					}
					else { // agent moves towards extremity1
							set skip_distance  value: geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
							set cp.released_location value: first (macro_patch.points);
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
					release rp in: world as: pedestrian returns: r_people;
					set pedestrian(first (list (r_people))).location value: r_position;
				}
			}
		}
		
		aspect base {
	 		draw shape: geometry color: rgb('yellow');
	 	}
	}
	 
	species dest {
	 	aspect base {
	 		draw shape: geometry color: rgb('magenta');
	 	}
	}
	 
	species zone {
	  	var id type: int;
	  	var color type: rgb init: rgb ( (zone_colors at (id - 1)) );
	  	
	  	aspect base {
	  		draw shape: geometry color: color;
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
		
	species roadwidth {
	   	aspect base {
	   		draw shape: geometry color: rgb('yellow');
	   	}
	}
	   
	species building {
	   	var floor type: int;
	   	var x type: float;
	   	var y type: float;
	   	var zone_id type: int;
	   	var color type: rgb init: zone3_building_color; 
	   	
	   	init {
	   		let overlapping_zone type: list of: zone value: zone overlapping shape;
	   		if condition: !(empty (overlapping_zone)) { 
	   			
	   			set zone_id value: (first(overlapping_zone)).id;
	   			
	   			if condition: (zone_id = 1) {
	   				set color value: zone1_building_color;
	   				
	   				
	   			}
	   			else {
	   					if condition: (zone_id = 2) {
	   						set color value: zone2_building_color;
	   					}
	   				}
	   		}
	   	}
	   	
	   	aspect base { 
	   		draw shape: geometry color: color;
	   	} 
	}
	   
	species beach {
	   	aspect base {
	   		draw shape: geometry color: rgb('green'); /// essai
	   	}
	}
		
	species river {
		aspect base {
			draw shape: geometry color: rgb('blue');
		}
	}
		
	species bounds {
		aspect base {
			draw shape: geometry color: rgb('gray');
		}
	}
	
	species guider skills: [moving] {
		var managed_road type: road;
		var target1 type: point;
		var target2 type: point;
		var reach_target1 type: bool init: false;
		var reach_target2 type: bool init: false;
		var finish_patrolling type: bool init: false;

		var safe_building type: dest;
		var reach_shelter type: bool init: false;
		
		init {
			set location value: any_location_in(managed_road.shape);
			set target1 value: first ((managed_road.shape).points);
			set target2 value: last ((managed_road.shape).points);

			set safe_building value: (list (dest)) closest_to shape;
		}
		
		reflex patrol when: !(finish_patrolling) {
			if condition: !(reach_target1) {
				
				do action: goto {
					arg target value: target1;
					arg on value: road_graph;
					arg speed value: guider_speed;
				}

				if condition: (location = target1) {
					set reach_target1 value: true;
				}
				
				else {
					if condition: !(reach_target2) {
						
						do action: goto {
							arg target value: target2;
							arg on value: road_graph;
							arg speed value: guider_speed;
						}
						
						if condition: (location = target2) {
							set reach_target2 value: true; 
						}
						
						else {
							set finish_patrolling value: true;
						}
					}
				}
			}
		}

		reflex move_to_shelter when: ( finish_patrolling and !(reach_shelter) ) {
			do action: goto {
				arg target value: safe_building;
				arg on value: road_graph;
				arg speed value: guider_speed;
			}
			
			if condition: (location = (safe_building.location)) {
				set reach_shelter value: true;
			}
		}
		
 		aspect base {
 			draw shape: geometry color: guider_color;
 		}
	}

	species pedestrian skills: [moving] {
		var previous_location type: point;
		var last_road type: road;

		var shelter type: dest;
		var current_road type: road;
		var reach_shelter type: bool init: false;
		
		init {
			
		}
		
		reflex search_shelter when: (shelter = nil) {
			let nearest_shelter type: dest value: dest closest_to self;
			if condition: ( (nearest_shelter != nil) and ( (nearest_shelter distance_to self) <= pedestrian_perception_range ) ) {
				set shelter value: nearest_shelter;
			}
			else {
					let nearest_guider type: guider value: guider closest_to self;
					if condition: ( (nearest_guider != nil) and ( (nearest_guider distance_to self) <= pedestrian_perception_range ) ) {
						set shelter value: nearest_guider.safe_building;
					}
					else {
							let neighbour_with_shelter type: pedestrian value: one_of ( ( pedestrian overlapping (shape + pedestrian_perception_range) ) where (each.shelter != nil) );
							
							if condition: (neighbour_with_shelter != nil) {
								set shelter value: neighbour_with_shelter.shelter;
							}
						}
					}		
				}
		
		reflex wander_around when: (shelter = nil) {
			
		}
		
		reflex move_to_shelter when: ( (shelter != nil) and !(reach_shelter) ) {
			set previous_location value: location;
			
			do action: goto {
				arg target value: shelter;
				arg on value: road_graph;
				arg speed value: pedestrian_speed;
			}
			
			if condition: (location = (shelter.location)) {
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

experiment default_expr type: gui {
	output { 
		display full_detail {
		 	species road aspect: base transparency: 0.1;
		 	species roadwidth aspect: base transparency: 0.1;
		 	species building aspect: base transparency: 0.1;
		 	species dest aspect: base transparency: 0.1;
		 	species beach aspect: base transparency: 0.9;
		 	species zone aspect: base transparency: 0.9;
		 	species river aspect: base transparency: 0.5;
		 	species ward aspect: base transparency: 0.9;
		 	species pedestrian aspect: base transparency: 0.1;
 			species guider aspect: base transparency: 0.1;
		}
		
		display pedestrian_road_network {
		 	species road aspect: base transparency: 0.1;
		 	species dest aspect: base transparency: 0.1;
		 	species pedestrian aspect: base transparency: 0.1;
		}
		
		display Execution_Time {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_duration_in_mili_second value: float(duration) color: (rgb ('green'));
			}
		}

		display Pedestrian_vs_Captured_Pedestrian {
			chart name: 'Pedestrian_vs._Captured_Pedestrian' type: series background: rgb ('black') {
				data pedestrians value: length (list (pedestrian)) color: rgb ('blue');
				data captured_people value: sum (list(road) collect (length (each.members))) color: rgb ('white');  
			}
		}

		monitor pedestrians value: length (list(pedestrian));
		monitor captured_pedestrians value: sum (list(road) collect (length (each.members)));

		monitor pedestrians_reach_shelter value: length(list(pedestrian) where (each.reach_shelter));
		monitor pedestrians_NOT_reach_shelter value: length(list(pedestrian) where !(each.reach_shelter));
		
		monitor guiders_reach_shelter value: length(list(guider) where (each.reach_shelter));
		monitor guiders_NOT_reach_shelter value: length(list(guider) where !(each.reach_shelter));
				
		monitor pedestrians_WITH_shelter_info value: length(list(pedestrian) where (each.shelter != nil));
		monitor pedestrian_WITHOUT_shelter_info value: length(list(pedestrian) where (each.shelter = nil));
		
		monitor step_duration value: duration;
		monitor simulation_duration value: total_duration;
		monitor average_step_duration value: average_duration;
		monitor destinations value: length(dest as list);

		monitor roads_WITH_macro_patch value: (length (list(road) where (each.macro_patch != nil)));
		monitor roads_WITHOUT_macro_patch value: (length (list(road) where (each.macro_patch = nil)));
	}
}