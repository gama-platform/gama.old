model neighbours_of_solution

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
		point goal <- polygon([one_of(TARGETS)]);
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
			do goto with: [on::road_graph_topology, target::goal.location, speed::speed];
			
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
			
			let local_window type: geometry <- nil;
			
			let current_circle type: geometry value: circle(local_density_window_length) at_location (current_people.location);
			let diameter_geom type: geometry value: current_circle intersection previous_road;
			
			let first_point type: point value: first(diameter_geom.points);
			let first_point_geom type: geometry value: polygon([first_point]);
			
			let last_point type: point value: last(diameter_geom.points);
			let last_point_geom type: geometry value: polygon([last_point]);
			
			let two_radius type: list of: geometry value: diameter_geom split_at (current_people.location);
			
			write 'length(two_radius): ' + (string(length(two_radius)));
			
			let first_point_2_goal type: float value: road_graph_topology distance_between [first_point, current_people.goal];
			let last_point_2_goal type: float value: road_graph_topology distance_between [last_point, current_people.goal];
			
			if (first_point_2_goal < last_point_2_goal) { // moves towards first point
				write 'first_point: ' + (string(first_point)) + '\nfirst(two_radius where (each.points contains first_point)).points: ' + (string (first(two_radius where (each.points contains first_point)).points ));
			
//				set (current_people.window_viewer).shape value: first(two_radius where (each.points contains first_point));
				set local_window value: first(two_radius where (each.points contains first_point));
			} else {
				write 'last_point: ' + (string(first_point)) + '\nfirst(two_radius where (each.points contains last_point)).points: ' + (string (first(two_radius where (each.points contains last_point)).points ));

//				set (current_people.window_viewer).shape value: first(two_radius where (each.points contains last_point));
				set local_window value: first(two_radius where (each.points contains last_point));
			}
			
			if (current_road != previous_road) {
				let current_road_part type: geometry value: current_circle intersection (current_road);
				
				if (current_road_part != nil) {
					set local_window value: local_window + current_road_part;
				}
			}
			
			set (current_people.window_viewer).shape value: local_window;
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

