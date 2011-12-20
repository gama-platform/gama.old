
/**
 * Purpose: Test that a species can't be a sub-species of its in-direct macro-species.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that C species can't be a sub-species of A.
 */
model testcase37

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated {
		species B {
			species C parent: A {
				
			}
		}
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}

