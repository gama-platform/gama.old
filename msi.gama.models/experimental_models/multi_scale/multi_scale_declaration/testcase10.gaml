/**
 * Purpose: Verify that a species can not have the same name with micro-species of other species.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. An the load process stops at the "Compiling model" phase.
 * 
 * TODO: improvement: the process of transforming the GAML source code to XML tree don't keep the (column, row) information
 * So we can't not inform the error in the GAML editor!
 */
model testcase10

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	
}

entities {
	species A {
		species B {
		}
	}
	
	species B {
		
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}