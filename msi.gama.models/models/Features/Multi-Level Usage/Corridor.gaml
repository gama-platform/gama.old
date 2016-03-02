/**
* Name: Corridor Multi-Level Architecture
* Author: 
* Description: This model shows how to use multi-level architecture. A corridor can capture pedestrians going from left to right side if
*	they are inside the corridor. This will result in changing their species from pedestrian to captured_pedestrian which will not be 
*	displayed. Once they pass enought time to consider they reach the exit of the corridor, they will be released by the corridor agent 
*	as pedestrians, letting them been displayed and going to their target. 
* Tags: multi_level, agent_movement
*/

model corridor

global {	
	//Capture pedestrians parameter to define if wall will capture pedestrians
	bool capture_pedestrians <- false parameter: 'Capture pedestrians?';
	
	int environment_size init: 2000;
	point environment_bounds init: {environment_size, environment_size} ;
	geometry shape <- rectangle(environment_bounds) ;		
	
	//Pedestrians parameters
	float pedestrian_size init: 1.0;
	geometry pedestrian_shape <- circle (pedestrian_size);
	rgb pedestrian_color <- #green; 
	float pedestrian_speed <- 2.0;
	
	
	//Wall parameters
	rgb corridor_wall_color <- #black;
	int corridor_wall_width <- int(environment_size / 2);
	int corridor_wall_height <- 200;
	geometry corridor_wall_0_shape <- rectangle ( {corridor_wall_width, corridor_wall_height} ) at_location {environment_size / 2, corridor_wall_height / 2};
	geometry corridor_wall_1_shape <- rectangle ( {corridor_wall_width, corridor_wall_height} ) at_location {environment_size / 2, environment_size - (corridor_wall_height / 2)};
	
	//Corridor parameters
	rgb corridor_color <- #blue;
	int corridor_width <- int(environment_size / 2) ;
	int corridor_height <- environment_size ;
	point corridor_location <- {environment_size / 2, environment_size / 2} ;
	geometry corridor_shape <- ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location) - (corridor_wall_0_shape + corridor_wall_1_shape);
	int corridor_left_bounds <- (int(corridor_location.x - (corridor_width / 2))) ;
	int corridor_right_bounds <- (int(corridor_location.x + (corridor_width / 2))) ;
	
	//Generation of new pedestrians parameters
	int new_pedestrian_rate <- 10;
	int new_pedestian_generate_frequency <- 1;
	int new_pedestrian_y_distance <- int(environment_size / new_pedestrian_rate);
	
	list<pedestrian> pedestrians <- [] update: list(pedestrian); 
	float start_time <- machine_time;
	
	init {
		create corridor;
		 
		create corridor_wall number: 2 returns: corridor_walls; 
		(corridor_walls at 0).shape <- corridor_wall_0_shape;
		(corridor_walls at 1).shape <- corridor_wall_1_shape;
		
	}
	
	//Reflex to generate new pedestrians according to the frequency generation parameter
	reflex generate_pedestrians when: ((cycle mod new_pedestian_generate_frequency) = 0) {
		create pedestrian number: new_pedestrian_rate returns: new_pedestrians; 
		
		int loop_times <- 0;
		loop p over: new_pedestrians {
			int y_coor <- rnd (new_pedestrian_y_distance);
			ask p as: pedestrian {
				do init_location loc: { 0, (loop_times * new_pedestrian_y_distance) + y_coor };
			}
			loop_times <- loop_times + 1;
		}
	}

} 

//Species pedestrian which will move from one side of the experiments to another and destroy itself once the other side is reached
species pedestrian skills: [moving] topology: ( topology (shape - (corridor_wall_0_shape + corridor_wall_1_shape)) ){
	geometry shape <-  circle (pedestrian_size);
	point initial_location;
	point target_location;
	int heading;
	float speed <- 2.0;
	
	action init_location (point loc) {
		location <- loc;
		initial_location <- loc;
		target_location <- {environment_size, loc.y};
		heading <- (self) towards (target_location);
	}
	
	//Reflex to make the agent move to its target_location
	reflex move_left {
		int update_heading <- (self) towards (target_location);
		
		point current_location <- location;
		
		do move heading: update_heading ;
		
		//Conditions to know if the agent doesn't move, in this case we take care if it is bcause of the walls 
		if (current_location = location) {
			if ( (location.y <= corridor_wall_height) or (location.y >= environment_size - corridor_wall_height) ) {
				do move heading: self towards {(environment_size / 2) - (corridor_width / 2), environment_size / 2}; 
			} else {
				do move heading: self towards {environment_size / 2, environment_size / 2}; 
			}
		}
		
		if( (target_location.x - location.x) <= speed ) { 
			do die;
		}
	}
	 
	aspect my_aspect {
		draw shape color: pedestrian_color;
	}
}

