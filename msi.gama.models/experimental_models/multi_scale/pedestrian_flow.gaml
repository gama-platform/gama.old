model pedestrian_flow

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	var create_macro_patch type: bool init: true; // switch on/off this value to see the effect of using or not using create_macro_patch
	
	const environment_size type: int init: 2000 min: 100;
	const environment_bounds type: point init: {environment_size, environment_size} depends_on: [environment_size];

	const pedestrian_size type: float init: 1.0;
	const pedestrian_shape type: geometry init: circle (pedestrian_size) depends_on: pedestrian_size;
	const pedestrian_color type: rgb init: rgb ('green');
	const pedestrian_speed type: float init: 2.0;
	
	const macro_patch_color type: rgb init: rgb ('blue');
	const macro_patch_width type: int init: environment_size / 2 depends_on: [environment_size];
	const macro_patch_height type: int init: environment_size depends_on: [environment_size];
	const macro_patch_location type: point init: {environment_size / 2, environment_size / 2} depends_on: [environment_size];
	const macro_patch_left_bounds type: int init: (macro_patch_location.x - (macro_patch_width / 2)) depends_on: [macro_patch_location, macro_patch_width];
	const macro_patch_right_bounds type: int init: (macro_patch_location.x + (macro_patch_width / 2)) depends_on: [macro_patch_location, macro_patch_width];
	
	const new_pedestrian_rate type: int init: 40;
	const new_pedestian_generate_frequency type: int init: 1;
	const new_pedestrian_y_distance type: int init: environment_size / new_pedestrian_rate depends_on: [environment_size, new_pedestrian_rate];
	
	var pedestrians type: list of: pedestrian init: [] value: list (pedestrian);
	
	init {
		if condition: create_macro_patch {
			create species: macro_patch number: 1;
		}
	}
	
	reflex generate_pedestrians when: (time < 1000) and (time mod new_pedestian_generate_frequency) = 0 {
		create species: pedestrian number: new_pedestrian_rate return: new_pedestrians;
		
		let loop_times type: int value: 0;
		loop p over: new_pedestrians {
			let y_coor value: rnd (new_pedestrian_y_distance);
			set p.location value: {0, (loop_times * new_pedestrian_y_distance) + y_coor};
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
	species pedestrian skills: situated {
		var shape type: geometry init: copy (pedestrian_shape);
		
		reflex name: step {
			let new_x value: location.x + pedestrian_speed;
			
			if condition: (new_x >= environment_size) {
				do action: die;

				else {
					set location value: {new_x, location.y};
				}				
			}
		}
		
		aspect name: default {
			draw shape: geometry color: pedestrian_color;
		}
	}
	
	species macro_patch skills: situated {
		const shape type: geometry init: (rectangle ({macro_patch_width, macro_patch_height})) at_location macro_patch_location;
		
		var managed_pedestrians type: map init: [] as map;
		
		delegation toto species: pedestrian {
			
			reflex name: step { }
			
			aspect name: default { 
			}
		}
		
		init {
			create species: text_drawer number: 1 with: [target :: self];
		}
		
		
		reflex aggregate {
//			let tobe_captured_pedestrians type: list of: pedestrian value: ( (pedestrian overlapping shape) - components );
			let tobe_captured_pedestrians type: list value: ( (pedestrian overlapping shape) - (list (toto)) );
			
			if condition: !(empty (tobe_captured_pedestrians)) {

				capture target: tobe_captured_pedestrians delegation: toto;


				put item: tobe_captured_pedestrians at: (int (time + (macro_patch_width / pedestrian_speed))) in: managed_pedestrians;
				
			}
		}
		
		reflex disaggregate {
			let tobe_released_pedestrians type: list value: (managed_pedestrians at time);
					
			if condition: (tobe_released_pedestrians != nil) and !(empty (tobe_released_pedestrians)) {

				release target: tobe_released_pedestrians;
				
				loop p over: tobe_released_pedestrians {
					set (pedestrian (p)).location value: {((pedestrian (p)).location).x + macro_patch_width + (2 * pedestrian_size), ((pedestrian (p)).location).y};
				}
				
				remove item: time from: managed_pedestrians;
				
			} 
		}
		
		aspect name: default {
			draw shape: geometry color: macro_patch_color;
		}
	}
	
	species text_drawer skills: situated {
		var target type: macro_patch;
		
		aspect name: default {
			draw text: 'Delegated pedestrians: ' + (string (length (target.components))) color: rgb ('blue') size: 65 at: {(target.location).x - 480, (target.location).y};
			draw text: 'Pedestrians: ' + (string (length (list (pedestrian)))) color: rgb ('blue') size: 65 at: {(target.location).x - 135, (target.location).y + 100};
		}
	} 
}

experiment type: gui name: 'With_Delegation' {
	output {
		display name: 'Display' refresh_every: 1 {
			species name: pedestrian aspect: default;
			species name: macro_patch aspect: default transparency: 0.8; 
			species name: text_drawer aspect: default;
		}

		display Execution_time refresh_every: 25 {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_length_in_second value: step_length color: (rgb ('green'));
			}
		}
		
		display Delegated_Pedestrians refresh_every: 25 {
			chart name: 'Delegated Pedestrian' type: series background: rgb ('black') {
				data delegated_pedestrians value: length ((list (macro_patch)) at 0) color: rgb ('blue');
				data pedestrians value: length (list (pedestrian)) color: rgb ('white');  
			}
		}

/*
    file pedestrian_flow_results type: text refresh_every: 50 data: 'cycle: ' + (time as string) 
         + '; pedestrians: ' + ((length (pedestrians)) as string)
         + '; step_length: ' + (step_length as string);
*/
	}
}

experiment type: gui name: 'Without_Delegation' {
	parameter var: create_macro_patch name: 'Create macro patch?' init: false;
	
	output {
		display name: 'Display' refresh_every: 10 {
			species name: pedestrian aspect: default;
			species name: macro_patch aspect: default transparency: 0.8; 
		}
		
		display Execution_time refresh_every: 25 {
			chart name: 'Simulation step length' type: series background: rgb('black') {
				data simulation_step_length_in_second value: step_length color: (rgb ('blue')); 
			}
		}
		
	
	
	/*    file pedestrian_flow_results type: text refresh_every: 50 data: 'cycle: ' + (time as string) 
	         + '; pedestrians: ' + ((length (pedestrians)) as string)
	         + '; step_length: ' + (step_length as string)
	         + '; ' + (string (length ( ( (list (macro_patch)) at 0).components) ) );
	         */
	}
}
