model evacuation_nearly_correct

global {
	float simulated_population_rate <- 1 min: 0 max: 1  parameter: 'Population rate';
	
	// GIS data
//	file shape_file_road <- shapefile('demo_gis/MainRoads.shp');
	file shape_file_road <- shapefile('demo_gis/roads.shp');
	string shape_file_bounds <- 'demo_gis/bounds.shp';
	string shape_file_shelters <- 'demo_gis/edu_gov.shp';
	string shape_file_ward <- 'demo_gis/wards.shp';

	float insideRoadCoeff <- 0.1 min: 0.01 max: 0.4 parameter: 'Size of the external parts of the roads:';
	
	rgb pedestrian_color <- rgb('green') const: true;
	
	float pedestrian_max_speed <- 5.4 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_window_length <- 2 min: 1 max: 5 parameter: 'Density window length' category: 'Pedestrian';
	float max_density <- 5 min: 1 max: 10 parameter: 'Max density (people per m2)' category: 'Pedestrian';

	int macro_patch_length_coeff <- 5 parameter: 'Macro-patch length coefficient';
	bool is_hybrid_simulation <- false parameter: 'Is hybrid simulation?';

	list ward_colors of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')] const: true;

	topology road_graph_topology;
	
	list nearest_roads_to_shelters <- [] type: list of: road;
	
	rgb shelter_color <- rgb('blue') const: true;
	rgb road_color <- rgb('black') const: true;
	
	float pedestrians_average_speed -> { self compute_average_speed [] };
	int pedestrians_reach_target -> { self compute_pedestrians_reach_target [] };
	int pedestrians_reach_shelter -> { length(list(pedestrian) where each.reach_shelter) };
	
	list not_reach_schelters of: pedestrian init: list(pedestrian) value: list(pedestrian) where !((pedestrian(each)).reach_shelter);
	
	rgb BLACK <- rgb('black') const: true;
	rgb BLUE <- rgb('blue') const: true;
	
//	string simulation_output -> { self build_simulation_output [] } ;
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
		
		ask list(density_grid) { do initialize; }
		
		loop w over: list(ward) {
			if condition: !(empty(w.roads)) {
				create pedestrian number: int ( (w.population * simulated_population_rate) ) {
					set location value: any_location_in (one_of (w.roads));
				}
			}
		}

/*
		create pedestrian number: 10 {
			set location value: any_location_in (one_of (list(road)));
		}
		* 
		*/
	}

}

