/**
 * Purpose: Test that 2 species can't be sub-species of each other.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that C and D species can't be sub-species of each other.
 */
model testcase33

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated {
		species C parent: D {
			
		}
		
		species D parent: C {
			
		}
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}
