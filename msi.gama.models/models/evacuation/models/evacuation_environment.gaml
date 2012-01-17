model evacuation_environment

global {
	/** GIS data */
	var shape_file_road type: string init: '../gis/roadlines.shp';
	var shape_file_rivers type: string init: '../gis/rivers.shp';
	var shape_file_beach type: string init: '../gis/beach.shp';
	var shape_file_roadwidth type: string init: '../gis/roads.shp';
	var shape_file_building type: string init: '../gis/buildr.shp';
	var shape_file_bounds type: string init: '../gis/bounds.shp';
	var shape_file_destination type: string init: '../gis/Destination.shp';

	var insideRoadCoeff type: float init: 0.1 min: 0.01 max: 0.4 parameter: "Size of the external parts of the roads:";
	var people_speed type: float init: 2;
	var people_number type: int init: 1000 parameter: "Number of agents";
	var people_shape_buffer type: float init: 0.01;
	
	var capture_pedestrian type: bool init: false parameter: "Capture pedestrian?";

	var theRoadInitializer type: initializerRoad;

	const building_colors type: list init: ['orange', 'red', 'blue', 'black', 'gray', 'magenta'];
	var the_graph type: graph;
	
	
	init {
		create species: road from: shape_file_road returns: the_roads;
//		create species: beach from: shape_file_beach;
//		create species: building from: shape_file_building; 
//		create species: roadwidth from: shape_file_roadwidth;
//		create species: river from: shape_file_rivers;
		create species: destination from: shape_file_destination with: [fid :: read ('IND')];
		set the_graph value: as_edge_graph (list(road) collect (each.shape));

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
			set goal value: one_of (destination as list) ;
			set location value:any_location_in(one_of(list(road)).shape);
		}
	}
}

environment bounds: shape_file_bounds;

entities {
	species road skills: situated {
		var extremity1 type: geometry;
		var extremity2 type: geometry;
		
		var macro_patch type: geometry;
		var macro_patch_buffer type: geometry;

		species captured_people parent: people schedules: [] {
			var released_time type: int;
			var released_location type: point;
			
			aspect default {
				
			}
		}

		reflex capture_people when: capture_pedestrian {

			let to_be_captured_people type: list of: people value: (people overlapping (macro_patch_buffer));
			if condition: ! (empty(to_be_captured_people)) {
				set to_be_captured_people value: to_be_captured_people where (
				(each.last_road != self)
				and (each.previous_location != nil) 
				and (each.location != ((each.goal).location)));
			}


			
			if condition: !(empty (to_be_captured_people)) {
				capture target: to_be_captured_people as: captured_people returns: c_people;
				
				loop cp over: c_people {
					let road_source_to_previous_location type: geometry value: ( shape split_at (cp.previous_location) ) first_with ( geometry(each).points contains (cp.previous_location) ) ;
					let road_source_to_current_location type: geometry value: ( shape split_at (cp.location) ) first_with ( geometry(each).points contains cp.location);
					
					let skip_distance type: float value: 0;
					
					if condition: (road_source_to_previous_location.perimeter < road_source_to_current_location.perimeter) { // agent moves towards extremity2
						set skip_distance value: geometry( (macro_patch split_at cp.location) last_with (geometry(each).points contains cp.location) ).perimeter;
						set cp.released_location value: last (macro_patch.points);
						
						else { // agent moves towards extremity2 
							set skip_distance  value: geometry( (macro_patch split_at cp.location) first_with (geometry(each).points contains cp.location) ).perimeter;
							set cp.released_location value: first (macro_patch.points);
						}
					}

/*
					do action: write {
						arg name: message value: 'skip_distance: ' + (string (skip_distance)) + '; released_time : ' + string(time + (skip_distance / people_speed));
					}
					 */
					 
					set cp.last_road value: self;
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
			draw shape: geometry color: 'yellow';
		}
	}
	
	species roadwidth skills: situated {
		aspect default {
			draw shape: geometry color: 'yellow';
		}
	}
	
	species building skills: situated {
		aspect default {
			draw shape: geometry color: 'red';
		}
	}
	
	species beach skills: situated {
		aspect default {
			draw shape: geometry color: 'green';
		}
	}
	
	species river skills: situated {
		aspect default {
			draw shape: geometry color: 'blue';
		}
	}
	
	species destination skills: situated {
		var fid type: int;
		var color type: rgb init: rgb (one_of (building_colors));
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	
	species people skills: [moving] {
		var previous_location type: point;
//		var previous_location type: point init: location depends_on: [location];
		
		// hack: people can only travel one direction!!!
		var last_road type: road;

		var goal type: destination ;
		var my_path type: path;
		//var shape_buffer type: geometry init: shape + people_shape_buffer value: shape + people_shape_buffer depends_on: [shape];

/*	
		aspect default {
			draw shape: circle color: 'green' size: 50 ;
		}
	 */
	 
		reflex when: (location != goal.location) {
			set previous_location value: location;
			do action:goto with: [on::the_graph, target::goal.location, speed::people_speed];
			//set previous_location value: followedPath.source;
			
		//	loop s over: followedPath.segments {
		//		do action: write {
		//			arg name: message value: 'At time: ' + (string (time)) + ' ' + name + ' with ' + (string (followedPath agent_from_geometry s));
		//		}
		//	}
			
		//	let the_road type: road value: followedPath agent_from_geometry (last (followedPath.segments));
		//	do action: write {
		//		arg name: message value: name + ' is current on ' + (string (the_road));
		//	}
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
			
			create species: insideRoad {
				set shape value: inside_road_geom;
			}
		}
	}

	species insideRoad {
		aspect default {
			draw shape: geometry color: 'red';
		}
	}
}

output {
	display name: 'Display' {
		species road aspect: default transparency: 0.1;
	//	species roadwidth aspect: default transparency: 0.1;
		species building aspect: default transparency: 0.8;
		species beach aspect: default transparency: 0.9;
		species river aspect: default transparency: 0.5;
		species destination aspect: default transparency: 0.1;
		species people aspect: default;
	}
		
		/*
		display macro_patches {
			species insideRoad;
			species people aspect: default;
		}
		*/
		
	monitor people_number value: length (people);
	monitor captured_people_number value: length (list(road) collect (length (each.members)));
		monitor step_length_monitor value: step_length;

	display Execution_Time refresh_every: 5 {
		chart name: 'Simulation step length' type: series background: rgb('black') {
			data simulation_step_length_in_mili_second value: step_length color: (rgb ('green'));
		}
	}
		
	display People_vs_Captured_People refresh_every: 5 {
		chart name: 'People_vs._Captured_People' type: series background: rgb ('black') {
			data people value: length (list (people)) color: rgb ('blue');				data captured_people value: sum (list(road) collect (length (each.members))) color: rgb ('white');  
		}
	}
	
}
