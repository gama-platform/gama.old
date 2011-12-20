/**
 * Purpose: Test "capture" & "release" commands.
 * 
 * Action(s):
 * 		1. Load the model and step the simulation.
 * 
 * Expected outcome:
 * 		1. At step 5, agent B0 is killed which make agents C0, D0 die also.
 */
model testcase20

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
		create species: C number: 1;
	}
	
	var scheduling type: list value: list (A) + list (C);
}

entities {
	species C skills: [situated, moving] {
		var shape type: geometry init: triangle (3.0) at_location {10, 10};
		
		reflex move_around {
			do action: wander {
				arg name: speed value: 1;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}
	}
	
	species A skills: situated {
		var shape type: geometry init: (square (50.0)) at_location {50, 50};
		
		var scheduling type: list value: list (B);
		
		species B skills: situated parent: C {
//			var shape type: geometry init: square (20.0) at_location {60, 50};
			
			
			reflex move_around {
				do action: wander {
					arg name: speed value: 3;
				}
			}

			aspect default {
				draw shape: geometry color: rgb ('red');
			}
		}
		
		reflex when: (time = 5) {
			capture target: list (C) as: B;
		}
		
		reflex when: (time = 10) {
			release target: list (B);
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
			species C transparency: 0.5;
			
			species A transparency: 0.5 {
				species B transparency: 0.5;
			}
		}
	}
}