model pedestrian_speed_vs_density

global {
	float people_max_speed <- 1 min: 0.1 max: 1.6 parameter: 'Max speed (m per s)' category: 'People';
	float max_density <- 1.5 min: 1 max: 10 parameter: 'Max density (people per m2)' category: 'People';
	float local_density_window_length <- 2 min: 1 max: 5 parameter: 'Density window length' category: 'People';

	int road_width <- 15 min: 4 max: 40 parameter: 'Width (m)' category: 'Road';
	float local_density_window_area <- road_width * local_density_window_length depends_on: [ road_width, local_density_window_length ];

	topology road_graph_topology;
	density_window_builder window_builder;
	
	point TARGET1 <- {5, 5} const: true;
	point TARGET2 <- {10, 90} const: true;
	
	list TARGETS <- [TARGET1, TARGET2] const: true;

	init {
		create species: road {
			set shape value: polyline([{5,5}, {20,20}, {50,5}, {75,80}]);
			set color value: rgb('green');
		}
		
		create species: road {
			set shape value: polyline([ {75, 80}, {90, 85}, {10, 90} ]);
			set color value: rgb('blue');
		}
		
		set road_graph_topology value: topology(as_edge_graph (list(road))); 
		
		create density_window_builder returns: builders;
		set window_builder value: builders at 0;
		
		create people;
	}

//	reflex generate_people {
//		create people number: 20;
//	}	
}

environment width: 100 height: 100;

entities {
	species road {
		rgb color;
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	species people skills: [moving] {
		point location <- any_location_in ((one_of(road as list)).shape);
		point goal <- one_of(TARGETS);
		rgb color <- rgb('magenta');
   		density_window_viewer window_viewer <- (self new_window_viewer []);
   		float local_density <- 0;
		
        action new_window_viewer type: density_window_viewer {
            create density_window_viewer returns: rets;
            return rets at 0;
        }
        	
		action compute_speed type: float {
			ask window_builder {
				do build with: [ current_people :: myself ];
			}
			
//			let obstacle_pedestrians type: list of: people value: (people overlapping window_viewer.shape);
			let obstacle_pedestrians type: list of: people value: (people overlapping (window_viewer.shape + 0.1));

			if (length(obstacle_pedestrians) = 0) { 
				set local_density value: 0;
				return people_max_speed;
			}
			
			set local_density value: ( float( length (obstacle_pedestrians) ) ) / local_density_window_area;
			
			if (local_density >= max_density) {  return value: 0; }
			
			return people_max_speed * ( 1 - (local_density / max_density) );
		}

		reflex move when: (goal.location != location) {
			set speed value: (self compute_speed []);
			do goto with: [on::road_graph_topology, target::goal.location, speed:: speed];
			
			if (goal.location = location) {
				ask window_viewer { do die; }
				
				do die;
			}
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	species density_window_viewer {
		aspect default {
			draw shape: geometry color: rgb('red');
		}
	}

	species density_window_builder skills: [moving] {
		action build {
			arg current_people type: people;
			
			set location value: current_people.location;
			let previous_road type: road value: list(road) closest_to location;
			let previous_location type: point value: location;
			
			do goto with: [ on :: road_graph_topology, speed :: local_density_window_length, target :: current_people.goal ];
			let current_location type: point value: location;
			let current_road type: road value: list(road) closest_to location;
			
			if (previous_road = current_road) {
				let current_road_first_point type: point value: first ((current_road.shape).points);
				let last_location_2_first_point type: float value: road_graph_topology distance_between [previous_location, current_road_first_point];
				let current_location_2_first_point type: float value: road_graph_topology distance_between [current_location, current_road_first_point];
				
				if (last_location_2_first_point > current_location_2_first_point) { // agent moves towards first point
				 
					let part2 type: geometry value: first (previous_road.shape split_at previous_location);
					set (current_people.window_viewer).shape value: last ( (geometry(part2)) split_at current_location);
					
				} else {
					// agent moves towards last point
					let part2 type: geometry value: last (previous_road.shape split_at previous_location);
					set (current_people.window_viewer).shape value: first ( (geometry(part2)) split_at current_location);
				}
			} else {
				// test if the agent moves toward first point or last point of the PREVIOUS road
				
				let previous_location_2_goal type: float value: road_graph_topology distance_between [previous_location, current_people.goal];
				let current_location_2_goal type: float value: road_graph_topology distance_between [current_location, current_people.goal];

				let previous_road_first_point type: point value: first (((previous_road).shape).points);
				let previous_road_first_location_2_goal type: float value: road_graph_topology distance_between [previous_road_first_point, current_people.goal];
				
				let previous_road_last_point type: point value: last ((previous_road.shape).points);
				let previous_road_last_location_2_goal type: float value: road_graph_topology distance_between [previous_road_last_point, current_people.goal];
				
				let current_road_first_point type: point value: first (((current_road).shape).points);

				switch (previous_road_first_location_2_goal) {
					
					// agent has passed first point of PREVIOUS road
					match_between [current_location_2_goal, previous_location_2_goal] {
						let part_on_previous_road type: geometry value: first ((previous_road.shape) split_at previous_location );
						
						if (previous_road_first_point = current_road_first_point) { // agent begins current road with its first point
							let part_on_current_road type: geometry value: first (current_road.shape split_at current_location);
							
							set (current_people.window_viewer).shape value: geometry(part_on_previous_road) + geometry(part_on_current_road);
						} else { // agent begins current road with its last point
						
							let part_on_current_road type: geometry value: last (current_road.shape split_at current_location);
							
							set (current_people.window_viewer).shape value: geometry(part_on_previous_road) + geometry(part_on_current_road);
						}
					}
				}
				
				switch (previous_road_last_location_2_goal) {

					// agent has passed last point of PREVIOUS road
					match_between [current_location_2_goal, previous_location_2_goal] {
						let part_on_previous_road type: geometry value: last ((previous_road.shape) split_at previous_location );
						
						if (previous_road_last_point = current_road_first_point) { // agent begins current road with its first point
							let part_on_current_road type: geometry value: first (current_road.shape split_at location);
							
							set (current_people.window_viewer).shape value: geometry(part_on_previous_road) + geometry(part_on_current_road);
//							set (current_people.window_viewer).shape value: polyline (list ((part_on_last_road).points + (part_on_current_road).points) );

						} else { // agent begins current road with its PREVIOUS point
							let part_on_current_road type: geometry value: last (current_road.shape split_at location);
							
							set (current_people.window_viewer).shape value: geometry(part_on_previous_road) + geometry(part_on_current_road);
						}
					}
				}
			}
		}
	}
}


experiment default_expr type: gui {
	output {
		display default_display {
			species road aspect: default ;
			species people aspect: default;
			species density_window_viewer aspect: default;
		}
		
		monitor total_people value: length(list(people)); 
		monitor average_speed value: (sum (list(people) collect (each.speed))) / (length(people));
		monitor stuck_people value: length(list(people) where (each.speed = 0));
		monitor average_local_density value: (sum (list(people) collect (each.local_density))) / (length(people));
	}
}

