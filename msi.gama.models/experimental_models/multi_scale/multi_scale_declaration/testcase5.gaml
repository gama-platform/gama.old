/**
 * Purpose: Verify that an agent (executor) can not create another agent whose species is not a direct micro-species of the executor.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. An error message in the "Errors" view indicating that the "C" species is not visible from the A species.
 *  
 */
model testcase5

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
	}
}

entities {
	species A {
		
		init {
			create species: C number: 1;
		}
		
		species B {
			species C {
				
			}
		}
		
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}
