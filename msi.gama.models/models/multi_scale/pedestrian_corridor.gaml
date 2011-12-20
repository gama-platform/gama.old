model pedestrian_corridor



global {
	var capture_pedestrians type: bool init: false parameter: 'Capture pedestrians?';
	
	const environment_size type: int init: 2000;
	const environment_bounds type: point init: {environment_size, environment_size} depends_on: [environment_size];
	
	const pedestrian_size type: float init: 1.0;
	const pedestrian_shape type: geometry init: circle (pedestrian_size) depends_on: pedestrian_size;
	const pedestrian_color type: rgb init: rgb ('green'); 
	const pedestrian_speed type: float init: 2.0;
	
	const corridor_wall_color type: rgb init: rgb ('black');
	const corridor_wall_width type: int init: environment_size / 2;
	const corridor_wall_height type: int init: 200;
	const corridor_wall_0_shape type: geometry init: rectangle ( {corridor_wall_width, corridor_wall_height} ) at_location {environment_size / 2, corridor_wall_height / 2};
	const corridor_wall_1_shape type: geometry init: rectangle ( {corridor_wall_width, corridor_wall_height} ) at_location {environment_size / 2, environment_size - (corridor_wall_height / 2)};
	
	const corridor_color type: rgb init: rgb ('blue');
	const corridor_width type: int init: environment_size / 2 depends_on: [environment_size];
	const corridor_height type: int init: environment_size depends_on: [environment_size];
	const corridor_location type: point init: {environment_size / 2, environment_size / 2} depends_on: [environment_size];
	const corridor_shape type: geometry init: ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location) - (corridor_wall_0_shape + corridor_wall_1_shape);
	const corridor_left_bounds type: int init: (corridor_location.x - (corridor_width / 2)) depends_on: [corridor_location, corridor_width];
	const corridor_right_bounds type: int init: (corridor_location.x + (corridor_width / 2)) depends_on: [corridor_location, corridor_width];
	
	const new_pedestrian_rate type: int init: 10;
	const new_pedestian_generate_frequency type: int init: 1;
	const new_pedestrian_y_distance type: int init: environment_size / new_pedestrian_rate depends_on: [environment_size, new_pedestrian_rate];
	
	var pedestrians type: list of: pedestrian init: [] value: list (pedestrian);
	
	init {
		create species: corridor number: 1;
		
		create species: corridor_wall number: 2 returns: corridor_walls;
		set (corridor_walls at 0).shape value: corridor_wall_0_shape;
		set (corridor_walls at 1).shape value: corridor_wall_1_shape;
	}
	
	reflex generate_pedestrians when: (time mod new_pedestian_generate_frequency) = 0 {
		create species: pedestrian number: new_pedestrian_rate returns: new_pedestrians;
		
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
	species pedestrian skills: [situated, moving] topology: shape - (corridor_wall_0_shape + corridor_wall_1_shape) frequency: 1 schedules: shuffle (list (pedestrian)) {
		var shape type: geometry init: copy (pedestrian_shape);
		var initial_location type: point;
		var target_location type: point;
		var heading type: int;
		var speed type: float init: 2.0;
		
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
			if condition: (current_location = location) { // hack
				if condition: ( (location.y <= corridor_wall_height) or (location.y >= environment_size - corridor_wall_height) ) {
					do action: move {
						arg name: heading value: self towards {(environment_size / 2) - (corridor_width / 2), environment_size / 2};
					} 
					
					else {
						do action: move {
							arg name: heading value: self towards {environment_size / 2, environment_size / 2};
						} 
					}
				}
			}
			
			if condition: ( (target_location.x - location.x) <= speed ) {
				do action: die;
			}
		}
		 
		aspect name: default {
			draw shape: geometry color: pedestrian_color;
		}
	}
	
	species corridor skills: situated {
		const shape type: geometry init: corridor_shape;
		
		species captured_pedestrian parent: pedestrian schedules: [] {
			var released_time type: int;
			
			aspect name: default { 
			}
		}
		
		init {
			create species: corridor_info_drawer number: 1 with: [target :: self];
		}
		
		
		reflex aggregate when: capture_pedestrians {
			let tobe_captured_pedestrians type: list value: (pedestrian overlapping shape);
			
			if condition: !(empty (tobe_captured_pedestrians)) {
				capture target: tobe_captured_pedestrians as: captured_pedestrian returns: cps;
				
				loop cp over: cps {
					set cp.released_time value: time + (int ( corridor_width - ( (((cp).location).x) - ((environment_size / 2) - (corridor_width / 2)) ) ) / pedestrian_speed) ;
				}
			}
		}
		
		reflex disaggregate  {
			let tobe_released_pedestrians type: list value: (list (members)) where (time >= (captured_pedestrian (each)).released_time);
			if condition: !(empty (tobe_released_pedestrians)) {
				release target: tobe_released_pedestrians {
					set location value: {((environment_size / 2) + (corridor_width / 2)) + (2 * pedestrian_size), (location).y};
				}
			}
		}
		
		aspect name: default {
			draw shape: geometry color: corridor_color;
		}
	}
	
	species corridor_wall skills: situated {
		init {
			create species: corridor_wall_info_drawer number: 1 with: [target :: self];
		}
		
		aspect name: default {
			draw shape: geometry color: corridor_wall_color;
		}
	}
	
	species corridor_info_drawer skills: situated {
		var target type: corridor;
		
		aspect default {
			draw text: 'Captured pedestrians: ' + (string (length (target.members))) color: rgb ('blue') size: 65 at: {(target.location).x - 480, (target.location).y};
			draw text: 'Pedestrians: ' + (string (length (list (pedestrian)))) color: rgb ('blue') size: 65 at: {(target.location).x - 135, (target.location).y + 100};
		}
	}
	
	species corridor_wall_info_drawer skills: situated {
		var target type: corridor_wall;
		
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
				data simulation_step_length_in_mili_second value: step_length color: (rgb ('green'));
			}
		}
		
		display Captured_Pedestrians refresh_every: 25 {
			chart name: 'Captured Pedestrian' type: series background: rgb ('black') {
				data captured_pedestrians value: length ( ((list (corridor)) at 0).members ) color: rgb ('blue');
				data pedestrians value: length (list (pedestrian)) color: rgb ('white');  
			}
		}
 
    file pedestrian_flow_results type: text refresh_every: 50 data: 'cycle: ' + (time as string) 
         + '; pedestrians: ' + ((length (pedestrians)) as string) + '; captured pedestrians: ' + (length ( ( (list (corridor)) at 0 ).members ) )
         + '; step_length: ' + (step_length as string);
	}
}
