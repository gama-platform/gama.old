/**
 * Purpose: Test that a species can't be a sub-species of its indirect micro-species.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that A species can't be a sub-species of C because C is not visible to A.
 */
model testcase39

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated parent: C {
		species B {
			species C {
				
			}
			
		}
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}
