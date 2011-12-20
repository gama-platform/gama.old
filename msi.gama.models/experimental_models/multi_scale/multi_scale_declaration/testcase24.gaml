/**
 * Purpose: Test the scheduling of agents at different time scale.
 * 
 * Action(s):
 * 		1. Load the model and step the simulation.
 * 
 * Expected outcome:
 * 		1. Observe the default display.
 */
model testcase24

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	var corridor_width type: int init: 50;
	var corridor_location type: point init: {50, 50};
	var environment_size type: int init: 100;
	
	var scheduling type: list value: list (corridor) + list (pedestrian);

	init {
		create species: corridor number: 1;
		create species: pedestrian number: 1;
	}
	
}

entities {
	species pedestrian skills: situated {
		var shape type: geometry init: circle (2.0) at_location {1, 50};

		reflex move {
			let next_x type: int value: location.x + 1;
			if condition: (next_x > environment_size) {
				set next_x value: 0;
			}
			
			set location value: {next_x, location.y};
		}
		
		aspect default_aspect {
			draw shape: geometry color: rgb ('blue');
		}
	}

	species corridor skills: situated {
		var shape type: geometry init: (rectangle ( {corridor_width, environment_size} )) at_location corridor_location;
		var scheduling type: list value: list (pedestrian_in_corridor);

		species pedestrian_in_corridor parent: pedestrian spatial_level: 0 {
			var shape type: geometry init: square (2.0);

			aspect default_aspect {
				draw shape: geometry color: rgb ('red');
			}
		}
		
		reflex {
			let to_be_captured type: list of: pedestrian value: ( (list (pedestrian)) where ( ( ( ( (each).location ).x ) > 25 ) and ( ( ( (each).location ).x ) < 75 ) ) );
			
			if condition: !(empty (to_be_captured)) {
				capture target: to_be_captured as: pedestrian_in_corridor return: captured_pedestrians;
			}
			
			let to_be_released type: list value: (list (pedestrian_in_corridor)) where ( ( (each).location ).x > ( (corridor_location).x + (corridor_width / 2) ) );
			
			if condition: !(empty (to_be_released)) {
				do action: write {
					arg name: message value: 'to_be_relesed = ' + (string (to_be_released));
				}
				
				release target: to_be_released return: released_pedestrians;
				
				if condition: !(empty (list (released_pedestrians))) {
					loop rp over: released_pedestrians {
						set (pedestrian (rp)).shape value: (circle (2.0)) at_location (pedestrian (rp)).location;
					}
				}
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
	
	/*
	species pedestrian skills: situated {
		var shape type: geometry init: circle (2.0) at_location {1, 50};

		reflex move {
			let next_x type: int value: location.x + 1;
			if condition: (next_x > environment_size) {
				set next_x value: 0;
			}
			
			set location value: {next_x, location.y};
		}
		
		aspect default_aspect {
			draw shape: geometry color: rgb ('blue');
		}
	}
	*/
}

environment width: environment_size height: environment_size {
}

experiment default_expr type: gui {
	output {
		display default {
			species corridor transparency: 0.5 {
				species pedestrian_in_corridor aspect: default_aspect;
			}
			
			species pedestrian aspect: default_aspect;
		}
	}
}