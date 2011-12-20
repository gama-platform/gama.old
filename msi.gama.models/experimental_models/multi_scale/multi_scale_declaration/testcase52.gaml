/**
 * Purpose: Test that the "circular" inheritance between species from different branches is not permitted.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that D can not be a sub-species of A because this will form a circular inheritance.
 */
model testcase46

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated {
		species B skills: situated parent: C {
			
		}
	}
	
	species C skills: situated {
		species D skills: situated parent: A {
			
		}
	}
}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}

