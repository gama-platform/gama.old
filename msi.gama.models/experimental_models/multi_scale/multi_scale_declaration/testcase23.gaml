/**
 * Purpose: Test the scheduling of agents at different time scale.
 * 
 * Action(s):
 * 		1. Load the model and step the simulation.
 * 
 * Expected outcome:
 * 		1. Observe the outcome in the console view.
 */
model testcase23

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
		create species: B number: 1;
	}
	
	var scheduling type: list value: (list (A)) + (list (A))   + ( (time mod 3 = 0) ? list (B) : [] );
}

entities {
	species A skills: situated {
		var shape type: geometry init: square (4.0);
		
		var scheduling type: list value: ( (time mod 2 = 0) ? list (C) : [] ) + (list (D));
		
		init {
			create species: C number: 1;
			create species: D number: 1;
		}
		
		species C skills: situated {
			
			reflex {
				do action: write {
					arg name: message value: 'agent ' + name + ' is stepped at time ' + (string (time));
				}
			}
			
		}
		
		species D skills: situated {
			reflex {
				do action: write {
					arg name: message value: 'agent ' + name + ' is stepped at time ' + (string (time));
				}
			}
			
		}
		
		reflex {
			do action: write {
				arg name: message value: 'agent ' + name + ' is stepped at time ' + (string (time));
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
	
	species B skills: situated {
		var shape type: geometry init: circle (2.0);
		
		reflex {
			do action: write {
				arg name: message value: 'agent ' + name + ' is stepped at time ' + (string (time));
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}
	}
}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	output {
		display default {
			species A;
			species B;
		}
	}
}