//Species which represents the corridor
species corridor  {
	geometry shape <- corridor_shape;
	
	//Subspecies for the multi-level architectures : captured pedestrians in this case
	species captured_pedestrian parent: pedestrian schedules: [] {
		float released_time;
		
		aspect my_aspect { }
	}
	
	init {
		create corridor_info_drawer number: 1 with: [target :: self];
	}
	
	//Reflex to capture pedestrians if the parameter is checked
	reflex aggregate when: capture_pedestrians {
		//List to get all the pedestrians inside the corridor
		list<pedestrian> tobe_captured_pedestrians <- (pedestrian overlapping shape);
		
		//If we have pedestrians inside the corridor, we capture them
		if !(empty (tobe_captured_pedestrians)) {
			capture tobe_captured_pedestrians as: captured_pedestrian returns: cps;
			
			//We update the time during which a pedestrian is captured according to the time the pedestrian
			// should need to pass through the corridor if it wasn't captured
			loop cp over: cps {
				cp.released_time <- time + ( ( corridor_width - ( (((cp).location).x) - ((environment_size / 2) - (corridor_width / 2)) ) ) / pedestrian_speed) ;
			}
		}
	}
	
	//Reflex to release pedestrians which have already passed enough time in the corridor
	// which means if they weren't captured by the corridor, they would have finish passing through it
	reflex disaggregate  {
		list tobe_released_pedestrians <- members where (time >= (captured_pedestrian (each)).released_time);
		if !(empty (tobe_released_pedestrians)) {
			release tobe_released_pedestrians as: pedestrian in: world {
				location <- {((environment_size / 2) + (corridor_width / 2)) + (2 * pedestrian_size), (location).y};
			}
		}
	}
	
	aspect my_aspect {
		draw shape color: corridor_color;
	}
}

species corridor_wall {
	init {
		create corridor_wall_info_drawer number: 1 with: [target :: self];
	}
	
	aspect name: my_aspect {
		draw shape color: corridor_wall_color;
	}
}

species corridor_info_drawer {
	corridor target;
	
	aspect my_aspect {
		draw  'Captured pedestrians: ' + (string (length (target.members))) color: rgb ('blue') size: 12°px at: {(target.location).x - 480, (target.location).y};
		draw  'Pedestrians: ' + (string (length (list (pedestrian)))) color: rgb ('blue') size: 12°px at: {(target.location).x - 135, (target.location).y + 100};
	}
}

species corridor_wall_info_drawer {
	corridor_wall target;
	
	init {
		location <- target.location;
	}
	
	aspect my_aspect { 
		draw 'WALL' color: rgb ('green') size: 15°px at: {(location).x - 40, (location).y};
	}
}


experiment corridor_expr type: gui{
	output {
		display defaut_display {
			species pedestrian;
			
			species corridor aspect: my_aspect transparency: 0.8 {
				species captured_pedestrian;
			}
			
			species corridor_wall aspect: my_aspect transparency: 0.7;
			species corridor_info_drawer aspect: my_aspect;
			species corridor_wall_info_drawer aspect: my_aspect;
		}

		display Execution_Time refresh: every(25) {
			chart name: 'Simulation step length' type: series background: #white {
				data 'simulation_step_length_in_mili_second' value: machine_time - start_time color: (rgb ('green'));
			}
		}
	 	
		display Captured_Pedestrians refresh: every(25){
			chart name: 'Captured Pedestrian' type: series background: #white {
				data 'captured_pedestrians' value: length ( ((list (corridor)) at 0).members ) color: rgb ('blue');
				data 'pedestrians' value: length (list (pedestrian)) color: rgb ('white');  
			}
		}
	}
}
