model testcase28

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: macro number: 1;
	}
}

entities {
	species macro skills: situated {
		var shape type: geometry init: (rectangle ({50.0, 100.0})) at_location {50, 50};
		
		var max_speed type: float; // Vmax (formula 5)
		var macro_length type: float min: 0; //
		
		
		var incoming_density type: float; // Pr (formula 5)
		var incoming_average_speed type: float;
		
		var outgoing_density type: float;
		var outgoing_average_speed type: float;
		
		var outgoing_noise_speed type: float init: 1.0;
		
		var Pr type: float value: incoming_density;
		var Pl type: float init: 0 const: true; // formula 5
		var Pmax type: float init: 5.0; // the maximum number of micro-agents can enter macro-agent at the same time
		
		var incoming_parameters type: map init: [] as map; // incoming time and position of a group of micro-agents at time t 
		
		
		reflex capture_micros {
			let micro_to_be_captured type: list of: micro value: [];
			
			let average_speed type: float value: 0;
			loop micro_a over: micro_to_be_captured {
				set average_speed value: average_speed + (micro_a.speed);
			}
			
			set incoming_average_speed value: average_speed / (length (micro_to_be_captured));
			set incoming_density value: (length (micro_to_be_captured)) / average_speed;
			
			let incoming_speed_and_density type: list value: [incoming_average_speed, incoming_density];
			put item: incoming_speed_and_density at: time in: incoming_parameters;
		}
		
		reflex release_micros { // Pl < Pr
			// formula 5
			
			let tobe_removed_incoming_parameters type: map value: [] as map;

			loop micro_group_incoming_time over: incoming_parameters.keys {
				let incoming_speed_and_density value: incoming_parameters at (micro_group_incoming_time);
				
				let group_outgoing_time type: float value: micro_group_incoming_time + (macro_length / (incoming_speed_and_density at 0) );

				let bound1 type: float value:  ( (max_speed * ( (1 - (2 * Pl) / Pmax ) ) ) * group_outgoing_time );
				let bound2 type: float value: ( (max_speed * ( (1 - (2 * Pr) / Pmax ) ) ) * group_outgoing_time );
				
				if condition: (macro_length <= bound1 ) {
					set outgoing_density value: Pl;
				}
				
				if condition: ( (macro_length > bound1) and (macro_length < bound2) ) {
					set outgoing_density value: (Pmax * max_speed) / ( (2 * max_speed) + Pmax ); 
				}
				
				if condition: (macro_length > bound2) {
					set outgoing_density value: Pr;
				}	
				
				let outgoing_speed type: float value: max_speed * ( 1 - (outgoing_density / Pmax));
				let released_number type: float value: outgoing_density * outgoing_speed;
				
				release target: released_number among (members) returns: released_micros;
				
				loop rm over: released_micros {
					set (micro (rm)).speed value: outgoing_speed + outgoing_noise_speed;
				}
				
				
				// accumulate elements to be removed
				if condition: (time >= group_outgoing_time + Gmax) { // formula 11; Gmax???
					
				}
			}
			
			// remove elements from incoming_parameters
			loop micro_group_incoming_time over: incoming_parameters.keys {
				
			}
			
		}
		
		/*
		action release_micros {
			arg name: 
		}
		*/
		
		species micro_in_macro skills: situated parent: micro {
			
		}
	}
	
	species micro skills: situated {
		var speed type: float ;
		
		reflex move {
			
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		display default_display {
			
		}
	}
}