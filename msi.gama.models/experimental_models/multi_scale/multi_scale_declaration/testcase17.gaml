/**
 * Purpose: Test the moving of a micro-agent inside a macro-agent.
 * 
 * Action(s):
 * 		1. Load the model and start the simulation.
 * 
 * Expected outcome:
 * 		1. The micro-agent moves inside the macro-agent's body.
 */
model testcase16

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
	}
	
	var scheduling type: list value: list (A);
	
}

entities {
	species A skills: situated {
		var shape type: geometry init: (square (50.0)) at_location {50, 50};
		
		var scheduling type: list value: list (B);
		
		init {
			create species: B number: 1;
		}
		
		species B skills: [situated, moving] {
			var shape type: geometry init: square (4.0) at_location {60, 50};
			
			reflex move_around {
				do action: wander {
					arg name: speed value: 1;
				}
			}
			
			aspect default {
				draw shape: geometry color: rgb ('red');
			}
		}

		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		display default {
			species A transparency: 0.5 {
				species B transparency: 0.5;
			}
		}
	}
}