environment bounds: shape_file_bounds {
	
	/*
	 * world.shape with width = 3291.63 meters and height = 4302.86 meters
	 * 
	 * => a grid with 329 x 430 = 141470 squares <=> 141470 agents
	 * 		square area ~ 10 meters x 10 meters
	 */
	grid density_grid width: 329 height: 430 {
//	grid density_grid width: 658 height: 860 { // 565880 agents -> not enough memory!
		list roads of: road;
		map road_intersections <- [] as map;
		map road_intersections_area <- [] as map;

		list pedestrians of: pedestrian value: (pedestrian overlapping self) where !(each.reach_shelter);
//		rgb color value: empty(pedestrians) ? BLACK : BLUE depends_on: [pedestrians]; // comment out the variable when need to draw the grid
		
		map speed_by_roads <- ([] as map);
		
		action initialize {
			set roads value: road overlapping self;
			
			loop r over: roads {
				add ( r :: ( (r inter self) + 0.1) ) to: road_intersections; // buffer road intersections
				add ( r :: ( (r inter self).perimeter * (r.width) ) ) to: road_intersections_area;
			}
		}
		
		reflex {
//			set speed_by_roads value: [] as map;
			remove from: speed_by_roads all: speed_by_roads;
			
			if !(empty(pedestrians)) {
				loop r over: roads {
					let speed type: float value: pedestrian_max_speed * ( 1 - ( ( float( length(pedestrians overlapping geometry(road_intersections at r)) ) / float( road_intersections_area at r) ) / max_density ) );
					add r :: speed to: speed_by_roads;
				}	
			}
		} 
		
		action get_speed type: float {
			arg a_road type: road;
			
			if (speed_by_roads contains a_road) {
				return (speed_by_roads at a_road);
			}
			
			return pedestrian_max_speed;
		}
	}
}

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
			
			let to_be_captured_pedestrian type: list of: pedestrian value: (pedestrian overlapping (macro_patch_buffer)) where !(each.reach_shelter);
			if condition: ! (empty(to_be_captured_pedestrian)) {
				set to_be_captured_pedestrian value: to_be_captured_pedestrian where (
					(each.current_road = self)
					and (each.last_macro_patch != self)
					and (each.previous_location != nil)
					and (each.speed > 0));
			}
			
			if !(empty (to_be_captured_pedestrian)) {
				capture to_be_captured_pedestrian as: captured_pedestrian returns: c_pedestrian;
				
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
			
			if !(empty (to_be_released_pedestrian)) {
				loop rp over: to_be_released_pedestrian {
					let r_position type: point value: rp.released_location;
					release rp in: world as: pedestrian returns: r_pedestrian;
					set pedestrian(first (list (r_pedestrian))).last_macro_patch value: self;
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

		bool reach_shelter <- false;
   		density_grid my_cell value: density_grid closest_to self;
   		float speed value: (my_cell != nil) ? (my_cell get_speed [ a_road :: (road closest_to self)] ) : pedestrian_max_speed depends_on: [my_cell];
   	
   		road last_macro_patch;
   		road current_road value: ( (list(road)) closest_to self ) depends_on: location;
   		point previous_location;

		geometry window_viewer;
		float local_density_window_area;
		shelter goal <- (list(shelter) closest_to self) depends_on: [ shape, location ];
		
		reflex move when: !(reach_shelter) {
			set previous_location value: location;

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
		
//		reflex when: (length(density_grid overlapping self) > 1) {
//			write name + ' overlapping with: ' + (string((density_grid overlapping self)));
//		}
		
 		aspect base {
// 			draw shape: geometry color: pedestrian_color;
 			draw shape: circle size: 20 color: pedestrian_color;
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
}

experiment HundredPercent_Population_Pure_ABM type: gui {
	parameter 'Hybrid simulation' var: is_hybrid_simulation init: false;
	  
	output {
		display ward_road_network {
//			species ward aspect: base transparency: 0.5;
			species road aspect: base transparency: 0.5;
 			species pedestrian aspect: base transparency: 0.1;
 			species shelter aspect: base transparency: 0.1;
		}
		
//		display grid_display {
//			grid density_grid;
//		}
		
//		display agent_display {
//			agents road_agent value: list(road) at 0 aspect: base;
//		}
		
		monitor total_pedestrian value: length(list(pedestrian)); 
		monitor average_speed value: pedestrians_average_speed;
		monitor pedestrians_reach_target value: pedestrians_reach_target;
		monitor stuck_people value: length( list(pedestrian) where ( (each.speed = 0)  and !(each.reach_shelter) ) );
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

//		file ABM_experiment_output data: simulation_output refresh_every: 2;
	}
}

experiment HundredPercentPopulation_Hybrid type: gui {
	parameter 'Hybrid simulation' var: is_hybrid_simulation init: true;  

	output {
		display ward_road_network {
//			species ward aspect: base transparency: 0.5;
			species road aspect: base transparency: 0.5;
 			species pedestrian aspect: base transparency: 0.1;
 			species shelter aspect: base transparency: 0.1;
		}
		
		monitor captured_pedestrians value: sum(list(road) collect ( length((each.members))) );
		monitor not_captured_pedestrians value: length(list(pedestrian));
		
		monitor average_speed value: pedestrians_average_speed;
		monitor pedestrians_reach_target value: pedestrians_reach_target;
		monitor stuck_people value: length( list(pedestrian) where ( (each.speed = 0)  and !(each.reach_shelter) ) );
		
		monitor average_step_duration value: average_duration;
		
		monitor roads_WITH_macro_patch value: length( (list(road) where (each.macro_patch != nil)) );
		monitor roads_WITHOUT_macro_patch value: length( (list(road) where (each.macro_patch = nil)) );
		monitor nearest_roads value: length(nearest_roads_to_shelters);
		
		monitor all_pedestrians value: length(world.agents of_generic_species pedestrian);
		
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

		
//		file HYBRID_experiment_output data: simulation_output refresh_every: 2;
	}
}