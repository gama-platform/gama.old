/**
 * Purpose: Test that direct micro/macro-species can't have the same parent-species if that parent-species has micro-species.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that B and A can't have C as the same parent species because 
 * 				a) they have micro-macro relationship and
 * 				b) C has micro-species
 */
model testcase41

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated parent: C {
		species B parent: C {
			
		}
	}
	
	species C {
		species D {
			
		}
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}


