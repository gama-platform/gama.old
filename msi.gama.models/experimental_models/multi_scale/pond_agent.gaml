model pond_agent

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	const environment_size type: int init: 1000;
	
	const pond_state_min_period type: int init: 10; 
	const row_size type: int init: 11;
	const column_size type: int init: 11;
	const pond_number type: int init: row_size * column_size;
	const pond_size type: float init: 80.0;
	
	const padding_space type: int init: 10;
	const top_left_x type: int init: (pond_size / 2) + padding_space;
	const top_left_y type: int init: (pond_size / 2) + padding_space;
	const two_ponds_distance type: int init: pond_size + padding_space;
	
	const base_fish_number type: int init: 100;
	
	init {
		create species: pond number: pond_number return: ponds;
		
		let column_index type: int value: 0;
		let row_index type: int value: 0;
		loop times: row_size {
			set column_index value: 0;
			
			loop times: column_size {
				set (ponds at ( (row_index * row_size) + column_index)).shape value: (square (pond_size)) at_location {top_left_x + (column_index * two_ponds_distance), top_left_y + (row_index * two_ponds_distance)};

				set column_index value: column_index + 1;
			}

			set row_index value: row_index + 1;
		}
	}
}

entities {
	species fish skills: [situated, moving] {
		var shape type: geometry init: circle (1.0);
		
		reflex move_around {
			do action: wander {
				arg name: speed value: 1.0;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
	
	species pond skills: [situated] control: fsm inner_environment: true {
		var fish_number type: int min: 100;
		var pond_state_period type: int;
		var begin_state_time type: int;
		
		init {
			set fish_number value: base_fish_number + rnd (100);
		}
		
		delegation fish_in_pond species: fish {
			
		}
		
		state un_infected initial: true {
			enter {
				ask target: list (fish_in_pond) {
					do action: die;
				}
				
				set begin_state_time value: time;
				set pond_state_period value: pond_state_min_period + (rnd (10));
			}
			
			transition to: infected when: ( (time - begin_state_time) > pond_state_period);
		}
		
		state infected {
			enter {
				create species: fish_in_pond number: fish_number;

				set begin_state_time value: time;
				set pond_state_period value: pond_state_min_period + (rnd (10));
			}
			
			transition to: un_infected when: ( (time - begin_state_time) > pond_state_period);
		}
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}
	}
}

environment width: environment_size height: environment_size;

experiment default_expr type: gui {
	output {
		display default_display {
			species fish;
			
			species pond transparency: 0.5 {
				micro_layer fish_in_pond;
			}
		}
		
		monitor empty_ponds value: length ( (list (pond)) where (empty (each.components) ) );
	}
}