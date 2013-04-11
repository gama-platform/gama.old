model pedestrian_corridor



global {
	bool capture_pedestrians <- false parameter: 'Capture pedestrians?';
	
	int environment_size init: 2000;
	point environment_bounds init: {environment_size, environment_size} ;
	
	float pedestrian_size init: 1.0;
	geometry pedestrian_shape <- circle (pedestrian_size);
	rgb pedestrian_color <- rgb ('green'); 
	float pedestrian_speed <- 2.0;
	
	rgb corridor_wall_color <- rgb ('black');
	int corridor_wall_width <- int(environment_size / 2);
	int corridor_wall_height <- 200;
	geometry corridor_wall_0_shape <- rectangle ( {corridor_wall_width, corridor_wall_height} ) at_location {environment_size / 2, corridor_wall_height / 2};
	geometry corridor_wall_1_shape <- rectangle ( {corridor_wall_width, corridor_wall_height} ) at_location {environment_size / 2, environment_size - (corridor_wall_height / 2)};
	
	rgb corridor_color <- rgb ('blue');
	int corridor_width <- int(environment_size / 2) ;
	int corridor_height <- environment_size ;
	point corridor_location <- {environment_size / 2, environment_size / 2} ;
	geometry corridor_shape <- ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location) - (corridor_wall_0_shape + corridor_wall_1_shape);
	int corridor_left_bounds <- (int(corridor_location.x - (corridor_width / 2))) ;
	int corridor_right_bounds <- (int(corridor_location.x + (corridor_width / 2))) ;
	
	int new_pedestrian_rate <- 10;
	int new_pedestian_generate_frequency <- 1;
	int new_pedestrian_y_distance <- int(environment_size / new_pedestrian_rate);
	
	list pedestrians of: pedestrian <- [] value: list(pedestrian); 
	float start_time <- machine_time;
	init {
		create corridor number: 1;
		 
		create corridor_wall number: 2 returns: corridor_walls; 
		set (corridor_walls at 0).shape value: corridor_wall_0_shape;
		set (corridor_walls at 1).shape value: corridor_wall_1_shape;
		
	}
	
	reflex generate_pedestrians when: ((time mod new_pedestian_generate_frequency) = 0) {
		create pedestrian number: new_pedestrian_rate returns: new_pedestrians; 
		
		let loop_times type: int value: 0;
		loop p over: new_pedestrians {
			let y_coor value: rnd (new_pedestrian_y_distance);
			ask target: p as: pedestrian {
				do action: init_location {
					arg loc value: { 0, (loop_times * new_pedestrian_y_distance) + y_coor };
				}			
			}
			set loop_times value: loop_times + 1;
		}
	}
	
	/*
	reflex halt {
		if condition: (time = 1250) {
			do action: write {
				arg name: message value: 'Simulation halted at time = 1250';
			}

			do action: halt;
		}
	}
	*/
} 

environment bounds: environment_bounds;

entities {
	species pedestrian skills: [situated, moving] topology: ( topology (shape - (corridor_wall_0_shape + corridor_wall_1_shape)) ) frequency: 1 schedules: shuffle (list (pedestrian)) {
//		var shape <-  (copy(pedestrian_shape)) type: geometry;
		geometry shape <-  circle (pedestrian_size);
		point initial_location;
		point target_location;
		int heading;
		float speed <- 2.0;
		
		action init_location {
			arg name: loc type: point;
			
			set location value: loc;
			set initial_location value: loc;
			set target_location value: {environment_size, loc.y};
			set heading value: (self) towards (target_location);
		}
		
		reflex move_left {
			let update_heading type: int value: (self) towards (target_location);
			
			let current_location type: point value: location;
			
			do action: move {
				arg name: heading value: update_heading;
			} 
			
//			if condition: (heading = update_heading + 180) {
			if (current_location = location) { // hack
				if ( (location.y <= corridor_wall_height) or (location.y >= environment_size - corridor_wall_height) ) {
					do move {
						arg name: heading value: self towards {(environment_size / 2) - (corridor_width / 2), environment_size / 2};
					} 
					

				} else {
						do move {
							arg name: heading value: self towards {environment_size / 2, environment_size / 2};
						} 
					}
			}
			
			if( (target_location.x - location.x) <= speed ) {
				do die;
			}
		}
		 
		aspect default {
			draw shape color: pedestrian_color;
		}
	}
	
	species corridor  {
		geometry shape <- corridor_shape;
		
		species captured_pedestrian parent: pedestrian schedules: [] {
			int released_time;
			
			aspect default { }
		}
		
		init {
			create corridor_info_drawer number: 1 with: [target :: self];
		}
		
		
		reflex aggregate when: capture_pedestrians {
			let tobe_captured_pedestrians type: list value: (pedestrian overlapping shape);
			
			if !(empty (tobe_captured_pedestrians)) {
				capture tobe_captured_pedestrians as: captured_pedestrian returns: cps;
				
				loop cp over: cps {
					set cp.released_time value: time + (int ( corridor_width - ( (((cp).location).x) - ((environment_size / 2) - (corridor_width / 2)) ) ) / pedestrian_speed) ;
				}
			}
		}
		
		reflex disaggregate  {
			let tobe_released_pedestrians type: list value: (list (members)) where (time >= (captured_pedestrian (each)).released_time);
			if !(empty (tobe_released_pedestrians)) {
				release tobe_released_pedestrians as: pedestrian in: world {
					set location value: {((environment_size / 2) + (corridor_width / 2)) + (2 * pedestrian_size), (location).y};
				}
			}
		}
		
		aspect default {
			draw shape color: corridor_color;
		}
	}
	
	species corridor_wall {
		init {
			create corridor_wall_info_drawer number: 1 with: [target :: self];
		}
		
		aspect name: default {
			draw shape color: corridor_wall_color;
		}
	}
	
	species corridor_info_drawer {
		corridor target;
		
		aspect default {
			draw text: 'Captured pedestrians: ' + (string (length (target.members))) color: rgb ('blue') size: 65 at: {(target.location).x - 480, (target.location).y};
			draw text: 'Pedestrians: ' + (string (length (list (pedestrian)))) color: rgb ('blue') size: 65 at: {(target.location).x - 135, (target.location).y + 100};
		}
	}
	
	species corridor_wall_info_drawer {
		corridor_wall target;
		
		init {
			set location value: target.location;
		}
		
		aspect default { 
			draw text: 'WALL' color: rgb ('green') size: 65 at: {(location).x - 40, (location).y};
		}
	}
}

experiment default_expr type: gui{
	output {
		display defaut_display {
			species pedestrian aspect: default;
			
			species corridor aspect: default transparency: 0.8 {
				species captured_pedestrian;
			}
			
			species corridor_wall aspect: default transparency: 0.7;
			species corridor_info_drawer aspect: default;
			species corridor_wall_info_drawer aspect: default;
		}

		display Execution_Time refresh_every: 25 {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data 'simulation_step_length_in_mili_second' value: machine_time - start_time color: (rgb ('green'));
			}
		}
	 	
		display Captured_Pedestrians refresh_every: 25 {
			chart name: 'Captured Pedestrian' type: series background: rgb ('black') {
				data 'captured_pedestrians' value: length ( ((list (corridor)) at 0).members ) color: rgb ('blue');
				data 'pedestrians' value: length (list (pedestrian)) color: rgb ('white');  
			}
		}
 
	}
}
