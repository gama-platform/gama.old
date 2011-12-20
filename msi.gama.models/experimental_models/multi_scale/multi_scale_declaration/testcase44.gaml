/**
 * Purpose: Test that parents of direct micro/macro-species can have direct parent-sub species relationship 
 * 				if these parent-species don't have micro-species.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome: 
 * 		1. The model can be loaded successfully.
 */
model testcase44

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated {
			
			species B {
			}
			
			species C parent: B {
			}
			
			species D parent: C {
				species E parent: B {
					
				}
			}
			
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}